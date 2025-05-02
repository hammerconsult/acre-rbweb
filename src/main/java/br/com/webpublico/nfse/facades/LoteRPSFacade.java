package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.LoteRPS;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class LoteRPSFacade extends AbstractFacade<LoteRPS> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LoteRPSFacade() {
        super(LoteRPS.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public LoteRPS recuperar(Object id) {
        LoteRPS recuperar = super.recuperar(id);
        Hibernate.initialize(recuperar.getListaRps());
        return recuperar;
    }
}
