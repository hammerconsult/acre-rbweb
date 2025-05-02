package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MotivoIsencao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by venom on 16/10/14.
 */
@Stateless
public class MotivoIsencaoFacade extends AbstractFacade<MotivoIsencao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public MotivoIsencaoFacade() {
        super(MotivoIsencao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
