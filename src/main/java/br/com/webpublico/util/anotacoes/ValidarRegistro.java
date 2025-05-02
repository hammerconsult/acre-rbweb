package br.com.webpublico.util.anotacoes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Desenvolvimento on 01/09/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidarRegistro {

    int tamanhoDoCampo() default 1;

    boolean campoNumerico() default false;

    String[] valoresPossiveis() default "";
}
