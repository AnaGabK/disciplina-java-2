package br.edu.utfpr.utilidades;

import br.edu.utfpr.dominio.CotacaoFrete;
import br.edu.utfpr.dominio.Estoque;
import br.edu.utfpr.dominio.Preco;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class ServicosExternos {

    private static final Map<String, Integer> BASE_ESTOQUE = Map.of(
            "MOUSE", 10,
            "TECLADO", 0,
            "NOTEBOOK", 5,
            "MONITOR", 5
    );

    private static final Map<String, BigDecimal> BASE_PRECO = Map.of(
            "MOUSE", new BigDecimal("89.90"),
            "TECLADO", new BigDecimal("149.90"),
            "NOTEBOOK", new BigDecimal("3500.00"),
            "MONITOR", new BigDecimal("1299.90")
    );

    private ServicosExternos() {
    }

    public static Estoque consultarEstoque(String produto, String identificador) {
        dormir(200);
        if (identificadorTerminaEmNumeroPar(identificador)) {
            throw new RuntimeException("erro simulado para pedido " + identificador);
        }
        final Integer quantidade = BASE_ESTOQUE.get(produto);
        if (quantidade == null) {
            throw new IllegalArgumentException("produto nao encontrado no estoque: " + produto);
        }
        return new Estoque(quantidade);
    }

    public static Preco consultarPreco(String produto, String identificador) {
        dormir(200);
        if (identificadorTerminaEmNumeroPar(identificador)) {
            throw new RuntimeException("erro simulado para pedido " + identificador);
        }
        final BigDecimal valorUnitario = BASE_PRECO.get(produto);
        if (valorUnitario == null) {
            throw new IllegalArgumentException("produto nao encontrado na base de precos: " + produto);
        }
        return new Preco(valorUnitario);
    }

    public static CotacaoFrete cotarFreteTransportadoraUm(String produto) {
        dormir(ThreadLocalRandom.current().nextLong(100, 301)); // 100–300 ms
        return new CotacaoFrete("TranspUm", BigDecimal.valueOf(49.90));
    }

    public static CotacaoFrete cotarFreteTransportadoraDois(String produto) {
        dormir(ThreadLocalRandom.current().nextLong(500, 901)); // 500–900 ms
        return new CotacaoFrete("TranspDois", BigDecimal.valueOf(19.90));
    }

    private static boolean identificadorTerminaEmNumeroPar(String identificador) {
        if (identificador == null || identificador.isEmpty()) return false;
        final char ultimo = identificador.charAt(identificador.length() - 1);
        return Character.isDigit(ultimo) && (Character.getNumericValue(ultimo) % 2 == 0);
    }

    private static void dormir(long milissegundos) {
        try {
            Thread.sleep(Duration.ofMillis(milissegundos));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("servico interrompido durante sleep", ex);
        }
    }
}
