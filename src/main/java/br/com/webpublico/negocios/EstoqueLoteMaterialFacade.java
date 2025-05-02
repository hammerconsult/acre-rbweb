/*
 * Codigo gerado automaticamente em Wed Jul 20 14:29:10 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Estoque;
import br.com.webpublico.entidades.EstoqueLoteMaterial;
import br.com.webpublico.entidades.LocalEstoqueOrcamentario;
import br.com.webpublico.entidades.LoteMaterial;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class EstoqueLoteMaterialFacade extends AbstractFacade<EstoqueLoteMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstoqueLoteMaterialFacade() {
        super(EstoqueLoteMaterial.class);
    }

    public EstoqueLoteMaterial recuperarEstoqueLoteMaterial(Estoque estoque, LoteMaterial loteMaterial) {
        String hql = "    select elm "
                + "         from EstoqueLoteMaterial elm "
                + "   inner join elm.estoque e "
                + "   inner join elm.loteMaterial lm "
                + "        where e = :estoque "
                + "          and lm = :loteMaterial";

        Query q = em.createQuery(hql);
        q.setParameter("estoque", estoque);
        q.setParameter("loteMaterial", loteMaterial);

        try {
            return (EstoqueLoteMaterial) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public EstoqueLoteMaterial recuperarEstoqueLoteMaterial(LoteMaterial loteMaterial, LocalEstoqueOrcamentario local) {
        String hql = "    select elm "
                + "         from EstoqueLoteMaterial elm "
                + "   inner join elm.loteMaterial lm "
                + "   inner join elm.estoque e "
                + "        where lm = :param"
                + "          and e.localEstoqueOrcamentario = :local"
                + "     order by e.dataEstoque desc";

        Query q = em.createQuery(hql);
        q.setParameter("param", loteMaterial);
        q.setParameter("local", local);
        q.setMaxResults(1);

        try {
            return (EstoqueLoteMaterial) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<EstoqueLoteMaterial> recuperarTodosEstoquesLoteDeUmEstoque(Estoque estoque) {
        String hql = "    select elm "
                + "         from EstoqueLoteMaterial elm "
                + "   inner join elm.estoque e "
                + "        where e = :estoque ";

        Query q = em.createQuery(hql);
        q.setParameter("estoque", estoque);
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
