package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TemplateNfse;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@Stateless
public class TemplateNfseFacade extends AbstractFacade<TemplateNfse> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public TemplateNfseFacade() {
        super(TemplateNfse.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TemplateNfse buscarPorTipo(TipoTemplateNfse tipo) {
        Query q = em.createQuery("from TemplateNfse where tipoTemplate = :tipo");
        q.setParameter("tipo", tipo);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (TemplateNfse) q.getResultList().get(0);
        }
        return null;

    }

}
