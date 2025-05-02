package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 23/07/15
 * Time: 10:57
 * To change this template use File | Settings | File Templates.
 */
public enum TipoAgrupamentoMapaConsolidado {
    TRIBUTO("Tributo"), CONTA_RECEITA("Conta de Receita"),
    BANCO_CONTA_RECEITA("Banco e Conta de Receita"),
    BANCO_CONTA_RECEITA_EXERCICIO("Banco, Conta de Receita e Exercício do Débito"),
    BANCO_CONTA_RECEITA_DATA("Banco, Conta de Receita e Data");

    private String descricao;

    TipoAgrupamentoMapaConsolidado(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
