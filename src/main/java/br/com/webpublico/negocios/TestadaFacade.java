/*
 * Codigo gerado automaticamente em Fri Feb 11 08:42:00 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Lote;
import br.com.webpublico.entidades.Testada;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TestadaFacade extends AbstractFacade<Testada> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TestadaFacade() {
        super(Testada.class);
    }

    public List<Testada> listaTestadasDoLote(Lote lote) {
        Query q = em.createQuery("from Testada t where t.lote = :lote").setParameter("lote", lote);
        return q.getResultList();
    }
}
