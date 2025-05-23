
package br.com.webpublico.controle.rh.administracaodepagamento.simulacaoimpactofinanceiro;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.FilaProcessamentoFolha;
import br.com.webpublico.entidades.rh.administracaodepagamento.FolhaDePagamentoSimulacao;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoCalculo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.administracaodepagamento.FilaProcessamentoFolhaFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.FolhaDePagamentoSimulacaoFacade;
import br.com.webpublico.script.ItemErroScript;
import br.com.webpublico.singletons.SingletonFolhaDePagamento;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@ManagedBean
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "lista-folha-simulacao", pattern = "/folhadepagamento-simulacao/listar/", viewId = "/faces/rh/administracaodepagamento/folhadepagamentosimulacao/lista.xhtml"),
    @URLMapping(id = "novo-folha-simulacao", pattern = "/folhadepagamento-simulacao/novo/", viewId = "/faces/rh/administracaodepagamento/folhadepagamentosimulacao/edita.xhtml"),
    @URLMapping(id = "gerando-folha-simulacao", pattern = "/folhadepagamento-simulacao/acompanhamento/", viewId = "/faces/rh/administracaodepagamento/folhadepagamentosimulacao/log.xhtml"),
    @URLMapping(id = "detalhes-folha-simulacao", pattern = "/folhadepagamento-simulacao/detalhes-folha-pagamento/", viewId = "/faces/rh/administracaodepagamento/folhadepagamentosimulacao/detalhes.xhtml"),
    @URLMapping(id = "visualizar-folha-simulacao", pattern = "/folhadepagamento-simulacao/visualizar/", viewId = "/faces/rh/administracaodepagamento/folhadepagamentosimulacao/visualizar.xhtml")
})
public class CalculoFolhaDePagamentoSimulacaoControlador extends PrettyControlador<FolhaDePagamentoSimulacao> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CalculoFolhaDePagamentoSimulacaoControlador.class);

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private FolhaDePagamentoSimulacaoFacade folhaDePagamentoSimulacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private PessoaFisica pessoaFisica;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private ConverterAutoComplete converterPessoaFisica;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;
    private List<ItemErroScript> itemErroScripts = new ArrayList<ItemErroScript>();
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private VinculoFP servidor;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private List<ContratoFP> contratosSemEnquadramento;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private DetalheProcessamentoFolha detalheProcessamentoFolha;
    private Long tempo;
    private boolean calculaTodos;
    private TipoCalculo tipoCalculo;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    private LoteProcessamento loteProcessamento;

    @EJB
    private CalendarioFPFacade calendarioFPFacade;
    @EJB
    private LoteProcessamentoFacade loteProcessamentoFacade;
    private ConverterGenerico converterLoteProcessamento;
    @EJB
    private SingletonFolhaDePagamento singletonFolhaDePagamento;
    private CompetenciaFP competenciaFP;

    private ConverterGenerico converterCompetenciaFP;
    private List<EventoFP> eventosBloqueados;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FilaProcessamentoFolhaFacade filaProcessamentoFolhaFacade;
    private boolean imprimeLogEmArquivo;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    public CalculoFolhaDePagamentoSimulacaoControlador() {
        metadata = new EntidadeMetaData(FolhaDePagamentoSimulacao.class);
    }

    public FolhaDePagamentoFacade getFacade() {
        return folhaDePagamentoFacade;
    }

    public VinculoFP getServidor() {
        return servidor;
    }

    public void setServidor(VinculoFP servidor) {
        this.servidor = servidor;
    }

    public CompetenciaFP getCompetenciaFP() {
        return competenciaFP;
    }

    public void setCompetenciaFP(CompetenciaFP competenciaFP) {
        this.competenciaFP = competenciaFP;
    }

    public List<EventoFP> getEventosBloqueados() {
        return eventosBloqueados;
    }

    public void setEventosBloqueados(List<EventoFP> eventosBloqueados) {
        this.eventosBloqueados = eventosBloqueados;
    }


    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterContratoFP;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/folhadepagamento-simulacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-folha-simulacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCalculadaEm(SistemaFacade.getDataCorrente());
        pessoaFisica = new PessoaFisica();
        servidor = null;
        contratosSemEnquadramento = new ArrayList<>();
        hierarquiaOrganizacional = null;
        tempo = 0l;
        calculaTodos = true;
        tipoCalculo = TipoCalculo.INDIVIDUAL;
        loteProcessamento = null;
        competenciaFP = null;
        eventosBloqueados = null;
        inicializaDependenciasDoCalculo();
    }

    private void inicializaDependenciasDoCalculo() {

        if (detalheProcessamentoFolha == null || detalheProcessamentoFolha.isLiberaCalculo()) {
            detalheProcessamentoFolha = new DetalheProcessamentoFolha();
        }
    }

    private void criarAssistente() {
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.setSelecionado(selecionado);
        assistenteBarraProgresso.setExecutando(true);
        assistenteBarraProgresso.setDescricaoProcesso("Cálculo da Folha de Pagamento - SIMULAÇÃO");
        assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivelSemView(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    @Override
    public void salvar() {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        super.salvar();
    }

    @Override
    public AbstractFacade getFacede() {
        return folhaDePagamentoSimulacaoFacade;
    }

    public void fixaUnidadePrincipal() {
        hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getRaizHierarquia(UtilRH.getDataOperacao());
    }


    public List<VinculoFP> completarContratoFP(String parte) {
        if (selecionado != null && selecionado.getTipoFolhaDePagamento() != null) {
            if (!TipoFolhaDePagamento.isFolhaRescisao(selecionado)) {
                return contratoFPFacade.recuperaContratoVigenteSemCedenciaOuAfastamento(parte.trim(), selecionado.getMes().getNumeroMes(), selecionado.getAno());
            }

            if (TipoFolhaDePagamento.isFolhaRescisao(selecionado)) {
                return contratoFPFacade.buscarContratosExoneradosSemFolhaEfetivadaFiltrando(parte.trim(), selecionado);
            }
            return new ArrayList<>();
        } else {
            return new ArrayList<>();
        }
    }

    FolhaDePagamentoSimulacao getFolhaDePagamento() {
        return selecionado;
    }

    public boolean isPodeMostrarBotaoCalcularTodosLotes() {
        HashSet users = Sets.newHashSet();
        users.add("mailson");
        users.add("alysson");
        users.add("cleber");
        users.add("KSOUZA");
        return users.contains(sistemaControlador.getUsuarioCorrente().getLogin());
    }

    public void calcularTodosLotesDoMes() {
        List<LoteProcessamento> lotes = new LinkedList<>();
        lotes.addAll(loteProcessamentoFacade.getLotesPorMesAnoAndTipoFolhaDePagamento(getFolhaDePagamento().getMes(), getFolhaDePagamento().getAno(), getFolhaDePagamento().getTipoFolhaDePagamento()));
        FolhaDePagamento f = getFolhaClonada();
        for (LoteProcessamento lote : lotes) {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    logger.debug(e.getMessage());
                }
                if (!singletonFolhaDePagamento.isCalculandoFolha()) {
                    novo();
                    getFolhaDePagamento().setQtdeMesesRetroacao(lote.getQtdeMesesRetroacao());
                    detalheProcessamentoFolha.setQuantidadeMesesRetroativos(lote.getQtdeMesesRetroacao());
                    this.loteProcessamento = lote;
                    this.tipoCalculo = TipoCalculo.LOTE;
                    calcularFolha();
                    break;
                }
            }
        }
        FacesUtil.addInfo("", "Terminado o processamento de todos os lotes.");
    }

    public FolhaDePagamento getFolhaClonada() {
        try {
            return (FolhaDePagamento) BeanUtils.cloneBean(selecionado);
        } catch (IllegalAccessException e) {
            logger.debug(e.getMessage());
        } catch (InstantiationException e) {
            logger.debug(e.getMessage());
        } catch (InvocationTargetException e) {
            logger.debug(e.getMessage());
        } catch (NoSuchMethodException e) {
            logger.debug(e.getMessage());
        }
        return null;
    }

    public void validarCalculoPorTipo() {
        ValidacaoException val = new ValidacaoException();
        if (tipoCalculo != null) {
            if (TipoCalculo.INDIVIDUAL.equals(tipoCalculo) && servidor == null) {
                val.adicionarMensagemDeCampoObrigatorio("Selecione uma Pessoa para o cálculo");
            }
            if (TipoCalculo.ORGAO.equals(tipoCalculo) && hierarquiaOrganizacional == null) {
                val.adicionarMensagemDeCampoObrigatorio("Selecione um Orgão para o cálculo");
            }
            if (calendarioFPFacade.buscarDataCalculoCalendarioFPPorFolha(getFolhaDePagamento()) == null) {
                val.adicionarMensagemDeCampoObrigatorio("Data de Calendário não encontrada");
            }
            if (TipoCalculo.LOTE.equals(tipoCalculo) && loteProcessamento == null) {
                val.adicionarMensagemDeCampoObrigatorio("Selecione um Lote para o cálculo");
            }
            val.lancarException();
        }
    }


    public Boolean isLiberadoCalculo() {
        if (getFolhaDePagamento() == null) {
            return false;
        }
        if (getFolhaDePagamento().getAno() == null) {
            FacesUtil.addMessageError("ano", "Atenção", "Preencha o ano");
            return false;
        }
        if (singletonFolhaDePagamento.isCalculandoFolha()) {
            FacesUtil.addWarn("Atenção", "Atualmente já existe um calculo em execução, iniciado por " + singletonFolhaDePagamento.getUsuarioSistema() + " às " + Util.dateHourToString(singletonFolhaDePagamento.getDataInicioCalculo()));
            return false;
        }
        if (getFolhaDePagamento().getMes() == null) {
            FacesUtil.addMessageError("mes", "Atenção", "Selecione o Mês");
            return false;
        }
        if (getFolhaDePagamento().getTipoFolhaDePagamento() == null) {
            FacesUtil.addMessageError("tipoFolha", "Atenção", "Selecione o Tipo de Folha");
            return false;
        }
        if (tipoCalculo == null) {
            FacesUtil.addError("Atenção", "É necessário informar se é um calculo para todos, individual, orgão ou lote de servidores.");
            return false;
        }

        if (temCompetenciaEfetivada()) {
            FacesUtil.addAtencao("A Folha de Pagamento do tipo '" + getFolhaDePagamento().getTipoFolhaDePagamento().getDescricao() + "' deste mês de competência já está efetivado! Entre em contato com o administrador do sistema para reabrir a competência.");
            return false;
        }
        if (!temCompetenciaAberta()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Operação não realizada.", "Não existe competência aberta para o período!"));
            return false;
        }
        if (temMaisDeUmaCompetenciaAberta() && !TipoFolhaDePagamento.isFolhaRescisao(selecionado.getTipoFolhaDePagamento())) {
            String comp = "";
            for (CompetenciaFP cp : competenciaFPFacade.findCompetenciasAbertasPorTipo(getFolhaDePagamento().getTipoFolhaDePagamento())) {
                comp += "(" + cp + ")";
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Operação não realizada.", "Existe mais de uma competência com status Aberta. [" + comp + "]"));
            return false;
        }

        return true;
    }

    public boolean teste() {
        if (getFolhaDePagamento().getAno() == null) {
            return false;
        }
        if (getFolhaDePagamento().getMes() == null) {
            return false;
        }
        if (getFolhaDePagamento().getTipoFolhaDePagamento() == null) {
            return false;
        }
        return true;
    }


    public void gerarCalculoIndividualNovo() {
        if (isFolhaEfetivada(selecionado)) {
            if (selecionado.getId() == null) {
                selecionado = folhaDePagamentoFacade.salvarNovaSimulacao(selecionado);
            }
        }

        servidor.setFolha(selecionado);
        servidor.getMatriculaFP().getPessoa().setFichaJaExcluidas(false);
        List<VinculoFP> vinculos = new ArrayList<>();
        vinculos.add(servidor);

        long tempoInicial = System.currentTimeMillis();
        detalheProcessamentoFolha.getDetalhesCalculoRH().setTotalServidores(vinculos.size());
        detalheProcessamentoFolha.setTipoCalculo(TipoCalculo.INDIVIDUAL);
        logger.info("----------------------CRIANDO FILA DE PROCESSAMENTO-----------------------");
        Map<VinculoFP, FilaProcessamentoFolha> filas = filaProcessamentoFolhaFacade.salvarFilaProcessamento(new HashSet<>(vinculos), sistemaFacade.getUsuarioCorrente(), null);

        FacesUtil.redirecionamentoInterno("/folhadepagamento-simulacao/acompanhamento/");
        folhaDePagamentoFacade.iniciarProcessamento(null, selecionado, vinculos, detalheProcessamentoFolha, sistemaControlador.getSistemaFacade().getDataOperacao(), sistemaControlador.getSistemaFacade().getUsuarioCorrente(), filas, assistenteBarraProgresso);

        for (ItemErroScript item : FolhaDePagamentoNovoCalculador.getItemErroScripts()) {
            FacesUtil.addMessageWarn("Atenção", item.getMensagem());
        }
    }

    private boolean isFolhaEfetivada(FolhaDePagamentoSimulacao folha) {
        if (folha == null) {
            return true;
        }
        if (folha != null && folha.getCompetenciaFP().getStatusCompetencia().equals(StatusCompetencia.EFETIVADA)) {
            return true;
        }
        if (folha != null && folha.getEfetivadaEm() != null) {
            return true;
        }
        return false;
    }

    public void calcularFolha() {
        try {
            if (isLiberadoCalculo()) {
                criarAssistente();
                validarCalculoPorTipo();
                isPossuiDadosDoCalendario();
                tempo = System.currentTimeMillis();
                detalheProcessamentoFolha = new DetalheProcessamentoFolha();
                detalheProcessamentoFolha.setQuantidadeMesesRetroativos(getFolhaDePagamento().getQtdeMesesRetroacao());
                detalheProcessamentoFolha.setEventosBloqueados(eventosBloqueados != null ? new ArrayList(eventosBloqueados) : new ArrayList());
                detalheProcessamentoFolha.setEventosBloqueadosPorTela(eventosBloqueados != null ? new ArrayList(eventosBloqueados) : new ArrayList());

                detalheProcessamentoFolha.getDetalhesCalculoRH().setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
                detalheProcessamentoFolha.setItemCalendarioFP(calendarioFPFacade.buscarDataCalculoCalendarioFPPorFolha(getFolhaDePagamento()));
                if (hierarquiaOrganizacional == null) {
                    fixaUnidadePrincipal();
                }
                selecionado.setImprimeLogEmArquivo(imprimeLogEmArquivo);
                defineDadosSingleton();
                if (servidor == null) {
                    gerarCalculoGeralNovo();
                } else {
                    gerarCalculoIndividualNovo();
                }
            }
        } catch (ValidacaoException va) {
            liberaSingleton();
            assistenteBarraProgresso.setExecutando(false);
            logger.error("Validação Exception: ", va);
            FacesUtil.printAllFacesMessages(va.getMensagens());
        } catch (Exception e) {
            liberaSingleton();
            assistenteBarraProgresso.setExecutando(false);
            logger.error("Erro tentar calcular a folha: ", e);
            FacesUtil.addError("Atenção", e + "");
        }
    }

    private void defineDadosSingleton() {
        if(TipoCalculo.TODOS.equals(tipoCalculo)) {
            singletonFolhaDePagamento.setCalculandoFolha(true);
        }
        singletonFolhaDePagamento.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        singletonFolhaDePagamento.setDataInicioCalculo(new Date());
    }

    public void liberaSingleton() {
        singletonFolhaDePagamento.setCalculandoFolha(false);
    }

    private void isPossuiDadosDoCalendario() {
        ValidacaoException validacaoException = new ValidacaoException();
        try {
            ItemCalendarioFP item = calendarioFPFacade.buscarDataCalculoCalendarioFPPorFolha(getFolhaDePagamento());
            if (item == null) {
                validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado dados do Calendário da folha, por favor preencha esses dados para prosseguir com o cálculo");
            }
            if (item != null && item.getUltimoDiaProcessamento() == null) {
                validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Por favor,  preencha os dados corretamente no Calendário Folha para prosseguir com o cálculo");
            }
            if (validacaoException.temMensagens()) {
                throw validacaoException;
            }
        } catch (ValidacaoException e) {
            throw e;
        } catch (ExcecaoNegocioGenerica e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public void gerarCalculoGeralNovo() {

        FolhaDePagamentoSimulacao folha = folhaDePagamentoFacade.salvarNovaSimulacao(selecionado);
        if (folha.getEfetivadaEm() != null) {
            liberaSingleton();
            FacesUtil.addWarn("Atenção!", "Folha já efetivada.");
            return;
        }
        List<VinculoFP> vinculos = new ArrayList<>();
        if (!folha.getTipoFolhaDePagamento().equals(TipoFolhaDePagamento.RESCISAO)) {

            if (tipoCalculo.equals(TipoCalculo.TODOS)) {
                vinculos = folhaDePagamentoFacade.recuperarMatriculasSemCedenciaEstagioOuAfastamento(folha.getMes(), folha.getAno());
            } else if (tipoCalculo.equals(TipoCalculo.LOTE)) {
                vinculos = folhaDePagamentoFacade.findVinculosSemCedenciaAfastamentoByLote(loteProcessamento, folha.getMes(), folha.getAno());
                folha.setLoteProcessamento(loteProcessamento);
            } else if (tipoCalculo.equals(TipoCalculo.ORGAO)) {
                vinculos = contratoFPFacade.listarVinculosPorHierarquiaSemCedenciaEstagioOuAfastamento(Lists.newArrayList(hierarquiaOrganizacional), folha.getMes(), folha.getAno(), TipoFolhaDePagamento.isFolhaRescisao(folha));
            } else if (tipoCalculo.equals(TipoCalculo.INDIVIDUAL)) {
                servidor.getMatriculaFP().getPessoa().setFichaJaExcluidas(false);
                vinculos.add(servidor);
            }
        }

        detalheProcessamentoFolha.getDetalhesCalculoRH().setTotalServidores(vinculos.size());
        detalheProcessamentoFolha.setTipoCalculo(tipoCalculo);
        logger.info("----------------------CRIANDO FILA DE PROCESSAMENTO-----------------------");
        Map<VinculoFP, FilaProcessamentoFolha> filas = filaProcessamentoFolhaFacade.salvarFilaProcessamento(new HashSet<>(vinculos), sistemaFacade.getUsuarioCorrente(), null);

        logger.error("----------------------INICIANDO O CÁLCULO GERAL-----------------------");
        folhaDePagamentoFacade.iniciarProcessamento(null, folha, vinculos, detalheProcessamentoFolha, sistemaControlador.getSistemaFacade().getDataOperacao(), sistemaControlador.getSistemaFacade().getUsuarioCorrente(), filas, assistenteBarraProgresso);
        FacesUtil.redirecionamentoInterno("/folhadepagamento-simulacao/acompanhamento/");
        logger.error("-----------------TEMPO DE PROCESSAMENTO DA FOLHA: {0}-----------------", (System.currentTimeMillis() - tempo));
        if (!detalheProcessamentoFolha.getErros().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Cálculo gerado com sucesso!"));
        }
    }

    public boolean temCompetenciaAberta() {
        CompetenciaFP competenciaFP = folhaDePagamentoFacade.verificaCompetenciaAbertaSimulacao(getFolhaDePagamento());
        if (competenciaFP != null) {
            selecionado.setCompetenciaFP(competenciaFP);
            return true;
        }

        return false;
    }

    public boolean temMaisDeUmaCompetenciaAberta() {
        List<CompetenciaFP> competenciaFP = competenciaFPFacade.findCompetenciasAbertasPorTipo(getFolhaDePagamento().getTipoFolhaDePagamento());
        if (competenciaFP != null && competenciaFP.size() > 1) {
            return true;
        }

        return false;
    }

    public boolean temCompetenciaEfetivada() {
        CompetenciaFP competenciaFP = folhaDePagamentoFacade.verificaCompetenciaEfetivada(getFolhaDePagamento());
        if (competenciaFP != null) {
            selecionado.setCompetenciaFP(competenciaFP);
            return true;
        }
        return false;
    }

    public FolhaDePagamentoSimulacao esteSelecionado() {
        return selecionado;
    }

    public void efetivarFolha() {
        FolhaDePagamento f = (FolhaDePagamento) folhaDePagamentoFacade.recuperarAlternativo(getFolhaDePagamento().getId());
        if (f == null) {
            FacesUtil.addError("Atenção", "Folha de pagamento não encontrada!");
        } else if (f.folhaEfetivada()) {
            FacesUtil.addError("Atenção", "A Folha de Pagamento do tipo '" + f.getTipoFolhaDePagamento().getDescricao() + "' deste mês de competência já está efetivado! Entre em contato com o administrador do sistema para reabrir a competência.");
        } else {
            folhaDePagamentoFacade.efetivarFolhaDePagamento(f, sistemaControlador.getDataOperacao());
            FacesUtil.addOperacaoRealizada("Efetivada com sucesso");
            FacesUtil.redirecionamentoInterno("/folhadepagamento-simulacao/listar/");
        }

    }

    public void abrirFolhaEfetivada() {
        if (getFolhaDePagamento() == null) {
            FacesUtil.addError("Atenção", "Folha de pagamento indisponível.");
            return;
        }
        if (temCompetenciaAberta()) {
            getFolhaDePagamento().setEfetivadaEm(null);
            folhaDePagamentoFacade.salvar(getFolhaDePagamento());
            FacesUtil.addOperacaoRealizada("Folha foi aberta com sucesso.");
            FacesUtil.redirecionamentoInterno("/folhadepagamento-simulacao/listar/");
        } else {
            FacesUtil.addError("Atenção", "A Folha de Pagamento do tipo '" + getFolhaDePagamento().getTipoFolhaDePagamento().getDescricao() + "' já está com a competência efetivada. Reabra a competência para prosseguir.");
        }
    }

    public void excluirSelecionado() {

        if (selecionado.getEfetivadaEm() != null) {
            FacesUtil.addError("Atenção!", "A Folha já está efetivada e não pode ser excluída.");
        } else if (selecionado.getCompetenciaFP() != null && !StatusCompetencia.ABERTA.equals(selecionado.getCompetenciaFP().getStatusCompetencia())) {
            FacesUtil.addError("Atenção!", "A Folha não pode ser excluída, a competência não está aberta.");
        } else {
            try {
                FacesUtil.addInfo(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Registro excluído com sucesso.");
            } catch (Exception e) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            }
        }
    }

    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        super.editar();
    }

    public List<SelectItem> getTiposEmpenhos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoEmpenhoFP object : TipoEmpenhoFP.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterPessoaFisica;
    }

    public List<SelectItem> getTipoFolhaDePagamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento object : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));

        }
        return toReturn;
    }

    public List<PessoaFisica> completaPessoa(String parte) {
        return pessoaFisicaFacade.listaFiltrando(parte.trim(), "nome");
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<ItemErroScript> getItemErroScripts() {
        return itemErroScripts;
    }

    public void setItemErroScripts(List<ItemErroScript> itemErroScripts) {
        this.itemErroScripts = itemErroScripts;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<ContratoFP> getContratosSemEnquadramento() {
        return contratosSemEnquadramento;
    }

    public void setContratosSemEnquadramento(List<ContratoFP> contratosSemEnquadramento) {
        this.contratosSemEnquadramento = contratosSemEnquadramento;
    }

    public DetalheProcessamentoFolha getDetalheProcessamentoFolha() {
        return detalheProcessamentoFolha;
    }

    public void setDetalheProcessamentoFolha(DetalheProcessamentoFolha detalheProcessamentoFolha) {
        this.detalheProcessamentoFolha = detalheProcessamentoFolha;
    }

    public String getTempoDecorrido() {
        long HOUR = TimeUnit.HOURS.toMillis(1);
        long decorrido = (System.currentTimeMillis() - tempo);
        if (decorrido < HOUR) {
            return String.format("%1$TM:%1$TS%n", decorrido);
        } else {
            return String.format("%d:%2$TM:%2$TS%n", decorrido / HOUR, decorrido % HOUR);
        }
    }

    public String getTempoEstimado() {
        long HOUR = TimeUnit.HOURS.toMillis(1);
        long unitario = (System.currentTimeMillis() - tempo) / (detalheProcessamentoFolha.getContadorCalculoFolha() + 1);
        Double qntoFalta = (unitario * (detalheProcessamentoFolha.getTotalCadastros() - detalheProcessamentoFolha.getContadorCalculoFolha()));
        if (qntoFalta < HOUR) {
            return String.format("%1$TM:%1$TS%n", qntoFalta.longValue());
        } else {
            return String.format("%d:%2$TM:%2$TS%n", qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
        }
    }

    public Integer getPosicaoCadastroAtual() {
        return detalheProcessamentoFolha.getContadorCalculoFolha();
    }

    public Double getTotalCadastros() {
        return detalheProcessamentoFolha.getTotalCadastros();
    }

    public Double getPorcentagemDoCalculo() {
        return detalheProcessamentoFolha.getPorcentagemDoCalculo().doubleValue();
    }

    public void abortaCalculo() {
        detalheProcessamentoFolha.getErros().put(null, "Calculo Abortado!");
        detalheProcessamentoFolha.cancelTasks();
        detalheProcessamentoFolha.finalizaCalculo();
    }

    public boolean isLiberaCalculo() {
        return detalheProcessamentoFolha.isLiberaCalculo();
    }

    public StringBuilder getLogSucesso() {
        if (detalheProcessamentoFolha != null) {
            return detalheProcessamentoFolha.getLogSucesso();
        }
        return new StringBuilder("");
    }

    public void geraTxt() throws FileNotFoundException, IOException {

        String conteudo = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
            + " </title>"
            + " </head>"
            + " <body>"
            + detalheProcessamentoFolha.getLogSucesso().toString()
            + " </body>"
            + " </html>";
        Util.downloadPDF("Log do Calculo " + getFolhaDePagamento(), conteudo, FacesContext.getCurrentInstance());
    }

    public boolean isCalculaTodos() {
        return calculaTodos;
    }

    public void setCalculaTodos(boolean calculaTodos) {
        this.calculaTodos = calculaTodos;
    }

    public TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public LoteProcessamento getLoteProcessamento() {
        return loteProcessamento;
    }

    public void setLoteProcessamento(LoteProcessamento loteProcessamento) {
        this.loteProcessamento = loteProcessamento;
    }

    public boolean isImprimeLogEmArquivo() {
        return imprimeLogEmArquivo;
    }

    public void setImprimeLogEmArquivo(boolean imprimeLogEmArquivo) {
        this.imprimeLogEmArquivo = imprimeLogEmArquivo;
    }

    public void liberarUnidade() {
        hierarquiaOrganizacional = null;
    }

    public void liberaServidor(SelectEvent evento) {
        servidor = null;
    }

    public void liberaLote() {
        hierarquiaOrganizacional = null;
        servidor = null;
        if (selecionado != null) {
            if (loteProcessamento != null && loteProcessamento.getQtdeMesesRetroacao() != null) {
                selecionado.setQtdeMesesRetroacao(loteProcessamento.getQtdeMesesRetroacao());
            } else {
                selecionado.setQtdeMesesRetroacao(1);
            }
        }
    }


    public List<SelectItem> getLoteProcessamentos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado != null) {
            if (selecionado.getMes() != null && selecionado.getAno() != null && selecionado.getTipoFolhaDePagamento() != null) {
                for (LoteProcessamento object : loteProcessamentoFacade.getLotesPorMesAnoAndTipoFolhaDePagamento(selecionado.getMes(), selecionado.getAno(), selecionado.getTipoFolhaDePagamento())) {
                    toReturn.add(new SelectItem(object, object.getDescricao()));

                }
            }
        }
        return toReturn;
    }

    public Converter getConverterLoteProcessamento() {
        if (converterLoteProcessamento == null) {
            converterLoteProcessamento = new ConverterGenerico(LoteProcessamento.class, loteProcessamentoFacade);
        }
        return converterLoteProcessamento;
    }

    public void setOperacoes(Operacoes op) {
        operacao = op;
    }

    public String getLegenda() {
        if (detalheProcessamentoFolha != null && detalheProcessamentoFolha.getDetalhesCalculoRH() != null) {
            return detalheProcessamentoFolha.getDetalhesCalculoRH().getDescricao();
        }
        return "Processando...";

    }

    public void definirPropriedades() {
        if (selecionado != null) {
            if (selecionado.getCalculadaEm() == null) {
                selecionado.setCalculadaEm(sistemaControlador.getDataOperacao());
            }
        }
    }

    public List<SelectItem> getFolhasDePagamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));

        if (competenciaFP == null) {
            return toReturn;
        }

        List<FolhaDePagamentoSimulacao> lista = folhaDePagamentoSimulacaoFacade.buscarFolhaPorCompetenciaEStatus(competenciaFP);
        if (lista.isEmpty()) {
            return toReturn;

        }

        for (FolhaDePagamentoSimulacao obj : lista) {
            if (!obj.folhaEfetivada()) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }

        return toReturn;
    }

    public ConverterGenerico getConverterCompetenciaFP() {
        if (converterCompetenciaFP == null) {
            converterCompetenciaFP = new ConverterGenerico(CompetenciaFP.class, competenciaFPFacade);
        }
        return converterCompetenciaFP;
    }


    public List<SelectItem> getCompetenciasFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        List<CompetenciaFP> lista = competenciaFPFacade.buscarCompetenciasPorStatus(StatusCompetencia.ABERTA);
        toReturn.add(new SelectItem(null, ""));
        if (lista.isEmpty()) {
            return toReturn;
        }

        for (CompetenciaFP obj : lista) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }

        return toReturn;
    }

}
