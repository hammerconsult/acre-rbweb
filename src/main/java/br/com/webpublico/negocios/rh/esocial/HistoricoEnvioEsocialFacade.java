package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.HistoricoEnvioEsocial;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by William on 25/05/2018.
 */
@Stateless
public class HistoricoEnvioEsocialFacade extends AbstractFacade<HistoricoEnvioEsocial> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    public HistoricoEnvioEsocialFacade() {
        super(HistoricoEnvioEsocial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public HistoricoEnvioEsocial recuperar(Object id) {
        HistoricoEnvioEsocial entity = super.recuperar(id);
        Hibernate.initialize(entity.getItemHistoricoEnvios());
        return entity;
    }
}
