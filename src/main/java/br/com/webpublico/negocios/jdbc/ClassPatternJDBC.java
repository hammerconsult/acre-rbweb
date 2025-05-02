package br.com.webpublico.negocios.jdbc;

import java.io.Serializable;
import java.sql.Connection;

/**
 * Created by venom on 03/03/15.
 */
public abstract class ClassPatternJDBC extends NullStatementParameters implements Serializable {

    protected static Connection conexao;

    protected static void showConnection(Class classe) {
        if (conexao != null) {
            System.out.println(classe.getSimpleName() + " instance with connection: " + conexao);
        } else {
            System.out.println(classe.getSimpleName() + " conexão nula!");
        }
    }
}
