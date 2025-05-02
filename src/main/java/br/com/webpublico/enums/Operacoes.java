/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

/**
 * @author Munif
 */
public enum Operacoes {
    NOVO("Novo"),
    EDITAR("Editar"),
    LISTAR("Listar"),
    VER("Ver"),
    EXCLUIR("Excluir");

    String descricao;

    Operacoes(String descricao) {
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
