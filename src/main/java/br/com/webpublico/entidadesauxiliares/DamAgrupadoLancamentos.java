package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DamAgrupadoLancamentos implements Serializable {
    private List<DamLancamentos> lancamentos;

    private String inscricaoCadastral;
    private String enderecoCadastro;
    private String divida;
    private String referencia;
    private String parcela;

    private Integer sd;
    private Date vencimento;

    private BigDecimal valorImposto;
    private BigDecimal valorTaxa;
    private BigDecimal valorJuros;
    private BigDecimal valorMulta;
    private BigDecimal valorCorrecao;
    private BigDecimal valorHonorarios;
    private BigDecimal valorDesconto;

    public DamAgrupadoLancamentos() {
        this.lancamentos = Lists.newArrayList();
        this.valorImposto = BigDecimal.ZERO;
        this.valorTaxa = BigDecimal.ZERO;
        this.valorJuros = BigDecimal.ZERO;
        this.valorMulta = BigDecimal.ZERO;
        this.valorCorrecao = BigDecimal.ZERO;
        this.valorHonorarios = BigDecimal.ZERO;
        this.valorDesconto = BigDecimal.ZERO;
    }

    public List<DamLancamentos> getLancamentos() {
        return lancamentos;
    }

    public void setLancamentos(List<DamLancamentos> lancamentos) {
        this.lancamentos = lancamentos;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getEnderecoCadastro() {
        return enderecoCadastro;
    }

    public void setEnderecoCadastro(String enderecoCadastro) {
        this.enderecoCadastro = enderecoCadastro;
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getParcela() {
        return parcela;
    }

    public void setParcela(String parcela) {
        this.parcela = parcela;
    }

    public Integer getSd() {
        return sd;
    }

    public void setSd(Integer sd) {
        this.sd = sd;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorCorrecao() {
        return valorCorrecao;
    }

    public void setValorCorrecao(BigDecimal valorCorrecao) {
        this.valorCorrecao = valorCorrecao;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios;
    }

    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }
}
