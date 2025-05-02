package br.com.webpublico.negocios.jdbc;

import java.sql.Connection;

/**
 * Created by venom on 23/02/15.
 */
public interface Conectavel {

    Connection getConnection();

}
