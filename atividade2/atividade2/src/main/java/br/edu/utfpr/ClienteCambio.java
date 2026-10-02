package br.edu.utfpr;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ClienteCambio {

    private static final String BASE_URL = "https://api.frankfurter.dev/v1/latest?base=USD&symbols=";

    private final HttpClient client;

    public ClienteCambio(HttpClient client) {
        this.client = client;
    }

    public CompletableFuture<Optional<Cotacao>> consultar(String moeda) {

        // TODO implementar aqui a chamada para a API
         HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + moeda))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json ->
                    JsonParser.extrairTaxa(json, moeda)
                        .map(taxa -> new Cotacao(moeda, taxa, java.time.LocalDateTime.now()))
                )
                .exceptionally(ex -> {
                    IO.println("Falha ao consultar moeda: " + moeda);
                    return Optional.empty();
                });
        // return CompletableFuture.completedFuture(Optional.empty());
    }
}