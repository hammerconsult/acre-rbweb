/*
 * Codigo gerado automaticamente em Fri Sep 02 15:57:16 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.LazyInitializationException;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@ManagedBean(name = "lancamentoFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLancamentoFP", pattern = "/lancamentofp/novo/", viewId = "/faces/rh/administracaodepagamento/lancamentofp/edita.xhtml"),
    @URLMapping(id = "listarLancamentoFP", pattern = "/lancamentofp/listar/", viewId = "/faces/rh/administracaodepagamento/lancamentofp/lista.xhtml"),
    @URLMapping(id = "correcaoLancamentoFP", pattern = "/lancamentofp/correcao/", viewId = "/faces/rh/administracaodepagamento/lancamentofp/correcao.xhtml"),
    @URLMapping(id = "editarLancamentoFP", pattern = "/lancamentofp/editar/#{lancamentoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/lancamentofp/edita.xhtml"),
    @URLMapping(id = "editarReajustarLancamentoFP", pattern = "/lancamentofp/reajustar/#{lancamentoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/lancamentofp/edita.xhtml"),
    @URLMapping(id = "editarFinalizarLancamentoFP", pattern = "/lancamentofp/finalizar/#{lancamentoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/lancamentofp/finalizarlancamento.xhtml"),
    @URLMapping(id = "verLancamentoFP", pattern = "/lancamentofp/ver/#{lancamentoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/lancamentofp/visualizar.xhtml"),

    @URLMapping(id = "novoLancamentoFP-EConsig", pattern = "/lancamentofp-econsig/novo/", viewId = "/faces/rh/rotinasanuaisemensais/econsig/edita.xhtml"),
    @URLMapping(id = "listarLancamentoFP-EConsig", pattern = "/lancamentofp-econsig/listar/", viewId = "/faces/rh/rotinasanuaisemensais/econsig/lista.xhtml"),
    @URLMapping(id = "editarLancamentoFP-EConsig", pattern = "/lancamentofp-econsig/editar/#{lancamentoFPControlador.id}/", viewId = "/faces/rh/rotinasanuaisemensais/econsig/edita.xhtml"),
    @URLMapping(id = "verLancamentoFP-EConsig", pattern = "/lancamentofp-econsig/ver/#{lancamentoFPControlador.id}/", viewId = "/faces/rh/rotinasanuaisemensais/econsig/visualizar.xhtml"),

    @URLMapping(id = "novoLancamentoFP-lote", pattern = "/lancamentofp-lote/novo/", viewId = "/faces/rh/administracaodepagamento/lancamentofp-lote/edita.xhtml"),
    @URLMapping(id = "listarLancamentoFP-lote", pattern = "/lancamentofp-lote/listar/", viewId = "/faces/rh/administracaodepagamento/lancamentofp-lote/lista.xhtml"),
    @URLMapping(id = "verLancamentoFP-lote", pattern = "/lancamentofp-lote/ver/#{lancamentoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/lancamentofp-lote/visualizar.xhtml"),

    @URLMapping(id = "importar-lancamentoFP", pattern = "/importar-lancamentofp/novo/", viewId = "/faces/rh/administracaodepagamento/lancamentofp/importarlancamentofp.xhtml"),

})
public class LancamentoFPControlador extends PrettyControlador<LancamentoFP> implements Serializable, CRUD {

    protected static final Logger log = LoggerFactory.getLogger(LancamentoFPControlador.class);
    public static String CADASTRO_DE_LANCAMENTO_DE_FOLHA_DE_PAGAMENTO = "Cadastro de Lançamento de Folha de Pagamento";
    @EJB
    FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    ArquivoEconsigFacade arquivoEconsigFacade;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private ConverterAutoComplete converterVinculoFP;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private ConverterAutoComplete converterEventoFP;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    private ContratoFP contratoFPFiltro;
    private EventoFP eventoFPFiltro;
    private Date mesAnoFiltro;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private ConverterGenerico exercicioConverter;
    @EJB
    private EsferaGovernoFacade esferaGovernoFacade;
    private ConverterGenerico converterEsferaGoverno;
    private LoteProcessamento loteProcessamento;
    @EJB
    private LoteProcessamentoFacade loteProcessamentoFacade;
    private String caminhoPadrao = "/lancamentofp/";
    private Mes mesLote;
    private Integer anoLote;
    private ItemBaseFP itemBaseFP;
    @EJB
    private BaseFPFacade baseFPFacade;
    private LancamentoFP original;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private PercentualConverter percentualConverter;
    private FileUploadEvent fileUploadEvent;
    private List<FileUploadEvent> fileUploadEvents;
    private List<LancamentoFP> lancamentosImportados;
    private List<LancamentoFP> lancamentosImportadosComErros;
    private List<LancamentoFP> listaLancamentosImportados;
    private MoneyConverter converterMoney;
    private Integer mes;
    private Integer ano;
    @EJB
    private VinculosFPPermitidosPorUsuarioFacade vinculosFPPermitidosPorUsuarioFacade;

    private VinculoFP vinculoFP;
    private VinculoFP outroVinculoFP;

    private Date dataCorrecao;
    private List<LancamentoFP> duplicados;
    @EJB
    private ComparadorDeFollhasFacade comparadorFolhasFacade;
    private boolean fluxoVindoDaTelaDeConsigacao;
    private List<LancamentoFP> excluidos;

    public LancamentoFPControlador() {
        super(LancamentoFP.class);
    }

    public Date getDataCorrecao() {
        return dataCorrecao;
    }

    public void setDataCorrecao(Date dataCorrecao) {
        this.dataCorrecao = dataCorrecao;
    }

    public List<LancamentoFP> getDuplicados() {
        return duplicados;
    }

    public void setDuplicados(List<LancamentoFP> duplicados) {
        this.duplicados = duplicados;
    }

    public VinculoFP getOutroVinculoFP() {
        return outroVinculoFP;
    }

    public void setOutroVinculoFP(VinculoFP outroVinculoFP) {
        this.outroVinculoFP = outroVinculoFP;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Mes getMesLote() {
        return mesLote;
    }

    public void setMesLote(Mes mesLote) {
        this.mesLote = mesLote;
    }

    public Integer getAnoLote() {
        return anoLote;
    }

    public void setAnoLote(Integer anoLote) {
        this.anoLote = anoLote;
    }

    public List<LoteProcessamento> completarLoteProcessamento(String parte) {
        return loteProcessamentoFacade.listaFiltrandoMesAno(mesLote, anoLote, parte.trim());
    }

    public LoteProcessamento getLoteProcessamento() {
        return loteProcessamento;
    }

    public void setLoteProcessamento(LoteProcessamento loteProcessamento) {
        this.loteProcessamento = loteProcessamento;
    }

    public LancamentoFPFacade getFacade() {
        return lancamentoFPFacade;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public List<LancamentoFP> getListaLancamentosImportados() {
        return listaLancamentosImportados;
    }

    public void setListaLancamentosImportados(List<LancamentoFP> listaLancamentosImportados) {
        this.listaLancamentosImportados = listaLancamentosImportados;
    }

    @Override
    public AbstractFacade getFacede() {
        return lancamentoFPFacade;
    }

    public List<VinculoFP> vinculoFPs(String parte) {
        return vinculosFPPermitidosPorUsuarioFacade.buscarVinculosPermitidosParaUsuarioLogado(parte.trim(), null);
    }

    public void verificarSituacaoVinculo() {
        if (selecionado != null && selecionado.getVinculoFP() != null && !(selecionado.getVinculoFP() instanceof ContratoFP)) {
            String mensagem = "ATENÇÃO! O vinculo selecionado é de servidor ";
            if (selecionado.getVinculoFP() instanceof Aposentadoria) mensagem += "APOSENTADO(A)";
            if (selecionado.getVinculoFP() instanceof Pensionista) mensagem += "PENSIONISTA";
            if (selecionado.getVinculoFP() instanceof Beneficiario) mensagem += "BENEFICIÁRIO";
            FacesUtil.addAtencao(mensagem);
        }
    }

    @URLAction(mappingId = "listarLancamentoFP-lote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        definirCaminhoDoLote();
    }

    @Override
    @URLAction(mappingId = "verLancamentoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarLancamentoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        mes = DataUtil.getMes(selecionado.getMesAnoInicial());
        ano = DataUtil.getAno(selecionado.getMesAnoInicial());
        definirDataCadastroConsignaveis();
        itemBaseFP = new ItemBaseFP();
        reloadBaseFP();
        fluxoVindoDaTelaDeConsigacao = false;
        definirNomeOriginalDoPainel();

//        original = (LancamentoFP) Util.clonarObjeto(selecionado);
    }

    private void definirDataCadastroConsignaveis() {
        if (selecionado.getDataCadastroEconsig() == null) {
            selecionado.setDataCadastroEconsig(sistemaFacade.getDataOperacao());
        }
    }

    private void definirNovasDatasSelecionado() {
        Date novoInicio = DataUtil.adicionaDias(selecionado.getMesAnoFinal(), 1);
        Date novoFim = DataUtil.adicionarMeses(novoInicio, 1);

        selecionado.setMesAnoInicial(novoInicio);
        selecionado.setMesAnoFinal(novoFim);
    }

    @URLAction(mappingId = "editarReajustarLancamentoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarReajustar() {
        super.editar();
        itemBaseFP = new ItemBaseFP();
        reloadBaseFP();
        original = (LancamentoFP) Util.clonarObjeto(selecionado);
        definirNovasDatasSelecionado();

    }

    @URLAction(mappingId = "editarFinalizarLancamentoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarFinalizar() {
        super.editar();
        itemBaseFP = new ItemBaseFP();
        reloadBaseFP();
    }

    @URLAction(mappingId = "verLancamentoFP-lote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verLote() {
        definirCaminhoDoLote();
        super.ver();
        reloadBaseFP();

    }

    public void reloadBaseFP() {
        if (selecionado.getBaseFP() != null && selecionado.getBaseFP().getId() != null) {
            selecionado.setBaseFP(baseFPFacade.recuperar(selecionado.getBaseFP().getId()));
        }
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarCampos();
            if (Operacoes.NOVO.equals(operacao)) {
                validarAtoLegal(selecionado);
            }
            verificarExistente(selecionado);
            podeLancarEventoEmFerias();
            validarLancamentoReajustado();
            validarValorMaximoPermitidoParaEventoFP();
            if (operacao == Operacoes.NOVO) {
                if (lancamentoFPFacade.validaLancamento(selecionado)) {
                    FacesUtil.addOperacaoNaoPermitida("Já existe um lançamento com os mesmos dados abaixo!");
                    return;
                }
                getFacede().salvarNovo(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
                FacesUtil.redirecionamentoInterno("/lancamentofp/novo/");
            } else {
                if (original != null) { //se original diferente de null, então está acontecendo um finalização de lançamento e criação de um novo.
                    if (lancamentoFPFacade.validarLancamentoReajuste(selecionado, original)) {
                        FacesUtil.addOperacaoNaoPermitida("Já existe um outro lançamento no mesmo período listado abaixo!");
                        return;
                    }
                    lancamentoFPFacade.salvarAlteracaoLancamentoFp(original, selecionado);
                    FacesUtil.addOperacaoRealizada("Reajuste do Lançamento Realizado com Sucesso.");
                    redireciona();
                } else {
                    super.salvar();
                }
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            return;
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getVinculoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarValorMaximoPermitidoParaEventoFP() {
        ValidacaoException val = new ValidacaoException();
        if (selecionado.getEventoFP() != null && selecionado.getEventoFP().getValorMaximoLancamento() != null) {
            if (selecionado.getEventoFP().getValorMaximoLancamento().compareTo(selecionado.getQuantificacao()) < 0) {
                val.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Valor lançado maior que o permitido para essa Verba. Valor Lançado: " + selecionado.getQuantificacao() + " - Valor máximo permitido: " + selecionado.getEventoFP().getValorMaximoLancamento());
                throw val;
            }
        }
    }

    private void validarLancamentoReajustado() throws ValidacaoException {
        if (original == null) {
            return;
        }
        verificarDatas(original.getMesAnoInicial(), original.getMesAnoFinal(), selecionado.getMesAnoInicial(), selecionado.getMesAnoFinal());
    }

    public void verificarDatas(Date originalInicio, Date originalFim, Date inicio, Date fim) {
        ValidacaoException ve = new ValidacaoException();
        DateTime originalIni = new DateTime(originalInicio), originalFi = new DateTime(originalFim), inicioSelecionado = new DateTime(inicio), fimSelecionado = new DateTime(fim);
        if (inicioSelecionado.isBefore(originalIni)) {
            mostrarInformacoes(originalInicio, originalFim, inicio, fim);
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data de Início do Lançamento está menor que o Lançamento a ser reajustado!");
        }
        if (fimSelecionado.isBefore(originalIni)) {
            mostrarInformacoes(originalInicio, originalFim, inicio, fim);
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data de Término do Lançamento está menor que o Lançamento a ser reajustado!");
        }

        if (inicioSelecionado.isAfter(originalFi.plusDays(1))) {
            mostrarInformacoes(originalInicio, originalFim, inicio, fim);
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de início do novo lançamento não deve ser superior a data de término do lançamento original!");
        }
        ve.lancarException();
    }

    public void mostrarInformacoes(Date originalInicio, Date originalFim, Date inicio, Date fim) {
        FacesUtil.addOperacaoNaoPermitida("Seguem as informações digitadas para conferência: <br />");
        FacesUtil.addOperacaoNaoPermitida("Lancamento Original: " + originalInicio + " - " + originalFim);
        FacesUtil.addOperacaoNaoPermitida("Lancamento Novo....: " + inicio + " - " + fim);
    }

    public void salvarImportacaoLancamentoFP() {
        try {
            if (podeSalvarImportacaoLancamento()) {
                lancamentoFPFacade.salvarLancamentoVinculoDaImportacao(lancamentosImportados);
                FacesUtil.redirecionamentoInterno("/importar-lancamentofp/novo/");
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }

    public void excluirImportacaoLancamentoFP() {
        try {
            if (verificaCompetenciaEfetivada()) {
                List<LancamentoFP> lancamentosMarcadosParaRemocao = new ArrayList<>();
                for (LancamentoFP lancamento : listaLancamentosImportados) {
                    if (lancamento.getSelecionado()) {
                        lancamentosMarcadosParaRemocao.add(lancamento);
                    }
                }

                for (LancamentoFP lancamento : lancamentosMarcadosParaRemocao) {
                    if (listaLancamentosImportados.contains(lancamento)) {
                        listaLancamentosImportados.remove(lancamento);
                    }
                }

                lancamentoFPFacade.excluirLancamentosSelecionadosDeImportacao(lancamentosMarcadosParaRemocao);
                FacesUtil.addOperacaoRealizada("Registro excluído com sucesso.");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }

    private boolean podeSalvarImportacaoLancamento() {
        if (selecionado.getAtoLegal() == null) {
            FacesUtil.addCampoObrigatorio("Campo ato legal é obrigatório.");
            return false;
        }
        if (selecionado.getMesAnoInicial() == null) {
            FacesUtil.addCampoObrigatorio("Campo data inicial é obrigatório.");
            return false;
        }
        if (selecionado.getMesAnoFinal() == null) {
            FacesUtil.addCampoObrigatorio("Campo data final é obrigatório.");
            return false;
        }
        return true;
    }

    public void salvarPorLote() {
        try {
            validarLote();
            List<MensagemValidacao> mensagens = new LinkedList<>();
            lancamentoFPFacade.salvar(selecionado, loteProcessamento, mensagens);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            if (!mensagens.isEmpty()) {
                FacesUtil.printAllMessages(mensagens);
            }
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            return;
        }
    }

    public void validarLote() {
        ValidacaoException ve = new ValidacaoException();
        if (loteProcessamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Lote deve ser informado!");
        }
        if (selecionado.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal deve ser informado!");
        }
        Util.validarCampos(selecionado);
        verificarExistente(selecionado);
        ve.lancarException();
    }

    public Converter getMoneyConverter() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String str) {
                if (str == null || str.toString().trim().equals("")) {
                    return new BigDecimal("0.00");
                } else {
                    str = str.replaceAll(Pattern.quote("."), "");
                    try {
                        str = str.replaceAll(Pattern.quote(","), ".");
                        BigDecimal valor = new BigDecimal(str);
                        return valor;
                    } catch (Exception e) {
                        return new BigDecimal("0.00");
                    }
                }
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object obj) {
                String s = null;
                if (obj == null || obj.toString().trim().equals("")) {
                    return "0,00";
                } else {
                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(3);
                    String saida = String.valueOf(obj).replaceAll("\\.", ",");
                    return saida;
                }
            }
        };
    }

    public void carregarLancamentoParaReajustar(BigDecimal identificador) {
        LancamentoFP lancamentoFP = lancamentoFPFacade.recuperar(identificador.longValue());
        FacesUtil.redirecionamentoInterno(caminhoPadrao + "reajustar/" + lancamentoFP.getId() + "/");
    }

    public void carregarLancamentoParaFinalizar(BigDecimal identificador) {
        LancamentoFP lancamentoFP = lancamentoFPFacade.recuperar(identificador.longValue());
        FacesUtil.redirecionamentoInterno(caminhoPadrao + "finalizar/" + lancamentoFP.getId() + "/");
    }

    public ConverterAutoComplete getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        return new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
    }

    public ConverterAutoComplete getLoteProcessamentoConverter() {
        return new ConverterAutoComplete(LoteProcessamento.class, loteProcessamentoFacade);
    }

    public List<EventoFP> eventoFPs(String parte) {
        return eventoFPFacade.buscarEventosAtivosAndPermiteLancamento(parte.trim());
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public List<SelectItem> getTipoLancamentoFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoLancamentoFP object : TipoLancamentoFP.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoImportacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoImportacao object : TipoImportacao.values()) {
            if (!object.equals(TipoImportacao.ARQUIVO)) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public Converter getConverterMesAno() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                try {
                    if (value != null && !value.equals("")) {

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date dataMesAnoFinal = new Date();
                        value = "01/" + value;
                        dataMesAnoFinal = sdf.parse(value);
                        Calendar c = GregorianCalendar.getInstance();
                        c.setTime(dataMesAnoFinal);
                        Integer ultimoDiaMes = c.getActualMinimum(Calendar.DAY_OF_MONTH);
                        c.set(Calendar.DAY_OF_MONTH, ultimoDiaMes);
                        return c.getTime();

                    }
                } catch (Exception ex) {
                    log.debug(ex + "");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro do conversão de data, favor insira um valor aceitável"));
                    return null;
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataMesAnoFinal = sdf.format(value);
                StringBuilder builder = new StringBuilder(dataMesAnoFinal);
                builder.replace(0, 3, "");
                return builder.toString();
            }
        };
    }

    private void validarAtoLegal(LancamentoFP l) {
        ValidacaoException ve = new ValidacaoException();
        AtoLegal ato = l.getAtoLegal();
        if (ato.getNumero() == null || ato.getNumero().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Ato Legal é obrigatório!");
        }
        if (ato.getTipoAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Ato é obrigatório!");
        }
        if (!operacao.equals(Operacoes.EDITAR)) {
            if (ato.getUnidadeOrganizacional() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário estar logado em uma Unidade Organizacional");
            }
        }
        if (ato.getEsferaGoverno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Esfera de Governo é obrigatório!");
        }
        if (ato.getDataEmissao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Emissão é obrigatório!");
        }
        if (ato.getDataPublicacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Publicação é obrigatório!");
        }
        ve.lancarException();
    }

    private void verificarExistente(LancamentoFP l) throws NumberFormatException {
        ValidacaoException ve = new ValidacaoException();
        if (!l.getMesAnoInicial().equals("") && !l.getMesAnoFinal().equals("") && !l.getMesAnoInicioCalculo().equals("")) {
            if (selecionado.isValor() && l.getQuantificacao() != null && l.getQuantificacao().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantificação deve ser um número positivo!");
            }
            if (l.getMesAnoInicial().after(l.getMesAnoFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data Inicial é maior que a data final!");
            }
            if (l.getMesAnoInicioCalculo().after(l.getMesAnoInicial())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("LançamentoFP não permitido para o período selecionado, o mês/ano do Início do Cálculo difere do mês/ano da Data Inicial!");
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor, informe todos os campos obrigatórios para prosseguir com o processo!");
        }
        ve.lancarException();
    }

    @Override
    public String getCaminhoPadrao() {
        return caminhoPadrao;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoLancamentoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        LancamentoFP lancamentoFP = ((LancamentoFP) selecionado);
        ((LancamentoFP) selecionado).setDataCadastro(UtilRH.getDataOperacao());
        definirDataCadastroConsignaveis();
        ((LancamentoFP) selecionado).setProporcionalizar(Boolean.TRUE);
        selecionado.setAtoLegal(new AtoLegal());
        itemBaseFP = new ItemBaseFP();
        inicializaParametrosAto(lancamentoFP);
        inicializaExercicioAtoLegal();
        fluxoVindoDaTelaDeConsigacao = false;
        definirNomeOriginalDoPainel();
        setDataInicialEDataFinal(lancamentoFP);
    }

    public void setDataInicialEDataFinal(LancamentoFP lancamentoFP) {
        LocalDate hoje = LocalDate.now();
        LocalDate primeiroDiaDoMes = hoje.withDayOfMonth(1);
        lancamentoFP.setMesAnoInicial(java.sql.Date.valueOf(primeiroDiaDoMes));
        LocalDate ultimoDiaDoMes = hoje.withDayOfMonth(hoje.lengthOfMonth());
        lancamentoFP.setMesAnoFinal(java.sql.Date.valueOf(ultimoDiaDoMes));
    }

    @URLAction(mappingId = "novoLancamentoFP-EConsig", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEconsig() {
        novo();
        selecionado.setTipoImportacao(TipoImportacao.ECONSIG);
        selecionado.setDataCadastroEconsig(sistemaFacade.getDataOperacao());
        inicializarAtributosVindosDeConsignacao();
    }

    @URLAction(mappingId = "editarLancamentoFP-EConsig", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEconsig() {
        editar();
        inicializarAtributosVindosDeConsignacao();
    }

    @URLAction(mappingId = "verLancamentoFP-EConsig", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEconsig() {
        ver();
        inicializarAtributosVindosDeConsignacao();
    }

    private void inicializarAtributosVindosDeConsignacao() {
        fluxoVindoDaTelaDeConsigacao = true;
        CADASTRO_DE_LANCAMENTO_DE_FOLHA_DE_PAGAMENTO += " E-Consig";
        definirCaminhoEconsig();
    }

    public void definirNomeOriginalDoPainel() {
        CADASTRO_DE_LANCAMENTO_DE_FOLHA_DE_PAGAMENTO = "Cadastro de Lançamento de Folha de Pagamento";
    }

    @URLAction(mappingId = "novoLancamentoFP-lote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoLote() {
        definirCaminhoDoLote();
        loteProcessamento = null;
        itemBaseFP = new ItemBaseFP();
        mesLote = Mes.getMesToInt(DataUtil.getMes(UtilRH.getDataOperacao()));
        anoLote = UtilRH.getExercicio().getAno();
        novo();
        selecionado.setAtoLegal(null);
    }

    private void definirCaminhoDoLote() {
        caminhoPadrao = "/lancamentofp-lote/";
    }

    private void definirCaminhoEconsig() {
        caminhoPadrao = "/lancamentofp-econsig/";
    }

    @URLAction(mappingId = "importar-lancamentoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoImportarLancamentofp() {
        definirCaminhoLancamento();
        super.novo();
        lancamentosImportados = new LinkedList<>();
        lancamentosImportadosComErros = new LinkedList<>();
    }

    private void definirCaminhoLancamento() {
        caminhoPadrao = "/importar-lancamentoFP/";
    }

    private void inicializaParametrosAto(LancamentoFP lancamentoFP) {
        lancamentoFP.getAtoLegal().setPropositoAtoLegal(PropositoAtoLegal.ATO_DE_PESSOAL);
        EsferaGoverno esfera = esferaGovernoFacade.findEsferaGovernoByNome("Municipal");
        verificaEDefineUnidadeDoAto();
        if (esfera != null) {
            lancamentoFP.getAtoLegal().setEsferaGoverno(esfera);
        }
    }

    public void inicializaExercicioAtoLegal() {
        Exercicio e = exercicioFacade.getExercicioCorrente();
        if (e != null) {
            selecionado.getAtoLegal().setExercicio(e);
        }
    }

    private void verificaEDefineUnidadeDoAto() {
        UnidadeOrganizacional un = getUnidadeOrgaoUnidadeOrganizacionalLogado();
        if (un != null) {
            selecionado.getAtoLegal().setUnidadeOrganizacional(un);
        }
    }

    public UnidadeOrganizacional getUnidadeOrgaoUnidadeOrganizacionalLogado() {
        UnidadeOrganizacional unidadeOrganizacional = sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente();
        if (unidadeOrganizacional != null) {
            HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), unidadeOrganizacional, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            if (ho != null) {
                HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.getHierarquiaAdministrativaPorOrgaoAndTipoAdministrativa(ho.getCodigoDo2NivelDeHierarquia());
                if (orgao != null) {
                    return orgao.getSubordinada();
                } else {
                    FacesUtil.addError("Atenção", "Não foi possível localizar o orgão da unidade administrativa corrente.");

                }
            } else {
                FacesUtil.addError("Atenção", "Não foi possível localizar o orgão da unidade administrativa corrente.");
            }
        }
        return null;
    }

    public List<SelectItem> getEsferaGovernoSelect() {
        return Util.getListSelectItem(esferaGovernoFacade.lista());
    }

    public ConverterGenerico getConverterEsferaGoverno() {
        if (converterEsferaGoverno == null) {
            converterEsferaGoverno = new ConverterGenerico(EsferaGoverno.class, esferaGovernoFacade);
        }
        return converterEsferaGoverno;
    }

    public void buscarAto() {
        try {
            AtoLegal a = getLancamentoFP().getAtoLegal();
            validaCamposAto(a);
            AtoLegal ato = atoLegalFacade.findAtoByAnoAndTipoAndNumeroAndUnidadeOrganizacional(a.getNumero(), a.getExercicio().getAno(), a.getTipoAtoLegal(), a.getUnidadeOrganizacional());
            if (ato != null) {
                getLancamentoFP().setAtoLegal(ato);
            } else {
                getLancamentoFP().setAtoLegal(new AtoLegal());
                inicializaParametrosAto(getLancamentoFP());
                getLancamentoFP().getAtoLegal().setNumero(a.getNumero());
                getLancamentoFP().getAtoLegal().setExercicio(a.getExercicio());
                getLancamentoFP().getAtoLegal().setTipoAtoLegal(a.getTipoAtoLegal());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validaCamposAto(AtoLegal ato) {
        ValidacaoException ve = new ValidacaoException();
        if (ato.getNumero() == null || (ato.getNumero() != null && ato.getNumero().equals(""))) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Ato Legal é obrigatório!");
        }
        if (ato.getExercicio() == null) {
            return;
        }
        if (ato.getUnidadeOrganizacional() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione uma unidade na barra superior do sistema!");
        }
        if (ato.getTipoAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Preencha o campo Tipo de Ato Legal!");
        }
        ve.lancarException();
    }

    public LancamentoFP getLancamentoFP() {
        return (LancamentoFP) selecionado;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte.trim(), PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    public List<VinculoFP> completaContratoFiltro(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosMatriculaFP(s.trim());
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public ContratoFP getContratoFPFiltro() {
        return contratoFPFiltro;
    }

    public void setContratoFPFiltro(ContratoFP contratoFPFiltro) {
        this.contratoFPFiltro = contratoFPFiltro;
    }

    public EventoFP getEventoFPFiltro() {
        return eventoFPFiltro;
    }

    public void setEventoFPFiltro(EventoFP eventoFPFiltro) {
        this.eventoFPFiltro = eventoFPFiltro;
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivosEPermiteLancamento(parte.trim());
    }

    public Date getMesAnoFiltro() {
        return mesAnoFiltro;
    }

    public void setMesAnoFiltro(Date mesAnoFiltro) {
        this.mesAnoFiltro = mesAnoFiltro;
    }

    public void alteraEventoFP(SelectEvent event) {
        EventoFP eventoFP = (EventoFP) event.getObject();
//        LancamentoFP lancamentoFP = (LancamentoFP) selecionado;

        if (eventoFP != null) {
            selecionado.setTipoLancamentoFP(eventoFP.getTipoLancamentoFP() != null ? eventoFP.getTipoLancamentoFP() : TipoLancamentoFP.VALOR);
            selecionado.setQuantificacao(eventoFP.getQuantificacao());
        } else {
            selecionado.setTipoLancamentoFP(null);
            selecionado.setQuantificacao(null);
        }
        //TODO retirado por solicitação de rio branco, diz que é burocrático!!!
//        lancamentoFPFacade.definirDatasNovoLancamento(lancamentoFP);
    }

    public void atribuiMesAnoInicioCalculo() {
        LancamentoFP lancamentoFP = (LancamentoFP) selecionado;
        lancamentoFP.setMesAnoInicioCalculo(lancamentoFP.getMesAnoInicial());
        isValidaDataFinalizacaoNaoPrenchida();
        aplicarDataFinalAtravesNumeroParcelas();
    }

    public boolean isValidaDataFinalizacaoNaoPrenchida() {
        if (selecionado.getMesAnoInicial() != null) {
            if (original != null) {
                if (new DateTime(selecionado.getMesAnoInicial()).isBefore(new DateTime(original.getMesAnoInicial())) || (new DateTime(selecionado.getMesAnoInicial()).getMonthOfYear() == new DateTime(original.getMesAnoInicial()).getMonthOfYear() && new DateTime(selecionado.getMesAnoInicial()).getYear() == new DateTime(original.getMesAnoInicial()).getYear())) {
                    FacesUtil.addError("Atenção ", "A nova data de início de Lançamento deve ser maior que a data do antigo lançamento.");
                    selecionado.setMesAnoInicial(null);
                    selecionado.setMesAnoInicioCalculo(selecionado.getMesAnoInicial());
                    return true;
                }
            }
        }
        return false;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Mes m : Mes.values()) {
            toReturn.add(new SelectItem(m, m.getDescricao()));
        }
        return toReturn;
    }

    public LancamentoFP getOriginal() {
        return original;
    }

    public void setOriginal(LancamentoFP original) {
        this.original = original;
    }

    public boolean isBloqueia() {
        return original != null;
    }

    public String getDescricaoPanel() {
        if (operacao.equals(Operacoes.NOVO)) {
            return CADASTRO_DE_LANCAMENTO_DE_FOLHA_DE_PAGAMENTO;
        } else if (operacao.equals(Operacoes.EDITAR) && original != null) {
            return "Finalização e Criação de Novo Lançamento de Folha de Pagamento";

        } else if (operacao.equals(Operacoes.EDITAR)) {
            return "Alteração de Lançamento de Folha de Pagamento";
        }
        return CADASTRO_DE_LANCAMENTO_DE_FOLHA_DE_PAGAMENTO;
    }

    public List<SelectItem> recuperarAnos() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Exercicio e : exercicioFacade.getExerciciosAtualPassados()) {
            toReturn.add(new SelectItem(e.getAno(), "" + e.getAno()));
        }
        return toReturn;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Exercicio e : exercicioFacade.getExerciciosAtualPassados()) {
            toReturn.add(new SelectItem(e, "" + e.getAno()));
        }
        return toReturn;
    }

    public ConverterGenerico getExercicioConverter() {
        if (exercicioConverter == null) {
            exercicioConverter = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return exercicioConverter;
    }

    public ItemBaseFP getItemBaseFP() {
        return itemBaseFP;
    }

    public void setItemBaseFP(ItemBaseFP itemBaseFP) {
        this.itemBaseFP = itemBaseFP;
    }

    public void addItemBaseFP() {
        if (validaBase()) {
            for (ItemBaseFP item : selecionado.getBaseFP().getItensBasesFPs()) {
                if ((item.getEventoFP().equals(itemBaseFP.getEventoFP()))
                    && (item.getOperacaoFormula().equals(itemBaseFP.getOperacaoFormula()))) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Já existe um item semelhante ao qual está sendo cadastrado !", "Já existe um item semelhante ao qual está sendo cadastrado !");
                    FacesContext.getCurrentInstance().addMessage("msgs", msg);
                    return;
                }
            }
            BaseFP bfp = selecionado.getBaseFP();
            itemBaseFP.setBaseFP(bfp);
            bfp.getItensBasesFPs().add(itemBaseFP);
            Collections.sort(bfp.getItensBasesFPs());
            itemBaseFP = new ItemBaseFP();
        }
    }

    private boolean validaBase() {
        if (selecionado.getBaseFP() != null) {
            if (selecionado.getBaseFP().getCodigo().equals("")) {
                FacesUtil.addWarn("Atenção", "Preencha o codigo da base.");
                return false;
            }
            if (selecionado.getBaseFP().getDescricao().equals("")) {
                FacesUtil.addWarn("Atenção", "Preencha a descrição da base.");
                return false;
            }
        }
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    public void cancelarBaseFP() {
        if (selecionado.getBaseFP() != null) {
            if (Strings.isNullOrEmpty(selecionado.getBaseFP().getCodigo())
                || Strings.isNullOrEmpty(selecionado.getBaseFP().getDescricao())
                || selecionado.getBaseFP().getItensBasesFPs().isEmpty()) {
                selecionado.setBaseFP(null);
            }
        }
    }

    public void confirmarBaseFP() {
        try {
            validarBaseFP();
            FacesUtil.executaJavaScript("dialogBaseFolhaPagamento.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarBaseFP() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getBaseFP() != null) {
            if (Strings.isNullOrEmpty(selecionado.getBaseFP().getCodigo())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo código é obrigatório.");
            }
            if (Strings.isNullOrEmpty(selecionado.getBaseFP().getDescricao())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo descrição é obrigatório.");
            }
            if (selecionado.getBaseFP().getItensBasesFPs() == null || selecionado.getBaseFP().getItensBasesFPs().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione ao menos um Item da Base Folha de Pagamento.");
            }
        }
        ve.lancarException();

    }

    public void removeItemBaseFP(ItemBaseFP e) {
        selecionado.getBaseFP().getItensBasesFPs().remove(e);
    }

    public void instanciaBaseFP() {
        if (selecionado.getBaseFP() == null) {
            selecionado.setBaseFP(getBaseFPLancamento());
            buscarUltimoCodigo();
        } else {
            try {
                selecionado.getBaseFP().getItensBasesFPs().size();
            } catch (LazyInitializationException lz) {
                selecionado.setBaseFP(baseFPFacade.recuperar(selecionado.getBaseFP()));
            }
        }
    }

    public void buscarBaseFP() {
        if (selecionado.getBaseFP() != null && !selecionado.getBaseFP().getCodigo().equals("")) {
            BaseFP b = baseFPFacade.getBaseByCodigoEFiltro(selecionado.getBaseFP().getCodigo().trim(), FiltroBaseFP.LANCAMENTOFP);
            if (b != null) {
                selecionado.setBaseFP(b);
            } else {
                if (selecionado.getBaseFP() != null && !operacao.equals(Operacoes.EDITAR))
                    if (baseFPFacade.existeCodigo(selecionado.getBaseFP().getCodigo())) {
                        FacesUtil.addWarn("Atenção", "Este código não pode ser utilizado");
                        selecionado.getBaseFP().setCodigo("");
                    }

            }
        }
    }

    private void buscarUltimoCodigo() {
        if (selecionado.getBaseFP() != null) {
            String b = baseFPFacade.getCodigoBaseFP();
            if (b != null) {
                selecionado.getBaseFP().setCodigo(b);
            }
        }
    }

    public void aplicarDataFinalAtravesNumeroParcelas() {
        if (selecionado.getNumeroParcelas() != null && selecionado.getNumeroParcelas() > 0 && selecionado.getMesAnoInicial() != null) {

            DateTime dt = new DateTime(selecionado.getMesAnoInicial());
            dt = dt.plusMonths(selecionado.getNumeroParcelas() - 1);
            dt = dt.withDayOfMonth(dt.dayOfMonth().getMaximumValue());
            selecionado.setMesAnoFinal(dt.toDate());

        }
    }

    public void aplicarNumeroParcelaAtravesDaDataFinal() {
        if (selecionado.getMesAnoInicial() != null && selecionado.getMesAnoFinal() != null) {
            try {
                Months quantidadeMeses = Months.monthsBetween(new DateTime(selecionado.getMesAnoInicial()), new DateTime(selecionado.getMesAnoFinal()));
                selecionado.setNumeroParcelas(quantidadeMeses.getMonths() + 1);

                logger.debug("quantidade de meses é " + quantidadeMeses.getMonths() + 1);
            } catch (Exception e) {
                logger.error("Sistema não conseguiu determinar o número de parcelas, erro: {}", e.getMessage());
                FacesUtil.addWarn("O sistema não consegiu determinar o número de parcelas", "Detalhes: " + e.getMessage());
            }
        }
    }

    public void podeLancarEventoEmFerias() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMesAnoInicial() != null && selecionado.getVinculoFP() != null && selecionado.getEventoFP() != null) {
            DateTime data = new DateTime(selecionado.getMesAnoInicial());
            boolean estaEmFerias = concessaoFeriasLicencaFacade.isEstaEmFeriasByMesEAno(selecionado.getVinculoFP(), data.getMonthOfYear(), data.getYear());
            if (selecionado.getEventoFP().getBloqueioFerias() != null) {
                if (estaEmFerias && (selecionado.getEventoFP().getBloqueioFerias().equals(BloqueioFerias.NAO_LANCAVEL) || selecionado.getEventoFP().getBloqueioFerias().equals(BloqueioFerias.NAO_CALCULAVEL_NAO_LANCAVEL))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Verba configurada para não receber lançamento FP de servidores que estão gozando férias!");
                }
            }
        }
        ve.lancarException();
    }

    public String getTituloQuantificacao() {
        if (selecionado.getTipoLancamentoFP().equals(TipoLancamentoFP.QUANTIDADE)) {
            return "Informe a quantidade.";
        }
        if (selecionado.getTipoLancamentoFP().equals(TipoLancamentoFP.DIAS)) {
            return "Informe os dias.";
        }
        if (selecionado.getTipoLancamentoFP().equals(TipoLancamentoFP.HORAS)) {
            return "Informe a hora.";
        }
        return "";
    }

    public String getIdCorreto() {
        String form = "Formulario:";
        if (selecionado.getTipoLancamentoFP() != null) {
            if (selecionado.getTipoLancamentoFP().equals(TipoLancamentoFP.QUANTIDADE)) {
                return form + "quantidade";
            }
            if (selecionado.getTipoLancamentoFP().equals(TipoLancamentoFP.REFERENCIA)) {
                return form + "pecentual";
            }
            if (selecionado.getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
                return form + "valor";
            }
        }
        return "";
    }

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    public List<FileUploadEvent> getFileUploadEvents() {
        return fileUploadEvents;
    }

    public List<LancamentoFP> getLancamentosImportados() {
        return lancamentosImportados;
    }

    public void setLancamentosImportados(List<LancamentoFP> lancamentosImportados) {
        this.lancamentosImportados = lancamentosImportados;
    }

    public List<LancamentoFP> getLancamentosImportadosComErros() {
        return lancamentosImportadosComErros;
    }

    public void setLancamentosImportadosComErros(List<LancamentoFP> lancamentosImportadosComErros) {
        this.lancamentosImportadosComErros = lancamentosImportadosComErros;
    }

    public void doUpload(FileUploadEvent fileUploadEvent) throws IOException {
        UploadedFile uploadedFile = fileUploadEvent.getFile();
        String fileNameUploaded = uploadedFile.getFileName();
        long fileSizeUploaded = uploadedFile.getSize();
        String infoAboutFile = "<br/> Arquivo recebido: <b>" + fileNameUploaded + "</b><br/>" + "Tamanho do Arquivo: <b>" + fileSizeUploaded + "</b>";
        processarArquivo(fileUploadEvent.getFile());
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage("Sucesso", infoAboutFile));
    }

    private void processarArquivo(UploadedFile file) throws IOException {

        XSSFWorkbook wb = new XSSFWorkbook(file.getInputstream());
        XSSFSheet ws = wb.getSheet("Plan1");

        int rowNum = ws.getLastRowNum() + 1;
        int colNum = ws.getRow(0).getLastCellNum();
        String[][] data = new String[rowNum][colNum];

        for (int i = 0; i < rowNum; i++) {
            XSSFRow row = ws.getRow(i);
            String matricula = row.getCell(0).toString();
            String contrato = row.getCell(1).toString();
            String nomeServidor = row.getCell(2).toString();
            String codigoEvento = row.getCell(3).toString();
            String valor = row.getCell(4).toString();
            String numeroParcela = row.getCell(5).toString();
            String observacao = row.getCell(6).toString();
            if (!matricula.equals("Matricula")) {
                LancamentoFP lanc = new LancamentoFP();
                VinculoFP vinculo = vinculoFPFacade.recuperarVinculoContratoPorMatriculaENumeroImportacaoLancFP(matricula.replaceAll("\\.0", ""), contrato.replaceAll("\\.0", ""));
                EventoFP evento = eventoFPFacade.recuperaEventoAtivo(codigoEvento.replace(".0", ""), TipoExecucaoEP.FOLHA);
                lanc.setVinculoFP(vinculo);
                lanc.setEventoFP(evento);
                lanc.setAtoLegal(selecionado.getAtoLegal());
                lanc.setMesAnoInicial(selecionado.getMesAnoInicial());
                lanc.setMesAnoFinal(selecionado.getMesAnoFinal());
                lanc.setQuantificacao(BigDecimal.valueOf(getValorConvertido(valor)));
                lanc.setMesAnoInicioCalculo(selecionado.getMesAnoInicial());
                lanc.setTipoLancamentoFP(TipoLancamentoFP.VALOR);
                lanc.setTipoImportacao(TipoImportacao.ARQUIVO);
                lanc.setNumeroParcelas(Integer.parseInt(numeroParcela.replace(".0", "")));
                lanc.setObservacao(observacao);

                if (lanc.isValidoParaImportacao()) {
                    lancamentosImportados.add(lanc);
                } else {
                    lanc.setObservacao(observacao + " - " + " Nome Servidor: " + nomeServidor + " - " + " Nº linha na planilha: " + (i + 1));
                    lancamentosImportadosComErros.add(lanc);
                }
            }
        }
    }

    private double getValorConvertido(String valor) {
        if (valor != null && valor.length() > 0) {
            try {
                return Double.parseDouble(valor.replace("R$", ""));
            } catch (Exception ex) {
                return 0;
            }
        }
        return 0;
    }

    public void recuperarLancamentoFPImportados() {
        if (validarFiltrosVisualizaImportacao()) {
            log.debug("Recuperando...");
            listaLancamentosImportados = lancamentoFPFacade.lancamentosArquivosImportados(mes, ano);
            System.out.println("Lista " + listaLancamentosImportados.size());
            for (LancamentoFP lancamentosImportado : listaLancamentosImportados) {
                lancamentosImportado.setSelecionado(Boolean.FALSE);
            }
        }
    }

    public boolean validarFiltrosVisualizaImportacao() {
        if (mes == null) {
            FacesUtil.addOperacaoNaoPermitida("Campo mês é obrigatório.");
            return false;
        }
        if (ano == null) {
            FacesUtil.addOperacaoNaoPermitida("Campo ano é obrigatório.");
            return false;
        }
        return true;
    }

    public boolean todosLancamentosMarcados() {
        try {

            for (LancamentoFP lancamento : listaLancamentosImportados) {
                if (lancamento.getSelecionado() == null || !lancamento.getSelecionado()) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void marcarTodosLancamentos() {
        for (LancamentoFP lancamento : listaLancamentosImportados) {
            marcarLancamento(lancamento);
        }
    }

    public void marcarLancamento(LancamentoFP lancamento) {
        lancamento.setSelecionado(Boolean.TRUE);
    }

    public void desmarcarTodosLancamentos() {

        for (LancamentoFP lancamento : listaLancamentosImportados) {
            desmarcarLancamento(lancamento);
        }
    }

    public void desmarcarLancamento(LancamentoFP lancamento) {
        lancamento.setSelecionado(Boolean.FALSE);
    }

    private boolean verificaCompetenciaEfetivada() {
        mes = DataUtil.getMes(selecionado.getMesAnoInicial());
        ano = DataUtil.getAno(selecionado.getMesAnoInicial());

        if (lancamentoFPFacade.isPermiteExcluirLancamento(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("Já existe folha de pagamento efetivada para o lançamento.");
            return false;
        }
        return true;
    }

    @Override
    public void excluir() {
        try {
            if (getVerificaVisualizar()) {
                if (verificaCompetenciaEfetivada()) {
                    lancamentoFPFacade.remover(selecionado, mes, ano);
                    FacesUtil.addOperacaoRealizada("Registro excluído com sucesso.");
                    redireciona();
                }
            }
        } catch (Exception e) {
            logger.trace(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }

    public Boolean getVerificaVisualizar() {
        return operacao.equals(Operacoes.VER);
    }

    public BaseFP getBaseFPLancamento() {
        BaseFP b = new BaseFP();
        b.setFiltroBaseFP(FiltroBaseFP.LANCAMENTOFP);
        return b;
    }

    public List<SelectItem> getTipoFolhaDePagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento object : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "correcaoLancamentoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void correcao() {
        dataCorrecao = sistemaFacade.getDataOperacao();
        duplicados = new LinkedList<>();
        excluidos = new ArrayList<>();
    }

    public void procurarDuplicados() {
        DateTime dateTime = new DateTime(dataCorrecao);
        duplicados = lancamentoFPFacade.listaLancamentosDuplicados(dateTime.getMonthOfYear(), dateTime.getYear());
    }

    public List<LancamentoFP> getExcluidos() {
        return excluidos;
    }

    public void setExcluidos(List<LancamentoFP> excluidos) {
        this.excluidos = excluidos;
    }

    public void deletarValoresIguais() {
        Map<VinculoFP, List<LancamentoFP>> lancamentosMap = new HashMap<>();

        if (duplicados != null && !duplicados.isEmpty()) {
            //lancamentosMap = montarMap(duplicados);
            List<LancamentoFP> lancamentos = new ArrayList<>(duplicados);

            for (Iterator<LancamentoFP> itDuplicado = duplicados.iterator(); itDuplicado.hasNext(); ) {
                LancamentoFP duplicado = itDuplicado.next();
                for (LancamentoFP lancamentoFP : lancamentos) {
                    if (!excluidos.contains(duplicado) && !excluidos.contains(lancamentoFP)) {
                        if (!duplicado.equals(lancamentoFP) && duplicado.getVinculoFP().equals(lancamentoFP.getVinculoFP()) && duplicado.getEventoFP().equals(lancamentoFP.getEventoFP())) {
                            removerLancamentoDuplicado(itDuplicado, duplicado, lancamentoFP);
                        }
                    }
                }
            }
        }
    }


    private Map<VinculoFP, List<LancamentoFP>> montarMap(List<LancamentoFP> duplicados) {
        Map<VinculoFP, List<LancamentoFP>> map = new HashMap<>();
        for (Iterator<LancamentoFP> itDuplicado = duplicados.iterator(); itDuplicado.hasNext(); ) {
            LancamentoFP lancamentoFP = itDuplicado.next();
            if (map.containsKey(lancamentoFP.getVinculoFP())) {
                map.get(lancamentoFP.getVinculoFP()).add(lancamentoFP);
                itDuplicado.remove();
            } else {
                List<LancamentoFP> lista = new ArrayList<>();
                lista.add(lancamentoFP);
                map.put(lancamentoFP.getVinculoFP(), lista);
                itDuplicado.remove();
            }
        }
        return map;
    }

    private void removerLancamentoDuplicado(Iterator<LancamentoFP> itDuplicado, LancamentoFP duplicado, LancamentoFP lancamentoFP) {
        if (lancamentoFP.getQuantificacao().equals(duplicado.getQuantificacao())) {
            log.debug("Valores Iguais..." + lancamentoFP.getQuantificacao());
            //itDuplicado.remove();
            //itLancamento.remove();
            excluidos.add(duplicado);
        } else {
            ObjetoPesquisa objetoPesquisa = getObjetoPesquisa(lancamentoFP);
            List<RegistroDB> registros = new LinkedList<>();
            try {
                registros = comparadorFolhasFacade.buscaFichaFinanceiraTurmalinaTodosCamposPorVerbas(objetoPesquisa);
                if (registros != null && !registros.isEmpty()) {
                    tentarEncontrarValorCorreto(itDuplicado, registros, lancamentoFP, duplicado);
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private BigDecimal tentarEncontrarValorCorreto(Iterator<LancamentoFP> itDuplicado, List<RegistroDB> registros, LancamentoFP lancamentoFP, LancamentoFP duplicado) {
        BigDecimal valorPercentual = null;
        if (TipoLancamentoFP.isReferencia(lancamentoFP.getTipoLancamentoFP())) {
            valorPercentual = getRegistro(registros, "PERCFA");
            if (valorPercentual == null) {
                valorPercentual = getRegistro(registros, "QTHRFA");
            }
        } else {
            valorPercentual = getRegistro(registros, "VALORFA");
        }
        if (valorPercentual != null) {
            if (lancamentoFP.getQuantificacao().equals(valorPercentual)) {
                log.debug("match turmalina lancamentoFP:  " + lancamentoFP + " - " + valorPercentual);
                //itDuplicado.remove();
                addExcluidos(duplicado);
            }
            if (duplicado.getQuantificacao().equals(valorPercentual)) {
                log.debug("match turmalina duplicado:  " + duplicado + " - " + valorPercentual);
                //itDuplicado.remove();
                addExcluidos(lancamentoFP);
            }
        }
        return valorPercentual;
    }

    public void addExcluidos(LancamentoFP lancamentoFP) {
        if (!excluidos.contains(lancamentoFP)) {
            excluidos.add(lancamentoFP);
        }
    }

    public BigDecimal getRegistro(List<RegistroDB> registros, String campo) {
        for (RegistroDB registro : registros) {
            return (BigDecimal) registro.getField(campo).getValor();
        }
        return null;
    }

    public DateTime getDataOperacao() {
        return new DateTime(dataCorrecao);
    }

    public ObjetoPesquisa getObjetoPesquisa(LancamentoFP lancamentoFP) {
        DateTime dataOperacao = getDataOperacao();
        ObjetoPesquisa objetoPesquisa = new ObjetoPesquisa();
        objetoPesquisa.setAno(dataOperacao.getYear());
        objetoPesquisa.setMes(dataOperacao.getMonthOfYear());
        objetoPesquisa.setVinculoFP(lancamentoFP.getVinculoFP());
        objetoPesquisa.setTipoFolhaDePagamentoWeb(TipoFolhaDePagamento.NORMAL);
        List<EventoFP> eventos = new ArrayList<>();
        eventos.add(lancamentoFP.getEventoFP());
        objetoPesquisa.setEventosFPList(eventos);
        return objetoPesquisa;
    }

    public void excluirItens() {
        try {
            lancamentoFPFacade.excluirLancamentos(excluidos);
            FacesUtil.addInfo("Sucesso", "Registros excluidos com sucesso.");
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Erro", "Erro ao excluir registros. " + e);
        }

    }

    public boolean isFluxoVindoDaTelaDeConsigacao() {
        return fluxoVindoDaTelaDeConsigacao;
    }

    public void setEdicaoVindoDaTelaDeEconsig(boolean isEdicaoVindoDaTelaDeEconsig) {
        this.fluxoVindoDaTelaDeConsigacao = isEdicaoVindoDaTelaDeEconsig;
    }
}
