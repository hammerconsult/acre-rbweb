/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author magraowar
 */
public enum TipoInscricaoRelatorioDebitos {
    ECONOMICO("Econômico"),
    IMOBILIARIO("Imobiliario"),
    RURAL("Rural");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoInscricaoRelatorioDebitos(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

