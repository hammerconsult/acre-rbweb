package br.com.webpublico.enums;

/**
 * Created by zaca on 06/06/17.
 */
public enum  TipoCaminhao {
    BASCULANTE("Basculante"),
    CARGA_SECA("Carga Seca");

    private String descricao;

    TipoCaminhao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
