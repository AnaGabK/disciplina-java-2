package br.edu.utfpr.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Venda(
        String numero,
        LocalDate dataVenda,
        LocalDate dataEntrega,
        String regiao,
        String estado,
        String gerente,
        String vendedor,
        String departamento,
        FormaPagamento formaPagamento,
        BigDecimal valor,
        Status status
) {

    public boolean isConcluida() {
        return this.status == Status.CONCLUIDA;
    }

    public boolean isCancelada() {
        return this.status == Status.CANCELADA;
    }
}
