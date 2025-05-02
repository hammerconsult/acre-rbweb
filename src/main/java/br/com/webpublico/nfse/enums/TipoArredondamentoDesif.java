package br.com.webpublico.nfse.enums;

public enum TipoArredondamentoDesif {
    TIPO_1(1, "1 - Arredondado"),
    TIPO_2(2, "2 - Truncado");

    private Integer codigo;
    private String descricao;

    TipoArredondamentoDesif(Integer codigo, String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoArredondamentoDesif findByCodigo(Integer codigo) {
        for (TipoArredondamentoDesif tipo : TipoArredondamentoDesif.values()) {
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
