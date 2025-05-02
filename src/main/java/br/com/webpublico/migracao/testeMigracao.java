/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Terminal-2
 */
public class testeMigracao implements Serializable {

    public static Connection conexaoWebPublico;
    public static Connection conexaoAgata;

    private static void teste() throws SQLException {
        ResultSet res = conexaoAgata.createStatement().executeQuery("SELECT * FROM RIOBRANCO.ATR02");
        while (res.next()) {
            for (int i = 1; i <= 3; i++) {
                //System.out.println("Resultado de posição " + i + res.getObject(i));
            }
        }
    }

    public static void main(String args[]) {
        try {
            conexaoWebPublico = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.100:1521:orcl", "webpublico", "senha10"); //NOSONAR
            conexaoAgata = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.100:1521:orcl", "riobranco", "senha10"); //NOSONAR
            teste();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
