/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author gustavo
 */
public enum TipoOcupacaoLocalizacao {
    ALUGUEL_PAT_PUBLICO("Aluguel de Patrimônio Público"),
    OCUPACAO_SOLO_URBANO("Ocupação de Solo Urbano");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoOcupacaoLocalizacao(String descricao) {
        this.descricao = descricao;
    }


}
