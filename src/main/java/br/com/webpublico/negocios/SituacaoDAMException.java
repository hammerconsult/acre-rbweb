/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.negocios;

import javax.ejb.ApplicationException;

/**
 * @author terminal1
 */
@ApplicationException(rollback = true)
public class SituacaoDAMException extends ExcecaoNegocioGenerica {

    public SituacaoDAMException(String message) {
        super(message);
    }

    public SituacaoDAMException(String message, Throwable e) {
        super(message, e);
    }
}
