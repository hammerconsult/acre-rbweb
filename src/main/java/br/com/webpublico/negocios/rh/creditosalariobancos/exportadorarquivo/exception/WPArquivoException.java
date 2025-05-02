package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception;

/**
 * @author Daniel Franco
 * @since 19/05/2016 08:56
 */
public class WPArquivoException extends RuntimeException {

    public WPArquivoException(String message) {
        super(message);
    }

    public WPArquivoException(String message, Throwable cause) {
        super(message, cause);
    }
}
