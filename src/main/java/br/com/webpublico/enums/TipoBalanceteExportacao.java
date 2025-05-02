package br.com.webpublico.enums;

public enum TipoBalanceteExportacao {
    AGREGADA("Agregada"),
    ENCERRAMENTO("Encerramento");

    private String descricao;

    TipoBalanceteExportacao(String descricao) {
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
