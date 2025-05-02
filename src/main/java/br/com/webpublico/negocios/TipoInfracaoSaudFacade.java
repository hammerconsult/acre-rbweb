package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoInfracaoSaud;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoInfracaoSaudFacade extends AbstractFacade<TipoInfracaoSaud> {

    @PersistenceContext
    private EntityManager em;

    public TipoInfracaoSaudFacade() {
        super(TipoInfracaoSaud.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
