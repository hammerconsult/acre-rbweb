package br.com.webpublico.negocios.rh.administracaodepagamento;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by William on 07/02/2019.
 */
@Stateless
public class EventoFPSimplificadoFacade extends AbstractFacade<EventoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public EventoFPSimplificadoFacade() {
        super(EventoFP.class);
    }

    @Override
    public EventoFP recuperar(Object id) {
        EventoFP evento = em.find(EventoFP.class, id);
        Hibernate.initialize(evento.getTiposFolha());
        return evento;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
