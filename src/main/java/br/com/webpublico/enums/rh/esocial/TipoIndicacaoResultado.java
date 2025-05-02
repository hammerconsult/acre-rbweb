package br.com.webpublico.enums.rh.esocial;

public enum TipoIndicacaoResultado {
    NORMAL("Normal", 1),
    ALTERADO("Alterado", 2),
    ESTAVEL("Estável", 3),
    AGRAVAMENTO("Agravamento", 4);

    private String descricao;
    private Integer codigo;

    TipoIndicacaoResultado(String descricao, Integer codigo) {
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
