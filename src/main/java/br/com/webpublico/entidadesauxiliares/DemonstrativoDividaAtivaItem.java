package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 02/04/14
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class DemonstrativoDividaAtivaItem {
    private String campo;
    private BigDecimal saldoExercicioAnterior;
    private BigDecimal inscricao;
    private BigDecimal atualizacao;
    private BigDecimal cobranca;
    private BigDecimal baixa;
    private BigDecimal saldoExercicioSeguinte;
    private Long nivel;
    private String orgao;
    private String unidade;
    private String unidadeGestora;

    public DemonstrativoDividaAtivaItem() {
    }

    public DemonstrativoDividaAtivaItem(String campo, BigDecimal saldoExercicioAnterior, BigDecimal inscricao, BigDecimal atualizacao, BigDecimal cobranca, BigDecimal baixa, BigDecimal saldoExercicioSeguinte, Long nivel, String orgao, String unidade, String unidadeGestora) {
        this.campo = campo;
        this.saldoExercicioAnterior = saldoExercicioAnterior;
        this.inscricao = inscricao;
        this.atualizacao = atualizacao;
        this.cobranca = cobranca;
        this.baixa = baixa;
        this.saldoExercicioSeguinte = saldoExercicioSeguinte;
        this.nivel = nivel;
        this.orgao = orgao;
        this.unidade = unidade;
        this.unidadeGestora = unidadeGestora;
    }

    public BigDecimal getSaldoExercicioAnterior() {
        return saldoExercicioAnterior;
    }

    public void setSaldoExercicioAnterior(BigDecimal saldoExercicioAnterior) {
        this.saldoExercicioAnterior = saldoExercicioAnterior;
    }

    public BigDecimal getInscricao() {
        return inscricao;
    }

    public void setInscricao(BigDecimal inscricao) {
        this.inscricao = inscricao;
    }

    public BigDecimal getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(BigDecimal atualizacao) {
        this.atualizacao = atualizacao;
    }

    public BigDecimal getCobranca() {
        return cobranca;
    }

    public void setCobranca(BigDecimal cobranca) {
        this.cobranca = cobranca;
    }

    public BigDecimal getBaixa() {
        return baixa;
    }

    public void setBaixa(BigDecimal baixa) {
        this.baixa = baixa;
    }

    public BigDecimal getSaldoExercicioSeguinte() {
        return saldoExercicioSeguinte;
    }

    public void setSaldoExercicioSeguinte(BigDecimal saldoExercicioSeguinte) {
        this.saldoExercicioSeguinte = saldoExercicioSeguinte;
    }

    public Long getNivel() {
        return nivel;
    }

    public void setNivel(Long nivel) {
        this.nivel = nivel;
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

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }
}
