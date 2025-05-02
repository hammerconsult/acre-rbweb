package br.com.webpublico.nfse.enums;

public enum TipoConsolidacaoDesif {
    TIPO_1(1, "1 - Instituição e alíquota"),
    TIPO_2(2, "2 - Instituição, alíquota e código de tributação DES-IF"),
    TIPO_3(3, "3 - Dependência e alíquota"),
    TIPO_4(4, "4 - Dependência, alíquota e código de tributação DES-IF");

    private Integer codigo;
    private String descricao;

    TipoConsolidacaoDesif(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoConsolidacaoDesif findByCodigo(Integer codigo) {
        for (TipoConsolidacaoDesif tipo : TipoConsolidacaoDesif.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
