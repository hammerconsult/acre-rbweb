package br.com.webpublico.entidadesauxiliares.administrativo.relatorio.patrimonio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wellington on 10/08/17.
 */
public class RelatorioInventarioBem implements Serializable {

    private Long idBem;
    private String registroPatrimonial;
    private Date dataAquisicao;
    private BigDecimal valorOriginal;
    private BigDecimal valorAmortizacao;
    private BigDecimal valorDepreciacao;
    private BigDecimal valorExaustao;
    private BigDecimal valorPerdas;
    private String descricaoBem;
    private String marcaBem;
    private String modeloBem;
    private String unidadeAdministrativa;
    private String unidadeOrcamentaria;
    private String codigoGrupoBem;
    private String grupoBem;
    private String grupoObjetoCompra;
    private String estadoBem;
    private String situacaoConservacaoBem;
    private String tipoAquisicaoBem;
    private String localizacao;
    private String notasFiscais;
    private String empenhos;
    private String responsavel;
    private String registroAnterior;

    public Long getIdBem() {
        return idBem;
    }

    public void setIdBem(Long idBem) {
        this.idBem = idBem;
    }

    public String getRegistroPatrimonial() {
        return registroPatrimonial;
    }

    public void setRegistroPatrimonial(String registroPatrimonial) {
        this.registroPatrimonial = registroPatrimonial;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorAmortizacao() {
        return valorAmortizacao;
    }

    public void setValorAmortizacao(BigDecimal valorAmortizacao) {
        this.valorAmortizacao = valorAmortizacao;
    }

    public BigDecimal getValorDepreciacao() {
        return valorDepreciacao;
    }

    public void setValorDepreciacao(BigDecimal valorDepreciacao) {
        this.valorDepreciacao = valorDepreciacao;
    }

    public BigDecimal getValorExaustao() {
        return valorExaustao;
    }

    public void setValorExaustao(BigDecimal valorExaustao) {
        this.valorExaustao = valorExaustao;
    }

    public BigDecimal getValorPerdas() {
        return valorPerdas;
    }

    public void setValorPerdas(BigDecimal valorPerdas) {
        this.valorPerdas = valorPerdas;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getMarcaBem() {
        return marcaBem;
    }

    public void setMarcaBem(String marcaBem) {
        this.marcaBem = marcaBem;
    }

    public String getModeloBem() {
        return modeloBem;
    }

    public void setModeloBem(String modeloBem) {
        this.modeloBem = modeloBem;
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

    public String getCodigoGrupoBem() {
        return codigoGrupoBem;
    }

    public void setCodigoGrupoBem(String codigoGrupoBem) {
        this.codigoGrupoBem = codigoGrupoBem;
    }

    public String getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(String grupoBem) {
        this.grupoBem = grupoBem;
    }

    public String getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(String grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public String getEstadoBem() {
        return estadoBem;
    }

    public void setEstadoBem(String estadoBem) {
        this.estadoBem = estadoBem;
    }

    public String getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(String situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    public String getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(String tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getNotasFiscais() {
        return notasFiscais;
    }

    public void setNotasFiscais(String notasFiscais) {
        this.notasFiscais = notasFiscais;
    }

    public String getEmpenhos() {
        return empenhos;
    }

    public void setEmpenhos(String empenhos) {
        this.empenhos = empenhos;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getRegistroAnterior() {
        return registroAnterior;
    }

    public void setRegistroAnterior(String registroAnterior) {
        this.registroAnterior = registroAnterior;
    }
}
