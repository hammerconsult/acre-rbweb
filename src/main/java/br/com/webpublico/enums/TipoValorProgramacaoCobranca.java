package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 31/07/13
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */
public enum TipoValorProgramacaoCobranca {

    ORIGINAL("Original"),
    ORIGINAL_CORRECAO("Original + Correção"),
    ATUALIZADO("Original + Correção + Juros + Multa + Honorários");

    private String descricao;

    private TipoValorProgramacaoCobranca(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
