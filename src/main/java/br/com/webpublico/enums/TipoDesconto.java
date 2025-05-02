/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;


/**
 * @author William
 */
public enum TipoDesconto {

    SEM_DESCONTO("1 - Sem Desconto"),
    DESCONTO_DE_MULTA("2 - Desconto de Multa"),
    DESCONTO_DE_JUROS("3 - Desconto de Juros"),
    AMBOS("4 - Ambos");

    private String descricao;

    private TipoDesconto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


}


