package br.com.webpublico.enums.rh.previdencia;

public enum GrauParentescoBBPrev {
    CONJUGE("1", "Cônjuge"),
    COMPANHEIRO("2", "Companheiro(A)"),
    CONJUGE_SEPARADO_COM_PENSAO_ALIMENTICIA("3", "Cônjuge Separado com Pensão Alimentícia"),
    FILHO("8", "Filho(A)"),
    ENTEADO("9", "Enteado(A)"),
    TUTELADO("10", "Tutelado(A)"),
    MENOR_SOB_GUARDA("11", "Menor Sob Guarda"),
    FILHO_INVALIDO("13", "Filho(A) Inválido"),
    ENTEADO_TUTELADO_MENOR_INVALIDO("14", "Enteado(A) / Tutelado(A) / Menor Inválido"),
    INDICADO("16", "Indicado"),
    IRMAO("18", "Irmão(A)"),
    IRMAO_INVALIDO("19", "Irmão(A) Inválido"),
    GENITORES("20", "Genitores"),
    DESIGNADO("30", "Designado");

    private String codigo;
    private String descricao;

    GrauParentescoBBPrev(String codigo, String descricao) {
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
