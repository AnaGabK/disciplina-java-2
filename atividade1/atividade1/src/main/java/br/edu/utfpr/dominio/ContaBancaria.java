package br.edu.utfpr.dominio;

import br.edu.utfpr.anotacoes.NaoNulo;
import br.edu.utfpr.anotacoes.Tamanho;

public sealed interface ContaBancaria permits ContaPessoaFisica, ContaPessoaJuridica{

    // TODO revisar e implementar corretamente conforme requisitos
    String agencia();

    String numero();
}
