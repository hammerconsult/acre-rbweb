package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 19/11/14
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
public enum TipoDuplicarConfiguracaoFechamentoExercicio {
    RECEITA("Receita"),
    DESPESA("Despesa"),
    EXTRA_ORCAMENTARIO("Extra-orçamentário"),
    MATERIAL("Material"),
    PATRIMONIO("Patrimònio"),
    DIVIDA_PUBLICA("Dívida Pública"),
    PROVISAO_MATEMATICA_PREVIDENCIARIA("Provisão Matemática Previdênciaria"),
    RESTO_PAGAR("Resto a Pagar"),
    AJUSTE("Ajustes"),
    PERIODO_FASES("Perído de Fases");

    private String descricao;

    private TipoDuplicarConfiguracaoFechamentoExercicio(String descricao) {
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
