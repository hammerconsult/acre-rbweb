/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author wiplash
 */
public class ItemAvaliacaoCumprimentoMetasFiscais {

    private String descricao;
    private BigDecimal metaPrevista;
    private BigDecimal percentualMetaPrevista;
    private BigDecimal metaRealizada;
    private BigDecimal percentualMetaRealizada;
    private BigDecimal variacaoValor;
    private BigDecimal variacaoPercentual;

    public ItemAvaliacaoCumprimentoMetasFiscais() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getMetaPrevista() {
        return metaPrevista;
    }

    public void setMetaPrevista(BigDecimal metaPrevista) {
        this.metaPrevista = metaPrevista;
    }

    public BigDecimal getPercentualMetaPrevista() {
        return percentualMetaPrevista;
    }

    public void setPercentualMetaPrevista(BigDecimal percentualMetaPrevista) {
        this.percentualMetaPrevista = percentualMetaPrevista;
    }

    public BigDecimal getMetaRealizada() {
        return metaRealizada;
    }

    public void setMetaRealizada(BigDecimal metaRealizada) {
        this.metaRealizada = metaRealizada;
    }

    public BigDecimal getPercentualMetaRealizada() {
        return percentualMetaRealizada;
    }

    public void setPercentualMetaRealizada(BigDecimal percentualMetaRealizada) {
        this.percentualMetaRealizada = percentualMetaRealizada;
    }

    public BigDecimal getVariacaoValor() {
        return variacaoValor;
    }

    public void setVariacaoValor(BigDecimal variacaoValor) {
        this.variacaoValor = variacaoValor;
    }

    public BigDecimal getVariacaoPercentual() {
        return variacaoPercentual;
    }

    public void setVariacaoPercentual(BigDecimal variacaoPercentual) {
        this.variacaoPercentual = variacaoPercentual;
    }
}
