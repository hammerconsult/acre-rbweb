package br.com.webpublico.enums.rh.esocial;

/**
 * Created by William on 05/10/2018.
 */
public enum TipoAcidenteTransito {
    ATROPELAMENTO(1, "Atropelamento"),
    COLISAO(2, "Colisão"),
    OUTROS(3, "Outros");

    private Integer codigo;
    private String descricao;

    TipoAcidenteTransito(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
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
        return this.codigo + " - " + this.descricao;
    }
}
