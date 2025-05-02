/*
 * Codigo gerado automaticamente em Fri Apr 29 14:53:23 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PPA;
import br.com.webpublico.entidades.ReceitaExercicioPPA;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ReceitaExercicioPPAFacade extends AbstractFacade<ReceitaExercicioPPA> {

    @EJB
    private ContaFacade contaFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReceitaExercicioPPAFacade() {
        super(ReceitaExercicioPPA.class);
    }

    public List<Conta> listaContaReceita(Exercicio e) {
        long id;
        if (e != null) {
            id = e.getId();
        } else {
            id = -1;
        }
        String hql = "select z from Conta z, PlanoDeContasExercicio y ";
        hql += " where z.planoDeContas = y.planoDeReceitas ";
        hql += " and y.exercicio = " + id;
        //  hql += " and z.id not in (select r.contaDeReceita FROM ReceitaExercicioPPA r where r.exercicio = " + id + ") ORDER BY z";
        Query q = em.createQuery(hql);
        //System.out.println("Tudooo Conta: : " + q.getResultList());
        return q.getResultList();
    }

    public List<ReceitaExercicioPPA> listaIdPPA(Long id, Long ppa) {

        String hql = "from ReceitaExercicioPPA obj where obj.exercicio = " + id + " and obj.ppa = " + ppa;
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }

    public List<ReceitaExercicioPPA> recuperaReceitasPorPPa(PPA ppa) {
        List<ReceitaExercicioPPA> recuperaReceitas = new ArrayList<ReceitaExercicioPPA>();
        String hql = "from ReceitaExercicioPPA r where r.ppa = :ppa";
        if (ppa != null) {
            Query q = em.createQuery(hql);
            q.setParameter("ppa", ppa);
            recuperaReceitas = q.getResultList();
        } else {
            recuperaReceitas = new ArrayList<ReceitaExercicioPPA>();
        }
        return recuperaReceitas;
    }

    public ReceitaExercicioPPA recuperar(ReceitaExercicioPPA id) {
        try {
            ReceitaExercicioPPA r = em.find(ReceitaExercicioPPA.class, id.getId());
            contaFacade.recuperar(r.getContaDeReceita());
            return r;
        } catch (Exception e) {
            return null;
        }
    }
}
