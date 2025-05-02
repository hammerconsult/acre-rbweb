package br.com.webpublico.enums.rh.esocial;

public enum HipoteseLegalContratacao {
    NECESSIDADE("Necessidade de substituição transitória de pessoal permanente", 1),
    DEMANDA("Demanda complementar de serviços", 2);

    private String descricao;
    private Integer codigo;

    HipoteseLegalContratacao(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
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
