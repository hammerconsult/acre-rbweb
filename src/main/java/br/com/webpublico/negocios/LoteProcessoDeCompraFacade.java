package br.com.webpublico.negocios;

import br.com.webpublico.entidades.LoteProcessoDeCompra;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Buzatto on 05/10/2016.
 */
@Stateless
public class LoteProcessoDeCompraFacade extends AbstractFacade<LoteProcessoDeCompra>{

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LoteProcessoDeCompraFacade() {
        super(LoteProcessoDeCompra.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public LoteProcessoDeCompra recuperar(Object id) {
        LoteProcessoDeCompra loteProcessoDeCompra = super.recuperar(id);
            loteProcessoDeCompra.getItensProcessoDeCompra().size();
            loteProcessoDeCompra.getLotesProposta().size();
        return loteProcessoDeCompra;
    }
}
