package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Criado por Mateus
 * Data: 05/05/2017.
 */
public class BensSegurados {

    private String numeroOrdenacao;
    private String registroPatrimonial;
    private String descricaoBem;
    private String estadoBem;
    private String formaAquisicao;
    private Date dataAquisicao;
    private String seguradora;
    private String apolice;
    private Date dataVencimentoApolice;
    private BigDecimal valorBem;

    public BensSegurados() {
    }

    public String getNumeroOrdenacao() {
        return numeroOrdenacao;
    }

    public void setNumeroOrdenacao(String numeroOrdenacao) {
        this.numeroOrdenacao = numeroOrdenacao;
    }

    public String getRegistroPatrimonial() {
        return registroPatrimonial;
    }

    public void setRegistroPatrimonial(String registroPatrimonial) {
        this.registroPatrimonial = registroPatrimonial;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getEstadoBem() {
        return estadoBem;
    }

    public void setEstadoBem(String estadoBem) {
        this.estadoBem = estadoBem;
    }

    public String getFormaAquisicao() {
        return formaAquisicao;
    }

    public void setFormaAquisicao(String formaAquisicao) {
        this.formaAquisicao = formaAquisicao;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public String getSeguradora() {
        return seguradora;
    }

    public void setSeguradora(String seguradora) {
        this.seguradora = seguradora;
    }

    public String getApolice() {
        return apolice;
    }

    public void setApolice(String apolice) {
        this.apolice = apolice;
    }

    public Date getDataVencimentoApolice() {
        return dataVencimentoApolice;
    }

    public void setDataVencimentoApolice(Date dataVencimentoApolice) {
        this.dataVencimentoApolice = dataVencimentoApolice;
    }

    public BigDecimal getValorBem() {
        return valorBem;
    }

    public void setValorBem(BigDecimal valorBem) {
        this.valorBem = valorBem;
    }
}