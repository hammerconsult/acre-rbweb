package br.com.webpublico.enums;

public enum SituacaoCalculo {

    ABERTO("Aberto"),
    EMITIDO("Emitido"),
    PAGO("Pago"),
    CANCELADO("Cancelado");

    private SituacaoCalculo(String descricao) {
        this.descricao = descricao;
    }
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
