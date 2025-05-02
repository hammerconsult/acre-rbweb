/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.anotacoes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Fabio
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AtributoIntegracaoSit {

    public String nome();

    public String atributoGenerico() default "";

    public ClasseAtributoGenerico classe() default ClasseAtributoGenerico.NENHUM;

    public String servicoUrbano() default "";

    public enum ClasseAtributoGenerico {
        CADASTRO,
        LOTE,
        CONSTRUCAO,
        NENHUM
    }



}
