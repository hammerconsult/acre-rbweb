/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesview;

import java.util.Date;

/**
 * @author major
 */
public class PagamentoEstornoView {

    private Long id;
    private Date data;
    private String numero;
    private String valor;
    private String pagamento;

    public PagamentoEstornoView(Long id, Date data, String numero, String valor, String pagamento) {
        this.id = id;
        this.data = data;
        this.numero = numero;
        this.valor = valor;
        this.pagamento = pagamento;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
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

    public String getPagamento() {
        return pagamento;
    }

    public void setPagamento(String pagamento) {
        this.pagamento = pagamento;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
