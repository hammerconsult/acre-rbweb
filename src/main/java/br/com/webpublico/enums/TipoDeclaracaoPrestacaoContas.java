/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author andre
 */
public enum TipoDeclaracaoPrestacaoContas implements EnumComDescricao {
    DECLARACOES("1 - Declarações"),
    PCA("2 - PCA"),
    PCM("3 - PCM"),
    RELATORIOS("4 - Relatórios");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoDeclaracaoPrestacaoContas(String descricao) {
        this.descricao = descricao;
    }
}
