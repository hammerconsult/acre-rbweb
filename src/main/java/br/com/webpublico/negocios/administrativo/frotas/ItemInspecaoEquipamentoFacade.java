package br.com.webpublico.negocios.administrativo.frotas;

import br.com.webpublico.entidades.administrativo.frotas.ItemInspecaoEquipamento;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by carlos on 24/05/17.
 */
@Stateless
public class ItemInspecaoEquipamentoFacade extends AbstractFacade<ItemInspecaoEquipamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemInspecaoEquipamentoFacade() {
        super(ItemInspecaoEquipamento.class);
    }
}
