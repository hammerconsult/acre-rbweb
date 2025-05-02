package br.com.webpublico.enums.rh.esocial;

public enum TipoAvaliacaoAgenteNocivo {
    CRITERIO_QUANTITATIVO("Critério quantitativo", 1),
    CRITERIO_QUALITATIVO("Critério qualitativo", 2);

    TipoAvaliacaoAgenteNocivo(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    private String descricao;
    private Integer codigo;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
