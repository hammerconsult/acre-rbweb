/*
 * Codigo gerado automaticamente em Wed Jan 04 10:38:40 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TomadorDeServico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TomadorDeServicoFacade extends AbstractFacade<TomadorDeServico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TomadorDeServicoFacade() {
        super(TomadorDeServico.class);
    }

    public boolean verificaTomador(TomadorDeServico tomador) {
        String hql = "";
        hql = "from TomadorDeServico t where t.tomador =:tomador";

        Query q = em.createQuery(hql);
        q.setParameter("tomador", tomador.getTomador());
        return q.getResultList().isEmpty();
    }

    public List<TomadorDeServico> listaFiltrandoAtributosTomador(String s, String... atributos) {
        String hql = "from TomadorDeServico t where ";
        for (String atributo : atributos) {
            hql += "lower(t.tomador." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

}
