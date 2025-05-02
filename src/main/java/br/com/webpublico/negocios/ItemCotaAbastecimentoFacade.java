package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemAbastecObjetoFrota;
import br.com.webpublico.entidades.ItemCotaAbastecimento;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 02/10/14
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ItemCotaAbastecimentoFacade extends AbstractFacade<ItemCotaAbastecimento> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ItemContratoFacade itemContratoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemCotaAbastecimentoFacade() {
        super(ItemCotaAbastecimento.class);
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return super.recuperar(id);
    }

    @Override
    public ItemCotaAbastecimento recuperar(Object id) {
        ItemCotaAbastecimento itemCotaAbastecimento = super.recuperar(id);
        itemCotaAbastecimento.setItemContrato(itemContratoFacade.recuperar(itemCotaAbastecimento.getItemContrato().getId()));
        return itemCotaAbastecimento;
    }
}
