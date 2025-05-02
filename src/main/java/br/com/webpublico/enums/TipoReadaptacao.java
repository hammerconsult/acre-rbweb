package br.com.webpublico.enums;


public enum TipoReadaptacao {


    TEMPORARIO("Temporário"),
    PERMANENTE("Permanente");

    private String descricao;

    private TipoReadaptacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
