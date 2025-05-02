/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.migracao;

import java.sql.Connection;

/**
 * @author Munif
 */
public class Conexao {

    private static Conexao instancia = new Conexao();
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public static Conexao getInstancia() {
        return instancia;
    }

    private Conexao() {
        conecta();
    }

    private void conecta() {
        /*try {
            Class.forName("oracle.jdbc.OracleDriver");
            //connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL", "webpublico2", "senha10");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.100:1521:ORCL", "webpublico", "senha10"); //NOSONAR
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
    }

    public static void main(String args[]) {
        Conexao c = new Conexao();

    }
}
