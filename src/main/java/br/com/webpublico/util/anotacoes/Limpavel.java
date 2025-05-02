/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.anotacoes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author munif
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Limpavel {
    public static int NULO = 0;
    public static int STRING_VAZIA = 1;
    public static int VERDADEIRO = 2;
    public static int FALSO = 3;
    public static int NOVO_OBJETO = 4;
    public static int ZERO = 5;

    public int value() default NULO;

}
