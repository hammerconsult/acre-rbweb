/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author peixe
 */
public enum TipoResponsavel {

    MEDICO("Médico"),
    ENGENHEIRO("Engenheiro"),
    TEC_SEGURANCA_TRABALHO("Técnico de Segurança do Trabalho");
    private String descricao;

    private TipoResponsavel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    private TipoResponsavel() {
    }
}
