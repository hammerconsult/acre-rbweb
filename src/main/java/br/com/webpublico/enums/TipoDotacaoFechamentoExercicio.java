package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 13/11/14
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public enum TipoDotacaoFechamentoExercicio {
    DOTACAO("Dotação"),
    SUPLEMENTAR("Suplementar"),
    ESPECIAL("Especial"),
    EXTRAORDINARIO("Extraordinário"),
    ANULACAO_DOTACAO("Anulação de Dotação"),
    SUPERAVIT("Superavit Financeira"),
    EXCESSO("Excesso de Arrecadação"),
    ANULACAO("Anulação"),
    OPERACAO_CREDITO("Operação de Crédito"),
    RESERVA_CONTIGENCIA("Reserva de Contigência"),
    ANULACAO_CREDITO("Anulação de crédito");

    private String descricao;

    private TipoDotacaoFechamentoExercicio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
