package br.com.webpublico.enums;

public enum FormaEntregaExecucao {

    UNICA("Única"),
    PARCELADA("Parcelada");
    private String descricao;

    FormaEntregaExecucao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isUnica() {
        return FormaEntregaExecucao.UNICA.equals(this);
    }

    public boolean isParcelada() {
        return FormaEntregaExecucao.PARCELADA.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
