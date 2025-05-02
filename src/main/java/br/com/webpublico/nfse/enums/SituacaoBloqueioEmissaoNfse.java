package br.com.webpublico.nfse.enums;

public enum SituacaoBloqueioEmissaoNfse {
    BLOQUEAR("Bloquear"), DESBLOQUEAR("Desbloquear");

    private String descricao;

    SituacaoBloqueioEmissaoNfse(String descricao) {
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
