package br.com.webpublico.exception.rh;

public class EnvioDadosRBPontoException extends RuntimeException {

    public EnvioDadosRBPontoException(String message) {
        super(message);
    }

    public EnvioDadosRBPontoException(String message, Throwable e) {
        super(message, e);
    }
}
