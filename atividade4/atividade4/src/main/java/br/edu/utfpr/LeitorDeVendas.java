package br.edu.utfpr;

import br.edu.utfpr.model.FormaPagamento;
import br.edu.utfpr.model.RankingVendedor;
import br.edu.utfpr.model.ResumoVendas;
import br.edu.utfpr.model.Venda;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;

public class LeitorDeVendas {

    private final List<Venda> vendas;

    public LeitorDeVendas(String salesFile) {

        final var stream = getClass().getClassLoader().getResourceAsStream(salesFile);

        if (stream == null) {
            throw new IllegalStateException("Arquivo não encontrado");
        }

        final var builder = new CsvToBeanBuilder<VendaCsv>(new InputStreamReader(stream, StandardCharsets.UTF_8));

        vendas = builder
                .withType(VendaCsv.class)
                .withSeparator(';')
                .build()
                .parse()
                .stream()
                .map(VendaCsv::toVenda)
                .toList();
    }

    public List<Venda> vendasConcluidasAcimaDoValorNaRegiao(BigDecimal valorMinimo, String regiao) {
        // TODO apenas operações funcionais com streams
        return vendas.stream()
                .filter(Venda::isConcluida)
                .filter(venda -> venda.valor().compareTo(valorMinimo) > 0)
                .filter(venda -> venda.regiao().equals(regiao))
                .toList();
    }

    public Optional<Venda> vendaDeMaiorValorNaRegiao(String regiao) {
        // TODO apenas operações funcionais com streams
        return vendas.stream()
                .filter(venda -> venda.regiao().equals(regiao))
                .max(Comparator.comparing(Venda::valor));
    }

    public List<Venda> topNVendasPorValor(int n) {
        // TODO utilizar PriorityQueue
        PriorityQueue<Venda> queue = new PriorityQueue<>(
                Comparator.comparing(Venda::valor)
        );

        for (Venda venda : vendas) {
            if (queue.size() < n) {
                queue.offer(venda);
            } else if (venda.valor().compareTo(queue.peek().valor()) > 0) {
                queue.poll();
                queue.offer(venda);
            }
        }

        return queue.stream()
                .sorted(Comparator.comparing(Venda::valor).reversed())
                .toList();
    }

    public List<RankingVendedor> top3VendedoresPorFaturamento() {
        // TODO apenas operações funcionais com streams
        return vendas.stream()
                .filter(Venda::isConcluida)
                .collect(Collectors.groupingBy(Venda::vendedor))
                .entrySet()
                .stream()
                .map(entry -> new RankingVendedor(
                        entry.getKey(),
                        entry.getValue()
                                .stream()
                                .map(Venda::valor)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .sorted(Comparator.comparing(RankingVendedor::faturamento).reversed())
                .limit(3)
                .toList();
    }

    public Map<String, BigDecimal> valorTotalDeVendasConcluidasPorRegiao() {
        // TODO apenas operações funcionais com streams
        return vendas.stream()
                .filter(Venda::isConcluida)
                .collect(Collectors.groupingBy(
                        Venda::regiao,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Venda::valor,
                                BigDecimal::add)));

    }

    public ResumoVendas resumoDeVendasConcluidas() {
        // TODO apenas operações funcionais com streams, usar collector customizado, use a classe AcumuladorDeResumo
        return vendas.stream()
                .filter(Venda::isConcluida)
                .collect(Collector.of(
                    AcumuladorDeResumo::new,
                        (acumulador, venda) -> acumulador.somar(venda.valor()),
                        AcumuladorDeResumo::combinar,
                        AcumuladorDeResumo::finalizar
                ));
    }

    public List<BigDecimal> faturamentoAcumuladoPorVendedorEMes(String vendedor, YearMonth mes) {
        // TODO apenas operações funcionais com streams, use Gatherers
        return vendas.stream()
                .filter(Venda::isConcluida)
                .filter(v -> v.vendedor().equals(vendedor))
                .filter(v -> YearMonth.from(v.dataVenda()).equals(mes))
                .sorted(Comparator.comparing(Venda::dataVenda))
                .map(Venda::valor)
                .gather(Gatherers.scan(
                        () -> BigDecimal.ZERO,
                        BigDecimal::add
                ))
                .toList();
    }

    public FormaPagamento formaPagamentoComMaisCancelamentos() {
        // TODO apenas operações funcionais com streams, usar groupingBy + counting
        return vendas.stream()
                .filter(Venda::isCancelada)
                .collect(Collectors.groupingBy(Venda::formaPagamento,
                                Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public long diasEntrePrimeiraEUltimaVendaCancelada() {
        // TODO apenas operações funcionais com streams, usar Collectors.teeing com minBy e maxBy
        return vendas.stream()
                .filter(Venda::isCancelada)
                .collect(Collectors.teeing(
                        Collectors.minBy(Comparator.comparing(Venda::dataVenda)),
                        Collectors.maxBy(Comparator.comparing(Venda::dataVenda)),
                        (primeiraVenda, ultimaVenda) ->
                                ChronoUnit.DAYS.between(primeiraVenda.get().dataVenda(), ultimaVenda.get().dataVenda())
                                ));
    }

    public Map<Integer, Map<FormaPagamento, Long>> quantidadeDeVendasConcluidasPorFormaDePagamentoAgrupadasPorAno() {
        // TODO apenas operações funcionais com streams, usar groupingBy aninhado
        return vendas.stream()
                .filter(Venda::isConcluida)
                .collect(Collectors.groupingBy(
                        venda -> venda.dataVenda().getYear(),
                        Collectors.groupingBy(
                                Venda::formaPagamento,
                                Collectors.counting()) ));
    }

    private static final class AcumuladorDeResumo {

        private BigDecimal total = BigDecimal.ZERO;
        private long quantidade = 0;

        private void somar(BigDecimal valor) {
            total = total.add(valor);
            quantidade++;
        }

        private AcumuladorDeResumo combinar(AcumuladorDeResumo outro) {
            total = total.add(outro.total);
            quantidade += outro.quantidade;
            return this;
        }

        private ResumoVendas finalizar() {
            final var media = quantidade == 0
                    ? BigDecimal.ZERO
                    : total.divide(BigDecimal.valueOf(quantidade), 2, RoundingMode.HALF_UP);
            return new ResumoVendas(total, media, quantidade);
        }
    }
}
