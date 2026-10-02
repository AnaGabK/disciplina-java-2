package br.edu.utfpr.validacao;

import java.util.List;

public sealed interface ResultadoValidacao permits ResultadoValidacao.Valido, ResultadoValidacao.Invalido {

    record Valido() implements ResultadoValidacao {
        // TODO revisar e implementar corretamente conforme requisitos
    }

    record Invalido(List<Violacao> violacoes) implements ResultadoValidacao {
        // TODO revisar e implementar corretamente conforme requisitos
        public Invalido{
            if (violacoes == null || violacoes.isEmpty()) throw new IllegalArgumentException("a lista de violacoes nao pode ser vazia");
        }
    }

    static ResultadoValidacao de(List<Violacao> violacoes) {
        return violacoes.isEmpty() ? new Valido() : new Invalido(violacoes);
    }
}
