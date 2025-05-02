/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.exception.rh;

import javax.ejb.ApplicationException;

/**
 * @author Felipe Marinzeck
 */
@ApplicationException(rollback = true)
public class SemConfiguracaoDeFaltaInjustificada extends RuntimeException {

    public SemConfiguracaoDeFaltaInjustificada(String message) {
        super(message);
    }

    public SemConfiguracaoDeFaltaInjustificada(String message, Throwable e) {
        super(message, e);
    }
}
