/*
 * Codigo gerado automaticamente em Mon Oct 24 17:56:04 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PecaObjetoFrota;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PecaObjetoFrotaFacade extends AbstractFacade<PecaObjetoFrota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PecaObjetoFrotaFacade() {
        super(PecaObjetoFrota.class);
    }

    public List<PecaObjetoFrota> buscarPecas(String s) {
        String hql = "from " + PecaObjetoFrota.class.getSimpleName() + " peca where ";
        hql += "lower(peca.descricao) like :filtro";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s + "%");
        return q.getResultList();
    }
}
