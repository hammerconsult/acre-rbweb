package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 25/08/14
 * Time: 08:57
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class BloqueioBeneficioFacade extends AbstractFacade<BloqueioBeneficio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public BloqueioBeneficioFacade() {
        super(BloqueioBeneficio.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Map<String, List<BloqueioBeneficio>> getVinculosBloqueadosPorBeneficio(Integer meses, Date dataProcessamento) {
        Map<String, List<BloqueioBeneficio>> listMap = new HashMap<>();
        DateTime inicio = new DateTime(dataProcessamento);
        DateTime fim = inicio.minusMonths(meses).withDayOfMonth(1);
        Query q = em.createNativeQuery("select bloqueio.contratofp_id, bloqueio.tipoBloqueio from BloqueioBeneficio bloqueio inner join contratoFP contrato on contrato.id = bloqueio.contratoFP_id " +
            "group by bloqueio.contratofp_id, bloqueio.tipoBloqueio ");

        List<Object[]> ids = q.getResultList();
        for (Object[] obj : ids) {
            BigDecimal idVinculo = (BigDecimal) obj[0];
            String tipo = (String) obj[1];
            Query query = em.createNativeQuery("select bloqueio.* from BloqueioBeneficio bloqueio where contratoFP_id = :vinculo and tipoBloqueio = :tipo and bloqueio.bloqueado = 1", BloqueioBeneficio.class);
            query.setParameter("vinculo", idVinculo.longValue());
            query.setParameter("tipo", TipoBloqueio.valueOf(tipo).name());
            List<BloqueioBeneficio> locks = query.getResultList();
            if (!locks.isEmpty()) {
                listMap.put(locks.get(0).getContratoFP().getIdCalculo() + "" + locks.get(0).getTipoBloqueio().name(), locks);
            }
        }
        return listMap;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Map<String, List<BloqueioBeneficio>> findAllVinculosBloqueadosPorBeneficio(Integer meses, Date dataProcessamento) {
        Map<String, List<BloqueioBeneficio>> listMap = new HashMap<>();
        Query q = em.createQuery("select bloqueio from BloqueioBeneficio bloqueio " +
            "where bloqueio.bloqueado = true ");

        List<BloqueioBeneficio> bloqueios = q.getResultList();

        for (BloqueioBeneficio bloqueio : bloqueios) {
            String chave = bloqueio.getContratoFP().getId() + "" + bloqueio.getTipoBloqueio().name();
            if (listMap.containsKey(chave)) {
                listMap.get(chave).add(bloqueio);
            } else {
                List<BloqueioBeneficio> locks = new ArrayList<>();
                locks.add(bloqueio);
                listMap.put(chave, locks);
            }
        }
        return listMap;
    }

    public boolean isLocked(Long id) {
        Query q = em.createQuery("select bloqueio.id from BloqueioBeneficio bloqueio where bloqueio.contratoFP.id = :id ");
        q.setParameter("id", id);

        return !q.getResultList().isEmpty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, List<BloqueioBeneficio>> getVinculosBloqueadosPorBeneficioPorVinculo(VinculoFP vinculoFP, Integer meses, Date dataProcessamento) {
        DateTime inicio = new DateTime(dataProcessamento);
        DateTime fim = inicio.minusMonths(meses);

        Map<String, List<BloqueioBeneficio>> listMap = new LinkedHashMap<>();
        Query q = em.createNativeQuery("select contrato.id, bloqueio.tipoBloqueio from BloqueioBeneficio bloqueio inner join ContratoFP contrato on bloqueio.contratofp_id = contrato.id where contrato.id = :id " +
            " and (trunc(bloqueio.inicioVigencia) between :inicio and :fim " +
            "    or :inicio between trunc(bloqueio.inicioVigencia) and coalesce(trunc(bloqueio.finalVigencia),:inicio) " +
            "    or :fim between trunc(bloqueio.inicioVigencia) and coalesce(trunc(bloqueio.finalVigencia),:fim) " +
            "    or coalesce(trunc(bloqueio.finalVigencia),:inicio) between :inicio and :fim) group by contrato.id, bloqueio.tipoBloqueio ");
        q.setParameter("id", vinculoFP.getId());
        q.setParameter("inicio", fim.toDate(), TemporalType.DATE);
        q.setParameter("fim", inicio.toDate(), TemporalType.DATE);
        List<Object[]> ids = q.getResultList();
        for (Object[] obj : ids) {
            BigDecimal idVinculo = (BigDecimal) obj[0];
            String tipo = (String) obj[1];
            Query query = em.createQuery("select bloqueio from BloqueioBeneficio bloqueio where contratoFP.id = :vinculo and tipoBloqueio = :tipo and bloqueio.bloqueado = true");
            query.setParameter("vinculo", idVinculo.longValue());
            query.setParameter("tipo", TipoBloqueio.valueOf(tipo));
            List<BloqueioBeneficio> locks = query.getResultList();
            if (!locks.isEmpty()) {
                listMap.put(locks.get(0).getContratoFP().getIdCalculo() + "" + locks.get(0).getTipoBloqueio().name(), locks);
            }

        }
        return listMap;
    }

    public boolean buscarBloqueiosVigentesPorVinculoFPAndTipoBloqueio(ContratoFP contratoFP, TipoBloqueio tipoBloqueio) {
        String sql = "SELECT BB.ID FROM BLOQUEIOBENEFICIO BB " +
            "INNER JOIN VINCULOFP VINCULO ON BB.CONTRATOFP_ID = VINCULO.ID " +
            "INNER JOIN CONTRATOFP CONTRATO ON CONTRATO.ID = VINCULO.ID " +
            "WHERE CONTRATO.ID = :contratoFP AND SYSDATE BETWEEN trunc(BB.INICIOVIGENCIA) AND COALESCE(trunc(BB.FINALVIGENCIA), SYSDATE) " +
            "AND BB.TIPOBLOQUEIO = :tipoBloqueio ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoFP", contratoFP.getId());
        q.setParameter("tipoBloqueio", tipoBloqueio.name());
        return !q.getResultList().isEmpty();
    }

    public List<BloqueioBeneficio> buscarBloqueiosBeneficiosPorContrato(ContratoFP contrato) {
        String sql = " select bloqueio.* from BloqueioBeneficio bloqueio " +
            " where bloqueio.CONTRATOFP_ID = :contrato ";
        Query q = em.createNativeQuery(sql, BloqueioBeneficio.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
