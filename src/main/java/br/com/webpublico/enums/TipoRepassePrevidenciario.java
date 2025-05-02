/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author venon
 */
public enum TipoRepassePrevidenciario {

    COBERTURA_INSUFUCIENCIA_FINANCEIRA("Repasse para cobertura de insuficiência financeira"),
    OUTROS_APORTES_PLANO_FINANCEIRO("Outros aportes plano financeiro"),
    COBERTURA_DEFICIT_FINANCEIRO("Repasse para cobertura de déficit financeiro"),
    OUTROS_APORTES_PLANO_PREVIDENCIARIO("Outros aportes plano previdenciário"),
    OUTROS_APORTES_RPPS("Outros aportes ao RPPS");
    private String descricao;

    private TipoRepassePrevidenciario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
