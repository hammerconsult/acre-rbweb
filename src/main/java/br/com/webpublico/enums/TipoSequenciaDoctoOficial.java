/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author claudio
 */
public enum TipoSequenciaDoctoOficial {
    EXERCICIO("Por Exercício"),
    SEQUENCIA("Sequência");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    private TipoSequenciaDoctoOficial(String descricao) {
        this.descricao = descricao;
    }
}
