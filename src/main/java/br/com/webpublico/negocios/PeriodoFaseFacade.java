package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoPeriodoFase;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.tratamentoerros.BuscaCausa;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 16/10/13
 * Time: 09:43
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class PeriodoFaseFacade extends AbstractFacade<PeriodoFase> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private RelatorioPesquisaGenericoFacade relatorioPesquisaGenericoFacade;
    @EJB
    private GrupoUsuarioFacade grupoUsuarioFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;


    public PeriodoFaseFacade() {
        super(PeriodoFase.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public PeriodoFase recuperar(Object id) {
        PeriodoFase periodoFase = em.find(PeriodoFase.class, id);
        periodoFase.getPeriodoFaseUnidades().size();
        for (PeriodoFaseUnidade periodoFaseUnidade : periodoFase.getPeriodoFaseUnidades()) {
            periodoFaseUnidade.getUsuarios().size();
        }

        return periodoFase;
    }

    public PeriodoFaseUnidade validouVigenciaPeriodoFase(PeriodoFaseUnidade periodoFaseUnidade) {
        try {
            String sql = " SELECT PFU.* FROM PERIODOFASE PF " +
                " INNER JOIN PERIODOFASEUNIDADE PFU ON PFU.PERIODOFASE_ID = PF.ID " +
                " INNER JOIN UNIDADEORGANIZACIONAL UO ON PFU.UNIDADEORGANIZACIONAL_ID = UO.ID " +
                " WHERE PF.FASE_ID = :idFase " +
                " AND UO.ID = :idUnidade " +
                " AND (TO_DATE(:dataInicial,'dd/MM/yyyy') BETWEEN trunc(PFU.INICIOVIGENCIA) AND COALESCE(trunc(PFU.FIMVIGENCIA), TO_DATE(:dataInicial,'dd/MM/yyyy')) " +
                " OR TO_DATE(:dataFinal,'dd/MM/yyyy') BETWEEN trunc(PFU.INICIOVIGENCIA) AND COALESCE(trunc(PFU.FIMVIGENCIA), TO_DATE(:dataFinal,'dd/MM/yyyy'))) ";
            if (periodoFaseUnidade.getPeriodoFase() != null) {
                if (periodoFaseUnidade.getPeriodoFase().getId() != null) {
                    sql += " AND PF.ID <> :idPeriodo";
                }
            }

            Query q = getEntityManager().createNativeQuery(sql, PeriodoFaseUnidade.class);
            q.setParameter("idFase", periodoFaseUnidade.getPeriodoFase().getFase().getId());
            q.setParameter("idUnidade", periodoFaseUnidade.getUnidadeOrganizacional().getId());
            q.setParameter("dataInicial", DataUtil.getDataFormatada(periodoFaseUnidade.getInicioVigencia()));
            q.setParameter("dataFinal", DataUtil.getDataFormatada(periodoFaseUnidade.getFimVigencia()));
            q.setMaxResults(1);

            if (periodoFaseUnidade.getPeriodoFase() != null) {
                if (periodoFaseUnidade.getPeriodoFase().getId() != null) {
                    q.setParameter("idPeriodo", periodoFaseUnidade.getPeriodoFase().getId());
                }
            }
            PeriodoFaseUnidade retorno = (PeriodoFaseUnidade) q.getSingleResult();
            return retorno;
        } catch (NoResultException e) {
            return null;
        }
    }

    public String getUnidades(List<HierarquiaOrganizacional> unidades) {
        String retorno = "";
        for (HierarquiaOrganizacional unidade : unidades) {
            retorno += unidade.getSubordinada().getId() + ",";
        }
        retorno = retorno.substring(0, retorno.length() - 1);
        return retorno;
    }

    public List<PeriodoFase> listaPeriodoFasePorUnidadesUnidadeGestoraExercicioFaseEData(Exercicio ex, String unidades, Fase fase, UnidadeGestora ug, Date dataInicial, Date dataFinal, SituacaoPeriodoFase situacao) {
        String sql = " SELECT DISTINCT P.* FROM PERIODOFASE P " +
            " INNER JOIN PERIODOFASEUNIDADE PFU ON PFU.PERIODOFASE_ID = P.ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = PFU.UNIDADEORGANIZACIONAL_ID " +
            " INNER JOIN UGESTORAUORGANIZACIONAL UGORG ON UGORG.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID " +
            " INNER JOIN UNIDADEGESTORA UG ON UG.ID = UGORG.UNIDADEGESTORA_ID " +
            " AND (TO_DATE(:dataInicial, 'dd/mm/yyyy') >= trunc(PFU.INICIOVIGENCIA) OR TO_DATE(:dataFinal, 'dd/mm/yyyy') <= trunc(PFU.FIMVIGENCIA)) " +
            " WHERE P.EXERCICIO_ID = :idExercicio " +
            " AND PFU.SITUACAOPERIODOFASE = :situacao " + unidades;
        if (fase != null) {
            if (fase.getId() != null) {
                sql += " AND P.FASE_ID = :idFase ";
            }
        }
        if (ug != null) {
            if (ug.getId() != null) {
                sql += " AND UG.ID = :idUnidadeGestora";
            }
        }
        Query q = em.createNativeQuery(sql, PeriodoFase.class);
        q.setParameter("idExercicio", ex.getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("situacao", situacao.name());

        if (fase != null) {
            if (fase.getId() != null) {
                q.setParameter("idFase", fase.getId());
            }
        }
        if (ug != null) {
            if (ug.getId() != null) {
                q.setParameter("idUnidadeGestora", ug.getId());
            }
        }
        List<PeriodoFase> periodoFases = q.getResultList();
        for (PeriodoFase pf : periodoFases) {
            pf.getPeriodoFaseUnidades().size();
            for (PeriodoFaseUnidade pfu : pf.getPeriodoFaseUnidades()) {
                pfu.getUsuarios().size();
            }
        }
        return periodoFases;
    }

    public List<PeriodoFase> recuperarPeriodoFasePorFase(Fase fase) {
        String sql = "SELECT * FROM PERIODOFASE PF " +
            "        WHERE PF.FASE_ID = :idFase";
        Query q = em.createNativeQuery(sql, PeriodoFase.class);
        q.setParameter("idFase", fase.getId());
        return q.getResultList();
    }

    @Override
    public void salvarNovo(PeriodoFase entity) {
        em.persist(entity);
    }

    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 60)
    public PeriodoFase salvarPeriodoFase(PeriodoFase entity) {
        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        salvarNotificaoAprovacaoPeriodoFase(entity);
        return entity;
    }

    public void lancarException(Exception ex) {
        Throwable t = BuscaCausa.desenrolarException(ex);
        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setDataRegistro(new Date());
        exceptionLog.setStackTrace(Util.getStackTraceDaException(t));
        exceptionLog.setTipoException(BuscaCausa.desenrolarException(t).getClass().getSimpleName());
        exceptionLog.setCausedBy(BuscaCausa.getCausedBy(t));
        ExceptionLog log = hierarquiaOrganizacionalFacade.getSistemaFacade().geraExceptionLog(exceptionLog);
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao() + " Error: " + log.getId(), ex.getMessage());
    }

    public void salvar(List<PeriodoFase> periodoFases, List<PeriodoFaseUnidade> periodoFaseUnidades, PeriodoFaseUnidade periodoFaseUnidade) {
        try {
            for (PeriodoFase periodoFase : periodoFases) {
                periodoFase.setInicioVigencia(periodoFaseUnidade.getInicioVigencia());
                periodoFase.setFimVigencia(periodoFaseUnidade.getFimVigencia());
                for (PeriodoFaseUnidade faseUnidade : periodoFaseUnidades) {
                    faseUnidade.setInicioVigencia(periodoFaseUnidade.getInicioVigencia());
                    faseUnidade.setFimVigencia(periodoFaseUnidade.getFimVigencia());
                    faseUnidade.setSituacaoPeriodoFase(periodoFaseUnidade.getSituacaoPeriodoFase());
                    faseUnidade.setPeriodoFase(periodoFase);
                }
                em.merge(periodoFase);
                salvarAlteracaoNotificaoPeriodoFase(periodoFase);
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void marcarNotificarComoLida(PeriodoFase entity, Notificacao notificacaoRecuperada) {
        if (notificacaoRecuperada != null) {
            NotificacaoService.getService().marcarComoLida(notificacaoRecuperada);
        }
    }

    public void salvarNotificaoAprovacaoPeriodoFase(PeriodoFase periodoFase) {
        if (periodoFase.getPeriodoFaseUnidades() != null) {
            List<Notificacao> notificacaos = Lists.newArrayList();
            for (PeriodoFaseUnidade periodoFaseUnidade : periodoFase.getPeriodoFaseUnidades()) {
                if (periodoFaseUnidade.getSituacaoPeriodoFase().equals(SituacaoPeriodoFase.ABERTO)) {
                    Notificacao notificacao = new Notificacao();
                    notificacao.setTitulo("Período Fase Liberado ");
                    notificacao.setDescricao("A Unidade: " + periodoFaseUnidade.getUnidadeOrganizacional()
                        + " foi aberta para a Fase: " + periodoFase.getFase()
                        + " no período de " + DataUtil.getDataFormatada(periodoFaseUnidade.getInicioVigencia())
                        + " à " + DataUtil.getDataFormatada(periodoFaseUnidade.getFimVigencia()));
                    notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
                    notificacao.setTipoNotificacao(TipoNotificacao.LIBERAR_PERIODO_FASE);
                    notificacao.setUnidadeOrganizacional(periodoFaseUnidade.getUnidadeOrganizacional());
                    notificacaos.add(notificacao);
                }
            }
            NotificacaoService.getService().notificar(notificacaos);
        }
    }

    public void salvarAlteracaoNotificaoPeriodoFase(PeriodoFase periodoFase) {
        List<Notificacao> notificacoes = Lists.newArrayList();
        for (PeriodoFaseUnidade periodoFaseUnidade : periodoFase.getPeriodoFaseUnidades()) {
            Notificacao notificacao = new Notificacao();
            notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
            notificacao.setTipoNotificacao(TipoNotificacao.LIBERAR_PERIODO_FASE);
            notificacao.setUnidadeOrganizacional(periodoFaseUnidade.getUnidadeOrganizacional());

            if (periodoFaseUnidade.getSituacaoPeriodoFase().equals(SituacaoPeriodoFase.ABERTO)) {
                notificacao.setTitulo("Período Fase Liberado ");
                notificacao.setDescricao("A Unidade: " + periodoFaseUnidade.getUnidadeOrganizacional()
                    + " foi aberta para a Fase: " + periodoFase.getFase()
                    + " no período de " + DataUtil.getDataFormatada(periodoFaseUnidade.getInicioVigencia())
                    + " à " + DataUtil.getDataFormatada(periodoFaseUnidade.getFimVigencia()));
            } else {
                notificacao.setTitulo("Período Fase Bloqueado ");
                notificacao.setDescricao("A Unidade: " + periodoFaseUnidade.getUnidadeOrganizacional()
                    + " foi fechada para a Fase: " + periodoFase.getFase()
                    + " no período de " + DataUtil.getDataFormatada(periodoFaseUnidade.getInicioVigencia())
                    + " à " + DataUtil.getDataFormatada(periodoFaseUnidade.getFimVigencia()));
            }
            notificacoes.add(notificacao);
        }
        NotificacaoService.getService().notificar(notificacoes);
    }


    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public RelatorioPesquisaGenericoFacade getRelatorioPesquisaGenericoFacade() {
        return relatorioPesquisaGenericoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public GrupoUsuarioFacade getGrupoUsuarioFacade() {
        return grupoUsuarioFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

}
