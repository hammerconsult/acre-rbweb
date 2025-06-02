package br.com.webpublico.negocios.contabil.reprocessamento;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.controle.contabil.reprocessamento.ReprocessamentoContabilControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ReprocessamentoContabil;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.MovimentoContabilDTO;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.ParametroEventoDTO;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.Singleton;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 23/12/13
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
@Singleton
@AccessTimeout(value = 50000)
public class ReprocessamentoLancamentoContabilSingleton implements Serializable {

    private static final Integer QUANTIDADE_LOG_OCORRENCIA = 20;
    private boolean calculando;
    private ReprocessamentoContabilHistorico reprocessamentoContabilHistorico;
    private ReprocessamentoContabilControlador.TipoLog tipoLog;
    private ReprocessamentoContabilControlador.ExtensaoArquioLog extensaoArquioLog;
    private DualListModel<EventoContabil> eventosLog;
    private Exercicio exercicioLogado;
    //reprocessamento utilizado pelo usuario
    private List<ReprocessamentoContabil> reprocessamentos;
    private Map<String, SuperFacadeContabil> mapa;

    private MovimentoContabilDTO movimentoContabilDTO;
    private Integer quantidadeTotalObjetos;

    public static Integer getQuantidadeLogOcorrencia() {
        return QUANTIDADE_LOG_OCORRENCIA;
    }

    @PostConstruct
    public void init() {

    }

    public Map<String, SuperFacadeContabil> getMapa() {
        if (mapa == null) {
            mapa = new HashMap<>();
        }
        return mapa;
    }

    public void limpar() {
        reprocessamentoContabilHistorico = null;
        eventosLog = null;
        exercicioLogado = null;
    }

    public String getMensagemConcorrenciaEnquantoReprocessa() {
        return "Está ocorrendo o <b> REPROCESSAMENTO CONTÁBIL </b> nesse momento, por isso aguarde aproximadamente <b>" + reprocessamentoContabilHistorico.getTempoEstimado() + "</b> para continuar a Operação.";
    }

    public void visualizarLog(ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        this.reprocessamentoContabilHistorico = reprocessamentoContabilHistorico;
        this.calculando = false;
        this.tipoLog = ReprocessamentoContabilControlador.TipoLog.COMPLETO;
        this.extensaoArquioLog = ReprocessamentoContabilControlador.ExtensaoArquioLog.PDF;

        List<EventoContabil> eventoContabils = new ArrayList<>();
        int quantidadeLogComErro = 0;
        for (ReprocessamentoLancamentoContabilLog reprocessamentoLancamentoContabilLog : reprocessamentoContabilHistorico.getMensagens()) {
            if (!eventoContabils.contains(reprocessamentoLancamentoContabilLog.getEventoContabil())) {
                eventoContabils.add(reprocessamentoLancamentoContabilLog.getEventoContabil());
            }
            if (reprocessamentoLancamentoContabilLog.getLogDeErro()) {
                quantidadeLogComErro++;
            }
        }
        this.eventosLog = new DualListModel<>(new ArrayList(), eventoContabils);

        this.reprocessamentoContabilHistorico.setProcessadosComErro(quantidadeLogComErro);
        this.reprocessamentoContabilHistorico.setProcessadosSemErro(this.reprocessamentoContabilHistorico.getProcessados() - quantidadeLogComErro);
    }

    public void atribuirReprocessamentoUsuario(ReprocessamentoContabil reprocessamentoContabil) {
        this.reprocessamentos.add(reprocessamentoContabil);
    }

    public void removerReprocessamentoUsuario(ReprocessamentoContabil reprocessamentoContabil) {
        this.reprocessamentos.remove(reprocessamentoContabil);
    }

    public void inicializar(Integer total, UsuarioSistema usuarioSistema, Date data, Date dataInicial, Date dataFinal, UnidadeOrganizacional unidadeOrganizacional, Boolean reprocessamentoInicial) {
        atribuirNullMovimentoContabil();
        reprocessamentoContabilHistorico = new ReprocessamentoContabilHistorico();
        reprocessamentoContabilHistorico.setDataHoraInicio(new Date());
        reprocessamentoContabilHistorico.setProcessados(0);
        reprocessamentoContabilHistorico.setProcessadosComErro(0);
        reprocessamentoContabilHistorico.setProcessadosSemErro(0);
        reprocessamentoContabilHistorico.setTotal(total);
        reprocessamentoContabilHistorico.setUsuarioSistema(usuarioSistema);
        reprocessamentoContabilHistorico.setDataHistorico(data);
        reprocessamentoContabilHistorico.setDataInicial(dataInicial);
        reprocessamentoContabilHistorico.setDataFinal(dataFinal);
        reprocessamentoContabilHistorico.setUnidadeOrganizacional(unidadeOrganizacional);
        reprocessamentoContabilHistorico.setReprocessamentoInicial(reprocessamentoInicial);
        calculando = true;
        reprocessamentoContabilHistorico.setTempo(System.currentTimeMillis());
        reprocessamentoContabilHistorico.setMensagens(new ArrayList<ReprocessamentoLancamentoContabilLog>());
        eventosLog = new DualListModel<EventoContabil>(new ArrayList<EventoContabil>(), new ArrayList<EventoContabil>());
        extensaoArquioLog = ReprocessamentoContabilControlador.ExtensaoArquioLog.PDF;
        mapa = new HashMap<>();
        reprocessamentos = Lists.newArrayList();
    }

    public void preparaDialogLog(ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        tipoLog = ReprocessamentoContabilControlador.TipoLog.COMPLETO;
        reprocessamentoContabilHistorico.setSelecionarEntidadesLog(false);
    }

    public void preparaDialogLogErros(ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        tipoLog = ReprocessamentoContabilControlador.TipoLog.SOMENTE_ERROS_DISTINTOS;
        reprocessamentoContabilHistorico.setSelecionarEntidadesLog(false);
    }

    public synchronized void finalizar() {
        calculando = false;
    }

    public synchronized void conta() {
        reprocessamentoContabilHistorico.setProcessados(reprocessamentoContabilHistorico.getProcessados() + 1);
    }

    public synchronized void contaComErro() {
        reprocessamentoContabilHistorico.setProcessadosComErro(reprocessamentoContabilHistorico.getProcessadosComErro() + 1);
    }

    public synchronized void contaSemErro() {
        reprocessamentoContabilHistorico.setProcessadosSemErro(reprocessamentoContabilHistorico.getProcessadosSemErro() + 1);
    }

    public Exercicio getExercicioLogado() {
        return exercicioLogado;
    }

    public void setExercicioLogado(Exercicio exercicioLogado) {
        this.exercicioLogado = exercicioLogado;
    }

    public ReprocessamentoContabilControlador.ExtensaoArquioLog getExtensaoArquioLog() {
        return extensaoArquioLog;
    }

    public void setExtensaoArquioLog(ReprocessamentoContabilControlador.ExtensaoArquioLog extensaoArquioLog) {
        this.extensaoArquioLog = extensaoArquioLog;
    }

    public ReprocessamentoContabilControlador.TipoLog getTipoLog() {
        return tipoLog;
    }

    public void setTipoLog(ReprocessamentoContabilControlador.TipoLog tipoLog) {
        this.tipoLog = tipoLog;
    }

    public DualListModel<EventoContabil> getEventosLog() {
        return eventosLog;
    }

    public void setEventosLog(DualListModel<EventoContabil> eventosLog) {
        this.eventosLog = eventosLog;
    }

    public ReprocessamentoContabilHistorico getReprocessamentoContabilHistorico() {
        return reprocessamentoContabilHistorico;
    }

    public void setReprocessamentoContabilHistorico(ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        this.reprocessamentoContabilHistorico = reprocessamentoContabilHistorico;
    }

    public void abortar() {
        calculando = false;
    }

    public boolean isCalculando() {
        return calculando;
    }

    public void setCalculando(boolean calculando) {
        this.calculando = calculando;
    }

    public String getLog() {
        List<ReprocessamentoLancamentoContabilLog> mensagens = reprocessamentoContabilHistorico.getMensagens();
        return gerarLogDoHistorico(mensagens);

    }

    public String gerarLogDoHistorico(List<ReprocessamentoLancamentoContabilLog> mensagens) {
        if (calculando) {
            StringBuilder logFinal = new StringBuilder();
            int tamanhoTodo = QUANTIDADE_LOG_OCORRENCIA;
            if (mensagens.size() < QUANTIDADE_LOG_OCORRENCIA) {
                tamanhoTodo = mensagens.size();
            }
            try {
                List<ReprocessamentoLancamentoContabilLog> reprocessamentoLancamentoContabilLogs = mensagens.subList(mensagens.size() - tamanhoTodo, mensagens.size());
                for (ReprocessamentoLancamentoContabilLog log : reprocessamentoLancamentoContabilLogs) {
                    logFinal.append(log.getLinhaLog());
                }
                return logFinal.toString();
            } catch (ConcurrentModificationException e) {
                corrigeLog();
                return logFinal.toString();
            }
        } else {
            StringBuilder logFinal = new StringBuilder();
            try {
                for (ReprocessamentoLancamentoContabilLog log : mensagens) {
                    logFinal.append(log.getLinhaLog());
                }
                return logFinal.toString();
            } catch (ConcurrentModificationException e) {
                corrigeLog();
                return logFinal.toString();
            }
        }
    }

    public void corrigeLog() {
        List<ReprocessamentoLancamentoContabilLog> log = reprocessamentoContabilHistorico.getMensagens();
        List<ReprocessamentoLancamentoContabilLog> copia = new ArrayList<ReprocessamentoLancamentoContabilLog>();
        copia.addAll(log);
        log = new ArrayList<ReprocessamentoLancamentoContabilLog>();
        log.addAll(copia);
    }

    public String getLogTodo(ReprocessamentoContabilControlador.TipoLog tipoLog, ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        String br = "</br>";
        List<ReprocessamentoLancamentoContabilLog> mensagens = reprocessamentoContabilHistorico.getMensagens();
        if (ReprocessamentoContabilControlador.TipoLog.COMPLETO.equals(tipoLog)) {
            StringBuilder logFinal = new StringBuilder();
            for (ReprocessamentoLancamentoContabilLog log : mensagens) {
                if (canAdicionarLog(log, reprocessamentoContabilHistorico)) {
                    logFinal.append(log.getLinhaLog());
                }
            }
            return logFinal.toString();
        }
        if (ReprocessamentoContabilControlador.TipoLog.SOMENTE_ERROS.equals(tipoLog)) {
            StringBuilder logFinal = new StringBuilder();
            for (ReprocessamentoLancamentoContabilLog log : mensagens) {
                if (log.getLogDeErro()) {
                    if (canAdicionarLog(log, reprocessamentoContabilHistorico)) {
                        logFinal.append(log.getLinhaLog());
                    }
                }
            }
            return logFinal.toString();
        }
        if (ReprocessamentoContabilControlador.TipoLog.SOMENTE_ERROS_DISTINTOS.equals(tipoLog)) {
            Set<String> logs = new HashSet<>();
            for (ReprocessamentoLancamentoContabilLog log : mensagens) {
                if (log.getLogDeErro()) {
                    if (canAdicionarLog(log, reprocessamentoContabilHistorico)) {
                        logs.add("<b>" + log.getEventoContabil().getTipoEventoContabil() + " - </b>" + getErroRemovendoData(log) + br);
                    }
                }
            }
            ArrayList<String> list = new ArrayList<>(logs);
            Collections.sort(list);
            StringBuilder logFinal = new StringBuilder();
            for (String log : list) {
                logFinal.append(log);
            }
            return logFinal.toString();
        }
        return "... Log do Reprocessamento Contábil ...";
    }

    private String getErroRemovendoData(ReprocessamentoLancamentoContabilLog log) {
        String erro = log.getErro();
        String retorno = "";
        if (erro.contains("Data")) {
            String[] datas = erro.split("Data");
            retorno = datas[0];
        } else {
            retorno = erro;
        }
        return retorno;
    }

    private boolean canAdicionarLog(ReprocessamentoLancamentoContabilLog log, ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        if (reprocessamentoContabilHistorico.getSelecionarEntidadesLog()) {
            if (eventosLog.getTarget().contains(log.getEventoContabil())) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public void adicionarLog(String mensagem) {
        reprocessamentoContabilHistorico.getMensagens().add(new ReprocessamentoLancamentoContabilLog(new Date(), mensagem, "", null, false, reprocessamentoContabilHistorico, null, null));
    }

    public void adicionarLogErro(String mensagem) {
        reprocessamentoContabilHistorico.getMensagens().add(new ReprocessamentoLancamentoContabilLog(new Date(), mensagem, "", null, true, reprocessamentoContabilHistorico, null, null));
    }


    public void adicionarReprocessandoLog(Object objeto, EventosReprocessar eventosReprocessar) {
        reprocessamentoContabilHistorico.getMensagens().add(new ReprocessamentoLancamentoContabilLog(new Date(), " Reprocessou o(a) " + Persistencia.getNomeDaClasse(objeto.getClass()) + ": <b>" + objeto.toString() + "</b>", "", eventosReprocessar.getEventoContabil(), false, reprocessamentoContabilHistorico, ((Long) Persistencia.getId(objeto)), objeto.getClass().getSimpleName()));
    }

    public void adicionarReprocessandoErroLog(String erro, Object objeto, EventosReprocessar eventosReprocessar) {
        try {
            reprocessamentoContabilHistorico.getMensagens().add(new ReprocessamentoLancamentoContabilLog(new Date(), " Erro ao Reprocessar o(a) " + Persistencia.getNomeDaClasse(objeto.getClass()) + " : " + objeto.toString(), " <b> <font color='red'>" + erro + " </font> (" + montarStringReprocessamentoEventoContabil(eventosReprocessar) + ") </b>", eventosReprocessar.getEventoContabil(), true, reprocessamentoContabilHistorico, ((Long) Persistencia.getId(objeto)), objeto.getClass().getSimpleName()));
        } catch (Exception e) {
        }
    }

    public String montarStringReprocessamentoEventoContabil(EventosReprocessar eventosReprocessar) {
        try {
            List<ReprocessamentoLancamentoContabilSingleton.EventosContabeisNormalEstornoTabelasSeparadas> eventosContabeisNormalEstornoTabelasSeparadases = Arrays.asList(ReprocessamentoLancamentoContabilSingleton.EventosContabeisNormalEstornoTabelasSeparadas.values());
            TipoEventoContabil tipoEventoContabil = eventosReprocessar.getEventoContabil().getTipoEventoContabil();
            boolean eventoNomalEstornoTabelaSeparada = false;
            for (EventosContabeisNormalEstornoTabelasSeparadas eventosContabeisNormalEstornoTabelasSeparadase : eventosContabeisNormalEstornoTabelasSeparadases) {
                if (tipoEventoContabil.name().equals(eventosContabeisNormalEstornoTabelasSeparadase.name())) {
                    eventoNomalEstornoTabelaSeparada = true;
                }
            }

            if (reprocessamentoContabilHistorico.getReprocessamentoInicial()) {
                String retorno = eventosReprocessar.getEventoContabil().getTipoEventoContabil().getDescricao();
                if (eventoNomalEstornoTabelaSeparada) {
                    retorno += " - " + eventosReprocessar.getEventoContabil().getTipoLancamento().getDescricao();
                }
                return retorno;
            }

            String retorno = eventosReprocessar.getEventoContabil().toString();
            if (eventoNomalEstornoTabelaSeparada) {
                retorno += " - " + eventosReprocessar.getEventoContabil().getTipoLancamento().getDescricao();
            }
            return retorno;
        } catch (Exception e) {
            return "";
        }
    }

    public void organizarEventosQuandoForInicial(List<EventosReprocessar> eventosDoSelecionado) {
        List<EventosReprocessar> eventos = Lists.newArrayList();
        HashMap<TipoEventoContabil, List<EventoContabil>> mapDeEventos = new HashMap<>();

        eventos.addAll(eventosDoSelecionado);
        eventosDoSelecionado.clear();
        for (EventosReprocessar evento : eventos) {
            TipoEventoContabil tipoEventoContabil = evento.getEventoContabil().getTipoEventoContabil();
            EventoContabil eventoContabil = evento.getEventoContabil();
            verificarEventosReprocessarMesmoEventoContabil(mapDeEventos, tipoEventoContabil, eventoContabil);
        }
        adicionarEventosReprocessar(eventosDoSelecionado, eventos, mapDeEventos);
    }

    private void verificarEventosReprocessarMesmoEventoContabil(HashMap<TipoEventoContabil, List<EventoContabil>> mapDeEventos, TipoEventoContabil tipoEventoContabil, EventoContabil eventoContabil) {
        if (!mapDeEventos.containsKey(tipoEventoContabil)) {

            List<EventoContabil> listaDeEventos = new ArrayList<>();
            listaDeEventos.add(eventoContabil);
            mapDeEventos.put(tipoEventoContabil, listaDeEventos);
        } else {
            List<EventoContabil> eventoContabilList = mapDeEventos.get(tipoEventoContabil);
            EventoContabil eventoContabil1ParaAdicionar = null;
            if (verificarAdicionarEventoNoMapa(eventoContabil, eventoContabilList)) {
                eventoContabil1ParaAdicionar = eventoContabil;
            }
            if (eventoContabil1ParaAdicionar != null) {
                mapDeEventos.get(tipoEventoContabil).add(eventoContabil1ParaAdicionar);
            }
        }
    }

    private void adicionarEventosReprocessar(List<EventosReprocessar> eventosDoSelecionado, List<EventosReprocessar> eventos, HashMap<TipoEventoContabil, List<EventoContabil>> mapDeEventos) {
        for (TipoEventoContabil tipoEventoContabil : mapDeEventos.keySet()) {
            List<EventoContabil> eventoContabilList = mapDeEventos.get(tipoEventoContabil);
            for (EventoContabil eventoContabil : eventoContabilList) {
                for (EventosReprocessar evento : eventos) {
                    if (evento.getEventoContabil().equals(eventoContabil)) {
                        eventosDoSelecionado.add(evento);
                    }
                }
            }
        }
    }

    private Boolean verificarAdicionarEventoNoMapa(EventoContabil contabil, List<EventoContabil> eventoContabilList) {
        List<EventosContabeisNormalEstornoTabelasSeparadas> eventosContabeisNormalEstornoTabelasSeparadases = Arrays.asList(ReprocessamentoLancamentoContabilSingleton.EventosContabeisNormalEstornoTabelasSeparadas.values());
        Boolean podeVerificarTipoLancamento = Boolean.FALSE;
        for (EventosContabeisNormalEstornoTabelasSeparadas eventosContabeisNormalEstornoTabelasSeparadase : eventosContabeisNormalEstornoTabelasSeparadases) {
            if (contabil.getTipoEventoContabil().name().equals(eventosContabeisNormalEstornoTabelasSeparadase.name())) {
                podeVerificarTipoLancamento = Boolean.TRUE;
            }
        }
        if (podeVerificarTipoLancamento) {
            for (EventoContabil eventoContabil : eventoContabilList) {
                if (eventoContabil.getTipoLancamento().equals(contabil.getTipoLancamento())) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public ReprocessamentoContabil getReprocessamentoContabil() {
        if (reprocessamentos != null && !reprocessamentos.isEmpty()) {
            return reprocessamentos.get(0);
        }
        return null;
    }

    public List<ReprocessamentoContabil> getReprocessamentos() {
        return reprocessamentos;
    }

    public DefaultStreamedContent gerarTXTLog(ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        return gerarTXTLog(getLogTodo(tipoLog, reprocessamentoContabilHistorico));
    }

    public Integer getQuantidadeTotalObjetos() {
        return quantidadeTotalObjetos == null ? 0 : quantidadeTotalObjetos;
    }

    public void setQuantidadeTotalObjetos(Integer quantidadeTotalObjetos) {
        if (this.quantidadeTotalObjetos == null) {
            this.quantidadeTotalObjetos = 0;
        }
        this.quantidadeTotalObjetos = quantidadeTotalObjetos;
    }

    public void gerarPDFLog(ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        geraPDFLog(getLogTodo(tipoLog, reprocessamentoContabilHistorico));
    }

    public DefaultStreamedContent gerarTXTLog(String log) {
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade = null;
        try {
            InitialContext initialContext = new InitialContext();
            hierarquiaOrganizacionalFacade = (HierarquiaOrganizacionalFacade) initialContext.lookup("java:module/HierarquiaOrganizacionalFacade");
        } catch (Exception e) {
        }
        String secretaria = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getDataOperacao()).getDescricao();

        String conteudo = secretaria + "</br>"
            + " LOG REPROCESSAMENTO LANÇAMENTO CONTÁBIL </br></br>"
            + log
            + "\"</br></br>" +
            " USUÁRIO RESPONSÁVEL:" + sistemaControlador.getUsuarioCorrente().toString()
            + "</br>"
            + "               DATA:" + sistemaControlador.getDataOperacaoFormatada();

        conteudo = conteudo.replace("</br>", System.getProperty("line.separator"));
        conteudo = conteudo.replace("<b>", " ");
        conteudo = conteudo.replace("</b>", " ");
        conteudo = conteudo.replace("<font color='red'>", " ");
        conteudo = conteudo.replace("<font color='blue'>", " ");
        conteudo = conteudo.replace("</font>", " ");

        String nomeArquivo = "Log de Acompanhamento do Reprocessamento Contábil.txt";

        File arquivo = new File(nomeArquivo);

        try {
            FileOutputStream fos = new FileOutputStream(arquivo);
            fos.write(conteudo.getBytes());
            InputStream stream = new FileInputStream(arquivo);
            return new DefaultStreamedContent(stream, "text/plain", nomeArquivo);
        } catch (Exception e) {
            return null;
        }
    }

    public void geraPDFLog(String log) {
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");

        String caminhoDaImagem = FacesUtil.geraUrlImagemDir() + "/img/Brasao_de_Rio_Branco.gif";
        HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade = null;
        try {
            InitialContext initialContext = new InitialContext();
            hierarquiaOrganizacionalFacade = (HierarquiaOrganizacionalFacade) initialContext.lookup("java:module/HierarquiaOrganizacionalFacade");
        } catch (Exception e) {
        }
        String secretaria = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getDataOperacao()).getDescricao();


        String conteudo = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
            + " </title>"
            + " </head>"
            + " <body>"
            + "<center>"
            + "<table>"
            + "<tr>"
            + "<td><img src=\"" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNICIPIO DE RIO BRANCO\" /></td>"
            + "<td> " + secretaria + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td colspan=\"2\"> <center> LOG REPROCESSAMENTO LANÇAMENTO CONTÁBIL </center> </td>"
            + "</tr>"
            + "</table>"
            + "</center>"
            + "<div style=\"border : solid #92B8D3 1px; \""
            + "<p style='font-size : 8px!important;'>"
            + log
            + "</p>"
            + "</div>"
            + "USUÁRIO RESPONSÁVEL:" + sistemaControlador.getUsuarioCorrente().toString() + "<br/>"
            + "               DATA:" + sistemaControlador.getDataOperacaoFormatada() + "<br/>"
            + " </body>"
            + " </html>";

        Util.downloadPDF("Log de Acompanhamento do Reprocessamento Contábil", conteudo, FacesContext.getCurrentInstance());
    }

    public void criarMovimentoContabil() {
        this.movimentoContabilDTO = new MovimentoContabilDTO();
    }

    public synchronized void atribuirNullMovimentoContabil() {
        this.movimentoContabilDTO = null;
    }

    public synchronized void adicionarMovimentoContabilizar(ParametroEventoDTO parametroEventoDTO) {
        if (getMovimentoContabilDTO() == null) {
            criarMovimentoContabil();
        }
        this.movimentoContabilDTO.getContabilizar().add(parametroEventoDTO);
    }

    public MovimentoContabilDTO getMovimentoContabilDTO() {
        return movimentoContabilDTO;
    }

    public enum EventosContabeisNormalEstornoTabelasSeparadas {

        TRANSFERENCIA_FINANCEIRA,
        TRANSFERENCIA_MESMA_UNIDADE,
        LIBERACAO_FINANCEIRA,
        DESPESA_EXTRA_ORCAMENTARIA,
        RECEITA_REALIZADA,
        OBRIGACAO_APAGAR,
        EMPENHO_DESPESA,
        ABERTURA,
        RESTO_PAGAR,
        LIQUIDACAO_DESPESA,
        LIQUIDACAO_RESTO_PAGAR,
        PAGAMENTO_DESPESA,
        PAGAMENTO_RESTO_PAGAR,
        RECEITA_EXTRA_ORCAMENTARIA,
        PREVISAO_ADICIONAL_RECEITA,
        CREDITO_ADICIONAL;

        EventosContabeisNormalEstornoTabelasSeparadas() {

        }
    }
}
