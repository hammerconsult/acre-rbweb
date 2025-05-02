package br.com.webpublico.enums;

public enum StatusDocumentoOtt {
    AGUARDANDO_AVALIACAO("Aguardando Avaliação"), APROVADO("Aprovado"), REJEITADO("Rejeitado");

    private String descricao;

    StatusDocumentoOtt(String descricao) {
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
