package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by mga on 22/06/2017.
 */
public class LevantamentoPatrimonialPorExercicio {

    private String exercicio;
    private String codigo;
    private String descricao;
    private BigDecimal quantidadeAdministrativo;
    private BigDecimal quantidadeLevantamento;
    private BigDecimal quantidadeAtual;
    private BigDecimal valorAdministrativo;
    private BigDecimal valorLevantamento;
    private BigDecimal valorAtual;
    private BigDecimal saldo;

    public LevantamentoPatrimonialPorExercicio() {
        quantidadeAdministrativo = BigDecimal.ZERO;
        quantidadeLevantamento = BigDecimal.ZERO;
        quantidadeAtual = BigDecimal.ZERO;
        valorAdministrativo = BigDecimal.ZERO;
        valorLevantamento = BigDecimal.ZERO;
        valorAtual = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getQuantidadeAdministrativo() {
        return quantidadeAdministrativo;
    }

    public void setQuantidadeAdministrativo(BigDecimal quantidadeAdministrativo) {
        this.quantidadeAdministrativo = quantidadeAdministrativo;
    }

    public BigDecimal getQuantidadeLevantamento() {
        return quantidadeLevantamento;
    }

    public void setQuantidadeLevantamento(BigDecimal quantidadeLevantamento) {
        this.quantidadeLevantamento = quantidadeLevantamento;
    }

    public BigDecimal getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(BigDecimal quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public BigDecimal getValorAdministrativo() {
        return valorAdministrativo;
    }

    public void setValorAdministrativo(BigDecimal valorAdministrativo) {
        this.valorAdministrativo = valorAdministrativo;
    }

    public BigDecimal getValorLevantamento() {
        return valorLevantamento;
    }

    public void setValorLevantamento(BigDecimal valorLevantamento) {
        this.valorLevantamento = valorLevantamento;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }
}
