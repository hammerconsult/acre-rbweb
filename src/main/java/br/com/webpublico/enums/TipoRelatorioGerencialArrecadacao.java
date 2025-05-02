/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Wellington
 */
public enum TipoRelatorioGerencialArrecadacao {
    CONTRIBUINTE("Contribuinte"),
    BAIRRO("Bairro"),
    CNAE("CNAE"),
    BANCO("Banco"),
    TRIBUTO("Tributo"),
    LISTA_SERVICO("Lista de Serviço");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoRelatorioGerencialArrecadacao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
