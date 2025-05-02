/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.StringUtil;

import java.io.Serializable;

/**
 * @author andre
 */
public class SefipRegistroTipo90 implements Serializable {

    private final String tipoDeRegistro = "90";
    private String marcaDeFinalDeRegistro;
    private String brancos;
    private final String finalDeLinha = "*";

    public SefipRegistroTipo90() {
    }

    public SefipRegistroTipo90(String marcaDeFinalDeRegistro, String brancos) {
        this.marcaDeFinalDeRegistro = marcaDeFinalDeRegistro;
        this.brancos = brancos;
    }

    public String getBrancos() {
        return brancos;
    }

    public void setBrancos(String brancos) {
        this.brancos = brancos;
    }

    public String getMarcaDeFinalDeRegistro() {
        return marcaDeFinalDeRegistro;
    }

    public void setMarcaDeFinalDeRegistro(String marcaDeFinalDeRegistro) {
        this.marcaDeFinalDeRegistro = marcaDeFinalDeRegistro;
    }

    public String getRegistroTipo90() {
        StringBuilder texto = new StringBuilder("");

        //Tipo de Registro - 2 Posições
        texto.append(tipoDeRegistro);
        //Marca de Final de Registros - 51 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(marcaDeFinalDeRegistro, 51, "9"));
        //Brancos - 306 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(brancos, 306, " "));
        //Final de Linha - 1 Posição
        texto.append(StringUtil.cortaOuCompletaEsquerda(finalDeLinha, 1, "*"));
        return texto.toString();
    }
}
