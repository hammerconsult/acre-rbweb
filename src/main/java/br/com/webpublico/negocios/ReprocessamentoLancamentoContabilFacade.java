/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusEventoReprocessar;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.exception.ExceptionReprocessamentoContabil;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ContabilizadorFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.seguranca.NotificacaoService;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author reidocrime
 */
@Stateless
public class ReprocessamentoLancamentoContabilFacade extends AbstractFacade<EventosReprocessar> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ContabilizadorFacade contabilizadorFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private AtualizaEventoContabilFacade atualizaEventoContabilFacade;

    public ReprocessamentoLancamentoContabilFacade() {
        super(EventosReprocessar.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void limparEM() {
        em.clear();
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public void setaReprocessadoESalvaEventoReprocessar(EventosReprocessar er) {
        er.setStatusEventoReprocessar(StatusEventoReprocessar.REPROCESSADO);

//        JdbcEventosReprocessarDAO jdbcEventosReprocessarDAO = (JdbcEventosReprocessarDAO) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcEventosReprocessarDAO");
//        jdbcEventosReprocessarDAO.persistir(er);
        atualizaEventoContabilFacade.salvarObjetoReprocessamento(er);
    }

    public List<EventosReprocessar> recuperaEventosPorTipo(StatusEventoReprocessar statusEventoReprocessar) {
        Query consulta = em.createQuery("select e from EventosReprocessar e where e.statusEventoReprocessar = :status");
        consulta.setParameter("status", statusEventoReprocessar);
        List<EventosReprocessar> resultList = consulta.getResultList();
        for (EventosReprocessar eventosReprocessar : resultList) {
            eventosReprocessar.getEventoReprocessarUOs().size();
        }
        return resultList;
    }

    public List<ReprocessamentoContabilHistorico> recuperaHistoricos(TipoEventoContabil tipoEventoContabilFiltroLog) {
        String hql = "select distinct e from ReprocessamentoContabilHistorico e ";
        if (tipoEventoContabilFiltroLog != null) {
            hql += " inner join e.mensagens log" +
                " where log.eventoContabil.tipoEventoContabil = :tipo";
        }
        hql += " order by e.id desc";
        Query consulta = em.createQuery(hql, ReprocessamentoContabilHistorico.class);
        if (tipoEventoContabilFiltroLog != null) {
            consulta.setParameter("tipo", tipoEventoContabilFiltroLog);
        }
        try {
            List<ReprocessamentoContabilHistorico> resultList = consulta.getResultList();
            for (ReprocessamentoContabilHistorico reprocessamentoContabilHistorico : resultList) {
                reprocessamentoContabilHistorico.getMensagens().size();
            }
            return resultList;
        } catch (Exception e) {
            return new ArrayList<ReprocessamentoContabilHistorico>();
        }
    }

    public ReprocessamentoContabilHistorico recuperaHistoricosPorDataETipo(Date data, TipoEventoContabil tipoEventoContabilFiltroLog) {
        String hql = "select distinct e from ReprocessamentoContabilHistorico e ";
        hql += " inner join e.mensagens log" +
            " where e.dataHistorico = :data ";
        if (tipoEventoContabilFiltroLog != null) {
            hql += " and log.eventoContabil.tipoEventoContabil = :tipo";
        }
        hql += " order by e.dataHistorico desc, e.id desc";

        Query consulta = em.createQuery(hql, ReprocessamentoContabilHistorico.class);
        consulta.setParameter("data", data);
        if (tipoEventoContabilFiltroLog != null) {
            consulta.setParameter("tipo", tipoEventoContabilFiltroLog);
        }
        try {
            consulta.setMaxResults(1);
            ReprocessamentoContabilHistorico retorno = (ReprocessamentoContabilHistorico) consulta.getSingleResult();
            retorno.getMensagens().size();
            return retorno;
        } catch (Exception e) {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void reprocessar(EventosReprocessar er) {
        reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> Contabilizando " + er.getObjetosParaReprocessar().size() + " lançamentos de " + reprocessamentoLancamentoContabilSingleton.montarStringReprocessamentoEventoContabil(er) + "... </b> ");
        for (Object objeto : er.getObjetosParaReprocessar()) {
            if (!reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                break;
            }
            try {
                contabilizadorFacade.contabilizar(er, (EntidadeContabil) objeto);
                reprocessamentoLancamentoContabilSingleton.adicionarReprocessandoLog(objeto, er);
                reprocessamentoLancamentoContabilSingleton.contaSemErro();
                atualizaEventoContabilFacade.processar(objeto);
            } catch (RuntimeException ex) {
                reprocessamentoLancamentoContabilSingleton.adicionarReprocessandoErroLog(ex.getMessage(), objeto, er);
                reprocessamentoLancamentoContabilSingleton.contaComErro();
                continue;
            } catch (Exception ex) {
                reprocessamentoLancamentoContabilSingleton.adicionarReprocessandoErroLog(ex.getMessage(), objeto, er);
                reprocessamentoLancamentoContabilSingleton.contaComErro();
                continue;
            } finally {
                reprocessamentoLancamentoContabilSingleton.conta();
            }
        }
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void reprocessarNovo(ReprocessamentoLancamentoContabil selecionado, Boolean reprocessarOsErros) {

        try {
            List<ReprocessamentoLancamentoContabilLog> logs = new ArrayList<ReprocessamentoLancamentoContabilLog>();
            if (reprocessarOsErros) {
                logs.addAll(reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().getMensagens());
            }

            reprocessamentoLancamentoContabilSingleton.inicializar(0, selecionado.getUsuarioSistema(), selecionado.getData(), selecionado.getDataInicio(), selecionado.getDataFim(), selecionado.getUnidadeOrganizacional(), selecionado.getReprocessamentoInicial());
            reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> <font color='blue'>... INICIANDO ...</font> </b>");

            if (reprocessarOsErros) {
                reprocessamentoDosErros(selecionado, logs);
            } else {
                reprocessamentoNormal(selecionado);
            }

            for (EventosReprocessar er : selecionado.getLista()) {
                /*reprocessamentoLancamentoContabilSingleton.setEventosReprocessar(er);*/

                reprocessar(er);
                setaReprocessadoESalvaEventoReprocessar(er);
                limparEMs();
            }

        } catch (ExcecaoNegocioGenerica e) {
            reprocessamentoLancamentoContabilSingleton.adicionarReprocessandoErroLog("ExcecaoNegocioGenerica ", e.getMessage(), null);
        } catch (ExceptionReprocessamentoContabil ex) {
            reprocessamentoLancamentoContabilSingleton.adicionarReprocessandoErroLog("ExceptionReprocessamentoContabil ", ex.getMessage(), null);
        } catch (Exception ex) {
            reprocessamentoLancamentoContabilSingleton.adicionarReprocessandoErroLog("Exception ", ex.getMessage(), null);
        } finally {
            finalizarLog();
        }
    }

    public void finalizarLog() {
        if (reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().getProcessados().compareTo(reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().getTotal()) == 0) {
            salvarNotificao(true);
        }

        reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> <font color='blue'>... FIM ...</font> </b>");
        reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> <font color='blue'>... Salvando histórico...</font> </b>");
        salvarHistorico(reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico());
        reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> <font color='blue'>... Histórico salvo...</font> </b>");
        reprocessamentoLancamentoContabilSingleton.finalizar();
        reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> <font color='blue'>... Finalizado...</font> </b>");
    }

    private void reprocessamentoNormal(ReprocessamentoLancamentoContabil selecionado) {
        for (EventosReprocessar er : selecionado.getLista()) {
            /*reprocessamentoLancamentoContabilSingleton.setEventosReprocessar(er);*/

//            eventoContabilFacade.deletaLancamentosContabil(er, selecionado.getReprocessamentoInicial());

            List<EntidadeContabil> lista = contabilizadorFacade.recuperarObjetosDoEvento(reprocessamentoLancamentoContabilSingleton.getExercicioLogado(), er, selecionado.getReprocessamentoInicial());
            reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> Recuperou " + lista.size() + " lançamentos ...</b>");
            reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().setTotal(lista.size());

            er.setObjetosParaReprocessar(new ArrayList<Object>());
            er.getObjetosParaReprocessar().addAll(lista);
        }
    }

    private void reprocessamentoDosErros(ReprocessamentoLancamentoContabil selecionado, List<ReprocessamentoLancamentoContabilLog> logs) {
        reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> <font color='blue'>... RECUPERANDO MOVIMENTOS COM ERROS ...</font> </b>");

        HashMap<EventoContabil, List<Object>> objetos = new HashMap<>();
        for (ReprocessamentoLancamentoContabilLog log : logs) {
            if (log.getLogDeErro()) {
                Long id = log.getIdObjeto();
                String classeObjeto = log.getClasseObjeto();
                if (id != null && classeObjeto != null) {
                    if (!classeObjeto.equals("String")) {
                        Object o = recuperarObjeto(id, classeObjeto);
                        if (o != null) {
                            if (objetos.containsKey(log.getEventoContabil())) {
                                List<Object> objects = objetos.get(log.getEventoContabil());
                                objects.add(o);
                                reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().setTotal(1);
                            } else {
                                List<Object> novo = new ArrayList<Object>();
                                novo.add(o);
                                objetos.put(log.getEventoContabil(), novo);
                                reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabilHistorico().setTotal(1);
                            }
                        }
                    }
                }
            }
        }

        for (EventoContabil eventoContabil : objetos.keySet()) {
            List<Object> objects = objetos.get(eventoContabil);

            EventosReprocessar eventosReprocessar = new EventosReprocessar();
            eventosReprocessar.setEventoContabil(eventoContabil);
            eventosReprocessar.setObjetosParaReprocessar(objects);


            selecionado.getLista().add(eventosReprocessar);
        }

        reprocessamentoLancamentoContabilSingleton.adicionarLog("<b> <font color='blue'>... INICIANDO REPREOCESSASAMENTO DOS ERROS ...</font> </b>");
    }

    public void salvarHistorico(ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        atualizaEventoContabilFacade.salvarObjetoReprocessamento(reprocessamentoContabilHistorico);
    }

    public AtualizaEventoContabilFacade getAtualizaEventoContabilFacade() {
        return atualizaEventoContabilFacade;
    }

    public ContabilizacaoFacade getContabilizacaoFacade() {
        return contabilizacaoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public void salvarNotificao(Boolean deuCerto) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Acesse o link e clique para visualizar o Log");
        if (deuCerto) {
            notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
            notificacao.setTitulo("Reprocessamento finalizado com sucesso!");
        } else {
            notificacao.setGravidade(Notificacao.Gravidade.ERRO);
            notificacao.setTitulo("Erro ao efetuar o Reprocessamento!");
        }
        notificacao.setLink("/reprocessamento-contabil/novo/");
        notificacao.setTipoNotificacao(TipoNotificacao.REPROCESSAMENTO_CONTABIL);
        NotificacaoService.getService().notificar(notificacao);
    }

    private Object recuperarObjeto(Long idObejto, String classe) {
        try {
            String nomeDaClasse = "br.com.webpublico.entidades." + classe;
            Class classeRecuperada = null;
            try {
                classeRecuperada = Class.forName(nomeDaClasse);
            } catch (ClassNotFoundException ex) {
                return null;
            }
            Query consulta = em.createQuery("select obj from " + classe + " obj where obj.id = :id", classeRecuperada);
            consulta.setParameter("id", idObejto);
            consulta.setMaxResults(1);
            return consulta.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void removerHistorico(ReprocessamentoContabilHistorico historico) {
        em.remove(em.getReference(ReprocessamentoContabilHistorico.class, historico.getId()));
    }

    public ContabilizadorFacade getContabilizadorFacade() {
        return contabilizadorFacade;
    }

    public void limparEMs() {

        limparEM();
        getContabilizacaoFacade().limparEM();
        getContabilizadorFacade().limparEM();
        getAtualizaEventoContabilFacade().limparEM();
    }
}
