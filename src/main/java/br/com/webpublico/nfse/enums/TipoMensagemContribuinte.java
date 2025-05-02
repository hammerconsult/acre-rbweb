package br.com.webpublico.nfse.enums;

public enum TipoMensagemContribuinte {
    INFORMACAO("Informação"), QUESTIONAMENTO("Questionamento");

    private String descricao;

    TipoMensagemContribuinte(String descricao) {
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
