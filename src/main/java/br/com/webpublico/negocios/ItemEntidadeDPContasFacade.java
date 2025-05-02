package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemEntidadeDPContas;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ItemEntidadeDPContasFacade extends AbstractFacade<ItemEntidadeDPContas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemEntidadeDPContasFacade() {
        super(ItemEntidadeDPContas.class);
    }

    @Override
    public ItemEntidadeDPContas recuperar(Object id) {
        ItemEntidadeDPContas item = em.find(ItemEntidadeDPContas.class, id);
        item.getItemEntidadeUnidadeOrganizacional().size();

        return item;
    }

}
