package br.com.webpublico.enums.rh.previdencia;

public enum GrauInstrucaoBBPrev {
    NAO_INFORMADO("0", "Não Informado"),
    PRIMEIRO_GRAU_INCOMPLETO("1", "1º Grau Incompleto"),
    PRIMEIRO_GRAU_COMPLETO("2", "1º Grau Completo"),
    SEGUNDO_GRAU_INCOMPLETO("3", "2º Grau Incompleto"),
    SEGUNDO_GRAU_COMPLETO("4", "2º Grau Completo"),
    TERCEIRO_GRAU_INCOMPLETO("5", "3º Grau Incompleto"),
    TERCEIRO_GRAU_COMPLETO("6", "3º Grau Completo"),
    MESTRADO("7", "Mestrado"),
    DOUTORADO("8", "Doutorado");

    private String codigo;
    private String descricao;

    GrauInstrucaoBBPrev(String codigo, String descricao) {
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
