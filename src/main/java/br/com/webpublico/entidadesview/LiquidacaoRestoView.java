/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesview;

import java.util.Date;

/**
 * @author Jaime
 */
public class LiquidacaoRestoView {

    private Long id;
    private Date data;
    private String numero;
    private String empenho;
    private String valor;
    private String saldo;

    public LiquidacaoRestoView(Long id, Date data, String numero, String valor, String saldo, String empenho) {
        this.id = id;
        this.data = data;
        this.numero = numero;
        this.empenho = empenho;
        this.valor = valor;
        this.saldo = saldo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getEmpenho() {
        return empenho;
    }

    public void setEmpenho(String empenho) {
        this.empenho = empenho;
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

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
