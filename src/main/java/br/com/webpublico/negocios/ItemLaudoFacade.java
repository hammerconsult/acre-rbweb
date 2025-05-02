/*
 * Codigo gerado automaticamente em Thu Mar 01 09:41:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemLaudo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ItemLaudoFacade extends AbstractFacade<ItemLaudo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemLaudoFacade() {
        super(ItemLaudo.class);
    }

    @Override
    public ItemLaudo recuperar(Object id) {
        ItemLaudo itemLaudo = em.find(ItemLaudo.class, id);
        itemLaudo.getValoresUnidadesOrganizacionals().size();
        return itemLaudo;
    }
}
