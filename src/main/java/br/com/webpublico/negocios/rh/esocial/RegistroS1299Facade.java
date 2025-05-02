package br.com.webpublico.negocios.rh.esocial;


import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocialS1299;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RegistroS1299Facade extends AbstractFacade<RegistroEventoEsocialS1299> {

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RegistroS1299Facade() {
        super(RegistroEventoEsocialS1299.class);
    }

    @Override
    public RegistroEventoEsocialS1299 recuperar(Object id) {
        RegistroEventoEsocialS1299 registro = super.recuperar(id);
        return registro;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
