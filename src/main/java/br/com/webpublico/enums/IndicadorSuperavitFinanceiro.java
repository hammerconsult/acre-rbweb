package br.com.webpublico.enums;

public enum IndicadorSuperavitFinanceiro {

    FINANCEIRO(1, "Financeiro"),
    PERMANENTE(2, "Permanente");

    private int codigo;
    private String descricao;

    private IndicadorSuperavitFinanceiro(int codigo,String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

}
