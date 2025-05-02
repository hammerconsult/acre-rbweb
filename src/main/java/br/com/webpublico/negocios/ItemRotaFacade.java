/*
 * Codigo gerado automaticamente em Wed Mar 09 10:31:39 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemRota;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ItemRotaFacade extends AbstractFacade<ItemRota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemRotaFacade() {
        super(ItemRota.class);
    }


}
