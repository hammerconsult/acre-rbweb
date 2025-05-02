package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoParecerVistoriaOtt  implements EnumComDescricao {
    FAVORAVEL("Favorável"),
    NAO_FAVORAVEL("Não Favorável");

    private String descricao;

    private TipoParecerVistoriaOtt(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
