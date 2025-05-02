package br.com.webpublico.enums.administrativo.frotas;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoMotorista implements EnumComDescricao {
    SERVIDOR("Servidor"),
    TERCEIRIZADO("Terceirizado");
    private String descricao;

    TipoMotorista(String descricao) {
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
