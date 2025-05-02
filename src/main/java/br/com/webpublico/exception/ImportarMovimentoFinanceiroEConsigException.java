/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.exception;

import javax.ejb.EJBException;

/**
 * @author Peixe
 */
public class ImportarMovimentoFinanceiroEConsigException extends EJBException {

    public ImportarMovimentoFinanceiroEConsigException(String message) {
        super(message);
    }
}
