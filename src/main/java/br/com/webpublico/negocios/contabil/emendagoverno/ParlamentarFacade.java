package br.com.webpublico.negocios.contabil.emendagoverno;

import br.com.webpublico.entidades.contabil.emendagoverno.Parlamentar;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ParlamentarFacade extends AbstractFacade<Parlamentar> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParlamentarFacade() {
        super(Parlamentar.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
