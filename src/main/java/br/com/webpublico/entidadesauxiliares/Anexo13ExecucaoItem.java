/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 *
 * @author wiplash
 */
public class Anexo13ExecucaoItem {
    private String descricao;
    private String descricaoOriginal;
    private BigDecimal valorColuna1;
    private BigDecimal valorColuna2;
    private Integer nivel;

    public Anexo13ExecucaoItem() {
        valorColuna1 = BigDecimal.ZERO;
        valorColuna2 = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorColuna1() {
        return valorColuna1;
    }

    public void setValorColuna1(BigDecimal valorColuna1) {
        this.valorColuna1 = valorColuna1;
    }

    public BigDecimal getValorColuna2() {
        return valorColuna2;
    }

    public void setValorColuna2(BigDecimal valorColuna2) {
        this.valorColuna2 = valorColuna2;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Integer getNivel() {
        return nivel;
    }

    public String getDescricaoOriginal() {
        return descricaoOriginal;
    }

    public void setDescricaoOriginal(String descricaoOriginal) {
        this.descricaoOriginal = descricaoOriginal;
    }
}
