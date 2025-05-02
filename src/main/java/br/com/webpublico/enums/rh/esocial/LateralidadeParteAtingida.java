package br.com.webpublico.enums.rh.esocial;

public enum LateralidadeParteAtingida {
    NAO_APLICAVEL("Não aplicável", 0),
    ESQUERDA("Esquerda", 1),
    DIREITA("Direita", 2),
    AMBAS("Ambas", 3);

    String descricao;
    Integer codigo;

    LateralidadeParteAtingida(String descricao, Integer codigo) {
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
