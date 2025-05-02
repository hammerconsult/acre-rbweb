package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by HardRock on 21/02/2017.
 */
public class RelatorioLiberacaoFinanceira {


    private Date dataSolicitacao;
    private String numeroSolicitacao;
    private Date dataLiberacao;
    private String numeroLiberacao;
    private String orgaoSolicitante;
    private String unidadeSolicitante;
    private String unidadeConcedente;
    private String orgaoConcedente;
    private String historico;
    private BigDecimal valorSolicitado;
    private BigDecimal valorLiberado;

    public RelatorioLiberacaoFinanceira() {
        valorSolicitado = BigDecimal.ZERO;
        valorLiberado = BigDecimal.ZERO;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getNumeroSolicitacao() {
        return numeroSolicitacao;
    }

    public void setNumeroSolicitacao(String numeroSolicitacao) {
        this.numeroSolicitacao = numeroSolicitacao;
    }

    public Date getDataLiberacao() {
        return dataLiberacao;
    }

    public void setDataLiberacao(Date dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }

    public String getNumeroLiberacao() {
        return numeroLiberacao;
    }

    public void setNumeroLiberacao(String numeroLiberacao) {
        this.numeroLiberacao = numeroLiberacao;
    }

    public String getOrgaoSolicitante() {
        return orgaoSolicitante;
    }

    public void setOrgaoSolicitante(String orgaoSolicitante) {
        this.orgaoSolicitante = orgaoSolicitante;
    }

    public String getUnidadeSolicitante() {
        return unidadeSolicitante;
    }

    public void setUnidadeSolicitante(String unidadeSolicitante) {
        this.unidadeSolicitante = unidadeSolicitante;
    }

    public String getUnidadeConcedente() {
        return unidadeConcedente;
    }

    public void setUnidadeConcedente(String unidadeConcedente) {
        this.unidadeConcedente = unidadeConcedente;
    }

    public String getOrgaoConcedente() {
        return orgaoConcedente;
    }

    public void setOrgaoConcedente(String orgaoConcedente) {
        this.orgaoConcedente = orgaoConcedente;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValorSolicitado() {
        return valorSolicitado;
    }

    public void setValorSolicitado(BigDecimal valorSolicitado) {
        this.valorSolicitado = valorSolicitado;
    }

    public BigDecimal getValorLiberado() {
        return valorLiberado;
    }

    public void setValorLiberado(BigDecimal valorLiberado) {
        this.valorLiberado = valorLiberado;
    }
}
