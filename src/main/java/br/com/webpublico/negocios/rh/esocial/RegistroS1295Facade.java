package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.RegistroS1295;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RegistroS1295Facade extends AbstractFacade<RegistroS1295> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    public RegistroS1295Facade() {
        super(RegistroS1295.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }
}
