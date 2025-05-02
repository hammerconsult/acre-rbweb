/*
 * Codigo gerado automaticamente em Mon Jun 25 20:29:45 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.entidades.ItemEventoCLP;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ItemEventoCLPFacade extends AbstractFacade<ItemEventoCLP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CLPFacade clpFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ClpHistoricoContabilFacade clpHistoricoContabilFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemEventoCLPFacade() {
        super(ItemEventoCLP.class);
    }

    public CLPFacade getClpFacade() {
        return clpFacade;
    }


    public void salvar(ItemEventoCLP entity, EventoContabil ev) {
        super.salvar(entity);
        eventoContabilFacade.salvar(ev);
    }


    public List<ItemEventoCLP> getListaItens(EventoContabil ec) {
        String sql = "SELECT * FROM ITEMEVENTOCLP ITEM "
                + " WHERE ITEM.EVENTOCONTABIL_ID = :param AND ITEM.DATAVIGENCIA IS null";
        Query q = em.createNativeQuery(sql, ItemEventoCLP.class);
        q.setParameter("param", ec.getId());
        return q.getResultList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ClpHistoricoContabilFacade getClpHistoricoContabilFacade() {
        return clpHistoricoContabilFacade;
    }


}
