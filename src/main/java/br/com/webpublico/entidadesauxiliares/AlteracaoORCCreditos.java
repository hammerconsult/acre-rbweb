/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author reidocrime
 */
public class AlteracaoORCCreditos {
    private String descricaoLinha;
    private BigDecimal suplementar;
    private BigDecimal especial;
    private BigDecimal extraordinaria;
    private BigDecimal totais;

    public AlteracaoORCCreditos() {
    }

    public AlteracaoORCCreditos(String descricaoLinha, BigDecimal suplementar, BigDecimal especial, BigDecimal extraordinaria, BigDecimal totais) {
        this.descricaoLinha = descricaoLinha;
        this.suplementar = suplementar;
        this.especial = especial;
        this.extraordinaria = extraordinaria;
        this.totais = totais;
    }

    public String getDescricaoLinha() {
        return descricaoLinha;
    }

    public void setDescricaoLinha(String descricaoLinha) {
        this.descricaoLinha = descricaoLinha;
    }


    public BigDecimal getEspecial() {
        return especial;
    }

    public void setEspecial(BigDecimal especial) {
        this.especial = especial;
    }

    public BigDecimal getExtraordinaria() {
        return extraordinaria;
    }

    public void setExtraordinaria(BigDecimal extraordinaria) {
        this.extraordinaria = extraordinaria;
    }

    public BigDecimal getSuplementar() {
        return suplementar;
    }

    public void setSuplementar(BigDecimal suplementar) {
        this.suplementar = suplementar;
    }

    public BigDecimal getTotais() {
        return totais;
    }

    public void setTotais(BigDecimal totais) {
        this.totais = totais;
    }
}
