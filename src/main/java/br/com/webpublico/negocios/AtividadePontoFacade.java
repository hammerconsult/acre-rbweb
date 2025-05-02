/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AtividadePonto;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class AtividadePontoFacade extends AbstractFacade<AtividadePonto> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AtividadePontoFacade() {
        super(AtividadePonto.class);
    }

    public boolean codigoJaExiste(AtividadePonto ap) {
        String hql = "from AtividadePonto ap where ap.codigo = :codigo";
        if (ap.getId() != null) {
            hql += " and ap != :ap";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", ap.getCodigo());
        if (ap.getId() != null) {
            q.setParameter("ap", ap);
        }
        return !q.getResultList().isEmpty();
    }

    public List<AtividadePonto> listaOrdenando(String ordenacao) {
        Query q = em.createQuery(" from AtividadePonto obj order by " + ordenacao);
        return q.getResultList();
    }
}
