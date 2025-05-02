/*
 * Codigo gerado automaticamente em Wed Apr 13 11:10:12 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PublicoAlvo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PublicoAlvoFacade extends AbstractFacade<PublicoAlvo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PublicoAlvoFacade() {
        super(PublicoAlvo.class);
    }
}
