package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 24/05/2016.
 */
public enum SicapModalidadeArquivo {
    EVENTUAL("Eventual"),
    PERIODICO("Periódico");

    SicapModalidadeArquivo(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

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
