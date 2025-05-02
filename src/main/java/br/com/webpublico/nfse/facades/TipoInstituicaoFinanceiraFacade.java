package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TipoInstituicaoFinanceira;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoInstituicaoFinanceiraFacade extends AbstractFacade<TipoInstituicaoFinanceira> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoInstituicaoFinanceiraFacade() {
        super(TipoInstituicaoFinanceira.class);
    }
}
