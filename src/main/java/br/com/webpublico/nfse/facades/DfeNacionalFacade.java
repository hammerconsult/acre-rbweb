package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.DfeNacional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DfeNacionalFacade extends AbstractFacade<DfeNacional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public DfeNacionalFacade() {
        super(DfeNacional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
