/*
 * Codigo gerado automaticamente em Wed Sep 07 10:28:33 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemBaseFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ItemBaseFPFacade extends AbstractFacade<ItemBaseFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemBaseFPFacade() {
        super(ItemBaseFP.class);
    }
}
