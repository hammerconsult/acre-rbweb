package br.com.webpublico.enums;

public enum TipoAjusteDadosCadastrais {

    ORIGINAL("Original"),
    ALTERACAO("Alteração");
    private String descricao;

    TipoAjusteDadosCadastrais(String descricao) {
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
