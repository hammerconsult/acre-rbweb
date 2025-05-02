/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author munif
 */
public class DescreveTabelas {

    public static void main(String args[]) {
        try {
            Conexao conexao = Conexao.getInstancia();
            Statement s = conexao.getConnection().createStatement();
            ResultSet res = s.executeQuery("SELECT * FROM cat WHERE TABLE_NAME NOT LIKE '%_AUD'");
            List<String> tabelas = new ArrayList<String>();
            while (res.next()) {
                String t = res.getString(1);
                if (!t.startsWith("BIN") && res.getString(2).equals("TABLE")) {
                    tabelas.add(t);
                }
            }

            int nTabela = 1;
            for (String nomeTabela : tabelas) {
                //System.out.println(nTabela + "," + nomeTabela);
                ResultSet res2 = s.executeQuery("SELECT * FROM " + nomeTabela);
                ResultSetMetaData data = res2.getMetaData();
                int nCampo = 1;
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    //System.out.println(nTabela + "." + nCampo + "," + data.getColumnName(i) + "," + data.getColumnTypeName(i) + "," + data.getPrecision(i) + ":" + data.getScale(i));
                    nCampo++;
                }

                nTabela++;
                //System.out.println("\n\n");
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
