/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author terminal3
 */
@GrupoDiagrama(nome = "Administrativo")
public enum TipoEntidade {

    ORGAO("Órgao", "1", "2"),
    AUTARQUIA("Autarquia", "2", "2"),
    EMPRESAPUBLICA("Empresa Pública", "3", "4"),
    FUNDOS("Fundo Especial Autônomo", "5", "2"),
    SOCIEDADE_DE_ECONOMIA_MISTA("Sociedade de Economia Mista", "3", "4"),
    FUNDO_PUBLICO("Fundo Público", "99", "2"),
    FUNDACAOPUBLICA("Fundação Pública", "4", "2");

    private String tipo;
    private String codigoSiprev;
    private String codigoDirf;

    TipoEntidade(String tipo, String codigoSiprev, String codigoDirf) {
        this.tipo = tipo;
        this.codigoSiprev = codigoSiprev;
        this.codigoDirf = codigoDirf;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCodigoSiprev() {
        return codigoSiprev;
    }

    public String getCodigoDirf() {
        return codigoDirf;
    }

    @Override
    public String toString() {
        return tipo;
    }
}
