/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum OperacaoClasseCredor {
    NENHUM("Nenhuma"),
    EXTRA("Extra - OFSS"),
    INTRA("Intra - OFSS"),
    INTER_UNIAO("Inter - OFSS UNIÃO"),
    INTER_ESTADO("Inter - OFSS ESTADO"),
    INTER("Inter - OFSS MUNICIPIO");
    private String descricao;

    private OperacaoClasseCredor(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


}
