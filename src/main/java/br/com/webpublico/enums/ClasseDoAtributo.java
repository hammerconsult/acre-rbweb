/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "AtributosDinamicos")
public enum ClasseDoAtributo {

    CADASTRO_IMOBILIARIO("Cadastro Imobiliário"),
    CADASTRO_RURAL("Cadastro Rural"),
    CADASTRO_ECONOMICO("Cadastro Econômico"),
    CONSTRUCAO("Construção"),
    LOTE("Terreno");
    private String descricao;

    private ClasseDoAtributo(String descricao) {
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
