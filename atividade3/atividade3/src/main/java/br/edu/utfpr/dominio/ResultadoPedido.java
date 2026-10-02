package br.edu.utfpr.dominio;

public sealed interface ResultadoPedido permits PedidoAprovado, PedidoRejeitado {
}
