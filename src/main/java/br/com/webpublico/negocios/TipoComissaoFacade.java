package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoComissaoExtraCurricular;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by mga on 01/06/2017.
 */
@Stateless
public class TipoComissaoFacade extends AbstractFacade<TipoComissaoExtraCurricular> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;


    public TipoComissaoFacade() {
        super(TipoComissaoExtraCurricular.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
