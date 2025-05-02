package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception;

/**
 * @author Daniel Franco
 * @since 25/05/2016 09:56
 */
public class ValorNaoNumericoException extends ValidadorException {

    public ValorNaoNumericoException(String message) {
        super(message);
    }

    public ValorNaoNumericoException(String message, Throwable cause) {
        super(message, cause);
    }
}
