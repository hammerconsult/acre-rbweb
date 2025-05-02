package br.com.webpublico.nfse.enums;

public enum SituacaoSorteioNfse {
    ABERTO("Aberto"),
    REALIZADO("Realizado");

    private String descricao;

    SituacaoSorteioNfse(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isRealizado() {
        return this.equals(REALIZADO);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
