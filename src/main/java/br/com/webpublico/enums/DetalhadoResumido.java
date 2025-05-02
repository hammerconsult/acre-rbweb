package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 09/03/2017.
 */
public enum DetalhadoResumido {
    DETALHADO("Detalhado"),
    RESUMIDO("Resumido");
    private String descricao;

    DetalhadoResumido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
