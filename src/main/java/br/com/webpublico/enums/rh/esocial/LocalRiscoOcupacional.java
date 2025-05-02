package br.com.webpublico.enums.rh.esocial;

public enum LocalRiscoOcupacional {
    ESTABELECIMENTO_PROPRIO_EMPREGADOR("Estabelecimento do próprio empregador", 1),
    ESTABELECIMENTO_TERCEIROS("Estabelecimento de terceiros", 2);

    private String descricao;
    private Integer codigo;

    LocalRiscoOcupacional(String descricao, Integer codigo) {
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
