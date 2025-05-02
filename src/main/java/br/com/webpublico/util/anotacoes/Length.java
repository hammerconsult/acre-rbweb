package br.com.webpublico.util.anotacoes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Criado por Mateus
 * Data: 16/05/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Length {
    int minimo() default 0;
    int maximo();
}
