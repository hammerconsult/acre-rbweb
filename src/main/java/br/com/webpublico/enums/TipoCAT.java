package br.com.webpublico.enums;

/**
 * @Author peixe on 13/09/2016  14:36.
 */
public enum TipoCAT {
    INICIAL("Inicial", 1),
    REABERTURA("Reabertura", 2),
    COMUNICADO_OBITO("Comunicado de Óbito", 3);

    private String descricao;
    private Integer codigo;

    TipoCAT(String descricao, Integer codigo) {
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
