/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 *
 * @author reidocrime
 */
public class RelatorioAnexoUm4320Execucao {
    private BigDecimal valor;
    private String descricao;

    public RelatorioAnexoUm4320Execucao() {
        this.valor = BigDecimal.ZERO;
        this.descricao = "";
    }


    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }




}
