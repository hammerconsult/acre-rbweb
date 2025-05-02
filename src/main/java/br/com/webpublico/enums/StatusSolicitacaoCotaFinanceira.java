/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author major
 */
public enum StatusSolicitacaoCotaFinanceira implements EnumComDescricao {
    ABERTA("Aberta"),
    A_APROVAR("A aprovar"),
    A_LIBERAR("A Liberar"),
    LIBERADO_PARCIALMENTE("Liberada Parcialmente"),
    CANCELADO("Cancelada"),
    CONCLUIDO("Concluída");
    private String descricao;

    StatusSolicitacaoCotaFinanceira(String descricao) {
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
