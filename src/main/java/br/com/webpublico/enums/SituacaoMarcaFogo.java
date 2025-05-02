package br.com.webpublico.enums;

public enum SituacaoMarcaFogo {
    ABERTO("Aberto"), PROCESSADO("Processado");

    private String descricao;

    SituacaoMarcaFogo(String descricao) {
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
