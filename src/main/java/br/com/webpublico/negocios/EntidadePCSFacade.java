/*
 * Codigo gerado automaticamente em Sat Jul 02 09:02:36 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EntidadePCS;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class EntidadePCSFacade extends AbstractFacade<EntidadePCS> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntidadePCSFacade() {
        super(EntidadePCS.class);
    }
}
