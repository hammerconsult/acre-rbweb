package br.com.webpublico.enums.rh.dirf;

public enum DirfTipoValor {
    RENDIMENTOS_TRIBUTAVEIS("Rendimentos Tributáveis"),
    SALARIO_FAMILIA("Salário Família");

    private String descricao;

    DirfTipoValor(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isRendimentosTributaveis() {
        return RENDIMENTOS_TRIBUTAVEIS.equals(this);
    }

    public boolean isSalarioFamilia() {
        return SALARIO_FAMILIA.equals(this);
    }
}
