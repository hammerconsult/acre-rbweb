package br.com.webpublico.enums.rh.previdencia;

public enum TipoMotivoDemissaoBBPrev {
    APOSENTADORIA("01", "Aposentadoria"),
    OBITO("02", "Obito"),
    INICIATIVA_PATROCINADORA("03", "Iniciativa da Patrocinadora"),
    A_PEDIDO("05", "A Pedido");

    private String codigo;
    private String descricao;

    TipoMotivoDemissaoBBPrev(String codigo, String descricao) {
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
