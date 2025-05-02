/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Claudio
 */
public enum TipoCobrancaInfracao {
    FIXO("Fixo"),
    DIA("Dia"),
    NAO_APLICADA("Não Aplicada"),
    QUANTIDADE("Quantidade");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoCobrancaInfracao(String descricao) {
        this.descricao = descricao;
    }
}
