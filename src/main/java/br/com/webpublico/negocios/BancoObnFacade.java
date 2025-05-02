package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BancoObn;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by israeleriston on 19/05/16.
 */
@Stateless
public class BancoObnFacade extends AbstractFacade<BancoObn> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    /**
     * Deve ser executado no construtor da subclasse para indicar qual é a
     * classe da entidade
     *
     * @param classe Classe da entidade
     */
    public BancoObnFacade() {
        super(BancoObn.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
