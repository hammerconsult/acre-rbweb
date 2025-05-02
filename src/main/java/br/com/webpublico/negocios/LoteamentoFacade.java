/*
 * Codigo gerado automaticamente em Fri Feb 11 14:03:19 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Loteamento;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class LoteamentoFacade extends AbstractFacade<Loteamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LoteamentoFacade() {
        super(Loteamento.class);
    }

    public boolean existeLoteamentoPorCodigo(Long id, Long codigo) {
        StringBuilder sql = new StringBuilder("select * from loteamento where codigo = :codigo");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString(), Loteamento.class);
        q.setParameter("codigo", codigo);
        if (id != null) {
            q.setParameter("id", id);
        }

        return !q.getResultList().isEmpty();
    }

    public Loteamento recuperaPorCodigo(Integer codigo) {
        StringBuilder sql = new StringBuilder("from Loteamento where codigo = :codigo");
        Query q = em.createQuery(sql.toString());
        q.setParameter("codigo", codigo.longValue());
        if (!q.getResultList().isEmpty()) {
            return (Loteamento) q.getResultList().get(0);
        }
        return null;
    }
}
