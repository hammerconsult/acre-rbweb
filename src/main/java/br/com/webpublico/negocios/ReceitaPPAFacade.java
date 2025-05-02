/*
 * Codigo gerado automaticamente em Fri Feb 11 08:09:57 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ReceitaPPA;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ReceitaPPAFacade extends AbstractFacade<ReceitaPPA> {

    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReceitaPPAFacade() {
        super(ReceitaPPA.class);
    }

    @Override
    public ReceitaPPA recuperar(Object id) {
        ReceitaPPA rppa = em.find(ReceitaPPA.class, id);
        rppa.getReceitaPPAContas().size();
        return rppa;
    }

    public PPAFacade getPpaFacade() {
        return ppaFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }
}
