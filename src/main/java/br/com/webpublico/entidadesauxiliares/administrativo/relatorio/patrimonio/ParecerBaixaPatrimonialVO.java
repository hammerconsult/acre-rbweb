package br.com.webpublico.entidadesauxiliares.administrativo.relatorio.patrimonio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by zaca on 30/03/17.
 */
public class ParecerBaixaPatrimonialVO implements Serializable {
    private Date dataParecer;
    private Date dataAquisicaoBem;
    private Date dataSolicitacao;
    private Number codigoParecer;
    private Number codigoSolicitacao;
    private String justificativaParecer;
    private String situacaoParecer;
    private String tipoBaixaSolicitacao;
    private String tipoBemSolicitacao;
    private String solicitante;
    private String parecerista;
    private String descricaoBem;
    private String codigoPatrimonio;
    private String estadoConservacaoBem;
    private String codigoGrupoBem;
    private String descricaoGrupoBem;
    private String administrativa;
    private String orcamentaria;
    private BigDecimal valorOriginal;
    private BigDecimal valorAcumuladoAjuste;
    private BigDecimal valorAcumuladoDepreciacao;
    private BigDecimal valorAcumuladoAmorizacao;
    private BigDecimal valorAcumuladoExaustao;
    private BigDecimal liquido;
    private BigDecimal ajustes;

    public ParecerBaixaPatrimonialVO(Date dataParecer,Date dataSolicitacao, Number codigoParecer,Number codigoSolicitacao,
                                     String justificativaParecer, String situacaoParecer,String tipoBaixaSolicitacao,
                                     String tipoBemSolicitacao, String solicitante, String parecerista,
                                     String descricaoBem, String codigoPatrimonio, Date dataAquisicaoBem, String estadoConservacaoBem,
                                     String codigoGrupoBem, String descricaoGrupoBem, String administrativa, String orcamentaria,
                                     BigDecimal valorOriginal, BigDecimal valorAcumuladoAjuste, BigDecimal valorAcumuladoDepreciacao,
                                     BigDecimal valorAcumuladoAmorizacao, BigDecimal valorAcumuladoExaustao, BigDecimal liquido,
                                     BigDecimal ajustes) {
        this.dataParecer = dataParecer;
        this.codigoParecer = codigoParecer;
        this.codigoSolicitacao = codigoSolicitacao;
        this.dataSolicitacao = dataSolicitacao;
        this.justificativaParecer = justificativaParecer;
        this.situacaoParecer = situacaoParecer;
        this.tipoBaixaSolicitacao = tipoBaixaSolicitacao;
        this.tipoBemSolicitacao = tipoBemSolicitacao;
        this.solicitante = solicitante;
        this.parecerista = parecerista;
        this.descricaoBem = descricaoBem;
        this.codigoPatrimonio = codigoPatrimonio;
        this.dataAquisicaoBem = dataAquisicaoBem;
        this.estadoConservacaoBem = estadoConservacaoBem;
        this.codigoGrupoBem = codigoGrupoBem;
        this.descricaoGrupoBem = descricaoGrupoBem;
        this.administrativa = administrativa;
        this.orcamentaria = orcamentaria;
        this.valorOriginal = valorOriginal;
        this.valorAcumuladoAjuste = valorAcumuladoAjuste;
        this.valorAcumuladoDepreciacao = valorAcumuladoDepreciacao;
        this.valorAcumuladoAmorizacao = valorAcumuladoAmorizacao;
        this.valorAcumuladoExaustao = valorAcumuladoExaustao;
        this.liquido = liquido;
        this.ajustes = ajustes;
    }

    public ParecerBaixaPatrimonialVO() {
        this.valorOriginal = BigDecimal.ZERO;
        this.valorAcumuladoAjuste = BigDecimal.ZERO;
        this.valorAcumuladoAmorizacao = BigDecimal.ZERO;
        this.valorAcumuladoExaustao = BigDecimal.ZERO;
        this.valorAcumuladoDepreciacao = BigDecimal.ZERO;
        this.liquido = BigDecimal.ZERO;
        this.ajustes = BigDecimal.ZERO;
    }

    public Date getDataParecer() {
        return dataParecer;
    }

    public void setDataParecer(Date dataParecer) {
        this.dataParecer = dataParecer;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Number getCodigoParecer() {
        return codigoParecer;
    }

    public void setCodigoParecer(Number codigoParecer) {
        this.codigoParecer = codigoParecer;
    }


    public String getJustificativaParecer() {
        return justificativaParecer;
    }

    public void setJustificativaParecer(String justificativaParecer) {
        this.justificativaParecer = justificativaParecer;
    }

    public String getSituacaoParecer() {
        return situacaoParecer;
    }

    public void setSituacaoParecer(String situacaoParecer) {
        this.situacaoParecer = situacaoParecer;
    }

    public String getTipoBaixaSolicitacao() {
        return tipoBaixaSolicitacao;
    }

    public void setTipoBaixaSolicitacao(String tipoBaixaSolicitacao) {
        this.tipoBaixaSolicitacao = tipoBaixaSolicitacao;
    }

    public String getTipoBemSolicitacao() {
        return tipoBemSolicitacao;
    }

    public void setTipoBemSolicitacao(String tipoBemSolicitacao) {
        this.tipoBemSolicitacao = tipoBemSolicitacao;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getParecerista() {
        return parecerista;
    }

    public void setParecerista(String parecerista) {
        this.parecerista = parecerista;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getCodigoPatrimonio() {
        return codigoPatrimonio;
    }

    public void setCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = codigoPatrimonio;
    }

    public Number getCodigoSolicitacao() {
        return codigoSolicitacao;
    }

    public void setCodigoSolicitacao(Number codigoSolicitacao) {
        this.codigoSolicitacao = codigoSolicitacao;
    }

    public Date getDataAquisicaoBem() {
        return dataAquisicaoBem;
    }

    public void setDataAquisicaoBem(Date dataAquisicaoBem) {
        this.dataAquisicaoBem = dataAquisicaoBem;
    }

    public String getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(String estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorAcumuladoAjuste() {
        return valorAcumuladoAjuste;
    }

    public void setValorAcumuladoAjuste(BigDecimal valorAcumuladoAjuste) {
        this.valorAcumuladoAjuste = valorAcumuladoAjuste;
    }

    public BigDecimal getValorAcumuladoDepreciacao() {
        return valorAcumuladoDepreciacao;
    }

    public void setValorAcumuladoDepreciacao(BigDecimal valorAcumuladoDepreciacao) {
        this.valorAcumuladoDepreciacao = valorAcumuladoDepreciacao;
    }

    public BigDecimal getValorAcumuladoAmorizacao() {
        return valorAcumuladoAmorizacao;
    }

    public void setValorAcumuladoAmorizacao(BigDecimal valorAcumuladoAmorizacao) {
        this.valorAcumuladoAmorizacao = valorAcumuladoAmorizacao;
    }

    public BigDecimal getValorAcumuladoExaustao() {
        return valorAcumuladoExaustao;
    }

    public void setValorAcumuladoExaustao(BigDecimal valorAcumuladoExaustao) {
        this.valorAcumuladoExaustao = valorAcumuladoExaustao;
    }

    public BigDecimal getLiquido() {
        return liquido;
    }

    public void setLiquido(BigDecimal liquido) {
        this.liquido = liquido;
    }

    public String getCodigoGrupoBem() {
        return codigoGrupoBem;
    }

    public void setCodigoGrupoBem(String codigoGrupoBem) {
        this.codigoGrupoBem = codigoGrupoBem;
    }

    public String getDescricaoGrupoBem() {
        return descricaoGrupoBem;
    }

    public void setDescricaoGrupoBem(String descricaoGrupoBem) {
        this.descricaoGrupoBem = descricaoGrupoBem;
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
}
