package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception;

/**
 * @author Daniel Franco
 * @since 25/05/2016 09:43
 */
public class TipoNaoValidadoException extends WPArquivoException {
    public TipoNaoValidadoException(String message) {
        super(message);
    }

    public TipoNaoValidadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
