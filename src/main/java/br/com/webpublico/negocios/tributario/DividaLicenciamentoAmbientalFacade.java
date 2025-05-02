package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidades.tributario.DividaLicenciamentoAmbiental;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class DividaLicenciamentoAmbientalFacade extends AbstractFacade<DividaLicenciamentoAmbiental> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public DividaLicenciamentoAmbientalFacade() {
        super(DividaLicenciamentoAmbiental.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public synchronized DividaLicenciamentoAmbiental recuperarUltimo() {
        Query q = em.createQuery("from DividaLicenciamentoAmbiental order by id desc ");
        List<DividaLicenciamentoAmbiental> dividas = q.getResultList();
        if (!dividas.isEmpty()) {
            return dividas.stream().findFirst().get();
        }
        return null;
    }
}
