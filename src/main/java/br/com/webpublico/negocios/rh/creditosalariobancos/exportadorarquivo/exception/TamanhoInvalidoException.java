package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception;

/**
 * @author Daniel Franco
 * @since 25/05/2016 09:57
 */
public class TamanhoInvalidoException extends ValidadorException {

    public TamanhoInvalidoException(String message) {
        super(message);
    }

    public TamanhoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
