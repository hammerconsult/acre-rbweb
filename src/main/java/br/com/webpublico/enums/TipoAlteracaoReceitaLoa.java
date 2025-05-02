/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum TipoAlteracaoReceitaLoa {
    PREVISAOADICIONALRECEITA("Previsão Adicional da Receita"),
    DEDUCAOPREVISAOADICIONALRECEITA("Dedução da Previsão Adicional da Receita"),
    DEDUCAOPREVIADICIONALRECEITAFUNDEB("Dedução da Previsão Adicional da Receita - Fundeb"),
    ANULACAOPREVISAORECEITA("Anulação da Previsão da Receita");
    private String descricao;

    private TipoAlteracaoReceitaLoa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
