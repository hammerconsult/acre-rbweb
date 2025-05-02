package br.com.webpublico.exception;

import javax.ejb.ApplicationException;

/**
 * Created by Wellington Abdo on 17/08/2016.
 */
@ApplicationException(rollback = true)
public class BloqueioMovimentoContabilException extends Exception {

    public BloqueioMovimentoContabilException(String message) {
        super(message);
    }
}
