package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RegistroS1298Facade extends AbstractFacade<RegistroEventoEsocial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;

    public RegistroS1298Facade() {
        super(RegistroEventoEsocial.class);
    }

    @Override
    public RegistroEventoEsocial recuperar(Object id) {
        RegistroEventoEsocial registro = em.find(getClasse(), id);
        Hibernate.initialize(registro.getItemVinculoFP());
        return registro;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }
}
