/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Heinz
 */
public enum SituacaoLoteBaixa implements EnumComDescricao {

    EM_ABERTO("Em Aberto"),
    INCONSISTENTE("Inconsistente"),
    CONSISTENTE("Consistente"),
    BAIXADO("Baixado"),
    BAIXADO_INCONSITENTE("Baixado com Inconsistência"),
    ESTORNADO("Estornado");
    public String descricao;

    public String getDescricao() {
        return descricao;
    }

    private SituacaoLoteBaixa(String descricao) {
        this.descricao = descricao;
    }

    public boolean isBaixado() {
        return this.equals(BAIXADO) || this.equals(BAIXADO_INCONSITENTE);
    }
}
