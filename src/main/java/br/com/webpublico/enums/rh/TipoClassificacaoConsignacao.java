package br.com.webpublico.enums.rh;

public enum TipoClassificacaoConsignacao {
    COMPULSORIA("Compulsória"),
    FACULTATIVA("Facultativa");

    private String descricao;

    TipoClassificacaoConsignacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
