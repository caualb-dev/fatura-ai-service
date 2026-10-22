# 🧾 Fatura AI Varejo

Microsserviço backend desenvolvido em **Java 21** com **Spring Boot** concebido para automatizar a leitura, extração e estruturação de documentos fiscais (cupons fiscais, notas térmicas NFC-e/SAT, DANFEs ou PDFs) através da API multimodal de visão computacional da Google (**Gemini 3.8 Flash**).

---

## 🎯 1. O Problema de Negócio que Resolve

No setor de varejo e grandes redes de supermercados:

* **Entrada manual morosa:** Registrar faturas de compras, notas de devolução ou recibos de despesas loja a loja exige que operadores digitem manualmente dezenas de itens, preços e CNPJs, gerando atrasos e erros de digitação.
* **Limitações do OCR comum:** Ferramentas de OCR legadas dependem de modelos rígidos e falham facilmente com papéis térmicos desgastados, dobras, sombras em fotos tiradas por celular ou alinhamentos tortos.
* **A Solução:** O microsserviço atua como um motor inteligente que recebe a imagem direta (`JPEG`, `PNG`, `WEBP`) ou o documento em `PDF` e devolve um **JSON padronizado** com os dados fiscais e a lista de itens comprados, pronto para integração direta com sistemas de ERP ou bancos de dados.

---

## 🛠️ 2. Tecnologias Utilizadas

* **Java 21** (Records, Text Blocks, Pattern Matching)
* **Spring Boot 4** (`spring-boot-starter-webmvc`, `spring-boot-starter-validation`)
* **Google Gen AI SDK (`com.google.genai:google-genai`)** — Integração oficial com o modelo **`gemini-3.8-flash`** e *Structured Outputs* (`responseSchema`)
* **Jackson Databind** — Serialização e desserialização JSON
* **JUnit 5 & Mockito** — Testes unitários automatizados
* **Maven Wrapper (`./mvnw`)**

---

## 📂 3. Estrutura do Projeto

```text
src/main/java/com/varejo/ia/fatura_ai_varejo
├── controller
│   └── FaturaController.java          # Endpoints REST (/api/v1/faturas)
├── dto
│   ├── FaturaResponseDTO.java         # Contrato JSON do documento fiscal
│   ├── ItemFaturaDTO.java             # Contrato JSON de cada item da nota
│   └── ErroRespostaDTO.java           # Contrato JSON padronizado para erros
├── exception
│   └── GlobalExceptionHandler.java    # Tratamento global de exceções (@RestControllerAdvice)
├── service
│   └── ExtracaoFaturaService.java     # Integração multimodal e schema JSON com o Gemini
└── FaturaAiVarejoApplication.java     # Classe principal Spring Boot
```

---

## 🚀 4. Como Executar o Projeto

### Pré-requisitos
* **JDK 21** instalado
* Uma chave de API do Google AI Studio ([obtenha gratuitamente aqui](https://aistudio.google.com/app/apikey))

### Passo a passo

1. Configure a variável de ambiente com a sua chave do Gemini no terminal:
   ```bash
   export GOOGLE_API_KEY="sua-chave-aqui"
   ```

2. Execute a aplicação com o Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Verifique se o serviço está online acessando no navegador ou via `curl`:
   ```bash
   curl http://localhost:8080/api/v1/faturas/status
   ```

### 🐳 Executando com Docker

```bash
# 1. Construir a imagem Docker
docker build -t fatura-ai-varejo .

# 2. Subir o container passando a sua chave do Gemini
docker run -p 8080:8080 -e GOOGLE_API_KEY="sua-chave-aqui" fatura-ai-varejo
```

---

## 📡 5. Endpoints da API

### 5.1. Extrair dados de Fatura / Cupom Fiscal
* **Rota:** `POST /api/v1/faturas/extrair`
* **Content-Type:** `multipart/form-data`
* **Parâmetro:** `arquivo` (Formatos aceitos: `image/jpeg`, `image/png`, `image/webp`, `application/pdf` — até 20MB)

#### Exemplo de chamada via `curl`:
```bash
curl -X POST http://localhost:8080/api/v1/faturas/extrair \
  -F "arquivo=@/caminho/para/cupom-fiscal.jpg"
```

#### Exemplo de Resposta (`HTTP 200 OK`):
```json
{
  "tipoDocumento": "NFCE",
  "nomeEmissor": "SUPERMERCADO BOM PRECO LTDA",
  "cnpjEmissor": "12.345.678/0001-90",
  "enderecoEmissor": "AV. BRASIL, 1500 - CENTRO",
  "numeroDocumento": "004582",
  "chaveAcesso": "35261012345678000190650010000045821000045829",
  "dataEmissao": "2026-10-21T19:45:12",
  "cpfCnpjConsumidor": "123.456.789-00",
  "subtotal": 68.30,
  "descontos": 3.30,
  "valorTotal": 65.00,
  "formaPagamento": "PIX",
  "itens": [
    {
      "codigo": "7891000100103",
      "descricao": "ARROZ BRANCO 5KG",
      "quantidade": 2,
      "unidadeMedida": "UN",
      "valorUnitario": 24.90,
      "valorTotal": 49.80
    },
    {
      "codigo": "7891991010856",
      "descricao": "CAFE TORRADO 500G",
      "quantidade": 1,
      "unidadeMedida": "UN",
      "valorUnitario": 18.50,
      "valorTotal": 18.50
    }
  ]
}
```

---

## 🧪 6. Executando os Testes Automatizados

Para rodar a suíte de testes unitários e de contexto:

```bash
./mvnw test
```
