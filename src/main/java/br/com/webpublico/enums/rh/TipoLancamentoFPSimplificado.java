package br.com.webpublico.enums.rh;

/**
 * Created by William on 13/02/2019.
 */

public enum TipoLancamentoFPSimplificado {
    VALOR("Valor", null),
    REFERENCIA("Referência", "calculador.calculaBase('baseParaCalculo');"),
    VENCIMENTO_BASE("Vencimento Base", "return (calculador.salarioBase() / calculador.obterDiasDoMes()) * calculador.diasTrabalhados();");

    private String descricao;
    private String formula;

    TipoLancamentoFPSimplificado(String descricao, String formula) {
        this.descricao = descricao;
        this.formula = formula;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
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
