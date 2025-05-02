package br.com.webpublico.enums.rh.esocial;

public enum SituacaoPessoaJuridicaESocial {

    SITUACAO_NORMAL("Situação Normal", 0),
    EXTINCAO("Extinção", 1),
    FUSAO("Fusão", 2),
    CISAO("Cisão", 3),
    INCORPORACAO("Incorporação", 4);

    private String descricao;
    private Integer codigo;

    SituacaoPessoaJuridicaESocial(String descricao, Integer codigo) {
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
