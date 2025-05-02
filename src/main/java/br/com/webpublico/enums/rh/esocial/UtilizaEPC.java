package br.com.webpublico.enums.rh.esocial;

public enum UtilizaEPC {
    NAO_APLICA("Não se aplica", 0),
    NAO_IMPLEMENTA("Não implementa", 1),
    IMPLEMENTA("Implementa", 2);

    private String descricao;
    private Integer codigo;

    UtilizaEPC(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

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
