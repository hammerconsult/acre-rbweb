package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class VOBemPortal {
    public String identificacao;
    public String descricao;
    public Date dataAquisicao;
    public String situacaoDeConservacao;
    public String estadoDoBem;
    public String tipoDeBem;
    public String tipoDeAquisicaoDoBem;
    public BigDecimal ValorDeAquisicao;
    public BigDecimal valorAjustes;
    public BigDecimal valorLiquido;
    public Long idUnidadeAdm;
    public Long idunidadeOrc;
    public String responsavel;
    public String tipoPropriedade;

    public VOBemPortal() {
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public String getSituacaoDeConservacao() {
        return situacaoDeConservacao;
    }

    public void setSituacaoDeConservacao(String situacaoDeConservacao) {
        this.situacaoDeConservacao = situacaoDeConservacao;
    }

    public String getEstadoDoBem() {
        return estadoDoBem;
    }

    public void setEstadoDoBem(String estadoDoBem) {
        this.estadoDoBem = estadoDoBem;
    }

    public String getTipoDeBem() {
        return tipoDeBem;
    }

    public void setTipoDeBem(String tipoDeBem) {
        this.tipoDeBem = tipoDeBem;
    }

    public String getTipoDeAquisicaoDoBem() {
        return tipoDeAquisicaoDoBem;
    }

    public void setTipoDeAquisicaoDoBem(String tipoDeAquisicaoDoBem) {
        this.tipoDeAquisicaoDoBem = tipoDeAquisicaoDoBem;
    }

    public BigDecimal getValorDeAquisicao() {
        return ValorDeAquisicao;
    }

    public void setValorDeAquisicao(BigDecimal valorDeAquisicao) {
        ValorDeAquisicao = valorDeAquisicao;
    }

    public BigDecimal getValorAjustes() {
        return valorAjustes;
    }

    public void setValorAjustes(BigDecimal valorAjustes) {
        this.valorAjustes = valorAjustes;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public Long getIdUnidadeAdm() {
        return idUnidadeAdm;
    }

    public void setIdUnidadeAdm(Long idUnidadeAdm) {
        this.idUnidadeAdm = idUnidadeAdm;
    }

    public Long getIdunidadeOrc() {
        return idunidadeOrc;
    }

    public void setIdunidadeOrc(Long idunidadeOrc) {
        this.idunidadeOrc = idunidadeOrc;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getTipoPropriedade() {
        return tipoPropriedade;
    }

    public void setTipoPropriedade(String tipoPropriedade) {
        this.tipoPropriedade = tipoPropriedade;
    }
}
