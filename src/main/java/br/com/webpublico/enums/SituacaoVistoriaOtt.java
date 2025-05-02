package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoVistoriaOtt implements EnumComDescricao {
    ABERTA("Aberta"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

    private String descricao;

    private SituacaoVistoriaOtt(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
