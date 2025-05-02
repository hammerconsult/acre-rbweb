package br.com.webpublico.enums;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 15/10/13
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public enum TipoMotivoTransferenciaPermissao {
    CESSAO("Cessão"),
    FALECIMENTO("Falecimento");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoMotivoTransferenciaPermissao(String descricao) {
        this.descricao = descricao;
    }


    @Override
    public String toString() {
        return descricao;
    }

}
