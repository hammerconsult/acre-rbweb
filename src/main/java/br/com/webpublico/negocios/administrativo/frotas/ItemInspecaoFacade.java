package br.com.webpublico.negocios.administrativo.frotas;

import br.com.webpublico.entidades.administrativo.frotas.ItemInspecao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Desenvolvimento on 05/04/2017.
 */
@Stateless
public class ItemInspecaoFacade extends AbstractFacade<ItemInspecao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemInspecaoFacade() {
        super(ItemInspecao.class);
    }
}
