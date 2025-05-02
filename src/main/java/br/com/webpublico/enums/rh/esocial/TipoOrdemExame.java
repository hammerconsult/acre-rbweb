package br.com.webpublico.enums.rh.esocial;

public enum TipoOrdemExame {
    INICIAL("Inicial", 1),
    SEQUENCIAL("Sequencial", 2);

    private String descricao;
    private Integer codigo;

    TipoOrdemExame(String descricao, Integer codigo) {
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
