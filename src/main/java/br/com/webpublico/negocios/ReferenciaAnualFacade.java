/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ReferenciaAnual;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Renato
 */
@Stateless
public class ReferenciaAnualFacade extends AbstractFacade<ReferenciaAnual> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public ReferenciaAnualFacade() {
        super(ReferenciaAnual.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean validaReferenciaAnualMesmoExercicio(ReferenciaAnual referenciaAnual) {
        String sql = " from ReferenciaAnual ref where ref.exercicio = :exercicio";
        if (referenciaAnual.getId() != null) {
            sql += " and ref.id <> :id ";
        }

        Query consulta = em.createQuery(sql);
        consulta.setParameter("exercicio", referenciaAnual.getExercicio());
        if (referenciaAnual.getId() != null) {
            consulta.setParameter("id", referenciaAnual.getId());
        }

        if (consulta.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

    public ReferenciaAnual recuperaReferenciaPorExercicio(Exercicio exercicio) {
        String sql = " from ReferenciaAnual ref where ref.exercicio = :exercicio";
        Query consulta = em.createQuery(sql);
        consulta.setParameter("exercicio", exercicio);
        if (!consulta.getResultList().isEmpty()) {
            return (ReferenciaAnual) consulta.getSingleResult();
        }
        return null;
    }
}
