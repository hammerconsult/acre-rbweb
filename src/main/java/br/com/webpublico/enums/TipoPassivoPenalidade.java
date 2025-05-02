/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author William
 */
public enum TipoPassivoPenalidade {

    PERMISSIONARIO("Permissionário"),
    MOTORISTA("Motorista"),
    PERMISSIONARIO_MOTORISTA("Permissionário e Motorista");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private TipoPassivoPenalidade(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
