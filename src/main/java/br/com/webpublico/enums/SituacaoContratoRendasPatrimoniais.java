package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author daniel
 */
public enum SituacaoContratoRendasPatrimoniais implements EnumComDescricao {

    ATIVO("Ativo"),
    ENCERRADO("Encerrado"),
    ALTERADO("Alterado"),
    RESCINDIDO("Rescindido"),
    RENOVADO("Renovado");
    String descricao;

    SituacaoContratoRendasPatrimoniais(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
