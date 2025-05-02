
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroImobiliarioDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroRuralDAO;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


@ManagedBean(name = "processoDebitoEmLoteControlador")
@ViewScoped
@URLMappings(mappings = {

    @URLMapping(id = "listaProcessoPrescricaoEmLote", pattern = "/tributario/conta-corrente/prescricao-de-debitos-em-lote/listar/", viewId = "/faces/tributario/contacorrente/processodebitosprescricaolote/lista.xhtml"),
    @URLMapping(id = "novoProcessoPrescricaoEmLote", pattern = "/tributario/conta-corrente/prescricao-de-debitos-em-lote/novo/", viewId = "/faces/tributario/contacorrente/processodebitosprescricaolote/edita.xhtml"),
    @URLMapping(id = "verProcessoPrescricaoEmLote", pattern = "/tributario/conta-corrente/prescricao-de-debitos-em-lote/ver/#{processoDebitoEmLoteControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosprescricaolote/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoPrescricaoEmLote", pattern = "/tributario/conta-corrente/prescricao-de-debitos-em-lote/editar/#{processoDebitoEmLoteControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitosprescricaolote/edita.xhtml"),

})

public class ProcessoDebitoEmLoteControlador extends PrettyControlador<ProcessoDebitoEmLote> implements Serializable, CRUD {

    private final String DIVIDA_ATIVA = "Dívida Ativa";
    private final String DIVIDA_ATIVA_AJUIZADA = "Dívida Ativa Ajuizada";
    private final String DO_EXERCICIO = "Do Exercício";
    @EJB
    private ProcessoDebitoFacade processoDebitoFacade;
    @EJB
    private ProcessoDebitoEmLoteFacade processoDebitoEmLoteFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    private JdbcCadastroImobiliarioDAO jdbcCadastroImobiliarioDAO;
    private JdbcCadastroRuralDAO jdbcCadastroRuralDAO;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterDivida;
    private ConverterAutoComplete converterExercicio;
    private List<ParcelaValorDivida> listaParcela;
    //FILTROS PARA CONSULTA
    private String filtroCodigoBarras;
    private String filtroNumeroDAM;
    private ItemProcessoDebito itemProcessoDebito;
    private TipoCadastroTributario tipoCadastroTributario;
    private Divida filtroDivida;
    private Exercicio filtroExercicioInicio;
    private Exercicio filtroExercicioFinal;
    private boolean dividaDoExercicio;
    private boolean dividaAtiva;
    private boolean dividaAtivaAzuijada;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroRendasPatrimoniais;
    private ConverterAutoComplete converterCadastroRural;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private LoteFacade loteFacade;
    private List<ResultadoParcela> resultadoConsulta;
    private SituacaoParcela situacaoParcela;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private ResultadoParcela[] resultados;
    private SituacaoCadastralCadastroEconomico situacaoEscolhidaCadastroEconomico;
    private Boolean situacaoEscolhidaCadastroImobiliarioOuRural;
    private SituacaoCadastralPessoa situacaoEscolhidaCadastroPessoa;
    private List<ParcelaProcessoDebitoEmLote> novosItens = Lists.newArrayList();
    private List<ParcelaProcessoDebitoEmLote> itensRemovidos = Lists.newArrayList();
    private LazyDataModel<ParcelaProcessoDebitoEmLote> itensDoProcesso;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<ResultadoValidacao> futureEstorno;

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public void setResultadoConsulta(List<ResultadoParcela> resultadoConsulta) {
        this.resultadoConsulta = resultadoConsulta;
    }

    public List<ParcelaProcessoDebitoEmLote> getItensRemovidos() {
        return itensRemovidos;
    }

    public List<ParcelaProcessoDebitoEmLote> getNovosItens() {
        return novosItens;
    }

    private void criarDataModelDosItens() {
        itensDoProcesso = new LazyDataModel<ParcelaProcessoDebitoEmLote>() {
            @Override
            public List<ParcelaProcessoDebitoEmLote> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                logger.error("First {}", first);
                setRowCount(processoDebitoEmLoteFacade.contarItens(selecionado, itensRemovidos) + novosItens.size());
                if (novosItens != null && novosItens.size() > first) {
                    List<ParcelaProcessoDebitoEmLote> itens = novosItens.subList(first, first + (novosItens.size() < pageSize ? novosItens.size() : pageSize));
                    if (itens.size() < pageSize) {
                        itens.addAll(processoDebitoEmLoteFacade.buscarItemProcesso(0, pageSize - itens.size(), selecionado, itensRemovidos));
                    }
                    return itens;
                }
                return processoDebitoEmLoteFacade.buscarItemProcesso(first, pageSize, selecionado, itensRemovidos);
            }
        };
    }

    public LazyDataModel<ParcelaProcessoDebitoEmLote> getItensDoProcesso() {
        return itensDoProcesso;
    }


    public ProcessoDebitoEmLoteControlador() {
        super(ProcessoDebitoEmLote.class);
        resultadoConsulta = Lists.newArrayList();
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcCadastroImobiliarioDAO = (JdbcCadastroImobiliarioDAO) ap.getBean("cadastroImobiliarioJDBCDAO");
        jdbcCadastroRuralDAO = (JdbcCadastroRuralDAO) ap.getBean("cadastroRuralDAO");
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public ProcessoDebitoEmLote getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ProcessoDebitoEmLote selecionado) {
        this.selecionado = selecionado;
    }

    public String getFiltroCodigoBarras() {
        return filtroCodigoBarras;
    }

    public void setFiltroCodigoBarras(String filtroCodigoBarras) {
        this.filtroCodigoBarras = filtroCodigoBarras;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getFiltroNumeroDAM() {
        return filtroNumeroDAM;
    }

    public void setFiltroNumeroDAM(String filtroNumeroDAM) {
        this.filtroNumeroDAM = filtroNumeroDAM;
    }

    public List<ParcelaValorDivida> getListaParcela() {
        return listaParcela;
    }

    public void setListaParcela(List<ParcelaValorDivida> listaParcela) {
        this.listaParcela = listaParcela;
    }

    public ItemProcessoDebito getItemProcessoDebito() {
        return itemProcessoDebito;
    }

    public void setItemProcessoDebito(ItemProcessoDebito itemProcessoDebito) {
        this.itemProcessoDebito = itemProcessoDebito;
    }

    public boolean isDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public boolean isDividaAtivaAzuijada() {
        return dividaAtivaAzuijada;
    }

    public void setDividaAtivaAzuijada(boolean dividaAtivaAzuijada) {
        this.dividaAtivaAzuijada = dividaAtivaAzuijada;
    }

    public boolean isDividaDoExercicio() {
        return dividaDoExercicio;
    }

    public void setDividaDoExercicio(boolean dividaDoExercicio) {
        this.dividaDoExercicio = dividaDoExercicio;
    }

    public Divida getFiltroDivida() {
        return filtroDivida;
    }

    public void setFiltroDivida(Divida filtroDivida) {
        this.filtroDivida = filtroDivida;
    }

    public Exercicio getFiltroExercicioFinal() {
        return filtroExercicioFinal;
    }

    public void setFiltroExercicioFinal(Exercicio filtroExercicioFinal) {
        this.filtroExercicioFinal = filtroExercicioFinal;
    }

    public Exercicio getFiltroExercicioInicio() {
        return filtroExercicioInicio;
    }

    public void setFiltroExercicioInicio(Exercicio filtroExercicioInicio) {
        this.filtroExercicioInicio = filtroExercicioInicio;
    }

    public ResultadoParcela[] getResultados() {
        return resultados;
    }

    public void setResultados(ResultadoParcela[] resultados) {
        this.resultados = resultados;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, processoDebitoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return processoDebitoFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, processoDebitoFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public List<Divida> completarDividas(String parte) {
        return processoDebitoFacade.getDividaFacade().listaDividasDoTipoCadastro(parte.trim(), tipoCadastroTributario, "26756724, 48756594, 6194205, 6321793");
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, processoDebitoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return processoDebitoFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    @Override
    public AbstractFacade getFacede() {
        return processoDebitoEmLoteFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/conta-corrente/" + selecionado.getTipo().getUrl() + "-de-debitos-em-lote/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void novoGeral() {
        itemProcessoDebito = new ItemProcessoDebito();
        operacao = Operacoes.NOVO;
        tipoCadastroTributario = null;
        novosItens = Lists.newArrayList();
        itensRemovidos = Lists.newArrayList();
        try {
            selecionado.setUsuarioIncluiu(processoDebitoFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
            selecionado.setLancamento(processoDebitoFacade.getSistemaFacade().getDataOperacao());
            selecionado.setExercicio(processoDebitoFacade.getSistemaFacade().getExercicioCorrente());
            selecionado.setCodigo(processoDebitoEmLoteFacade.recuperarProximoCodigoPorExercicioTipo(selecionado.getExercicio(), selecionado.getTipo()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro inesperado durante o processo!", " Log do servidor: " + ex.getMessage()));
        }
    }

    @URLAction(mappingId = "novoProcessoPrescricaoEmLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPrescricao() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.PRESCRICAO);
        novoGeral();
    }

    @URLAction(mappingId = "verProcessoPrescricaoEmLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPrescricao() {
        ver();
    }

    @Override
    public void cancelar() {
        if (selecionado.getTipo() != null) {
            String url = selecionado.getTipo().getUrl();
            Web.navegacao(getUrlAtual(), "/tributario/conta-corrente/" + url + "-de-debitos-em-lote/listar/");
        }
    }

    public void ver() {
        recuperarObjeto();
        operacao = Operacoes.VER;
        inicializarObjetos();
        criarDataModelDosItens();
    }

    @Override
    public void editar() {
        super.editar();
        inicializarObjetos();
        criarDataModelDosItens();
    }

    private void inicializarObjetos() {
        selecionado.setDataEstorno(processoDebitoFacade.getSistemaFacade().getDataOperacao());
        tipoCadastroTributario = selecionado.getTipoCadastro();
        novosItens = Lists.newArrayList();
        itensRemovidos = Lists.newArrayList();
    }

    @URLAction(mappingId = "editarProcessoPrescricaoEmLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPrescricao() {
        editar();
    }

    @Override
    public void salvar() {
        try {
            ResultadoValidacao resultadoValidacao = validarProcesso(selecionado);
            if (resultadoValidacao.isValidado()) {
                selecionado.setTipoCadastro(tipoCadastroTributario);
                if (!novosItens.isEmpty()) {
                    selecionado.setParcelasProcessoEmLote(novosItens);
                }
                selecionado = processoDebitoEmLoteFacade.salvaRetornando(selecionado);
                processoDebitoEmLoteFacade.deletarItens(itensRemovidos);
                navegaParaVisualizar();
            } else {
                FacesUtil.printAllMessages(resultadoValidacao.getMensagens());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    public ResultadoValidacao validarProcesso(ProcessoDebitoEmLote processo) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        final String summary = "Não foi possível continuar!";
        if (processo.getCodigo() == null) {
            retorno.addErro(null, summary, "O Código deve ser preenchido!");
        } else if (processo.getCodigo().longValue() <= 0) {
            retorno.addErro(null, summary, "O Código deve ser maior que zero!");
        } else if (processoDebitoEmLoteFacade.codigoEmUso(processo)) {
            retorno.addErro(null, summary, "Código informado em uso! Foi gerado um novo código.");
        }
        if (processo.getTipo() == null) {
            retorno.addErro(null, summary, "O Tipo de Processo de Débitos deve ser informado!");
        }
        if (processo.getLancamento() == null) {
            retorno.addErro(null, summary, "A Data de Lançamento deve ser preenchida!");
        }
        if (processo.getMotivo() == null || processo.getMotivo().trim().length() <= 0) {
            retorno.addErro(null, summary, "O Motivo ou Fundamentação Legal deve ser preenchido!");
        }
        if (((novosItens == null || novosItens.isEmpty()) && (itensDoProcesso == null || itensDoProcesso.getRowCount() == 0))) {
            retorno.addErro(null, summary, "O processo deve conter ao menos uma parcela!");
        }
        if (processo.getUsuarioIncluiu() == null || processo.getUsuarioIncluiu().getId() == null) {
            retorno.addErro(null, summary, "O usuário que incluiu o processo deve ser informado!");
        }
        if (processo.getSituacao() == null) {
            retorno.addErro(null, summary, "A Situação do Processo deve ser preenchida!");
        } else if (processo.getId() != null && processo.getSituacao() != SituacaoProcessoDebito.EM_ABERTO) {
            retorno.addErro(null, summary, "O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos EM ABERTO podem ser alterados.");
        }
        if (TipoProcessoDebito.BAIXA.equals(processo.getTipo())) {
            if (processo.getDataPagamento() == null) {
                retorno.addErro(null, summary, "A Data do Pagamento deve ser preenchida!");
            }
            if (processo.getValorPago() == null || processo.getValorPago().compareTo(BigDecimal.ZERO) <= 0) {
                retorno.addErro(null, summary, "O Valor Pago deve ser preenchido!");
            }
        }
        if (TipoProcessoDebito.SUSPENSAO.equals(processo.getTipo())) {
            if (processo.getValidade() == null || Util.getDataHoraMinutoSegundoZerado(processo.getValidade())
                .compareTo(Util.getDataHoraMinutoSegundoZerado(new Date())) < 0) {
                retorno.addErro(null, summary, "A Data de Validade deve ser preenchida e deve ser igual ou posterior a data atual!");
            }
        }
        return retorno;
    }

    private void navegaParaVisualizar() {
        String url = selecionado.getTipo().getUrl();
        Web.navegacao(getUrlAtual(), "/tributario/conta-corrente/" + url + "-de-debitos-em-lote/ver/" + selecionado.getId() + "/");
    }

    public void encerrarProcesso() {
        try {
            validarEncerramentoProcesso();
            ResultadoValidacao resultado = processoDebitoEmLoteFacade.encerrarProcesso(selecionado, processoDebitoEmLoteFacade.getSistemaFacade().getUsuarioCorrente());
            if (resultado.isValidado()) {
                processoDebitoEmLoteFacade.executarDependenciasDoProcesso(selecionado);
            }
            Web.navegacao(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Processo de Débitos processado com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao processar: " + e.getMessage());
        }
    }

    private void validarEncerramentoProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (TipoProcessoDebito.PRESCRICAO.equals(selecionado.getTipo())) {
            for (ParcelaProcessoDebitoEmLote item : selecionado.getParcelasProcessoEmLote()) {
                if (item.getParcela().isDebitoProtestado()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido efetivar um processo de prescrição com débito protestado!");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void limpaEstorno() {
        selecionado.setMotivoEstorno(null);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.adicionarParametro("NOME_RELATORIO", "RELATÓRIO DE PRESCRIÇÃO DE DÉBITOS EM LOTE");
            dto.setNomeRelatorio("Relatório de Prescrição de débitos em lote");
            dto.adicionarParametro("ID_PROCESSO", selecionado.getId());
            dto.setApi("tributario/relatorio-prescricao-em-lote/");
            ReportService.getInstance().gerarRelatorio(Util.getSistemaControlador().getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Ocorreu um erro ao gerar o relatorio: " + e);
        }
    }

    public void validarEstornoProcesso() {
        try {
            validarEstorno();
            iniciarEstornoProcesso();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    public void iniciarEstornoProcesso() {
        try {
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            futureEstorno = processoDebitoEmLoteFacade.estornar(assistenteBarraProgresso, selecionado, SituacaoProcessoDebito.ESTORNADO);
            FacesUtil.executaJavaScript("pollEstorno.start()");
            FacesUtil.executaJavaScript("openDialog(dlgEstorno)");
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    public void acompanharEstornoProcesso() {
        if (futureEstorno.isDone()) {
            FacesUtil.executaJavaScript("pollEstorno.stop()");
            FacesUtil.executaJavaScript("closeDialog(dlgEstorno)");
            FacesUtil.executaJavaScript("rcFinalizarEstorno()");
        }
        if (futureEstorno.isCancelled()) {
            FacesUtil.executaJavaScript("pollEstorno.stop()");
            FacesUtil.executaJavaScript("closeDialog(dlgEstorno");
        }
    }

    public void finalizarEstornoProcesso() {
        try {
            ResultadoValidacao resultadoValidacao = futureEstorno.get();
            FacesUtil.printAllMessages(resultadoValidacao.getMensagens());
            if (resultadoValidacao.isValidado()) {
                processoDebitoEmLoteFacade.executarDependenciasDoProcesso(selecionado);
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMotivoEstorno() == null || selecionado.getMotivoEstorno().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo do estorno!");
        }
        if (selecionado.getDataEstorno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data do estorno!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    @Override
    public void excluir() {
        if (selecionado.getSituacao().equals(selecionado.getSituacao().ESTORNADO) || (selecionado.getSituacao().equals(selecionado.getSituacao().FINALIZADO))) {
            FacesUtil.addError("O registro selecionado está finalizado e não pode ser excluido!", "");
        } else {
            try {
                processoDebitoEmLoteFacade.deletarItens(selecionado.getParcelasProcessoEmLote());
                processoDebitoEmLoteFacade.remover(selecionado);
                FacesUtil.addInfo("Registro excluído com sucesso!", "");
                cancelar();
            } catch (Exception e) {
                FacesUtil.addError("Não foi possível realizar a exclusão!", "");
            }
        }
    }

    public void adicionarItem(ProcessoDebitoEmLote processoDebitoEmLote, ParcelaValorDivida parcela) {
        FacesUtil.printAllMessages(processoDebitoEmLoteFacade.adicionarItem(parcela, processoDebitoEmLote, novosItens, listaParcela).getMensagens());
    }

    public void adicionarParcelas() {
        if (resultados.length > 0) {
            try {
                validarParcelaParaAdicionar();
                for (ResultadoParcela resultadoParcela : resultados) {
                    processoDebitoEmLoteFacade.adicionarItem(resultadoParcela, selecionado, novosItens, resultadoConsulta);
                }
                criarDataModelDosItens();
                FacesUtil.addOperacaoRealizada(resultados.length + " parcelas adicionadas ao Processo de Débitos!");
                FacesUtil.atualizarComponente("Formulario");
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        } else {
            FacesUtil.addOperacaoNaoRealizada("Nenhuma parcela foi selecionada!");
        }
    }

    private void validarParcelaParaAdicionar() {
        ValidacaoException ve = new ValidacaoException();
        for (ResultadoParcela resultado : resultados) {
            if (TipoProcessoDebito.PRESCRICAO.equals(selecionado.getTipo())) {
                if (resultado.getPrescricao().after(selecionado.getLancamento())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar parcela que tenha a Data de Prescrição maior que a Data de Lançamento!");
                }
                if (resultado.getDebitoProtestado()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido adicionar débito protestado!");
                }
            }
        }
        ve.lancarException();
    }

    public void removerItem(ParcelaProcessoDebitoEmLote item) {
        if (item != null) {
            itensRemovidos.add(item);
            if (novosItens.contains(item)) {
                novosItens.remove(item);
            }
        }
    }

    public void pesquisar() {
        try {
            validarCamposPreenchidos();
            resultadoConsulta.clear();
            ConsultaParcela consulta = new ConsultaParcela();
            adicionarParametro(consulta);
            if (isContribuinte()) {
                if (situacaoEscolhidaCadastroPessoa != null) {
                    consulta.addComplementoDoWhere(" and pessoa.situacaocadastralpessoa = '" + situacaoEscolhidaCadastroPessoa.name() + "' ");
                }
                consulta.addComplementoDoWhere(" and vw.cadastro_id is null ");
                consulta.executaConsulta();
                for (ResultadoParcela resultado : consulta.getResultados()) {
                    if (resultado.getPrescricao().before(new Date())) {
                        resultadoConsulta.add(resultado);
                    }
                }
                resultadoConsulta.sort(ResultadoParcela.getComparatorByValuePadraoSemCadastro());
            } else {
                if (isCadastroEconomico() && situacaoEscolhidaCadastroEconomico != null) {
                    consulta.addComplementoDoWhere(" and vw.cadastro_id in (select cmc.id " +
                        " from cadastroeconomico cmc " +
                        " where (select situacao.situacaocadastral " +
                        "       from situacaocadastroeconomico situacao " +
                        "       where situacao.id = (select max(sit_ce.id) " +
                        "                            from situacaocadastroeconomico sit_ce " +
                        "                                     inner join ce_situacaocadastral ce_situacao " +
                        "                                                on ce_situacao.situacaocadastroeconomico_id = sit_ce.id " +
                        "                            where ce_situacao.cadastroeconomico_id = cmc.id)) = '" + situacaoEscolhidaCadastroEconomico.name() + "') ");
                } else if (isCadastroImobiliario() && situacaoEscolhidaCadastroImobiliarioOuRural != null) {
                    consulta.addComplementoDoWhere(" and vw.cadastro_id in (select ci.id from cadastroimobiliario ci where coalesce(ci.ativo, 1) = " + (situacaoEscolhidaCadastroImobiliarioOuRural ? 1 : 0) + ") ");
                } else if (isCadastroRural() && situacaoEscolhidaCadastroImobiliarioOuRural != null) {
                    consulta.addComplementoDoWhere(" and vw.cadastro_id in (select cr.id from cadastrorural cr where coalesce(cr.ativo, 1) = " + (situacaoEscolhidaCadastroImobiliarioOuRural ? 1 : 0) + ") ");
                }
                consulta.executaConsulta();
                for (ResultadoParcela resultado : consulta.getResultados()) {
                    if (resultado.getPrescricao().before(new Date())) {
                        resultadoConsulta.add(resultado);
                    }
                }
                resultadoConsulta.sort(ResultadoParcela.getComparatorByValuePadrao());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        try {
            if (resultadoConsulta.isEmpty()) {
                FacesUtil.addError("A pesquisa não encontrou nenhum Débito!", "");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Impossível continuar", e.getMessage());
        }
    }


    public ConsultaParcela adicionarParametro(ConsultaParcela consulta) {
        if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, filtroExercicioInicio.getAno());
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, filtroExercicioFinal.getAno());
        }

        if (filtroDivida != null) {
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, filtroDivida.getId());
        } else if (tipoCadastroTributario != null) {
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, processoDebitoEmLoteFacade.buscarIdDividaPorTipoCadastro(tipoCadastroTributario));
        }

        if (selecionado.getTipo().equals(TipoProcessoDebito.PRESCRICAO)) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.DIFERENTE, 1);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, ConsultaParcela.Operador.DIFERENTE, 1);
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.NOT_IN, processoDebitoEmLoteFacade.buscarIdDividaFiscalizacao());
        } else {
            if (dividaDoExercicio && !dividaAtiva && !dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
            } else if (dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
            } else if (dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
            } else if (!dividaDoExercicio && dividaAtiva && dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
            } else if (!dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
            } else if (!dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
            }
        }

        if (vencimentoInicial != null && vencimentoFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, vencimentoFinal);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, vencimentoInicial);
        }
        consulta.addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC).addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC);
        if (TipoProcessoDebito.REATIVACAO.equals(selecionado.getTipo())) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, Lists.newArrayList(SituacaoParcela.SUSPENSAO, SituacaoParcela.SUSPENSAO_LEI));
        } else {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        }
        return consulta;
    }

    public void inicializarFiltros() {
        filtroCodigoBarras = null;
        filtroNumeroDAM = null;
        listaParcela = Lists.newArrayList();
        resultadoConsulta = Lists.newArrayList();
        filtroDivida = null;
        filtroExercicioFinal = null;
        filtroExercicioInicio = null;
        dividaAtiva = false;
        dividaAtivaAzuijada = false;
        dividaDoExercicio = false;
    }

    private void validarCamposPreenchidos() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoCadastroTributario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro.");
        } else if ((filtroCodigoBarras == null || filtroCodigoBarras.trim().isEmpty())
            && (filtroNumeroDAM == null || filtroNumeroDAM.trim().isEmpty())
            && (filtroDivida == null || filtroDivida.getId() == null)
            && (filtroExercicioInicio == null || filtroExercicioInicio.getId() == null)
            && (filtroExercicioFinal == null || filtroExercicioFinal.getId() == null)
            && (vencimentoInicial == null && vencimentoFinal == null)
            && !dividaAtiva
            && !dividaAtivaAzuijada
            && !dividaDoExercicio) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um filtro.");
        } else if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            if (filtroExercicioFinal.getAno().compareTo(filtroExercicioInicio.getAno()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício final não pode ser posterior ao exercício inicial.");
            }
        } else if (filtroExercicioInicio != null && filtroExercicioFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício final deve ser informado.");
        } else if (filtroExercicioInicio == null && filtroExercicioFinal != null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício inicial deve ser informado.");
        } else if (vencimentoInicial != null && vencimentoFinal != null) {
            if (vencimentoInicial.compareTo(vencimentoFinal) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Final não pode ser maior que a Data de Vencimento Inicial.");
            }
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }


    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    public boolean habilitarBotaoSalvar() {
        inicializarFiltros();
        return selecionado.getSituacao() == null || selecionado.getSituacao().equals(SituacaoProcessoDebito.EM_ABERTO);
    }

    public boolean naoProcessado() {
        return selecionado.getSituacao() != null && selecionado.getSituacao().equals(SituacaoProcessoDebito.EM_ABERTO) && selecionado.getId() != null;
    }

    public boolean habilitarBotaoEstornar() {
        return selecionado.getSituacao() != null
            && selecionado.getSituacao().equals(SituacaoProcessoDebito.FINALIZADO)
            && selecionado.getId() != null
            && selecionado.getValido();
    }

    public void setaSituacaoPadraoCadastro() {
        setSituacaoEscolhidaCadastroImobiliarioOuRural(null);
        setSituacaoEscolhidaCadastroEconomico(null);
        setSituacaoEscolhidaCadastroPessoa(null);
        itensDoProcesso = null;
    }

    public SituacaoCadastralCadastroEconomico getSituacaoEscolhidaCadastroEconomico() {
        return situacaoEscolhidaCadastroEconomico;
    }

    public void setSituacaoEscolhidaCadastroEconomico(SituacaoCadastralCadastroEconomico situacaoEscolhidaCadastroEconomico) {
        this.situacaoEscolhidaCadastroEconomico = situacaoEscolhidaCadastroEconomico;
    }

    public Boolean getSituacaoEscolhidaCadastroImobiliarioOuRural() {
        return situacaoEscolhidaCadastroImobiliarioOuRural;
    }

    public void setSituacaoEscolhidaCadastroImobiliarioOuRural(Boolean situacaoEscolhidaCadastroImobiliarioOuRural) {
        this.situacaoEscolhidaCadastroImobiliarioOuRural = situacaoEscolhidaCadastroImobiliarioOuRural;
    }

    public SituacaoCadastralPessoa getSituacaoEscolhidaCadastroPessoa() {
        return situacaoEscolhidaCadastroPessoa;
    }

    public void setSituacaoEscolhidaCadastroPessoa(SituacaoCadastralPessoa situacaoEscolhidaCadastroPessoa) {
        this.situacaoEscolhidaCadastroPessoa = situacaoEscolhidaCadastroPessoa;
    }

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        return Lists.newArrayList(getSituacaoEscolhidaCadastroPessoa());
    }

    public String buscarInscricaoDoCadastro(ParcelaValorDivida parcela) {
        return processoDebitoFacade.getConsultaDebitoFacade().buscarNumeroTipoCadastroPorParcela(parcela);
    }

    public String retornarSituacaoDaDivida(ParcelaValorDivida parcela) {
        if (parcela == null) {
            return " - ";
        }
        if (parcela.isDividaAtiva()) {
            return DIVIDA_ATIVA;
        }
        if (parcela.isDividaAtivaAjuizada()) {
            return DIVIDA_ATIVA_AJUIZADA;
        }
        return DO_EXERCICIO;
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, processoDebitoFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return processoDebitoFacade.getAtoLegalFacade().listaFiltrando(parte.trim(), "nome");
    }

    public void alterarSituacaoParcela() {
        if (!selecionado.getTipo().equals(TipoProcessoDebito.REATIVACAO)) {
            situacaoParcela = SituacaoParcela.EM_ABERTO;
        } else {
            situacaoParcela = SituacaoParcela.SUSPENSAO;
        }
        FacesUtil.executaJavaScript("dialogo.show()");
    }

    public boolean isCadastroImobiliario() {
        return TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastroTributario);
    }

    public boolean isCadastroEconomico() {
        return TipoCadastroTributario.ECONOMICO.equals(tipoCadastroTributario);
    }

    public boolean isCadastroRural() {
        return TipoCadastroTributario.RURAL.equals(tipoCadastroTributario);
    }

    public boolean isContribuinte() {
        return TipoCadastroTributario.PESSOA.equals(tipoCadastroTributario);
    }
}
