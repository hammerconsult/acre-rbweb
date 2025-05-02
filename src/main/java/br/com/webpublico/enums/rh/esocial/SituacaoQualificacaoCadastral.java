package br.com.webpublico.enums.rh.esocial;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created by mateus on 18/07/17.
 */
public enum SituacaoQualificacaoCadastral implements EnumComDescricao {
    QUALIFICADO("Qualificado"),
    PENDENTE("Pendente"),
    NAO_QUALIFICADO("Não qualificado");

    private String descricao;

    SituacaoQualificacaoCadastral(String descricao) {
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
