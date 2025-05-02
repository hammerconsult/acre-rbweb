package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoAutonomo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author daniel
 */
@Stateless
public class TipoAutonomoFacade extends AbstractFacade<TipoAutonomo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoAutonomoFacade() {
        super(TipoAutonomo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<TipoAutonomo> lista() {
        String hql = "from TipoAutonomo tipo order by tipo.descricao";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public boolean jaExisteCodigo(TipoAutonomo tipoAutonomo) {
        if (tipoAutonomo == null || tipoAutonomo.getCodigo() == null) {
            return false;
        }
        String hql = "from TipoAutonomo ta where ta.codigo = :codigo";
        if (tipoAutonomo.getId() != null) {
            hql += " and ta <> :tipoAutonomo";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", tipoAutonomo.getCodigo());
        if (tipoAutonomo.getId() != null) {
            q.setParameter("tipoAutonomo", tipoAutonomo);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean jaExisteDescricao(TipoAutonomo tipoAutonomo) {
        if (tipoAutonomo == null || tipoAutonomo.getCodigo() == null) {
            return false;
        }
        String hql = "from TipoAutonomo ta where lower(ta.descricao) = :descricao";
        if (tipoAutonomo.getId() != null) {
            hql += " and ta <> :tipoAutonomo";
        }
        Query q = em.createQuery(hql);
        q.setParameter("descricao", tipoAutonomo.getDescricao().toLowerCase().trim());
        if (tipoAutonomo.getId() != null) {
            q.setParameter("tipoAutonomo", tipoAutonomo);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean usadoPorBCE(TipoAutonomo selecionado) {
        return false;
    }

    public List<TipoAutonomo> completaTipoAutonomo(String parte) {
        String hql = " from TipoAutonomo" +
            "     where to_char(codigo) like :filtro" +
            "        or lower(descricao) like :filtro";

        return em.createQuery(hql).setParameter("filtro", "%" + parte.trim().toLowerCase() + "%").setMaxResults(10).getResultList();
    }
}
