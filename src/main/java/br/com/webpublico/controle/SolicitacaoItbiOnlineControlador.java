package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.SimulacaoParcelamento;
import br.com.webpublico.entidadesauxiliares.VODadosCadastroITBI;
import br.com.webpublico.entidadesauxiliares.VOProprietarioITBI;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.CadastroRuralFacade;
import br.com.webpublico.negocios.SolicitacaoItbiOnlineFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoItbiOnline", pattern = "/itbi-online/novo/", viewId = "/faces/tributario/itbi/solicitacaoitbionline/edita.xhtml"),
    @URLMapping(id = "editaItbiOnline", pattern = "/itbi-online/editar/#{solicitacaoItbiOnlineControlador.id}/", viewId = "/faces/tributario/itbi/solicitacaoitbionline/edita.xhtml"),
    @URLMapping(id = "verItbiOnline", pattern = "/itbi-online/ver/#{solicitacaoItbiOnlineControlador.id}/", viewId = "/faces/tributario/itbi/solicitacaoitbionline/visualizar.xhtml"),
    @URLMapping(id = "listarItbiOnline", pattern = "/itbi-online/listar/", viewId = "/faces/tributario/itbi/solicitacaoitbionline/lista.xhtml")})
public class SolicitacaoItbiOnlineControlador extends PrettyControlador<SolicitacaoItbiOnline> implements CRUD {

    @EJB
    private SolicitacaoItbiOnlineFacade solicitacaoItbiOnlineFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    private Map<Long, List<VOProprietarioITBI>> mapaProprietarios;
    private VODadosCadastroITBI dadosCadastroITBI;
    private CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline;
    private TransmitentesSolicitacaoItbiOnline transmitente;
    private AdquirentesSolicitacaoITBIOnline adquirente;
    private ItemCalculoITBIOnline itemCalculo;
    private ParametrosITBI parametro;
    private List<PropriedadeSimulacaoITBIOnline> transmitentesSimulacao;
    private List<PropriedadeSimulacaoITBIOnline> adquirentesSimulacao;
    private boolean canMostrarInfoCadastros;
    private String mensagemValidacao;
    private AssistenteBarraProgresso assistente;
    private CompletableFuture<AssistenteBarraProgresso> futureHomologacao;
    private SolicitacaoItbiOnlineDocumento solicitacaoDocumento;
    private String observacao;
    private List<TramiteSolicitacaoItbiOnline> tramites;
    private UsuarioSistema auditorFiscal;
    private boolean dadosCorretos;
    private Arquivo planilhaAvaliacao;
    private BigDecimal valorAvaliado;
    private Map<CalculoSolicitacaoItbiOnline, List<SimulacaoParcelamento>> mapaSimulacaoPorCalculo;
    private Map<Integer, String> mapaIconeBtnParcela;
    private boolean canMostrarParcelasDoCalculo;
    private Integer indexParcelas;
    private SolicitacaoItbiOnlineDocumento documentoOriginalSelecionado;
    private ConverterAutoComplete converterDocumentoSolicitacao;

    public SolicitacaoItbiOnlineControlador() {
        super(SolicitacaoItbiOnline.class);
    }

    @Override
    public AbstractFacade<SolicitacaoItbiOnline> getFacede() {
        return solicitacaoItbiOnlineFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/itbi-online/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<TramiteSolicitacaoItbiOnline> getTramites() {
        return tramites;
    }

    public void setTramites(List<TramiteSolicitacaoItbiOnline> tramites) {
        this.tramites = tramites;
    }

    public UsuarioSistema getAuditorFiscal() {
        return auditorFiscal;
    }

    public void setAuditorFiscal(UsuarioSistema auditorFiscal) {
        this.auditorFiscal = auditorFiscal;
    }

    public boolean getDadosCorretos() {
        return dadosCorretos;
    }

    public void setDadosCorretos(boolean dadosCorretos) {
        this.dadosCorretos = dadosCorretos;
    }

    public Arquivo getPlanilhaAvaliacao() {
        return planilhaAvaliacao;
    }

    public void setPlanilhaAvaliacao(Arquivo planilhaAvaliacao) {
        this.planilhaAvaliacao = planilhaAvaliacao;
    }

    public BigDecimal getValorAvaliado() {
        return valorAvaliado;
    }

    public void setValorAvaliado(BigDecimal valorAvaliado) {
        this.valorAvaliado = valorAvaliado;
    }

    public SolicitacaoItbiOnlineDocumento getDocumentoOriginalSelecionado() {
        return documentoOriginalSelecionado;
    }

    public void setDocumentoOriginalSelecionado(SolicitacaoItbiOnlineDocumento documentoOriginalSelecionado) {
        this.documentoOriginalSelecionado = documentoOriginalSelecionado;
    }

    public ConverterAutoComplete getConverterDocumentoSolicitacao() {
        if (converterDocumentoSolicitacao == null) {
            converterDocumentoSolicitacao = new ConverterAutoComplete(SolicitacaoItbiOnlineDocumento.class, solicitacaoItbiOnlineFacade);
        }
        return converterDocumentoSolicitacao;
    }

    public void setConverterDocumentoSolicitacao(ConverterAutoComplete converterDocumentoSolicitacao) {
        this.converterDocumentoSolicitacao = converterDocumentoSolicitacao;
    }

    @URLAction(mappingId = "novoItbiOnline", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarDadosNovo();
    }

    @URLAction(mappingId = "editaItbiOnline", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarDadosEditar();
    }

    @URLAction(mappingId = "verItbiOnline", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializarDadosVer();
    }

    public boolean isCanMostrarInfoCadastros() {
        return canMostrarInfoCadastros;
    }

    public void setCanMostrarInfoCadastros(boolean canMostrarInfoCadastros) {
        this.canMostrarInfoCadastros = canMostrarInfoCadastros;
    }

    public List<PropriedadeSimulacaoITBIOnline> getTransmitentesSimulacao() {
        if (transmitentesSimulacao == null) {
            transmitentesSimulacao = Lists.newArrayList();
        }
        return transmitentesSimulacao;
    }

    public List<PropriedadeSimulacaoITBIOnline> getAdquirentesSimulacao() {
        if (adquirentesSimulacao == null) {
            adquirentesSimulacao = Lists.newArrayList();
        }
        return adquirentesSimulacao;
    }

    public void setAdquirentesSimulacao(List<PropriedadeSimulacaoITBIOnline> adquirentesSimulacao) {
        this.adquirentesSimulacao = adquirentesSimulacao;
    }

    public VODadosCadastroITBI getDadosCadastroITBI() {
        return dadosCadastroITBI;
    }

    public void setDadosCadastroITBI(VODadosCadastroITBI dadosCadastroITBI) {
        this.dadosCadastroITBI = dadosCadastroITBI;
    }

    public CalculoSolicitacaoItbiOnline getCalculoITBIOnline() {
        return calculoSolicitacaoItbiOnline;
    }

    public void setCalculoITBIOnline(CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline) {
        this.calculoSolicitacaoItbiOnline = calculoSolicitacaoItbiOnline;
    }

    public boolean isCanMostrarParcelasDoCalculo() {
        return canMostrarParcelasDoCalculo;
    }

    public void setCanMostrarParcelasDoCalculo(boolean canMostrarParcelasDoCalculo) {
        this.canMostrarParcelasDoCalculo = canMostrarParcelasDoCalculo;
    }

    public Map<Integer, String> getMapaIconeBtnParcela() {
        if (mapaIconeBtnParcela == null) mapaIconeBtnParcela = new HashMap<>();
        return mapaIconeBtnParcela;
    }

    public void setMapaIconeBtnParcela(Map<Integer, String> mapaIconeBtnParcela) {
        this.mapaIconeBtnParcela = mapaIconeBtnParcela;
    }

    public TransmitentesSolicitacaoItbiOnline getTransmitente() {
        return transmitente;
    }

    public void setTransmitente(TransmitentesSolicitacaoItbiOnline transmitente) {
        this.transmitente = transmitente;
    }

    public ItemCalculoITBIOnline getItemCalculo() {
        return itemCalculo;
    }

    public void setItemCalculo(ItemCalculoITBIOnline itemCalculo) {
        this.itemCalculo = itemCalculo;
    }

    public AdquirentesSolicitacaoITBIOnline getAdquirente() {
        return adquirente;
    }

    public void setAdquirente(AdquirentesSolicitacaoITBIOnline adquirente) {
        this.adquirente = adquirente;
    }

    public String getMensagemValidacao() {
        return mensagemValidacao;
    }

    public void setMensagemValidacao(String mensagemValidacao) {
        this.mensagemValidacao = mensagemValidacao;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public void preSalvar() {
        try {
            validarSalvar();

            int countCalculosIsentos = 0;
            for (CalculoSolicitacaoItbiOnline calc : selecionado.getCalculos()) {
                if (calc.getTipoIsencaoITBI() != null) {
                    countCalculosIsentos++;
                }
            }
            if (selecionado.getCalculos().size() != countCalculosIsentos) {
                simularParcelas();
                definirIconeBtnParcela();
                FacesUtil.executaJavaScript("wParcelas.show()");
                FacesUtil.atualizarComponente("formDialogParcelas");
            } else {
                FacesUtil.executaJavaScript("wConfirmSalvar.show()");
                FacesUtil.atualizarComponente("formDialogConfirmSalvar");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void definirIconeBtnParcela() {
        for (int i = 0; i < selecionado.getCalculos().size(); i++) {
            getMapaIconeBtnParcela().put(i, "ui-icon ui-icon-circle-triangle-e");
        }
    }

    public void mostrarParcelasDoCalculo(CalculoSolicitacaoItbiOnline calculo, Integer index) {
        calculoSolicitacaoItbiOnline = calculo;
        canMostrarParcelasDoCalculo = !canMostrarParcelasDoCalculo;
        indexParcelas = indexParcelas == null ? index : null;
        for (Map.Entry<Integer, String> entry : mapaIconeBtnParcela.entrySet()) {
            if (entry.getKey().compareTo(index) != 0) {
                entry.setValue("ui-icon ui-icon-circle-triangle-e");
            }
        }
        mapaIconeBtnParcela.put(index, (canMostrarParcelasDoCalculo ? "ui-icon ui-icon-circle-triangle-s" : "ui-icon ui-icon-circle-triangle-e"));
    }

    public void simularParcelas() {
        mapaSimulacaoPorCalculo = Maps.newHashMap();

        if (selecionado != null && selecionado.getCalculos() != null && !selecionado.getCalculos().isEmpty()) {
            for (CalculoSolicitacaoItbiOnline calculo : selecionado.getCalculos()) {
                if (calculo.getTipoIsencaoITBI() != null) {
                    mapaSimulacaoPorCalculo.put(calculo, Lists.newArrayList());
                } else {
                    simularParcelasPorCalculo(calculo);
                }
            }
        }
    }

    public List<CalculoSolicitacaoItbiOnline> buscarCalculosDoMapa() {
        if (mapaSimulacaoPorCalculo != null) {
            return Lists.newArrayList(mapaSimulacaoPorCalculo.keySet());
        }
        return Lists.newArrayList();
    }

    public List<SimulacaoParcelamento> buscarSimulacoesDoCalculo(CalculoSolicitacaoItbiOnline calculo) {
        if (calculo != null && mapaSimulacaoPorCalculo.containsKey(calculo)) {
            return mapaSimulacaoPorCalculo.get(calculo);
        }
        return Lists.newArrayList();
    }

    public void simularParcelasPorCalculo(CalculoSolicitacaoItbiOnline calculo) {
        if (calculo.getQuantidadeParcelas().compareTo(buscarQuantidadeMaximaDeParcelas(calculo)) > 0 || calculo.getQuantidadeParcelas().compareTo(0) <= 0) {
            FacesUtil.addOperacaoNaoRealizada("O número de parcelas deve ser maior que zero(0) e menor que " + buscarQuantidadeMaximaDeParcelas(calculo) + ".");
            calculo.setQuantidadeParcelas(buscarQuantidadeMaximaDeParcelas(calculo));
        } else {
            mapaSimulacaoPorCalculo.remove(calculo);
            Calendar vencimento = Calendar.getInstance();
            vencimento.setTime(buscarVencimentoPrimeiraParcela());

            BigDecimal valorParcela = calculo.getValorCalculado().divide(new BigDecimal(calculo.getQuantidadeParcelas()), 2, RoundingMode.DOWN);
            BigDecimal valorPrimeiraParcela = calculo.getValorCalculado().subtract(valorParcela.multiply(new BigDecimal(calculo.getQuantidadeParcelas() - 1)));

            for (int i = 1; i <= calculo.getQuantidadeParcelas(); i++) {
                SimulacaoParcelamento simulacao = new SimulacaoParcelamento();

                simulacao.setParcela("Parcela " + i);
                if (i == 1) {
                    simulacao.setValor(valorPrimeiraParcela);
                } else {
                    simulacao.setValor(valorParcela);
                }
                simulacao.setVencimento(vencimento.getTime());

                vencimento.setTime(DataUtil.ajustarData(vencimento.getTime(), Calendar.MONTH, 1, solicitacaoItbiOnlineFacade.getFeriadoFacade()));

                if (!mapaSimulacaoPorCalculo.containsKey(calculo)) {
                    mapaSimulacaoPorCalculo.put(calculo, Lists.newArrayList(simulacao));
                } else {
                    mapaSimulacaoPorCalculo.get(calculo).add(simulacao);
                }
            }
        }
    }

    public String buscarIconeBtnParcela(Integer index) {
        String icone = mapaIconeBtnParcela.get(index);
        return icone != null ? icone : "ui-icon ui-icon-circle-triangle-e";
    }

    @Override
    public void salvar() {
        try {
            selecionado = solicitacaoItbiOnlineFacade.salvarRetornando(selecionado);
            redirecionarParaVisualiza("Solicitação de ITBI salva com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar solicitação ITBI. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar a solicitação de ITBI. Detalhes: " + e.getMessage());
        }
    }

    private void redirecionarParaVisualiza(String msg) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        FacesUtil.addOperacaoRealizada(msg);
    }

    public void redirecionarParaEditar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId());
    }

    private void validarSalvar() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        validarBloqueioTransferenciaProprietario();
        ve.lancarException();
    }

    private void inicializarDadosNovo() {
        this.transmitente = new TransmitentesSolicitacaoItbiOnline();
        this.adquirente = new AdquirentesSolicitacaoITBIOnline();
        iniciarCalculo();
        this.mapaProprietarios = Maps.newHashMap();
        this.canMostrarInfoCadastros = false;
        selecionado.setExercicio(solicitacaoItbiOnlineFacade.buscarExercicioCorrente());
        selecionado.setDataLancamento(solicitacaoItbiOnlineFacade.buscarDataOperacao());
        selecionado.setTipoITBI(TipoITBI.IMOBILIARIO);
        selecionado.setSituacao(SituacaoSolicitacaoITBI.EM_ANALISE);
        selecionado.setUsuarioSistema(solicitacaoItbiOnlineFacade.getSistemaFacade().getUsuarioCorrente());
        recuperarParametro();
        selecionado.setDocumentos(Lists.newArrayList());
        solicitacaoItbiOnlineFacade.popularDocumentosSolicitacao(parametro, selecionado);
    }

    private void iniciarCalculo() {
        this.calculoSolicitacaoItbiOnline = new CalculoSolicitacaoItbiOnline();
        this.calculoSolicitacaoItbiOnline.setPercentual(CEM);
        this.itemCalculo = new ItemCalculoITBIOnline();
    }

    private void inicializarDadosEditar() {
        this.mapaProprietarios = Maps.newHashMap();
        this.transmitente = new TransmitentesSolicitacaoItbiOnline();
        this.adquirente = new AdquirentesSolicitacaoITBIOnline();
        this.adquirente = new AdquirentesSolicitacaoITBIOnline();
        this.itemCalculo = new ItemCalculoITBIOnline();
        recuperarParametro();
        ordenarCalculos(selecionado.getCalculos(), false);
        buscarDadosCadastroITBI();
        buscarTramites();
    }

    private void inicializarDadosVer() {
        this.dadosCorretos = false;
        this.mapaProprietarios = Maps.newHashMap();
        auditorFiscal = selecionado.getAuditorFiscal();
        recuperarParametro();
        ordenarCalculos(selecionado.getCalculos(), false);
        buscarDadosCadastroITBI();
        buscarTramites();
    }

    private void buscarTramites() {
        tramites = solicitacaoItbiOnlineFacade.getTramiteSolicitacaoItbiOnlineFacade().buscarTramitesPorSolicitacao(selecionado);
    }

    public void mudouTipoITBI() {
        selecionado.setCadastroImobiliario(null);
        selecionado.setCadastroRural(null);
        recuperarParametro();
        selecionado.setDocumentos(Lists.newArrayList());
        solicitacaoItbiOnlineFacade.popularDocumentosSolicitacao(parametro, selecionado);
        iniciarDocumentosProcurador();
        iniciarCalculo();
    }

    private void recuperarParametro() {
        if (selecionado.getExercicio() != null && selecionado.getTipoITBI() != null) {
            parametro = solicitacaoItbiOnlineFacade.recuperarParametroITBIVigente(selecionado.getExercicio(), selecionado.getTipoITBI());
            if (parametro != null) {
                if (calculoSolicitacaoItbiOnline != null) {
                    calculoSolicitacaoItbiOnline.setTipoBaseCalculoITBI(parametro.getTipoBaseCalculo() != null ? parametro.getTipoBaseCalculo() : TipoBaseCalculo.VALOR_APURADO);
                }
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não existe parâmetro de ITBI " + selecionado.getTipoITBI().getDescricao() + " para o exercício de " + selecionado.getExercicio().getAno() + ".");
            }
        }
    }

    public List<SelectItem> buscarTiposITBI() {
        return Util.getListSelectItemSemCampoVazio(TipoITBI.values());
    }

    public List<SelectItem> buscarTiposBaseCalculo() {
        return Util.getListSelectItemSemCampoVazio(TipoBaseCalculo.values());
    }

    public List<SelectItem> buscarTiposNegociacao() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoNegociacao t : solicitacaoItbiOnlineFacade.buscarTiposNegociacaoAtivas()) {
            lista.add(new SelectItem(t, t.getDescricao()));
        }
        return lista;
    }

    public List<PessoaFisica> completarAuditorFiscal(String parte) {
        return solicitacaoItbiOnlineFacade.buscarAuditorFiscal(parte);
    }

    public List<TipoIsencaoITBI> buscarTiposDeIsencaoITBI(String parte) {
        return solicitacaoItbiOnlineFacade.buscarTiposDeIsencaoITBI(parte);
    }

    public List<SituacaoCadastralPessoa> buscarSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public List<? extends Cadastro> buscarCadastroPorTipo(String parte) {
        return solicitacaoItbiOnlineFacade.buscarCadastroPorTipo(parte, isItbiImobiliario());
    }

    public Boolean isItbiImobiliario() {
        return selecionado != null && TipoITBI.IMOBILIARIO.equals(selecionado.getTipoITBI());
    }

    public void colocarSelecionadoNaSecao() {
        Web.poeNaSessao(selecionado);
        Web.poeTodosFieldsNaSessao(this);
    }

    public List<VOProprietarioITBI> buscarProprietarios() {
        if (selecionado != null && selecionado.getCadastro() != null) {
            if (!mapaProprietarios.containsKey(selecionado.getIdCadastro())) {
                List<VOProprietarioITBI> proprietarios = solicitacaoItbiOnlineFacade.buscarProprietarios(selecionado.isImobiliario(), selecionado.getIdCadastro());
                mapaProprietarios.put(selecionado.getIdCadastro(), proprietarios);
            }
            return mapaProprietarios.get(selecionado.getIdCadastro());
        }
        return Lists.newArrayList();
    }

    public void mostrarInfoCadastros() {
        canMostrarInfoCadastros = !canMostrarInfoCadastros;
    }

    public void buscarDadosCadastroITBIAndLimparCalculos() {
        selecionado.getCalculos().clear();
        buscarDadosCadastroITBI();
    }

    private void buscarDadosCadastroITBI() {
        validarBloqueioTransferenciaProprietario();
        if (selecionado.getTipoITBI().equals(TipoITBI.IMOBILIARIO)) {
            dadosCadastroITBI = solicitacaoItbiOnlineFacade.montarDadosProprietarioImobiliario(selecionado.getIdCadastro());
        } else if (selecionado.getTipoITBI().equals(TipoITBI.RURAL)) {
            dadosCadastroITBI = solicitacaoItbiOnlineFacade.montarDadosProprietarioRural(selecionado.getIdCadastro());
        }
    }

    private void validarBloqueioTransferenciaProprietario() {
        if (TipoITBI.IMOBILIARIO.equals(selecionado.getTipoITBI()) &&
            selecionado.getCadastroImobiliario() != null) {
            solicitacaoItbiOnlineFacade.getBloqueioTransferenciaProprietarioFacade()
                .validarBloqueioTransferenciaProprietario(selecionado.getCadastroImobiliario());
        }
    }

    public void mudouCadastroImobiliario() {
        try {
            validarBloqueioTransferenciaProprietario();
            buscarDadosCadastroITBIAndLimparCalculos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public List<Pessoa> adquirentesTransmissaoAnterior(String parte) {
        List<Pessoa> retorno = Lists.newArrayList();
        if (selecionado.getCalculos().isEmpty()) {
            retorno.addAll(solicitacaoItbiOnlineFacade.getPessoaFacade().listaTodasPessoas(parte, buscarSituacoesPesquisaPessoa().get(0)));
        } else {
            for (AdquirentesSolicitacaoITBIOnline adquirentesSolicitacaoITBIOnline : selecionado.getUltimoCalculo().getAdquirentesCalculo()) {
                retorno.add(adquirentesSolicitacaoITBIOnline.getAdquirente());
            }
        }
        return retorno;
    }

    public String montarNomesAdquirentes(CalculoSolicitacaoItbiOnline calculoITBI) {
        StringBuilder adquirente = new StringBuilder();
        for (AdquirentesSolicitacaoITBIOnline adquirenteCalculo : calculoITBI.getAdquirentesCalculo()) {
            adquirente.append(adquirenteCalculo.getAdquirente().getNomeCpfCnpj()).append("\n");
        }
        return adquirente.toString();
    }

    public String montarPercentuaisAdquirentes(CalculoSolicitacaoItbiOnline calculoITBI) {
        StringBuilder percentualAdquirente = new StringBuilder();
        for (AdquirentesSolicitacaoITBIOnline adquirenteCalculo : calculoITBI.getAdquirentesCalculo()) {
            percentualAdquirente.append(adquirenteCalculo.getPercentual()).append("%").append("\n");
        }
        return percentualAdquirente.toString();
    }

    void popularMapSimulacao() {
        setAdquirentesSimulacao(calculoSolicitacaoItbiOnline.getProprietariosSimulacao());
    }

    public void adicionarCalculo() {
        try {
            validarCalculo();
            if (parametro != null) {
                atribuirValoresAoCalculo();
                Util.adicionarObjetoEmLista(selecionado.getCalculos(), calculoSolicitacaoItbiOnline);
                novosProprietariosSimulacao();
                FacesUtil.executaJavaScript("wTransmissao.hide()");
                FacesUtil.addOperacaoRealizada("Transmissão adicionada com sucesso.");
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não foi possível localizar o parâmetro de ITBI.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarCalculo() {
        calculoSolicitacaoItbiOnline = null;
    }

    public void editarCalculo(CalculoSolicitacaoItbiOnline calculo) {
        try {
            validarAcoesCalculo(calculo, "editar");
            calculoSolicitacaoItbiOnline = calculo;
            calculoSolicitacaoItbiOnline.setIsentoSub(calculoSolicitacaoItbiOnline.getTipoIsencaoITBI() != null);
            FacesUtil.executaJavaScript("wTransmissao.show();");
            FacesUtil.atualizarComponente("formTransmissao");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void visualizarSimulacao(CalculoSolicitacaoItbiOnline calc) {
        try {
            calculoSolicitacaoItbiOnline = calc;
            calculoSolicitacaoItbiOnline.setIsentoSub(calculoSolicitacaoItbiOnline.getTipoIsencaoITBI() != null);
            popularMapSimulacao();
            FacesUtil.executaJavaScript("wSimulacaoVer.show();");
            FacesUtil.atualizarComponente("formSimulacaoVer");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarAcoesCalculo(CalculoSolicitacaoItbiOnline calculo, String tipoAcao) {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getUltimoCalculo().equals(calculo)) {
            ve.adicionarMensagemDeCampoObrigatorio("Para " + tipoAcao.trim() + " a transmissão " +
                calculo.getOrdemCalculo() + " é necessário remover a transmissão " + (calculo.getOrdemCalculo() + 1) + "!");
        }
        ve.lancarException();
    }

    public Boolean bloquearAlteracaoDePorcentagemDialog() {
        if (selecionado.getUltimoCalculo() != null) {
            return !selecionado.getUltimoCalculo().equals(calculoSolicitacaoItbiOnline);
        }
        return false;
    }

    public void salvarDialogEdicaoPorcentagem() {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (calculoSolicitacaoItbiOnline != null) {
                BigDecimal porcentagem = BigDecimal.ZERO;
                for (PropriedadeSimulacaoITBIOnline propriedadeSimulacaoITBI : getAdquirentesSimulacao()) {
                    porcentagem = porcentagem.add(BigDecimal.valueOf(propriedadeSimulacaoITBI.getProporcao()));
                }
                if (porcentagem.compareTo(CEM) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A soma do percentual dos adquirentes não deve ultrapassar 100%.");
                }
                if (porcentagem.compareTo(CEM) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A soma do percentual dos adquirentes não deve ser menor que 100%.");
                }
            }
            ve.lancarException();
            calculoSolicitacaoItbiOnline.setProprietariosSimulacao(getAdquirentesSimulacao());
            FacesUtil.executaJavaScript("wSimulacaoVer.hide();");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void visualizarCalculo(CalculoSolicitacaoItbiOnline calculo) {
        this.calculoSolicitacaoItbiOnline = (CalculoSolicitacaoItbiOnline) Util.clonarObjeto(calculo);
        if (calculoSolicitacaoItbiOnline != null && !calculoSolicitacaoItbiOnline.getTransmitentesCalculo().isEmpty()) {
            calculoSolicitacaoItbiOnline.setIsentoSub(calculoSolicitacaoItbiOnline.getTipoIsencaoITBI() != null);
            transmitente = (TransmitentesSolicitacaoItbiOnline) Util.clonarObjeto(calculoSolicitacaoItbiOnline.getTransmitentesCalculo().get(0));
        }
        FacesUtil.executaJavaScript("wTransmissaoVer.show();");
        FacesUtil.atualizarComponente("formTransmissaoVer");
    }

    private void atribuirValoresAoCalculo() {
        if (calculoSolicitacaoItbiOnline != null) {
            calculoSolicitacaoItbiOnline.setExercicio(selecionado.getExercicio());
            calculoSolicitacaoItbiOnline.setSolicitacaoItbiOnline(selecionado);
            calculoSolicitacaoItbiOnline.setSequencia(null);

            BigDecimal valorCalculadoSomado = BigDecimal.ZERO;
            BigDecimal valorInformadoSomado = BigDecimal.ZERO;
            for (ItemCalculoITBIOnline ic : calculoSolicitacaoItbiOnline.getItensCalculo()) {
                valorCalculadoSomado = valorCalculadoSomado.add(ic.getValorCalculado());
                valorInformadoSomado = valorInformadoSomado.add(ic.getValorInformado());
            }
            if (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI())) {
                valorCalculadoSomado = valorCalculadoSomado.add(calculoSolicitacaoItbiOnline.getValorVenal().setScale(2, RoundingMode.HALF_UP).multiply(calculoSolicitacaoItbiOnline.getValorReajuste())).divide(CEM, 2, RoundingMode.HALF_UP);
            }
            if (calculoSolicitacaoItbiOnline.getBaseCalculo().compareTo(BigDecimal.ZERO) == 0) {
                calculoSolicitacaoItbiOnline.setBaseCalculo(valorInformadoSomado);
            }
            calculoSolicitacaoItbiOnline.setValorCalculado(valorCalculadoSomado);
        }
    }

    private void novosProprietariosSimulacao() {
        if (calculoSolicitacaoItbiOnline.getProprietariosSimulacao() != null) {
            calculoSolicitacaoItbiOnline.getProprietariosSimulacao().clear();
        } else {
            calculoSolicitacaoItbiOnline.setProprietariosSimulacao(Lists.newArrayList());
        }
        if (selecionado.getTipoITBI().equals(TipoITBI.IMOBILIARIO)) {
            for (Propriedade novoProprietario : solicitacaoItbiOnlineFacade.simulacaoDasProporcoesImobiliario(selecionado.getCadastroImobiliario(), selecionado.getCalculos()).get((calculoSolicitacaoItbiOnline.getOrdemCalculo()))) {
                PropriedadeSimulacaoITBIOnline propriedadeSimulacao = new PropriedadeSimulacaoITBIOnline();
                propriedadeSimulacao.setPessoa(novoProprietario.getPessoa());
                propriedadeSimulacao.setCalculoITBI(calculoSolicitacaoItbiOnline);
                propriedadeSimulacao.setProporcao(novoProprietario.getProporcao());
                calculoSolicitacaoItbiOnline.getProprietariosSimulacao().add(propriedadeSimulacao);
            }
        } else {
            for (PropriedadeRural novoProprietario : solicitacaoItbiOnlineFacade.simulacaoDasProporcoesRural(selecionado.getCadastroRural(), selecionado.getCalculos()).get(calculoSolicitacaoItbiOnline.getOrdemCalculo())) {
                PropriedadeSimulacaoITBIOnline propriedadeSimulacao = new PropriedadeSimulacaoITBIOnline();
                propriedadeSimulacao.setPessoa(novoProprietario.getPessoa());
                propriedadeSimulacao.setCalculoITBI(calculoSolicitacaoItbiOnline);
                propriedadeSimulacao.setProporcao(novoProprietario.getProporcao());
                calculoSolicitacaoItbiOnline.getProprietariosSimulacao().add(propriedadeSimulacao);
            }
        }
    }

    private void validarCalculo() {
        ValidacaoException ve = new ValidacaoException();
        if (calculoSolicitacaoItbiOnline != null) {
            if (calculoSolicitacaoItbiOnline.getOrdemCalculo() > 1) {
                for (CalculoSolicitacaoItbiOnline calculoItbi : selecionado.getCalculos()) {
                    if (calculoItbi.getOrdemCalculo() == 1) {
                        BigDecimal somaTotalTransmitentes = BigDecimal.ZERO;
                        for (TransmitentesSolicitacaoItbiOnline transmitentesCalculoITBI : calculoItbi.getTransmitentesCalculo()) {
                            somaTotalTransmitentes = transmitentesCalculoITBI.getPercentual().setScale(2, RoundingMode.HALF_UP);
                        }
                        if (CEM.compareTo(somaTotalTransmitentes) > 0) {
                            ve.adicionarMensagemDeCampoObrigatorio("A soma total dos transmitentes da primeira transmissão deve ser igual à 100%.");
                        }
                    }
                }
            }
            if (calculoSolicitacaoItbiOnline.getTransmitentesCalculo() == null || calculoSolicitacaoItbiOnline.getTransmitentesCalculo().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Adicione pelo menos um Transmitente ao processo.");
            }
            if (calculoSolicitacaoItbiOnline.getAdquirentesCalculo() == null || calculoSolicitacaoItbiOnline.getAdquirentesCalculo().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione pelo menos um Adquirente ao processo.");
            }
            if (calculoSolicitacaoItbiOnline.getPercentual() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Percentual da Base de Cálculo deve ser informado.");
            } else if (calculoSolicitacaoItbiOnline.getPercentual().compareTo(BigDecimal.ZERO) <= 0 || calculoSolicitacaoItbiOnline.getPercentual().compareTo(CEM) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Percentual da Base de Cálculo deve estar entre zero(0) e cem(100).");
            }
            if (calculoSolicitacaoItbiOnline.getTotalBaseCalculo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Total da base de Cálculo deve ser informado.");
            } else if (calculoSolicitacaoItbiOnline.getTotalBaseCalculo().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Total da base de Cálculo deve ser maior que zero(0).");
            }
            if (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI()) && (calculoSolicitacaoItbiOnline.getValorReajuste() == null || calculoSolicitacaoItbiOnline.getValorReajuste().compareTo(BigDecimal.ZERO) < 0) || calculoSolicitacaoItbiOnline.getValorReajuste().compareTo(new BigDecimal("999.99")) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Percentual de Reajuste sobre o Valor Venal do Imóvel deve ser maior que zero e menor que 1.000%.");
            }
            if (!TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI())) {
                if (calculoSolicitacaoItbiOnline.getItensCalculo() == null || calculoSolicitacaoItbiOnline.getItensCalculo().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione pelo menos um Tipo de Negociação.");
                }
                BigDecimal valorTotal = BigDecimal.ZERO;
                for (ItemCalculoITBIOnline ic : calculoSolicitacaoItbiOnline.getItensCalculo()) {
                    valorTotal = valorTotal.add(ic.getValorInformado());
                }
                if (TipoBaseCalculo.VALOR_NEGOCIADO.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI())) {
                    if (calculoSolicitacaoItbiOnline.getTotalBaseCalculo().compareTo(valorTotal) != 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O Total da Base de Cálculo deve ser igual à soma total dos Tipos de Negociação informados.");
                    }
                } else {
                    BigDecimal valorVenal = calculoSolicitacaoItbiOnline.getValorVenal().setScale(2, RoundingMode.HALF_UP);
                    BigDecimal valorMinimo = valorVenal.multiply(buscarPercentualTotalAdquirentes()).divide(CEM, 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
                    if (valorTotal.setScale(2, RoundingMode.HALF_UP).compareTo(valorMinimo) < 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A soma dos Valores Informados não pode ser menor que o Valor Venal do Imóvel: " + Util.formataValor(valorMinimo));
                    }
                }
            }
            if (buscarPercentualTotalAdquirentes().compareTo(buscarPercentualTotalTransmitentes()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A soma do percentual dos adquirentes não deve ultrapassar a soma total do percentual dos transmitentes.");
            }
        }
        ve.lancarException();
    }

    public void removerCalculo(CalculoSolicitacaoItbiOnline calculo) {
        try {
            validarAcoesCalculo(calculo, "remover");
            selecionado.getCalculos().remove(calculo);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarTransmitente() {
        try {
            validarTransmitente();
            validarDocumentosPessoa(transmitente.getTransmitente());
            //transmitente.setCalculoITBI(calculoITBIOnline);
            Util.adicionarObjetoEmLista(calculoSolicitacaoItbiOnline.getTransmitentesCalculo(), transmitente);
            solicitacaoItbiOnlineFacade.popularDocumentosTransmitente(parametro, selecionado, transmitente);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            transmitente = new TransmitentesSolicitacaoItbiOnline();
        }
    }

    public void limparListaTransmitentes() {
        calculoSolicitacaoItbiOnline.getTransmitentesCalculo().clear();
    }

    public void removerTransmitente(TransmitentesSolicitacaoItbiOnline transmitenteCalculo) {
        calculoSolicitacaoItbiOnline.getTransmitentesCalculo().remove(transmitenteCalculo);
        solicitacaoItbiOnlineFacade.removerDocumentos(selecionado,
            " (" + transmitenteCalculo.getTransmitente().getNome() + ") ");
    }

    public String montarNomesTransmitentes(CalculoSolicitacaoItbiOnline calculoITBI) {
        StringBuilder transmitente = new StringBuilder();
        for (TransmitentesSolicitacaoItbiOnline transmitenteCalculo : calculoITBI.getTransmitentesCalculo()) {
            transmitente.append(transmitenteCalculo.getTransmitente().getNomeCpfCnpj()).append("\n");
        }
        return transmitente.toString();
    }

    public String montarPercentuaisTransmitentes(CalculoSolicitacaoItbiOnline calculoITBI) {
        StringBuilder percentualTransmitente = new StringBuilder();
        for (TransmitentesSolicitacaoItbiOnline transmitenteCalculo : calculoITBI.getTransmitentesCalculo()) {
            percentualTransmitente.append(transmitenteCalculo.getPercentual()).append("%").append("\n");
        }
        return percentualTransmitente.toString();
    }

    private void validarTransmitente() {
        ValidacaoException ve = new ValidacaoException();
        if (transmitente.getTransmitente() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Transmitente deve ser informado.");
        } else {
            for (TransmitentesSolicitacaoItbiOnline transmitenteCalculo : calculoSolicitacaoItbiOnline.getTransmitentesCalculo()) {
                if (transmitente.getTransmitente().equals(transmitenteCalculo.getTransmitente()) && !calculoSolicitacaoItbiOnline.getTransmitentesCalculo().contains(transmitente)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O transmitente já foi adicionado a lista.");
                }
            }
            if (transmitente.getPercentual() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Percentual deve ser informado.");
            } else {
                if (transmitente.getPercentual().compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Percentual deve maior que zero(0).");
                }
                if (transmitente.getPercentual().compareTo(new BigDecimal(100)) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo percentual deve ser menor ou igual a cem(100)");
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarAdquirente() {
        try {
            validarAdquirente();
            validarDocumentosPessoa(adquirente.getAdquirente());
            adquirente.setCalculoITBIOnline(calculoSolicitacaoItbiOnline);
            Util.adicionarObjetoEmLista(calculoSolicitacaoItbiOnline.getAdquirentesCalculo(), adquirente);
            calculoSolicitacaoItbiOnline.setPercentual(buscarPercentualTotalAdquirentes());
            solicitacaoItbiOnlineFacade.popularDocumentosAdquirente(parametro, selecionado, adquirente);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            adquirente = new AdquirentesSolicitacaoITBIOnline();
        }
    }

    private void validarAdquirente() {
        ValidacaoException ve = new ValidacaoException();
        if (adquirente.getAdquirente() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Adquirente deve ser informado.");
        } else {
            for (AdquirentesSolicitacaoITBIOnline adquirenteCalculo : calculoSolicitacaoItbiOnline.getAdquirentesCalculo()) {
                if (adquirente.getAdquirente().equals(adquirenteCalculo.getAdquirente()) && !calculoSolicitacaoItbiOnline.getAdquirentesCalculo().contains(adquirente)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O adquirente já foi adicionado a lista.");
                }
            }
            if (adquirente.getPercentual() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Percentual deve ser informado.");
            } else {
                if (adquirente.getPercentual().compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Percentual deve maior que zero(0).");
                }
                if (adquirente.getPercentual().compareTo(new BigDecimal(100)) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo percentual deve ser menor ou igual a cem(100)");
                }
                BigDecimal percentualAdquirente = calculoSolicitacaoItbiOnline.getAdquirentesCalculo().contains(adquirente) ? BigDecimal.ZERO : adquirente.getPercentual();
                if (percentualAdquirente.add(buscarPercentualTotalAdquirentes()).compareTo(new BigDecimal(100)) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A soma do percentual dos adquirentes deve ser menor ou igual a cem(100).");
                }
            }
        }
        ve.lancarException();
    }

    private void validarDocumentosPessoa(Pessoa pessoa) {
        if (pessoa != null) {
            ValidacaoException ve = new ValidacaoException();
            String docto = pessoa.isPessoaFisica() ? "CPF" : "CNPJ";

            if (StringUtils.isBlank(pessoa.getCpf_Cnpj()) || !Util.valida_CpfCnpj(pessoa.getCpf_Cnpj())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O " + docto + " de " + pessoa.getNome() + " não é válido!");
            } else {
                if (solicitacaoItbiOnlineFacade.hasOutraPessoaComMesmoDocto(pessoa)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O " + docto + " de " + pessoa.getNome() + " pertence também a outra pessoa!");
                }
            }
            ve.lancarException();
        }
    }

    public void editarAdquirente(AdquirentesSolicitacaoITBIOnline adquirenteCalculo) {
        adquirente = (AdquirentesSolicitacaoITBIOnline) Util.clonarObjeto(adquirenteCalculo);
    }

    public void removerAdquirente(AdquirentesSolicitacaoITBIOnline adquirenteCalculo) {
        calculoSolicitacaoItbiOnline.getAdquirentesCalculo().remove(adquirenteCalculo);
        calculoSolicitacaoItbiOnline.setPercentual(buscarPercentualTotalAdquirentes());
        solicitacaoItbiOnlineFacade.removerDocumentos(selecionado,
            " (" + adquirenteCalculo.getAdquirente().getNome() + ") ");
    }

    public void adicionarItemCalculo() {
        try {
            validarItemCalculo();
            BigDecimal residualParaSerInformado = calculoSolicitacaoItbiOnline.getTotalBaseCalculo().subtract(itemCalculo.getValorInformado());
            itemCalculo.setCalculoITBIOnline(calculoSolicitacaoItbiOnline);
            itemCalculo.setValorCalculado(itemCalculo.getValorInformado().multiply(itemCalculo.getTipoNegociacao().getAliquota()).divide(CEM, 2, RoundingMode.HALF_UP));
            itemCalculo.setPercentual((itemCalculo.getValorInformado().multiply(CEM)).divide(calculoSolicitacaoItbiOnline.getTotalBaseCalculo(), 2, RoundingMode.HALF_UP));
            Util.adicionarObjetoEmLista(calculoSolicitacaoItbiOnline.getItensCalculo(), itemCalculo);
            itemCalculo = new ItemCalculoITBIOnline();
            if (calculoSolicitacaoItbiOnline.getTotalBaseCalculo().compareTo(buscarSomaValorInformadoDasNegociacoes()) > 0 && residualParaSerInformado.compareTo(BigDecimal.ZERO) > 0) {
                itemCalculo.setValorInformado(residualParaSerInformado);
            }
            validarPercentualAndAtualizarValorTipoNegociacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarItemCalculo() {
        ValidacaoException ve = new ValidacaoException();
        if (itemCalculo.getTipoNegociacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Negociação deve ser informado.");
        }
        if (itemCalculo.getValorInformado() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Informado(R$) deve ser informado.");
        } else if (itemCalculo.getValorInformado().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("o campo Valor Informado deve ser maior que zero(0).");
        } else {
            if (isBaseCalculoValorVenal() && (itemCalculo != null && itemCalculo.getPercentual().compareTo(BigDecimal.ZERO) <= 0)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O percentual deve ser maior que zero(0)!");
            } else if (isBaseCalculoValorVenal() && (itemCalculo != null && (!calculoSolicitacaoItbiOnline.getItensCalculo().contains(itemCalculo) ? itemCalculo.getPercentual() : BigDecimal.ZERO).add(buscarPercentualTotalNegociacoes()).compareTo(CEM) > 0)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Soma dos percentuais com o percentual informado não pode ser maior que cem(100)!");
            } else {
                if (calculoSolicitacaoItbiOnline != null && itemCalculo != null) {
                    for (ItemCalculoITBIOnline itemCalculoITBI : calculoSolicitacaoItbiOnline.getItensCalculo()) {
                        if (itemCalculoITBI.getTipoNegociacao().equals(itemCalculo.getTipoNegociacao()) && !calculoSolicitacaoItbiOnline.getItensCalculo().contains(itemCalculo)) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de Negociação informado já foi adicionado!");
                            break;
                        }
                    }
                    if (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI())) {
                        validarPorcentagemPermitida();
                    }
                    if (isBaseCalculoValorVenal() && calculoSolicitacaoItbiOnline.getBaseCalculo().compareTo(BigDecimal.ZERO) == 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O imóvel informado não possui Valor Venal para base de cálculo.");
                    }
                    if (isBaseCalculoValorVenal() && itemCalculo.getValorInformado().setScale(2, RoundingMode.HALF_UP).compareTo(calculoSolicitacaoItbiOnline.getTotalBaseCalculo().setScale(2, RoundingMode.HALF_UP)) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O valor informado é maior que o total da base de cálculo.");
                    }
                    if (isBaseCalculoValorVenal() && (buscarSomaValorInformadoDasNegociacoes().add(itemCalculo.getValorInformado())).compareTo(calculoSolicitacaoItbiOnline.getTotalBaseCalculo()) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A soma dos valores informados é maior que a base de cálculo.");
                    }
                }
            }
        }
        if (calculoSolicitacaoItbiOnline != null && itemCalculo != null) {
            if (itemCalculo.getTipoNegociacao() != null && TipoBaseCalculo.VALOR_NEGOCIADO.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI()) || TipoBaseCalculo.VALOR_APURADO.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI())) {
                if (itemCalculo.getValorInformado().compareTo(calculoSolicitacaoItbiOnline.getTotalBaseCalculo()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor informado não pode exceder o total da base de cálculo.");
                }
                if (calculoSolicitacaoItbiOnline.getItensCalculo() != null && !calculoSolicitacaoItbiOnline.getItensCalculo().isEmpty()) {
                    BigDecimal totalValorInformado = buscarSomaValorInformadoDasNegociacoes();
                    if ((totalValorInformado.add(itemCalculo.getValorInformado())).compareTo(calculoSolicitacaoItbiOnline.getTotalBaseCalculo()) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A soma dos valores informados na tabela de negociação não podem exceder o valor total da base de cálculo.");
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void editarItemCalculo(ItemCalculoITBIOnline itemCalculoITBI) {
        this.itemCalculo = (ItemCalculoITBIOnline) Util.clonarObjeto(itemCalculoITBI);
    }

    public void removerItemCalculo(ItemCalculoITBIOnline itemCalculoITBI) {
        calculoSolicitacaoItbiOnline.getItensCalculo().remove(itemCalculoITBI);
        validarPercentualAndAtualizarValorTipoNegociacao();
    }

    public void validarPorcentagemPermitidaAndAtualizarValores() {
        if (calculoSolicitacaoItbiOnline != null) {
            if (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI())) {
                try {
                    validarPorcentagemPermitida();
                    atualizarValores();
                } catch (ValidacaoException ve) {
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                }
            } else {
                atualizarValores();
            }
        }
    }

    private void carregarDadosDoCadastro() {
        if (selecionado.getCadastro() != null) {
            validarPorcentagemPermitidaAndAtualizarValores();
        }
    }

    private String montarJavaScriptAlert(VerificarDebitosDoImovel tipoVerificacao) {
        setMensagemValidacao(tipoVerificacao.getMensagem());
        FacesUtil.atualizarComponente("formAlertaComDebitos");
        return "alertaComDebito.show()";
    }

    private String montarJavaScriptConfirm(VerificarDebitosDoImovel tipoVerificacao) {
        mensagemValidacao = tipoVerificacao.getMensagem();
        FacesUtil.atualizarComponente("formConfirmaComDebitos");
        return "confirmaComDebito.show()";
    }

    private void atualizarValores() {
        if (calculoSolicitacaoItbiOnline != null && calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI() != null) {
            if (selecionado.getCadastroImobiliario() != null || selecionado.getCadastroRural() != null) {
                if (TipoBaseCalculo.VALOR_VENAL.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI())) {
                    calculoSolicitacaoItbiOnline.setBaseCalculo(calculoSolicitacaoItbiOnline.getValorVenal().setScale(2, RoundingMode.HALF_UP));
                    mudouBaseCalculo();
                } else if (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI()) && calculoSolicitacaoItbiOnline.getValorReajuste() != null) {
                    calculoSolicitacaoItbiOnline.setBaseCalculo(calculoSolicitacaoItbiOnline.getValorVenal().setScale(2, RoundingMode.HALF_UP).add(calculoSolicitacaoItbiOnline.getValorVenal().multiply(calculoSolicitacaoItbiOnline.getValorReajuste().divide(CEM, 8, RoundingMode.HALF_UP))));
                    mudouBaseCalculo();
                } else {
                    calculoSolicitacaoItbiOnline.setBaseCalculo(BigDecimal.ZERO);
                    mudouBaseCalculo();
                }
            }
        }
    }

    private void validarPorcentagemPermitida() {
        if (calculoSolicitacaoItbiOnline != null && calculoSolicitacaoItbiOnline.getValorReajuste() != null) {
            ValidacaoException ve = new ValidacaoException();
            if (calculoSolicitacaoItbiOnline.getValorReajuste().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Percentual de Reajuste sobre o Valor Venal do Imóvel (%) deve ser maior que zero.");
            }

            if (calculoSolicitacaoItbiOnline.getValorReajuste().compareTo(new BigDecimal("999.99")) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Percentual de Reajuste sobre o Valor Venal do Imóvel (%) deve ser menor que 1.000%.");
            }
            ve.lancarException();
        }
    }

    public void validarPercentualAndAtualizarValorTipoNegociacao() {
        if (isBaseCalculoValorVenal() && itemCalculo != null) {
            if ((itemCalculo.getPercentual().add(buscarPercentualTotalNegociacoes())).compareTo(BigDecimal.valueOf(100)) > 0) {
                itemCalculo.setPercentual(CEM.subtract(buscarPercentualTotalNegociacoes()));
            } else {
                itemCalculo.setValorInformado(calculoSolicitacaoItbiOnline.getTotalBaseCalculo().multiply(itemCalculo.getPercentual().divide(CEM, 2, RoundingMode.HALF_UP)));
            }
        }
    }

    public void validarValorAndAtualizarPorcentualTipoNegociacao() {
        if (isBaseCalculoValorVenal() && calculoSolicitacaoItbiOnline != null && itemCalculo != null) {
            if (itemCalculo.getValorInformado() == null) {
                itemCalculo.setValorInformado(BigDecimal.ZERO);
            }
            if (itemCalculo.getPercentual() == null) {
                itemCalculo.setPercentual(BigDecimal.ZERO);
            }
            if ((itemCalculo.getValorInformado().add(buscarSomaValorInformadoDasNegociacoes())).compareTo(calculoSolicitacaoItbiOnline.getTotalBaseCalculo()) > 0) {
                FacesUtil.addOperacaoNaoPermitida("O valor total informado não pode ser maior que a Base de Cálculo!");
                itemCalculo.setValorInformado(calculoSolicitacaoItbiOnline.getTotalBaseCalculo().subtract(buscarSomaValorInformadoDasNegociacoes()));
            } else {
                itemCalculo.setPercentual((itemCalculo.getValorInformado().multiply(CEM)).divide(calculoSolicitacaoItbiOnline.getTotalBaseCalculo(), 2, RoundingMode.HALF_UP));
            }
        }
    }

    private BigDecimal buscarPercentualTotalNegociacoes() {
        BigDecimal percentualTotal = BigDecimal.ZERO;
        if (calculoSolicitacaoItbiOnline != null) {
            for (ItemCalculoITBIOnline item : calculoSolicitacaoItbiOnline.getItensCalculo()) {
                percentualTotal = percentualTotal.add(item.getPercentual());
            }
        }
        return percentualTotal;
    }

    private BigDecimal buscarPercentualTotalAdquirentes() {
        BigDecimal percentualTotalAdiquirente = BigDecimal.ZERO;
        if (calculoSolicitacaoItbiOnline != null) {
            for (AdquirentesSolicitacaoITBIOnline adquirenteCalculo : calculoSolicitacaoItbiOnline.getAdquirentesCalculo()) {
                if (this.adquirente.equals(adquirenteCalculo)) {
                    percentualTotalAdiquirente = percentualTotalAdiquirente.add(this.adquirente.getPercentual());
                } else {
                    percentualTotalAdiquirente = percentualTotalAdiquirente.add(adquirenteCalculo.getPercentual());
                }
            }
        }
        return percentualTotalAdiquirente;
    }

    private BigDecimal buscarPercentualTotalTransmitentes() {
        BigDecimal percentualTotalTransmitentes = BigDecimal.ZERO;
        if (calculoSolicitacaoItbiOnline != null) {
            for (TransmitentesSolicitacaoItbiOnline transmitenteCalculo : calculoSolicitacaoItbiOnline.getTransmitentesCalculo()) {
                if (transmitente.equals(transmitenteCalculo)) {
                    percentualTotalTransmitentes = percentualTotalTransmitentes.add(transmitente.getPercentual());
                } else {
                    percentualTotalTransmitentes = percentualTotalTransmitentes.add(transmitenteCalculo.getPercentual());
                }
            }
        }
        return percentualTotalTransmitentes;
    }

    public void mudouBaseCalculo() {
        if (calculoSolicitacaoItbiOnline.getItensCalculo() != null && !calculoSolicitacaoItbiOnline.getItensCalculo().isEmpty()) {
            itemCalculo.setValorInformado(calculoSolicitacaoItbiOnline.getTotalBaseCalculo().subtract(buscarSomaValorInformadoDasNegociacoes()));
        } else {
            itemCalculo.setValorInformado(calculoSolicitacaoItbiOnline.getTotalBaseCalculo());
        }
    }

    public BigDecimal buscarSomaValorInformadoDasNegociacoes() {
        BigDecimal resultado = BigDecimal.ZERO;
        if (calculoSolicitacaoItbiOnline != null) {
            for (ItemCalculoITBIOnline icItbi : calculoSolicitacaoItbiOnline.getItensCalculo()) {
                if (!calculoSolicitacaoItbiOnline.getItensCalculo().contains(itemCalculo)) {
                    resultado = resultado.add(icItbi.getValorInformado());
                }
            }
        }
        return resultado;
    }

    public boolean canMostrarPercentualDeReajuste() {
        return calculoSolicitacaoItbiOnline != null && calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI() != null && TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI());
    }

    public boolean isBaseCalculoValorVenal() {
        return calculoSolicitacaoItbiOnline != null && (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI()) || TipoBaseCalculo.VALOR_VENAL.equals(calculoSolicitacaoItbiOnline.getTipoBaseCalculoITBI()));
    }

    public boolean canAdicionarNegociacoes() {
        if ((calculoSolicitacaoItbiOnline != null && calculoSolicitacaoItbiOnline.getAdquirentesCalculo().isEmpty()) || (buscarPercentualTotalNegociacoes().compareTo(CEM) == 0)) {
            return true;
        }
        if (TipoITBI.IMOBILIARIO.equals(selecionado.getTipoITBI())) {
            return selecionado.getCadastroImobiliario() == null;
        }
        return selecionado.getCadastroRural() == null;
    }

    public void definirInformacoesCalculo() {
        calculoSolicitacaoItbiOnline = new CalculoSolicitacaoItbiOnline();
        if (selecionado.getCalculos().isEmpty()) {
            if (selecionado.isImobiliario()) {
                for (Propriedade propriedade : cadastroImobiliarioFacade.recuperarProprietariosVigentes(selecionado.getCadastroImobiliario())) {
                    TransmitentesSolicitacaoItbiOnline novoTransmitente = new TransmitentesSolicitacaoItbiOnline(calculoSolicitacaoItbiOnline, propriedade.getPessoa(), BigDecimal.valueOf(propriedade.getProporcao()));
                    calculoSolicitacaoItbiOnline.getTransmitentesCalculo().add(novoTransmitente);
                    solicitacaoItbiOnlineFacade.popularDocumentosTransmitente(parametro, selecionado, novoTransmitente);
                }
            } else {
                for (PropriedadeRural propriedade : cadastroRuralFacade.recuperarPropriedadesVigentes(selecionado.getCadastroRural())) {
                    TransmitentesSolicitacaoItbiOnline novoTransmitente = new TransmitentesSolicitacaoItbiOnline(calculoSolicitacaoItbiOnline, propriedade.getPessoa(), BigDecimal.valueOf(propriedade.getProporcao()));
                    calculoSolicitacaoItbiOnline.getTransmitentesCalculo().add(novoTransmitente);
                    solicitacaoItbiOnlineFacade.popularDocumentosTransmitente(parametro, selecionado, novoTransmitente);
                }
            }
        } else {
            for (PropriedadeSimulacaoITBIOnline propriedadeSimulacaoITBI : selecionado.getUltimoCalculo().getProprietariosSimulacao()) {
                calculoSolicitacaoItbiOnline.getTransmitentesCalculo().add(new TransmitentesSolicitacaoItbiOnline(calculoSolicitacaoItbiOnline, propriedadeSimulacaoITBI.getPessoa(), BigDecimal.valueOf(propriedadeSimulacaoITBI.getProporcao())));
            }
        }
        if (dadosCadastroITBI != null) {
            calculoSolicitacaoItbiOnline.setValorVenal(dadosCadastroITBI.getValorVenal());
        }
        calculoSolicitacaoItbiOnline.setOrdemCalculo(selecionado.getCalculos() != null ? (selecionado.getCalculos().size() + 1) : 1);
    }

    public Integer buscarQuantidadeMaximaDeParcelas(CalculoSolicitacaoItbiOnline calculo) {
        try {
            return parametro.getFaixaValorParcelamento(calculo.getValorCalculado()).getQtdParcela();
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    private Date buscarVencimentoPrimeiraParcela() {
        Calendar c = Calendar.getInstance();
        c.setTime(selecionado.getDataLancamento());
        c.add(Calendar.DAY_OF_MONTH, parametro.getDiaFixoVencimento());
        return c.getTime();
    }

    private void ordenarCalculos(List<CalculoSolicitacaoItbiOnline> calculos, Boolean desc) {
        Collections.sort(calculos, new Comparator<CalculoSolicitacaoItbiOnline>() {
            @Override
            public int compare(CalculoSolicitacaoItbiOnline o1, CalculoSolicitacaoItbiOnline o2) {
                return ComparisonChain.start().compare((desc ? o2 : o1).getOrdemCalculo(), (desc ? o1 : o2).getOrdemCalculo()).result();
            }
        });
    }

    public SolicitacaoItbiOnlineDocumento getSolicitacaoDocumento() {
        return solicitacaoDocumento;
    }

    public void setSolicitacaoDocumento(SolicitacaoItbiOnlineDocumento solicitacaoDocumento) {
        this.solicitacaoDocumento = solicitacaoDocumento;
    }

    public void uploadDocumento(FileUploadEvent event) {
        try {
            Arquivo arquivo = solicitacaoItbiOnlineFacade.getArquivoFacade().criarArquivo(event.getFile());
            solicitacaoDocumento.setDocumento(arquivo);
            FacesUtil.executaJavaScript("dlgUploadDocumento.hide()");
            FacesUtil.atualizarComponente("Formulario:tabDadosItbi:opDocumentos");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<SelectItem> tiposDocumento() {
        List<SelectItem> selectItems = Lists.newArrayList();
        selectItems.add(new SelectItem(null, ""));
        List<String> descricaoDocsJaAdicionados = Lists.newArrayList();
        selecionado.getDocumentos().sort((o1, o2) -> o1.getDataRegistro().compareTo(o2.getDataRegistro()));
        for (SolicitacaoItbiOnlineDocumento doc : selecionado.getDocumentos()) {
            if (!descricaoDocsJaAdicionados.contains(doc.getDescricao()) && doc.getId() != null) {
                descricaoDocsJaAdicionados.add(doc.getDescricao());
                selectItems.add(new SelectItem(doc, doc.getDescricao()));
            }
        }
        return selectItems;
    }

    public void uploadDocumentoAdicional(FileUploadEvent event) {
        try {
            if (documentoOriginalSelecionado == null) return;
            Arquivo arquivo = solicitacaoItbiOnlineFacade.getArquivoFacade().criarArquivo(event.getFile());
            SolicitacaoItbiOnlineDocumento docAdicional = new SolicitacaoItbiOnlineDocumento();
            docAdicional.setSolicitacaoItbiOnline(selecionado);
            docAdicional.setParametrosITBIDocumento(documentoOriginalSelecionado.getParametrosITBIDocumento());
            docAdicional.setDescricao(documentoOriginalSelecionado.getDescricao());
            docAdicional.setDocumento(arquivo);
            selecionado.getDocumentos().add(docAdicional);
            FacesUtil.executaJavaScript("dlgUploadDocumentoAdicional.hide()");
            FacesUtil.atualizarComponente("Formulario:tabDadosItbi:opDocumentos");
            documentoOriginalSelecionado = null;
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void selecionarDocumento(SolicitacaoItbiOnlineDocumento solicitacaoDocumento) {
        this.solicitacaoDocumento = solicitacaoDocumento;
    }

    public void substituirDocumento(SolicitacaoItbiOnlineDocumento solicitacaoDocumento) {
        selecionarDocumento(solicitacaoDocumento);
        this.solicitacaoDocumento.setDocumento(null);
        FacesUtil.executaJavaScript("dlgUploadDocumento.show()");
        FacesUtil.atualizarComponente("formUploadDocumento");
    }

    public void removerDocumento(SolicitacaoItbiOnlineDocumento solicitacaoDocumento) {
        if (solicitacaoDocumento.getId() != null) return;
        selecionado.getDocumentos().remove(solicitacaoDocumento);
        FacesUtil.atualizarComponente("formUploadDocumento");
    }

    public void aprovarSolicitacao() {
        try {
            solicitacaoItbiOnlineFacade.aprovarSolicitacao(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Solicitação de ITBI aprovada com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void iniciarRejeicaoSolicitacao() {
        observacao = "";
    }

    public void rejeitarSolicitacao() {
        try {
            solicitacaoItbiOnlineFacade.rejeitarSolicitacao(selecionado, observacao);

            AsyncExecutor.getInstance().execute(new AssistenteBarraProgresso(solicitacaoItbiOnlineFacade.getSistemaFacade()
                    .getUsuarioCorrente(), "Envio de e-mail de rejeição de solicitação de itbi online", 0),
                () -> {
                    solicitacaoItbiOnlineFacade.enviarEmailRejeicaoSolicitacao(selecionado);
                    return null;
                });
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Solicitação de ITBI rejeitada com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void iniciarDesignacaoSolicitacao() {
        auditorFiscal = null;
    }

    public void designarSolicitacao() {
        try {
            solicitacaoItbiOnlineFacade.designarSolicitacao(selecionado, auditorFiscal);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Solicitação de ITBI designada com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public boolean podeAvaliar() {
        return selecionado.isDesignada() && (usuarioLogadoDesignado() || usuarioLogadoChefeDepartamentoItbi());
    }

    public boolean podeHomologar() {
        return selecionado.isDeferida() && usuarioLogadoChefeDepartamentoItbi();
    }

    public boolean usuarioLogadoDesignado() {
        UsuarioSistema usuarioSistema = solicitacaoItbiOnlineFacade.getSistemaFacade().getUsuarioCorrente();
        TipoUsuarioTribUsuario tipoUsuario = solicitacaoItbiOnlineFacade.getUsuarioSistemaFacade().listaTipoUsuarioVigenteDoUsuarioPorTipo(usuarioSistema, TipoUsuarioTributario.FISCAL_TRIBUTARIO);
        if (tipoUsuario != null && tipoUsuario.getSupervisor()) return true;
        if (auditorFiscal == null) return false;
        return solicitacaoItbiOnlineFacade.getSistemaFacade().getUsuarioCorrente().getId().equals(auditorFiscal.getId());
    }

    public boolean usuarioLogadoChefeDepartamentoItbi() {
        UsuarioSistema usuarioSistema = solicitacaoItbiOnlineFacade.getSistemaFacade().getUsuarioCorrente();
        parametro = solicitacaoItbiOnlineFacade.recuperarParametroITBIVigente(selecionado.getExercicio(), selecionado.getTipoITBI());
        for (ParametrosFuncionarios funcionario : parametro.getListaFuncionarios()) {
            if ((TipoFuncaoParametrosITBI.DIRETOR_CHEFE_DEPARTAMENTO_TRIBUTO.equals(funcionario.getFuncaoParametrosITBI().getFuncao())
                || TipoFuncaoParametrosITBI.RESPONSAVEL_COMISSAO_AVALIADORA.equals(funcionario.getFuncaoParametrosITBI().getFuncao()))
                && usuarioSistema.getPessoaFisica().getId().equals(funcionario.getPessoa().getId())) {
                return true;
            }
        }
        return false;
    }

    public void iniciarAvaliacaoSolicitacao() {
        dadosCorretos = Boolean.TRUE;
        planilhaAvaliacao = null;
        valorAvaliado = selecionado.getCalculos().get(0).getBaseCalculo();
    }

    public void handlePlanilhaAvaliacao(FileUploadEvent event) {
        try {
            planilhaAvaliacao = solicitacaoItbiOnlineFacade.getArquivoFacade().criarArquivo(event.getFile());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void removerPlanilhaAvaliacao() {
        planilhaAvaliacao = null;
    }

    public void salvarAvaliarSolicitacao() {
        try {
            UsuarioSistema usuarioCorrente = solicitacaoItbiOnlineFacade.getSistemaFacade().getUsuarioCorrente();
            if (!dadosCorretos) {
                solicitacaoItbiOnlineFacade.salvarAvaliacaoSolicitacao(selecionado, usuarioCorrente.getNome(),
                    planilhaAvaliacao, valorAvaliado, observacao, SituacaoSolicitacaoITBI.INDEFERIDA);
                AsyncExecutor.getInstance().execute(new AssistenteBarraProgresso(solicitacaoItbiOnlineFacade.getSistemaFacade()
                        .getUsuarioCorrente(), "Envio de e-mail de avaliação do fiscal sobre a solicitação de itbi online", 0),
                    () -> {
                        solicitacaoItbiOnlineFacade.enviarEmailAvaliacaoSolicitacao(selecionado);
                        return null;
                    });
            } else {
                solicitacaoItbiOnlineFacade.salvarAvaliacaoSolicitacao(selecionado, usuarioCorrente.getNome(),
                    planilhaAvaliacao, valorAvaliado, observacao, SituacaoSolicitacaoITBI.DEFERIDA);
                FacesUtil.executaJavaScript("dlgAvaliacao.hide()");
            }
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Solicitação de ITBI avaliada com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void homologarSolicitacao() {
        try {
            UsuarioSistema usuarioCorrente = solicitacaoItbiOnlineFacade.getSistemaFacade().getUsuarioCorrente();
            assistente = new AssistenteBarraProgresso();
            assistente.setUsuarioSistema(solicitacaoItbiOnlineFacade.getSistemaFacade().getUsuarioCorrente());
            assistente.setSelecionado(selecionado);
            assistente.setDescricaoProcesso("Homologando solicitação de ITBI [" + selecionado.toString() + "]");
            futureHomologacao = AsyncExecutor.getInstance().execute(assistente,
                () -> {
                    try {
                        return solicitacaoItbiOnlineFacade.homologarSolicitacao(assistente,
                            usuarioCorrente.getNome());
                    } catch (Exception e) {
                        logger.error("Erro ao homologar a solicitação de ITBI.", e);
                    }
                    return null;
                });
            FacesUtil.executaJavaScript("iniciarHomologacao()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharHomologacao() {
        if (futureHomologacao != null && futureHomologacao.isDone() || futureHomologacao.isCancelled()) {
            FacesUtil.executaJavaScript("pararTimer()");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Solicitação de ITBI homologada com sucesso!");
        }
    }

    public void mudouDadosCorretos() {
        valorAvaliado = selecionado.getCalculos().get(0).getBaseCalculo();
    }

    public void verCalculoITBI() {
        ProcessoCalculoITBI processoCalculoITBI = solicitacaoItbiOnlineFacade.buscarProcessoCalculoITBIDaSolicitacao(selecionado);
        if (processoCalculoITBI != null) {
            FacesUtil.redirecionamentoInterno("/calculo-de-itbi/ver/" + processoCalculoITBI.getId() + "/");
        } else {
            FacesUtil.addOperacaoNaoRealizada("Cálculo de I.T.B.I não encontrado");
        }
    }

    public void mudouProcurador() {
        iniciarDocumentosProcurador();
    }

    private void iniciarDocumentosProcurador() {
        solicitacaoItbiOnlineFacade.removerDocumentos(selecionado, "(Procurador)");
        if (selecionado.getProcurador()) {
            solicitacaoItbiOnlineFacade.popularDocumentosProcurador(parametro, selecionado);
        }
    }

    public String nomeUsuarioCriacaoSolicitacao() {
        if (selecionado.getUsuarioSistema() != null) {
            return selecionado.getUsuarioSistema().getNome();
        } else if (selecionado.getUsuarioWeb() != null) {
            return selecionado.getUsuarioWeb().getPessoa().getNome();
        } else {
            return "";
        }
    }

    public void alterouIsento() {
        if (!calculoSolicitacaoItbiOnline.getIsentoSub()) {
            calculoSolicitacaoItbiOnline.setTipoIsencaoITBI(null);
        }
    }

    public Map<String, Object> dadosCadastro() {
        Map<String, Object> map = Maps.newLinkedHashMap();
        if (dadosCadastroITBI == null) return map;
        if (isItbiImobiliario()) {
            map.put("Setor:", dadosCadastroITBI.getCodigoSetor());
            map.put("Quadra:", dadosCadastroITBI.getCodigoQuadra());
            map.put("Lote:", dadosCadastroITBI.getCodigoLote());
            map.put("Loteamento:", dadosCadastroITBI.getDescricaoLote());
            map.put("Logradouro:", dadosCadastroITBI.getTipoLogradouro() + " " + dadosCadastroITBI.getLogradouro());
            map.put("Número:", dadosCadastroITBI.getNumero());
            map.put("Complemento (Lote):", dadosCadastroITBI.getComplementoLote());
            map.put("Complemento (BCI):", dadosCadastroITBI.getComplemento());
            map.put("Bairro:", dadosCadastroITBI.getBairro());
            map.put("Área do Terreno (M²):", Util.formatarValor(dadosCadastroITBI.getAreaTerreno(), null, false));
            map.put("Área Construída (M²):", Util.formatarValor(dadosCadastroITBI.getAreaConstruida(), null, false));

        } else {
            map.put("Código da Propriedade:", dadosCadastroITBI.getCodigoPropriedade());
            map.put("Nome da Propriedade:", dadosCadastroITBI.getNomePropriedade());
            map.put("Logradouro:", dadosCadastroITBI.getLogradouro());
            if (selecionado.getCadastroRural() != null) {
                map.put("Área do Terreno (" + selecionado.getCadastroRural().getTipoAreaRural().getDescricao() + "):", dadosCadastroITBI.getAreaTerreno());
            }
        }
        map.put("Valor Venal:", Util.formatarValor(dadosCadastroITBI.getValorVenal()));
        return map;
    }

    public boolean mostrarTabelaAnexosTramites(TramiteSolicitacaoItbiOnline tramite) {
        return SituacaoSolicitacaoITBI.DESIGNADA.equals(selecionado.getSituacao()) && !tramite.getDocumentos().isEmpty();
    }

    public StreamedContent downloadArquivo(Arquivo arquivo) {
        return solicitacaoItbiOnlineFacade.getArquivoFacade().montarArquivoParaDownloadPorArquivo(arquivo);
    }

    public String extencoesPermitidasDocOriginal() {
        if (documentoOriginalSelecionado == null) return "";
        return documentoOriginalSelecionado.getParametrosITBIDocumento().getExtensoesPermitidas();
    }
}
