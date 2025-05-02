package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Favorito;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 01/03/14
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class FavoritoFacade extends AbstractFacade<Favorito> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FavoritoFacade() {
        super(Favorito.class);
    }


}
