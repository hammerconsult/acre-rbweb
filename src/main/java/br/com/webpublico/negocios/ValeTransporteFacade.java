package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BloqueioBeneficio;
import br.com.webpublico.entidades.OpcaoValeTransporteFP;
import br.com.webpublico.entidades.TipoBloqueio;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 08/03/2017.
 */
@Stateless
public class ValeTransporteFacade extends AbstractFacade<OpcaoValeTransporteFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public ValeTransporteFacade() {
        super(OpcaoValeTransporteFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public BloqueioBeneficio buscarBloqueios(OpcaoValeTransporteFP selecionado) {
        String sql = " select b.* " +
            " from BloqueioBeneficio b " +
            " where b.contratoFP_id = :idContratoFp " +
            "   and to_date(:dataReferencia, 'dd/MM/yyyy') between trunc(b.inicioVigencia) and trunc(b.finalVigencia) " +
            "   and b.bloqueado = 1 " +
            "   and b.tipoBloqueio = :valeTransporte ";
        Query q = em.createNativeQuery(sql, BloqueioBeneficio.class);
        q.setParameter("idContratoFp", selecionado.getContratoFP().getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(selecionado.getInicioVigencia()));
        q.setParameter("valeTransporte", TipoBloqueio.VALE_TRANSPORTE.name());
        List<BloqueioBeneficio> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }
}
