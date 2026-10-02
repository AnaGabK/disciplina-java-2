package br.edu.utfpr.dominio;

import br.edu.utfpr.anotacoes.NaoNulo;
import br.edu.utfpr.anotacoes.Tamanho;

import java.time.LocalDate;

public record ContaPessoaFisica(
        @NaoNulo(mensagem = "agencia nao pode ser nula")
        @Tamanho(min = 4, max = 4, mensagem = "tamanho de agencia fora dos limites")
        String agencia,

        @NaoNulo(mensagem = "numero nao pode ser nula")
        String numero,

        @NaoNulo(mensagem = "titular nao pode ser nula")
        String titular,

        @NaoNulo(mensagem = "cpf nao pode ser nula")
        @Tamanho(min = 11, max = 11, mensagem = "tamanho de cpf fora dos limites")
        String cpf,

        @NaoNulo(mensagem = "email nao pode ser nula")
        String email,

        @NaoNulo(mensagem = "dataNascimento nao pode ser nula")
        LocalDate dataNascimento
) implements ContaBancaria{
    // TODO revisar e implementar corretamente conforme requisitos
}
