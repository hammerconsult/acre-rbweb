package br.com.webpublico.enums;

import java.io.Serializable;

public enum Sistema implements Serializable {
    NOTA_PREMIADA("Nota Premiada"),
    NFSE("Nota Fiscal Eletrônica de Serviço"),
    APP_SERVIDOR("Aplicativo do Servidor"),
    PORTAL_CONTRIBUINTE("Portal do Contribuinte");

    private String descricao;

    Sistema(String descricao) {
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
