package br.edu.utfpr.dominio;

import java.math.BigDecimal;

public record CotacaoFrete(String transportadora, BigDecimal valor) {
}
