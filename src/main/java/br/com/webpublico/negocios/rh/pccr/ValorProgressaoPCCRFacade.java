package br.com.webpublico.negocios.rh.pccr;

import br.com.webpublico.entidades.rh.pccr.CategoriaPCCR;
import br.com.webpublico.entidades.rh.pccr.ProgressaoPCCR;
import br.com.webpublico.entidades.rh.pccr.ReferenciaProgressaoPCCR;
import br.com.webpublico.entidades.rh.pccr.ValorProgressaoPCCR;
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
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ValorProgressaoPCCRFacade extends AbstractFacade<ValorProgressaoPCCR> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PlanoCargosSalariosPCCRFacade planoCargosSalariosPCCRFacade;
    @EJB
    private EnquadramentoPCCRFacade enquadramentoPCCRFacade;


    public ValorProgressaoPCCRFacade() {
        super(ValorProgressaoPCCR.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ValorProgressaoPCCR findValorByProgresaoAndCategoria(ReferenciaProgressaoPCCR refe, CategoriaPCCR cat) {
        Query q = em.createQuery("select valor from CategoriaPCCR  cat inner join cat.cargosCategoriaPCCR cargo inner join cargo.progressaoPCCR prog inner join prog.valorProgressaoPCCRs valor join valor.referenciaProgressaoPCCR refe " +
                "where refe = :refe " +
                " and :data between valor.inicioVigencia and coalesce(valor.finalVigencia, :data) " +
                " and :data between cargo.inicioVigencia and coalesce(cargo.finalVigencia, :data) " +
                " and cat = :cat ");
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setParameter("refe", refe);
        q.setParameter("cat", cat);
        if (q.getResultList().isEmpty()) return null;
        return (ValorProgressaoPCCR) q.getResultList().get(0);
    }

    public List<ValorProgressaoPCCR> findValorByProgresao(ProgressaoPCCR progressao) {
        Query q = em.createQuery("select valor from ValorProgressaoPCCR  valor inner join valor.progressaoPCCR p  where p = :progressao order by valor.inicioVigencia asc, valor.referenciaProgressaoPCCR.letra asc");
        q.setParameter("progressao", progressao);
        return q.getResultList();
    }
}
