package br.com.webpublico.negocios.rh.pccr;

import br.com.webpublico.entidades.rh.pccr.PlanoCargosSalariosPCCR;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 26/06/14
 * Time: 19:25
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class PlanoCargosSalariosPCCRFacade extends AbstractFacade<PlanoCargosSalariosPCCR> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public PlanoCargosSalariosPCCRFacade() {
        super(PlanoCargosSalariosPCCR.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<PlanoCargosSalariosPCCR> findPlanosCargosPCCRVigentes(Date data) {
        Query q = em.createQuery("from PlanoCargosSalariosPCCR where :data between inicioVigencia and coalesce(finalVigencia,:data)");
        q.setParameter("data", data);
        return q.getResultList();
    }


    public PlanoCargosSalariosPCCR findOnePlanosCargosPCCR(Long id) {
        Query q = em.createQuery("from PlanoCargosSalariosPCCR where id = :id ");
        q.setParameter("id", id);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (PlanoCargosSalariosPCCR) q.getResultList().get(0);
    }
}
