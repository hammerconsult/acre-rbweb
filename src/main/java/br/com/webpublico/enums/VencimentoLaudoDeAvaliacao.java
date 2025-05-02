package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: ROBSONLUIS-MGA
 * Date: 04/10/13
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
public enum VencimentoLaudoDeAvaliacao {

    DATA_VENCIMENTO_PRIMEIRA_PARCELA("Data de Vencimento da Primeira Parcela"),
    DATA_LANCAMENTO("Data de Lançamento"),
    DATA_EMISSAO_LAUDO("Data de Emissão do Laudo");

    private String descricao;

    private VencimentoLaudoDeAvaliacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
