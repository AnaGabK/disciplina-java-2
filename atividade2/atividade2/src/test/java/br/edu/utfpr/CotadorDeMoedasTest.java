package br.edu.utfpr;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CotadorDeMoedasTest {

    private final CotadorDeMoedas cotador = new CotadorDeMoedas();

    private static final LocalDateTime INSTANTE = LocalDateTime.of(2026, 1, 30, 12, 0, 0);

    private static CompletableFuture<Optional<Cotacao>> sucesso(String moeda, double valor) {
        return CompletableFuture.completedFuture(Optional.of(new Cotacao(moeda, valor, INSTANTE)));
    }

    private static CompletableFuture<Optional<Cotacao>> falha() {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Nested
    @DisplayName("verificarEntrada")
    class VerificarEntrada {

        @Test
        @DisplayName("retorna o caminho padrão entrada/moedas.txt")
        void retornaCaminhoPadrao() {
            assertThat(cotador.verificarEntrada())
                    .isEqualTo(Path.of("entrada", "moedas.txt"));
        }
    }

    @Nested
    @DisplayName("verificarSaida")
    class VerificarSaida {

        @Test
        @DisplayName("retorna um caminho válido, (re)criando o diretório de saída mesmo que ele já exista")
        void retornaCaminhoPadrao() throws IOException {

            final Path dirSaida = Path.of("saida");

            apagarRecursivamente(dirSaida);
            assertThat(Files.exists(dirSaida)).isFalse();

            try {
                final Path saida = cotador.verificarSaida();

                assertThat(saida).isEqualTo(Path.of("saida", "cotacoes.csv"));
                assertThat(Files.isDirectory(saida.getParent())).isTrue(); // o diretório foi criado
            } finally {
                apagarRecursivamente(dirSaida);
            }
        }
    }

    @Nested
    @DisplayName("lerArquivoDeMoedas")
    class LerArquivoDeMoedas {

        @Test
        @DisplayName("ignora moedas fora do formato de 3 letras e normaliza (trim + maiúsculas)")
        void ignoraMoedasEmFormatoIncorreto(@TempDir Path dir) throws IOException {

            final Path arquivo = dir.resolve("moedas.txt");

            Files.write(arquivo, List.of(
                    "eur",       // ok -> EUR
                    "  brl  ",   // ok (após trim) -> BRL
                    "DOLAR",     // 5 letras -> descartada
                    "JP",        // 2 letras -> descartada
                    "",          // vazia -> descartada
                    "usd"        // ok -> USD
            ));

            final List<String> moedas = cotador.lerArquivoDeMoedas(arquivo);

            assertThat(moedas).containsExactly("EUR", "BRL", "USD");
        }

        @Test
        @DisplayName("retorna lista vazia quando o arquivo não existe")
        void arquivoInexistenteRetornaListaVazia(@TempDir Path dir) {
            final List<String> moedas = cotador.lerArquivoDeMoedas(dir.resolve("naoexiste.txt"));
            assertThat(moedas).isEmpty();
        }
    }

    @Nested
    @DisplayName("cotarERegistrar")
    class CotarERegistrar {

        @Test
        @DisplayName("quando a consulta de uma moeda falha, as demais são registradas sem erro")
        void quandoUmaConsultaFalhaRegistraAsDemais(@TempDir Path dir) throws IOException {

            final Path saida = dir.resolve("cotacoes.csv");

            final ClienteCambio cliente = mock(ClienteCambio.class);
            when(cliente.consultar("EUR")).thenReturn(sucesso("EUR", 0.92));
            when(cliente.consultar("BRL")).thenReturn(falha()); // esta falha
            when(cliente.consultar("GBP")).thenReturn(sucesso("GBP", 0.79));

            cotador.cotarERegistrar(saida, List.of("EUR", "BRL", "GBP"), cliente);

            final List<String> linhas = Files.readAllLines(saida);

            assertThat(linhas.getFirst()).isEqualTo("moeda,valor,coletadoEm");

            assertThat(linhas).anyMatch(l -> l.startsWith("EUR,"));
            assertThat(linhas).anyMatch(l -> l.startsWith("GBP,"));
            assertThat(linhas).noneMatch(l -> l.startsWith("BRL,")); // a que falhou não é registrada
        }

        @Test
        @DisplayName("cria o arquivo de saída quando ele ainda não existe")
        void quandoArquivoDeSaidaNaoExisteCriaOArquivo(@TempDir Path dir) throws IOException {

            final Path saida = dir.resolve("cotacoes.csv");
            assertThat(Files.exists(saida)).isFalse();

            final ClienteCambio cliente = mock(ClienteCambio.class);
            when(cliente.consultar("EUR")).thenReturn(sucesso("EUR", 0.92));

            cotador.cotarERegistrar(saida, List.of("EUR"), cliente);

            assertThat(Files.exists(saida)).isTrue();
            assertThat(Files.readString(saida))
                    .startsWith("moeda,valor,coletadoEm\n")
                    .contains("EUR,0.92,");
        }

        @Test
        @DisplayName("quando a lista de moedas é vazia, não faz nada: não cria nem altera o arquivo CSV")
        void semMoedasNaoCriaNemAlteraOArquivo(@TempDir Path dir) throws IOException {

            final Path saida = dir.resolve("cotacoes.csv");

            final ClienteCambio cliente = mock(ClienteCambio.class);

            cotador.cotarERegistrar(saida, List.of(), cliente);
            assertThat(Files.exists(saida)).isFalse();

            final String conteudoOriginal = "conteudo,pre,existente\n";
            Files.writeString(saida, conteudoOriginal);

            cotador.cotarERegistrar(saida, List.of(), cliente);
            assertThat(Files.readString(saida)).isEqualTo(conteudoOriginal);

            verifyNoInteractions(cliente);
        }
    }

    private static void apagarRecursivamente(Path raiz) throws IOException {

        if (!Files.exists(raiz)) {
            return;
        }

        try (Stream<Path> caminhos = Files.walk(raiz)) {
            caminhos.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }
}
