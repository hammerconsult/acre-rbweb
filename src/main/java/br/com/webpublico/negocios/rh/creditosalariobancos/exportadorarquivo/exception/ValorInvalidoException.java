package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception;

/**
 * @author Daniel Franco
 * @since 25/05/2016 09:59
 */
public class ValorInvalidoException extends ValidadorException {

    public ValorInvalidoException(String message) {
        super(message);
    }

    public ValorInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
