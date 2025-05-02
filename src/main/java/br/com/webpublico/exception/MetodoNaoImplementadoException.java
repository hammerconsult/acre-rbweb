package br.com.webpublico.exception;

import br.com.webpublico.repositorios.jdbc.WebPublicoJDBCException;

/**
 * @author Daniel Franco
 * @since 01/08/2016 17:16
 */
public class MetodoNaoImplementadoException extends WebPublicoJDBCException {

    public MetodoNaoImplementadoException(String message) {
        super(message);
    }

    public MetodoNaoImplementadoException(String message, Exception cause) {
        super(message, cause);
    }
}
