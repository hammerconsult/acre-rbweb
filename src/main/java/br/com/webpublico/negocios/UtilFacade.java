/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Munif
 */
@Stateless
public class UtilFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


}
