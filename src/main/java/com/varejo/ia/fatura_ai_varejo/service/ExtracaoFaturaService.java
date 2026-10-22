package com.varejo.ia.fatura_ai_varejo.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import com.varejo.ia.fatura_ai_varejo.dto.FaturaResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ExtracaoFaturaService {

    private static final Set<String> MIME_TYPES_SUPORTADOS = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
            "application/pdf"
    );

    private static final String PROMPT_EXTRACAO = """
            Você é um especialista em contabilidade e automação fiscal para o varejo brasileiro.
            Analise a imagem ou PDF do documento fiscal anexo (Cupom Fiscal, NFC-e, SAT, DANFE ou Recibo)
            e extraia todos os dados estruturados seguindo rigorosamente o schema JSON definido.
            
            Regras de extração:
            1. Ignore dobras, sombras, manchas de papel térmico ou desalinhamentos.
            2. Para valores monetários e quantidades, utilize números decimais com ponto (ex: 24.90).
            3. Em 'tipoDocumento', classifique como: CUPOM_FISCAL, NFCE, DANFE, SAT ou RECIBO.
            4. Em 'chaveAcesso', extraia os 44 dígitos numéricos sem espaços (se presente no documento).
            5. Caso algum campo opcional não exista ou esteja ilegível na nota, retorne null para esse campo.
            """;

    private final String apiKey;
    private final String modelName;
    private final ObjectMapper objectMapper;

    public ExtracaoFaturaService(
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.model:gemini-3.8-flash}") String modelName
    ) {
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public FaturaResponseDTO extrairDadosFatura(MultipartFile arquivo) {
        validarArquivo(arquivo);

        try {
            byte[] bytesArquivo = arquivo.getBytes();
            String mimeType = normalizarMimeType(arquivo.getContentType());

            Content content = Content.fromParts(
                    Part.fromText(PROMPT_EXTRACAO),
                    Part.fromBytes(bytesArquivo, mimeType)
            );

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .candidateCount(1)
                    .temperature(0.1f)
                    .responseSchema(construirSchemaFatura())
                    .build();

            Client client = criarClienteGemini();
            GenerateContentResponse response = client.models.generateContent(modelName, content, config);

            String jsonResposta = response.text();
            if (jsonResposta == null || jsonResposta.isBlank()) {
                throw new IllegalStateException("O modelo Gemini retornou uma resposta vazia para o documento enviado.");
            }

            return objectMapper.readValue(jsonResposta, FaturaResponseDTO.class);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao processar os bytes ou converter o JSON do documento fiscal.", e);
        }
    }

    protected Client criarClienteGemini() {
        if (apiKey != null && !apiKey.isBlank()) {
            return Client.builder().apiKey(apiKey).build();
        }
        return new Client();
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo do documento fiscal não pode estar vazio.");
        }
        String contentType = arquivo.getContentType();
        if (contentType == null || !MIME_TYPES_SUPORTADOS.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Formato de arquivo não suportado: " + contentType +
                    ". Envie uma imagem (JPEG, PNG, WEBP) ou documento PDF."
            );
        }
    }

    private String normalizarMimeType(String contentType) {
        if ("image/jpg".equalsIgnoreCase(contentType)) {
            return "image/jpeg";
        }
        return contentType.toLowerCase();
    }

    private Schema construirSchemaFatura() {
        Schema itemSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "codigo", Schema.builder().type(Type.Known.STRING).nullable(true).build(),
                        "descricao", Schema.builder().type(Type.Known.STRING).build(),
                        "quantidade", Schema.builder().type(Type.Known.NUMBER).build(),
                        "unidadeMedida", Schema.builder().type(Type.Known.STRING).nullable(true).build(),
                        "valorUnitario", Schema.builder().type(Type.Known.NUMBER).build(),
                        "valorTotal", Schema.builder().type(Type.Known.NUMBER).build()
                ))
                .required(List.of("descricao", "quantidade", "valorUnitario", "valorTotal"))
                .build();

        return Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.ofEntries(
                        Map.entry("tipoDocumento", Schema.builder().type(Type.Known.STRING).build()),
                        Map.entry("nomeEmissor", Schema.builder().type(Type.Known.STRING).nullable(true).build()),
                        Map.entry("cnpjEmissor", Schema.builder().type(Type.Known.STRING).nullable(true).build()),
                        Map.entry("enderecoEmissor", Schema.builder().type(Type.Known.STRING).nullable(true).build()),
                        Map.entry("numeroDocumento", Schema.builder().type(Type.Known.STRING).nullable(true).build()),
                        Map.entry("chaveAcesso", Schema.builder().type(Type.Known.STRING).nullable(true).build()),
                        Map.entry("dataEmissao", Schema.builder().type(Type.Known.STRING).nullable(true).build()),
                        Map.entry("cpfCnpjConsumidor", Schema.builder().type(Type.Known.STRING).nullable(true).build()),
                        Map.entry("subtotal", Schema.builder().type(Type.Known.NUMBER).nullable(true).build()),
                        Map.entry("descontos", Schema.builder().type(Type.Known.NUMBER).nullable(true).build()),
                        Map.entry("valorTotal", Schema.builder().type(Type.Known.NUMBER).build()),
                        Map.entry("formaPagamento", Schema.builder().type(Type.Known.STRING).nullable(true).build()),
                        Map.entry("itens", Schema.builder().type(Type.Known.ARRAY).items(itemSchema).build())
                ))
                .required(List.of("tipoDocumento", "valorTotal", "itens"))
                .build();
    }
}
