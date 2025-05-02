/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import java.lang.String;

/**
 *
 * @author java
 */
@GrupoDiagrama(nome = "Categoria Veículo")
public enum CategoriaVeiculo {

    ESPECIAL("Especial"),
    CONVENCIONAL("Convencional"),
    LOTACAO("Lotação"),
    TURISMO("Turismo");
    private String descricao;

    CategoriaVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao.toString();
    }
}
