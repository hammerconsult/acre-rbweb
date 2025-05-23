package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DAMResultadoParcela;
import br.com.webpublico.entidadesauxiliares.SimulacaoParcelamento;
import br.com.webpublico.entidadesauxiliares.VODadosCadastroITBI;
import br.com.webpublico.entidadesauxiliares.VOProprietarioITBI;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLancamentoItbi", pattern = "/calculo-de-itbi/novo/", viewId = "/faces/tributario/itbi/calculoitbi/edita.xhtml"),
    @URLMapping(id = "editaLancamentoItbi", pattern = "/calculo-de-itbi/editar/#{calculoITBIControlador.id}/", viewId = "/faces/tributario/itbi/calculoitbi/edita.xhtml"),
    @URLMapping(id = "verLancamentoItbi", pattern = "/calculo-de-itbi/ver/#{calculoITBIControlador.id}/", viewId = "/faces/tributario/itbi/calculoitbi/visualizar.xhtml"),
    @URLMapping(id = "listarLancamentoItbi", pattern = "/calculo-de-itbi/listar/", viewId = "/faces/tributario/itbi/calculoitbi/lista.xhtml")})
public class CalculoITBIControlador extends PrettyControlador<ProcessoCalculoITBI> implements CRUD {

    @EJB
    private CalculoITBIFacade calculoITBIFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    private Map<Long, List<VOProprietarioITBI>> mapaProprietarios;
    private Map<CalculoITBI, List<SimulacaoParcelamento>> mapaSimulacaoPorCalculo;
    private Map<Integer, String> mapaIconeBtnParcela;

    private VODadosCadastroITBI dadosCadastroITBI;
    private CalculoITBI calculoITBI;
    private CalculoITBI calculoSelecionado;
    private TransmitentesCalculoITBI transmitente;
    private AdquirentesCalculoITBI adquirente;
    private ItemCalculoITBI itemCalculo;
    private ParametrosITBI parametro;
    private LaudoAvaliacaoITBI laudoAvaliacaoITBI;
    private RetificacaoCalculoITBI retificacao;
    private List<ParametrosFuncionarios> responsaveisComissaoAvaliadora;
    private List<ParametrosFuncionarios> diretoresChefeDepartamentoTributos;
    private List<DAMResultadoParcela> parcelasCalculoITBI;
    private List<PropriedadeSimulacaoITBI> transmitentesSimulacao;
    private List<PropriedadeSimulacaoITBI> adquirentesSimulacao;
    private boolean canMostrarInfoCadastros;
    private boolean canMostrarParcelasDoCalculo;
    private boolean canAssinar;
    private Boolean gerouCalculo;
    private String mensagemValidacao;
    private String emails;
    private String mensagemEmail;
    private Integer indexParcelas;
    private boolean isento;
    private boolean isSegundaVia;
    private TipoIsencaoITBI tipoIsencaoITBI;
    private AssistenteBarraProgresso assistente;
    private CompletableFuture<AssistenteBarraProgresso> futureLancamentoItbi;

    public CalculoITBIControlador() {
        super(ProcessoCalculoITBI.class);
    }

    @Override
    public AbstractFacade<ProcessoCalculoITBI> getFacede() {
        return calculoITBIFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/calculo-de-itbi/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoLancamentoItbi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarDadosNovo();
    }

    @URLAction(mappingId = "editaLancamentoItbi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verLancamentoItbi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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

    public boolean isCanMostrarParcelasDoCalculo() {
        return canMostrarParcelasDoCalculo;
    }

    public void setCanMostrarParcelasDoCalculo(boolean canMostrarParcelasDoCalculo) {
        this.canMostrarParcelasDoCalculo = canMostrarParcelasDoCalculo;
    }

    public List<PropriedadeSimulacaoITBI> getTransmitentesSimulacao() {
        if (transmitentesSimulacao == null) {
            transmitentesSimulacao = Lists.newArrayList();
        }
        return transmitentesSimulacao;
    }

    public void setTransmitentesSimulacao(List<PropriedadeSimulacaoITBI> transmitentesSimulacao) {
        this.transmitentesSimulacao = transmitentesSimulacao;
    }

    public List<PropriedadeSimulacaoITBI> getAdquirentesSimulacao() {
        if (adquirentesSimulacao == null) {
            adquirentesSimulacao = Lists.newArrayList();
        }
        return adquirentesSimulacao;
    }

    public void setAdquirentesSimulacao(List<PropriedadeSimulacaoITBI> adquirentesSimulacao) {
        this.adquirentesSimulacao = adquirentesSimulacao;
    }

    public VODadosCadastroITBI getDadosCadastroITBI() {
        return dadosCadastroITBI;
    }

    public void setDadosCadastroITBI(VODadosCadastroITBI dadosCadastroITBI) {
        this.dadosCadastroITBI = dadosCadastroITBI;
    }

    public CalculoITBI getCalculoITBI() {
        return calculoITBI;
    }

    public void setCalculoITBI(CalculoITBI calculoITBI) {
        this.calculoITBI = calculoITBI;
    }

    public CalculoITBI getCalculoSelecionado() {
        return calculoSelecionado;
    }

    public void setCalculoSelecionado(CalculoITBI calculoSelecionado) {
        this.calculoSelecionado = calculoSelecionado;
    }

    public TransmitentesCalculoITBI getTransmitente() {
        return transmitente;
    }

    public void setTransmitente(TransmitentesCalculoITBI transmitente) {
        this.transmitente = transmitente;
    }

    public ItemCalculoITBI getItemCalculo() {
        return itemCalculo;
    }

    public void setItemCalculo(ItemCalculoITBI itemCalculo) {
        this.itemCalculo = itemCalculo;
    }

    public AdquirentesCalculoITBI getAdquirente() {
        return adquirente;
    }

    public void setAdquirente(AdquirentesCalculoITBI adquirente) {
        this.adquirente = adquirente;
    }

    public Boolean getGerouCalculo() {
        return gerouCalculo != null ? gerouCalculo : Boolean.FALSE;
    }

    public void setGerouCalculo(Boolean gerouCalculo) {
        this.gerouCalculo = gerouCalculo;
    }

    public String getMensagemValidacao() {
        return mensagemValidacao;
    }

    public void setMensagemValidacao(String mensagemValidacao) {
        this.mensagemValidacao = mensagemValidacao;
    }

    public Map<CalculoITBI, List<SimulacaoParcelamento>> getMapaSimulacaoPorCalculo() {
        return mapaSimulacaoPorCalculo;
    }

    public void setMapaSimulacaoPorCalculo(Map<CalculoITBI, List<SimulacaoParcelamento>> mapaSimulacaoPorCalculo) {
        this.mapaSimulacaoPorCalculo = mapaSimulacaoPorCalculo;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public LaudoAvaliacaoITBI getLaudoAvaliacaoITBI() {
        return laudoAvaliacaoITBI;
    }

    public void setLaudoAvaliacaoITBI(LaudoAvaliacaoITBI laudoAvaliacaoITBI) {
        this.laudoAvaliacaoITBI = laudoAvaliacaoITBI;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getMensagemEmail() {
        return mensagemEmail;
    }

    public void setMensagemEmail(String mensagemEmail) {
        this.mensagemEmail = mensagemEmail;
    }

    public List<DAMResultadoParcela> getParcelasCalculoITBI() {
        return parcelasCalculoITBI;
    }

    public void setParcelasCalculoITBI(List<DAMResultadoParcela> parcelasCalculoITBI) {
        this.parcelasCalculoITBI = parcelasCalculoITBI;
    }

    public BigDecimal getValorDAM() {
        BigDecimal valorDAM = BigDecimal.ZERO;
        if (!parcelasCalculoITBI.isEmpty()) {
            for (DAMResultadoParcela damResultadoParcela : parcelasCalculoITBI) {
                if (damResultadoParcela.getResultadoParcela().isEmAberto())
                    valorDAM = valorDAM.add(damResultadoParcela.getResultadoParcela().getValorTotal());
            }
        }
        return valorDAM;
    }

    public Integer getIndexParcelas() {
        return indexParcelas;
    }

    public void setIndexParcelas(Integer indexParcelas) {
        this.indexParcelas = indexParcelas;
    }

    public RetificacaoCalculoITBI getRetificacao() {
        return retificacao;
    }

    public boolean isIsento() {
        return isento;
    }

    public void setIsento(boolean isento) {
        this.isento = isento;
    }

    public TipoIsencaoITBI getTipoIsencaoITBI() {
        return tipoIsencaoITBI;
    }

    public void setTipoIsencaoITBI(TipoIsencaoITBI tipoIsencaoITBI) {
        this.tipoIsencaoITBI = tipoIsencaoITBI;
    }

    public void setRetificacao(RetificacaoCalculoITBI retificacao) {
        this.retificacao = retificacao;
    }

    public Integer buscarQuantidadeMaximaDeParcelas(CalculoITBI calculo) {
        try {
            return parametro.getFaixaValorParcelamento(calculo.getValorReal()).getQtdParcela();
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            selecionado.setCodigoVerificacao(calculoITBIFacade.gerarAssinaturaDigital(selecionado));
            selecionado.setCodigo(calculoITBIFacade.buscarSequeciaCodigoITBI(selecionado));
            selecionado = calculoITBIFacade.salvarRetornando(selecionado);
            redirecionarParaVisualiza("Lançamento de ITBI salvo com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar cadeia dominial de ITBI. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Lançamento de Cadeia Dominial de ITBI. Detalhes: " + e.getMessage());
        }
    }


    private void redirecionarParaVisualiza(String msg) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        FacesUtil.addOperacaoRealizada(msg);
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (selecionado.getTipoITBI() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de ITBI deve ser informado.");
        } else {
            String tipoCadastro = "";
            if (TipoITBI.RURAL.equals(selecionado.getTipoITBI())) {
                tipoCadastro = "Rural";
            } else {
                tipoCadastro = "Imobiliário";
            }
            if (selecionado.getCadastro() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro " + tipoCadastro + " deve ser informado.");
            }
        }
        if (selecionado.getCalculos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione pelo menos uma Transmissão ao processo.");
        }
        validarSomaTotalTransmitentesUltimoCalculo(ve);
        validarBloqueioTransferenciaProprietario();
        ve.lancarException();
    }

    private void validarSomaTotalTransmitentesUltimoCalculo(ValidacaoException ve) {
        CalculoITBI ultimoCalculo = selecionado.getUltimoCalculo();
        if (ultimoCalculo == null) return;
        double proporcaoUltimaTransmissao = 0.0;
        for (PropriedadeSimulacaoITBI propriedadeSimulacaoITBI : ultimoCalculo.getProprietariosSimulacao()) {
            proporcaoUltimaTransmissao = proporcaoUltimaTransmissao + propriedadeSimulacaoITBI.getProporcao();
        }
        if (proporcaoUltimaTransmissao > 100 || proporcaoUltimaTransmissao < 100) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A soma total dos Transmitentes da ultima transmissão devem ser igual à 100%. Visualize a ultima transmissão para ajustar manualmente.");
        }
    }

    public void prepararProcessamentoCadeiaDominial() {
        try {
            int countCalculosIsentos = 0;
            for (CalculoITBI calc : selecionado.getCalculos()) {
                if (calc.getIsento()) {
                    countCalculosIsentos++;
                }
            }
            if (selecionado.getCalculos().size() != countCalculosIsentos) {
                simularParcelas();
                definirIconeBtnParcela();
                FacesUtil.executaJavaScript("wParcelas.show()");
                FacesUtil.atualizarComponente("formDialogParcelas");
            } else {
                FacesUtil.executaJavaScript("confirmaIsento.show()");
                FacesUtil.atualizarComponente("formIsencao");
            }
        } catch (Exception e) {
            logger.error("Erro ao simular parcelas da cadeia dominial de ITBI. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao simular parcelas da cadeia dominial de ITBI. Detalhes: " + e.getMessage());
        }
    }

    public void simularParcelas() {
        mapaSimulacaoPorCalculo = Maps.newHashMap();

        if (selecionado != null && selecionado.getCalculos() != null && !selecionado.getCalculos().isEmpty()) {
            for (CalculoITBI calculo : selecionado.getCalculos()) {
                if (calculo.getIsento()) {
                    mapaSimulacaoPorCalculo.put(calculo, Lists.newArrayList());
                } else {
                    simularParcelasPorCalculo(calculo);
                }
            }
        }
    }

    public void simularParcelasPorCalculo(CalculoITBI calculo) {
        if (calculo.getNumeroParcelas().compareTo(buscarQuantidadeMaximaDeParcelas(calculo)) > 0 || calculo.getNumeroParcelas().compareTo(0) <= 0) {
            FacesUtil.addOperacaoNaoRealizada("O número de parcelas deve ser maior que zero(0) e menor que " + buscarQuantidadeMaximaDeParcelas(calculo) + ".");
            calculo.setNumeroParcelas(buscarQuantidadeMaximaDeParcelas(calculo));
        } else {
            mapaSimulacaoPorCalculo.remove(calculo);
            Calendar vencimento = Calendar.getInstance();
            vencimento.setTime(buscarVencimentoPrimeiraParcela());

            BigDecimal valorParcela = calculo.getValorReal().divide(new BigDecimal(calculo.getNumeroParcelas()), 2, RoundingMode.DOWN);
            BigDecimal valorPrimeiraParcela = calculo.getValorReal().subtract(valorParcela.multiply(new BigDecimal(calculo.getNumeroParcelas() - 1)));

            for (int i = 1; i <= calculo.getNumeroParcelas(); i++) {
                SimulacaoParcelamento simulacao = new SimulacaoParcelamento();

                simulacao.setParcela("Parcela " + i);
                if (i == 1) {
                    simulacao.setValor(valorPrimeiraParcela);
                } else {
                    simulacao.setValor(valorParcela);
                }
                simulacao.setVencimento(vencimento.getTime());

                vencimento.setTime(DataUtil.ajustarData(vencimento.getTime(), Calendar.MONTH, 1, calculoITBIFacade.getFeriadoFacade()));

                if (!mapaSimulacaoPorCalculo.containsKey(calculo)) {
                    mapaSimulacaoPorCalculo.put(calculo, Lists.newArrayList(simulacao));
                } else {
                    mapaSimulacaoPorCalculo.get(calculo).add(simulacao);
                }
            }
        }
    }

    public List<CalculoITBI> buscarCalculosDoMapa() {
        if (mapaSimulacaoPorCalculo != null) {
            return Lists.newArrayList(mapaSimulacaoPorCalculo.keySet());
        }
        return Lists.newArrayList();
    }

    public List<SimulacaoParcelamento> buscarSimulacoesDoCalculo(CalculoITBI calculo) {
        if (calculo != null && mapaSimulacaoPorCalculo.containsKey(calculo)) {
            return mapaSimulacaoPorCalculo.get(calculo);
        }
        return Lists.newArrayList();
    }

    private Date buscarVencimentoPrimeiraParcela() {
        Calendar c = Calendar.getInstance();
        c.setTime(selecionado.getDataLancamento());
        c.add(Calendar.DAY_OF_MONTH, parametro.getDiaFixoVencimento());
        return c.getTime();
    }

    public void processar() {
        try {
            validarProcessar();
            assistente = new AssistenteBarraProgresso();
            assistente.setDescricaoProcesso("Efetivação do Lançamento de ITBI " + selecionado.getCodigo());
            assistente.setExercicio(selecionado.getExercicio());
            assistente.setUsuarioSistema(calculoITBIFacade.buscarUsuarioSistema());
            assistente.setDescricaoProcesso("Processando Calculo de ITBI [" + selecionado.toString() + "]");
            assistente.setExecutando(true);
            atribuirAdquirentesAoCalculo();
            assistente.setSelecionado(selecionado);
            futureLancamentoItbi = AsyncExecutor.getInstance().execute(assistente,
                () -> calculoITBIFacade.processar(assistente));
            FacesUtil.executaJavaScript("pollProcessar.start()");
            FacesUtil.executaJavaScript("closeDialog(wParcelas)");
            FacesUtil.executaJavaScript("openDialog(dlgProcessar)");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao processar Cadeia Dominial de ITBI. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao processar Cadeia Dominial de ITBI. Detalhes: " + e.getMessage());
        }
    }

    private void atribuirAdquirentesAoCalculo() {
        for (CalculoITBI calculo : selecionado.getCalculos()) {
            for (AdquirentesCalculoITBI adquirente : calculo.getAdquirentesCalculo()) {
                calculo.getPessoas().add(new CalculoPessoa(calculo, adquirente.getAdquirente()));
                calculo.defineReferencia();
            }
        }
    }

    public void acompanharProcesso() {
        if (futureLancamentoItbi != null && futureLancamentoItbi.isDone()) {
            FacesUtil.executaJavaScript("pollProcessar.stop()");
            FacesUtil.executaJavaScript("closeDialog(dlgProcessar)");
            FacesUtil.executaJavaScript("rcFinalizarProcesso()");
        }
        if (futureLancamentoItbi == null || futureLancamentoItbi.isCancelled()) {
            FacesUtil.executaJavaScript("pollProcessar.stop()");
            FacesUtil.executaJavaScript("closeDialog(dlgProcessar");
        }
    }

    public void finalizarProcesso() {
        try {
            assistente = futureLancamentoItbi.get();
            if (assistente != null) {
                assistente.setExecutando(false);
                if (assistente.getMensagensValidacaoFacesUtil().isEmpty()) {
                    selecionado = (ProcessoCalculoITBI) assistente.getSelecionado();
                    redirecionarParaVisualiza("Cadeia Dominial de ITBI Processada com sucesso!");
                } else {
                    FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
                    ValidacaoException ve = new ValidacaoException();
                    for (FacesMessage message : assistente.getMensagensValidacaoFacesUtil()) {
                        ve.adicionarMensagemDeOperacaoNaoRealizada(message.getDetail());
                    }
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } finally {
            futureLancamentoItbi = null;
            if (assistente != null) {
                assistente.setExecutando(false);
            }
        }
    }

    private void validarProcessar() {
        ValidacaoException ve = new ValidacaoException();

        for (CalculoITBI calculo : selecionado.getCalculos()) {
            if (!calculo.getIsento()) {
                Integer maxParcelas = buscarQuantidadeMaximaDeParcelas(calculo);
                if (calculo.getNumeroParcelas() == null || (calculo.getNumeroParcelas().compareTo(0) <= 0 || calculo.getNumeroParcelas().compareTo(maxParcelas) > 0)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O número de parcelas para o cálculo do Transmitente: " + calculo.getTransmitentesCalculo().get(0) + " é inválido.");
                }
            }
        }
        ve.lancarException();
    }

    private void inicializarDadosNovo() {
        this.calculoITBI = new CalculoITBI();
        this.calculoITBI.setPercentual(CEM);
        this.transmitente = new TransmitentesCalculoITBI();
        this.adquirente = new AdquirentesCalculoITBI();
        this.itemCalculo = new ItemCalculoITBI();
        this.mapaProprietarios = Maps.newHashMap();
        this.canMostrarInfoCadastros = false;
        selecionado.setExercicio(calculoITBIFacade.buscarExercicioCorrente());
        selecionado.setDataLancamento(calculoITBIFacade.buscarDataOperacao());
        selecionado.setUsuarioSistema(calculoITBIFacade.buscarUsuarioSistema());
        selecionado.setTipoITBI(TipoITBI.IMOBILIARIO);
        selecionado.setDescricao("ITBI / " + selecionado.getExercicio().getAno());
        selecionado.setSituacao(SituacaoITBI.ABERTO);
        recuperarParametro();
    }

    private void inicializarDadosVer() {
        this.mapaSimulacaoPorCalculo = Maps.newHashMap();
        this.mapaProprietarios = Maps.newHashMap();
        this.parametro = calculoITBIFacade.recuperarParametroITBIVigente(selecionado.getExercicio(), selecionado.getTipoITBI());
        this.canMostrarParcelasDoCalculo = false;
        this.mapaIconeBtnParcela = Maps.newHashMap();
        this.retificacao = new RetificacaoCalculoITBI();
        recuperarParametro();
        buscarParcelasDoITBI();
        ordenarCalculos(selecionado.getCalculos(), false);
        buscarDadosCadastroITBI();
        laudoAvaliacaoITBI = calculoITBIFacade.recuperarLaudo(selecionado);
    }

    public void recuperarParametro() {
        if (selecionado.getExercicio() != null && selecionado.getTipoITBI() != null) {
            parametro = calculoITBIFacade.recuperarParametroITBIVigente(selecionado.getExercicio(), selecionado.getTipoITBI());
            if (parametro != null) {
                selecionado.setDivida(parametro.getDivida());
                if (calculoITBI != null) {
                    calculoITBI.setTipoBaseCalculoITBI(parametro.getTipoBaseCalculo() != null ? parametro.getTipoBaseCalculo() : TipoBaseCalculo.VALOR_APURADO);
                }
                responsaveisComissaoAvaliadora = calculoITBIFacade.buscarFuncionariosPorFuncao(parametro, TipoFuncaoParametrosITBI.RESPONSAVEL_COMISSAO_AVALIADORA);
                diretoresChefeDepartamentoTributos = calculoITBIFacade.buscarFuncionariosPorFuncao(parametro, TipoFuncaoParametrosITBI.DIRETOR_CHEFE_DEPARTAMENTO_TRIBUTO);
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não existe parâmetro de ITBI " + selecionado.getTipoITBI().getDescricao() + " para o exercício de " + selecionado.getExercicio().getAno() + ".");
            }
        }
    }

    public List<SelectItem> buscarTiposITBI() {
        mapaProprietarios = Maps.newHashMap();
        selecionado.setCadastroImobiliario(null);
        selecionado.setCadastroRural(null);
        return Util.getListSelectItemSemCampoVazio(TipoITBI.values());
    }

    public List<SelectItem> buscarTiposBaseCalculo() {
        return Util.getListSelectItemSemCampoVazio(TipoBaseCalculo.values());
    }

    public List<SelectItem> buscarTiposNegociacao() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoNegociacao t : calculoITBIFacade.buscarTiposNegociacaoAtivas()) {
            lista.add(new SelectItem(t, t.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> buscarResponsaveisComissaoAvaliadora() {
        List<SelectItem> items = Lists.newArrayList();
        items.add(new SelectItem(null, ""));
        if (responsaveisComissaoAvaliadora != null) {
            for (ParametrosFuncionarios diretor : responsaveisComissaoAvaliadora) {
                items.add(new SelectItem(diretor, diretor.toString()));
            }
        }
        return items;
    }

    public List<SelectItem> buscarDiretoresDepartamentoTributos() {
        List<SelectItem> items = Lists.newArrayList();
        items.add(new SelectItem(null, ""));
        if (diretoresChefeDepartamentoTributos != null) {
            for (ParametrosFuncionarios diretor : diretoresChefeDepartamentoTributos) {
                items.add(new SelectItem(diretor, diretor.toString()));
            }
        }
        return items;
    }

    public List<PessoaFisica> completarAuditorFiscal(String parte) {
        return calculoITBIFacade.buscarAuditorFiscal(parte);
    }

    public List<TipoIsencaoITBI> buscarTiposDeIsencaoITBI(String parte) {
        return calculoITBIFacade.buscarTiposDeIsencaoITBI(parte);
    }

    public List<SituacaoCadastralPessoa> buscarSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public List<? extends Cadastro> buscarCadastroPorTipo(String parte) {
        return calculoITBIFacade.buscarCadastroPorTipo(parte, isItbiImobiliario());
    }

    public Boolean isItbiImobiliario() {
        return selecionado != null && TipoITBI.IMOBILIARIO.equals(selecionado.getTipoITBI());
    }

    public boolean canRenderizarDadosCadastro() {
        return selecionado != null && selecionado.getTipoITBI() != null && (selecionado.getCadastro() != null);
    }

    public void colocarSelecionadoNaSecao() {
        Web.poeNaSessao(selecionado);
        Web.poeTodosFieldsNaSessao(this);
    }

    public List<VOProprietarioITBI> buscarProprietarios() {
        if (selecionado != null && selecionado.getCadastro() != null) {
            if (!mapaProprietarios.containsKey(selecionado.getIdCadastro())) {
                List<VOProprietarioITBI> proprietarios = calculoITBIFacade.buscarProprietarios(selecionado.isImobiliario(), selecionado.getIdCadastro());
                mapaProprietarios.put(selecionado.getIdCadastro(), proprietarios);
            }
            return mapaProprietarios.get(selecionado.getIdCadastro());
        }
        return Lists.newArrayList();
    }

    public void mostrarInfoCadastros() {
        canMostrarInfoCadastros = !canMostrarInfoCadastros;
    }

    public void mostrarParcelasDoCalculo(CalculoITBI calculo, Integer index) {
        calculoSelecionado = calculo;
        canMostrarParcelasDoCalculo = !canMostrarParcelasDoCalculo;
        indexParcelas = indexParcelas == null ? index : null;

        for (Map.Entry<Integer, String> entry : mapaIconeBtnParcela.entrySet()) {
            if (entry.getKey().compareTo(index) != 0) {
                entry.setValue("ui-icon ui-icon-circle-triangle-e");
            }
        }

        mapaIconeBtnParcela.put(index, (canMostrarParcelasDoCalculo ? "ui-icon ui-icon-circle-triangle-s" : "ui-icon ui-icon-circle-triangle-e"));
    }

    public void buscarDadosCadastroITBI() {
        validarBloqueioTransferenciaProprietario();
        if (selecionado.getTipoITBI().equals(TipoITBI.IMOBILIARIO)) {
            dadosCadastroITBI = calculoITBIFacade.montarDadosProprietarioImobiliario(selecionado.getIdCadastro());
        } else if (selecionado.getTipoITBI().equals(TipoITBI.RURAL)) {
            dadosCadastroITBI = calculoITBIFacade.montarDadosProprietarioRural(selecionado.getIdCadastro());
        }
        if (!isOperacaoVer()) {
            verificarDebitos();
        }
    }

    private void validarBloqueioTransferenciaProprietario() {
        if (TipoITBI.IMOBILIARIO.equals(selecionado.getTipoITBI()) &&
            selecionado.getCadastroImobiliario() != null) {
            calculoITBIFacade.getBloqueioTransferenciaProprietarioFacade()
                .validarBloqueioTransferenciaProprietario(selecionado.getCadastroImobiliario());
        }
    }

    public void mudouCadastroImobiliario() {
        try {
            validarBloqueioTransferenciaProprietario();
            buscarDadosCadastroITBI();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public List<Pessoa> adquirentesTransmissaoAnterior(String parte) {
        List<Pessoa> retorno = Lists.newArrayList();
        if (selecionado.getCalculos().isEmpty()) {
            retorno.addAll(calculoITBIFacade.getPessoaFacade().listaTodasPessoas(parte, buscarSituacoesPesquisaPessoa().get(0)));
        } else {
            for (PropriedadeSimulacaoITBI transmitentesCalculoITBI : selecionado.getUltimoCalculo().getProprietariosSimulacao()) {
                retorno.add(transmitentesCalculoITBI.getPessoa());
            }
        }
        return retorno;
    }

    public String montarNomesAdquirentes(CalculoITBI calculoITBI) {
        StringBuilder adquirente = new StringBuilder();
        for (AdquirentesCalculoITBI adquirenteCalculo : calculoITBI.getAdquirentesCalculo()) {
            adquirente.append(adquirenteCalculo.getAdquirente().getNomeCpfCnpj()).append("\n");
        }
        return adquirente.toString();
    }

    public String montarPercentuaisAdquirentes(CalculoITBI calculoITBI) {
        StringBuilder percentualAdquirente = new StringBuilder();
        for (AdquirentesCalculoITBI adquirenteCalculo : calculoITBI.getAdquirentesCalculo()) {
            percentualAdquirente.append(adquirenteCalculo.getPercentual()).append("%").append("\n");
        }
        return percentualAdquirente.toString();
    }

    void popularMapSimulacao() {
        setAdquirentesSimulacao(calculoITBI.getProprietariosSimulacao());
    }

    public void adicionarCalculo() {
        try {
            validarCalculo();
            if (parametro != null) {
                atribuirValoresAoCalculo();
                Util.adicionarObjetoEmLista(selecionado.getCalculos(), calculoITBI);
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
        calculoITBI = null;
    }

    public void editarCalculo(CalculoITBI calculo) {
        try {
            validarAcoesCalculo(calculo, "editar");
            calculoITBI = calculo;
            FacesUtil.executaJavaScript("wTransmissao.show();");
            FacesUtil.atualizarComponente("formTransmissao");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void visualizarSimulacao(CalculoITBI calc) {
        try {
            calculoITBI = calc;
            popularMapSimulacao();
            FacesUtil.executaJavaScript("wSimulacaoVer.show();");
            FacesUtil.atualizarComponente("formSimulacaoVer");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarAcoesCalculo(CalculoITBI calculo, String tipoAcao) {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getUltimoCalculo().equals(calculo)) {
            ve.adicionarMensagemDeCampoObrigatorio("Para " + tipoAcao.trim() + " a transmissão " +
                calculo.getOrdemCalculo() + " é necessário remover a transmissão " + (calculo.getOrdemCalculo() + 1) + "!");
        }
        ve.lancarException();
    }

    public Boolean bloquearAlteracaoDePorcentagemDialog() {
        if (selecionado.getUltimoCalculo() != null) {
            return !selecionado.getUltimoCalculo().equals(calculoITBI);
        }
        return false;
    }

    public BigDecimal porcentagemTotalAdquirentesSimulacao() {
        BigDecimal porcentagem = BigDecimal.ZERO;
        for (PropriedadeSimulacaoITBI propriedadeSimulacaoITBI : getAdquirentesSimulacao()) {
            porcentagem = porcentagem.add(BigDecimal.valueOf(propriedadeSimulacaoITBI.getProporcao()));
        }
        return porcentagem;
    }

    public void salvarDialogEdicaoPorcentagem() {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (calculoITBI != null) {
                BigDecimal porcentagem = porcentagemTotalAdquirentesSimulacao();
                if (porcentagem.compareTo(CEM) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A soma do percentual dos adquirentes não deve ultrapassar 100%.");
                }
                if (porcentagem.compareTo(CEM) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A soma do percentual dos adquirentes não deve ser menor que 100%.");
                }
            }
            ve.lancarException();
            for (AdquirentesCalculoITBI adquirenteCalculoITBI : calculoITBI.getAdquirentesCalculo()) {
                for (PropriedadeSimulacaoITBI propriedadeSimulacaoITBI : calculoITBI.getProprietariosSimulacao()) {
                    if (adquirenteCalculoITBI.getAdquirente().getId().equals(propriedadeSimulacaoITBI.getPessoa().getId())) {
                        adquirenteCalculoITBI.setPercentual(BigDecimal.valueOf(propriedadeSimulacaoITBI.getProporcao()));
                        break;
                    }
                }
            }
            calculoITBI.setProprietariosSimulacao(getAdquirentesSimulacao());
            FacesUtil.executaJavaScript("wSimulacaoVer.hide();");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void visualizarCalculo(CalculoITBI calculo) {
        this.calculoITBI = (CalculoITBI) Util.clonarObjeto(calculo);
        if (calculoITBI != null && !calculoITBI.getTransmitentesCalculo().isEmpty()) {
            transmitente = (TransmitentesCalculoITBI) Util.clonarObjeto(calculoITBI.getTransmitentesCalculo().get(0));
        }

        FacesUtil.executaJavaScript("wTransmissaoVer.show();");
        FacesUtil.atualizarComponente("formTransmissaoVer");
    }

    private void atribuirValoresAoCalculo() {
        if (calculoITBI != null) {
            calculoITBI.setDataLancamento(selecionado.getDataLancamento());
            calculoITBI.setDataCalculo(new Date());
            calculoITBI.setSimulacao(false);
            calculoITBI.setProcessoCalculoITBI(selecionado);
            calculoITBI.setCadastro(null);
            calculoITBI.setCadastroImobiliario(selecionado.getCadastroImobiliario());
            calculoITBI.setCadastroRural(selecionado.getCadastroRural());
            calculoITBI.setSequencia(null);

            BigDecimal valorCalculadoSomado = BigDecimal.ZERO;
            BigDecimal valorInformadoSomado = BigDecimal.ZERO;
            for (ItemCalculoITBI ic : calculoITBI.getItensCalculo()) {
                valorCalculadoSomado = valorCalculadoSomado.add(ic.getValorCalculado());
                valorInformadoSomado = valorInformadoSomado.add(ic.getValorInformado());
            }
            if (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoITBI.getTipoBaseCalculoITBI())) {
                valorCalculadoSomado = valorCalculadoSomado.add(calculoITBI.getValorVenal().setScale(2, RoundingMode.HALF_UP).multiply(calculoITBI.getValorReajuste())).divide(CEM, 2, RoundingMode.HALF_UP);
            }
            if (calculoITBI.getBaseCalculo().compareTo(BigDecimal.ZERO) == 0) {
                calculoITBI.setBaseCalculo(valorInformadoSomado);
            }
            calculoITBI.setIsento(isento);
            if (calculoITBI.getIsento()) {
                calculoITBI.setTipoIsencaoITBI(tipoIsencaoITBI);
            } else {
                calculoITBI.setTipoIsencaoITBI(null);
            }
            calculoITBI.setValorReal(calculoITBI.getIsento() ? BigDecimal.ZERO : valorCalculadoSomado);
            calculoITBI.setValorEfetivo(calculoITBI.getIsento() ? BigDecimal.ZERO : valorCalculadoSomado);
            calculoITBI.setNumeroParcelas(1);
        }
    }

    private void novosProprietariosSimulacao() {
        if (calculoITBI.getProprietariosSimulacao() != null) {
            calculoITBI.getProprietariosSimulacao().clear();
        } else {
            calculoITBI.setProprietariosSimulacao(Lists.newArrayList());
        }
        if (selecionado.getTipoITBI().equals(TipoITBI.IMOBILIARIO)) {
            for (Propriedade novoProprietario : calculoITBIFacade.simulacaoDasProporcoesImobiliario(selecionado.getCadastroImobiliario(), selecionado.getCalculos()).get((calculoITBI.getOrdemCalculo()))) {
                PropriedadeSimulacaoITBI propriedadeSimulacao = new PropriedadeSimulacaoITBI();
                propriedadeSimulacao.setPessoa(novoProprietario.getPessoa());
                propriedadeSimulacao.setCalculoITBI(calculoITBI);
                propriedadeSimulacao.setProporcao(novoProprietario.getProporcao());
                calculoITBI.getProprietariosSimulacao().add(propriedadeSimulacao);
            }
        } else {
            for (PropriedadeRural novoProprietario : calculoITBIFacade.simulacaoDasProporcoesRural(selecionado.getCadastroRural(), selecionado.getCalculos()).get(calculoITBI.getOrdemCalculo())) {
                PropriedadeSimulacaoITBI propriedadeSimulacao = new PropriedadeSimulacaoITBI();
                propriedadeSimulacao.setPessoa(novoProprietario.getPessoa());
                propriedadeSimulacao.setCalculoITBI(calculoITBI);
                propriedadeSimulacao.setProporcao(novoProprietario.getProporcao());
                calculoITBI.getProprietariosSimulacao().add(propriedadeSimulacao);
            }
        }
    }

    private void validarCalculo() {
        ValidacaoException ve = new ValidacaoException();
        if (calculoITBI != null) {
            if (isento && tipoIsencaoITBI == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de isenção.");
            }
            if (calculoITBI.getOrdemCalculo() > 1) {
                for (CalculoITBI calculoItbi : selecionado.getCalculos()) {
                    if (calculoItbi.getOrdemCalculo() == 1) {
                        BigDecimal somaTotalTransmitentes = BigDecimal.ZERO;
                        for (TransmitentesCalculoITBI transmitentesCalculoITBI : calculoItbi.getTransmitentesCalculo()) {
                            somaTotalTransmitentes = transmitentesCalculoITBI.getPercentual().setScale(15, RoundingMode.HALF_UP);
                        }
                        if (CEM.compareTo(somaTotalTransmitentes) > 0) {
                            ve.adicionarMensagemDeCampoObrigatorio("A soma total dos transmitentes da primeira transmissão deve ser igual à 100%.");
                        }
                    }
                }
            }
            if (calculoITBI.getTransmitentesCalculo() == null || calculoITBI.getTransmitentesCalculo().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Adicione pelo menos um Transmitente ao processo.");
            }
            if (calculoITBI.getAdquirentesCalculo() == null || calculoITBI.getAdquirentesCalculo().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione pelo menos um Adquirente ao processo.");
            }
            if (calculoITBI.getPercentual() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Percentual da Base de Cálculo deve ser informado.");
            } else if (calculoITBI.getPercentual().compareTo(BigDecimal.ZERO) <= 0 || calculoITBI.getPercentual().compareTo(CEM) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Percentual da Base de Cálculo deve estar entre zero(0) e cem(100).");
            }
            if (calculoITBI.getTotalBaseCalculo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Total da base de Cálculo deve ser informado.");
            } else if (calculoITBI.getTotalBaseCalculo().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Total da base de Cálculo deve ser maior que zero(0).");
            }
            if (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoITBI.getTipoBaseCalculoITBI()) && (calculoITBI.getValorReajuste() == null || calculoITBI.getValorReajuste().compareTo(BigDecimal.ZERO) < 0) || calculoITBI.getValorReajuste().compareTo(new BigDecimal("999.99")) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Percentual de Reajuste sobre o Valor Venal do Imóvel deve ser maior que zero e menor que 1.000%.");
            }
            if (!TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoITBI.getTipoBaseCalculoITBI())) {
                if (calculoITBI.getItensCalculo() == null || calculoITBI.getItensCalculo().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione pelo menos um Tipo de Negociação.");
                }
                BigDecimal valorTotal = BigDecimal.ZERO;
                for (ItemCalculoITBI ic : calculoITBI.getItensCalculo()) {
                    valorTotal = valorTotal.add(ic.getValorInformado());
                }
                if (TipoBaseCalculo.VALOR_NEGOCIADO.equals(calculoITBI.getTipoBaseCalculoITBI())) {
                    if (calculoITBI.getTotalBaseCalculo().compareTo(valorTotal) != 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O Total da Base de Cálculo deve ser igual à soma total dos Tipos de Negociação informados.");
                    }
                } else {
                    BigDecimal valorVenal = calculoITBI.getValorVenal().setScale(2, RoundingMode.HALF_UP);
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

    public void removerCalculo(CalculoITBI calculo) {
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
            validarDocumentosPessoa(transmitente.getPessoa());
            transmitente.setCalculoITBI(calculoITBI);
            Util.adicionarObjetoEmLista(calculoITBI.getTransmitentesCalculo(), transmitente);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            transmitente = new TransmitentesCalculoITBI();
        }
    }

    public void limparListaTransmitentes() {
        calculoITBI.getTransmitentesCalculo().clear();
    }

    public void removerTransmitente(TransmitentesCalculoITBI transmitenteCalculo) {
        calculoITBI.getTransmitentesCalculo().remove(transmitenteCalculo);
    }

    public String montarNomesTransmitentes(CalculoITBI calculoITBI) {
        StringBuilder transmitente = new StringBuilder();
        for (TransmitentesCalculoITBI transmitenteCalculo : calculoITBI.getTransmitentesCalculo()) {
            transmitente.append(transmitenteCalculo.getPessoa().getNomeCpfCnpj()).append("\n");
        }
        return transmitente.toString();
    }

    public String montarPercentuaisTransmitentes(CalculoITBI calculoITBI) {
        StringBuilder percentualTransmitente = new StringBuilder();
        for (TransmitentesCalculoITBI transmitenteCalculo : calculoITBI.getTransmitentesCalculo()) {
            percentualTransmitente.append(transmitenteCalculo.getPercentual()).append("%").append("\n");
        }
        return percentualTransmitente.toString();
    }

    private void validarTransmitente() {
        ValidacaoException ve = new ValidacaoException();
        if (transmitente.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Transmitente deve ser informado.");
        } else {
            for (TransmitentesCalculoITBI transmitenteCalculo : calculoITBI.getTransmitentesCalculo()) {
                if (transmitente.getPessoa().equals(transmitenteCalculo.getPessoa()) && !calculoITBI.getTransmitentesCalculo().contains(transmitente)) {
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
            adquirente.setCalculoITBI(calculoITBI);
            Util.adicionarObjetoEmLista(calculoITBI.getAdquirentesCalculo(), adquirente);
            calculoITBI.setPercentual(buscarPercentualTotalAdquirentes());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            adquirente = new AdquirentesCalculoITBI();
        }
    }

    private void validarAdquirente() {
        ValidacaoException ve = new ValidacaoException();
        if (adquirente.getAdquirente() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Adquirente deve ser informado.");
        } else {
            for (AdquirentesCalculoITBI adquirenteCalculo : calculoITBI.getAdquirentesCalculo()) {
                if (adquirente.getAdquirente().equals(adquirenteCalculo.getAdquirente()) && !calculoITBI.getAdquirentesCalculo().contains(adquirente)) {
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
                BigDecimal percentualAdquirente = calculoITBI.getAdquirentesCalculo().contains(adquirente) ? BigDecimal.ZERO : adquirente.getPercentual();
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
                if (calculoITBIFacade.hasOutraPessoaComMesmoDocto(pessoa)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O " + docto + " de " + pessoa.getNome() + " pertence também a outra pessoa!");
                }
            }
            ve.lancarException();
        }
    }

    public void editarAdquirente(AdquirentesCalculoITBI adquirenteCalculo) {
        adquirente = (AdquirentesCalculoITBI) Util.clonarObjeto(adquirenteCalculo);
    }

    public void removerAdquirente(AdquirentesCalculoITBI adquirenteCalculo) {
        calculoITBI.getAdquirentesCalculo().remove(adquirenteCalculo);
        calculoITBI.setPercentual(buscarPercentualTotalAdquirentes());
    }

    public void adicionarItemCalculo() {
        try {
            validarItemCalculo();
            BigDecimal residualParaSerInformado = calculoITBI.getTotalBaseCalculo().subtract(itemCalculo.getValorInformado());
            itemCalculo.setCalculoITBI(calculoITBI);
            itemCalculo.setValorCalculado(itemCalculo.getValorInformado().multiply(itemCalculo.getTipoNegociacao().getAliquota()).divide(CEM, 2, RoundingMode.HALF_UP));
            itemCalculo.setPercentual((itemCalculo.getValorInformado().multiply(CEM)).divide(calculoITBI.getTotalBaseCalculo(), 2, RoundingMode.HALF_UP));
            Util.adicionarObjetoEmLista(calculoITBI.getItensCalculo(), itemCalculo);
            itemCalculo = new ItemCalculoITBI();
            if (calculoITBI.getTotalBaseCalculo().compareTo(buscarSomaValorInformadoDasNegociacoes()) > 0 && residualParaSerInformado.compareTo(BigDecimal.ZERO) > 0) {
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
            } else if (isBaseCalculoValorVenal() && (itemCalculo != null && (!calculoITBI.getItensCalculo().contains(itemCalculo) ? itemCalculo.getPercentual() : BigDecimal.ZERO).add(buscarPercentualTotalNegociacoes()).compareTo(CEM) > 0)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Soma dos percentuais com o percentual informado não pode ser maior que cem(100)!");
            } else {
                if (calculoITBI != null && itemCalculo != null) {
                    for (ItemCalculoITBI itemCalculoITBI : calculoITBI.getItensCalculo()) {
                        if (itemCalculoITBI.getTipoNegociacao().equals(itemCalculo.getTipoNegociacao()) && !calculoITBI.getItensCalculo().contains(itemCalculo)) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de Negociação informado já foi adicionado!");
                            break;
                        }
                    }
                    if (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoITBI.getTipoBaseCalculoITBI())) {
                        validarPorcentagemPermitida();
                    }
                    if (isBaseCalculoValorVenal() && calculoITBI.getBaseCalculo().compareTo(BigDecimal.ZERO) == 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O imóvel informado não possui Valor Venal para base de cálculo.");
                    }
                    if (isBaseCalculoValorVenal() && itemCalculo.getValorInformado().setScale(2, RoundingMode.HALF_UP).compareTo(calculoITBI.getTotalBaseCalculo().setScale(2, RoundingMode.HALF_UP)) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O valor informado é maior que o total da base de cálculo.");
                    }
                    if (isBaseCalculoValorVenal() && (buscarSomaValorInformadoDasNegociacoes().add(itemCalculo.getValorInformado())).compareTo(calculoITBI.getTotalBaseCalculo()) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A soma dos valores informados é maior que a base de cálculo.");
                    }
                }
            }
        }
        if (calculoITBI != null && itemCalculo != null) {
            if (itemCalculo.getTipoNegociacao() != null && TipoBaseCalculo.VALOR_NEGOCIADO.equals(calculoITBI.getTipoBaseCalculoITBI()) || TipoBaseCalculo.VALOR_APURADO.equals(calculoITBI.getTipoBaseCalculoITBI())) {
                if (itemCalculo.getValorInformado().compareTo(calculoITBI.getTotalBaseCalculo()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor informado não pode exceder o total da base de cálculo.");
                }
                if (calculoITBI.getItensCalculo() != null && !calculoITBI.getItensCalculo().isEmpty()) {
                    BigDecimal totalValorInformado = buscarSomaValorInformadoDasNegociacoes();
                    if ((totalValorInformado.add(itemCalculo.getValorInformado())).compareTo(calculoITBI.getTotalBaseCalculo()) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A soma dos valores informados na tabela de negociação não podem exceder o valor total da base de cálculo.");
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void editarItemCalculo(ItemCalculoITBI itemCalculoITBI) {
        this.itemCalculo = (ItemCalculoITBI) Util.clonarObjeto(itemCalculoITBI);
    }

    public void removerItemCalculo(ItemCalculoITBI itemCalculoITBI) {
        calculoITBI.getItensCalculo().remove(itemCalculoITBI);
        validarPercentualAndAtualizarValorTipoNegociacao();
    }

    public void validarPorcentagemPermitidaAndAtualizarValores() {
        if (calculoITBI != null) {
            if (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoITBI.getTipoBaseCalculoITBI())) {
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

    public void verificarDebitos() {
        if (selecionado != null) {
            switch (parametro.getVerificarDebitosImovel()) {
                case DEBITOS_VENCIDOS:
                    verificarDebitosVencidosPodendoPermitir();
                    break;
                case DEBITOS_VENCIDOS_OU_A_VENCER:
                    verificarDebitosVencidosOuAVencerPodendoPermitir();
                    break;
                case DEBITOS_AJUIZADOS:
                    verificarDebitosAjuizados();
                    break;
                case DEBITOS_NAO_PERMITIDOS:
                    verificarDebitosVencimentoNaoPermitindo();
                    break;
                case DEBITOS_NAO_PERMITIDOS_VENCIDOS_OU_A_VENCER:
                    verificarDebitosVencidosOuAvencerNaoPermitindo();
                    break;
                case DEBITOS_NAO_PERMITIDOS_AJUIZADOS:
                    verificarDebitosAjuizadosNaoPermitindo();
                    break;
                case DEBITOS_ITBI:
                    verificarDebitosITBIPodendoPermitir();
                    break;
                case DEBITOS_NAO_PERMITIDOS_ITBI:
                    verificarDebitosITBINaoPermitido();
                    break;
                default:
                    carregarDadosDoCadastro();
                    break;
            }
        }
    }

    private void verificarDebitosVencidosPodendoPermitir() {
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            if (calculoITBIFacade.isCadastroComDebitos(selecionado.getCadastro(), false)) {
                FacesUtil.executaJavaScript(montarJavaScriptConfirm(VerificarDebitosDoImovel.DEBITOS_VENCIDOS));
            } else {
                carregarDadosDoCadastro();
            }
        }
    }

    private void verificarDebitosVencidosOuAVencerPodendoPermitir() {
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            if (calculoITBIFacade.isCadastroComDebitos(selecionado.getCadastro(), true)) {
                FacesUtil.executaJavaScript(montarJavaScriptConfirm(VerificarDebitosDoImovel.DEBITOS_VENCIDOS_OU_A_VENCER));
            } else {
                carregarDadosDoCadastro();
            }
        }
    }

    private void verificarDebitosAjuizados() {
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            if (calculoITBIFacade.isCadastroComDebitosAjuizados(selecionado.getCadastro())) {
                FacesUtil.executaJavaScript(montarJavaScriptConfirm(VerificarDebitosDoImovel.DEBITOS_AJUIZADOS));
            } else {
                carregarDadosDoCadastro();
            }
        }
    }

    private void verificarDebitosVencimentoNaoPermitindo() {
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            if (calculoITBIFacade.isCadastroComDebitos(selecionado.getCadastro(), false)) {
                FacesUtil.executaJavaScript(montarJavaScriptAlert(VerificarDebitosDoImovel.DEBITOS_NAO_PERMITIDOS));
                novo();
            } else {
                carregarDadosDoCadastro();
            }
        }
    }

    private void verificarDebitosVencidosOuAvencerNaoPermitindo() {
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            if (calculoITBIFacade.isCadastroComDebitos(selecionado.getCadastro(), true)) {
                FacesUtil.executaJavaScript(montarJavaScriptAlert(VerificarDebitosDoImovel.DEBITOS_NAO_PERMITIDOS_VENCIDOS_OU_A_VENCER));
                novo();
            } else {
                carregarDadosDoCadastro();
            }
        }
    }

    private void verificarDebitosAjuizadosNaoPermitindo() {
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            if (calculoITBIFacade.isCadastroComDebitosAjuizados(selecionado.getCadastro())) {
                FacesUtil.executaJavaScript(montarJavaScriptAlert(VerificarDebitosDoImovel.DEBITOS_NAO_PERMITIDOS_AJUIZADOS));
                novo();
            } else {
                carregarDadosDoCadastro();
            }
        }
    }

    private void verificarDebitosITBIPodendoPermitir() {
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            if (calculoITBIFacade.isCadastroPossuiDebitosItbi(selecionado.getCadastro())) {
                FacesUtil.executaJavaScript(montarJavaScriptConfirm(VerificarDebitosDoImovel.DEBITOS_ITBI));
            } else {
                carregarDadosDoCadastro();
            }
        }
    }

    private void verificarDebitosITBINaoPermitido() {
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            if (calculoITBIFacade.isCadastroPossuiDebitosItbi(selecionado.getCadastro())) {
                FacesUtil.executaJavaScript(montarJavaScriptAlert(VerificarDebitosDoImovel.DEBITOS_NAO_PERMITIDOS_ITBI));
                novo();
            } else {
                carregarDadosDoCadastro();
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
        if (calculoITBI != null && calculoITBI.getTipoBaseCalculoITBI() != null) {
            if (selecionado.getCadastroImobiliario() != null || selecionado.getCadastroRural() != null) {
                if (TipoBaseCalculo.VALOR_VENAL.equals(calculoITBI.getTipoBaseCalculoITBI())) {
                    calculoITBI.setBaseCalculo(calculoITBI.getValorVenal().setScale(2, RoundingMode.HALF_UP));
                    mudouBaseCalculo();
                } else if (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoITBI.getTipoBaseCalculoITBI()) && calculoITBI.getValorReajuste() != null) {
                    calculoITBI.setBaseCalculo(calculoITBI.getValorVenal().setScale(2, RoundingMode.HALF_UP).add(calculoITBI.getValorVenal().multiply(calculoITBI.getValorReajuste().divide(CEM, 8, RoundingMode.HALF_UP))));
                    mudouBaseCalculo();
                } else {
                    calculoITBI.setBaseCalculo(BigDecimal.ZERO);
                    mudouBaseCalculo();
                }
            }
        }
    }

    private void validarPorcentagemPermitida() {
        if (calculoITBI != null && calculoITBI.getValorReajuste() != null) {
            ValidacaoException ve = new ValidacaoException();
            if (calculoITBI.getValorReajuste().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Percentual de Reajuste sobre o Valor Venal do Imóvel (%) deve ser maior que zero.");
            }

            if (calculoITBI.getValorReajuste().compareTo(new BigDecimal("999.99")) > 0) {
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
                itemCalculo.setValorInformado(calculoITBI.getTotalBaseCalculo().multiply(itemCalculo.getPercentual().divide(CEM, 2, RoundingMode.HALF_UP)));
            }
        }
    }

    public void validarValorAndAtualizarPorcentualTipoNegociacao() {
        if (isBaseCalculoValorVenal() && calculoITBI != null && itemCalculo != null) {
            if (itemCalculo.getValorInformado() == null) {
                itemCalculo.setValorInformado(BigDecimal.ZERO);
            }
            if (itemCalculo.getPercentual() == null) {
                itemCalculo.setPercentual(BigDecimal.ZERO);
            }
            if ((itemCalculo.getValorInformado().add(buscarSomaValorInformadoDasNegociacoes())).compareTo(calculoITBI.getTotalBaseCalculo()) > 0) {
                FacesUtil.addOperacaoNaoPermitida("O valor total informado não pode ser maior que a Base de Cálculo!");
                itemCalculo.setValorInformado(calculoITBI.getTotalBaseCalculo().subtract(buscarSomaValorInformadoDasNegociacoes()));
            } else {
                itemCalculo.setPercentual((itemCalculo.getValorInformado().multiply(CEM)).divide(calculoITBI.getTotalBaseCalculo(), 2, RoundingMode.HALF_UP));
            }
        }
    }

    private BigDecimal buscarPercentualTotalNegociacoes() {
        BigDecimal percentualTotal = BigDecimal.ZERO;
        if (calculoITBI != null) {
            for (ItemCalculoITBI item : calculoITBI.getItensCalculo()) {
                percentualTotal = percentualTotal.add(item.getPercentual());
            }
        }
        return percentualTotal;
    }

    private BigDecimal buscarPercentualTotalAdquirentes() {
        BigDecimal percentualTotalAdiquirente = BigDecimal.ZERO;
        if (calculoITBI != null) {
            for (AdquirentesCalculoITBI adquirenteCalculo : calculoITBI.getAdquirentesCalculo()) {
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
        if (calculoITBI != null) {
            for (TransmitentesCalculoITBI transmitenteCalculo : calculoITBI.getTransmitentesCalculo()) {
                if (this.transmitente.equals(transmitenteCalculo)) {
                    percentualTotalTransmitentes = percentualTotalTransmitentes.add(this.transmitente.getPercentual());
                } else {
                    percentualTotalTransmitentes = percentualTotalTransmitentes.add(transmitenteCalculo.getPercentual());
                }
            }
        }
        return percentualTotalTransmitentes;
    }

    public void mudouBaseCalculo() {
        if (calculoITBI.getItensCalculo() != null && !calculoITBI.getItensCalculo().isEmpty()) {
            itemCalculo.setValorInformado(calculoITBI.getTotalBaseCalculo().subtract(buscarSomaValorInformadoDasNegociacoes()));
        } else {
            itemCalculo.setValorInformado(calculoITBI.getTotalBaseCalculo());
        }
    }

    public BigDecimal buscarSomaValorInformadoDasNegociacoes() {
        BigDecimal resultado = BigDecimal.ZERO;
        if (calculoITBI != null) {
            for (ItemCalculoITBI icItbi : calculoITBI.getItensCalculo()) {
                if (!calculoITBI.getItensCalculo().contains(itemCalculo)) {
                    resultado = resultado.add(icItbi.getValorInformado());
                }
            }
        }
        return resultado;
    }

    public boolean canMostrarPercentualDeReajuste() {
        return calculoITBI != null && calculoITBI.getTipoBaseCalculoITBI() != null && TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoITBI.getTipoBaseCalculoITBI());
    }

    public boolean isBaseCalculoValorVenal() {
        return calculoITBI != null && (TipoBaseCalculo.VALOR_VENAL_ACRESCIMOS.equals(calculoITBI.getTipoBaseCalculoITBI()) || TipoBaseCalculo.VALOR_VENAL.equals(calculoITBI.getTipoBaseCalculoITBI()));
    }

    public boolean canAdicionarNegociacoes() {
        if ((calculoITBI != null && calculoITBI.getAdquirentesCalculo().isEmpty()) || (buscarPercentualTotalNegociacoes().compareTo(CEM) == 0)) {
            return true;
        }
        if (TipoITBI.IMOBILIARIO.equals(selecionado.getTipoITBI())) {
            return selecionado.getCadastroImobiliario() == null;
        }
        return selecionado.getCadastroRural() == null;
    }

    public boolean canAlterarValorBaseCalculo() {
        return calculoITBI != null && ((TipoBaseCalculo.VALOR_NEGOCIADO.equals(calculoITBI.getTipoBaseCalculoITBI()) || TipoBaseCalculo.VALOR_APURADO.equals(calculoITBI.getTipoBaseCalculoITBI())) && calculoITBI.getItensCalculo().isEmpty());
    }

    public void preparaImpressaoDAM() {
        buscarParcelasDoITBI();
        FacesUtil.executaJavaScript("confirmaDam.show()");
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.atualizarComponente("formConfirmaDam");
    }

    public void imprimirDamIndividual() {
        try {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(gerarDamIndividual());
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível imprimir o DAM", "Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.error("Erro ao imprimir o Dam Individual. ", e);
        }
    }

    public List<ResultadoParcela> getParcelasImpressaoDAM() {
        List<ResultadoParcela> resultadoParcelaList = Lists.newArrayList();
        for (DAMResultadoParcela damResultadoParcela : parcelasCalculoITBI) {
            if (damResultadoParcela.getResultadoParcela().isEmAberto()) {
                resultadoParcelaList.add(damResultadoParcela.getResultadoParcela());
            }
        }
        return resultadoParcelaList;
    }

    public List<DAM> gerarDamIndividual() {
        List<DAM> dams = Lists.newArrayList();
        try {
            List<ResultadoParcela> parcelas = getParcelasImpressaoDAM();
            Exercicio exercicio = calculoITBIFacade.buscarExercicioCorrente();
            for (ResultadoParcela parcela : parcelas) {
                DAM dam = calculoITBIFacade.buscarOuGerarDam(parcela);
                dams.add(dam);
            }
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível gerar o DAM", "Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.error("Erro ao gerar o dam individual: ", e);
        }
        return dams;
    }

    public void imprimirDamAgrupado() throws Exception {
        try {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            List<ResultadoParcela> parcelas = getParcelasImpressaoDAM();

            Collections.sort(parcelas, ResultadoParcela.getComparatorByValuePadrao());
            DAM dam = gerarDamAgrupado(parcelas);
            Pessoa pessoa = selecionado.getUltimoCalculo() != null ? selecionado.getUltimoCalculo().getAdquirente() : null;
            imprimeDAM.imprimirDamCompostoViaApi(dam, pessoa);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public DAM gerarDamAgrupado(List<ResultadoParcela> resultadoParcelas) {
        List<ParcelaValorDivida> parcelas = Lists.newArrayList();
        try {
            boolean gerarNovoDam = true;
            BigDecimal valorTotalParcelas = BigDecimal.ZERO;
            for (ResultadoParcela parcela : resultadoParcelas) {
                valorTotalParcelas = valorTotalParcelas.add(parcela.getValorTotal());
            }
            List<DAM> damsAgrupados = calculoITBIFacade.buscarDamsAgrupadosDaParcela(resultadoParcelas.get(0).getIdParcela());
            DAM damAgrupado = null;
            for (DAM dam : damsAgrupados) {
                if (dam.getValorTotal().compareTo(valorTotalParcelas) == 0 && verificarParcelasDoDam(dam, resultadoParcelas) && !dam.isVencido()) {
                    gerarNovoDam = false;
                    damAgrupado = dam;
                    break;
                }
            }
            if (gerarNovoDam && damAgrupado == null) {
                for (ResultadoParcela parcela : resultadoParcelas) {
                    ParcelaValorDivida p = calculoITBIFacade.recuperarParcela(parcela.getIdParcela());
                    parcelas.add(p);
                }
                return calculoITBIFacade.gerarDAM(parcelas, parcelas.get(0).getVencimento());
            } else {
                return damAgrupado;
            }
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível gerar o DAM", "Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.error(e.getMessage());
        }
        return null;
    }

    private boolean verificarParcelasDoDam(DAM dam, List<ResultadoParcela> resultadoParcelas) {
        for (ItemDAM itemDAM : dam.getItens()) {
            boolean temParcela = false;
            for (ResultadoParcela resultadoParcela : resultadoParcelas) {
                if (resultadoParcela.getIdParcela().equals(itemDAM.getParcela().getId())) {
                    temParcela = true;
                    break;
                }
            }
            if (!temParcela) {
                return false;
            }
        }
        return !dam.getItens().isEmpty();
    }

    public boolean isCancelado() {
        return SituacaoITBI.CANCELADO.equals(selecionado.getSituacao());
    }

    public boolean isEmitido() {
        return SituacaoITBI.EMITIDO.equals(selecionado.getSituacao());
    }

    public boolean isAssinado() {
        return SituacaoITBI.ASSINADO.equals(selecionado.getSituacao());
    }

    public boolean isRetificado() {
        return SituacaoITBI.RETIFICADO.equals(selecionado.getSituacao());
    }

    public boolean isPago() {
        return SituacaoITBI.PAGO.equals(selecionado.getSituacao());
    }

    public boolean isAberto() {
        return SituacaoITBI.ABERTO.equals(selecionado.getSituacao());
    }

    public boolean isProcessado() {
        return SituacaoITBI.PROCESSADO.equals(selecionado.getSituacao());
    }

    public boolean hasParcelasEmAberto() {
        if (parcelasCalculoITBI != null) {
            List<ResultadoParcela> parcelas = montarListaParcelas();
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isEmAberto()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isLaudoPrimeiraViaEmitido() {
        return laudoAvaliacaoITBI != null && (laudoAvaliacaoITBI.getLaudoImpresso() != null && laudoAvaliacaoITBI.getLaudoImpresso());
    }

    public boolean isLaudoSegundaViaEmitido() {
        return laudoAvaliacaoITBI != null && laudoAvaliacaoITBI.getId() != null && (laudoAvaliacaoITBI.getLaudoSegundaViaImpresso() != null && laudoAvaliacaoITBI.getLaudoSegundaViaImpresso());
    }

    public void solicitarImpresaoLaudoAvaliacaoITBI(Boolean segundaVia, Boolean somenteAssinar) {
        isSegundaVia = segundaVia;
        if (!somenteAssinar) {
            if (!selecionado.getIsentoImune() && !habilitarEmissaoLaudo("emitido")) {
                return;
            }
        }
        laudoAvaliacaoITBI = calculoITBIFacade.recuperarLaudo(selecionado);
        if (laudoAvaliacaoITBI == null) {
            laudoAvaliacaoITBI = new LaudoAvaliacaoITBI();
            laudoAvaliacaoITBI.setProcessoCalculoITBI(selecionado);
        } else {
            laudoAvaliacaoITBI.setLaudoImpresso(true);
            if (laudoAvaliacaoITBI.getRespComissaoAvaliadora() != null && !responsaveisComissaoAvaliadora.contains(laudoAvaliacaoITBI.getRespComissaoAvaliadora())) {
                laudoAvaliacaoITBI.setRespComissaoAvaliadora(null);
            }
            if (laudoAvaliacaoITBI.getDiretorChefeDeparTributo() != null && !diretoresChefeDepartamentoTributos.contains(laudoAvaliacaoITBI.getDiretorChefeDeparTributo())) {
                laudoAvaliacaoITBI.setDiretorChefeDeparTributo(null);
            }
        }
        if (laudoAvaliacaoITBI.getDataImpressaoLaudo() == null) {
            laudoAvaliacaoITBI.setDataImpressaoLaudo(new Date());
            laudoAvaliacaoITBI.setLaudoImpresso(false);
        }
        if (segundaVia) {
            laudoAvaliacaoITBI.setLaudoSegundaViaImpresso(true);
            if (laudoAvaliacaoITBI.getDataImpressao2Via() == null) {
                laudoAvaliacaoITBI.setDataImpressao2Via(new Date());
            }
            if (parametro.getDiasVencimentoSegundaViaItbi() != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(laudoAvaliacaoITBI.getDataImpressao2Via());
                calendar.add(Calendar.DAY_OF_MONTH, parametro.getDiasVencimentoSegundaViaItbi());
                laudoAvaliacaoITBI.setDataVencimento(calendar.getTime());
            }
        } else if (laudoAvaliacaoITBI.getDataVencimento() == null) {
            laudoAvaliacaoITBI.setDataVencimento(calculoITBIFacade.montarDataVencimentoLaudo(parametro,
                selecionado.getDataLancamento(), laudoAvaliacaoITBI.getDataImpressaoLaudo()));
        }
        if (laudoAvaliacaoITBI.getUsuarioImpressaoLaudo() == null && !somenteAssinar) {
            laudoAvaliacaoITBI.setUsuarioImpressaoLaudo(Util.recuperarUsuarioCorrente());
        }
        boolean laudoJaAssinado = false;
        if (somenteAssinar && laudoAvaliacaoITBI.getRespComissaoAvaliadora() != null) {
            FacesUtil.addOperacaoNaoPermitida("O laudo desse ITBI ja está assinado pelo responsável pela comissão avaliadora.");
            laudoJaAssinado = true;
        }
        if (!laudoJaAssinado) {
            FacesUtil.executaJavaScript(somenteAssinar ? "dlgAssinaturaLaudo.show()" : "dlgImpressaoLaudo.show()");
        }
    }

    public void abrirDialogRetificacaoLaudo() {
        if (isPago() || isAberto()) {
            FacesUtil.addOperacaoNaoPermitida("O Lançamento do ITBI não pode ser Retificado!");
        } else {
            retificacao.setDataRetificacao(new Date());
            retificacao.setUsuarioRetificacao(Util.recuperarUsuarioCorrente());
            FacesUtil.executaJavaScript("retificacaoLaudo.show()");
        }
    }

    private boolean habilitarEmissaoLaudo(String emitidoEnviado) {
        if (hasParcelaEmAberto()) {
            FacesUtil.addOperacaoNaoPermitida("O Laudo de Avaliação pode ser " + emitidoEnviado + " somente após a quitação dos DAM(s) de ITBI.");
            return false;
        } else if (calculoITBIFacade.isUsuarioComPermissaoParaEmitirLaudo()) {
            if (!hasParcelaPaga()) {
                FacesUtil.addOperacaoNaoPermitida("O Laudo de Avaliação pode ser " + emitidoEnviado + " somente após a quitação dos DAM(s) de ITBI.");
                return false;
            }
        }
        return true;
    }

    public boolean hasParcelaEmAberto() {
        List<ResultadoParcela> parcelas = montarListaParcelas();
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.getSituacaoEnumValue().isInscritoDa()) {
                    ResultadoParcela dividaAtiva = calculoITBIFacade.buscarParcelaDividaAtivaDaParcelaOriginal(parcela.getIdParcela());
                    if (SituacaoParcela.EM_ABERTO.equals(dividaAtiva.getSituacaoEnumValue())) {
                        return true;
                    } else if (!dividaAtiva.getSituacaoEnumValue().isPago()) {
                        Divida divida = calculoITBIFacade.recuperarDivida(parcela.getIdDivida());
                        ConsultaParcela consultaParcela = new ConsultaParcela();
                        consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, calculoITBIFacade.buscarIdsDividasParcelamento(divida));
                        consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, parcela.getIdPessoa());
                        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
                        return !consultaParcela.executaConsulta().getResultados().isEmpty();
                    }
                } else if (parcela.isEmAberto()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasParcelaPaga() {
        List<ResultadoParcela> parcelas = montarListaParcelas();
        if (parcelas != null && !parcelas.isEmpty()) {
            boolean parcelaPaga = false;
            boolean parcelaVencida = false;
            for (ResultadoParcela par : parcelas) {
                if (par.getSituacaoEnumValue().isPago() || par.getSituacaoEnumValue().isCompensado()) {
                    parcelaPaga = true;
                } else if (SituacaoParcela.EM_ABERTO.equals(par.getSituacaoEnumValue()) && par.getVencido()) {
                    parcelaVencida = true;
                }
            }
            return parcelaPaga && !parcelaVencida;
        }
        return true;
    }

    public boolean canAlterarAssinaturaResponsavel() {
        if (laudoAvaliacaoITBI != null && laudoAvaliacaoITBI.getRespComissaoAvaliadora() != null) {
            return laudoAvaliacaoITBI.getRespComissaoAvaliadora().getPessoa().getId().equals(Util.recuperarUsuarioCorrente().getPessoaFisica().getId());
        }
        return true;
    }

    public boolean canAlterarAssinaturaDiretor() {
        if (laudoAvaliacaoITBI != null && laudoAvaliacaoITBI.getDiretorChefeDeparTributo() != null) {
            return laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getPessoa().getId().equals(Util.recuperarUsuarioCorrente().getPessoaFisica().getId());
        }
        return true;
    }

    public List<Long> getIdsDams() {
        List<Long> ids = Lists.newArrayList();
        for (DAMResultadoParcela damResultadoParcela : parcelasCalculoITBI) {
            if (damResultadoParcela.getDam() != null)
                ids.add(damResultadoParcela.getDam().getId());
        }
        return ids;
    }

    public void imprimirLaudo() {
        try {
            validarPermissaoDeImpressaoLaudo();
            atualizarSituacaoAndSalvar();
            ByteArrayOutputStream bytes = calculoITBIFacade.imprimirLaudo(selecionado.getId(), getIdsDams());
            AbstractReport report = AbstractReport.getAbstractReport();
            report.setGeraNoDialog(true);
            report.escreveNoResponse("LaudoAvaliacaoITBI.jasper", bytes.toByteArray());
            FacesUtil.atualizarComponente(":Formulario");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public Boolean podeAssinarLaudo() {
        Long idPessoaUsuario = calculoITBIFacade.buscarUsuarioSistema().getPessoaFisica().getId();
        for (ParametrosFuncionarios parametroFuncionarios : responsaveisComissaoAvaliadora) {
            if (parametroFuncionarios.getPessoa().getId().equals(idPessoaUsuario)) {
                return true;
            }
        }
        for (ParametrosFuncionarios diretorChefeDepartamentoTributo : diretoresChefeDepartamentoTributos) {
            if (diretorChefeDepartamentoTributo.getPessoa().getId().equals(idPessoaUsuario)) {
                return true;
            }
        }
        return false;
    }

    public void assinarLaudo() {
        try {
            canAssinar = false;
            Long idPessoaUsuario = calculoITBIFacade.buscarUsuarioSistema().getPessoaFisica().getId();
            for (ParametrosFuncionarios parametroFuncionarios : responsaveisComissaoAvaliadora) {
                if (parametroFuncionarios.getPessoa().getId().equals(idPessoaUsuario)) {
                    laudoAvaliacaoITBI.setRespComissaoAvaliadora(parametroFuncionarios);
                    canAssinar = true;
                }
            }
            for (ParametrosFuncionarios diretorChefeDepartamentoTributo : diretoresChefeDepartamentoTributos) {
                if (diretorChefeDepartamentoTributo.getPessoa().getId().equals(idPessoaUsuario)) {
                    laudoAvaliacaoITBI.setDiretorChefeDeparTributo(diretorChefeDepartamentoTributo);
                    canAssinar = true;
                }
            }
            if (canAssinar) {
                atualizarSituacaoAndSalvar();
            }
            FacesUtil.addOperacaoRealizada("Laudo assinado com sucesso.");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void retificarLaudo() {
        selecionado.setSituacao(SituacaoITBI.RETIFICADO);
        retificacao.setProcessoCalculoITBI(selecionado);
        selecionado.getRetificacoes().add(retificacao);
        retificacao.setNumeroRetificacao(selecionado.getRetificacoes().isEmpty() ? 1 : selecionado.getRetificacoes().size());
        selecionado = calculoITBIFacade.salvarRetornando(selecionado);
        retificacao = new RetificacaoCalculoITBI();
    }

    private void validarPermissaoDeImpressaoLaudo() {
        canAssinar = true;
        ValidacaoException ve = new ValidacaoException();
        LaudoAvaliacaoITBI laudoSalvo = calculoITBIFacade.recuperarLaudo(selecionado);

        boolean isUsuarioComPermissao = calculoITBIFacade.isUsuarioComPermissaoParaEmitirLaudo();
        Long idPessoaUsuario = calculoITBIFacade.buscarUsuarioSistema().getPessoaFisica().getId();

        String mensagemValidacao = "O usuário logado não tem permissão ";
        if (laudoAvaliacaoITBI.getRespComissaoAvaliadora() != null
            && (laudoSalvo == null || laudoSalvo.getRespComissaoAvaliadora() == null
            || (responsaveisComissaoAvaliadora.contains(laudoSalvo.getRespComissaoAvaliadora())
            && !laudoAvaliacaoITBI.getRespComissaoAvaliadora().equals(laudoSalvo.getRespComissaoAvaliadora())))) {
            if (!laudoAvaliacaoITBI.getRespComissaoAvaliadora().getPessoa().getId().equals(idPessoaUsuario)) {
                canAssinar = false;
                mensagemValidacao += " para assinar o Laudo de ITBI como Responsável Pela Comissão Avalidadora";
            }
        }
        if (laudoAvaliacaoITBI.getDiretorChefeDeparTributo() != null
            && (laudoSalvo == null || laudoSalvo.getDiretorChefeDeparTributo() == null
            || (diretoresChefeDepartamentoTributos.contains(laudoSalvo.getDiretorChefeDeparTributo())
            && !laudoAvaliacaoITBI.getDiretorChefeDeparTributo().equals(laudoSalvo.getDiretorChefeDeparTributo())))) {
            if (canAssinar && !laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getPessoa().getId().equals(idPessoaUsuario)) {
                canAssinar = false;
                mensagemValidacao += " para assinar o Laudo de ITBI como Diretor Chefe do Departamento de Tributos";
            } else if (!canAssinar && !laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getPessoa().getId().equals(idPessoaUsuario)) {
                mensagemValidacao += " e como Diretor Chefe do Departamento de Tributos";
            }
        }
        if ((laudoAvaliacaoITBI.getRespComissaoAvaliadora() == null
            || (laudoSalvo != null && laudoSalvo.getRespComissaoAvaliadora() != null
            && laudoAvaliacaoITBI.getRespComissaoAvaliadora().equals(laudoSalvo.getRespComissaoAvaliadora())))
            && (laudoAvaliacaoITBI.getDiretorChefeDeparTributo() == null
            || (laudoSalvo != null && laudoSalvo.getDiretorChefeDeparTributo() != null
            && laudoAvaliacaoITBI.getDiretorChefeDeparTributo().equals(laudoSalvo.getDiretorChefeDeparTributo())))
            && !isUsuarioComPermissao) {
            canAssinar = false;
            mensagemValidacao += " para emitir o Laudo de ITBI";
        }
        if (!isSegundaVia && canAssinar) {
            if (laudoAvaliacaoITBI.getRespComissaoAvaliadora() == null
                && (laudoSalvo == null || laudoSalvo.getRespComissaoAvaliadora() == null)) {
                canAssinar = false;
                mensagemValidacao += " para emitir o laudo de ITBI, pois não está assinado pelo Responsável pela Comissão Avaliadora";
            }
        }
        if (!canAssinar) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagemValidacao + ".");
        }
        ve.lancarException();
    }

    private void atualizarSituacaoAndSalvar() {
        if (!isCancelado()) {
            laudoAvaliacaoITBI = calculoITBIFacade.salvarLaudoAvaliacao(laudoAvaliacaoITBI);
            if (laudoAvaliacaoITBI.getRespComissaoAvaliadora() == null && laudoAvaliacaoITBI.getDiretorChefeDeparTributo() == null) {
                selecionado.setSituacao(SituacaoITBI.EMITIDO);
            } else {
                if (!SituacaoITBI.RETIFICADO.equals(selecionado.getSituacao())) {
                    selecionado.setSituacao(SituacaoITBI.ASSINADO);
                }
            }
            selecionado = calculoITBIFacade.salvarRetornando(selecionado);
        }
    }

    public void confirmarRetificacaoItbi() {
        try {
            validarRetificacao();
            FacesUtil.executaJavaScript("dialogConfirmaRetificacaoItbi.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarRetificacaoLaudo() {
        retificacao = new RetificacaoCalculoITBI();
    }

    private void validarRetificacao() {
        ValidacaoException ve = new ValidacaoException();
        if (StringUtils.isBlank(retificacao.getMotivoRetificacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Motivo da Retificação deve ser informado.");
        }
        ve.lancarException();
    }

    public void abrirDialogCancelamentoLaudo() {
        selecionado.setDataCancelamento(new Date());
        selecionado.setUsuarioCancelamento(Util.recuperarUsuarioCorrente());
        FacesUtil.executaJavaScript("cancelamentoLaudo.show()");
    }

    public void confirmarCancelamentoItbi() {
        try {
            validarCancelamento();
            FacesUtil.executaJavaScript("dialogConfirmaCancelamentoItbi.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCancelamento() {
        ValidacaoException ve = new ValidacaoException();
        if (StringUtils.isBlank(selecionado.getMotivoCancelamento())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Motivo do Cancelamento deve ser informado.");
        }
        ve.lancarException();
    }

    public void cancelarLaudo() {
        try {
            if (!isCancelado()) {
                List<ResultadoParcela> parcelas = montarListaParcelas();
                calculoITBIFacade.cancelarParcelasItbi(parcelas);
                selecionado.setSituacao(SituacaoITBI.CANCELADO);
                calculoITBIFacade.removerProprietariosAtuaisERecuperarAnteriores(selecionado, parcelasCalculoITBI);
                selecionado = calculoITBIFacade.salvarRetornando(selecionado);
                redirecionarParaVisualiza("Cadeia Dominial de ITBI Cancelada com sucesso!");
            } else {
                FacesUtil.addOperacaoNaoPermitida("Esse Lançamento de ITBI já está cancelado!");
            }
        } catch (Exception e) {
            logger.error("Erro ao cancelar Laudo de ITBI. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao cancela Laudo de ITBI. Detalhes: " + e.getMessage());
        }
    }

    public void buscarParcelasDoITBI() {
        parcelasCalculoITBI = Lists.newArrayList();
        Set<ResultadoParcela> parcelasITBI = Sets.newHashSet();

        List<Long> idsCalculos = buscarIdsCalculos();

        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsCalculos);
        consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IN, buscarIdsAdquirentes());

        parcelasITBI.addAll(consulta.executaConsulta().getResultados());

        List<ResultadoParcela> dividasAtivas = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelasITBI) {
            if (parcela.getSituacaoEnumValue().isInscritoDa()) {
                dividasAtivas.add(calculoITBIFacade.buscarParcelaDividaAtivaDaParcelaOriginal(parcela.getIdParcela()));
            }
        }
        parcelasITBI.addAll(dividasAtivas);

        List<ResultadoParcela> parcelamentos = Lists.newArrayList();
        for (ResultadoParcela p : parcelasITBI) {
            if (p.getSituacaoEnumValue().isParcelado()) {
                parcelamentos.addAll(calculoITBIFacade.buscarParcelaDoParcelamentoDaDividaAtiva(p.getIdParcela()));
            }
        }
        parcelasITBI.addAll(parcelamentos);
        buscarDebitosOriginadosDeCompensacao(parcelasITBI);

        ArrayList<ResultadoParcela> parcelas = Lists.newArrayList(parcelasITBI);
        Collections.sort(parcelas);
        for (ResultadoParcela parcela : parcelas) {
            DAMResultadoParcela damParcela = new DAMResultadoParcela();
            for (CalculoITBI calculo : selecionado.getCalculos()) {
                if (parcela.getIdCalculo().equals(calculo.getId())) {
                    damParcela.setNumeroCalculo(calculo.getOrdemCalculo());
                }
            }
            if (damParcela.getNumeroCalculo() == null) damParcela.setNumeroCalculo(1);
            damParcela.setResultadoParcela(parcela);
            damParcela.setDam(recuperarDansParaOLaudo(parcela.getIdParcela()));
            parcelasCalculoITBI.add(damParcela);
        }
        Collections.sort(parcelasCalculoITBI);
    }

    public void definirInformacoesCalculo() {
        calculoITBI = new CalculoITBI();
        isento = false;

        if (selecionado.getCalculos().isEmpty()) {
            if (selecionado.isImobiliario()) {
                for (Propriedade propriedade : cadastroImobiliarioFacade.recuperarProprietariosVigentes(selecionado.getCadastroImobiliario())) {
                    calculoITBI.getTransmitentesCalculo().add(new TransmitentesCalculoITBI(calculoITBI, propriedade.getPessoa(), BigDecimal.valueOf(propriedade.getProporcao())));
                }
            } else {
                for (PropriedadeRural propriedade : cadastroRuralFacade.recuperarPropriedadesVigentes(selecionado.getCadastroRural())) {
                    calculoITBI.getTransmitentesCalculo().add(new TransmitentesCalculoITBI(calculoITBI, propriedade.getPessoa(), BigDecimal.valueOf(propriedade.getProporcao())));
                }
            }
        } else {
            for (PropriedadeSimulacaoITBI propriedadeSimulacaoITBI : selecionado.getUltimoCalculo().getProprietariosSimulacao()) {
                calculoITBI.getTransmitentesCalculo().add(new TransmitentesCalculoITBI(calculoITBI, propriedadeSimulacaoITBI.getPessoa(), BigDecimal.valueOf(propriedadeSimulacaoITBI.getProporcao())));
            }
        }
        if (dadosCadastroITBI != null) {
            calculoITBI.setValorVenal(dadosCadastroITBI.getValorVenal());
        }
        calculoITBI.setOrdemCalculo(selecionado.getCalculos() != null ? (selecionado.getCalculos().size() + 1) : 1);
    }

    public DAM recuperarDansParaOLaudo(Long parcela) {
        return calculoITBIFacade.recuperarDAMPeloIdParcela(parcela);
    }

    private void buscarDebitosOriginadosDeCompensacao(Set<ResultadoParcela> parcelas) {
        Set<ResultadoParcela> compensacoes = Sets.newHashSet();
        for (ResultadoParcela p : parcelas) {
            if (p.getSituacaoEnumValue().isCompensado()) {
                compensacoes.addAll(calculoITBIFacade.buscarParcelasDaCompensacao(p.getIdParcela()));
                buscarDebitosOriginadosDeCompensacao(compensacoes);
            }
        }
        parcelas.addAll(compensacoes);
    }

    public List<Long> buscarIdsCalculos() {
        List<Long> idsCalculos = Lists.newArrayList();
        if (selecionado != null) {
            for (CalculoITBI calculo : selecionado.getCalculos()) {
                idsCalculos.add(calculo.getId());
            }
        }
        return idsCalculos;
    }

    public List<Long> buscarIdsAdquirentes() {
        List<Long> idsAdquirentes = Lists.newArrayList();
        if (selecionado != null && selecionado.getCalculos() != null) {
            for (CalculoITBI calculo : selecionado.getCalculos()) {
                idsAdquirentes.addAll(buscarIdsAdquirentes(calculo));
            }
        }
        return idsAdquirentes;
    }

    public List<Long> buscarIdsAdquirentes(CalculoITBI calculo) {
        List<Long> idsAdquirentes = Lists.newArrayList();
        for (AdquirentesCalculoITBI adquirente : calculo.getAdquirentesCalculo()) {
            idsAdquirentes.add(adquirente.getAdquirente().getId());
        }
        return idsAdquirentes;
    }

    public void prepararParaEnviarEmail() {
        if (!habilitarEmissaoLaudo("enviado")) return;
        emails = null;
        mensagemEmail = null;
        laudoAvaliacaoITBI = calculoITBIFacade.recuperarLaudo(selecionado);
        if (laudoAvaliacaoITBI == null) {
            laudoAvaliacaoITBI = new LaudoAvaliacaoITBI();
            laudoAvaliacaoITBI.setProcessoCalculoITBI(selecionado);

        } else {
            laudoAvaliacaoITBI.setLaudoImpresso(true);
        }
        if (laudoAvaliacaoITBI.getDataImpressaoLaudo() == null) {
            laudoAvaliacaoITBI.setDataImpressaoLaudo(new Date());
            laudoAvaliacaoITBI.setLaudoImpresso(false);
        }
        if (laudoAvaliacaoITBI.getUsuarioImpressaoLaudo() == null) {
            laudoAvaliacaoITBI.setUsuarioImpressaoLaudo(Util.recuperarUsuarioCorrente());
        }
        if (laudoAvaliacaoITBI.getLaudoSegundaViaImpresso()) {
            if (laudoAvaliacaoITBI.getDataImpressao2Via() == null) {
                laudoAvaliacaoITBI.setDataImpressao2Via(new Date());
            }
            if (parametro.getDiasVencimentoSegundaViaItbi() != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(laudoAvaliacaoITBI.getDataImpressao2Via());
                calendar.add(Calendar.DAY_OF_MONTH, parametro.getDiasVencimentoSegundaViaItbi());
                laudoAvaliacaoITBI.setDataVencimento(calendar.getTime());
            }
        } else {
            if (laudoAvaliacaoITBI.getDataVencimento() == null) {
                laudoAvaliacaoITBI.setDataVencimento(calculoITBIFacade.montarDataVencimentoLaudo(parametro,
                    selecionado.getDataLancamento(), laudoAvaliacaoITBI.getDataImpressaoLaudo()));
            }
        }
        carregarEmailsDefaultEnvioITBI();
        FacesUtil.executaJavaScript("listaEmails.show()");
    }

    private void carregarEmailsDefaultEnvioITBI() {
        StringBuilder emailsPorDefault = new StringBuilder();
        String juncao = "";
        CalculoITBI ultimoCalculo = selecionado.getUltimoCalculo();
        for (AdquirentesCalculoITBI adquirente : ultimoCalculo.getAdquirentesCalculo()) {
            if (adquirente.getAdquirente().getEmail() != null && !adquirente.getAdquirente().getEmail().trim().isEmpty()) {
                emailsPorDefault.append(juncao).append(adquirente.getAdquirente().getEmail());
                juncao = ", ";
            }
        }
        for (TransmitentesCalculoITBI transmitente : ultimoCalculo.getTransmitentesCalculo()) {
            if (transmitente.getPessoa().getEmail() != null && !transmitente.getPessoa().getEmail().trim().isEmpty()) {
                emailsPorDefault.append(juncao).append(transmitente.getPessoa().getEmail());
                juncao = ", ";
            }
        }
        this.emails = emailsPorDefault.toString();
    }

    public void enviarITBIPorEmail() {
        try {
            validarPermissaoDeImpressaoLaudo();
            String[] emailsSeparados = validarAndSepararEmails();
            enviarEmailsITBIs(emailsSeparados);
            FacesUtil.executaJavaScript("listaEmails.hide();");
        } catch (AddressException e) {
            FacesUtil.addOperacaoNaoRealizada("O e-mail informado é invalido!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public String[] validarAndSepararEmails() throws AddressException {
        ValidacaoException ve = new ValidacaoException();
        String emailsQuebrados[] = null;
        if (emails == null || emails.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo E-mail é obrigatório.");
        } else {
            emailsQuebrados = emails.split(Pattern.quote(","));
            for (String emailsQuebrado : emailsQuebrados) {
                InternetAddress emailAddr = new InternetAddress(emailsQuebrado);
                emailAddr.validate();
            }
        }
        if (mensagemEmail == null || mensagemEmail.trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mensagem é obrigatório.");
        }
        ve.lancarException();
        return emailsQuebrados;
    }

    public void enviarEmailsITBIs(String[] emailsSeparados) {
        try {
            atualizarSituacaoAndSalvar();
            calculoITBIFacade.gerarLaudoAndEnviarEmail(selecionado, getIdsDams(), emailsSeparados, mensagemEmail);
            FacesUtil.addOperacaoRealizada("E-mail enviado com sucesso!");
            FacesUtil.executaJavaScript("listaEmails.hide()");
        } catch (Exception e) {
            logger.error("Ocorreu um erro ao enviar o e-mail. ", e);
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao enviar o e-mail com o ITBI. Detalhes " + e.getMessage());
        }
    }

    public List<ResultadoParcela> montarListaParcelas() {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        if (parcelasCalculoITBI != null) {
            for (DAMResultadoParcela damParcela : parcelasCalculoITBI) {
                parcelas.add(damParcela.getResultadoParcela());
            }
        }
        return parcelas;
    }

    private void definirIconeBtnParcela() {
        for (int i = 0; i < selecionado.getCalculos().size(); i++) {
            mapaIconeBtnParcela.put(i, "ui-icon ui-icon-circle-triangle-e");
        }
    }

    public String buscarIconeBtnParcela(Integer index) {
        String icone = mapaIconeBtnParcela.get(index);
        return icone != null ? icone : "ui-icon ui-icon-circle-triangle-e";
    }

    private void ordenarCalculos(List<CalculoITBI> calculos, Boolean desc) {
        Collections.sort(calculos, new Comparator<CalculoITBI>() {
            @Override
            public int compare(CalculoITBI o1, CalculoITBI o2) {
                return ComparisonChain.start().compare((desc ? o2 : o1).getOrdemCalculo(), (desc ? o1 : o2).getOrdemCalculo()).result();
            }
        });
    }

    public void verSolicitacaoITBIOnline() {
        FacesUtil.redirecionamentoInterno("/itbi-online/ver/" + selecionado.getSolicitacaoItbiOnline().getId() + "/");
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
}
