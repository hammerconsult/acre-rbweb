/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author major
 */
public class RelatorioAnexoDozeReceita {

    private BigDecimal previsao;
    private BigDecimal execucao;
    private BigDecimal diferenca;
    private String descricao;
    private Integer nivel;

    public RelatorioAnexoDozeReceita() {
    }


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public BigDecimal getExecucao() {
        return execucao;
    }

    public void setExecucao(BigDecimal execucao) {
        this.execucao = execucao;
        this.diferenca = this.execucao.subtract(this.previsao);
    }

    public BigDecimal getPrevisao() {
        return previsao;
    }

    public void setPrevisao(BigDecimal previsao) {
        this.previsao = previsao;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
