package br.edu.utfpr;

import java.util.Optional;

/**
 * Um simples parser de JSON que entende a seguinte entrada:
 * <p>
 * {"amount":1.0,"base":"USD","date":"2026-01-30","rates":{"BRL":5.43}}
 */
public final class JsonParser {

    public static Optional<Double> extrairTaxa(String json, String moeda) {

        final String chave = "\"" + moeda + "\":";

        final int index = json.indexOf(chave);

        if (index < 0) {
            return Optional.empty();
        }

        int inicio = index + chave.length();
        int fim = inicio;

        while (fim < json.length()) {
            final char xar = json.charAt(fim);
            if (Character.isDigit(xar) || xar == '.' || xar == '-' || xar == 'E' || xar == 'e' || xar == '+') {
                fim++;
            } else {
                break;
            }
        }

        try {
            return Optional.of(Double.parseDouble(json.substring(inicio, fim)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}