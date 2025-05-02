/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum TipoFerias {

    FERIAS_GOZO_NORMAL("Férias de Gozo Normal"),
    FERIAS_INDENIZADAS("Férias Indenizadas"),
    FERIAS_PERDIDAS("Férias Perdidas (Não Gozadas)"),
    FERIAS_A_GOZAR("Férias a Gozar"),
    FERIAS_AVERBADAS("Férias Averbadas");

    private String descricao;

    private TipoFerias(String descricao) {
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
