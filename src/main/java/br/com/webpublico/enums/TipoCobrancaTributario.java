package br.com.webpublico.enums;

/**
 * Created by William on 07/06/2016.
 */
public enum TipoCobrancaTributario {
    NOTIFICACAO("Notificação"),
    COTA_UNICA("Cota Única"),
    PARCELADO("Parcelado");

    private String descricao;

    TipoCobrancaTributario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
