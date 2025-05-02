package br.com.webpublico.nfse.enums;

public enum TipoDesif {
    TIPO_1(1, "1 - Normal"),
    TIPO_2(2, "2 - Retificadora");

    private Integer codigo;
    private String descricao;

    TipoDesif(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoDesif findByCodigo(Integer codigo) {
        for (TipoDesif tipo : TipoDesif.values()) {
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
