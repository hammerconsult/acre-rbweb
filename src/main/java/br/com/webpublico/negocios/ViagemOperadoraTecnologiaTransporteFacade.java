package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ViagemOperadoraTecnologiaTransporte;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author octavio
 */
@Stateless
public class ViagemOperadoraTecnologiaTransporteFacade extends AbstractFacade<ViagemOperadoraTecnologiaTransporte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ViagemOperadoraTecnologiaTransporteFacade() {
        super(ViagemOperadoraTecnologiaTransporte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ViagemOperadoraTecnologiaTransporte recuperar(Object id) {
        ViagemOperadoraTecnologiaTransporte viagem = em.find(ViagemOperadoraTecnologiaTransporte.class, id);
        Hibernate.initialize(viagem.getOperadoraTecTransporte());
        return viagem;
    }
}
