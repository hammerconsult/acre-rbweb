package br.com.webpublico.repositorios.jdbc.util;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.ContaCorrenteContribuinte;
import br.com.webpublico.entidades.EntidadeWebPublico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.exception.CampoVazioException;
import br.com.webpublico.repositorios.jdbc.WebPublicoJDBCException;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

/**
 * @author Daniel
 * @since 26/08/2015 11:55
 */
public class JDBCUtil {

    private static final Logger logger = LoggerFactory.getLogger(JDBCUtil.class);

    private JDBCUtil() {
    }

    /**
     * Verifica se a Connection está apta a se comunicar com o Banco de Dados
     *
     * @param conn - A Connection a ser verificada
     * @return true se válida, uma Exception caso encontre problema
     */
    public static boolean validateConnection(Connection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("Connection não pode ser null");
        }
        try {
            if (!conn.isValid(5)) {
                throw new IllegalArgumentException("Connection não é válida");
            }
        } catch (SQLException ex) {
            throw new WebPublicoJDBCException("Erro Validando Connection", ex);
        }
        return true;
    }

    public static boolean stringVazia(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean entidadeVazia(EntidadeWebPublico entidade) {
        return entidade == null || entidade.getId() == null;
    }

    public static void validarCampo(Object campo, String nome) {
        boolean invalido;
        if (campo instanceof String) {
            invalido = stringVazia((String) campo);
        } else if (campo instanceof EntidadeWebPublico) {
            invalido = entidadeVazia((EntidadeWebPublico) campo);
        } else {
            invalido = campo == null;
        }
        if (invalido) {
            throw new CampoVazioException(nome);
        }
    }

    public static Long getLong(ResultSet rs, String fieldName) throws SQLException {
        final Long retorno = rs.getLong(fieldName);
        return rs.wasNull() ? null : retorno;
    }

    public static BigDecimal getBigDecimal(ResultSet rs, String fieldName) throws SQLException {
        final BigDecimal retorno = rs.getBigDecimal(fieldName);
        return rs.wasNull() ? null : retorno;
    }

    public static Date getDate(ResultSet rs, String fieldName) throws SQLException {
        final Date retorno = rs.getDate(fieldName);
        return rs.wasNull() ? null : new LocalDate(retorno).toDate();
    }

    public static Boolean getBoolean(ResultSet rs, String fieldName) throws SQLException {
        final Boolean retorno = rs.getBoolean(fieldName);
        return rs.wasNull() ? null : retorno;
    }

    public static Integer getInt(ResultSet rs, String fieldName) throws SQLException {
        final Integer retorno = rs.getInt(fieldName);
        return rs.wasNull() ? null : retorno;
    }

    public static String getString(ResultSet rs, String fieldName) throws SQLException {
        return rs.getString(fieldName) == null || rs.getString(fieldName).trim().isEmpty() ? null : rs.getString(fieldName);
    }

    public static Time getTime(ResultSet rs, String fieldName) throws SQLException {
        return rs.getTime(fieldName);
    }

    public static InputStream getInputStream(ResultSet rs, String fieldName) throws SQLException {
        InputStream inputStream = rs.getBinaryStream(fieldName);
        return inputStream == null ? null : inputStream;
    }

    public static <T extends Enum<T>> T getEnum(String valor, Class<T> enumClass) {
        if (valor == null || valor.trim().isEmpty()) {
            logger.trace("Impossível encontrar Enum para valor vazio");
            return null;
        }
        try {
            return Enum.valueOf(enumClass, valor.trim().toUpperCase());
        } catch (IllegalArgumentException iae) {
            logger.warn("Nenhum valor encontrado no enum {} para a String [{}]", enumClass.getName(), valor.trim().toUpperCase());
            return null;
        }
    }

    public static <T extends Enum<T>> T getEnum(ResultSet rs, String fieldName, Class<T> enumClass) throws SQLException {
        return getEnum(getString(rs, fieldName), enumClass);
    }

    public static void atribuirLong(PreparedStatement ps, int posicao, Long id) throws SQLException {
        if (id != null) {
            ps.setLong(posicao, id);
        } else {
            ps.setNull(posicao, Types.LONGNVARCHAR);
        }
    }

    public static void atribuirString(PreparedStatement ps, int posicao, String valor) throws SQLException {
        if (valor != null) {
            ps.setString(posicao, valor);
        } else {
            ps.setNull(posicao, Types.VARCHAR);
        }
    }

    public static void setBoolean(PreparedStatement preparedStatement, int index, Boolean value) throws SQLException {
        if (value != null) {
            preparedStatement.setBoolean(index, value);
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    public static void setLong(PreparedStatement preparedStatement, int index, Long value) throws SQLException {
        if (value != null) {
            preparedStatement.setLong(index, value);
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    public static void setString(PreparedStatement preparedStatement, int index, String value) throws SQLException {
        if (value != null) {
            preparedStatement.setString(index, value);
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    public static void setDate(PreparedStatement preparedStatement, int index, Date value) throws SQLException {
        if (value != null) {
            preparedStatement.setDate(index, DateUtils.toSQLDate(value));
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    public static void setEntity(PreparedStatement preparedStatement, int index, SuperEntidade entidade) throws SQLException {
        if (entidade != null) {
            preparedStatement.setLong(index, entidade.getId());
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    public static void setEnum(PreparedStatement preparedStatement, int index, Enum value) throws SQLException {
        if (value != null) {
            preparedStatement.setString(index, value.name());
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    public static void setInt(PreparedStatement preparedStatement, int index, Integer value) throws SQLException {
        if (value != null) {
            preparedStatement.setInt(index, value);
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }
}
