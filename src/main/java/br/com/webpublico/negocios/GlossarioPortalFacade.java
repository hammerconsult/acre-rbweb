package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.GlossarioPortal;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class GlossarioPortalFacade extends AbstractFacade<GlossarioPortal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public GlossarioPortalFacade() {
        super(GlossarioPortal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
