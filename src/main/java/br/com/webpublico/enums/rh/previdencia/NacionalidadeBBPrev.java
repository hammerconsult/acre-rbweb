package br.com.webpublico.enums.rh.previdencia;

public enum NacionalidadeBBPrev {
    BRASILEIRO("10", "Brasileiro"),
    NATURALIZADO_BRASILEIRO("20", "Naturalizado/Brasileiro"),
    ARGENTINO("21", "Argentino"),
    BOLIVIANO("22", "Boliviano"),
    CHILENO("23", "Chileno"),
    PARAGUAIO("24", "Paraguaio"),
    URUGUAIO("25", "Uruguaio"),
    ALEMAO("30", "Alemão"),
    BELGA("31", "Belga"),
    BRITANICO("32", "Britânico"),
    CANADENSE("34", "Canadense"),
    ESPANHOL("35", "Espanhol"),
    NORTE_AMERICANO("36", "Norte-Americano (EUA)"),
    FRANCES("37", "Francês"),
    SUICO("38", "Suíço"),
    ITALIANO("39", "Italiano"),
    JAPONES("41", "Japonês"),
    CHINES("42", "Chinês"),
    COREANO("43", "Coreano"),
    PORTUGUES("45", "Português"),
    OUTROS("50", "Outros");

    private String codigo;
    private String descricao;

    NacionalidadeBBPrev(String codigo, String descricao) {
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
