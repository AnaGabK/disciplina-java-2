package br.edu.utfpr.dominio;

public record PedidoRejeitado(String identificador, String motivo) implements ResultadoPedido {
}
