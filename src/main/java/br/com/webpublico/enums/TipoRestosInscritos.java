/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author Edi
 */
public enum TipoRestosInscritos {

    INSCRITOS("Inscritos"),
    DE_EXERCICIOS_ANTERIORES("De Exercícios Anteriores");
    private String descricao;

    private TipoRestosInscritos(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
