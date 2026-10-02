package br.edu.utfpr;

import java.time.LocalDateTime;

public record Cotacao(String moeda, double valor, LocalDateTime coletadoEm) {

    public String paraCsv() {
        return moeda + "," + valor + "," + coletadoEm;
    }
}