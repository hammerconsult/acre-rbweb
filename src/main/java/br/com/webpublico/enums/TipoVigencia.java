package br.com.webpublico.enums;

/**
 * Created by Wellington on 20/04/2016.
 */
public enum TipoVigencia {
    VIGENTE("Vigentes"), NAO_VIGENTE("Não Vigentes");

    private String descricao;

    TipoVigencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
