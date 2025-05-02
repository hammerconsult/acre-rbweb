/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesview;

import java.util.Date;

/**
 * @author major
 */
public class EmpenhoEstornoRestoView {

    private Long id;
    private Date data;
    private String numero;
    private String valor;
    private String empenho;

    public EmpenhoEstornoRestoView(Long id, Date data, String numero, String valor, String empenho) {
        this.id = id;
        this.data = data;
        this.numero = numero;
        this.valor = valor;
        this.empenho = empenho;
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

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}
