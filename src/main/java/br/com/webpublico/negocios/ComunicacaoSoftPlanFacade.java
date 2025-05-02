package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ComunicacaoSoftPlan;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ComunicacaoSoftPlanFacade extends AbstractFacade<ComunicacaoSoftPlan> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ComunicacaoSoftPlanFacade() {
        super(ComunicacaoSoftPlan.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
