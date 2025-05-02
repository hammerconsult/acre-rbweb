/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesview;

import java.util.Date;

/**
 * @author major
 */
public class LiquidacaoEstornoView {

    private Long id;
    private Date data;
    private String numero;
    private String valor;
    private String liquidacao;

    public LiquidacaoEstornoView(Long id, Date data, String numero, String valor, String liquidacao) {
        this.id = id;
        this.data = data;
        this.numero = numero;
        this.valor = valor;
        this.liquidacao = liquidacao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(String liquidacao) {
        this.liquidacao = liquidacao;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}
