/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @author Munif
 */
public class Principal {


    public static void main(String args[]) {
        try {
            File arquivoImportacao = new File("/home/munif/webpublico/contabil/planoImportacaoContabil.txt");
            //File arquivoImportacao = new File("/home/munif/webpublico/geral/planoGeral.txt");
            FileInputStream stream = new FileInputStream(arquivoImportacao);
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(streamReader);
            String linha = null;
            while ((linha = br.readLine()) != null) {
                String partes[] = linha.split(";");
                String tabela = partes[0];
                String arquivo = partes[1];
                //System.out.println("Importando dados na tabela " + tabela + " do arquivo " + arquivo);
                new ImportadorGenerico(tabela, arquivo).importar();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
