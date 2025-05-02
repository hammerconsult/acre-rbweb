package br.com.webpublico.enums;

/**
 * Created by zaca on 26/04/17.
 */
public enum TipoResponsavelTecnico {
    CONTRATANTE("Contratante"),
    CONTRATADO("Contratado");

    private String descricao;

    TipoResponsavelTecnico(String descricao) {
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
        return getDescricao();
    }
}
