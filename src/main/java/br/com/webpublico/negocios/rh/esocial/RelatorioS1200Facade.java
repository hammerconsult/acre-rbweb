package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.negocios.AbstractFacade;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class RelatorioS1200Facade extends AbstractFacade<RegistroESocial> {

    public RelatorioS1200Facade() {
        super(RegistroESocial.class);
    }

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


}
