package br.edu.utfpr.dominio;

import java.util.List;

public record Relatorio(List<PedidoAprovado> aprovados, List<PedidoRejeitado> rejeitados) {

    public int totalProcessado() {
        return aprovados.size() + rejeitados.size();
    }
}
