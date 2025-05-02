/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class RREOAnexo14ItemReceita {
    private String descricao;
    private BigDecimal previsaoAtualizada;
    private BigDecimal receitaRealizada;
    private BigDecimal saldoRealizar;

    public RREOAnexo14ItemReceita() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrevisaoAtualizada() {
        return previsaoAtualizada;
    }

    public void setPrevisaoAtualizada(BigDecimal previsaoAtualizada) {
        this.previsaoAtualizada = previsaoAtualizada;
    }

    public BigDecimal getReceitaRealizada() {
        return receitaRealizada;
    }

    public void setReceitaRealizada(BigDecimal receitaRealizada) {
        this.receitaRealizada = receitaRealizada;
    }

    public BigDecimal getSaldoRealizar() {
        return saldoRealizar;
    }

    public void setSaldoRealizar(BigDecimal saldoRealizar) {
        this.saldoRealizar = saldoRealizar;
    }
}
