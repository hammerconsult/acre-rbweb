package br.com.webpublico.enums;

public enum SituacaoRemessaProtesto {
    PROTESTADO("Protestado"),
    AGUARDANDO_PROTOCOLIZACAO("Aguardando protocolização");

    private final String descricao;

    SituacaoRemessaProtesto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
