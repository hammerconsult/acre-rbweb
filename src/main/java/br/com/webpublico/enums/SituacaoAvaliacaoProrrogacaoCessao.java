package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/05/14
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public enum  SituacaoAvaliacaoProrrogacaoCessao implements EnumComDescricao {
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada");
    private String descricao;

    SituacaoAvaliacaoProrrogacaoCessao(String descricao) {
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
