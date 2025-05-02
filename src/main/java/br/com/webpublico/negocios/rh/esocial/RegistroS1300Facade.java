package br.com.webpublico.negocios.rh.esocial;


import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RegistroS1300Facade extends AbstractFacade<RegistroEventoEsocial> {

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RegistroS1300Facade() {
        super(RegistroEventoEsocial.class);
    }

    @Override
    public RegistroEventoEsocial recuperar(Object id) {
        RegistroEventoEsocial registro = super.recuperar(id);
        Hibernate.initialize(registro.getItemVinculoFP());
        return registro;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
