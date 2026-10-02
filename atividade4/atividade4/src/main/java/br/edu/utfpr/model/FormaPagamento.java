package br.edu.utfpr.model;

import java.util.EnumSet;

public enum FormaPagamento {

    BOLETO_BANCARIO("boleto bancário"),
    CARTAO_DE_CREDITO("Cartão de Crédito"),
    CARTAO_DE_DEBITO("Cartão de Débito"),
    TRANSFERENCIA_ELETRONICA("Transferência Eletrônica");

    private final String texto;

    FormaPagamento(String texto) {
        this.texto = texto;
    }

    private String getTexto() {
        return texto;
    }

    public static FormaPagamento doTexto(String texto) {
        return EnumSet.allOf(FormaPagamento.class)
                .stream()
                .filter(valor -> valor.getTexto().equalsIgnoreCase(texto))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Forma de pagamento inválida"));
    }
}
