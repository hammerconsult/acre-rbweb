package br.com.webpublico.exception;

/**
 * Created with IntelliJ IDEA.
 * User: scarpini
 * Date: 28/03/14
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
public class UsuarioWebNaoExistenteException extends Exception {

    public UsuarioWebNaoExistenteException(String message) {
        super(message);
    }
}
