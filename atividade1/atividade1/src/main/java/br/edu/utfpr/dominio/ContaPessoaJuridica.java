package br.edu.utfpr.dominio;

import br.edu.utfpr.anotacoes.NaoNulo;
import br.edu.utfpr.anotacoes.Positivo;
import br.edu.utfpr.anotacoes.Tamanho;

import java.math.BigDecimal;

public record ContaPessoaJuridica(
        @NaoNulo(mensagem = "agencia nao pode ser nula")
        @Tamanho(min = 4, max = 4, mensagem = "tamanho de agencia fora dos limites")
        String agencia,

        @NaoNulo(mensagem = "numero nao pode ser nula")
        String numero,

        @NaoNulo(mensagem = "razaoSocial nao pode ser nula")
        String razaoSocial,

        @NaoNulo(mensagem = "cnpj nao pode ser nula")
        @Tamanho(min = 14, max = 14, mensagem = "tamanho de agencia fora dos limites")
        String cnpj,

        @NaoNulo(mensagem = "capitalSocial nao pode ser nula")
        BigDecimal capitalSocial
) implements ContaBancaria{
    // TODO revisar e implementar corretamente conforme requisitos
}
