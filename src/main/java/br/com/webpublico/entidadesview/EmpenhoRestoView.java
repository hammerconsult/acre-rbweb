/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesview;

import java.util.Date;

/**
 * @author Jaime
 */
public class EmpenhoRestoView {

    private Long id;
    private String numero;
    private Date data;
    private String fornecedor;
    private String valor;
    private String saldo;

    public EmpenhoRestoView(Long id, Date data, String numero, String valor, String saldo, String fornecedor) {
        this.id = id;
        this.numero = numero;
        this.data = data;
        this.fornecedor = fornecedor;
        this.valor = valor;
        this.saldo = saldo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
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
