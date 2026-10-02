package br.edu.utfpr.model;

import java.math.BigDecimal;

public record ResumoVendas(BigDecimal total, BigDecimal media, long quantidade) {
}
