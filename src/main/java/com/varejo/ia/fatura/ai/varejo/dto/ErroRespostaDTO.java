package com.varejo.ia.fatura.ai.varejo.dto;

import java.time.Instant;

/**
 * Padroniza o retorno JSON em caso de erros na API.
 */
public record ErroRespostaDTO(
        Instant timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho
) {
}
