/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author Felipe Marinzeck
 */
public enum FuncaoResponsavelUnidade {

    GESTOR("Gestor"),
    SECRETARIO("Secretário"),
    SECRETARIO_ADJUNTO("Secretário Adjunto"),
    DIRETOR("Diretor");
    private String descricao;

    private FuncaoResponsavelUnidade(String descricao) {
        this.descricao = descricao;
    }

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
}
