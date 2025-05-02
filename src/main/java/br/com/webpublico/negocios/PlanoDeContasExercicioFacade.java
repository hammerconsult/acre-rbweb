/*
 * Codigo gerado automaticamente em Fri Apr 15 09:21:53 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PlanoDeContasExercicio;
import br.com.webpublico.entidades.SubConta;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PlanoDeContasExercicioFacade extends AbstractFacade<PlanoDeContasExercicio> {

    @EJB
    private PlanoDeContasFacade PlanoDeContasFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlanoDeContasExercicioFacade() {
        super(PlanoDeContasExercicio.class);
    }

    public PlanoDeContasExercicio recuperarPorExercicio(Exercicio exercicio) {
        if (exercicio != null) {
            String hql = "from " + super.getClasse().getSimpleName() + " obj where obj.exercicio = " + exercicio.getId();
            Query q = em.createQuery(hql);
            try {
                return (PlanoDeContasExercicio) q.getSingleResult();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public boolean validarParaMesmoExercicio(PlanoDeContasExercicio planoDeContasExercicio, String exercicio) {
        String sql = " select pce.* from planodecontasexercicio pce"
                + "    inner join exercicio ex on ex.id = pce.exercicio_id "
                + "    where ex.ano = :exercicio";
        if (planoDeContasExercicio.getId() != null) {
            sql += " and pce.id <> :id ";
        }

        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("exercicio", exercicio);
        if (planoDeContasExercicio.getId() != null) {
            consulta.setParameter("id", planoDeContasExercicio.getId());
        }

        if (consulta.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public PlanoDeContasFacade getPlanoDeContasFacade() {
        return PlanoDeContasFacade;
    }
}
