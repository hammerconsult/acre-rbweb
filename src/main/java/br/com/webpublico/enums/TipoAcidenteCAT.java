package br.com.webpublico.enums;

/**
 * @Author peixe on 13/09/2016  14:33.
 */
public enum TipoAcidenteCAT {
    TIPICO("Típico", 1),
    DOENCA("Doença", 2),
    TRAJETO("Trajeto", 3);

    private String descricao;
    private Integer codigo;

    TipoAcidenteCAT(String descricao, Integer codigo) {
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
        return descricao;
    }
}
