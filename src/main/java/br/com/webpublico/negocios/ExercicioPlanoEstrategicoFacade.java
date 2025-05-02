/*
 * Codigo gerado automaticamente em Fri Apr 15 15:39:56 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ExercicioPlanoEstrategico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ExercicioPlanoEstrategicoFacade extends AbstractFacade<ExercicioPlanoEstrategico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExercicioPlanoEstrategicoFacade() {
        super(ExercicioPlanoEstrategico.class);
    }
}
