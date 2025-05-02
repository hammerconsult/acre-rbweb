/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel
 */
public class ExecutaJDBC {

    private static final Logger logger = LoggerFactory.getLogger(ExecutaJDBC.class);

    private ExecutaJDBC() {
    }

    public static List<RegistroDB> consulta(Connection conn, String consulta, Object... parametros) {
        if (conn == null) {
            return null;
        }
        List<RegistroDB> retorno = null;
        PreparedStatement ps = null;
        try {
            ps = preparaConsulta(conn, consulta, parametros);
            retorno = preencheRegistros(getJDBCResult(ps));
        } catch (Exception ex) {
            logger.error("Não foi possível realizar a consulta JDBC ");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
        return retorno;
    }

    private static PreparedStatement preparaConsulta(Connection conn, String consulta, Object... parametros) throws SQLException {
        PreparedStatement retorno = conn.prepareStatement(consulta);
        if (parametros.length != retorno.getParameterMetaData().getParameterCount()) {
            throw new IllegalArgumentException("Número de Parâmetros Inválido: Consulta possui " + retorno.getParameterMetaData().getParameterCount()
                + " parametros e foram passados " + parametros.length + ".");
        }
        for (int i = 1; i <= parametros.length; i++) {
            Object param = parametros[i - 1];
            if (param instanceof Long) {
                retorno.setLong(i, (Long) param);
            } else if (param instanceof Integer) {
                retorno.setInt(i, (Integer) param);
            } else if (param instanceof String) {
                retorno.setString(i, param.toString());
            } else {
                retorno.setObject(i, parametros[i - 1]);
            }
        }
        return retorno;
    }

    private static ResultSet getJDBCResult(PreparedStatement ps) throws SQLException {
        ResultSet retorno = null;
        if (ps.execute()) {
            retorno = ps.getResultSet();
        }
        return retorno;
    }

    private static List<RegistroDB> preencheRegistros(ResultSet rs) throws SQLException {
        List<RegistroDB> retorno = new ArrayList<>();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                RegistroDB registro = new RegistroDB();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String tabela = metaData.getTableName(i);
                    String nomeCampo = metaData.getColumnName(i);
                    CampoDB campo = new CampoDB(tabela, nomeCampo, getDescricao(rs.getStatement().getConnection(), nomeCampo), rs.getObject(i));
                    registro.addField(campo);
                }
                retorno.add(registro);
            }
        } finally {
            rs.close();
        }
        return retorno;
    }

    private static String getDescricao(Connection conn, String nomeCampo) throws SQLException {
        String retorno = "";
        if ((conn == null || !conn.isValid(3)) || nomeCampo == null) {
            return retorno;
        }
        String sql = "SELECT descricao FROM descricaoCampos WHERE trim(lower(nomeCampo)) = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nomeCampo.trim().toLowerCase());
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                retorno = rs.getString("descricao");
            }
        } catch (Exception ex) {
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return retorno;
    }
}
