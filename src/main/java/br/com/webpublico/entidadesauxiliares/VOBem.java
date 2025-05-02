package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VOBem implements Serializable {

    private Long id;
    private String registroPatimonial;
    private String registroAnterior;
    private Date dataAquisicao;
    private String descricao;
    private String marca;
    private String modelo;
    private String grupoPatrimonial;
    private String grupoObjetoCompra;
    private String unidadeAdministrativa;
    private String unidadeOrcamentaria;
    private String tipoAquisicao;
    private String estadoConservacao;
    private String situacaoConservacao;
    private String tipoGrupo;
    private BigDecimal valorOriginal;
    private BigDecimal valorDepreciacao;
    private BigDecimal valorAmortizacao;
    private BigDecimal valorExaustao;
    private BigDecimal valorAjuste;
    private BigDecimal valorLiquido;

    public VOBem() {
        valorOriginal = BigDecimal.ZERO;
        valorDepreciacao = BigDecimal.ZERO;
        valorAmortizacao = BigDecimal.ZERO;
        valorExaustao = BigDecimal.ZERO;
        valorAjuste = BigDecimal.ZERO;
        valorLiquido = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistroPatimonial() {
        return registroPatimonial;
    }

    public void setRegistroPatimonial(String registroPatimonial) {
        this.registroPatimonial = registroPatimonial;
    }

    public String getRegistroAnterior() {
        return registroAnterior;
    }

    public void setRegistroAnterior(String registroAnterior) {
        this.registroAnterior = registroAnterior;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(String grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public String getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(String grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public String getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(String unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public String getEstadoConservacao() {
        return estadoConservacao;
    }

    public void setEstadoConservacao(String estadoConservacao) {
        this.estadoConservacao = estadoConservacao;
    }

    public String getSituacaoConservacao() {
        return situacaoConservacao;
    }

    public void setSituacaoConservacao(String situacaoConservacao) {
        this.situacaoConservacao = situacaoConservacao;
    }

    public String getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(String tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorDepreciacao() {
        return valorDepreciacao;
    }

    public void setValorDepreciacao(BigDecimal valorDepreciacao) {
        this.valorDepreciacao = valorDepreciacao;
    }

    public BigDecimal getValorAmortizacao() {
        return valorAmortizacao;
    }

    public void setValorAmortizacao(BigDecimal valorAmortizacao) {
        this.valorAmortizacao = valorAmortizacao;
    }

    public BigDecimal getValorExaustao() {
        return valorExaustao;
    }

    public void setValorExaustao(BigDecimal valorExaustao) {
        this.valorExaustao = valorExaustao;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getTipoAquisicao() {
        return tipoAquisicao;
    }

    public void setTipoAquisicao(String tipoAquisicao) {
        this.tipoAquisicao = tipoAquisicao;
    }
}
