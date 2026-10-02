package br.edu.utfpr;

import java.net.http.HttpClient;

public class Main {

    static void main() {

        final var cotador = new CotadorDeMoedas();

        final var entrada = cotador.verificarEntrada();
        final var saida = cotador.verificarSaida();

        final var moedas = cotador.lerArquivoDeMoedas(entrada);

        try (HttpClient client = HttpClient.newHttpClient()) {
            cotador.cotarERegistrar(saida, moedas, new ClienteCambio(client));
        }
    }
}