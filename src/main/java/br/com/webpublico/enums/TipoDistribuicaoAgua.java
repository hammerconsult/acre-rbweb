package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
public enum TipoDistribuicaoAgua {
    SAERB("SAERB"),
    POCO_PROPRIO("Poço Próprio"),
    POCO_VIZINHO("Poço Vizinho"),
    CAMINHAO_PIPA("Caminhão pipa");

    private String descricao;

    TipoDistribuicaoAgua(String descricao) {
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
