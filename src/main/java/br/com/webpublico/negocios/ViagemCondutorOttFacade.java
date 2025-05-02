package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ViagemCondutorOtt;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ViagemCondutorOttFacade extends AbstractFacade<ViagemCondutorOtt> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    public ViagemCondutorOttFacade(){
        super(ViagemCondutorOtt.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
