package com.varejo.ia.fatura.ai.varejo.dto;

import java.math.BigDecimal;

/**
 * Representa um item/produto individual extraído do documento fiscal.
 */
public record ItemFaturaDTO(
        String codigo,
        String descricao,
        BigDecimal quantidade,
        String unidadeMedida,
        BigDecimal valorUnitario,
        BigDecimal valorTotal
) {
}
