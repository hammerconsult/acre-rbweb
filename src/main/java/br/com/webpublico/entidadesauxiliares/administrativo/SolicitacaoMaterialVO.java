package br.com.webpublico.entidadesauxiliares.administrativo;

import br.com.webpublico.util.DataUtil;

import java.util.Date;

public class SolicitacaoMaterialVO {
    private Long id;
    private String tipoSolicitacao;
    private String leiLicitacao;
    private String subTipoObjetoCompra;
    private Date dataSolicitacao;
    private Integer numero;
    private String descricao;
    private String justificativa;
    private String solicitante;
    private String modalidadeLicitacao;
    private String unidadeAdm;
    private String statusAtual;
    private String criadoPor;

    public SolicitacaoMaterialVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoSolicitacao() {
        return tipoSolicitacao;
    }

    public void setTipoSolicitacao(String tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public String getLeiLicitacao() {
        return leiLicitacao;
    }

    public void setLeiLicitacao(String leiLicitacao) {
        this.leiLicitacao = leiLicitacao;
    }

    public String getSubTipoObjetoCompra() {
        return subTipoObjetoCompra;
    }

    public void setSubTipoObjetoCompra(String subTipoObjetoCompra) {
        this.subTipoObjetoCompra = subTipoObjetoCompra;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(String modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public String getUnidadeAdm() {
        return unidadeAdm;
    }

    public void setUnidadeAdm(String unidadeAdm) {
        this.unidadeAdm = unidadeAdm;
    }

    public String getStatusAtual() {
        return statusAtual;
    }

    public void setStatusAtual(String statusAtual) {
        this.statusAtual = statusAtual;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public String getDataSolicitacaoFormatada() {
        return DataUtil.getDataFormatada(dataSolicitacao);
    }
}
