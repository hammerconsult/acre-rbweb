package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by alex on 06/07/17.
 */
public class RelatorioSolicitacaoEstorno {

    private BigDecimal codigo;
    private Date dataHoraCriacao;
    private String descricao;
    private String situacaoTransferenciaBem;
    private String origem;
    private String responsavelOrigem;
    private String destino;
    private String responsavelDestino;
    private String estadoConservacaoBem;
    private String codigoPatrimonio;
    private BigDecimal atual;
    private String descricaoBem;
    private Date dataOperacao;
    private BigDecimal valordoLancamento;
    private String administrativa;
    private String orcamentaria;
    private BigDecimal ajustes;
    private String grupoCodigo;
    private String grupoDescricao;

    public RelatorioSolicitacaoEstorno() {
    }

    public BigDecimal getCodigo() {
        return codigo;
    }

    public void setCodigo(BigDecimal codigo) {
        this.codigo = codigo;
    }

    public Date getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public void setDataHoraCriacao(Date dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSituacaoTransferenciaBem() {
        return situacaoTransferenciaBem;
    }

    public void setSituacaoTransferenciaBem(String situacaoTransferenciaBem) {
        this.situacaoTransferenciaBem = situacaoTransferenciaBem;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getResponsavelOrigem() {
        return responsavelOrigem;
    }

    public void setResponsavelOrigem(String responsavelOrigem) {
        this.responsavelOrigem = responsavelOrigem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getResponsavelDestino() {
        return responsavelDestino;
    }

    public void setResponsavelDestino(String responsavelDestino) {
        this.responsavelDestino = responsavelDestino;
    }

    public String getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(String estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    public String getCodigoPatrimonio() {
        return codigoPatrimonio;
    }

    public void setCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = codigoPatrimonio;
    }

    public BigDecimal getAtual() {
        return atual;
    }

    public void setAtual(BigDecimal atual) {
        this.atual = atual;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public BigDecimal getValordoLancamento() {
        return valordoLancamento;
    }

    public void setValordoLancamento(BigDecimal valordoLancamento) {
        this.valordoLancamento = valordoLancamento;
    }

    public String getAdministrativa() {
        return administrativa;
    }

    public void setAdministrativa(String administrativa) {
        this.administrativa = administrativa;
    }

    public String getOrcamentaria() {
        return orcamentaria;
    }

    public void setOrcamentaria(String orcamentaria) {
        this.orcamentaria = orcamentaria;
    }

    public BigDecimal getAjustes() {
        return ajustes;
    }

    public void setAjustes(BigDecimal ajustes) {
        this.ajustes = ajustes;
    }

    public String getGrupoCodigo() {
        return grupoCodigo;
    }

    public void setGrupoCodigo(String grupoCodigo) {
        this.grupoCodigo = grupoCodigo;
    }

    public String getGrupoDescricao() {
        return grupoDescricao;
    }

    public void setGrupoDescricao(String grupoDescricao) {
        this.grupoDescricao = grupoDescricao;
    }
}
