/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

/**
 * @author Munif
 */
public class ImportadorFactory {

    private static ImportadorFactory instancia = new ImportadorFactory();

    public static ImportadorFactory getInstancia() {
        return instancia;
    }

    private ImportadorFactory() {
    }


}
