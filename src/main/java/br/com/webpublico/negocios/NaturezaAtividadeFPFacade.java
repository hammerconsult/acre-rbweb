/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.NaturezaAtividadeFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author peixe
 */
@Stateless
public class NaturezaAtividadeFPFacade extends AbstractFacade<NaturezaAtividadeFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NaturezaAtividadeFPFacade() {
        super(NaturezaAtividadeFP.class);
    }

    public List<NaturezaAtividadeFP> listaNaturezaAtividadePorCargo(Cargo cargo) {
        Query q = em.createQuery("from NaturezaAtividadeFP n where n.cargo = :cargo");
        q.setParameter("cargo", cargo);
        return q.getResultList();
    }
}
