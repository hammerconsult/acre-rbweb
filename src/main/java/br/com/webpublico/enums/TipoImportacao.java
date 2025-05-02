package br.com.webpublico.enums;


public enum TipoImportacao {

    ARQUIVO("Importados do Arquivo"),
    ECONSIG("Econsig");

    private String descricao;

    private TipoImportacao(String descricao){
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
