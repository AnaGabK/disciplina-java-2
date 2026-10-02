package br.edu.utfpr;

import br.edu.utfpr.model.FormaPagamento;
import br.edu.utfpr.model.RankingVendedor;
import br.edu.utfpr.model.Venda;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LeitorDeVendasTest {

    private final LeitorDeVendas reader = new LeitorDeVendas("vendas.csv");

    @Test
    @Order(1)
    @DisplayName("topico 1 - composicao de predicados: vendas concluidas acima do valor, na regiao")
    void assert_vendasConcluidasAcimaDoValorNaRegiao() {

        final var resultado = reader.vendasConcluidasAcimaDoValorNaRegiao(new BigDecimal("14500"), "Sul");

        assertThat(resultado)
                .extracting(Venda::numero)
                .containsExactly("9854", "10016", "10405", "11039");
    }

    @Test
    @Order(2)
    @DisplayName("topico 2 - Optional sem antipadrao: venda de maior valor na regiao")
    void assert_vendaDeMaiorValorNaRegiao() {

        assertThat(reader.vendaDeMaiorValorNaRegiao("Norte"))
                .map(Venda::numero)
                .hasValue("10634");

        assertThat(reader.vendaDeMaiorValorNaRegiao("Regiao Inexistente"))
                .isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("topico 3 - estrutura de dados: top N vendas por valor via PriorityQueue")
    void assert_topNVendasPorValor() {

        final var resultado = reader.topNVendasPorValor(3);

        assertThat(resultado)
                .extracting(Venda::numero)
                .containsExactly("10469", "10405", "10993");
    }

    @Test
    @Order(4)
    @DisplayName("topico 4 - Top 3 de vendedores por faturamento")
    void assert_top3VendedoresPorFaturamento() {

        final var resultado = reader.top3VendedoresPorFaturamento();

        assertThat(resultado)
                .hasSize(3)
                .extracting(RankingVendedor::vendedor, RankingVendedor::faturamento)
                .startsWith(
                        tuple("Daniel Campos", new BigDecimal("2028668.65")),
                        tuple("Adriana Gomes", new BigDecimal("1904660.25")),
                        tuple("Newton Souza", new BigDecimal("1117876.28"))
                );
    }

    @Test
    @Order(5)
    @DisplayName("topico 5 - Valor total das vendas concluidas por região")
    void assert_valorTotalVendasConcluidasPorRegiao() {

        assertThat(reader.valorTotalDeVendasConcluidasPorRegiao())
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        "Centro-Oeste", new BigDecimal("2123698.94"),
                        "Nordeste", new BigDecimal("4887480.52"),
                        "Norte", new BigDecimal("2779893.90"),
                        "Sudeste", new BigDecimal("5439543.30"),
                        "Sul", new BigDecimal("2645658.81")
                ));
    }

    @Test
    @Order(6)
    @DisplayName("topico 6 - resumo de vendas concluidas")
    void assert_resumoDeVendasConcluidas() {

        final var resumo = reader.resumoDeVendasConcluidas();

        assertThat(resumo.total()).isEqualTo(new BigDecimal("17876275.47"));
        assertThat(resumo.media()).isEqualTo(new BigDecimal("3728.11"));
        assertThat(resumo.quantidade()).isEqualTo(4795L);
    }

    @Test
    @Order(7)
    @DisplayName("topico 7 - faturamento acumulado por vendedor e mes")
    void assert_faturamentoAcumuladoPorVendedorEMes() {

        final var resultado = reader.faturamentoAcumuladoPorVendedorEMes("Adriana Gomes", YearMonth.of(2010, 7));

        assertThat(resultado)
                .containsExactly(
                        new BigDecimal("10330.88"),
                        new BigDecimal("12580.55"),
                        new BigDecimal("22912.15"),
                        new BigDecimal("23292.56"),
                        new BigDecimal("25477.19"),
                        new BigDecimal("26612.37"),
                        new BigDecimal("33221.24")
                );
    }

    @Test
    @Order(8)
    @DisplayName("topico 8 - forma de pagamento com mais cancelamentos")
    void assert_formaPagamentoComMaisCancelamentos() {

        assertThat(reader.formaPagamentoComMaisCancelamentos())
                .isEqualTo(FormaPagamento.CARTAO_DE_CREDITO);
    }

    @Test
    @Order(9)
    @DisplayName("topico 9 - dias entre a primeira e a ultima venda cancelada")
    void assert_diasEntrePrimeiraEUltimaVendaCancelada() {

        assertThat(reader.diasEntrePrimeiraEUltimaVendaCancelada())
                .isEqualTo(1814);
    }

    @Test
    @Order(10)
    @DisplayName("topico 10 - quantidade de vendas concluidas por forma de pagamento, agrupadas por ano")
    void assert_quantidadeDeVendasConcluidasPorFormaDePagamentoAgrupadasPorAno() {

        final var resultado = reader.quantidadeDeVendasConcluidasPorFormaDePagamentoAgrupadasPorAno();

        assertThat(resultado.get(2010))
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        FormaPagamento.BOLETO_BANCARIO, 107L,
                        FormaPagamento.CARTAO_DE_CREDITO, 395L,
                        FormaPagamento.CARTAO_DE_DEBITO, 295L,
                        FormaPagamento.TRANSFERENCIA_ELETRONICA, 137L
                ));

        assertThat(resultado.get(2014))
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        FormaPagamento.BOLETO_BANCARIO, 132L,
                        FormaPagamento.CARTAO_DE_CREDITO, 376L,
                        FormaPagamento.CARTAO_DE_DEBITO, 300L,
                        FormaPagamento.TRANSFERENCIA_ELETRONICA, 142L
                ));
    }
}
