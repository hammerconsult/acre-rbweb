package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PublicacaoAlteracaoContratual;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PublicacaoAlteracaoContratualFacade extends AbstractFacade<PublicacaoAlteracaoContratual> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;

    public PublicacaoAlteracaoContratualFacade() {
        super(PublicacaoAlteracaoContratual.class);
    }

    public AlteracaoContratualFacade getAlteracaoContratualFacade() {
        return alteracaoContratualFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
