package com.varejo.ia.fatura_ai_varejo.dto;

import java.math.BigDecimal;


public record ItemFaturaDTO(
        String codigo,
        String descricao,
        BigDecimal quantidade,
        String unidadeMedida,
        BigDecimal valorUnitario,
        BigDecimal valorTotal
) {
}
