/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.exception;

import javax.ejb.ApplicationException;

/**
 * @author gecen
 */
@ApplicationException(rollback = true)
public class FuncoesFolhaFacadeException extends RuntimeException {

    public FuncoesFolhaFacadeException(String message, Throwable cause) {
        super(message, cause);
    }

}
