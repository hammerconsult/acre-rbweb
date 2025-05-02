package br.com.webpublico.enums.rh;

/**
 * Created by carlos on 06/02/17.
 */
public enum StatusAfastamento {
    SERVIDOR_CONTINUA_AFASTADO("Servidores que Continuam Afastados"),
    SERVIDOR_RETORNOU_AFASTAMENTO("Servidores que Retornaram do Afastamento");

    private String descricao;

    StatusAfastamento(String descricao) {
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
