package br.com.webpublico.exception;

import javax.ejb.ApplicationException;

/**
 * Created with IntelliJ IDEA.
 * User: scarpini
 * Date: 18/10/13
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
@ApplicationException(rollback = true)
public class NenhumaUnidadeVigenteException extends Exception {

    public NenhumaUnidadeVigenteException(String message) {
        super(message);
    }

    public NenhumaUnidadeVigenteException(String message, Throwable e) {
        super(message, e);
    }
}
