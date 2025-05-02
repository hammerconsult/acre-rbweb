package br.com.webpublico.nfse.enums;

import java.io.Serializable;

public enum TipoCancelamento implements Serializable {
    MANUAL("Manual"), AUTOMATICO("Automático");

    private String descricao;

    TipoCancelamento(String descricao) {
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
