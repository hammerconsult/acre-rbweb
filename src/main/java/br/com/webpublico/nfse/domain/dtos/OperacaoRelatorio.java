package br.com.webpublico.nfse.domain.dtos;

public enum OperacaoRelatorio {

    BETWEEN("between"),
    IGUAL("="),
    MAIOR_IGUAL(">="),
    MAIOR(">"),
    MENOR_IGUAL("<="),
    MENOR("<"),
    IN("in"),
    NOT_IN("not in"),
    LIKE("like"),
    DIFERENTE("<>"),
    IS_NULL(" is null "),
    IS_NOT_NULL(" is not null ");

    private String descricao;

    private OperacaoRelatorio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
