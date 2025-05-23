/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.FilaProcessamentoFolha;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidadesauxiliares.rh.FiltroFolhaDePagamentoDTO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.enums.rh.TipoCalculo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.administracaodepagamento.FilaProcessamentoFolhaFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.script.ItemErroScript;
import br.com.webpublico.singletons.SingletonFolhaDePagamento;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.beanutils.BeanUtils;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static br.com.webpublico.enums.TipoFolhaDePagamento.*;
import static br.com.webpublico.util.FacesUtil.addWarn;

/*
 * Codigo gerado automaticamente em Wed Jun 29 14:21:41 BRT 2011
 */
@ManagedBean(name = "calculoFolhaDePagamentoControlador")
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "listaFP", pattern = "/folhadepagamento/listar/", viewId = "/faces/rh/administracaodepagamento/folhadepagamento/lista.xhtml"),
    @URLMapping(id = "novoFP", pattern = "/folhadepagamento/novo/", viewId = "/faces/rh/administracaodepagamento/folhadepagamento/edita.xhtml"),
    @URLMapping(id = "gerandoFolha", pattern = "/folhadepagamento/acompanhamento/", viewId = "/faces/rh/administracaodepagamento/folhadepagamento/log.xhtml"),
    @URLMapping(id = "detalhesFolha", pattern = "/folhadepagamento/detalhes-folha-pagamento/#{calculoFolhaDePagamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/folhadepagamento/detalhes.xhtml"),
    @URLMapping(id = "visualizarFP", pattern = "/folhadepagamento/ver/#{calculoFolhaDePagamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/folhadepagamento/visualizar.xhtml")
})
public class CalculoFolhaDePagamentoControlador extends PrettyControlador<FolhaDePagamento> implements Serializable, CRUD {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private CalendarioFPFacade calendarioFPFacade;
    @EJB
    private LoteProcessamentoFacade loteProcessamentoFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private SingletonFolhaDePagamento singletonFolhaDePagamento;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private EntidadeDPContasFacade entidadeDPContasFacade;
    @EJB
    private ItemEntidadeDPContasFacade itemEntidadeDPContasFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FilaProcessamentoFolhaFacade filaProcessamentoFolhaFacade;
    @EJB
    PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    EventoFPFacade eventoFPFacade;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private PessoaFisica pessoaFisica;
    private ConverterAutoComplete converterPessoaFisica;
    private ConverterAutoComplete converterItemEntidadeDPContas;
    private List<ItemErroScript> itemErroScripts = new ArrayList<>();
    private ConverterAutoComplete converterContratoFP;
    private List<ContratoFP> contratosSemEnquadramento;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private DetalheProcessamentoFolha detalheProcessamentoFolha;
    private Long tempo;
    private boolean calculaTodos;
    private ConverterGenerico converterLoteProcessamento;
    private CompetenciaFP competenciaFP;
    private FolhaDePagamento folhaDePagamento;
    private ConverterGenerico converterCompetenciaFP;
    private boolean imprimeLogEmArquivo;
    private boolean gravarMemoriaCalculo;
    private boolean processarCalculoTransient;
    private ConfiguracaoRH configuracaoRH;
    private ItemCalendarioFP itemCalendarioFP;
    private List<String> retorno;
    private boolean podeConfigurarRetroacaoParaDecimo = false;
    private List<HierarquiaOrganizacional> hieraquiasOrganizacionaisSelecionadas;
    private FiltroFolhaDePagamentoDTO filtro;
    private List<FiltroFolhaDePagamentoDTO> filtros;
    private FiltroFolhaDePagamento filtroFolhaDePagamentoAtual;
    public CalculoFolhaDePagamentoControlador() {
        metadata = new EntidadeMetaData(FolhaDePagamento.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/folhadepagamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "gerandoFolha", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void acompanhamento() {
        FacesUtil.atualizarComponente("remoteForm");
        FacesUtil.executaJavaScript("iniciaPagamento();");
    }

    @URLAction(mappingId = "novoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCalculadaEm(SistemaFacade.getDataCorrente());
        gravarMemoriaCalculo = true;
        processarCalculoTransient = true;
        pessoaFisica = new PessoaFisica();
        contratosSemEnquadramento = new ArrayList<>();
        tempo = 0l;
        calculaTodos = true;
        competenciaFP = null;
        folhaDePagamento = null;
        filtro = new FiltroFolhaDePagamentoDTO();
        itemCalendarioFP = null;
        inicializaDependenciasDoCalculo();
        hieraquiasOrganizacionaisSelecionadas = new ArrayList<>();
    }

    @URLAction(mappingId = "visualizarFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        podeConfigurarRetroacaoParaDecimo = vinculoFPFacade.hasAutorizacaoEspecialRH(sistemaFacade.getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_CONFIGURACAO_RETROACAO_DECIMO_TERCEIRO);
        configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente();
        buscarDadosFiltros();
    }

    public void verDetalhesFolha(BigDecimal id) {
        selecionado = folhaDePagamentoFacade.recuperarAlternativo(id.longValue());
        Web.navegacao(getCaminhoPadrao() + "listar/", getCaminhoPadrao() + "detalhes-folha-pagamento/" + selecionado.getId() + "/", selecionado);
    }

    public void recuperarFolhaPorId(BigDecimal id) {
        selecionado = folhaDePagamentoFacade.recuperarSimples(id.longValue());
        FacesUtil.executaJavaScript("window.open(' " + FacesUtil.getRequestContextPath() +
            "/relatorio/folha-por-secretaria-recurso-vinculoFP/?ano=" + selecionado.getAno() +
            "&mes=" + selecionado.getMes().getNumeroMes() +
            "&tipo=" + selecionado.getTipoFolhaDePagamento().name() +
            "&versao=" + selecionado.getVersao() +
            "&orgao=" + selecionado.getUnidadeOrganizacional().getId() + "');");
    }

    private void criarAssistente() {
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.setSelecionado(selecionado);
        assistenteBarraProgresso.setExecutando(true);
        assistenteBarraProgresso.setDescricaoProcesso("Cálculo da Folha de Pagamento");
        assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
    }

    @Override
    public AbstractFacade getFacede() {
        return folhaDePagamentoFacade;
    }

    public FolhaDePagamentoFacade getFacade() {
        return folhaDePagamentoFacade;
    }

    public CompetenciaFP getCompetenciaFP() {
        return competenciaFP;
    }

    public void setCompetenciaFP(CompetenciaFP competenciaFP) {
        this.competenciaFP = competenciaFP;
    }

    public FiltroFolhaDePagamentoDTO getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroFolhaDePagamentoDTO filtro) {
        this.filtro = filtro;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterContratoFP;
    }

    private void inicializaDependenciasDoCalculo() {
        podeConfigurarRetroacaoParaDecimo = vinculoFPFacade.hasAutorizacaoEspecialRH(sistemaFacade.getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_CONFIGURACAO_RETROACAO_DECIMO_TERCEIRO);
        if (detalheProcessamentoFolha == null || detalheProcessamentoFolha.isLiberaCalculo()) {
            detalheProcessamentoFolha = new DetalheProcessamentoFolha();
        }
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

    public List<ItemEntidadeDPContas> completarItemEntidadeDPContas(String parte) {
        return entidadeDPContasFacade.buscarEntidadesConfiguracaoEntidadeDPContasVigentePorCategoria(CategoriaDeclaracaoPrestacaoContas.FOLHA_PAGAMENTO, sistemaControlador.getDataOperacao(), parte.trim());
    }

    @Override
    public void salvar() {
        if (filtro.getHierarquiaOrganizacional() != null) {
            selecionado.setUnidadeOrganizacional(filtro.getHierarquiaOrganizacional().getSubordinada());
        }
        super.salvar();
    }

    public void fixaUnidadePrincipal() {
        filtro.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getRaizHierarquia(UtilRH.getDataOperacao()));
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public List<VinculoFP> completarContratoFP(String parte) {
        if (folhaDePagamento != null && folhaDePagamento.getTipoFolhaDePagamento() != null) {
            if (!TipoFolhaDePagamento.isFolhaRescisao(getFolhaDePagamento())) {
                return contratoFPFacade.recuperaContratoVigenteNaoCedidoMatricula(parte.trim(), folhaDePagamento.getMes().getNumeroMes(), folhaDePagamento.getAno());
            }

            if (TipoFolhaDePagamento.isFolhaRescisao(folhaDePagamento)) {
                return contratoFPFacade.buscarContratosExoneradosSemFolhaEfetivadaFiltrando(parte.trim(), folhaDePagamento);
            }
            return new ArrayList<>();
        } else {
            return new ArrayList<>();
        }
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
                    selecionado = f;
                    getFolhaDePagamento().setQtdeMesesRetroacao(lote.getQtdeMesesRetroacao());
                    detalheProcessamentoFolha.setQuantidadeMesesRetroativos(lote.getQtdeMesesRetroacao());
                    filtro.setLoteProcessamento(lote);
                    filtro.setTipoCalculo(TipoCalculo.LOTE);
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
        if (filtro.getTipoCalculo() != null) {
            if (TipoCalculo.INDIVIDUAL.equals(filtro.getTipoCalculo()) && filtro.getVinculoFP() == null) {
                val.adicionarMensagemDeCampoObrigatorio("Selecione uma Pessoa para o cálculo.");
            }
            if (TipoCalculo.ORGAO.equals(filtro.getTipoCalculo()) && filtro.getHierarquiaOrganizacional() == null) {
                val.adicionarMensagemDeCampoObrigatorio("Selecione um Orgão para o cálculo.");
            }
            if (calendarioFPFacade.buscarDataCalculoCalendarioFPPorFolha(getFolhaDePagamento()) == null) {
                val.adicionarMensagemDeCampoObrigatorio("Data de Calendário não encontrada");
            }
            if (TipoCalculo.LOTE.equals(filtro.getTipoCalculo()) && filtro.getLoteProcessamento() == null) {
                val.adicionarMensagemDeCampoObrigatorio("Selecione um Lote para o cálculo.");
            }
            if (TipoCalculo.ENTIDADE.equals(filtro.getTipoCalculo()) && filtro.getItemEntidadeDPContas() == null) {
                val.adicionarMensagemDeCampoObrigatorio("Selecione a Entidade para o cálculo.");
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
            addWarn("Atenção", "Atualmente já existe um calculo em execução, iniciado por " + singletonFolhaDePagamento.getUsuarioSistema() + " às " + Util.dateHourToString(singletonFolhaDePagamento.getDataInicioCalculo()));
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
        if (filtro.getTipoCalculo() == null) {
            FacesUtil.addError("Atenção", "É necessário informar se é um calculo para todos, individual, orgão ou lote de servidores.");
            return false;
        }

        if (temCompetenciaEfetivada()) {
            addWarn("Atenção", "A Folha de Pagamento do tipo '" + getFolhaDePagamento().getTipoFolhaDePagamento().getDescricao() + "' deste mês de competência já está efetivado! Entre em contato com o administrador do sistema para reabrir a competência.");
            return false;
        }
        if (!temCompetenciaAberta(folhaDePagamento)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Operação não realizada.", "Não existe competência aberta para o período!"));
            return false;
        }
        if (temMaisDeUmaCompetenciaAberta() && !TipoFolhaDePagamento.isFolhaRescisao(folhaDePagamento.getTipoFolhaDePagamento())) {
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
        FolhaDePagamento f = folhaDePagamento;


        if (isFolhaEfetivada(f)) {
            if (folhaDePagamento.getId() == null) {
                folhaDePagamentoFacade.salvarNovo(selecionado);
            }
            f = selecionado;
        }
        filtro.getVinculoFP().setFolha(f);
        filtro.getVinculoFP().getMatriculaFP().getPessoa().setFichaJaExcluidas(false);
        List<VinculoFP> vinculos = new ArrayList<>();
        vinculos.add(filtro.getVinculoFP());

        long tempoInicial = System.currentTimeMillis();
        detalheProcessamentoFolha.getDetalhesCalculoRH().setTotalServidores(vinculos.size());
        detalheProcessamentoFolha.setTipoCalculo(TipoCalculo.INDIVIDUAL);
        detalheProcessamentoFolha.bloqueiaCalculo();
        detalheProcessamentoFolha.setTotalCadastros(0d);

        logger.info("----------------------CRIANDO FILA DE PROCESSAMENTO-----------------------");
        Map<VinculoFP, FilaProcessamentoFolha> filas = filaProcessamentoFolhaFacade.salvarFilaProcessamento(new HashSet<>(vinculos), sistemaFacade.getUsuarioCorrente(), f);


        FacesUtil.redirecionamentoInterno("/folhadepagamento/acompanhamento/");
        folhaDePagamentoFacade.iniciarProcessamento(filtroFolhaDePagamentoAtual, f, vinculos, detalheProcessamentoFolha, sistemaFacade.getDataOperacao(), sistemaFacade.getUsuarioCorrente(), filas, assistenteBarraProgresso);

        for (ItemErroScript item : FolhaDePagamentoNovoCalculador.getItemErroScripts()) {
            FacesUtil.addMessageWarn("Atenção", item.getMensagem());
        }
        assistenteBarraProgresso.setExecutando(false);
    }

    private boolean isFolhaEfetivada(FolhaDePagamento folha) {
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
                detalheProcessamentoFolha.setEventosBloqueados(filtro.getEventosFPsBloqueados() != null ? Lists.newArrayList(filtro.getEventosFPsBloqueados()) : Lists.newArrayList());
                detalheProcessamentoFolha.setEventosBloqueadosPorTela(filtro.getEventosFPsBloqueados() != null ? Lists.newArrayList(filtro.getEventosFPsBloqueados()) : Lists.newArrayList());

                detalheProcessamentoFolha.getDetalhesCalculoRH().setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
                detalheProcessamentoFolha.getDetalhesCalculoRH().setMesesRetroacao(folhaDePagamento.getQtdeMesesRetroacao());
                detalheProcessamentoFolha.setItemCalendarioFP(calendarioFPFacade.buscarDataCalculoCalendarioFPPorFolha(getFolhaDePagamento()));
                detalheProcessamentoFolha.setEventoFPAdiantamento13Salario(configuracaoRHFacade.buscarConfiguracaoFPVigente(sistemaFacade.getDataOperacao()).getAdiantamento13Salario());
                if (filtro.getHierarquiaOrganizacional() == null) {
                    fixaUnidadePrincipal();
                }
                folhaDePagamento.setImprimeLogEmArquivo(imprimeLogEmArquivo);
                folhaDePagamento.setGravarMemoriaCalculo(gravarMemoriaCalculo);
                folhaDePagamento.setProcessarCalculoTransient(processarCalculoTransient);
                if (hieraquiasOrganizacionaisSelecionadas != null && !hieraquiasOrganizacionaisSelecionadas.isEmpty()) {
                    filtro.setHierarquiasOrganizacionais(hierarquiaOrganizacionalFacade.agruparHierarquiasFilhas(hieraquiasOrganizacionaisSelecionadas));
                }
                filtroFolhaDePagamentoAtual = new FiltroFolhaDePagamento(folhaDePagamento, SistemaFacade.getDataCorrente(), filtro.toJSON());

                defineDadosSingleton();
                if (filtro.getVinculoFP() == null) {
                    gerarCalculoGeralNovo();
                } else {
                    gerarCalculoIndividualNovo();
                }
            }
        } catch (ValidacaoException va) {
            liberaSingleton();
            logger.error("Validação Exception: ", va);
            FacesUtil.printAllFacesMessages(va.getMensagens());
        } catch (Exception e) {
            liberaSingleton();
            logger.error("Erro tentar calcular a folha: ", e);
            FacesUtil.addError("Atenção", e + "");
        } finally {
            assistenteBarraProgresso.setExecutando(false);
            resetarPropsArvoreHierarquiaOrganizacional();
        }
    }

    private void defineDadosSingleton() {
        if (TipoCalculo.TODOS.equals(filtro.getTipoCalculo())) {
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
        FolhaDePagamento folha = folhaDePagamento;
        if (folha == null) {
            folhaDePagamentoFacade.salvarNovo(selecionado);
            folha = selecionado;
        } else if (folha.getEfetivadaEm() != null) {
            liberaSingleton();
            addWarn("Atenção!", "Folha já efetivada.");
            return;
        }
        List<VinculoFP> vinculos = new ArrayList<>();

        if (TipoCalculo.TODOS.equals(filtro.getTipoCalculo())) {
            if (TipoFolhaDePagamento.isFolhaRescisao(folha) && hieraquiasOrganizacionaisSelecionadas.isEmpty()) {
                vinculos = folhaDePagamentoFacade.recuperaContratosExonerados(getFolhaDePagamento());
            } else if (!hieraquiasOrganizacionaisSelecionadas.isEmpty()) {
                vinculos = contratoFPFacade.recuperaMatriculaPorOrgaoRecursivaPelaView(hieraquiasOrganizacionaisSelecionadas, folha.getMes(), folha.getAno(), TipoFolhaDePagamento.isFolhaRescisao(folha));
            } else {
                vinculos = folhaDePagamentoFacade.recuperarTodasMatriculas(folha.getMes(), folha.getAno());
            }
        } else if (TipoCalculo.LOTE.equals(filtro.getTipoCalculo())) {
            vinculos = folhaDePagamentoFacade.findVinculosByLote(filtro.getLoteProcessamento());
            folha.setLoteProcessamento(filtro.getLoteProcessamento());
        } else if (TipoCalculo.ORGAO.equals(filtro.getTipoCalculo())) {
            vinculos = contratoFPFacade.recuperaMatriculaPorOrgaoRecursivaPelaView(Lists.newArrayList(filtro.getHierarquiaOrganizacional()), folha.getMes(), folha.getAno(), TipoFolhaDePagamento.isFolhaRescisao(folha));
        } else if (TipoCalculo.INDIVIDUAL.equals(filtro.getTipoCalculo())) {
            filtro.getVinculoFP().getMatriculaFP().getPessoa().setFichaJaExcluidas(false);
            vinculos.add(filtro.getVinculoFP());
        } else if (TipoCalculo.ENTIDADE.equals(filtro.getTipoCalculo())) {
            vinculos = contratoFPFacade.buscarVinculoFpPorItemEntidadeDPContas(filtro.getItemEntidadeDPContas(), TipoFolhaDePagamento.isFolhaRescisao(folha));
        }

        detalheProcessamentoFolha.getDetalhesCalculoRH().setTotalServidores(vinculos.size());
        detalheProcessamentoFolha.setTipoCalculo(filtro.getTipoCalculo());
        detalheProcessamentoFolha.bloqueiaCalculo();
        detalheProcessamentoFolha.setTotalCadastros(0d);
        logger.info("----------------------CRIANDO FILA DE PROCESSAMENTO-----------------------");
        Map<VinculoFP, FilaProcessamentoFolha> filas = filaProcessamentoFolhaFacade.salvarFilaProcessamento(new HashSet<>(vinculos), sistemaFacade.getUsuarioCorrente(), folha);

        logger.error("----------------------INICIANDO O CÁLCULO GERAL-----------------------");
        FacesUtil.redirecionamentoInterno("/folhadepagamento/acompanhamento/");
        folhaDePagamentoFacade.iniciarProcessamento(filtroFolhaDePagamentoAtual, folha, vinculos, detalheProcessamentoFolha, sistemaFacade.getDataOperacao(), sistemaFacade.getUsuarioCorrente(), filas, assistenteBarraProgresso);
        logger.error("-----------------TEMPO DE PROCESSAMENTO DA FOLHA: {0}-----------------", (System.currentTimeMillis() - tempo));
        if (!detalheProcessamentoFolha.getErros().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso", "Cálculo gerado com sucesso!"));
        }
        assistenteBarraProgresso.setExecutando(false);
    }


    public boolean temCompetenciaAberta(FolhaDePagamento folha) {
        CompetenciaFP competenciaFP = folhaDePagamentoFacade.verificaCompetenciaAberta(folha);
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


    public void efetivarFolha() {
        FolhaDePagamento folha = folhaDePagamentoFacade.recuperarAlternativo(selecionado.getId());
        if (folha == null) {
            FacesUtil.addError("Atenção", "Folha de pagamento não encontrada!");
        } else if (folha.folhaEfetivada()) {
            FacesUtil.addError("Atenção", "A Folha de Pagamento do tipo '" + folha.getTipoFolhaDePagamento().getDescricao() + "' deste mês de competência já está efetivado! Entre em contato com o administrador do sistema para reabrir a competência.");
        } else {
            folhaDePagamentoFacade.efetivarFolhaDePagamento(folha, new Date());
            processarServidoresPortalTransparencia(folha);
            FacesUtil.addOperacaoRealizada("Folha Efetivada com sucesso!");
        }
    }

    public void processarServidoresPortalTransparencia(FolhaDePagamento folhaDePagamento) {
        AssistenteBarraProgresso assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        assistenteBarraProgresso.setDataAtual(sistemaFacade.getDataOperacao());
        assistenteBarraProgresso.setDescricaoProcesso("Recuperando servidores para o portal transparência");

        CompletableFuture<AssistenteBarraProgresso> future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso, () ->
            folhaDePagamentoFacade.recuperarInformacoesPortalTransparencia(assistenteBarraProgresso, folhaDePagamento));

        future.thenAccept(assistente -> {
            assistente.setDescricaoProcesso("Salvando servidores para o portal transparência");
            AsyncExecutor.getInstance().execute(assistente,
                ()-> folhaDePagamentoFacade.salvarServidores(assistente));
        });
    }

    public void abrirFolhaEfetivada() {
        if (selecionado == null) {
            FacesUtil.addError("Atenção", "Folha de pagamento indisponível.");
            return;
        }
        if (temCompetenciaAberta((selecionado))) {
            selecionado = folhaDePagamentoFacade.recuperarAlternativo(selecionado.getId());
            selecionado.setEfetivadaEm(null);
            selecionado.setDataPortal(null);
            selecionado.setExibirPortal(Boolean.FALSE);
            folhaDePagamentoFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("Folha foi aberta com sucesso.");
            FacesUtil.redirecionamentoInterno("/folhadepagamento/listar/");
        } else {
            FacesUtil.addError("Atenção", "A Folha de Pagamento do tipo '" + (selecionado).getTipoFolhaDePagamento().getDescricao() + "' já está com a competência efetivada. Reabra a competência para prosseguir.");
        }
    }

    @Override
    public void excluir() {
        if (selecionado.getEfetivadaEm() != null) {
            FacesUtil.addError("Atenção!", "A Folha já está efetivada e não pode ser excluída.");
        } else if (selecionado.getCompetenciaFP() != null && !selecionado.getCompetenciaFP().getStatusCompetencia().equals(StatusCompetencia.ABERTA)) {
            FacesUtil.addError("Atenção!", "A Folha não pode ser excluída, a competência não está aberta.");
        } else {
            try {
                folhaDePagamentoFacade.remover(selecionado);
                redireciona();
                FacesUtil.addInfo(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Registro excluído com sucesso.");
            } catch (Exception e) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            }
        }
    }

    public List<SelectItem> getTiposEmpenhos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoEmpenhoFP object : TipoEmpenhoFP.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
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

    public Converter getConverterItemEntidadeDPContas() {
        if (converterItemEntidadeDPContas == null) {
            converterItemEntidadeDPContas = new ConverterAutoComplete(ItemEntidadeDPContas.class, itemEntidadeDPContasFacade);
        }
        return converterItemEntidadeDPContas;
    }

    public List<SelectItem> getTipoFolhaDePagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
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

    /**
     * @return the contratosSemEnquadramento
     */
    public List<ContratoFP> getContratosSemEnquadramento() {
        return contratosSemEnquadramento;
    }

    /**
     * @param contratosSemEnquadramento the contratosSemEnquadramento to set
     */
    public void setContratosSemEnquadramento(List<ContratoFP> contratosSemEnquadramento) {
        this.contratosSemEnquadramento = contratosSemEnquadramento;
    }

    public DetalheProcessamentoFolha getDetalheProcessamentoFolha() {
        return detalheProcessamentoFolha;
    }

    public void setDetalheProcessamentoFolha(DetalheProcessamentoFolha detalheProcessamentoFolha) {
        this.detalheProcessamentoFolha = detalheProcessamentoFolha;
    }

    public List<String> getRetorno() {
        return retorno;
    }

    public void setRetorno(List<String> retorno) {
        this.retorno = retorno;
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
        assistenteBarraProgresso.setExecutando(false);
    }

    public boolean isLiberaCalculo() {
        return detalheProcessamentoFolha.isLiberaCalculo();
    }


    public void posCalculo() {
        if (detalheProcessamentoFolha.isLiberaCalculo()) {
            FacesUtil.executaJavaScript("terminaPagamento();");
        }
    }

    public StringBuilder getLogSucesso() {
        return detalheProcessamentoFolha.getLogSucesso();
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

    public boolean isImprimeLogEmArquivo() {
        return imprimeLogEmArquivo;
    }

    public void setImprimeLogEmArquivo(boolean imprimeLogEmArquivo) {
        this.imprimeLogEmArquivo = imprimeLogEmArquivo;
    }

    public boolean isGravarMemoriaCalculo() {
        return gravarMemoriaCalculo;
    }

    public void setGravarMemoriaCalculo(boolean gravarMemoriaCalculo) {
        this.gravarMemoriaCalculo = gravarMemoriaCalculo;
    }

    public boolean isProcessarCalculoTransient() {
        return processarCalculoTransient;
    }

    public void setProcessarCalculoTransient(boolean processarCalculoTransient) {
        this.processarCalculoTransient = processarCalculoTransient;
    }

    public void liberarUnidade() {
        filtro.setHierarquiaOrganizacional(null);
    }

    public void liberaServidor() {
        filtro.setVinculoFP(null);
    }

    public void liberaLote() {
        liberarUnidade();
        liberaServidor();
        if (folhaDePagamento != null) {
            if (filtro.getLoteProcessamento() != null && filtro.getLoteProcessamento().getQtdeMesesRetroacao() != null) {
                folhaDePagamento.setQtdeMesesRetroacao(filtro.getLoteProcessamento().getQtdeMesesRetroacao());
            } else {
                folhaDePagamento.setQtdeMesesRetroacao(1);
            }
        }
    }


    public List<SelectItem> getLoteProcessamentos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (folhaDePagamento != null) {
            if (folhaDePagamento.getMes() != null && folhaDePagamento.getAno() != null && folhaDePagamento.getTipoFolhaDePagamento() != null) {
                for (LoteProcessamento object : loteProcessamentoFacade.getLotesPorMesAnoAndTipoFolhaDePagamento(folhaDePagamento.getMes(), folhaDePagamento.getAno(), folhaDePagamento.getTipoFolhaDePagamento())) {
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
        if (folhaDePagamento != null) {
            folhaDePagamento = folhaDePagamentoFacade.recuperarSimples(folhaDePagamento.getId());
            folhaDePagamento.setCalculadaEm(sistemaControlador.getDataOperacao());
        }
    }

    public List<SelectItem> getFolhasDePagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        if (competenciaFP == null) {
            return toReturn;
        }

        List<FolhaDePagamento> lista = folhaDePagamentoFacade.recuperaFolhaPorCompetenciaEStatus(competenciaFP);
        if (lista.isEmpty()) {
            return toReturn;

        }

        for (FolhaDePagamento obj : lista) {
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
        List<SelectItem> toReturn = new ArrayList<>();
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

    public ConfiguracaoRH getConfiguracaoRH() {
        return configuracaoRH;
    }

    public void setConfiguracaoRH(ConfiguracaoRH configuracaoRH) {
        this.configuracaoRH = configuracaoRH;
    }

    public void exibirFolhaPortal() {
        selecionado.setExibirPortal(Boolean.TRUE);
        selecionado.setDataPortal(sistemaFacade.getDataOperacao());
        folhaDePagamentoFacade.salvar(selecionado);
    }

    public void omitirFolhaPortal() {
        selecionado.setExibirPortal(Boolean.FALSE);
        selecionado.setDataPortal(null);
        folhaDePagamentoFacade.salvar(selecionado);
    }

    public ItemCalendarioFP getItemCalendarioFP() {
        if (itemCalendarioFP == null) {
            itemCalendarioFP = calendarioFPFacade.buscarDataCalculoCalendarioFPPorFolha(getFolhaDePagamento());
        }
        return itemCalendarioFP;
    }

    public void setItemCalendarioFP(ItemCalendarioFP itemCalendarioFP) {
        this.itemCalendarioFP = itemCalendarioFP;
    }

    public boolean desabilitarConfiguracaoRetrocao() {
        if (folhaDePagamento != null && folhaDePagamento.getTipoFolhaDePagamento() != null) {
            if (COMPLEMENTAR.equals(folhaDePagamento.getTipoFolhaDePagamento()) || RESCISAO_COMPLEMENTAR.equals(folhaDePagamento.getTipoFolhaDePagamento())) {
                return true;
            }
            if( (SALARIO_13.equals(folhaDePagamento.getTipoFolhaDePagamento())  || ADIANTAMENTO_13_SALARIO.equals(folhaDePagamento.getTipoFolhaDePagamento()) ) && !podeConfigurarRetroacaoParaDecimo){
                return true;
            }
        }
        return false;
    }

    public List<HierarquiaOrganizacional> getHieraquiasOrganizacionaisSelecionadas() {
        return hieraquiasOrganizacionaisSelecionadas;
    }

    public void setHieraquiasOrganizacionaisSelecionadas(List<HierarquiaOrganizacional> hieraquiasOrganizacionaisSelecionadas) {
        this.hieraquiasOrganizacionaisSelecionadas = hieraquiasOrganizacionaisSelecionadas;
    }

    public boolean deveRenderizarArvoreHierarquiaOrganizacional() {
        return this.hieraquiasOrganizacionaisSelecionadas != null && TipoCalculo.TODOS.equals(filtro.getTipoCalculo());
    }


    public void resetarPropsArvoreHierarquiaOrganizacional() {
        setHieraquiasOrganizacionaisSelecionadas(new ArrayList<>());
    }

    public void popularAtributosFiltro(FiltroFolhaDePagamentoDTO filtro) {
        if (filtro == null) {
            return;
        }
        if (filtro.getHierarquiaOrganizacionalId() != null) {
            filtro.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.recuperar(filtro.getHierarquiaOrganizacionalId()));
        }
        if (filtro.getItemEntidadeDPContasId() != null) {
            filtro.setItemEntidadeDPContas(itemEntidadeDPContasFacade.recuperar(filtro.getItemEntidadeDPContasId()));
        }
        if (filtro.getVinculoFPId() != null) {
            filtro.setVinculoFP(vinculoFPFacade.recuperar(filtro.getVinculoFPId()));
        }
        if (filtro.getLoteProcessamentoId() != null) {
            filtro.setLoteProcessamento(loteProcessamentoFacade.recuperar(filtro.getLoteProcessamentoId()));
        }
        if (filtro.getEventoFPsBloqueadosIds() != null && !filtro.getEventoFPsBloqueadosIds().isEmpty()) {
            filtro.setEventosFPsBloqueados(eventoFPFacade.buscarEventoFPs(filtro.getEventoFPsBloqueadosIds()));
        }
        if (filtro.getHierarquiasOrganizacionaisIds() != null && !filtro.getHierarquiasOrganizacionaisIds().isEmpty()) {
            filtro.setHierarquiasOrganizacionais(hierarquiaOrganizacionalFacade.buscarHierarquiasOrganizacionais(filtro.getHierarquiasOrganizacionaisIds()));
        }
    }

    public void buscarDadosFiltros() {
        filtros = Lists.newArrayList();
        if (selecionado == null || selecionado.getId() == null) {
            return;
        }
        List<FiltroFolhaDePagamento> filtrosFolhaDePagamento = folhaDePagamentoFacade.buscarFiltros(selecionado);
        for(FiltroFolhaDePagamento filtroFolhaDePagamento :filtrosFolhaDePagamento) {
            FiltroFolhaDePagamentoDTO filtroFolhaDePagamentoDTO = FiltroFolhaDePagamentoDTO.daEntidade(filtroFolhaDePagamento);
            popularAtributosFiltro(filtroFolhaDePagamentoDTO);
            filtros.add(filtroFolhaDePagamentoDTO);
        }
    }

    public List<FiltroFolhaDePagamentoDTO> getFiltros() {
        return filtros;
    }

    public void setFiltros(List<FiltroFolhaDePagamentoDTO> filtros) {
        this.filtros = filtros;
    }

    public FiltroFolhaDePagamento getFiltroFolhaDePagamentoAtual() {
        return filtroFolhaDePagamentoAtual;
    }

    public void setFiltroFolhaDePagamentoAtual(FiltroFolhaDePagamento filtroFolhaDePagamentoAtual) {
        this.filtroFolhaDePagamentoAtual = filtroFolhaDePagamentoAtual;
    }
}
