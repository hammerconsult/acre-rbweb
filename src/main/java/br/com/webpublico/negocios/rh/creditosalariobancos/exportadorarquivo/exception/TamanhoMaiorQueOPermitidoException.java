package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception;

/**
 * @author Daniel Franco
 * @since 25/05/2016 09:15
 */
public class TamanhoMaiorQueOPermitidoException extends TamanhoInvalidoException {

    public TamanhoMaiorQueOPermitidoException(String message) {
        super(message);
    }

    public TamanhoMaiorQueOPermitidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
