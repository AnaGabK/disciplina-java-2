package br.edu.utfpr.anotacoes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO requisito 7
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tamanho {

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String mensagem() default "tamanho fora do intervalo permitido";
}
