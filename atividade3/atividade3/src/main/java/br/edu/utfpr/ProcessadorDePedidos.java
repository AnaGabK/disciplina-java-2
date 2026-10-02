package br.edu.utfpr;

import br.edu.utfpr.dominio.*;
import br.edu.utfpr.utilidades.LeitorDePedidos;
import br.edu.utfpr.utilidades.ServicosExternos;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public final class ProcessadorDePedidos {

    public ResultadoPedido processarPedido(Pedido pedido) {
        // TODO ver requisitos

        try (var escopo = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())){
            Subtask<Estoque> estoque = escopo.fork(() ->
                    ServicosExternos.consultarEstoque(
                        pedido.produto(),
                        pedido.identificador()));
            Subtask<Preco> preco = escopo.fork(() ->
                ServicosExternos.consultarPreco(
                        pedido.produto(),
                        pedido.identificador()));
            Subtask<CotacaoFrete> frete = escopo.fork(() ->
                    cotarFrete(pedido.produto()));

            try{
                escopo.join();
            }catch(Exception e){
                Throwable causa = e.getCause();
                return new PedidoRejeitado(pedido.identificador(), causa.getMessage());
            }

            Preco precoProduto = preco.get();
            Estoque estoqueProduto = estoque.get();
            CotacaoFrete fretePedido = frete.get();

            BigDecimal valorTotal = precoProduto.valorUnitario().multiply(BigDecimal.valueOf(pedido.quantidade())).add(fretePedido.valor());

            if(pedido.quantidade() > estoqueProduto.quantidadeDisponivel()) {
                return new PedidoRejeitado(pedido.identificador(), "estoque");
            }
            return new PedidoAprovado(pedido.identificador(), valorTotal, fretePedido);
        }
    }

    private CotacaoFrete cotarFrete(String produto) throws InterruptedException {
        // TODO ver requisitos
        try (var escopo = StructuredTaskScope.open(StructuredTaskScope.Joiner.<CotacaoFrete>anySuccessfulResultOrThrow())){
            escopo.fork(() -> ServicosExternos.cotarFreteTransportadoraUm(produto));
            escopo.fork(() -> ServicosExternos.cotarFreteTransportadoraDois(produto));
            CotacaoFrete frete = escopo.join();
            return frete;
        }
    }

    public Relatorio processarArquivo(Path arquivoEntrada) {
        // TODO ver requisitos
        List<Pedido> listaDePedidos = LeitorDePedidos.ler(arquivoEntrada);
        List<PedidoAprovado> aprovados = new ArrayList<>();
        List<PedidoRejeitado> rejeitados = new ArrayList<>();

        try (var escopo = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAll())){
            List<Subtask<ResultadoPedido>> subtarefas = listaDePedidos.stream()
                    .map(pedido -> escopo.fork(() -> processarPedido(pedido)))
                    .toList();
            escopo.join();
            for(Subtask<ResultadoPedido> subtarefa : subtarefas) {
                switch (subtarefa.get()) {
                    case PedidoAprovado aprovado -> aprovados.add(aprovado);
                    case PedidoRejeitado rejeitado -> rejeitados.add(rejeitado);
                }
            }

            return new Relatorio(
                    aprovados,
                    rejeitados);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
