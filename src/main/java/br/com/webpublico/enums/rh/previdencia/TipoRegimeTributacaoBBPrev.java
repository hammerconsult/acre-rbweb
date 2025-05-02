package br.com.webpublico.enums.rh.previdencia;

public enum TipoRegimeTributacaoBBPrev {
    TABELA_PROGRESSIVA("P", "Tabela progressiva"),
    TABELA_REGRESSIVA("R", "Tabela regressiva"),
    INFORMAREI_POSTERIORMENTE("I", "Informarei posteriormente");

    private String codigo;
    private String descricao;

    TipoRegimeTributacaoBBPrev(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.descricao;
    }
}
