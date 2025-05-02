package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ExceptionLog;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Gustavo
 */
@Stateless
public class ExceptionLogFacade extends AbstractFacade<ExceptionLog> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExceptionLogFacade() {
        super(ExceptionLog.class);
    }

}
