/*
 * Codigo gerado automaticamente em Mon Dec 19 16:47:42 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CompetenciaFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.StatusCompetencia;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class CompetenciaFPFacade extends AbstractFacade<CompetenciaFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CompetenciaFPFacade() {
        super(CompetenciaFP.class);
    }

    @Override
    public List<CompetenciaFP> lista() {
        return em.createQuery("select c from CompetenciaFP c order by c.exercicio desc, c.mes desc").getResultList();
    }

    public List<CompetenciaFP> recuperarUltimasCompetencias(Integer quantidade) {
        return em.createQuery("select c from CompetenciaFP c order by c.exercicio desc, c.mes desc").setMaxResults(quantidade).getResultList();
    }

    public CompetenciaFP recuperarUltimaCompetencia(StatusCompetencia status, TipoFolhaDePagamento tipo) {
        String hql = "SELECT * FROM competenciafp comp "
            + "                 JOIN exercicio e ON comp.exercicio_id = e.id "
            + "                     WHERE comp.statuscompetencia = :status "
            + "                     AND comp.tipofolhadepagamento = :tipo "
            + "                         ORDER BY e.ano DESC, comp.mes DESC ";
        Query q = em.createNativeQuery(hql, CompetenciaFP.class);
        q.setParameter("status", status.name());
        q.setParameter("tipo", tipo.name());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (CompetenciaFP) q.getResultList().get(0);
        }
        return null;
    }

    public List<CompetenciaFP> buscarCompetenciasPorStatus(StatusCompetencia status) {
        String hql = "SELECT * FROM competenciafp comp "
            + "                     JOIN exercicio e ON comp.exercicio_id = e.id "
            + "                WHERE comp.statuscompetencia = :status "
            + "                     ORDER BY e.ano DESC, comp.mes DESC ";
        Query q = em.createNativeQuery(hql, CompetenciaFP.class);
        q.setParameter("status", status.name());
        return q.getResultList();
    }

    public List<CompetenciaFP> buscarTodasCompetencias() {
        String hql = "SELECT * FROM competenciafp comp "
            + "                     JOIN exercicio e ON comp.exercicio_id = e.id "
            + "                     ORDER BY e.ano DESC, comp.mes DESC ";
        Query q = em.createNativeQuery(hql, CompetenciaFP.class);
        return q.getResultList();
    }

    public DateTime recuperarDataUltimaCompetencia() {
        String hql = "SELECT * FROM competenciafp comp "
            + "                 JOIN exercicio e ON comp.exercicio_id = e.id "
            + "                     WHERE "
            + "                     comp.tipofolhadepagamento = :tipo "
            + "                         ORDER BY e.ano DESC, comp.mes DESC ";
        Query q = em.createNativeQuery(hql, CompetenciaFP.class);
        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL.name());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            CompetenciaFP cp = (CompetenciaFP) q.getResultList().get(0);
            DateTime dt = new DateTime();
            dt = dt.withMonthOfYear(cp.getMes().getNumeroMes());
            dt = dt.withYear(cp.getExercicio().getAno());
            dt = dt.withDayOfMonth(1);
            return dt;
        }
        return null;
    }

    public CompetenciaFP recuperarCompetenciaPorTipoMesAno(Mes mes, Integer ano, TipoFolhaDePagamento tipo) {
        String hql = "select comp from CompetenciaFP comp" +
            " where comp.mes = :mes" +
            " and comp.exercicio.ano = :ano" +
            " and comp.tipoFolhaDePagamento = :tipo";
        Query q = em.createQuery(hql);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("tipo", tipo);
        q.setMaxResults(1);

        try {
            return (CompetenciaFP) q.getSingleResult();
        } catch (NoResultException npe) {
            return null;
        }
    }

    public CompetenciaFP recuperarCompetenciaPorMesAnoStatus(Integer mes, Integer ano) {
        String hql = "select comp from CompetenciaFP comp" +
            " where comp.mes = :mes" +
            " and comp.exercicio.ano = :ano" +
            " and comp.statusCompetencia = :status";
        Query q = em.createQuery(hql);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ano", ano);
        q.setParameter("status", StatusCompetencia.EFETIVADA);
        q.setMaxResults(1);

        try {
            return (CompetenciaFP) q.getSingleResult();
        } catch (NoResultException npe) {
            return null;
        }
    }

    public List<CompetenciaFP> findCompetenciasAbertasPorTipo(TipoFolhaDePagamento tipo) {
        String hql = "select comp from CompetenciaFP comp" +
            " where " +
            " comp.tipoFolhaDePagamento = :tipo and comp.statusCompetencia = :status ";
        Query q = em.createQuery(hql);

        q.setParameter("status", StatusCompetencia.ABERTA);
        q.setParameter("tipo", tipo);
        try {
            return q.getResultList();
        } catch (NoResultException npe) {
            return null;
        }
    }

    @Override
    public void salvar(CompetenciaFP entity) {
        efetivarFolhaPorCompetencia(entity);
        super.salvar(entity);
    }

    private void efetivarFolhaPorCompetencia(CompetenciaFP competenciaFP) {
        if (StatusCompetencia.EFETIVADA.equals(competenciaFP.getStatusCompetencia())) {
            folhaDePagamentoFacade.efetivarFolhaDePagamentoPorCompetencia(competenciaFP, new Date());
        }
    }
}
