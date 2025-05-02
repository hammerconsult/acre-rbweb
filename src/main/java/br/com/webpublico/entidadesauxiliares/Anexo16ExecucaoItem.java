package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 11/11/2014.
 */
public class Anexo16ExecucaoItem {
    private String descricao;
    private BigDecimal saldoAnterior;
    private BigDecimal inscricao;
    private BigDecimal incorporacao;
    private BigDecimal amortizacao;
    private BigDecimal desincorporacao;
    private BigDecimal saldoSeguinte;

    public Anexo16ExecucaoItem() {
        saldoAnterior = BigDecimal.ZERO;
        inscricao = BigDecimal.ZERO;
        incorporacao = BigDecimal.ZERO;
        amortizacao = BigDecimal.ZERO;
        desincorporacao = BigDecimal.ZERO;
        saldoSeguinte = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getInscricao() {
        return inscricao;
    }

    public void setInscricao(BigDecimal inscricao) {
        this.inscricao = inscricao;
    }

    public BigDecimal getIncorporacao() {
        return incorporacao;
    }

    public void setIncorporacao(BigDecimal incorporacao) {
        this.incorporacao = incorporacao;
    }

    public BigDecimal getAmortizacao() {
        return amortizacao;
    }

    public void setAmortizacao(BigDecimal amortizacao) {
        this.amortizacao = amortizacao;
    }

    public BigDecimal getDesincorporacao() {
        return desincorporacao;
    }

    public void setDesincorporacao(BigDecimal desincorporacao) {
        this.desincorporacao = desincorporacao;
    }

    public BigDecimal getSaldoSeguinte() {
        return saldoSeguinte;
    }

    public void setSaldoSeguinte(BigDecimal saldoSeguinte) {
        this.saldoSeguinte = saldoSeguinte;
    }
}
