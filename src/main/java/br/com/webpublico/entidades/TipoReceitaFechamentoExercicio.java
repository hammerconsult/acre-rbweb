package br.com.webpublico.entidades;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 03/11/14
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
public enum TipoReceitaFechamentoExercicio {
    RECEITA_A_REALIZAR("Receita à Realizar"),
    REESTIMATIVA("Reestimativa"),
    DEDUCAO_INICIAL_RECEITA("Dedução da Previsão Inicial da Receita"),
    DEDUCAO_RECEITA_REALIZADA("Dedução da Receita Realizada"),
    RECEITA_REALIZADA("Receita Realizada");

    private String descricao;

    private TipoReceitaFechamentoExercicio(String descricao) {
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
