package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ProdutoServicoBancario;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ProdutoServicoBancarioFacade extends AbstractFacade<ProdutoServicoBancario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProdutoServicoBancarioFacade() {
        super(ProdutoServicoBancario.class);
    }
}
