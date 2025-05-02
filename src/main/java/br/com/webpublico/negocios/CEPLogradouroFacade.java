/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CEPLogradouro;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Wanderley
 */
@Stateless
public class CEPLogradouroFacade extends AbstractFacade<CEPLogradouro> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public CEPLogradouroFacade() {
        super(CEPLogradouro.class);
    }

    public CEPLogradouro salvarCEPLogradouro(CEPLogradouro cepLogradouro){
        return em.merge(cepLogradouro);
    }
}
