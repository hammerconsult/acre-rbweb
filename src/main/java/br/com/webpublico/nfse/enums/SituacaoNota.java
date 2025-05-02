package br.com.webpublico.nfse.enums;

public enum SituacaoNota {
    EMITIDA("Emitida"),
    PAGA("Paga"),
    CANCELADA("Cancelada"),
    NAO_EMITIDA("Não Emitida");

    private String descricao;

    SituacaoNota(String descricao) {
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
