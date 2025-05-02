package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 25/10/13
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class RREOAnexo08Item12 {
    private String descricao;
    private BigDecimal saldoAteBimestre;
    private BigDecimal canceladosEm;
    private Integer nivel;

    public RREOAnexo08Item12() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getSaldoAteBimestre() {
        return saldoAteBimestre;
    }

    public void setSaldoAteBimestre(BigDecimal saldoAteBimestre) {
        this.saldoAteBimestre = saldoAteBimestre;
    }

    public BigDecimal getCanceladosEm() {
        return canceladosEm;
    }

    public void setCanceladosEm(BigDecimal canceladosEm) {
        this.canceladosEm = canceladosEm;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
