/*
 * Codigo gerado automaticamente em Tue Apr 17 14:27:35 BRT 2012
 * Gerador de Facace
*/

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.UnidadeJudiciaria;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UnidadeJudiciariaFacade extends AbstractFacade<UnidadeJudiciaria> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadeJudiciariaFacade() {
        super(UnidadeJudiciaria.class);
    }
}
