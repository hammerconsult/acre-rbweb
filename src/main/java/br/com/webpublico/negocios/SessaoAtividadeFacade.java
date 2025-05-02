/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.SessaoAtividade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Camila
 */
@Stateless
public class SessaoAtividadeFacade extends AbstractFacade<SessaoAtividade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public SessaoAtividadeFacade() {
        super(SessaoAtividade.class);
    }

    @Override
    public SessaoAtividade recuperar(Object id) {
        SessaoAtividade obj = super.recuperar(id);
        Hibernate.initialize(obj.getSessaoAtividadeCnaes());
        return obj;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}


