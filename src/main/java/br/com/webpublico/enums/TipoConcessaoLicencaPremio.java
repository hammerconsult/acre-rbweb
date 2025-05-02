package br.com.webpublico.enums;

/**
 * Created by carlos on 21/10/15.
 */
public enum TipoConcessaoLicencaPremio {
    TOTAL("Total"),
    PARCIAL("Parcial");
    private String descricao;

    TipoConcessaoLicencaPremio(String descricao) {
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
