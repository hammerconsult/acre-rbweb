/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.anotacoes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Renato
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Pesquisavel {

    public boolean mostrarNaOrdenacao() default true;

    public String labelBooleanVerdadeiro() default "Sim";

    public String labelBooleanFalso() default "Não";
}
