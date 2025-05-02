package br.com.webpublico.negocios;

import br.com.webpublico.entidades.VeiculoTransporte;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Marcos on 31/05/2017.
 */
@Stateless
public class VeiculoTransporteFacade extends AbstractFacade<VeiculoTransporte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public VeiculoTransporteFacade(){
        super(VeiculoTransporte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
