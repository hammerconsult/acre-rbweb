/*
 * Codigo gerado automaticamente em Tue Dec 06 14:04:16 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoRetencao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoRetencaoFacade extends AbstractFacade<TipoRetencao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoRetencaoFacade() {
        super(TipoRetencao.class);
    }

    public List<TipoRetencao> listaPorCodigo(String parte) {
        Query q = em.createQuery("from TipoRetencao where codigo in (:codigo)");
        q.setParameter("codigo", Integer.parseInt(parte));
        q.setMaxResults(50);
        return q.getResultList();
    }
}
