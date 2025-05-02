package br.com.webpublico.enums;

/**
 * Created by carlos on 22/06/15.
 */
public enum SituacaoASO {

    APTO("Apto", 1),
    INAPTO("Inapto", 2);
    private String descricao;
    private Integer codigo;

    SituacaoASO(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
