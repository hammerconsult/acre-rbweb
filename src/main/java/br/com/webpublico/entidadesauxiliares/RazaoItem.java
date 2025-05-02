package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 29/05/14
 * Time: 10:50
 * To change this template use File | Settings | File Templates.
 */
public class RazaoItem {

    private BigDecimal rowNum;
    private String codClp;
    private BigDecimal item;
    private String codLcp;
    private String codEvento;
    private String descEvento;
    private String itemCd;
    private String contaCd;
    private String undCd;
    private String orgCd;
    private String contraCd;
    private Date dataLac;
    private String operacao;
    private BigDecimal lanc;
    private String complementoHistorico;
    private BigDecimal valor;
    private String orgDesc;
    private String contaDesc;
    private String contaFunc;
    private String undDesc;
    private Long subordinadaId;
    private String contraDesc;
    private Date dataSaldoInicial;
    private BigDecimal credSaldoIni;
    private BigDecimal debSaldoIni;
    private BigDecimal saldoAnterior;
    private BigDecimal credSaldoFim;
    private BigDecimal debSaldoFim;
    private BigDecimal saldoAtual;
    private String unidade;
    private String orgao;
    private String unidadeGestora;

    public RazaoItem() {
    }

    public RazaoItem(BigDecimal rowNum, String codClp, BigDecimal item, String codLcp, String codEvento, String descEvento, String itemCd, String contaCd, String undCd, String orgCd, String contraCd, Date dataLac, String operacao, BigDecimal lanc, String complementoHistorico, BigDecimal valor, String orgDesc, String contaDesc, String contaFunc, String undDesc, Long subordinadaId, String contraDesc, Date dataSaldoInicial, BigDecimal credSaldoIni, BigDecimal debSaldoIni, BigDecimal saldoAnterior, BigDecimal credSaldoFim, BigDecimal debSaldoFim, BigDecimal saldoAtual, String unidade, String orgao, String unidadeGestora) {
        this.rowNum = rowNum;
        this.codClp = codClp;
        this.item = item;
        this.codLcp = codLcp;
        this.codEvento = codEvento;
        this.descEvento = descEvento;
        this.itemCd = itemCd;
        this.contaCd = contaCd;
        this.undCd = undCd;
        this.orgCd = orgCd;
        this.contraCd = contraCd;
        this.dataLac = dataLac;
        this.operacao = operacao;
        this.lanc = lanc;
        this.complementoHistorico = complementoHistorico;
        this.valor = valor;
        this.orgDesc = orgDesc;
        this.contaDesc = contaDesc;
        this.contaFunc = contaFunc;
        this.undDesc = undDesc;
        this.subordinadaId = subordinadaId;
        this.contraDesc = contraDesc;
        this.dataSaldoInicial = dataSaldoInicial;
        this.credSaldoIni = credSaldoIni;
        this.debSaldoIni = debSaldoIni;
        this.saldoAnterior = saldoAnterior;
        this.credSaldoFim = credSaldoFim;
        this.debSaldoFim = debSaldoFim;
        this.saldoAtual = saldoAtual;
        this.unidade = unidade;
        this.orgao = orgao;
        this.unidadeGestora = unidadeGestora;
    }

    public BigDecimal getRowNum() {
        return rowNum;
    }

    public void setRowNum(BigDecimal rowNum) {
        this.rowNum = rowNum;
    }

    public String getCodClp() {
        return codClp;
    }

    public void setCodClp(String codClp) {
        this.codClp = codClp;
    }

    public BigDecimal getItem() {
        return item;
    }

    public void setItem(BigDecimal item) {
        this.item = item;
    }

    public String getCodLcp() {
        return codLcp;
    }

    public void setCodLcp(String codLcp) {
        this.codLcp = codLcp;
    }

    public String getCodEvento() {
        return codEvento;
    }

    public void setCodEvento(String codEvento) {
        this.codEvento = codEvento;
    }

    public String getDescEvento() {
        return descEvento;
    }

    public void setDescEvento(String descEvento) {
        this.descEvento = descEvento;
    }

    public String getItemCd() {
        return itemCd;
    }

    public void setItemCd(String itemCd) {
        this.itemCd = itemCd;
    }

    public String getContaCd() {
        return contaCd;
    }

    public void setContaCd(String contaCd) {
        this.contaCd = contaCd;
    }

    public String getUndCd() {
        return undCd;
    }

    public void setUndCd(String undCd) {
        this.undCd = undCd;
    }

    public String getOrgCd() {
        return orgCd;
    }

    public void setOrgCd(String orgCd) {
        this.orgCd = orgCd;
    }

    public String getContraCd() {
        return contraCd;
    }

    public void setContraCd(String contraCd) {
        this.contraCd = contraCd;
    }

    public Date getDataLac() {
        return dataLac;
    }

    public void setDataLac(Date dataLac) {
        this.dataLac = dataLac;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public BigDecimal getLanc() {
        return lanc;
    }

    public void setLanc(BigDecimal lanc) {
        this.lanc = lanc;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getOrgDesc() {
        return orgDesc;
    }

    public void setOrgDesc(String orgDesc) {
        this.orgDesc = orgDesc;
    }

    public String getContaDesc() {
        return contaDesc;
    }

    public void setContaDesc(String contaDesc) {
        this.contaDesc = contaDesc;
    }

    public String getContaFunc() {
        return contaFunc;
    }

    public void setContaFunc(String contaFunc) {
        this.contaFunc = contaFunc;
    }

    public String getUndDesc() {
        return undDesc;
    }

    public void setUndDesc(String undDesc) {
        this.undDesc = undDesc;
    }

    public Long getSubordinadaId() {
        return subordinadaId;
    }

    public void setSubordinadaId(Long subordinadaId) {
        this.subordinadaId = subordinadaId;
    }

    public String getContraDesc() {
        return contraDesc;
    }

    public void setContraDesc(String contraDesc) {
        this.contraDesc = contraDesc;
    }

    public Date getDataSaldoInicial() {
        return dataSaldoInicial;
    }

    public void setDataSaldoInicial(Date dataSaldoInicial) {
        this.dataSaldoInicial = dataSaldoInicial;
    }

    public BigDecimal getCredSaldoIni() {
        return credSaldoIni;
    }

    public void setCredSaldoIni(BigDecimal credSaldoIni) {
        this.credSaldoIni = credSaldoIni;
    }

    public BigDecimal getDebSaldoIni() {
        return debSaldoIni;
    }

    public void setDebSaldoIni(BigDecimal debSaldoIni) {
        this.debSaldoIni = debSaldoIni;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getCredSaldoFim() {
        return credSaldoFim;
    }

    public void setCredSaldoFim(BigDecimal credSaldoFim) {
        this.credSaldoFim = credSaldoFim;
    }

    public BigDecimal getDebSaldoFim() {
        return debSaldoFim;
    }

    public void setDebSaldoFim(BigDecimal debSaldoFim) {
        this.debSaldoFim = debSaldoFim;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
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
