package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 31/07/13
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public enum TipoSituacaoProgramacaoCobranca implements EnumComDescricao {
    SIMULACAO("Simulação"),
    PROGRAMADO("Programado"),
    EXECUTANDO("Executando"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private String descricao;

    private TipoSituacaoProgramacaoCobranca(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
