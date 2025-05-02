/*
 * Codigo gerado automaticamente em Tue Apr 03 17:54:41 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PenalidadeFP;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class PenalidadeFPFacade extends AbstractFacade<PenalidadeFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PenalidadeFPFacade() {
        super(PenalidadeFP.class);
    }

    public List<PenalidadeFP> listaPenalidadesPorContratoVigencia(ContratoFP contratoFP, Date inicioVigencia, Date finalVigencia) {
        Query q = em.createQuery(" from PenalidadeFP penalidade "
            + " where penalidade.contratoFP = :contratoFP "
            + " and penalidade.inicioVigencia >= :inicioVigencia ");
        //+ " and penalidade.finalVigencia <= :finalVigencia ");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("inicioVigencia", inicioVigencia);
        //q.setParameter("finalVigencia", finalVigencia);
        return q.getResultList();
    }

    public Integer recuperaTotalDiasPenalidadePorContratoVigencia(ContratoFP contratoFP, Date inicioVigencia, Date finalVigencia) {
        Integer dias = 0;
        for (PenalidadeFP p : listaPenalidadesPorContratoVigencia(contratoFP, inicioVigencia, finalVigencia)) {
            dias += DataUtil.getDias(p.getInicioVigencia(), p.getFinalVigencia());
        }
        return dias;
    }

    public Date recuperaFinalVigenciaPenalidadePorContratoVigencia(ContratoFP contratoFP, Date inicioVigencia, Date finalVigencia) {
        Query q = em.createQuery(" from PenalidadeFP penalidade"
            + " where penalidade.contratoFP = :contratoFP order by penalidade.finalVigencia desc");
        q.setParameter("contratoFP", contratoFP);
        q.setMaxResults(1);
        PenalidadeFP penalidadeFP = (PenalidadeFP) q.getSingleResult();
        return penalidadeFP.getFinalVigencia();
    }

    public Integer buscarQuantidadeDePenalidadesPorData(Date inicio) {
        Integer total = 0;
        String hql = "select count(distinct r.id) from PenalidadeFP_aud r inner join revisaoAuditoria rev on rev.id= r.rev where r.revtype = 0 and to_date(to_char(rev.datahora,'dd/MM/yyyy'),'dd/MM/yyyy') =  :data ";
        Query q = em.createNativeQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        BigDecimal bg = (BigDecimal) q.getSingleResult();
        total = bg.intValue();
        return total;
    }


    public List<PenalidadeFP> buscarPenalidadePorContrato(ContratoFP contratoFP) {
        String sql = " select p.* "
            + "    from PenalidadeFP p "
            + "   where p.contratofp_id = :contratoFP ";
        Query q = em.createNativeQuery(sql, PenalidadeFP.class);
        q.setParameter("contratoFP", contratoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
