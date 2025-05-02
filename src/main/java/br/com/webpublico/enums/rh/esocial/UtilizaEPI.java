package br.com.webpublico.enums.rh.esocial;

public enum UtilizaEPI {
    NAO_APLICA("Não se aplica", 0),
    NAO_UTILIZADO("Não utilizado", 1),
    UTILIZADO("Utilizado", 2);

    private String descricao;
    private Integer codigo;

    UtilizaEPI(String descricao, Integer codigo) {
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
