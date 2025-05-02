/*
 * Codigo gerado automaticamente em Tue Apr 17 10:59:29 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Tribunal;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TribunalFacade extends AbstractFacade<Tribunal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TribunalFacade() {
        super(Tribunal.class);
    }
}
