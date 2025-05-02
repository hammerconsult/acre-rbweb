package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception;

/**
 * @author Daniel Franco
 * @since 25/05/2016 11:25
 */
public class TamanhoMenorQueOPermitidoException extends TamanhoInvalidoException {

    public TamanhoMenorQueOPermitidoException(String message) {
        super(message);
    }

    public TamanhoMenorQueOPermitidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
