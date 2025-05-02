package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ProfissionalConfea;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by venom on 03/11/14.
 */
@Stateless
public class ProfissionalConfeaFacade extends AbstractFacade<ProfissionalConfea> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;

    public ProfissionalConfeaFacade() {
        super(ProfissionalConfea.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
