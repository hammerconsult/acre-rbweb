package br.com.webpublico.exception;

import br.com.webpublico.repositorios.jdbc.WebPublicoJDBCException;

import java.sql.PreparedStatement;

/**
 * @author Daniel
 * @since 27/08/2015 09:39
 */
public class CampoObrigatorioVazioException extends WebPublicoJDBCException {

    public CampoObrigatorioVazioException(String message, PreparedStatement ps, Integer position) {
        super(message.concat(getMessagePosition(ps.toString(), position)));
    }

    public CampoObrigatorioVazioException(String message, Exception cause) {
        super(message, cause);
    }

    private static String getMessagePosition(String query, Integer position) {
        String[] split = query.split(",");
        String campo = split[position - 1];
        return ". {".concat(campo).concat(" ["+position+"]}");
    }

}
