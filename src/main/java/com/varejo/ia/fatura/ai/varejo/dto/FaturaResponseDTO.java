package com.varejo.ia.fatura.ai.varejo.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Representa o JSON padronizado de saída com os dados estruturados da fatura/cupom fiscal.
 */
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
