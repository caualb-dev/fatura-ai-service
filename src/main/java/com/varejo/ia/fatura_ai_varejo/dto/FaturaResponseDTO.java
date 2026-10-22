package com.varejo.ia.fatura_ai_varejo.dto;

import java.math.BigDecimal;
import java.util.List;


public record FaturaResponseDTO(
        String tipoDocumento,
        String nomeEmissor,
        String cnpjEmissor,
        String enderecoEmissor,
        String numeroDocumento,
        String chaveAcesso,
        String dataEmissao,
        String cpfCnpjConsumidor,
        BigDecimal subtotal,
        BigDecimal descontos,
        BigDecimal valorTotal,
        String formaPagamento,
        List<ItemFaturaDTO> itens
) {
}
