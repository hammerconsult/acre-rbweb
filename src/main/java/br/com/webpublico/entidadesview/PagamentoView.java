/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesview;

import java.util.Date;

/**
 * @author major
 */
public class PagamentoView {

    private Long id;
    private Date dataPgto;
    private Date dataPrevisao;
    private String numero;
    private String valor;
    private String liquidacao;

    public PagamentoView(Long id, Date dataPgto, Date dataPrevisao, String numero, String valor, String liquidacao) {
        this.id = id;
        this.dataPgto = dataPgto;
        this.dataPrevisao = dataPrevisao;
        this.numero = numero;
        this.valor = valor;
        this.liquidacao = liquidacao;
    }


    public Date getDataPgto() {
        return dataPgto;
    }

    public void setDataPgto(Date dataPgto) {
        this.dataPgto = dataPgto;
    }

    public Date getDataPrevisao() {
        return dataPrevisao;
    }

    public void setDataPrevisao(Date dataPrevisao) {
        this.dataPrevisao = dataPrevisao;
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
