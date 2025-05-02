package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Funeraria;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
@Stateless
public class FunerariaFacade extends AbstractFacade<Funeraria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public FunerariaFacade() {
        super(Funeraria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
