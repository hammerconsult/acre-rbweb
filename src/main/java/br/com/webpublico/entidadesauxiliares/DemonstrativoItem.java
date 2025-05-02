package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 02/04/14
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class DemonstrativoItem {
    private String campo;
    private BigDecimal saldoExercicioAnterior;
    private BigDecimal inscricao;
    private BigDecimal atualizacao;
    private BigDecimal ajusteAumentativo;
    private BigDecimal cobranca;
    private BigDecimal baixa;
    private BigDecimal ajusteDiminutivo;
    private BigDecimal saldoExercicioSeguinte;

    public DemonstrativoItem() {
        saldoExercicioAnterior = BigDecimal.ZERO;
        inscricao = BigDecimal.ZERO;
        atualizacao = BigDecimal.ZERO;
        ajusteAumentativo = BigDecimal.ZERO;
        cobranca = BigDecimal.ZERO;
        baixa = BigDecimal.ZERO;
        saldoExercicioSeguinte = BigDecimal.ZERO;
        ajusteDiminutivo = BigDecimal.ZERO;
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

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public BigDecimal getAjusteAumentativo() {
        return ajusteAumentativo;
    }

    public void setAjusteAumentativo(BigDecimal ajusteAumentativo) {
        this.ajusteAumentativo = ajusteAumentativo;
    }

    public BigDecimal getAjusteDiminutivo() {
        return ajusteDiminutivo;
    }

    public void setAjusteDiminutivo(BigDecimal ajusteDiminutivo) {
        this.ajusteDiminutivo = ajusteDiminutivo;
    }
}
