package com.varejo.ia.fatura.ai.varejo.controller;

import com.varejo.ia.fatura.ai.varejo.dto.FaturaResponseDTO;
import com.varejo.ia.fatura.ai.varejo.service.ExtracaoFaturaService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/faturas")
public class FaturaController {

    private final ExtracaoFaturaService extracaoFaturaService;

    public FaturaController(ExtracaoFaturaService extracaoFaturaService) {
        this.extracaoFaturaService = extracaoFaturaService;
    }

    @PostMapping(
            value = "/extrair",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<FaturaResponseDTO> extrairFatura(
            @RequestParam("arquivo") MultipartFile arquivo
    ) {
        FaturaResponseDTO resultado = extracaoFaturaService.extrairDadosFatura(arquivo);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> verificarStatus() {
        return ResponseEntity.ok(Map.of(
                "servico", "fatura-ai-varejo",
                "status", "ONLINE",
                "modelo", "gemini-3.8-flash"
        ));
    }
}
