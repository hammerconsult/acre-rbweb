package br.com.webpublico.enums.rh;

public enum TipoOperacaoIntegracaoRBPonto {
    EDICAO("Edição"),
    EXCLUSAO("Exclusão");

    private String descricao;

    TipoOperacaoIntegracaoRBPonto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
