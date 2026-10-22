package com.varejo.ia.fatura_ai_varejo.dto;

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
