/*
 * Codigo gerado automaticamente em Tue Feb 22 10:48:40 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CategoriaServico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class CategoriaServicoFacade extends AbstractFacade<CategoriaServico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaServicoFacade() {
        super(CategoriaServico.class);
    }

    public boolean codigoJaExite(CategoriaServico selecionado) {
        String hql = "from CategoriaServico c where c.codigo = :codigo ";
        if (selecionado.getId() != null) {
            hql += "and c != :categoria";
        }
        Query q = em.createQuery(hql);
        if (selecionado.getId() != null) {
            q.setParameter("categoria", selecionado);
        }
        q.setParameter("codigo", selecionado.getCodigo());
        return !q.getResultList().isEmpty();
    }
}
