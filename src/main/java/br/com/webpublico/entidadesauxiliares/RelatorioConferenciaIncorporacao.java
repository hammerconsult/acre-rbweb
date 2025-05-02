package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 04/06/2018.
 */
public class RelatorioConferenciaIncorporacao {

    //    Solicitação
    private String codigo;
    private String situacao;
    private String responsavel;
    private Date dataLancamento;
    private String unidadeAdministrativa;
    private String unidadeOrcamentaria;
    private String tipoAquisicao;
    private Date dataAquisicao;
    private String fornecedor;
    private String notaEmpenho;
    private Date dataNotaEmpenho;
    private String notaFiscal;
    private Date dataNotaFiscal;
    private String observacao;

//    Aprovação
    private Date dataAprovacao;
    private String responsavelAprovacao;
    private String observacaoAprovacao;
    private String situacaoAprovacao;
    private String motivoRejeicao;

    //    Bens
    private String registroAnterior;
    private String identificacaoBem;
    private String descricaoBem;
    private String grupoPatrimonial;
    private String estadoConservacao;
    private BigDecimal quantidade;
    private BigDecimal valorOriginal;

    //    Origem Recurso
    private String tipoOrigemRecurso;
    private String descricaoRecurso;

    private List<RelatorioConferenciaIncorporacao> bens;
    private List<RelatorioConferenciaIncorporacao> origensRecurso;

    public RelatorioConferenciaIncorporacao() {
        bens = Lists.newArrayList();
        origensRecurso = Lists.newArrayList();
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public String getResponsavelAprovacao() {
        return responsavelAprovacao;
    }

    public void setResponsavelAprovacao(String responsavelAprovacao) {
        this.responsavelAprovacao = responsavelAprovacao;
    }

    public String getObservacaoAprovacao() {
        return observacaoAprovacao;
    }

    public void setObservacaoAprovacao(String observacaoAprovacao) {
        this.observacaoAprovacao = observacaoAprovacao;
    }

    public String getSituacaoAprovacao() {
        return situacaoAprovacao;
    }

    public void setSituacaoAprovacao(String situacaoAprovacao) {
        this.situacaoAprovacao = situacaoAprovacao;
    }

    public List<RelatorioConferenciaIncorporacao> getBens() {
        return bens;
    }

    public void setBens(List<RelatorioConferenciaIncorporacao> bens) {
        this.bens = bens;
    }

    public List<RelatorioConferenciaIncorporacao> getOrigensRecurso() {
        return origensRecurso;
    }

    public void setOrigensRecurso(List<RelatorioConferenciaIncorporacao> origensRecurso) {
        this.origensRecurso = origensRecurso;
    }

    public String getRegistroAnterior() {
        return registroAnterior;
    }

    public void setRegistroAnterior(String registroAnterior) {
        this.registroAnterior = registroAnterior;
    }

    public String getIdentificacaoBem() {
        return identificacaoBem;
    }

    public void setIdentificacaoBem(String identificacaoBem) {
        this.identificacaoBem = identificacaoBem;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(String grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public String getEstadoConservacao() {
        return estadoConservacao;
    }

    public void setEstadoConservacao(String estadoConservacao) {
        this.estadoConservacao = estadoConservacao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public String getTipoOrigemRecurso() {
        return tipoOrigemRecurso;
    }

    public void setTipoOrigemRecurso(String tipoOrigemRecurso) {
        this.tipoOrigemRecurso = tipoOrigemRecurso;
    }

    public String getDescricaoRecurso() {
        return descricaoRecurso;
    }

    public void setDescricaoRecurso(String descricaoRecurso) {
        this.descricaoRecurso = descricaoRecurso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
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

    public String getTipoAquisicao() {
        return tipoAquisicao;
    }

    public void setTipoAquisicao(String tipoAquisicao) {
        this.tipoAquisicao = tipoAquisicao;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getNotaEmpenho() {
        return notaEmpenho;
    }

    public void setNotaEmpenho(String notaEmpenho) {
        this.notaEmpenho = notaEmpenho;
    }

    public Date getDataNotaEmpenho() {
        return dataNotaEmpenho;
    }

    public void setDataNotaEmpenho(Date dataNotaEmpenho) {
        this.dataNotaEmpenho = dataNotaEmpenho;
    }

    public String getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(String notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public Date getDataNotaFiscal() {
        return dataNotaFiscal;
    }

    public void setDataNotaFiscal(Date dataNotaFiscal) {
        this.dataNotaFiscal = dataNotaFiscal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
