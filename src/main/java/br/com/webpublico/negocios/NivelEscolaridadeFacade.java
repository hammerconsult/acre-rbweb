/*
 * Codigo gerado automaticamente em Tue Feb 08 13:52:56 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.NivelEscolaridade;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class NivelEscolaridadeFacade extends AbstractFacade<NivelEscolaridade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean existeNivelEscolaridadeDescricao(NivelEscolaridade nivel) {
        String hql = "from NivelEscolaridade nivel where trim(lower(nivel.descricao)) = :descricao ";
        if (nivel.getId() != null) {
            hql += " and nivel <> :nivel";
        }

        Query q = em.createQuery(hql);
        q.setParameter("descricao", nivel.getDescricao().trim().toLowerCase());
        if (nivel.getId() != null) {
            q.setParameter("nivel", nivel);
        }

        return !q.getResultList().isEmpty();
    }

    public Boolean existeNivelEscolaridadeOrdem(NivelEscolaridade nivel) {
        String hql = "from NivelEscolaridade nivel where nivel.ordem = :ordem";
        if (nivel.getId() != null) {
            hql += " and nivel.id <> :id";
        }

        Query q = em.createQuery(hql);
        q.setParameter("ordem", nivel.getOrdem());
        if (nivel.getId() != null) {
            q.setParameter("id", nivel.getId());
        }

        if (!q.getResultList().isEmpty()) {
            return true;
        }

        return false;
    }

    public NivelEscolaridadeFacade() {
        super(NivelEscolaridade.class);
    }

    public List<NivelEscolaridade> buscarTodosNiveisEscolaridadeOrdenadoPeloCodigo() {
        String hql = "from NivelEscolaridade nivel order by ordem";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }
}
