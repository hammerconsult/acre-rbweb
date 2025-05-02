package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.PaginaMenuHorizontalPaginaPrefeitura;
import br.com.webpublico.controle.portaltransparencia.entidades.PaginaMenuHorizontalPortal;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PaginaMenuHorizontalPortalFacade extends AbstractFacade<PaginaMenuHorizontalPortal> {

    public PaginaMenuHorizontalPortalFacade() {
        super(PaginaMenuHorizontalPortal.class);
    }

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    @Override
    public PaginaMenuHorizontalPortal recuperar(Object id) {
        PaginaMenuHorizontalPortal entity = super.recuperar(id);
        Hibernate.initialize(entity.getPaginasPrefeituraPortal());
        for (PaginaMenuHorizontalPaginaPrefeitura paginaMenuHorizontal : entity.getPaginasPrefeituraPortal()) {
            Hibernate.initialize(paginaMenuHorizontal.getSubPaginas());
        }
        return entity;
    }

    public PaginaMenuHorizontalPortal salvarNovaPaginaMenuHorizontal(PaginaMenuHorizontalPortal entity) {
        return em.merge(entity);
    }
}
