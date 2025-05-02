/*
 * Codigo gerado automaticamente em Wed Nov 23 20:17:02 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItinerarioViagem;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ItinerarioViagemFacade extends AbstractFacade<ItinerarioViagem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItinerarioViagemFacade() {
        super(ItinerarioViagem.class);
    }
}
