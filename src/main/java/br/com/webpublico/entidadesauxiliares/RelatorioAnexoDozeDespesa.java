/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author major
 */
public class RelatorioAnexoDozeDespesa {

    private BigDecimal fixacao;
    private BigDecimal execucao;
    private BigDecimal diferenca;
    private String descricao;
    private Integer nivel;

    public RelatorioAnexoDozeDespesa() {
    }

    public BigDecimal getFixacao() {
        return fixacao;
    }

    public void setFixacao(BigDecimal fixacao) {
        this.fixacao = fixacao;
    }

    public BigDecimal getExecucao() {
        return execucao;
    }

    public void setExecucao(BigDecimal execucao) {
        this.execucao = execucao;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
