/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author juggernaut
 */
public enum TipoTransferenciaFinanceira {
    COTA("Cota"),
    REPASSE("Repasse"),
    SUBREPASSE("Sub-Repasse"),
    REPASSE_PARA_COBERTURA_DE_INSUFICIENCIA_FINANCEIRA("Repasse para cobertura de insuficiência financeira"),
    OUTROS_APORTES_PLANO_FINANCEIRO("Outros aportes plano financeiro"),
    REPASSE_PARA_COBERTURA_DE_DEFICIT_FINANCEIRO("Repasse para cobertura de déficit financeiro"),
    OUTROS_APORTES_PLANO_PREVIDENCIARIO("Outros aportes plano previdenciário"),
    TRANSFERENCIAS_DIVERSAS("Transferências diversas");

    private TipoTransferenciaFinanceira(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
