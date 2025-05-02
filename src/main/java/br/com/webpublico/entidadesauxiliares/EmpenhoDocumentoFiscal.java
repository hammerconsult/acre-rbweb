/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Empenho;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class EmpenhoDocumentoFiscal {

    private Long id;
    private String numero;
    private String categoria;
    private String tipo;
    private BigDecimal valor;
    private Empenho empenho;
    private String desdobramentoEmpenho;
    private List<EmpenhoDocumentoFiscalItem> itens;

    public EmpenhoDocumentoFiscal() {
        valor = BigDecimal.ZERO;
    }

    public EmpenhoDocumentoFiscal(Empenho empenho) {
        this.id = empenho.getId();
        this.numero = empenho.getNumero() + " / " + empenho.getExercicio().getAno();
        this.categoria = empenho.getCategoriaOrcamentaria().getDescricao();
        this.tipo = empenho.getTipoEmpenho().getDescricao();
        this.valor = empenho.getValor();
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public String getDesdobramentoEmpenho() {
        return desdobramentoEmpenho;
    }

    public void setDesdobramentoEmpenho(String desdobramentoEmpenho) {
        this.desdobramentoEmpenho = desdobramentoEmpenho;
    }

    public List<EmpenhoDocumentoFiscalItem> getItens() {
        return itens;
    }

    public void setItens(List<EmpenhoDocumentoFiscalItem> itens) {
        this.itens = itens;
    }

}
