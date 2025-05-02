/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.configuracao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.rh.configuracao.ReajusteMediaAposentadoria;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AposentadoriaFacade;
import br.com.webpublico.negocios.PensionistaFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class ReajusteMediaAposentadoriaFacade extends AbstractFacade<ReajusteMediaAposentadoria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReajusteMediaAposentadoriaFacade() {
        super(ReajusteMediaAposentadoria.class);
    }

    public ReajusteMediaAposentadoria buscarReajusteMediaAposentadoriaDe(Mes mes, Exercicio exercicio) {
        String sql = "select rma.* from reajustemediaaposentadoria rma where rma.mes = :mes and rma.exercicio_id = :exercicio_id";
        Query q = em.createNativeQuery(sql, ReajusteMediaAposentadoria.class);
        q.setMaxResults(1);
        q.setParameter("mes", mes.name());
        q.setParameter("exercicio_id", exercicio.getId());
        try {
            return (ReajusteMediaAposentadoria) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<ReajusteMediaAposentadoria> buscarReajusteMediaAposentadoriaPorExercicio(Exercicio exercicio) {
        String sql = "select rma.* from reajustemediaaposentadoria rma where rma.exercicio_id = :exercicio_id";
        Query q = em.createNativeQuery(sql, ReajusteMediaAposentadoria.class);
        q.setParameter("exercicio_id", exercicio.getId());
        return q.getResultList();
    }


    public List<Exercicio> buscarExerciciosDoReajuste() {
        String sql = "select ex from Exercicio ex where ex.id in(select distinct rea.exercicio.id from ReajusteMediaAposentadoria rea) order by ex.ano desc";
        Query q = em.createQuery(sql);
        return q.getResultList();
    }

    public List<ReajusteMediaAposentadoria> buscarPorExercicios(Exercicio exercicioAplicacao, Exercicio exercicioReferencia) {
        String sql = " select rma.* from reajustemediaaposentadoria rma " +
            " where rma.exercicio_id = :exercicioAplicacao " +
            " and rma.exercicioreferencia_id = :exercicioReferencia ";
        Query q = em.createNativeQuery(sql, ReajusteMediaAposentadoria.class);
        q.setParameter("exercicioAplicacao", exercicioAplicacao.getId());
        q.setParameter("exercicioReferencia", exercicioReferencia.getId());
        List resultado = q.getResultList();
        if (resultado != null) {
            return resultado;
        }
        return Lists.newArrayList();
    }

    public List<ReajusteMediaAposentadoria> buscarReajustesVigentesPorExercicios(Date dataOperacao, Exercicio exercicioAplicacao, Exercicio exercicioReferencia) {
        String sql = " select rma.*" +
            " from reajustemediaaposentadoria rma " +
            " where rma.exercicio_id = :exercicioAplicacao" +
            "  and rma.exercicioreferencia_id = :exercicioReferencia" +
            "  and :dataOperacao between rma.iniciovigencia and coalesce(finalvigencia, :dataOperacao) ";
        Query q = em.createNativeQuery(sql, ReajusteMediaAposentadoria.class);
        q.setParameter("exercicioAplicacao", exercicioAplicacao.getId());
        q.setParameter("exercicioReferencia", exercicioReferencia.getId());
        q.setParameter("dataOperacao", dataOperacao);
        List resultado = q.getResultList();
        if (resultado != null) {
            return resultado;
        }
        return Lists.newArrayList();
    }

    public List<ReajusteMediaAposentadoria> buscarReajustePorAnoReajusteAnoReferenciEMes(Exercicio exercicioReajuste, Exercicio exercicioReferencia, Mes mes) {
        String sql = " select rma.* " +
            "from reajustemediaaposentadoria rma " +
            "where rma.exercicio_id = :exercicioReajuste " +
            "  and rma.exercicioreferencia_id = :exercicioReferencia " +
            "  and rma.mes = :mes ";
        Query q = em.createNativeQuery(sql, ReajusteMediaAposentadoria.class);
        q.setParameter("exercicioReajuste", exercicioReajuste.getId());
        q.setParameter("exercicioReferencia", exercicioReferencia.getId());
        q.setParameter("mes", mes.name());
        List resultado = q.getResultList();
        if (resultado != null) {
            return resultado;
        }
        return Lists.newArrayList();
    }
}
