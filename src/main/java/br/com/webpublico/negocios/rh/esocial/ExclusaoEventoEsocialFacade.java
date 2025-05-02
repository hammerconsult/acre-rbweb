package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.ExclusaoEventoEsocial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by William on 30/11/2018.
 */
@Stateless
public class ExclusaoEventoEsocialFacade extends AbstractFacade<ExclusaoEventoEsocial> {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ExclusaoEventoEsocialFacade() {
        super(ExclusaoEventoEsocial.class);
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }

    @Override
    public ExclusaoEventoEsocial recuperar(Object id) {
        ExclusaoEventoEsocial exclusao = super.recuperar(id);
        Hibernate.initialize(exclusao.getItemExclusao());
        return exclusao;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
