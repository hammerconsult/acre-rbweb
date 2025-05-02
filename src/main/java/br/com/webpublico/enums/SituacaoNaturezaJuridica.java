package br.com.webpublico.enums;

public enum SituacaoNaturezaJuridica {

    ATIVO("Ativo"),
    INATIVO("Inativo");

    private String descricao;

    private SituacaoNaturezaJuridica(String descricao) {
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
