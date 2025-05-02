/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author juggernaut
 */
public enum TipoRelatorioItemDemonstrativo {

    RREO("RREO"),
    RGF("RGF"),
    LEI_4320("Lei 4320"),
    LDO("LDO"),
    PPA("PPA"),
    OUTROS("Outros");

    private String descricao;

    private TipoRelatorioItemDemonstrativo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
