package br.edu.utfpr.validacao;

import br.edu.utfpr.anotacoes.NaoNulo;
import br.edu.utfpr.anotacoes.Positivo;
import br.edu.utfpr.anotacoes.Tamanho;
import br.edu.utfpr.dominio.ContaBancaria;
import br.edu.utfpr.dominio.ContaPessoaFisica;
import br.edu.utfpr.dominio.ContaPessoaJuridica;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.cert.CertPathValidatorResult;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Validador {

    private static final int IDADE_MINIMA = 18;
    private static final BigDecimal CAPITAL_SOCIAL_MINIMO = new BigDecimal("10000");

    public ResultadoValidacao validar(ContaBancaria conta) {

        final List<Violacao> violacoes = new ArrayList<>();
        // TODO ver requisito 1, implementar aqui
        violacoes.addAll(validarCampos(conta));
        violacoes.addAll(regrasDeNegocio(conta));

        return ResultadoValidacao.de(violacoes);
    }

    public List<Violacao> validarCampos(Object objeto) {

        final List<Violacao> violacoes = new ArrayList<>();

        // TODO ver requisito 2, implementar aqui
        final Class<?> clazz = objeto.getClass();

        for(Field field : clazz.getDeclaredFields()){
            field.setAccessible(true);
            try{
                Object valor = field.get(objeto);
                if (field.getAnnotation(NaoNulo.class) != null) {
                    verificarNaoNulo(field, valor, violacoes);
                }
                if (field.getAnnotation(Tamanho.class) != null) {
                    verificarTamanho(field, valor, violacoes);
                }
                if (field.getAnnotation(Positivo.class) != null) {
                    verificarPositivo(field, valor, violacoes);
                }
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return violacoes;
    }

    private void verificarNaoNulo(Field campo, Object valor, List<Violacao> acc) {
        // TODO implementar regra de negocio de nao nulo
        if (valor != null){
            return;
        }
        NaoNulo annotation = campo.getAnnotation(NaoNulo.class);
        acc.add(new Violacao(campo.getName(), annotation.mensagem()));
    }

    private void verificarTamanho(Field campo, Object valor, List<Violacao> acc) {
        // TODO implementar regra de negocio de tamanho
        if(valor == null){
            return;
        }

        Tamanho annotation = campo.getAnnotation(Tamanho.class);
        String texto = (String) valor;
        if (texto.length() > annotation.max() || texto.length() < annotation.min()){
            acc.add(new Violacao(campo.getName(), annotation.mensagem()));
        }
    }

    private void verificarPositivo(Field campo, Object valor, List<Violacao> acc) {
        // TODO implementar regra de negocio de positivo
        if (valor == null) {
            return;
        }
        Positivo annotation = campo.getAnnotation(Positivo.class);
        boolean positivo = switch (valor){
            case BigDecimal bd -> bd.compareTo(BigDecimal.ZERO) > 0;
            case Number n -> n.doubleValue() > 0;
            default -> throw new IllegalArgumentException("Esse tipo nao é suportado");
        };

        if(!positivo){
            acc.add(new Violacao(campo.getName(), annotation.mensagem()));
        }
    }

    private List<Violacao> regrasDeNegocio(ContaBancaria conta) {
        // TODO implementar as regras de negocio por tipo
        return switch (conta){
            case ContaPessoaFisica pessoaFisica -> validarMaioridade(pessoaFisica);
            case ContaPessoaJuridica pessoaJuridica -> validarCapitalSocial(pessoaJuridica.capitalSocial());
        };
    }

    private List<Violacao> validarMaioridade(ContaPessoaFisica pf) {
        // TODO implementar regra de negocio de maioridade
        if (pf.dataNascimento() == null) {
            return List.of();
        }

        int idade = Period.between(
                pf.dataNascimento(),
                LocalDate.now()
        ).getYears();

        if(idade < IDADE_MINIMA) {
            return List.of(new Violacao(
                    Violacao.DATA_NASCIMENTO_CAMPO,
                    Violacao.DATA_NASCIMENTO_MENSAGEM
            ));
        }

        return List.of();
    }

    private List<Violacao> validarCapitalSocial(BigDecimal capital) {
        // TODO implementar regra de negocio de capital social
        if(capital.compareTo(CAPITAL_SOCIAL_MINIMO) < 0){
            return List.of(new Violacao(
                    Violacao.CAPITAL_SOCIAL_CAMPO,
                    Violacao.CAPITAL_SOCIAL_MENSAGEM
            ));
        }

        return List.of();
    }
}
