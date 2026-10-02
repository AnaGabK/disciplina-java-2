package br.edu.utfpr;

import br.edu.utfpr.dominio.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("Processador de Pedidos")
@TestMethodOrder(MethodOrderer.MethodName.class)
class ProcessadorDePedidosTest {

    private final ProcessadorDePedidos processador = new ProcessadorDePedidos();

    @Test
    @DisplayName("Teste 1 — pedido válido é aprovado")
    void teste1_pedidoAprovadoSimples() {

        final ResultadoPedido resultado = processador.processarPedido(new Pedido("P1", "MOUSE", 2));

        assertThat(resultado).isInstanceOf(PedidoAprovado.class);
    }

    @Test
    @DisplayName("Teste 2 — estoque insuficiente rejeita o pedido")
    void teste2_estoqueInsuficienteRejeita() {

        final ResultadoPedido resultado = processador.processarPedido(new Pedido("P21", "TECLADO", 1));

        assertThat(resultado)
                .isInstanceOfSatisfying(PedidoRejeitado.class, rejeitado ->
                        assertThat(rejeitado.motivo())
                                .as("motivo da rejeicao")
                                .contains("estoque"));
    }

    @Test
    @DisplayName("Teste 3 — produto inexistente é rejeitado")
    void teste3_produtoInexistenteRejeita() {

        final ResultadoPedido resultado = processador.processarPedido(new Pedido("P3", "CADEIRA", 1));

        assertThat(resultado).isInstanceOf(PedidoRejeitado.class);
    }

    @Test
    @DisplayName("Teste 4 — valor total = preço × quantidade + frete")
    void teste4_calculoDoValorTotalCorreto() {

        final ResultadoPedido resultado = processador.processarPedido(new Pedido("P41", "NOTEBOOK", 2));

        assertThat(resultado)
                .isInstanceOfSatisfying(PedidoAprovado.class, aprovado ->
                        assertThat(aprovado.valorTotal())
                                .as("valor total deve ser 3500*2 + 49.90 = 7049.90")
                                .isCloseTo(new BigDecimal("7049.90"), within(new BigDecimal("0.01"))));
    }

    @Test
    @DisplayName("Teste 5 — frete escolhido é o da transportadora mais rápida")
    void teste5_freteEscolhidoEhOMaisRapido() {

        final ResultadoPedido resultado = processador.processarPedido(new Pedido("P5", "MONITOR", 1));

        assertThat(resultado)
                .isInstanceOfSatisfying(PedidoAprovado.class, aprovado ->
                        assertThat(aprovado.freteEscolhido().transportadora())
                                .isEqualTo("TranspUm"));
    }

    @Test
    @DisplayName("Teste 6 — frete cotado concorrentemente (< 700 ms)")
    void teste6_freteCotadoConcorrentemente() {

        final long inicio = System.currentTimeMillis();

        processador.processarPedido(new Pedido("P61", "MONITOR", 1));

        final long duracao = System.currentTimeMillis() - inicio;

        assertThat(duracao)
                .as("frete deve ser cotado concorrentemente (foi %d ms)", duracao)
                .isLessThan(700L);
    }

    @Test
    @DisplayName("Teste 7 — estoque e preço buscados em paralelo (< 350 ms)")
    void teste7_dadosBuscadosConcorrentemente() {

        final long inicio = System.currentTimeMillis();

        processador.processarPedido(new Pedido("P7", "TECLADO", 1));

        final long duracao = System.currentTimeMillis() - inicio;

        assertThat(duracao)
                .as("estoque e preco devem ser buscados em paralelo (foi %d ms)", duracao)
                .isLessThan(350L);
    }

    @Test
    @DisplayName("Teste 8 — processar arquivo: 4 pedidos, 3 aprovados, 1 rejeitado")
    void teste8_processarArquivoCompleto() throws Exception {

        final Path arquivo = Files.createTempFile("pedidos", ".txt");

        try {
            Files.writeString(arquivo, """
                    P101;MOUSE;2
                    P113;TECLADO;1
                    P125;NOTEBOOK;1
                    
                    P137;MONITOR;3
                    """);

            final Relatorio relatorio = processador.processarArquivo(arquivo);

            assertThat(relatorio.totalProcessado()).isEqualTo(4);
            assertThat(relatorio.aprovados()).hasSize(3);
            assertThat(relatorio.rejeitados()).hasSize(1);

        } finally {
            Files.deleteIfExists(arquivo);
        }
    }

    @Test
    @DisplayName("Teste 9 — erro no serviço externo rejeita pedido com identificador pares")
    void teste9_erroServicoExternoRejeita() {

        final ResultadoPedido resultado = processador.processarPedido(new Pedido("P2", "MOUSE", 1));

        assertThat(resultado)
                .isInstanceOfSatisfying(PedidoRejeitado.class, rejeitado -> {

                    assertThat(rejeitado.identificador())
                            .as("identificador do pedido rejeitado")
                            .isEqualTo("P2");

                    assertThat(rejeitado.motivo())
                            .as("motivo deve mencionar o identificador")
                            .contains("P2");
                });
    }
}
