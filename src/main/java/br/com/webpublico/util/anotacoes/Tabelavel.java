/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.anotacoes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Munif
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Tabelavel {

    ALINHAMENTO ALINHAMENTO() default ALINHAMENTO.ESQUERDA;

    TIPOCAMPO TIPOCAMPO() default TIPOCAMPO.TEXTO;

    public boolean podeImprimir() default true;

    public boolean campoSelecionado() default true;

    public boolean agrupavel() default false;

    public boolean protocolo() default false;

    int ordemApresentacao() default 1;

    public enum ALINHAMENTO {

        CENTRO("center"), DIREITA("right"), ESQUERDA("left");
        private String valor;

        public String getValor() {
            return valor;
        }

        private ALINHAMENTO(String valor) {
            this.valor = valor;
        }
    }

    public enum TIPOCAMPO {

        DATA, NUMERO, NUMERO_ORDENAVEL, TEXTO, DECIMAL_QUATRODIGITOS;
    }
}
