/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.exception.rh;

import javax.ejb.ApplicationException;


public class EntidadeDeConfiguracaoSemVigenciaException extends RuntimeException {

    public EntidadeDeConfiguracaoSemVigenciaException(String message) {
        super(message);
    }

    public EntidadeDeConfiguracaoSemVigenciaException(String message, Throwable e) {
        super(message, e);
    }
}
