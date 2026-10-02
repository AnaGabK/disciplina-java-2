package br.edu.utfpr.model;

public enum Status {

    CONCLUIDA("Concluída"), CANCELADA("Cancelada");

    Status(String texto) {
    }

    public static Status doTexto(String texto) {
        return switch (texto) {
            case "Concluída" -> CONCLUIDA;
            case "Cancelada" -> CANCELADA;
            default -> throw new IllegalArgumentException("Texto inválido");
        };
    }
}
