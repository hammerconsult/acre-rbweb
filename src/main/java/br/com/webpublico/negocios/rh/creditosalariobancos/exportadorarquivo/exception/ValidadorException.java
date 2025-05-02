package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception;

/**
 * @author Daniel Franco
 * @since 25/05/2016 09:54
 */
public class ValidadorException extends WPArquivoException {

    public ValidadorException(String message) {
        super(message);
    }

    public ValidadorException(String message, Throwable cause) {
        super(message, cause);
    }
}
