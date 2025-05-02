package br.com.webpublico.util.anotacoes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ColunaAuditoriaRH {
    boolean monetario() default false;
    int posicao() default 1;
}
