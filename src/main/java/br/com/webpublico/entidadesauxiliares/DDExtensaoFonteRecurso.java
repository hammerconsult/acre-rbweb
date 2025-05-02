package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by mateus on 05/07/17.
 */
public class DDExtensaoFonteRecurso {

    private String orgao;
    private String unidade;
    private String unidadeGestora;
    private String codigoConta;
    private String descricaoConta;
    private BigDecimal dotacaoInicial;
    private BigDecimal dotacaoAtualizada;
    private BigDecimal despesaEmpenhada;
    private BigDecimal despesaLiquidada;
    private BigDecimal despesaPaga;
    private BigDecimal despesaLiquidadaAPagar;
    private BigDecimal despesaEmpenhadaALiquidar;
    private BigDecimal saldo;

    public DDExtensaoFonteRecurso() {
        dotacaoInicial = BigDecimal.ZERO;
        dotacaoAtualizada = BigDecimal.ZERO;
        despesaEmpenhada = BigDecimal.ZERO;
        despesaLiquidada = BigDecimal.ZERO;
        despesaPaga = BigDecimal.ZERO;
        despesaLiquidadaAPagar = BigDecimal.ZERO;
        despesaEmpenhadaALiquidar = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public String getCodigoConta() {
        return codigoConta;
    }

    public void setCodigoConta(String codigoConta) {
        this.codigoConta = codigoConta;
    }

    public String getDescricaoConta() {
        return descricaoConta;
    }

    public void setDescricaoConta(String descricaoConta) {
        this.descricaoConta = descricaoConta;
    }

    public BigDecimal getDotacaoInicial() {
        return dotacaoInicial;
    }

    public void setDotacaoInicial(BigDecimal dotacaoInicial) {
        this.dotacaoInicial = dotacaoInicial;
    }

    public BigDecimal getDotacaoAtualizada() {
        return dotacaoAtualizada;
    }

    public void setDotacaoAtualizada(BigDecimal dotacaoAtualizada) {
        this.dotacaoAtualizada = dotacaoAtualizada;
    }

    public BigDecimal getDespesaEmpenhada() {
        return despesaEmpenhada;
    }

    public void setDespesaEmpenhada(BigDecimal despesaEmpenhada) {
        this.despesaEmpenhada = despesaEmpenhada;
    }

    public BigDecimal getDespesaLiquidada() {
        return despesaLiquidada;
    }

    public void setDespesaLiquidada(BigDecimal despesaLiquidada) {
        this.despesaLiquidada = despesaLiquidada;
    }

    public BigDecimal getDespesaPaga() {
        return despesaPaga;
    }

    public void setDespesaPaga(BigDecimal despesaPaga) {
        this.despesaPaga = despesaPaga;
    }

    public BigDecimal getDespesaLiquidadaAPagar() {
        return despesaLiquidada.subtract(despesaPaga);
    }

    public void setDespesaLiquidadaAPagar(BigDecimal despesaLiquidadaAPagar) {
        this.despesaLiquidadaAPagar = despesaLiquidadaAPagar;
    }

    public BigDecimal getDespesaEmpenhadaALiquidar() {
        return despesaEmpenhada.subtract(despesaLiquidada);
    }

    public void setDespesaEmpenhadaALiquidar(BigDecimal despesaEmpenhadaALiquidar) {
        this.despesaEmpenhadaALiquidar = despesaEmpenhadaALiquidar;
    }

    public BigDecimal getSaldo() {
        return dotacaoAtualizada.subtract(despesaEmpenhada);
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
