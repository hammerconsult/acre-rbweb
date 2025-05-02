/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "CadastroUnico")
public enum CategoriaHabilitacao {

    A("Categoria A"),
    B("Categoria B"),
    C("Categoria C"),
    D("Categoria D"),
    E("Categoria E"),
    AB("Categorias AB"),
    AC("Categorias AC"),
    AD("Categorias AD"),
    AE("Categorias AE");
    private String descricao;

    private CategoriaHabilitacao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
