/*
 * Codigo gerado automaticamente em Tue Jan 03 15:36:34 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CategoriaSEFIP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class CategoriaSEFIPFacade extends AbstractFacade<CategoriaSEFIP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaSEFIPFacade() {
        super(CategoriaSEFIP.class);
    }

    public List<CategoriaSEFIP> listaFiltrandoLong(String s, String... atributos) {
        Long cod;
        try {
            cod = Long.parseLong(s);
        } catch (Exception e) {
            cod = 0l;
        }

        String hql = "from CategoriaSEFIP categoria where "
                + " (categoria.codigo = :codigo) or (";
        for (String atributo : atributos) {
            hql += "lower(categoria." + atributo + ") like :filtro) OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        //System.out.println(hql);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigo", cod);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<CategoriaSEFIP> buscarCategoriaSefip(String filtro) {
        String sql = " select cs.* from CategoriaSefip cs" +
            "          where cs.descricao like :filtro or cs.codigo like :filtro ";
        Query q = em.createNativeQuery(sql, CategoriaSEFIP.class);
        q.setParameter("filtro", "%" + filtro.trim().toUpperCase() + "%");
        return q.getResultList();
    }
}
