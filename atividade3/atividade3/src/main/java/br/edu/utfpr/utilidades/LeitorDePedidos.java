package br.edu.utfpr.utilidades;

import br.edu.utfpr.dominio.Pedido;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class LeitorDePedidos {

    public static List<Pedido> ler(Path arquivoEntrada) {
        try (final Stream<String> linhas = Files.lines(arquivoEntrada)) {
            return linhas.map(String::trim)
                    .filter(linha -> !linha.isBlank())
                    .map(LeitorDePedidos::linhaParaPedido)
                    .toList();
        } catch (final IOException excecao) {
            throw new UncheckedIOException("Falha ao ler arquivo de pedidos", excecao);
        }
    }

    private static Pedido linhaParaPedido(String linha) {

        final String[] partes = linha.split(";");

        if (partes.length != 3) {
            throw new IllegalArgumentException("linha invalida: " + linha);
        }

        final String identificador = partes[0].trim();
        final String produto = partes[1].trim().toUpperCase();
        final int quantidade = Integer.parseInt(partes[2].trim());

        return new Pedido(identificador, produto, quantidade);
    }
}
