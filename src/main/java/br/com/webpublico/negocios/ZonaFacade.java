package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Zona;
import br.com.webpublico.exception.ValidacaoException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ZonaFacade extends AbstractFacade<Zona> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ZonaFacade() {
        super(Zona.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasCodigoRegistrado(Zona zona) {
        String hql = "from Zona where codigo = : codigo";
        if (zona.getId() != null) {
            hql += " and id != :id ";
        }
        Query query = em.createQuery(hql);
        query.setParameter("codigo", zona.getCodigo());
        if (zona.getId() != null) {
            query.setParameter("id", zona.getId());
        }
        return !query.getResultList().isEmpty();
    }

    @Override
    public void preSave(Zona entidade) {
        if (hasCodigoRegistrado(entidade)) {
            throw new ValidacaoException("O código informado já está registrado.");
        }
    }

    public List<Zona> buscarZonas(String parte) {
        return em.createNativeQuery(" select * from zona z " +
                " where (trim(lower(z.sigla)) like :parte or trim(lower(z.descricao)) like :parte)" +
                " order by z.sigla ", Zona.class)
            .setParameter("parte", "%" + parte.trim().toLowerCase() + "%")
            .getResultList();
    }
}
