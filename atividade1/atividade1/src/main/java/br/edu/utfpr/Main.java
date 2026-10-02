package br.edu.utfpr;

import br.edu.utfpr.dominio.ContaBancaria;
import br.edu.utfpr.dominio.ContaPessoaFisica;
import br.edu.utfpr.dominio.ContaPessoaJuridica;
import br.edu.utfpr.validacao.ResultadoValidacao;
import br.edu.utfpr.validacao.ResultadoValidacao.Invalido;
import br.edu.utfpr.validacao.ResultadoValidacao.Valido;
import br.edu.utfpr.validacao.Validador;
import br.edu.utfpr.validacao.Violacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Demonstração do mini-framework de validação de contas bancárias.
 *
 * <p>Compilar e executar, a partir da raiz do projeto:</p>
 * <pre>
 *   ./mvnw -q compile exec:java
 * </pre>
 */
public class Main {

    /**
     * Aqui vocês vão encontrar os casos básicos para testes, execute esta classe e os resultados da validação devem
     * ser mostrados no console.
     * <p>
     * O exercício será considerado pronto quando todos os casos de teste da classe {@link ValidadorTest} passarem.
     * <p>
     * OBS: assinatura do metodo esta no estilo antigo para o plugin java-exec do maven conseguir achar o metodo
     */
    public static void main(String[] args) {

        final Validador validador = new Validador();

        // CASO 1 — Pessoa física totalmente válida (maior de idade).
        final ContaBancaria pfValida = new ContaPessoaFisica("0001", "12345-6", "Joao da Silva",
                "12345678901", "joao.silva@email.com", LocalDate.of(1995, 5, 20));
        imprimir("Caso 1 - PF valida", pfValida, validador.validar(pfValida));

        // CASO 2 — Pessoa física menor de idade (regra de negocio: idade < 18).
        final ContaBancaria pfMenor = new ContaPessoaFisica("0001", "22222-2", "Maria Souza",
                "98765432100", "maria@email.com", LocalDate.now().minusYears(15));
        imprimir("Caso 2 - PF menor de idade", pfMenor, validador.validar(pfMenor));

        // CASO 3 — Pessoa física com varios campos invalidos (camada de annotations).
        final ContaBancaria pfCamposRuins = new ContaPessoaFisica("1", null, "Ana", "123",
                "email-invalido", LocalDate.of(2000, 1, 1));
        imprimir("Caso 3 - PF com campos invalidos", pfCamposRuins, validador.validar(pfCamposRuins));

        // CASO 4 — Pessoa juridica valida (capital social >= 10k).
        final ContaBancaria pjValida = new ContaPessoaJuridica("0500", "99887-7",
                "Acme Tecnologia LTDA", "12345678000199", new BigDecimal("50000"));

        imprimir("Caso 4 - PJ valida", pjValida, validador.validar(pjValida));

        // CASO 5 — Pessoa juridica com capital social abaixo do minimo.
        final ContaBancaria pjPobre = new ContaPessoaJuridica("0500", "55555-5",
                "Fundo de Quintal ME", "98765432000111", new BigDecimal("2500"));

        imprimir("Caso 5 - PJ capital social insuficiente", pjPobre, validador.validar(pjPobre));
    }

    /**
     * Imprime o resultado fazendo <b>pattern matching</b> sobre o tipo sealed {@link ResultadoValidacao}.
     * O {@code switch} é exaustivo: como só existem {@link Valido} e {@link Invalido}, não precisamos de {@code default}.
     */
    private static void imprimir(String titulo, ContaBancaria conta, ResultadoValidacao resultado) {

        IO.println("=== " + titulo + " ===");
        IO.println("Objeto: " + conta);

        switch (resultado) {
            case Valido _ -> IO.println("Nenhuma violacao encontrada");
            case Invalido(var violacoes) -> {
                IO.println("Violacoes (" + violacoes.size() + "):");
                for (final Violacao violacao : violacoes) {
                    IO.println("  - " + violacao);
                }
            }
        }
        IO.println();
    }
}
