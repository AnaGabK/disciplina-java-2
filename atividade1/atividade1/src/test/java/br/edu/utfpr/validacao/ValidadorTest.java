package br.edu.utfpr.validacao;

import br.edu.utfpr.dominio.ContaBancaria;
import br.edu.utfpr.dominio.ContaPessoaFisica;
import br.edu.utfpr.dominio.ContaPessoaJuridica;
import br.edu.utfpr.validacao.ResultadoValidacao.Invalido;
import br.edu.utfpr.validacao.ResultadoValidacao.Valido;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ValidadorTest {

    private final Validador validador = new Validador();

    @Test
    @DisplayName("Caso 1 - PF valida -> Valido (nenhuma violacao)")
    void caso1_pessoaFisicaValida() {

        final ContaBancaria conta = new ContaPessoaFisica("0001", "12345-6", "Joao da Silva",
                "12345678901", "joao.silva@email.com", LocalDate.of(1995, 5, 20));

        final ResultadoValidacao resultado = validador.validar(conta);

        assertThat(resultado).isInstanceOf(Valido.class);
    }

    @Test
    @DisplayName("Caso 2 - PF menor de idade -> 1 violacao em dataNascimento")
    void caso2_pessoaFisicaMenorDeIdade() {

        final ContaBancaria conta = new ContaPessoaFisica("0001", "22222-2", "Maria Souza",
                "98765432100", "maria@email.com", LocalDate.now().minusYears(15));

        final List<Violacao> violacoes = violacoesDe(validador.validar(conta));

        assertThat(violacoes)
                .extracting(Violacao::campo, Violacao::mensagem)
                .containsExactly(tuple("dataNascimento", "deve ser maior que 18 anos"));
    }

    @Test
    @DisplayName("Caso 3 - PF com campos invalidos -> violacoes em agencia, numero e cpf")
    void caso3_pessoaFisicaComCamposInvalidos() {

        final ContaBancaria conta = new ContaPessoaFisica("1", null, "Ana", "123",
                "email-invalido", LocalDate.of(2000, 1, 1));

        final List<Violacao> violacoes = violacoesDe(validador.validar(conta));

        assertThat(violacoes)
                .extracting(Violacao::campo)
                .containsExactlyInAnyOrder("agencia", "numero", "cpf");
    }

    @Test
    @DisplayName("Caso 4 - PJ valida -> Valido (nenhuma violacao)")
    void caso4_pessoaJuridicaValida() {

        final ContaBancaria conta = new ContaPessoaJuridica("0500", "99887-7",
                "Acme Tecnologia LTDA", "12345678000199", new BigDecimal("50000"));

        final ResultadoValidacao resultado = validador.validar(conta);

        assertThat(resultado).isInstanceOf(Valido.class);
    }

    @Test
    @DisplayName("Caso 5 - PJ com capital insuficiente -> 1 violacao em capitalSocial")
    void caso5_pessoaJuridicaCapitalInsuficiente() {

        final ContaBancaria conta = new ContaPessoaJuridica("0500", "55555-5",
                "Fundo de Quintal ME", "98765432000111", new BigDecimal("2500"));

        final List<Violacao> violacoes = violacoesDe(validador.validar(conta));

        assertThat(violacoes)
                .extracting(Violacao::campo, Violacao::mensagem)
                .containsExactly(tuple("capitalSocial", "deve ser maior que R$ 10.000,00"));
    }

    @Test
    @DisplayName("Caso 6 - objeto nulo enviado para validacao -> erro")
    void caso6_objetoNulo() {

        final List<Violacao> violacoes = violacoesDe(validador.validar(null));

        assertThat(violacoes)
                .extracting(Violacao::campo, Violacao::mensagem)
                .containsExactly(tuple("(objeto)", "o objeto a validar e nulo"));
    }

    @Test
    @DisplayName("Caso 7 - PF com data de nascimento nula -> nao deve quebrar a regra de negocio")
    void caso7_pessoaFisicaComDataNascimentoNula() {

        final ContaBancaria conta = new ContaPessoaFisica("0001", "22222-2", "Maria Souza",
                "98765432100", "maria@email.com", null);

        final List<Violacao> violacoes = violacoesDe(validador.validar(conta));

        assertThat(violacoes)
                .extracting(Violacao::campo, Violacao::mensagem)
                .containsExactly(tuple("dataNascimento", "a data de nascimento e obrigatoria"));
    }

    @Test
    @DisplayName("Caso 8 - PJ com capital social nulo -> nao deve quebrar a regra de negocio")
    void caso8_pessoaJuridicaComCapitalSocialNulo() {

        final ContaBancaria conta = new ContaPessoaJuridica("0500", "55555-5",
                "Fundo de Quintal ME", "98765432000111", null);

        final List<Violacao> violacoes = violacoesDe(validador.validar(conta));

        assertThat(violacoes)
                .extracting(Violacao::campo, Violacao::mensagem)
                .containsExactly(tuple("capitalSocial", "o capital social e obrigatorio"));
    }

    private List<Violacao> violacoesDe(ResultadoValidacao resultado) {
        assertThat(resultado).isInstanceOf(Invalido.class);
        return ((Invalido) resultado).violacoes();
    }
}
