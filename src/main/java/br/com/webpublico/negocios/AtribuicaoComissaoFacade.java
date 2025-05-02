package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AtribuicaoComissaoExtraCurricular;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by mga on 01/06/2017.
 */
@Stateless
public class AtribuicaoComissaoFacade extends AbstractFacade<AtribuicaoComissaoExtraCurricular> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;


    public AtribuicaoComissaoFacade() {
        super(AtribuicaoComissaoExtraCurricular.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
