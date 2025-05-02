/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author peixe
 */
public enum TipoLaudo {

    PPRA("PPRA"),
    PCMSO("PCMSO"),
    LDCAT("LDCAT");
    private String descricao;

    private TipoLaudo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
