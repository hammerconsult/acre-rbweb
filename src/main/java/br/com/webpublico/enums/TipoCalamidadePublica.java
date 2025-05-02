package br.com.webpublico.enums;

public enum TipoCalamidadePublica {
    COVID("Covid-19");

    private String descricao;

    TipoCalamidadePublica(String descricao){
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
        return super.toString();
    }
}
