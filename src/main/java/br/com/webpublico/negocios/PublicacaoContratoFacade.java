package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PublicacaoContrato;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PublicacaoContratoFacade extends AbstractFacade<PublicacaoContrato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;

    public PublicacaoContratoFacade() {
        super(PublicacaoContrato.class);
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
