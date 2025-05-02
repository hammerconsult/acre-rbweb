package br.com.webpublico.enums;

/**
 * Created by mga on 22/05/2018.
 */
public enum FormaReservaContrato {
    VALOR("Valor"),
    PERCENTUAL("Percentual");
    private String descricao;

    FormaReservaContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
