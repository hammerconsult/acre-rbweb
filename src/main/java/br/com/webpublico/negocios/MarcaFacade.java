/*
 * Codigo gerado automaticamente em Tue Oct 18 14:33:52 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Marca;
import br.com.webpublico.entidades.Modelo;
import br.com.webpublico.enums.TipoMarca;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MarcaFacade extends AbstractFacade<Marca> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MarcaFacade() {
        super(Marca.class);
    }

    public List<Modelo> recuperarModelos(Marca marca) {
        String hql = " from Modelo m "
                + "   where m.marca = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", marca);

        return q.getResultList();
    }

    public List<Marca> recuperarMarcasPeloTipo(String parte, TipoMarca... tiposMarca) {
        String hql = " from Marca m"
                + "   where lower(m.descricao) like :parte"
                + "     and m.tipoMarca in (:tipoMarca)";

        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("tipoMarca", Lists.newArrayList(tiposMarca));

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public Marca recuperarMarcaPorDescricao(String filtro) {
        String sql = " select m.* " +
                "        from marca m " +
                "      where trim(Lower(m.descricao)) like :filtro";
        Query q = em.createNativeQuery(sql, Marca.class);
        try {
            q.setParameter("filtro", filtro.trim().toLowerCase());
            q.setMaxResults(1);
            return (Marca) q.getSingleResult();
        } catch (NoResultException | NullPointerException no) {
            return null;
        }
    }

    public Marca salvarMerge(Marca marca) {
        return em.merge(marca);
    }
}
