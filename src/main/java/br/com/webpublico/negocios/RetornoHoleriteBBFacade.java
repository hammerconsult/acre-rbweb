package br.com.webpublico.negocios;

import br.com.webpublico.entidades.RetornoHoleriteBB;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RetornoHoleriteBBFacade extends AbstractFacade<RetornoHoleriteBB> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RetornoHoleriteBBFacade() {
        super(RetornoHoleriteBB.class);
    }

}
