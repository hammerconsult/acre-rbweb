package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 13/11/14
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public enum TipoDespesaFechamentoExercicio {
    EMPENHO_A_LIQUIDAR_NAO_PROCESSADOS("Empenhos a Liquidar Inscritos em Resto a Pagar Não Processado"),
    EMPENHO_A_LIQUIDAR_PROCESSADOS("Empenhos a Liquidar Inscritos em Resto a Pagar Processado"),
    CREDITO_EMPENHADO_PAGO("Crédito Empenhado Pago"),
    PAGO_RESTO_PAGAR_PROCESSADO("Total Pago dos Restos a Pagar Processados"),
    PAGO_RESTO_PAGAR_NAO_PROCESSADO("Total Pago dos Restos a Pagar Não Processados"),
    CANCELADO_RESTO_PAGAR_PROCESSADO("Total Cancelado dos Restos a Pagar Processados"),
    CANCELADO_RESTO_PAGAR_NAO_PROCESSADO("Total Cancelado dos Restos a Pagar Não Processados");

    private String descricao;

    private TipoDespesaFechamentoExercicio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
