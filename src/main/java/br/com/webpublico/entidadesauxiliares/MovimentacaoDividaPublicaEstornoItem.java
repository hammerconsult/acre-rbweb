package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 07/08/14
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class MovimentacaoDividaPublicaEstornoItem {
    private BigDecimal documento;
    private Date data;
    private String operacao;
    private String historico;
    private String numeroDoctCont;
    private BigDecimal saldo;
    private String orgUni;
    private BigDecimal cont;
    private String unidade;
    private String orgao;
    private String unidadeGestora;

    public MovimentacaoDividaPublicaEstornoItem() {
    }

    public BigDecimal getDocumento() {
        return documento;
    }

    public void setDocumento(BigDecimal documento) {
        this.documento = documento;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getNumeroDoctCont() {
        return numeroDoctCont;
    }

    public void setNumeroDoctCont(String numeroDoctCont) {
        this.numeroDoctCont = numeroDoctCont;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getOrgUni() {
        return orgUni;
    }

    public void setOrgUni(String orgUni) {
        this.orgUni = orgUni;
    }

    public BigDecimal getCont() {
        return cont;
    }

    public void setCont(BigDecimal cont) {
        this.cont = cont;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
