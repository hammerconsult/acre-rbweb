/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.exception;

import javax.ejb.EJBException;

/**
 * @author MGA
 */
public class ConflitoDeDividasDentroDoExercicio extends EJBException {

    public ConflitoDeDividasDentroDoExercicio(String message) {
        super(message);
    }

}
