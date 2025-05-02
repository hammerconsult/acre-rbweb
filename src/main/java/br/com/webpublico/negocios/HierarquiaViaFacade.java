package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HierarquiaVia;
import br.com.webpublico.exception.ValidacaoException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class HierarquiaViaFacade extends AbstractFacade<HierarquiaVia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public HierarquiaViaFacade() {
        super(HierarquiaVia.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasCodigoRegistrado(HierarquiaVia hierarquiaVia) {
        String hql = "from HierarquiaVia where codigo = : codigo";
        if (hierarquiaVia.getId() != null) {
            hql += " and id != :id ";
        }
        Query query = em.createQuery(hql);
        query.setParameter("codigo", hierarquiaVia.getCodigo());
        if (hierarquiaVia.getId() != null) {
            query.setParameter("id", hierarquiaVia.getId());
        }
        return !query.getResultList().isEmpty();
    }

    @Override
    public void preSave(HierarquiaVia entidade) {
        if (hasCodigoRegistrado(entidade)) {
            throw new ValidacaoException("O código informado já está registrado.");
        }
    }
}
