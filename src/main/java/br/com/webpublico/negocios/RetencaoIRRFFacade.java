/*
 * Codigo gerado automaticamente em Tue Jan 03 15:21:40 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.RetencaoIRRF;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class RetencaoIRRFFacade extends AbstractFacade<RetencaoIRRF> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RetencaoIRRFFacade() {
        super(RetencaoIRRF.class);
    }

    public List<RetencaoIRRF> listaFiltrandoLong(String s, String... atributos) {
        Long cod;
        try {
            cod = Long.parseLong(s);
        } catch (Exception e) {
            cod = 0l;
        }

        String hql = "from RetencaoIRRF retencao where "
                + " retencao.codigo = :codigo or ";
        for (String atributo : atributos) {
            hql += "lower(retencao." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigo", cod);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }
}
