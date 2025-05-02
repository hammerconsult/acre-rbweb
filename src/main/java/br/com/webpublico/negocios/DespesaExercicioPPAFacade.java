/*
 * Codigo gerado automaticamente em Tue Apr 26 17:48:46 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.DespesaExercicioPPA;
import br.com.webpublico.entidades.Exercicio;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class DespesaExercicioPPAFacade extends AbstractFacade<DespesaExercicioPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DespesaExercicioPPAFacade() {
        super(DespesaExercicioPPA.class);
    }

    public List<Conta> listaContaDespesa(Exercicio e) {

        String hql = "select z from Conta z, PlanoDeContasExercicio y ";
        hql += "where z.planoDeContas = y.planoDeDespesas ";
        hql += "and y.exercicio =:paramExerc ORDER BY z";
        //  hql += " and z.id not in (select r.contaDeDespesa FROM DespesaExercicioPPA r where r.exercicio = " + id + ") ORDER BY z";
        Query q = em.createQuery(hql);
        q.setParameter("paramExerc", e);
        return q.getResultList();
    }

    public List<DespesaExercicioPPA> listaIdPPA(Long id, Long ppa) {

        String hql = "from DespesaExercicioPPA obj where obj.exercicio = " + id + " and obj.ppa = " + ppa;
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }

    @Override
    public DespesaExercicioPPA recuperar(Object id) {
        try {
            DespesaExercicioPPA d = em.find(DespesaExercicioPPA.class, id);
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
