package br.com.webpublico.negocios.comum;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.comum.FechamentoMensal;
import br.com.webpublico.entidades.comum.FechamentoMensalMes;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SituacaoFechamentoMensal;
import br.com.webpublico.enums.TipoSituacaoAgendamento;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.contabil.singleton.SingletonFechamentoMensal;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class FechamentoMensalFacade extends AbstractFacade<FechamentoMensal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonFechamentoMensal singletonFechamentoMensal;

    public FechamentoMensalFacade() {
        super(FechamentoMensal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public FechamentoMensal recuperar(Object id) {
        FechamentoMensal entity = super.recuperar(id);
        entity.getMeses().size();
        return entity;
    }

    @Override
    public void salvarNovo(FechamentoMensal entity) {
        super.salvarNovo(entity);
        singletonFechamentoMensal.limparAndPopularFechamentos();
    }

    @Override
    public void salvar(FechamentoMensal entity) {
        super.salvar(entity);
        singletonFechamentoMensal.limparAndPopularFechamentos();
    }

    @Override
    public void remover(FechamentoMensal entity) {
        super.remover(entity);
        singletonFechamentoMensal.limparAndPopularFechamentos();
    }

    public FechamentoMensal buscarFechamentoPorExercicio(Long idExercicio) {
        String sql = " select * from FechamentoMensal where exercicio_id = :idExercicio ";
        Query q = em.createNativeQuery(sql, FechamentoMensal.class);
        q.setParameter("idExercicio", idExercicio);
        try {
            return (FechamentoMensal) q.getSingleResult();
        } catch (NoResultException nr) {
            return null;
        }
    }

    public List<FechamentoMensal> buscarFechamentosMensais() {
        String sql = " select * from FechamentoMensal  ";
        Query q = em.createNativeQuery(sql, FechamentoMensal.class);
        return q.getResultList();
    }

    public void validarMesContabil(Date dataSaldo) {
        validarMesContabil(DataUtil.getMes(dataSaldo), DataUtil.getAno(dataSaldo));
    }

    public void validarMesContabilPorPeriodo(Date dataInicial, Date dataFinal) {
        if (dataInicial != null && dataFinal != null) {
            Integer mesInicial = DataUtil.getMes(dataInicial);
            Integer anoInicial = DataUtil.getAno(dataInicial);

            Integer mesFinal = DataUtil.getMes(dataFinal);
            Integer anoFinal = DataUtil.getAno(dataFinal);

            if (anoInicial.compareTo(anoFinal) == 0) {
                for (int count = mesInicial; count <= mesFinal; count++) {
                    validarMesContabil(count, anoFinal);
                }
            } else {
                for (int count = mesInicial; count <= 12; count++) {
                    validarMesContabil(count, anoInicial);
                }

                for (int count = 1; count <= mesFinal; count++) {
                    validarMesContabil(count, anoFinal);
                }
            }
        }
    }

    public void validarMesContabil(Integer mesInt, Integer anoInt) {
        Exercicio ano = exercicioFacade.getExercicioPorAno(anoInt);
        Mes mes = Mes.getMesToInt(mesInt);

        if (!hasMesContabilAbertoPorExercicio(mes, ano)) {
            throw new ExcecaoNegocioGenerica("O mês <b> " + mes.getDescricao() + " </b> se encontra com situação <b> Fechado </b> para o Exercício <b> " + ano.getAno() + " </b>.");
        }
    }

    public boolean hasMesContabilAbertoPorExercicio(Mes mes, Exercicio exercicio) {
        String sql = " select fMes.* " +
            " from fechamentomensalmes fMes " +
            " inner join fechamentomensal f on fMes.fechamentomensal_id = f.id " +
            " where f.exercicio_id = :idExercicio and fMes.mes = :mes AND fMes.situacaoContabil = :aberto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", mes.name());
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("aberto", SituacaoFechamentoMensal.ABERTO.name());
        List<Object[]> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }

    public List<FechamentoMensalMes> buscarConfiguracoesReprocessamento() {
        String hql = "from FechamentoMensalMes where situacaoContabil = :aberto and cron is not null and tipoSituacaoAgendamento = :aguardando and dataAgendamento = :dataHoraMinutoAtual ";
        Query q = em.createQuery(hql);
        q.setParameter("aberto", SituacaoFechamentoMensal.ABERTO);
        q.setParameter("aguardando", TipoSituacaoAgendamento.AGUARDANDO);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        q.setParameter("dataHoraMinutoAtual", c.getTime());
        return q.getResultList();
    }

    public void salvarFechamentoMensalMes(FechamentoMensalMes fmm) {
        try {
            em.merge(fmm);
        } catch (Exception ex) {
            logger.error("Problema ao alterar FechamentoMensalMes", ex);
        }
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
