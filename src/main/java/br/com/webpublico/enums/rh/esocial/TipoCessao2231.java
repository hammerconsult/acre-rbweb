package br.com.webpublico.enums.rh.esocial;

public enum TipoCessao2231 {

    INI_CESSAO("Inicio da cessão"),
    FIM_CESSAO("Final da cessão");

    private String descricao;

    TipoCessao2231(String descricao) {
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
