package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 02/06/15
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
public class RestoAPagarPorFonteItem {

    private Date dataPagamento;
    private Date dataEmpenho;
    private Date dataLiquidacao;
    private String pessoa;
    private String classe;
    private String processados;
    private String codigoFonte;
    private String descricaoFonte;
    private String codigoContaDesp;
    private String descricaoContaDesp;
    private String unidade;
    private String orgao;
    private String unidadeGestora;
    private BigDecimal valorEmpenho;
    private BigDecimal saldoEmpenho;
    private BigDecimal valorLiquidado;
    private BigDecimal valorAnulado;
    private BigDecimal valorPago;
    private BigDecimal valorPagar;


    public RestoAPagarPorFonteItem() {
        valorEmpenho = BigDecimal.ZERO;
        saldoEmpenho = BigDecimal.ZERO;
        valorLiquidado = BigDecimal.ZERO;
        valorAnulado = BigDecimal.ZERO;
        valorPago = BigDecimal.ZERO;
        valorPagar = BigDecimal.ZERO;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Date getDataEmpenho() {
        return dataEmpenho;
    }

    public void setDataEmpenho(Date dataEmpenho) {
        this.dataEmpenho = dataEmpenho;
    }

    public Date getDataLiquidacao() {
        return dataLiquidacao;
    }

    public void setDataLiquidacao(Date dataLiquidacao) {
        this.dataLiquidacao = dataLiquidacao;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getProcessados() {
        return processados;
    }

    public void setProcessados(String processados) {
        this.processados = processados;
    }

    public String getCodigoFonte() {
        return codigoFonte;
    }

    public void setCodigoFonte(String codigoFonte) {
        this.codigoFonte = codigoFonte;
    }

    public String getDescricaoFonte() {
        return descricaoFonte;
    }

    public void setDescricaoFonte(String descricaoFonte) {
        this.descricaoFonte = descricaoFonte;
    }

    public String getCodigoContaDesp() {
        return codigoContaDesp;
    }

    public void setCodigoContaDesp(String codigoContaDesp) {
        this.codigoContaDesp = codigoContaDesp;
    }

    public String getDescricaoContaDesp() {
        return descricaoContaDesp;
    }

    public void setDescricaoContaDesp(String descricaoContaDesp) {
        this.descricaoContaDesp = descricaoContaDesp;
    }

    public BigDecimal getValorEmpenho() {
        return valorEmpenho;
    }

    public void setValorEmpenho(BigDecimal valorEmpenho) {
        this.valorEmpenho = valorEmpenho;
    }

    public BigDecimal getSaldoEmpenho() {
        return saldoEmpenho;
    }

    public void setSaldoEmpenho(BigDecimal saldoEmpenho) {
        this.saldoEmpenho = saldoEmpenho;
    }

    public BigDecimal getValorLiquidado() {
        return valorLiquidado;
    }

    public void setValorLiquidado(BigDecimal valorLiquidado) {
        this.valorLiquidado = valorLiquidado;
    }

    public BigDecimal getValorAnulado() {
        return valorAnulado;
    }

    public void setValorAnulado(BigDecimal valorAnulado) {
        this.valorAnulado = valorAnulado;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getValorPagar() {
        return valorPagar;
    }

    public void setValorPagar(BigDecimal valorPagar) {
        this.valorPagar = valorPagar;
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
