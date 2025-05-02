package br.com.webpublico.enums.rh.esocial;

public enum IndicativoSubstituicao {
    INTEGRALMENTE_SUBSTITUIDA("Integralmente substituída", 1),
    PARCIALMENTE_SUBSTITUIDA("Parcialmente substituída", 2);

    private String descricao;
    private Integer codigo;

    IndicativoSubstituicao(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo +" - " + descricao;
    }
}
