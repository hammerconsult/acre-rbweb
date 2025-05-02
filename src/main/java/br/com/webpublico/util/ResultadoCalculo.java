/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author daniel
 */
public class ResultadoCalculo implements Serializable {

    private BigDecimal valor;
    private String mensagem;
    private boolean sucesso;

    private ResultadoCalculo() {
        valor = BigDecimal.ZERO;
        mensagem = "Função executada com sucesso";
        sucesso = true;
    }

    public static ResultadoCalculo newResultadoOK(BigDecimal valor) {
        return new ResultadoCalculo(valor, "fórmula executada com sucesso", true);
    }

    public static ResultadoCalculo newResultadoFail(String mensagem) {
        return new ResultadoCalculo(BigDecimal.ZERO, mensagem, false);
    }

    private ResultadoCalculo(BigDecimal valor, String mensagem, boolean sucesso) {
        this.valor = valor;
        this.mensagem = mensagem;
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public BigDecimal getValor() {
        return valor.setScale(4, RoundingMode.HALF_EVEN);
    }

    public boolean isSucesso() {
        return sucesso;
    }
}
