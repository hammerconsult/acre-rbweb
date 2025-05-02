/*
 * Codigo gerado automaticamente em Thu Nov 22 14:40:47 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ValidadorValoresOBN600;
import br.com.webpublico.enums.LayoutArquivoBordero;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoBordero;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoarquivo", pattern = "/arquivo-obn600/novo/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/gerararquivo/edita.xhtml"),
    @URLMapping(id = "editararquivo", pattern = "/arquivo-obn600/editar/#{arquivoRemessaBancoControlador.id}/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/gerararquivo/edita.xhtml"),
    @URLMapping(id = "verarquivo", pattern = "/arquivo-obn600/ver/#{arquivoRemessaBancoControlador.id}/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/gerararquivo/visualizar.xhtml"),
    @URLMapping(id = "listararquivo", pattern = "/arquivo-obn600/listar/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/gerararquivo/lista.xhtml")
})
public class ArquivoRemessaBancoControlador extends PrettyControlador<ArquivoRemessaBanco> implements Serializable, CRUD {

    @EJB
    private ArquivoRemessaBancoFacade arquivoRemessaBancoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterBanco;
    private List<Bordero> listaBordero;
    private List<GuiaPagamento> listaGuiasPagamentos;
    private List<GuiaPagamentoExtra> listaGuiasDespesaExtra;
    private Bordero[] arrayBordero;
    private File arquivo;
    private StreamedContent file;
    private Future<StreamedContent> future;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<FileUploadEvent> fileUploadEvents;
    private Date dtInicial;
    private Date dtFinal;
    private SubConta contaFinanceiraIni;
    private SubConta contaFinanceiraFim;
    private ConverterAutoComplete converterContaFinanceiraIni;
    private ConverterAutoComplete converterContaFinanceiraFim;
    private List<HierarquiaOrganizacional> listaUnidades;
    private MoneyConverter moneyConverter;
    private List<Bordero> borderosExclusao;
    private List<ValidadorValoresOBN600> validadores;
    private HierarquiaOrganizacional organizacional;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoObnFacade contratoObnFacade;
    private ConverterAutoComplete converterContrato;
    private ConverterAutoComplete converterHierarquia;
    private BancoObn bancoObn;
    private List<HierarquiaOrganizacional> informativoUnidades;
    @EJB
    private SistemaFacade sistemaFacade;

    public ArquivoRemessaBancoControlador() {
        super(ArquivoRemessaBanco.class);
    }

    public ArquivoRemessaBancoFacade getFacade() {
        return arquivoRemessaBancoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/arquivo-obn600/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return arquivoRemessaBancoFacade;
    }

    @URLAction(mappingId = "novoarquivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataGeracao(new Date());
        iniciarVariaveis();
    }

    @URLAction(mappingId = "verarquivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
        bancoObn = arquivoRemessaBancoFacade.getConfiguracaoArquivoObnFacade().recuperarBancoGeradorArquivo(selecionado.getBanco().getNumeroBanco());
    }

    @URLAction(mappingId = "editararquivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    private void recuperarEditarVer() {
        iniciarVariaveis();
        recuperarValidadores();
        selecionado.setGerarArquivoTeste(Boolean.FALSE);
//        validarDataPagamentoGuias();
    }

    private void iniciarVariaveis() {
        listaBordero = new ArrayList<Bordero>();
        selecionado.setLayoutArquivoBordero(LayoutArquivoBordero.OBN600);
        borderosExclusao = new ArrayList<Bordero>();
        listaGuiasPagamentos = new ArrayList<>();
        listaGuiasDespesaExtra = new ArrayList<>();
        organizacional = new HierarquiaOrganizacional();
        informativoUnidades = Lists.newArrayList();
        limparFiltros();
    }

    public List<Banco> completaBanco(String parte) {
        return arquivoRemessaBancoFacade.getBancoFacade().listaBancoPorCodigoOuNomeNoBordero(parte.trim());
    }

    public void adicionaBorderos() {
        if (arrayBordero.length <= 0) {
            FacesUtil.addOperacaoNaoPermitida(" Selecione uma ou mais ordens bancárias para adicionar ao arquivo bancário.");
            return;
        }
        for (int i = 0; i < arrayBordero.length; i++) {
            ArquivoRemBancoBordero arbb = new ArquivoRemBancoBordero();
            arbb.setArquivoRemessaBanco(selecionado);
            arbb.setBordero(arrayBordero[i]);
            Boolean controle = true;
            for (ArquivoRemBancoBordero arbbSelecionado : selecionado.getArquivoRemBancoBorderos()) {
                if (arbbSelecionado.getBordero().equals(arbb.getBordero())) {
                    FacesUtil.addOperacaoNaoPermitida(" A Ordem Bancária Nº " + arbb.getBordero() + " já foi adicionado ao arquivo bancário.");
                    controle = false;
                }
            }
            if (controle) {
                selecionado.setValorTotalDoc(selecionado.getValorTotalDoc().add(arbb.getBordero().getValor()));
                selecionado.setValorTotalBor(selecionado.getValorTotalBor().add(arbb.getBordero().getValor()));
                selecionado.setQntdDocumento(selecionado.getQntdDocumento() + 1l);
                selecionado.getArquivoRemBancoBorderos().add(arbb);
            }
        }
    }

    public void imprimirOBSelecionadas() throws IOException, JRException {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            String arquivoJasper = "RelatorioOBN600_OrdemBancaria.jasper";
            HashMap parameters = new HashMap();
            parameters.put("MUNICIPIO", "Município de Rio Branco");
            parameters.put("IMAGEM", abstractReport.getCaminhoImagem());
            parameters.put("USER", getNomeUsuarioLogado());
            parameters.put("MODULO", "Financeiro");
            parameters.put("TOTAL_RECURSO_PROPRIO", getValorTotalRecursoProprio());
            parameters.put("TOTAL_OUTRO_RECURSO", getValorTotalOutrasFontes());
            parameters.put("TOTAL_ARQUIVO", getValorTotalArquivo());
            parameters.put("DATA_ARQUIVO", DataUtil.getDataFormatada(selecionado.getDataGeracao()));
            parameters.put("ID_ARQUIVO", selecionado.getId());
            abstractReport.gerarRelatorio(arquivoJasper, parameters);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void baixarOrdemBancaria(ArquivoRemBancoBordero arb) {
        try {
            arquivoRemessaBancoFacade.getBorderoFacade().baixarBordero(arb.getBordero());
            FacesUtil.addOperacaoRealizada(" A baixa da ordem bancária: " + arb.getBordero() + " foi realizada com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void indeferirArquivo() {
        try {
            if (validaIndeferirArquivo()) {
                for (ArquivoRemBancoBordero arb : selecionado.getArquivoRemBancoBorderos()) {
                    arquivoRemessaBancoFacade.getBorderoFacade().indeferirOrdemBancaria(arb.getBordero());
                }
                FacesUtil.addOperacaoRealizada("Arquivo Nº " + selecionado.getNumero() + " teve suas ordens bancárias indeferidas com sucesso.");
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void indeferirOrdemBancaria(ArquivoRemBancoBordero arq) {
        try {
            arquivoRemessaBancoFacade.getBorderoFacade().indeferirOrdemBancaria(arq.getBordero());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }


    public Boolean podeIndeferirOB(ArquivoRemBancoBordero arq) {
        Boolean controle = true;
        if (SituacaoBordero.PAGO.equals(arq.getBordero().getSituacao())
            || SituacaoBordero.INDEFERIDO.equals(arq.getBordero().getSituacao())) {
            controle = false;
        }
        return controle;
    }

    private Boolean validaIndeferirArquivo() {
        Boolean controle = true;
        if (!selecionado.getArquivoRemBancoBorderos().isEmpty()) {
            for (ArquivoRemBancoBordero arb : selecionado.getArquivoRemBancoBorderos()) {
                if (SituacaoBordero.PAGO.equals(arb.getBordero().getSituacao())) {
                    FacesUtil.addOperacaoNaoPermitida("Arquivo Nº " + selecionado.getNumero() + " contém ordem bancária 'Paga'. Portanto seu indeferimento não é possível.");
                    controle = false;
                    break;
                }
            }
        }
        return controle;
    }

    public String getNomeUsuarioLogado() {
        if (getSistemaControlador().getUsuarioCorrente().getPessoaFisica() != null) {
            return getSistemaControlador().getUsuarioCorrente().getPessoaFisica().getNome();
        } else {
            return getSistemaControlador().getUsuarioCorrente().getUsername();
        }
    }

    public boolean desabilitarCampos() {
        if (selecionado.getArquivoRemBancoBorderos().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean podeExcluir() {
        if (selecionado.getArquivoRemBancoBorderos().isEmpty()) {
            return true;
        }
        return false;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    private String filtroUnidade() {
        StringBuilder stb = new StringBuilder();
        StringBuilder idUnidades = new StringBuilder();
        String concat = "";

        if (this.listaUnidades.size() > 0) {
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concat).append(lista.getSubordinada().getId());
                concat = ",";
            }
            stb.append(" and ").append(" B.UNIDADEORGANIZACIONAL_ID IN (").append(idUnidades).append(")");
            logger.debug("buscou unidades específicas ");
        } else {
            for (HierarquiaOrganizacional hierarquiaOrganizacional : contratoObnFacade.buscarHierarquiaPorContratoAndBanco("", selecionado.getContratoObn(), selecionado.getBanco())) {
                idUnidades.append(concat).append(hierarquiaOrganizacional.getSubordinada().getId());
                concat = ",";
            }
            stb.append(" and ").append(" B.UNIDADEORGANIZACIONAL_ID IN (").append(idUnidades).append(")");
            logger.debug(" buscou todas as unidades. ");
        }
        return stb.toString();
    }

    public void limparFiltros() {
        dtInicial = sistemaControlador.getDataOperacao();
        Calendar c = Calendar.getInstance();
        c.setTime(dtInicial);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
        dtInicial = c.getTime();
        dtFinal = sistemaControlador.getDataOperacao();
        listaUnidades = new ArrayList<>();
        contaFinanceiraIni = new SubConta();
        contaFinanceiraFim = new SubConta();
    }

    public List<Bordero> listaBorderosBancoStatus() {
        if (validarFiltros()) {
            listaBordero = arquivoRemessaBancoFacade.getBorderoFacade()
                .listaBorderosPorBancoStatus(
                    SituacaoBordero.DEFERIDO,
                    dtInicial,
                    dtFinal,
                    contaFinanceiraIni,
                    contaFinanceiraFim,
                    filtroUnidade(),
                    selecionado.getBanco());
            return listaBordero;
        }
        return new ArrayList<>();
    }

    public void validarArquivo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getBanco() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Banco deve ser informado.");
        }
        if (selecionado.getId() == null) {
            if (selecionado.getArquivoRemBancoBorderos().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Por favor adicione uma ou mais ordem(s) bancária(s) para o arquivo bancário.");
            }
        }
        ve.lancarException();
    }

    private void validarNumeroRE() {
        ValidacaoException ve = new ValidacaoException();
        for (ArquivoRemBancoBordero ob : selecionado.getArquivoRemBancoBorderos()) {
            arquivoRemessaBancoFacade.getBorderoFacade().validarNumeroRE(ob.getBordero());
        }
        ve.lancarException();
    }

    public boolean validarFiltros() {
        boolean controle = true;
        if (dtInicial == null) {
            FacesUtil.addCampoObrigatorio(" A Data Inicial deve ser informado para filtrar a ordem bancária.");
            controle = false;
        } else if (dtFinal == null) {
            FacesUtil.addCampoObrigatorio(" A Data Final deve ser informado para filtrar a ordem bancária.");
            controle = false;
        } else if (dtFinal.before(dtInicial)) {
            FacesUtil.addCampoObrigatorio(" A Data Final deve maior que a Data Inicial.");
            controle = false;
        }
        return controle;
    }

    public void confirmarDownload() {
        arquivoRemessaBancoFacade.salvar(selecionado);
        FacesUtil.addOperacaoRealizada(" Arquivo " + selecionado.getNumero() + " foi gerado com sucesso.");
        redireciona();
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            validarDataPagamentoGuias();
            if (operacao.equals(Operacoes.NOVO)) {
                salvarNovoArquivo();
            } else {
                salvarArquivo();
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void validarSalvar() {
        ValidacaoException ex = new ValidacaoException();
        if (selecionado.getContratoObn() == null)
            ex.adicionarMensagemDeOperacaoNaoPermitida("Por favor informe um contrato. ");

        if (ex.temMensagens())
            throw ex;
    }

    private void salvarArquivo() {
        try {
            validarArquivo();
            validarNumeroRE();
            if (!listaGuiasPagamentos.isEmpty()
                || !listaGuiasDespesaExtra.isEmpty()) {
                FacesUtil.executaJavaScript("dialogMsgErro.show()");
            } else {
                arquivoRemessaBancoFacade.salvar(selecionado, borderosExclusao);
                redirecionarParaVisualizar();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void salvarNovoArquivo() {
        try {
            validarArquivo();
            validarNumeroRE();
            if (!listaGuiasPagamentos.isEmpty()
                || !listaGuiasDespesaExtra.isEmpty()) {
                FacesUtil.executaJavaScript("dialogMsgErro.show()");
            } else {
                arquivoRemessaBancoFacade.salvarNovo(selecionado);
                redirecionarParaVisualizar();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private String getIdsOrdemBancaria() {
        String idsOB = "";
        String condicao = "";
        if (!selecionado.getArquivoRemBancoBorderos().isEmpty()) {
            for (ArquivoRemBancoBordero arq : selecionado.getArquivoRemBancoBorderos()) {
                idsOB += arq.getBordero().getId() + ",";
            }
            condicao += idsOB.substring(0, idsOB.length() - 1);
        }
        return condicao;
    }

    private void validarDataPagamentoGuias() {
        if (!selecionado.getArquivoRemBancoBorderos().isEmpty()) {
            Date dataGeracaoArq = DataUtil.adicionaDias(selecionado.getDataGeracao(), 1);
            List<GuiaPagamento> guiaPagamentos = arquivoRemessaBancoFacade.buscarGuiasPagamentoPorOrdemBancaria(getIdsOrdemBancaria());
            for (GuiaPagamento guia : guiaPagamentos) {
                if (guia.isGuiaTipoCinco(guia.getPagamento())
                    && guia.getDataPagamento() != null) {
                    adicionarGuiaVencidas(guia, dataGeracaoArq, guia.getDataPagamento(), listaGuiasPagamentos);
                }
            }
            List<GuiaPagamentoExtra> guiaDespesasExtras = arquivoRemessaBancoFacade.buscarGuiasDespesaExtraPorOrdemBancaria(getIdsOrdemBancaria());
            for (GuiaPagamentoExtra guia : guiaDespesasExtras) {
                if (guia.isGuiaTipoCinco(guia.getPagamentoExtra())
                    && guia.getDataPagamento() != null) {
                    adicionarGuiaVencidas(guia, dataGeracaoArq, guia.getDataPagamento(), listaGuiasDespesaExtra);
                }
            }
        }
    }

    private void adicionarGuiaVencidas(Object guia, Date dataGeracaoArq, Date dataPgtoGuia, List lista) {

        if (DataUtil.ehDiaNaoUtil(dataGeracaoArq, arquivoRemessaBancoFacade.getFeriadoFacade())) {
            dataGeracaoArq = DataUtil.adicionaDias(dataGeracaoArq, 1);
        }
        if (Util.getDataHoraMinutoSegundoZerado(dataPgtoGuia).compareTo(Util.getDataHoraMinutoSegundoZerado(dataGeracaoArq)) < 0) {
            lista.add(guia);
        }
    }

    public void limparListaGuias() {
        listaGuiasPagamentos = new ArrayList<>();
        listaGuiasDespesaExtra = new ArrayList<>();
    }

    public void gerarPDFGuia() {
        Util.geraPDF("Guias", gerarPDFGuiasDivergentes(), FacesContext.getCurrentInstance());
    }

    private String gerarPDFGuiasDivergentes() {
        String caminhoDaImagem = FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";
        String conteudoMensagem = "<?xml version='1.0' encoding='iso-8859-1'?>";
        conteudoMensagem += " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">";
        conteudoMensagem += " <html>";
        conteudoMensagem += "<html> ";
        conteudoMensagem += "<div style='text-align:center'> ";
        conteudoMensagem += "<img src=\"" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /> </br> ";
        conteudoMensagem += "<b> PREFEITURA DE RIO BRANCO </br> ";
        conteudoMensagem += "</br>GUIAS NÃO AUTORIZADAS A PAGAR</b> ";
        conteudoMensagem += "</div> ";
        conteudoMensagem += "</br>";

        conteudoMensagem += "<div style='text-align:left'> ";
        conteudoMensagem = montarTableGuiaPagamento(conteudoMensagem);
        conteudoMensagem += "</br>";
        conteudoMensagem += "</br>";
        conteudoMensagem = montarTableGuiaDespesaExtra(conteudoMensagem);
        conteudoMensagem += "</div>";
        conteudoMensagem += "<div style='position: fixed; bottom: 0; min-width: 100%;font-size: 11px'> ";
        conteudoMensagem += sistemaControlador.getUsuarioCorrente() + ", em " + DataUtil.getDataFormatada(new Date()) + " às " + Util.hourToString(new Date());
        conteudoMensagem += "</div> ";
        conteudoMensagem += "</html>";
        return conteudoMensagem;
    }

    private String montarTableGuiaPagamento(String conteudoMensagem) {
        if (!listaGuiasPagamentos.isEmpty()) {
            conteudoMensagem += "<b>GUIAS DE PAGAMENTO</b>";
            conteudoMensagem += "<table border='0' style='width:100%;'>";
            conteudoMensagem += "<tr>";
            conteudoMensagem += "<td colspan=\"1\">Tipo de Guia</td>";
            conteudoMensagem += "<td colspan=\"1\">Data de Pagamento</td>";
            conteudoMensagem += "<td colspan=\"1\">Unidade</td>";
            conteudoMensagem += "<td colspan=\"1\">Nº Pagamento</td>";
            conteudoMensagem += "<td colspan=\"1\">Nº O.B.</td>";
            conteudoMensagem += "<td colspan=\"1\">Valor</td>";
            conteudoMensagem += "<hr style='margin-bottom: 0px'>";
            conteudoMensagem += "</tr>";

            for (GuiaPagamento guia : listaGuiasPagamentos) {
                conteudoMensagem += "<tr>";
                conteudoMensagem += "<td>" + guia.getTipoDeGuia(guia.getPagamento()) + "</td>";
                conteudoMensagem += "<td>" + DataUtil.getDataFormatada(guia.getDataPagamento()) + "</td>";
                conteudoMensagem += "<td>" + getCodigoHierarquia(guia) + "</td>";
                conteudoMensagem += "<td>" + guia.getPagamento().getNumeroPagamento() + "</td>";
                conteudoMensagem += "<td>" + guia.getPagamento().getNumDocumento() + "</td>";
                conteudoMensagem += "<td>" + Util.formataValor(guia.getValorTotalPorGuia()) + "</td>";
                conteudoMensagem += "</tr>";
            }
        }
        conteudoMensagem += "</table>";
        return conteudoMensagem;
    }

    private String montarTableGuiaDespesaExtra(String conteudoMensagem) {
        if (!listaGuiasDespesaExtra.isEmpty()) {
            conteudoMensagem += "<b>GUIAS DE DESPESA EXTRA</b>";
            conteudoMensagem += "<table border='0' style='width:100%;'>";
            conteudoMensagem += "<tr>";
            conteudoMensagem += "<td colspan=\"1\">Tipo de Guia</td>";
            conteudoMensagem += "<td colspan=\"1\">Data de Pagamento</td>";
            conteudoMensagem += "<td colspan=\"1\">Unidade</td>";
            conteudoMensagem += "<td colspan=\"1\">Nº Despesa</td>";
            conteudoMensagem += "<td colspan=\"1\">Nº O.B.</td>";
            conteudoMensagem += "<td colspan=\"1\">Valor</td>";
            conteudoMensagem += "<hr style='margin-bottom: 0px'>";
            conteudoMensagem += "</tr>";

            for (GuiaPagamentoExtra guia : listaGuiasDespesaExtra) {
                conteudoMensagem += "<tr>";
                conteudoMensagem += "<td>" + guia.getTipoDeGuia(guia.getPagamentoExtra()) + "</td>";
                conteudoMensagem += "<td>" + DataUtil.getDataFormatada(guia.getDataPagamento()) + "</td>";
                conteudoMensagem += "<td>" + getCodigoHierarquia(guia) + "</td>";
                conteudoMensagem += "<td>" + guia.getPagamentoExtra().getNumero() + "</td>";
                conteudoMensagem += "<td>" + guia.getPagamentoExtra().getNumeroPagamento() + "</td>";
                conteudoMensagem += "<td>" + Util.formataValor(guia.getValorTotalPorGuia()) + "</td>";
                conteudoMensagem += "</tr>";
            }
        }
        conteudoMensagem += "</table>";
        return conteudoMensagem;
    }

    public String getCodigoHierarquia(Object obj) {
        String codigo = "";
        UnidadeOrganizacional unidadeOrganizacional = null;
        if (obj instanceof GuiaPagamento) {
            unidadeOrganizacional = ((GuiaPagamento) obj).getPagamento().getUnidadeOrganizacional();
        } else {
            unidadeOrganizacional = ((GuiaPagamentoExtra) obj).getPagamentoExtra().getUnidadeOrganizacional();
        }
        HierarquiaOrganizacional hierarquiaOrganizacional = arquivoRemessaBancoFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade("ORCAMENTARIA", unidadeOrganizacional, sistemaControlador.getDataOperacao());
        if (hierarquiaOrganizacional != null) {
            codigo = hierarquiaOrganizacional.getCodigo();
        }
        return codigo;
    }

    public Boolean verificarGuiaEmergencial(Date dataPagamento) {
        if (Util.getDataHoraMinutoSegundoZerado(dataPagamento).compareTo(Util.getDataHoraMinutoSegundoZerado(selecionado.getDataGeracao())) == 0) {
            return true;
        }
        return false;
    }

    public void pesquisarOrdemBancaria() {

        if (selecionado.getContratoObn() == null) {
            FacesUtil.executaJavaScript("aguarde.hide();");
            FacesUtil.addOperacaoNaoPermitida("Para realizar a consulta por ordem bancária, é necessário informar o contrato.");
            return;
        }
        setArrayBordero(null);
        FacesUtil.executaJavaScript("aguarde.hide();");
        FacesUtil.executaJavaScript("dialogo.show();");
        organizacional = null;
    }

    public void validarPesquisarOrdemBancaria() {
        ValidacaoException ex = new ValidacaoException();
        if (selecionado.getContratoObn() == null) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("Para realizar a consulta por ordem bancária, é necessário informar o contrato. ");
        }
        if (ex.temMensagens())
            throw ex;
    }

    public void addUnidade() {
        try {
            validarAdicionarUnidade();
            listaUnidades.add(organizacional);
            organizacional = null;
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }

    }

    public void definirContratoBanco() {
        if (selecionado.getBanco() != null) {
            List<ContratoObn> contratos = contratoObnFacade.completarContratoPorBanco("", selecionado.getBanco());
            if (contratos.size() == 1) {
                selecionado.setContratoObn(contratos.get(0));

            } else if (contratos.isEmpty()) {
                String caminho = "/configuracao-arquivo-obn/novo/";
                String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/configuracao-arquivo-obn/listar/' target='_blank'>Configuração Arquivo Obn</a></b>";
                FacesUtil.addAtencao("Esse Banco não possui Contrato! por favor cadastre um contrato para esse banco  em  " + url + "  ");
            }
        }
    }

    public void validarAdicionarUnidade() {
        ValidacaoException ex = new ValidacaoException();
        if (organizacional == null) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("È necessário informar uma unidade.");
        } else if (listaUnidades.contains(organizacional)) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("A unidade  <b> " + organizacional.getSubordinada().getDescricao() + "</b>" + "  já foi informada.");
        }

        if (ex.temMensagens())
            throw ex;
    }


    private void redirecionarParaVisualizar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        FacesUtil.addOperacaoRealizada(" O " + metadata.getNomeEntidade() + " " + selecionado.getNumero() + " foi salvo com sucesso.");
    }

    public void redirecionarParaLista() {
        FacesUtil.addOperacaoRealizada(" O " + metadata.getNomeEntidade() + " " + selecionado.getNumero() + " foi salvo com sucesso.");
        redireciona();
    }

    public void removerBordero(ArquivoRemBancoBordero b) {
        selecionado.getArquivoRemBancoBorderos().remove(b);
        selecionado.setValorTotalBor(selecionado.getValorTotalBor().subtract(b.getBordero().getValor()));
        selecionado.setValorTotalDoc(selecionado.getValorTotalBor());
        borderosExclusao.add(b.getBordero());
    }

    public boolean desabilitarBtRemoverOB(ArquivoRemBancoBordero b) {
        if (SituacaoBordero.PAGO.equals(b.getBordero().getSituacao())
            && selecionado.getTransmitido()) {
            return true;
        }
        return false;
    }

    public void removerUnidade(HierarquiaOrganizacional uni) {
        listaUnidades.remove(uni);
    }

    public List<HierarquiaOrganizacional> completarUnidades(String filter) {
        return contratoObnFacade.buscarHierarquiaPorContratoAndBanco(filter.trim(), selecionado.getContratoObn(), selecionado.getBanco());
    }

    public void visualizarInformativoUnidadePorContrato() {
        if (selecionado.getContratoObn() == null) {
            FacesUtil.executaJavaScript("aguarde.hide();");
            FacesUtil.addOperacaoNaoPermitida("Por favor informe um contrato para visualizar as unidades.");
            return;
        } else {
            informativoUnidades = Lists.newArrayList();
            informativoUnidades = contratoObnFacade.buscarHierarquiaPorContratoAndBanco("", selecionado.getContratoObn(), selecionado.getBanco());
            FacesUtil.executaJavaScript("dlgUnidade.show();");
        }
    }

    public void limparTabelaInformativaUnidade() {
        informativoUnidades = Lists.newArrayList();
    }

    public List<ContratoObn> completarContrato(String filter) {
        if (selecionado.getBanco() == null) {
            FacesUtil.addAtencao(" È necessário informar um banco primeiro. ");
            return null;
        } else {
            return contratoObnFacade.completarContratoPorBanco(filter.trim(), selecionado.getBanco());
        }

    }

    public ConverterAutoComplete getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, arquivoRemessaBancoFacade.getBancoFacade());
        }
        return converterBanco;
    }

    public void gerarArquivoOBN600() {
        try {
            if (validarGerarArquivo()) {
                assistenteBarraProgresso = new AssistenteBarraProgresso();
                assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
                assistenteBarraProgresso.setIp(sistemaFacade.getIp());
                logger.debug(" gerando arquivo bancário para o contrato  {} ", selecionado.getContratoObn().getNumeroContrato());
                FacesUtil.executaJavaScript("iniciaExportacao()");
                future = arquivoRemessaBancoFacade.getArquivoOBN600().gerarArquivoOBN600(selecionado, assistenteBarraProgresso);
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void posExportacao() {
        if (future != null && future.isDone()) {
            try {
                if (assistenteBarraProgresso.getMensagensValidacaoFacesUtil() != null && !assistenteBarraProgresso.getMensagensValidacaoFacesUtil().isEmpty()) {
                    ValidacaoException ve = new ValidacaoException();
                    for (FacesMessage mensagemValidacao : assistenteBarraProgresso.getMensagensValidacaoFacesUtil()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(mensagemValidacao.getDetail());
                    }
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                    FacesUtil.executaJavaScript("ocorreuErro();");
                } else {
                    file = future.get();
                    FacesUtil.executaJavaScript("terminaExportacao();");
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void downloadExportacao() {
        try {
            if (file != null) {
                escreverNoResponse(IOUtils.toByteArray(file.getStream()), gerarNomeDoArquivo(selecionado));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private String gerarNomeDoArquivo(ArquivoRemessaBanco arb) {
        SimpleDateFormat dataHora = new SimpleDateFormat("ddMMyyyyHHmm");
        String nome = dataHora.format(new Date());
        if (arb.getContratoObn() != null) {
            if (arb.getContratoObn().getComplementoNomeArquivo() != null) {
                return nome + arb.getContratoObn().getComplementoNomeArquivo();
            }
        }
        return nome;
    }

    public void escreverNoResponse(byte[] bytes, String nomeArquivo) throws IOException {
        if (bytes != null && bytes.length > 0) {
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/text");
            response.setHeader("Content-Disposition", "inline; filename=" + nomeArquivo);
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            FacesContext.getCurrentInstance().responseComplete();
        }
    }

    public ConverterAutoComplete getConverterContaFinanceiraIni() {
        if (converterContaFinanceiraIni == null) {
            converterContaFinanceiraIni = new ConverterAutoComplete(SubConta.class, arquivoRemessaBancoFacade.getSubContaFacade());
        }
        return converterContaFinanceiraIni;
    }

    public ConverterAutoComplete getConverterContaFinanceiraFim() {
        if (converterContaFinanceiraFim == null) {
            converterContaFinanceiraFim = new ConverterAutoComplete(SubConta.class, arquivoRemessaBancoFacade.getSubContaFacade());
        }
        return converterContaFinanceiraFim;
    }

    public BigDecimal getValorTotalBorderos() {
        BigDecimal soma = BigDecimal.ZERO;
        if (listaBordero != null) {
            for (Bordero bordero : listaBordero) {
                soma = soma.add(bordero.getValor());
            }
        }
        return soma;
    }

    public BigDecimal getValorTotalBorderosAdicionados() {
        BigDecimal soma = BigDecimal.ZERO;
        if (selecionado.getArquivoRemBancoBorderos() != null) {
            for (ArquivoRemBancoBordero arquivoRemBancoBordero : selecionado.getArquivoRemBancoBorderos()) {
                soma = soma.add(arquivoRemBancoBordero.getBordero().getValor());
            }
        }
        return soma;
    }

    public BigDecimal getValorTotalItensDaOrdemBancaria() {
        BigDecimal total = BigDecimal.ZERO;
        for (ValidadorValoresOBN600 item : validadores) {
            total = total.add(item.getValorItemBordero());
        }
        return total;
    }

    public BigDecimal getValorTotalMovimentosDaOrdemBancaria() {
        BigDecimal total = BigDecimal.ZERO;
        for (ValidadorValoresOBN600 movimento : validadores) {
            total = total.add(movimento.getTotalMovimentos());
        }
        return total;
    }

    public BigDecimal getValorTotalGuiasPagamento() {
        BigDecimal total = BigDecimal.ZERO;
        for (ArquivoRemBancoBordero arquivo : selecionado.getArquivoRemBancoBorderos()) {
            for (BorderoPagamento borderoPagamento : arquivo.getBordero().getListaPagamentos()) {
                Pagamento pagamento = arquivoRemessaBancoFacade.getBorderoFacade().getPagamentoFacade().recuperar(borderoPagamento.getPagamento().getId());
                for (GuiaPagamento guiaPagamento : pagamento.getGuiaPagamento()) {
                    total = total.add(guiaPagamento.getValorTotalPorGuia());
                }
            }
        }
        return total;
    }

    public BigDecimal getValorTotalGuiasPagamentoExtra() {
        BigDecimal total = BigDecimal.ZERO;
        for (ArquivoRemBancoBordero arquivo : selecionado.getArquivoRemBancoBorderos()) {
            for (BorderoPagamentoExtra borderoPagamento : arquivo.getBordero().getListaPagamentosExtra()) {
                PagamentoExtra pagamento = arquivoRemessaBancoFacade.getBorderoFacade().getPagamentoExtraFacade().recuperar(borderoPagamento.getPagamentoExtra().getId());
                for (GuiaPagamentoExtra guiaPagamento : pagamento.getGuiaPagamentoExtras()) {
                    total = total.add(guiaPagamento.getValorTotalPorGuia());
                }
            }
        }
        return total;
    }


    public BigDecimal getValorTotalGuias() {
        BigDecimal total = BigDecimal.ZERO;
        total = getValorTotalGuiasPagamento().add(getValorTotalGuiasPagamentoExtra());
        return total;
    }

    public BigDecimal getValorTotalOrdemBancaria() {
        BigDecimal total = BigDecimal.ZERO;
        for (ValidadorValoresOBN600 ob : validadores) {
            total = total.add(ob.getTotalBordero());
        }
        return total;
    }

    public BigDecimal getDiferencaTotalArquivo() {
        BigDecimal total = BigDecimal.ZERO;
        total = getValorTotalItensDaOrdemBancaria().subtract(getValorTotalOrdemBancaria());
        return total;
    }

    public BigDecimal getValorTotalArquivo() {
        BigDecimal valor = BigDecimal.ZERO;
        valor = getValorTotalRecursoProprio().add(getValorTotalOutrasFontes());
        return valor;
    }

    public String getOrdemBancariaComDiferencaValor() {
        StringBuilder numeroOB = new StringBuilder();
        String concat = ", ";
        for (ValidadorValoresOBN600 lista : validadores) {
            if (lista.getPossuiDiferenca()) {
                numeroOB.append(lista.getBordero()).append(concat);
            }
        }
        return numeroOB.toString().substring(0, numeroOB.toString().length() - 2);
    }

    public BigDecimal getValorTotalRecursoProprio() {
        BigDecimal valor = BigDecimal.ZERO;
        for (ArquivoRemBancoBordero arquivo : selecionado.getArquivoRemBancoBorderos()) {
            for (BorderoPagamento borderoPagamento : arquivo.getBordero().getListaPagamentos()) {
                if (!borderoPagamento.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                    if (borderoPagamento.getPagamento().getSubConta().getContaBancariaEntidade().equals(bancoObn.getContaBancariaEntidade())) {
                        valor = valor.add(borderoPagamento.getValor());
                    }
                }
            }
            for (BorderoPagamentoExtra borderoPagamentoExtra : arquivo.getBordero().getListaPagamentosExtra()) {
                if (!borderoPagamentoExtra.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                    if (borderoPagamentoExtra.getPagamentoExtra().getSubConta().getContaBancariaEntidade().equals(bancoObn.getContaBancariaEntidade())) {

                        valor = valor.add(borderoPagamentoExtra.getValor());
                    }
                }
            }
            for (BorderoTransferenciaFinanceira borderoTransferencia : arquivo.getBordero().getListaTransferenciaFinanceira()) {
                if (!borderoTransferencia.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                    if (borderoTransferencia.getTransferenciaContaFinanceira().getSubContaRetirada().getContaBancariaEntidade().equals(bancoObn.getContaBancariaEntidade())) {
                        valor = valor.add(borderoTransferencia.getValor());
                    }
                }
            }
            for (BorderoTransferenciaMesmaUnidade borderoTransferencia : arquivo.getBordero().getListaTransferenciaMesmaUnidade()) {
                if (!borderoTransferencia.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                    if (borderoTransferencia.getTransferenciaMesmaUnidade().getSubContaRetirada().getContaBancariaEntidade().equals(bancoObn.getContaBancariaEntidade())) {
                        valor = valor.add(borderoTransferencia.getValor());
                    }
                }
            }
            for (BorderoLiberacaoFinanceira borderoLiberacaoFinanceira : arquivo.getBordero().getListaLiberacaoCotaFinanceira()) {
                if (!borderoLiberacaoFinanceira.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                    if (borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira().getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada().getContaBancariaEntidade().equals(bancoObn.getContaBancariaEntidade())) {
                        valor = valor.add(borderoLiberacaoFinanceira.getValor());
                    }
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalOutrasFontes() {
        BigDecimal valor = BigDecimal.ZERO;
        for (ArquivoRemBancoBordero arquivo : selecionado.getArquivoRemBancoBorderos()) {
            for (BorderoPagamento borderoPagamento : arquivo.getBordero().getListaPagamentos()) {
                if (!borderoPagamento.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                    if (!borderoPagamento.getPagamento().getSubConta().getContaBancariaEntidade().equals(bancoObn.getContaBancariaEntidade())) {
                        valor = valor.add(borderoPagamento.getValor());
                    }
                }
            }
            for (BorderoPagamentoExtra borderoPagamentoExtra : arquivo.getBordero().getListaPagamentosExtra()) {
                if (!borderoPagamentoExtra.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                    if (!borderoPagamentoExtra.getPagamentoExtra().getSubConta().getContaBancariaEntidade().equals(bancoObn.getContaBancariaEntidade())) {
                        valor = valor.add(borderoPagamentoExtra.getValor());
                    }
                }
            }
            for (BorderoTransferenciaFinanceira borderoTransferencia : arquivo.getBordero().getListaTransferenciaFinanceira()) {
                if (!borderoTransferencia.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                    if (!borderoTransferencia.getTransferenciaContaFinanceira().getSubContaRetirada().getContaBancariaEntidade().equals(bancoObn.getContaBancariaEntidade())) {
                        valor = valor.add(borderoTransferencia.getValor());
                    }
                }
            }
            for (BorderoTransferenciaMesmaUnidade borderoTransferencia : arquivo.getBordero().getListaTransferenciaMesmaUnidade()) {
                if (!borderoTransferencia.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                    if (!borderoTransferencia.getTransferenciaMesmaUnidade().getSubContaRetirada().getContaBancariaEntidade().equals(bancoObn.getContaBancariaEntidade())) {
                        valor = valor.add(borderoTransferencia.getValor());
                    }
                }
            }
            for (BorderoLiberacaoFinanceira borderoLiberacaoFinanceira : arquivo.getBordero().getListaLiberacaoCotaFinanceira()) {
                if (!borderoLiberacaoFinanceira.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                    if (!borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira().getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada().getContaBancariaEntidade().equals(bancoObn.getContaBancariaEntidade())) {
                        valor = valor.add(borderoLiberacaoFinanceira.getValor());
                    }
                }
            }
        }
        return valor;
    }

    public void visualizarBordero(Bordero bordero) {
        BorderoControlador borderoControlador = (BorderoControlador) Util.getControladorPeloNome("borderoControlador");
        Web.navegacao(getCaminhoOrigem(), borderoControlador.getCaminhoPadrao() + "editar/" + bordero.getId() + "/", selecionado);
    }

    public void recuperarValidadores() {
        validadores = arquivoRemessaBancoFacade.recuperarValidadorValores(selecionado);
    }

    public boolean possuiDiferencaNoValidador() {
        try {
            for (ValidadorValoresOBN600 validador : validadores) {
                if (validador.getPossuiDiferenca()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validarGerarArquivo() {
        try {
            Boolean possuiDiferenca = false;
            for (ValidadorValoresOBN600 validador : validadores) {
                if (validador.getPossuiDiferenca()) {
                    possuiDiferenca = true;
                }
            }
            if (possuiDiferenca) {
                FacesUtil.addOperacaoNaoPermitida(" Existe uma diferença de <b>" + Util.formataValor(getDiferencaTotalArquivo()) + "</b> no valor total do arquivo. A inconsistência encontra-se na(s) O.B. Nº <b>" + getOrdemBancariaComDiferencaValor() + "</b>. Por favor, corrigir a diferença para continuar.");
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao validar o arquivo OBN600 " + e.getMessage());
        }
    }

    public List<ValidadorValoresOBN600> getValidadores() {
        return validadores;
    }

    public void setValidadores(List<ValidadorValoresOBN600> validadores) {
        this.validadores = validadores;
    }

    public List<Bordero> getListaBordero() {
        return listaBordero;
    }

    public void setListaBordero(List<Bordero> listaBordero) {
        this.listaBordero = listaBordero;
    }

    public Bordero[] getArrayBordero() {
        return arrayBordero;
    }

    public void setArrayBordero(Bordero[] arrayBordero) {
        this.arrayBordero = arrayBordero;
    }

    public List<GuiaPagamento> getListaGuiasPagamentos() {
        return listaGuiasPagamentos;
    }

    public void setListaGuiasPagamentos(List<GuiaPagamento> listaGuiasPagamentos) {
        this.listaGuiasPagamentos = listaGuiasPagamentos;
    }

    public List<GuiaPagamentoExtra> getListaGuiasDespesaExtra() {
        return listaGuiasDespesaExtra;
    }

    public void setListaGuiasDespesaExtra(List<GuiaPagamentoExtra> listaGuiasDespesaExtra) {
        this.listaGuiasDespesaExtra = listaGuiasDespesaExtra;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public List<FileUploadEvent> getFileUploadEvents() {
        return fileUploadEvents;
    }

    public void setFileUploadEvents(List<FileUploadEvent> fileUploadEvents) {
        this.fileUploadEvents = fileUploadEvents;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public SubConta getContaFinanceiraIni() {
        return contaFinanceiraIni;
    }

    public void setContaFinanceiraIni(SubConta contaFinanceiraIni) {
        this.contaFinanceiraIni = contaFinanceiraIni;
    }

    public SubConta getContaFinanceiraFim() {
        return contaFinanceiraFim;
    }

    public void setContaFinanceiraFim(SubConta contaFinanceiraFim) {
        this.contaFinanceiraFim = contaFinanceiraFim;
    }

    public List<SubConta> completaContaFinanceiraIni(String parte) {
        return arquivoRemessaBancoFacade.getSubContaFacade().listaPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<SubConta> completaContaFinanceiraFim(String parte) {
        return arquivoRemessaBancoFacade.getSubContaFacade().listaPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterContrato() {
        if (converterContrato == null) {
            converterContrato = new ConverterAutoComplete(ContratoObn.class, contratoObnFacade);
        }
        return converterContrato;
    }

    public void setConverterContrato(ConverterAutoComplete converterContrato) {
        this.converterContrato = converterContrato;
    }

    public HierarquiaOrganizacional getOrganizacional() {
        return organizacional;
    }

    public void setOrganizacional(HierarquiaOrganizacional organizacional) {
        this.organizacional = organizacional;
    }

    public ConverterAutoComplete getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public void setConverterHierarquia(ConverterAutoComplete converterHierarquia) {
        this.converterHierarquia = converterHierarquia;
    }

    public BancoObn getBancoObn() {
        return bancoObn;
    }

    public void setBancoObn(BancoObn bancoObn) {
        this.bancoObn = bancoObn;
    }

    public List<HierarquiaOrganizacional> getInformativoUnidades() {
        return informativoUnidades;
    }

    public void setInformativoUnidades(List<HierarquiaOrganizacional> informativoUnidades) {
        this.informativoUnidades = informativoUnidades;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
