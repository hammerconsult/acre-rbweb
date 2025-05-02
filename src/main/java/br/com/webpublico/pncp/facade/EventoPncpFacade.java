/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.pncp.facade;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.pncp.entidade.EventoPncp;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class EventoPncpFacade extends AbstractFacade<EventoPncp> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public EventoPncpFacade() {
        super(EventoPncp.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoPncp recuperarUltimoEvento(Long idOrigem) {
        try {
            String sql = " select eve.* from eventopncp eve " +
                "            where eve.idOrigem = :idOrigem " +
                "           order by eve.data desc ";
            Query q = em.createNativeQuery(sql, EventoPncp.class);
            q.setParameter("idOrigem", idOrigem);
            q.setMaxResults(1);
            return (EventoPncp) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<EventoPncp> buscarTodos() {
        try {
            String sql = " select * from eventopncp eve order by eve.data desc ";
            Query q = em.createNativeQuery(sql, EventoPncp.class);
            List<EventoPncp> resultList = q.getResultList();
            for (EventoPncp eventoPncp : resultList) {
                Hibernate.initialize(eventoPncp.getLogs());
                Hibernate.initialize(eventoPncp.getEnvios());
            }
            return resultList;
        } catch (Exception ex) {
            return Lists.newArrayList();
        }
    }

    public List<EventoPncp> buscarPorIdOrigem(Long idOrigem) {
        try {
            String sqlString = "select * from eventopncp eve where eve.idorigem = :idOrigem order by eve.data desc";
            Query q = em.createNativeQuery(sqlString, EventoPncp.class);
            q.setParameter("idOrigem", idOrigem);
            List<EventoPncp> resultList = q.getResultList();
            for (EventoPncp eventoPncp : resultList) {
                Hibernate.initialize(eventoPncp.getLogs());
                Hibernate.initialize(eventoPncp.getEnvios());
            }
            return resultList;
        } catch (Exception ex) {
            return Lists.newArrayList();
        }
    }
}


