package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by venom on 24/02/15.
 */
public abstract class NullStatementParameters {

    public boolean allFieldsNotNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    protected void setDate(PreparedStatement ps, int index, java.sql.Date valor) throws SQLException {
        if (valor == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setDate(index, valor);
        }
    }

    protected void setLong(PreparedStatement ps, int index, Long valor) throws SQLException {
        if (valor == null) {
            ps.setNull(index, Types.NUMERIC);
        } else {
            ps.setLong(index, valor);
        }
    }

    protected void setString(PreparedStatement ps, int index, String valor) throws SQLException {
        if (valor == null) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, valor);
        }
    }
}
