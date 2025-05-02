/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author juggernaut
 */
public enum TipoLiberacaoFinanceira implements EnumComDescricao {

    COTA("Cota"),
    REPASSE("Repasse"),
    SUB_REPASSE("Sub-Repasse"),
    REPASSE_PARA_COBERTURA_DE_INSUFICIENCIA_FINANCEIRA("Repasse para cobertura de insuficiência financeira"),
    OUTROS_APORTES_PLANO_FINANCEIRO("Outros aportes plano financeiro"),
    REPASSE_PARA_COBERTURA_DE_DEFICIT_FINANCEIRO("Repasse para cobertura de déficit financeiro"),
    OUTROS_APORTES_PLANO_PREVIDENCIÁRIO("Outros aportes plano previdenciário"),
    TRANSFERENCIAS_DIVERSAS("Transferências diversas");

    private String descricao;

    TipoLiberacaoFinanceira(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
