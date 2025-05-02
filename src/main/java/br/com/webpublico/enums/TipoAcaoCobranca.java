package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 31/07/13
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public enum TipoAcaoCobranca implements EnumComDescricao {

    NOTIFICACAO("Notificação de Cobrança"),
    AVISO("Aviso de Cobrança");
    private String descricao;

    private TipoAcaoCobranca(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
