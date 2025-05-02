package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.Faltas;
import br.com.webpublico.entidades.JustificativaFaltas;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 28/08/13
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class JustificativaFaltasFacade extends AbstractFacade<JustificativaFaltas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public JustificativaFaltasFacade() {
        super(JustificativaFaltas.class);
    }

    @Override
    public void salvar(JustificativaFaltas entity) {
        entity = em.merge(entity);
        processarPA(entity.getFaltas().getContratoFP());
    }

    @Override
    public void salvarNovo(JustificativaFaltas entity) {
        entity = super.salvarRetornando(entity);
        processarPA(entity.getFaltas().getContratoFP());
    }

    @Override
    public void remover(JustificativaFaltas entity) {
        ContratoFP contratoFP = contratoFPFacade.recuperarSomentePeriodosAquisitivos(entity.getFaltas().getContratoFP().getId());
        super.remover(entity);
        processarPA(contratoFP);
    }

    public void processarPA(ContratoFP contratofp) {
        periodoAquisitivoFLFacade.excluirPeriodosAquisitivosParaRegerar(contratofp);
        periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(contratofp, SistemaFacade.getDataCorrente(), null);
    }

    public List<JustificativaFaltas> recuperarJustificativaDeFaltasPorFalta(Faltas falta, JustificativaFaltas justificativaFaltas) {
        String hql = "select justificativa from JustificativaFaltas justificativa " +
            " where justificativa.faltas = :falta ";
        if (justificativaFaltas != null && justificativaFaltas.getId() != null) {
            hql += " and justificativa != :justificativaFaltas";
        }
        hql += "  order by justificativa.inicioVigencia ";
        Query q = em.createQuery(hql);
        q.setParameter("falta", falta);
        if (justificativaFaltas != null && justificativaFaltas.getId() != null) {
            q.setParameter("justificativaFaltas", justificativaFaltas);
        }
        q.getResultList();
        return q.getResultList();
    }

    public Integer buscarQuantidadeDeJustificativaFaltasPorData(Date inicio) {
        Integer total = 0;
        String hql = "select count(distinct r.id) from JustificativaFaltas_aud r inner join revisaoAuditoria rev on rev.id= r.rev where r.revtype = 0 and to_date(to_char(rev.datahora,'dd/MM/yyyy'),'dd/MM/yyyy') =  :data ";
        Query q = em.createNativeQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        BigDecimal bg = (BigDecimal) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public List<JustificativaFaltas> buscarJustificativasPorIdFalta(ContratoFP contrato, Long idFalta) {
        String sql = "select j.* from justificativafaltas j " +
            " inner join vwfalta v on v.justificativa_id = j.id " +
            " where v.contratofp_id = :contrato " +
            " and j.faltas_id = :idFalta ";
        Query q = em.createNativeQuery(sql, JustificativaFaltas.class);
        q.setParameter("contrato", contrato.getId());
        q.setParameter("idFalta", idFalta);

        try {
            return (List<JustificativaFaltas>) q.getResultList();
        } catch (NoResultException npe) {
            return Collections.emptyList();
        }
    }
}
