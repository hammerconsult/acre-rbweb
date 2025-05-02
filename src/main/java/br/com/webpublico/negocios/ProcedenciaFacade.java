package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Procedencia;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
@Stateless
public class ProcedenciaFacade extends AbstractFacade<Procedencia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProcedenciaFacade() {
        super(Procedencia.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
