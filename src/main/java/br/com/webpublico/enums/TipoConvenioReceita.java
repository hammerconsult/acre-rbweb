/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author venon
 */
public enum TipoConvenioReceita implements EnumComDescricao {
    CONVENIO("1 - Convênio"),
    CONTRATO_REPASSE("2 - Contrato de Repasse"),
    PLANO_DE_ACAO("3 - Plano de Ação"),
    TERMO_COMPROMISSO("4 - Termo de Compromisso"),
    PROPOSTA_FUNDO_A_FUNDO("5 - Proposta Fundo a Fundo"),
    PROGRAMACAO_SIGTV("6 - Programação SIGTV");
    private String descricao;

    TipoConvenioReceita(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
