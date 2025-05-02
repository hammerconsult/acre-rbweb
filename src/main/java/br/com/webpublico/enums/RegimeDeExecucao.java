package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 27/03/14
 * Time: 00:45
 * To change this template use File | Settings | File Templates.
 */
public enum RegimeDeExecucao implements EnumComDescricao {

    EXECUCAO_DIRETA("Execução Direta"),
    EXECUCAO_INDIRETA("Execução Indireta"),
    EMPREITADA_POR_PRECO_UNITARIO("Empreitada Por Preço Unitário"),
    EMPREITADA_POR_PRECO_GLOBAL("Empreitada Por Preço Global"),
    EMPREITADA_INTEGRAL("Empreitada Integral"),
    TAREFA("Tarefa");
    private String descricao;

    RegimeDeExecucao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
