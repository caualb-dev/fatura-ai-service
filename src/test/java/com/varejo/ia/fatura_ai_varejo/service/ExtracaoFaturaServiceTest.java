package com.varejo.ia.fatura_ai_varejo.service;

import com.varejo.ia.fatura_ai_varejo.controller.FaturaController;
import com.varejo.ia.fatura_ai_varejo.dto.FaturaResponseDTO;
import com.varejo.ia.fatura_ai_varejo.dto.ItemFaturaDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ExtracaoFaturaServiceTest {

    private final ExtracaoFaturaService service = new ExtracaoFaturaService("fake-api-key", "gemini-3.8-flash");

    @Test
    void deveRejeitarArquivoVazio() {
        MockMultipartFile arquivoVazio = new MockMultipartFile(
                "arquivo",
                "cupom.jpg",
                "image/jpeg",
                new byte[0]
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.extrairDadosFatura(arquivoVazio)
        );

        assertTrue(ex.getMessage().contains("não pode estar vazio"));
    }

    @Test
    void deveRejeitarFormatoNaoSuportado() {
        MockMultipartFile arquivoTexto = new MockMultipartFile(
                "arquivo",
                "nota.txt",
                "text/plain",
                "conteudo invalido".getBytes()
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.extrairDadosFatura(arquivoTexto)
        );

        assertTrue(ex.getMessage().contains("Formato de arquivo não suportado"));
    }

    @Test
    void controllerDeveRetornarFaturaExtraidaComSucesso() {
        ExtracaoFaturaService serviceMock = Mockito.mock(ExtracaoFaturaService.class);
        FaturaController controller = new FaturaController(serviceMock);

        MockMultipartFile imagemValida = new MockMultipartFile(
                "arquivo",
                "cupom.png",
                "image/png",
                new byte[]{1, 2, 3, 4}
        );

        FaturaResponseDTO respostaEsperada = new FaturaResponseDTO(
                "NFCE",
                "SUPERMERCADO BOM PRECO LTDA",
                "12.345.678/0001-90",
                "AV. BRASIL, 1500 - CENTRO",
                "004582",
                "35261012345678000190650010000045821000045829",
                "2026-10-21T19:45:12",
                "123.456.789-00",
                new BigDecimal("68.30"),
                new BigDecimal("3.30"),
                new BigDecimal("65.00"),
                "PIX",
                List.of(
                        new ItemFaturaDTO(
                                "7891000100103",
                                "ARROZ BRANCO 5KG",
                                new BigDecimal("2"),
                                "UN",
                                new BigDecimal("24.90"),
                                new BigDecimal("49.80")
                        )
                )
        );

        when(serviceMock.extrairDadosFatura(imagemValida)).thenReturn(respostaEsperada);

        ResponseEntity<FaturaResponseDTO> response = controller.extrairFatura(imagemValida);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("SUPERMERCADO BOM PRECO LTDA", response.getBody().nomeEmissor());
        assertEquals(new BigDecimal("65.00"), response.getBody().valorTotal());
        assertEquals(1, response.getBody().itens().size());
    }

    @Test
    void controllerDeveRetornarStatusOnline() {
        ExtracaoFaturaService serviceMock = Mockito.mock(ExtracaoFaturaService.class);
        FaturaController controller = new FaturaController(serviceMock);

        ResponseEntity<Map<String, String>> response = controller.verificarStatus();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ONLINE", response.getBody().get("status"));
    }
}
