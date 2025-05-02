/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.SuplementacaoORC;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author claudio
 */
@Stateless
public class SuplementacaoOrcFacade extends AbstractFacade<SuplementacaoORC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SuplementacaoOrcFacade() {
        super(SuplementacaoORC.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SuplementacaoORC recuperar(Object id) {
        SuplementacaoORC sup = em.find(SuplementacaoORC.class, id);
        return sup;
    }

    public SuplementacaoORC salvarRetornando(SuplementacaoORC suplementacaoORC) {
        em.merge(suplementacaoORC);
        return suplementacaoORC;
    }
}
