package br.com.webpublico.entidadesauxiliares.contabil.analise;

public enum TipoAnaliseContabil {
    ORCAMENTARIO("ORCAMENTARIO"),
    FINANCEIRO("FINANCEIRO"),
    EXTRAORCAMENTARIO("EXTRAORCAMENTARIO"),
    DIVIDAPUBLICA("DIVIDA PUBLICA"),
    CONTABIL("CONTABIL"),
    CONTABIL_SICONFI("CONTABIL - SICONFI"),
    BEM_MOVEL("BEM MÓVEL"),
    BEM_IMOVEL("BEM IMÓVEL"),
    BEM_ESTOQUE("BEM ESTOQUE"),
    BEM_INTANGIVEL("BEM INTANGÍVEL");

    private String descricao;

    TipoAnaliseContabil(String descricao) {
        this.descricao = descricao;
    }


    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
