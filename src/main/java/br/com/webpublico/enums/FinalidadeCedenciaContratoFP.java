/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "RecursosHumanos")
public enum FinalidadeCedenciaContratoFP {
    INTERNA("Interna"),
    EXTERNA("Externa");

    private String descricao;

    private FinalidadeCedenciaContratoFP(String d) {
        descricao = d;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
