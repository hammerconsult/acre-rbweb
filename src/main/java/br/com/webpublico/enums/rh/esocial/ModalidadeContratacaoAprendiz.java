package br.com.webpublico.enums.rh.esocial;

public enum ModalidadeContratacaoAprendiz {
    CONTRATACAO_DIRETA(1, "Contratação direta"),
    CONTRATACAO_INDIRETA(2, "Contratação indireta");

    private Integer codigo;
    private String descricao;

    ModalidadeContratacaoAprendiz(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
