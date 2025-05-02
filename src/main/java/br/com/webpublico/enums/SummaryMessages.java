package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 29/08/13
 * Time: 10:03
 * To change this template use File | Settings | File Templates.
 */
public enum SummaryMessages {

    CAMPO_OBRIGATORIO("Campo Obrigatório!"),
    OPERACAO_REALIZADA("Operação Realizada!"),
    OPERACAO_NAO_REALIZADA("Operação Não Realizada!"),
    OPERACAO_NAO_PERMITIDA("Operação Não Permitida!"),
    ATENCAO("Atenção!");

    private String descricao;

    private SummaryMessages(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
