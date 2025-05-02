/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class LRFAnexo05ItemRelatorioReserva {
    private String descricao;
    private BigDecimal previsaoOrcamentaria;
    private Integer nivel;


    public LRFAnexo05ItemRelatorioReserva() {
    }

    public BigDecimal getPrevisaoOrcamentaria() {
        return previsaoOrcamentaria;
    }

    public void setPrevisaoOrcamentaria(BigDecimal previsaoOrcamentaria) {
        this.previsaoOrcamentaria = previsaoOrcamentaria;
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
