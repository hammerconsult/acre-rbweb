package br.com.webpublico.exception;

import javax.ejb.ApplicationException;

/**
 * @author Alex
 * @since 29/07/2016 10:05
 */
@ApplicationException(rollback = true)
public class StatusLicitacaoException extends ValidacaoException {

    public StatusLicitacaoException() {
        super();
    }
}
