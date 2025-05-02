/*
 * Codigo gerado automaticamente em Tue Feb 15 14:19:22 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EventoRedeSim;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class SincronizadorRedeSimFacade extends AbstractFacade<EventoRedeSim> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SincronizadorRedeSimFacade() {
        super(EventoRedeSim.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EventoRedeSim recuperar(Object id) {
        return em.find(EventoRedeSim.class, id);
    }
}
