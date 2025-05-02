package br.com.webpublico.util.anotacoes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Wellington on 18/05/2016.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Positivo {
    boolean permiteZero() default true;

}
