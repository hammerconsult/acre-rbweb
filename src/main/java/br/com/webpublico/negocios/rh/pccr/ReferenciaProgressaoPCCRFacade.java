package br.com.webpublico.negocios.rh.pccr;

import br.com.webpublico.entidades.rh.pccr.CategoriaPCCR;
import br.com.webpublico.entidades.rh.pccr.ProgressaoPCCR;
import br.com.webpublico.entidades.rh.pccr.ReferenciaProgressaoPCCR;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.util.UtilRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 27/06/14
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ReferenciaProgressaoPCCRFacade extends AbstractFacade<ReferenciaProgressaoPCCR> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PlanoCargosSalariosPCCRFacade planoCargosSalariosPCCRFacade;
    @EJB
    private EnquadramentoPCCRFacade enquadramentoPCCRFacade;


    public ReferenciaProgressaoPCCRFacade() {
        super(ReferenciaProgressaoPCCR.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ReferenciaProgressaoPCCR> findLetrasByCategoria(CategoriaPCCR categoriaPCCR) {
        Query q = em.createQuery("select valor.referenciaProgressaoPCCR from CategoriaPCCR cat join cat.cargosCategoriaPCCR cargo join cargo.progressaoPCCR prog join prog.valorProgressaoPCCRs valor where " +
                " cat = :cat " +
                " and :data between valor.inicioVigencia and coalesce(valor.finalVigencia,:data) " +
                " and :data between cargo.inicioVigencia and coalesce(cargo.finalVigencia,:data) order by valor.referenciaProgressaoPCCR.letra ");
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setParameter("cat", categoriaPCCR);
        return q.getResultList();
    }

    public List<ReferenciaProgressaoPCCR> findReferenciaByProgressaoPCCR(ProgressaoPCCR prog) {
        Query q = em.createQuery("select valor.referenciaProgressaoPCCR from ProgressaoPCCR prog join prog.valorProgressaoPCCRs valor where " +
                " prog = :progre " +
                " and :data between valor.inicioVigencia and coalesce(valor.finalVigencia,:data) " +
                "  order by valor.referenciaProgressaoPCCR.letra ");
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setParameter("progre", prog);
        return q.getResultList();
    }
}
