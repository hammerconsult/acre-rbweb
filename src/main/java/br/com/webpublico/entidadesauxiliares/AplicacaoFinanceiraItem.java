package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 25/06/14
 * Time: 08:23
 * To change this template use File | Settings | File Templates.
 */
public class AplicacaoFinanceiraItem {

    private String descricao;
    private BigDecimal aplicacao;
    private BigDecimal resgate;
    private BigDecimal rendimento;
    private BigDecimal saldo;
    private String codigoUnidade;
    private String unidade;
    private String orgao;
    private String unidadeGestora;
    private BigDecimal count;

    public AplicacaoFinanceiraItem() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getAplicacao() {
        return aplicacao;
    }

    public void setAplicacao(BigDecimal aplicacao) {
        this.aplicacao = aplicacao;
    }

    public BigDecimal getResgate() {
        return resgate;
    }

    public void setResgate(BigDecimal resgate) {
        this.resgate = resgate;
    }

    public BigDecimal getRendimento() {
        return rendimento;
    }

    public void setRendimento(BigDecimal rendimento) {
        this.rendimento = rendimento;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
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

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }
}
