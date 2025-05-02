package br.com.webpublico.repositorios.jdbc;

/**
 * @author Daniel
 * @since 26/08/2015 10:33
 */
public class WebPublicoJDBCException extends RuntimeException {

    public WebPublicoJDBCException(String message) {
        super(message);
    }

    public WebPublicoJDBCException(String message, Exception cause) {
        super(message, cause);
    }
}
