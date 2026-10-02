package br.edu.utfpr.dominio;

import java.math.BigDecimal;

public record PedidoAprovado(
        String identificador,
        BigDecimal valorTotal,
        CotacaoFrete freteEscolhido
) implements ResultadoPedido {
}
