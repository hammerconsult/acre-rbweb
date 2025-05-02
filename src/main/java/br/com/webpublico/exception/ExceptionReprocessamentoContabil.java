/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.exception;

import javax.ejb.ApplicationException;

/**
 * @author Renato
 */
@ApplicationException(rollback = true)
public class ExceptionReprocessamentoContabil extends RuntimeException {

//    List<MovimentosContabeisErroReprocessamento> erros;
//
//    public ExceptionReprocessamentoContabil(List<MovimentosContabeisErroReprocessamento> erros) {
//        super("Não foi possível efetuar o reprocessamento.");
//        this.erros = erros;
//    }
//
//    public ExceptionReprocessamentoContabil(String message) {
//        super(message);
//    }
//
//    public List<MovimentosContabeisErroReprocessamento> getErros() {
//        return erros;
//    }
//
//    public void setErros(List<MovimentosContabeisErroReprocessamento> erros) {
//        this.erros = erros;
//    }
}
