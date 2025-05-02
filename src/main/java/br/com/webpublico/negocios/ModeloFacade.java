/*
 * Codigo gerado automaticamente em Tue Oct 18 14:56:33 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Marca;
import br.com.webpublico.entidades.Modelo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ModeloFacade extends AbstractFacade<Modelo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ModeloFacade() {
        super(Modelo.class);
    }

    public List<Modelo> listaPorMarca(String s, Marca m) {
        String hql = "from Modelo mod"
                + "  where mod.marca = :marca " +
                "      and lower(mod.descricao) like :desc ";

        Query q = (Query) getEntityManager().createQuery(hql);
        q.setParameter("marca", m);
        q.setParameter("desc", "%" + s.trim().toLowerCase() + "%");

        return q.getResultList();
    }

    public Modelo recuperarModeloPorDescricaoEhMarca(String filtro, Marca marca) {
        if (marca == null) {
            return null;
        }
        String sql = " select m.* " +
                "        from modelo m " +
                "     where trim(Lower(m.descricao)) like :filtro" +
                "       and m.marca_id = :id_marca";
        Query q = em.createNativeQuery(sql, Modelo.class);
        try {
            q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
            q.setParameter("id_marca", marca.getId());
            q.setMaxResults(1);
            return (Modelo) q.getSingleResult();
        } catch (NullPointerException | NoResultException ex) {
            return null;
        }
    }

    public Modelo salvarMerge(Modelo modelo) {
        return em.merge(modelo);
    }
}
