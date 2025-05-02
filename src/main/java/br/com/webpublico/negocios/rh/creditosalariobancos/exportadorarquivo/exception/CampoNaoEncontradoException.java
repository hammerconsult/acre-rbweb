package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception;

/**
 * @author Daniel Franco
 * @since 19/05/2016 08:57
 */
public class CampoNaoEncontradoException extends WPArquivoException {

    public CampoNaoEncontradoException(String message) {
        super(message);
    }

    public CampoNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
