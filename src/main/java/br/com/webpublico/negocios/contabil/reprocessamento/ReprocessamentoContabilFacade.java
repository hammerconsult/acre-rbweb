package br.com.webpublico.negocios.contabil.reprocessamento;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.entidadesauxiliares.contabil.ReprocessamentoContabil;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.FechamentoMensalFacade;
import br.com.webpublico.negocios.contabil.ReprocessamentoContabilHistoricoFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcReprocessamentoContabilDAO;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@Stateless
public class ReprocessamentoContabilFacade implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ReprocessamentoContabilFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContabilizadorFacade contabilizadorFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private AtualizaEventoContabilFacade atualizaEventoContabilFacade;
    @EJB
    private ReprocessamentoContabilHistoricoFacade reprocessamentoContabilHistoricoFacade;
    @EJB
    private ReprocessamentoLancamentoContabilFacade reprocessamentoLancamentoContabilFacade;
    private JdbcReprocessamentoContabilDAO jdbcReprocessamentoContabilDAO;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private TipoContaAuxiliarFacade tipoContaAuxiliarFacade;
    @EJB
    private TransferenciaSaldoContaAuxiliarFacade transferenciaSaldoContaAuxiliarFacade;
    @EJB
    private FechamentoMensalFacade fechamentoMensalFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcReprocessamentoContabilDAO = (JdbcReprocessamentoContabilDAO) ap.getBean("jdbcReprocessamentoContabilDAO");
    }


    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<ReprocessamentoContabil> reprocessar(ReprocessamentoContabil reprocessamentoContabil) {
        BarraProgressoAssistente barraProgressoAssistente = reprocessamentoContabil.getBarraProgressoAssistente();
        barraProgressoAssistente.setMensagem("<b> <font color='blue'>... INICIANDO ...</font> </b>");
        List<EventosReprocessar> eventos = recuperarObjetos(reprocessamentoContabil);
        barraProgressoAssistente.setMensagem(" Preparando informações de " + eventos.size() + "eventos!");
        reprocessamentoContabil.setEventosReprocessar(eventos);
        reprocessarEventos(reprocessamentoContabil);
        if (getReprocessamentoLancamentoContabilSingleton().getReprocessamentos() != null) {
            if (getReprocessamentoLancamentoContabilSingleton().getReprocessamentos().isEmpty()) {
                finalizar(barraProgressoAssistente);
            }
        }
        return new AsyncResult<>(reprocessamentoContabil);
    }

    public void finalizar(BarraProgressoAssistente barraProgressoAssistente) {
        barraProgressoAssistente.finaliza();
        finalizarSingleton();
        barraProgressoAssistente.setMensagem(" Finalizado!");
    }


    public void apagar(ReprocessamentoContabil reprocessamentoContabil) {
        jdbcReprocessamentoContabilDAO.removerLancamentoSaldo(reprocessamentoContabil);
    }


    public void inicializarSingleton(ReprocessamentoContabil reprocessamentoContabil) {
        reprocessamentoLancamentoContabilSingleton.inicializar(0, reprocessamentoContabil.getUsuarioSistema(), new Date(), reprocessamentoContabil.getDataInicio(),
            reprocessamentoContabil.getDataFim(), reprocessamentoContabil.getUnidadeOrganizacional(), Boolean.FALSE);

    }

    public void buscarContasAuxiliaresDetalhadas(Exercicio exercicio) {
        tipoContaAuxiliarFacade.popularContasAuxiliaresDetalhadas(exercicio);
    }

    public void finalizarSingleton() {
        if (reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().getProcessados().compareTo(reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().getTotal()) == 0) {
            reprocessamentoLancamentoContabilFacade.salvarNotificao(true);
        } else {
            reprocessamentoLancamentoContabilFacade.salvarNotificao(false);
        }
        reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> <font color='blue'>... FIM ...</font> </b>");
        reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> <font color='blue'>... Salvando histórico...</font> </b>");
        reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().setDataHoraTermino(new Date());
        reprocessamentoLancamentoContabilSingleton.finalizar();
        reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> <font color='blue'>... Finalizado...</font> </b>");
        salvarHistorico(reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void salvarHistorico(ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        atualizaEventoContabilFacade.salvarObjetoReprocessamento(reprocessamentoContabilHistorico);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(value = 6, unit = TimeUnit.HOURS)
    private void reprocessarEventos(ReprocessamentoContabil reprocessamentoContabil) {
        for (EventosReprocessar evento : reprocessamentoContabil.getEventosReprocessar()) {
            reprocessamentoContabil.getBarraProgressoAssistente().setMensagem("<b> Reprocessando " + evento.getObjetosParaReprocessar().size()
                + " lançamentos de " + evento.getEventoContabil().getTipoEventoContabil().getDescricao().toUpperCase()
                + " e do tipo " + evento.getEventoContabil().getTipoLancamento().getDescricao().toUpperCase() + " ...</b>");

            for (Object objeto : evento.getObjetosParaReprocessar()) {
                if (!reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                    break;
                }
                try {
                    contabilizadorFacade.contabilizar(evento, (EntidadeContabil) objeto);
                    reprocessamentoLancamentoContabilSingleton.adicionarReprocessandoLog(objeto, evento);
                    reprocessamentoLancamentoContabilSingleton.contaSemErro();
                    contarRegistroProcessado(reprocessamentoContabil.getBarraProgressoAssistente());
                    atualizaEventoContabilFacade.processar(objeto);
                } catch (RuntimeException ex) {
                    reprocessamentoLancamentoContabilSingleton.adicionarReprocessandoErroLog(ex.getMessage(), objeto, evento);
                    reprocessamentoLancamentoContabilSingleton.contaComErro();
                    continue;
                } catch (Exception ex) {
                    reprocessamentoLancamentoContabilSingleton.adicionarReprocessandoErroLog(ex.getMessage(), objeto, evento);
                    reprocessamentoLancamentoContabilSingleton.contaComErro();
                    continue;
                } finally {
                    reprocessamentoLancamentoContabilSingleton.conta();
                }
            }
        }
        getReprocessamentoLancamentoContabilSingleton().removerReprocessamentoUsuario(reprocessamentoContabil);
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<ReprocessamentoContabil> reprocessarTransferencias(ReprocessamentoContabil reprocessamentoContabil) {
        List<TransferenciaSaldoContaAuxiliar> transferencias = transferenciaSaldoContaAuxiliarFacade.buscarTransferenciasPorPeriodo(reprocessamentoContabil.getDataInicio(), reprocessamentoContabil.getDataFim(), reprocessamentoContabil.getListaHierarquias());
        if (!transferencias.isEmpty()) {
            reprocessamentoContabil.getBarraProgressoAssistente().setMensagem("<b> Reprocessando Transferência de Saldo de Conta Auxiliar ...</b>");
            for (TransferenciaSaldoContaAuxiliar transferencia : transferencias) {
                if (!reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                    break;
                }
                try {
                    contabilizadorFacade.contabilizarTransferenciaSaldoContaAuxiliar(transferencia);
                    reprocessamentoLancamentoContabilSingleton.adicionarLog(" Reprocessou o(a) " + Persistencia.getNomeDaClasse(transferencia.getClass()) + ": <b>" + transferencia.toString() + "</b>");
                    reprocessamentoLancamentoContabilSingleton.contaSemErro();
                    contarRegistroProcessado(reprocessamentoContabil.getBarraProgressoAssistente());
                } catch (Exception ex) {
                    reprocessamentoLancamentoContabilSingleton.adicionarLog(" Erro ao Reprocessar o(a) " + Persistencia.getNomeDaClasse(transferencia.getClass()) + " : " + transferencia.toString() + " <b> <font color='red'>" + ex.getMessage());
                    reprocessamentoLancamentoContabilSingleton.contaComErro();
                    continue;
                } finally {
                    reprocessamentoLancamentoContabilSingleton.conta();
                }
            }
        }
        getReprocessamentoLancamentoContabilSingleton().removerReprocessamentoUsuario(reprocessamentoContabil);
        return new AsyncResult<>(reprocessamentoContabil);
    }


    private List<EventosReprocessar> recuperarObjetos(ReprocessamentoContabil reprocessamentoContabil) {
        BarraProgressoAssistente barraProgressoAssistente = reprocessamentoContabil.getBarraProgressoAssistente();
        List<EventosReprocessar> eventos = recuperarEventosContabeis(reprocessamentoContabil);
        reprocessamentoLancamentoContabilSingleton.organizarEventosQuandoForInicial(eventos);
        recuperarObjetosPorEventoContabil(reprocessamentoContabil, barraProgressoAssistente, eventos);
        return eventos;
    }

    private void recuperarObjetosPorEventoContabil(ReprocessamentoContabil reprocessamentoContabil, BarraProgressoAssistente barraProgressoAssistente, List<EventosReprocessar> eventos) {
        for (EventosReprocessar er : eventos) {
            if (!reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                break;
            }
            try {
                List<EntidadeContabil> lista = contabilizadorFacade.recuperarObjetosDoEvento(reprocessamentoContabil.getExercicio(), er, Boolean.TRUE);
                barraProgressoAssistente.setMensagem("<b> Recuperou " + lista.size()
                    + " lançamentos de "
                    + er.getEventoContabil().getTipoEventoContabil().getDescricao().toUpperCase()
                    + " e do tipo " + er.getEventoContabil().getTipoLancamento().getDescricao().toUpperCase() + " ...</b>");
                barraProgressoAssistente.setTotal(barraProgressoAssistente.getTotal() + lista.size());
                reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().setTotal(lista.size());
                reprocessamentoLancamentoContabilSingleton.setQuantidadeTotalObjetos(reprocessamentoLancamentoContabilSingleton.getQuantidadeTotalObjetos()
                    + reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().getTotal());
                er.setObjetosParaReprocessar(Lists.newArrayList());
                er.getObjetosParaReprocessar().addAll(lista);
            } catch (Exception ex) {
                continue;
            }
        }
    }

    private List<EventosReprocessar> recuperarEventosContabeis(ReprocessamentoContabil reprocessamentoContabil) {
        List<EventosReprocessar> eventos = Lists.newArrayList();
        reprocessamentoContabil.setEventos(getEventoContabilFacade().buscarEventoPorTipoBalanceteAndTipoEventoContabil(reprocessamentoContabil));
        for (EventoContabil er : reprocessamentoContabil.getEventos()) {
            EventosReprocessar eventosReprocessar = new EventosReprocessar();
            eventosReprocessar.setEventoContabil(er);
            eventosReprocessar.setDataInicial(reprocessamentoContabil.getDataInicio());
            eventosReprocessar.setDataFinal(reprocessamentoContabil.getDataFim());
            eventosReprocessar.setDataRegistro(new Date());

            eventosReprocessar.setEventoReprocessarUOs(Lists.<EventoReprocessarUO>newArrayList());
            for (HierarquiaOrganizacional hierarquiaOrganizacional : reprocessamentoContabil.getListaHierarquias()) {
                eventosReprocessar.getEventoReprocessarUOs().add(new EventoReprocessarUO(eventosReprocessar, hierarquiaOrganizacional.getSubordinada()));
            }

            eventos.add(eventosReprocessar);
        }
        return eventos;
    }

    private void contarRegistroProcessado(BarraProgressoAssistente assistente) {
        assistente.setProcessados(assistente.getProcessados() + 1);
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public ReprocessamentoContabilHistoricoFacade getReprocessamentoContabilHistoricoFacade() {
        return reprocessamentoContabilHistoricoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public FechamentoMensalFacade getFechamentoMensalFacade() {
        return fechamentoMensalFacade;
    }

    public ConfiguracaoContabil buscarConfiguracaoContabilVigente() {
        return configuracaoContabilFacade.configuracaoContabilVigente();
    }
}
