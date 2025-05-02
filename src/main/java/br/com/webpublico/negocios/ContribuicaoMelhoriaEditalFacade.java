package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContribuicaoMelhoriaEdital;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by William on 28/06/2016.
 */
@Stateless
public class ContribuicaoMelhoriaEditalFacade extends AbstractFacade<ContribuicaoMelhoriaEdital> {

    @PersistenceContext(unitName = "webpublicoPU")
    public EntityManager em;

    public ContribuicaoMelhoriaEditalFacade() {
        super(ContribuicaoMelhoriaEdital.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
