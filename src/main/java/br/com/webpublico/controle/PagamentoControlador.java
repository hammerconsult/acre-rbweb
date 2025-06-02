/*
 * Codigo gerado automaticamente em Thu Dec 08 11:12:57 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamento;
import br.com.webpublico.entidadesauxiliares.GuiaPagamentoItem;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.VwHierarquiaDespesaORC;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.IConciliar;
import br.com.webpublico.interfaces.IGuiaArquivoOBN600;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.PagamentoFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
//        Pagamento Normal
    @URLMapping(id = "novopagamento", pattern = "/pagamento/novo/", viewId = "/faces/financeiro/orcamentario/pagamento/edita.xhtml"),
    @URLMapping(id = "editarpagamento", pattern = "/pagamento/editar/#{pagamentoControlador.id}/", viewId = "/faces/financeiro/orcamentario/pagamento/edita.xhtml"),
    @URLMapping(id = "verpagamento", pattern = "/pagamento/ver/#{pagamentoControlador.id}/", viewId = "/faces/financeiro/orcamentario/pagamento/visualizar.xhtml"),
    @URLMapping(id = "listarpagamento", pattern = "/pagamento/listar/", viewId = "/faces/financeiro/orcamentario/pagamento/lista.xhtml"),

//        Pagamento de Restos a Pagar
    @URLMapping(id = "novopagamento-resto", pattern = "/pagamento/resto-a-pagar/novo/", viewId = "/faces/financeiro/orcamentario/pagamento/edita.xhtml"),
    @URLMapping(id = "editarpagamento-resto", pattern = "/pagamento/resto-a-pagar/editar/#{pagamentoControlador.id}/", viewId = "/faces/financeiro/orcamentario/pagamento/edita.xhtml"),
    @URLMapping(id = "verpagamento-resto", pattern = "/pagamento/resto-a-pagar/ver/#{pagamentoControlador.id}/", viewId = "/faces/financeiro/orcamentario/pagamento/visualizar.xhtml"),
    @URLMapping(id = "listarpagamento-resto", pattern = "/pagamento/resto-a-pagar/listar/", viewId = "/faces/financeiro/orcamentario/pagamento/listarestopagar.xhtml")
})

public class PagamentoControlador extends PrettyControlador<Pagamento> implements Serializable, CRUD, IConciliar {

    private static final String CODIGO_BARRAS_INVALIDO = "Código de Barras Inválido!";
    @EJB
    protected PagamentoFacade pagamentoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterSubConta;
    private ConverterAutoComplete converterHistoricoContabil;
    private ConverterAutoComplete converterLiquidacao;
    private ConverterAutoComplete converterContaExtraorcamentaria;
    private ConverterAutoComplete converterFonteDeRecursos;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterClasseCredor;
    private ConverterAutoComplete converterPessoaGuia;
    private MoneyConverter moneyConverter;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private RetencaoPgto retencaoPgto;
    private Boolean viewPanel;
    private DespesaORC despesaORCFuncional;
    private UsuarioUnidadeOrganizacional usuarioUnidadeOrganizacional;
    private VwHierarquiaDespesaORC vwHierarquiaDespesaORCTemp;
    private Boolean lancouRetencao;
    private Boolean pagarPagamento;
    private BigDecimal valorRetencoes;
    private Integer indiceAba;
    private ContaBancariaEntidade contaBancariaEntidade;
    private GuiaPagamento guiaPagamento;
    private GuiaPagamentoItem guiaPagamentoItem;
    private Boolean imprimirDialog;
    private Boolean gestorFinanceiro;
    private CodigoCO codigoCO;

    //Desdobramento
    private DesdobramentoPagamento desdobramento;
    private ConverterAutoComplete converterDesdobramentoLiquidacao;

    public PagamentoControlador() {
        super(Pagamento.class);
    }

    public PagamentoFacade getFacade() {
        return pagamentoFacade;
    }

    public String actionSelecionar() {
        return "edita";
    }

    @Override
    public String getCaminhoPadrao() {
        if (getCategoriaOrcamentariaPagamento()) {
            return "/pagamento/";
        } else {
            return "/pagamento/resto-a-pagar/";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novopagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUuid(UUID.randomUUID().toString());
        parametrosIniciais();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
    }

    @URLAction(mappingId = "editarpagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditar();
        if (selecionado.getCategoriaOrcamentaria().isResto()) {
            FacesUtil.addOperacaoNaoPermitida("O Pagamento selecionado é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível editar na tela de Pagamento Normal.");
            redireciona();
        }
    }

    @URLAction(mappingId = "verpagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarVer();
        if (selecionado.getCategoriaOrcamentaria().isResto()) {
            FacesUtil.addOperacaoNaoPermitida("O Pagamento selecionado é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível visualizar na tela de Pagamento Normal.");
            redireciona();
        }
    }

    @URLAction(mappingId = "novopagamento-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRestoPagar() {
        super.novo();
        selecionado.setUuid(UUID.randomUUID().toString());
        parametrosIniciais();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
    }

    @URLAction(mappingId = "editarpagamento-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarRestoPagar() {
        super.editar();
        recuperarEditar();
        if (selecionado.getCategoriaOrcamentaria().isNormal()) {
            FacesUtil.addOperacaoNaoPermitida("O Pagamento selecionado é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível editar na tela de Restos a Pagar.");
            redireciona();
        }
    }

    @URLAction(mappingId = "verpagamento-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRestoPagar() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        recuperarVer();
        if (selecionado.getCategoriaOrcamentaria().isNormal()) {
            FacesUtil.addOperacaoNaoPermitida("O Pagamento selecionado é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível visualizar na tela de Restos a Pagar.");
            redireciona();
        }
    }

    public void redirecionarParaLista() {
        if (getCategoriaOrcamentariaPagamento()) {
            FacesUtil.redirecionamentoInterno("/pagamento/listar/");
        } else {
            FacesUtil.redirecionamentoInterno("/pagamento/resto-a-pagar/listar/");
        }
    }

    public void redirecionarParaEdita() {
        if (getCategoriaOrcamentariaPagamento()) {
            FacesUtil.redirecionamentoInterno("/pagamento/editar/" + selecionado.getId());
        } else {
            FacesUtil.redirecionamentoInterno("/pagamento/resto-a-pagar/editar/" + selecionado.getId());
        }
    }

    public String getTituloPanelPagamento() {
        if (getCategoriaOrcamentariaPagamento()) {
            return "Pagamento";
        } else {
            return "Pagamento de Resto a Pagar";
        }
    }

    public String getTituloLiquidacao() {
        if (getCategoriaOrcamentariaPagamento()) {
            return "Liquidação";
        } else {
            return "Liquidação de Resto a Pagar";
        }
    }

    public String getTituloEmpenho() {
        if (getCategoriaOrcamentariaPagamento()) {
            return "Empenho";
        } else {
            return "Empenho de Resto a Pagar";
        }
    }

    public String getAlinhaCampos() {
        if (getCategoriaOrcamentariaPagamento()) {
            return "margin-right: 32px";
        } else {
            return "margin-right: 65px";
        }
    }

    public Boolean getCategoriaOrcamentariaPagamento() {
        if (selecionado.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.NORMAL)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getHabilitaEditar() {
        Pagamento p = ((Pagamento) selecionado);
        Boolean controle = true;
        if (p.getStatus() == null) {
            return controle;
        }
        if (p.getStatus().equals(StatusPagamento.INDEFERIDO)
            || p.getStatus().equals(StatusPagamento.DEFERIDO)
            || p.getStatus().equals(StatusPagamento.ABERTO)) {
            controle = false;
        }
        return controle;
    }

    public Boolean getHabilitaBotaoEditar() {
        if (selecionado.getStatus() == null) {
            return false;
        }
        if (selecionado.getStatus().equals(StatusPagamento.INDEFERIDO)
            || selecionado.getStatus().equals(StatusPagamento.DEFERIDO)
            || selecionado.getStatus().equals(StatusPagamento.ABERTO)) {
            return true;
        }
        return false;
    }

    public boolean isBancoCaixaEconomica() {
        if (selecionado.getSubConta() != null
            && contaBancariaEntidade.getAgencia() != null) {
            Banco banco = selecionado.getSubConta().getContaBancariaEntidade().getAgencia().getBanco();
            if (pagamentoFacade.getBancoFacade().isBancoCaixaEconomica(banco)) {
                return true;
            }
        }
        return false;
    }

    private void parametrosIniciais() {
        gestorFinanceiro = isGestorFinanceiro();
        retencaoPgto = new RetencaoPgto();
        viewPanel = false;
        selecionado.setPrevistoPara(sistemaControlador.getDataOperacao());
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaSTR("");
        selecionado.setTipoDocPagto(TipoDocPagto.ORDEMPAGAMENTO);
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setDataRegistro(sistemaControlador.getDataOperacao());
        selecionado.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        selecionado.setDataPagamento(null);
        selecionado.setSubConta(null);
        lancouRetencao = Boolean.FALSE;
        pagarPagamento = Boolean.FALSE;
        imprimirDialog = Boolean.FALSE;
        valorRetencoes = BigDecimal.ZERO;
        codigoCO = null;
        cancelarGuiaPagamento();
        if (selecionado.getLiquidacao() != null) {
            recuperarValores();
        }
        if (selecionado.getHistoricoContabil() != null) {
            copiarHistoricoUsuario();
        }
        if (pagamentoFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", pagamentoFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    private void recuperarVer() {
        gestorFinanceiro = isGestorFinanceiro();
        setaValorFinalPagto();
        parametrosRetencao();
        despesaOrcDoEmpenho();
        viewPanel = false;
        contaBancariaEntidade = selecionado.getSubConta().getContaBancariaEntidade();
        recuperarValores();
        recuperarValorRetencoes();
        codigoCO = null;
    }

    private void recuperarValorRetencoes() {
        try {
            valorRetencoes = getSomaRetencoes();
        } catch (Exception e) {
            valorRetencoes = BigDecimal.ZERO;
        }
    }

    private void despesaOrcDoEmpenho() {
        try {
            despesaORCFuncional = selecionado.getLiquidacao().getEmpenho().getDespesaORC();
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " A Despesa orçamentária do empenho não encontrada para o pagamento selecionado.");
        }
    }

    private void recuperarEditar() {
        setOperacao(Operacoes.EDITAR);
        parametrosRetencao();
        contaBancariaEntidade = selecionado.getSubConta().getContaBancariaEntidade();
        indiceAba = 0;
        pagarPagamento = Boolean.TRUE;
        setaValorFinalPagto();
        despesaOrcDoEmpenho();
        viewPanel = false;
        recuperarValores();
        gestorFinanceiro = isGestorFinanceiro();
        cancelarGuiaPagamento();
        recuperarValorRetencoes();
        novoDetalhamento();
        codigoCO = null;
    }

    private void parametrosRetencao() {
        retencaoPgto = new RetencaoPgto();
        retencaoPgto.setPagamento(selecionado);
        retencaoPgto.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        retencaoPgto.setNumero(getNumeroMaiorReceitaExtra().toString());
        if (selecionado.getRetencaoPgtos().isEmpty()) {
            lancouRetencao = Boolean.FALSE;
        } else {
            lancouRetencao = Boolean.TRUE;
        }

        ContaDeDestinacao contaDeDestinacaoEmpenho = getContaDeDestinacaoEmpenho();
        if (contaDeDestinacaoEmpenho != null) {
            retencaoPgto.setFonteDeRecursos(contaDeDestinacaoEmpenho.getFonteDeRecursos());
        }
        retencaoPgto.setSubConta(selecionado.getSubConta());
    }

    public void validarPagamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Valor deve ser maior que zero(0).");
        }
        if (!selecionado.getRetencaoPgtos().isEmpty()) {
            if (selecionado.getValor().compareTo(getSomaRetencoes()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor total das retenções de <b>" + Util.formataValor(getSomaRetencoes()) + "</b> é maior que o valor do pagamento de <b>" + selecionado.getValor() + "</b>.");
            }
        }
        if (selecionado.getValor().compareTo(selecionado.getTotalDesdobramentos()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor do pagamento " + Util.formataValor(selecionado.getValor()) +
                " e valor total dos desdobramentos " + Util.formataValor(selecionado.getTotalDesdobramentos()) + " não conferem!");
        }
        if (selecionado.getValorFinal().compareTo(new BigDecimal(BigInteger.ZERO)) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor da Final não pode ser negativo.");
        }
        if (selecionado.getLiquidacao().getEmpenho().getTipoEmpenho().equals(TipoEmpenho.ORDINARIO) && selecionado.getId() == null) {
            if (selecionado.getLiquidacao().getSaldo().compareTo(selecionado.getValor()) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A " + getTituloLiquidacao() + " selecionada é de um " + getTituloEmpenho() + " <b>Ordinário</b>, por isso o valor do seu pagamento deve ser igual ao seu saldo de <b>" + Util.formataValor(selecionado.getLiquidacao().getSaldo()) + "</b>.");
            }
        }
        if (DataUtil.dataSemHorario(selecionado.getPrevistoPara()).before(DataUtil.dataSemHorario(selecionado.getLiquidacao().getDataLiquidacao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A data de Previsão do Pagamento (" + DataUtil.getDataFormatada(selecionado.getPrevistoPara()) + ") deve ser superior ou igual a data da liquidação. Data da liquidação: <b>" + DataUtil.getDataFormatada(selecionado.getLiquidacao().getDataLiquidacao()) + "</b>.");
        }
        if (selecionado.getLiquidacao() != null) {
            pagamentoFacade.getLiquidacaoFacade().getHierarquiaOrganizacionalFacade()
                .validarUnidadesIguais(selecionado.getLiquidacao().getUnidadeOrganizacional(), selecionado.getUnidadeOrganizacional()
                    , ve
                    , "A Unidade Organizacional do pagamento deve ser a mesma da liquidação.");
        }
        if (selecionado.isPagamentoComGuia()
            && selecionado.getGuiaPagamento().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Quando utilizado o tipo de documento <b> " + selecionado.getTipoDocPagto().toString() + " </b>, é obrigatório ter pelo menos uma guia de " + selecionado.getTipoDocPagto().toString() + ".");
        }
        if (selecionado.getLiquidacao().getEmpenho().getCodigoCO() == null &&
            selecionado.getSubConta() != null && selecionado.getSubConta().getObrigarCodigoCO()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Código CO deve ser informado.");
        }
        ve.lancarException();
    }

    private Boolean validarValorGuias() {
        if (!selecionado.getGuiaPagamento().isEmpty()) {
            if (selecionado.getValorFinal().compareTo(guiaPagamento.getValorTotalGuiasPagamento()) != 0) {
                FacesUtil.addOperacaoNaoPermitida(" O valor total para as Guias do tipo: " + selecionado.getTipoDocPagto().getDescricao() + " (" + selecionado.getTipoDocPagto().getCodigo() + ") de <b>" + Util.formataValor(guiaPagamento.getValorTotalGuiasPagamento()) + "</b>, está diferente do valor final do pagamento de <b>" + Util.formataValor(selecionado.getValorFinal()) + "</b>.");
                return false;
            }
        }
        return true;
    }

    @Override
    public void salvar() {
        try {
            atualizarCodigoCOEmpenho();
            realizarValidacoes();
            if (isOperacaoNovo()) {
                pagamentoFacade.validarConvenioDespesaVigente(selecionado, selecionado.getDataRegistro());
                salvarNovoPagamenoLancandoRetencao();
            } else {
                pagamentoFacade.validarConcorrencia(selecionado);
                pagamentoFacade.salvar(selecionado);
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getTituloPanelPagamento() + " " + selecionado.toString() + " foi alterado com sucesso.");
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    private void salvarNovoPagamenoLancandoRetencao() {
        if (lancouRetencao) {
            salvarPagamento();
        } else {
            lancouRetencao = Boolean.TRUE;
            FacesUtil.executaJavaScript("dialogRetencao.show()");
        }
    }

    private void salvarPagamento() {
        pagamentoFacade.salvarNovoPagto(selecionado);
        FacesUtil.addOperacaoRealizada(getTituloPanelPagamento() + " " + selecionado.toString() + " foi salvo com sucesso.");
        if (lancouRetencao) {
            redirecionarParaEdita();
        }
    }

    public void cancelarLancarRetencao() {
        FacesUtil.executaJavaScript("dialogRetencao.hide()");
        this.lancouRetencao = Boolean.TRUE;
        salvarPagamento();
    }

    private void realizarValidacoes() {
        selecionado.realizarValidacoes();
        validarSaldoLiquidacao();
        validarPagamento();
        validarFinalidadePagamento();
        validarIntegracaoDividaPublica();
        validarIntegracaoOperacaoDeCredito();
    }

    private void validarDataConciliacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataConciliacao() != null && DataUtil.getAno(selecionado.getDataPagamento()).compareTo(DataUtil.getAno(selecionado.getDataConciliacao())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        ve.lancarException();
    }

    private void validarIntegracaoDividaPublica() {
        if (selecionado.getLiquidacao().getEmpenho().getDividaPublica() != null) {
            ValidacaoException ve = new ValidacaoException();
            TipoContaDespesa tipoContaDespesa = selecionado.getLiquidacao().getEmpenho().getTipoContaDespesa();
            if (TipoContaDespesa.PRECATORIO.equals(tipoContaDespesa)) {
                validarFonteDaDividaPublica(ve, TipoValidacao.MOVIMENTO_DIVIDA_PUBLICA, selecionado.getLiquidacao().getEmpenho().getDividaPublica());
            }
            if (tipoContaDespesa.isDividaPublica()) {
                validarFonteDaDividaPublica(ve, TipoValidacao.MOVIMENTO_DIVIDA_PUBLICA, selecionado.getLiquidacao().getEmpenho().getDividaPublica());
            }
        }
    }

    private void validarIntegracaoOperacaoDeCredito() {
        if (selecionado.getLiquidacao().getEmpenho().getOperacaoDeCredito() != null) {
            ValidacaoException ve = new ValidacaoException();
            validarFonteDaDividaPublica(ve, TipoValidacao.DESPESA_ORCAMENTARIA, selecionado.getLiquidacao().getEmpenho().getOperacaoDeCredito());
        }
    }

    private void validarFonteDaDividaPublica(ValidacaoException ve, TipoValidacao tipoValidacao, DividaPublica dividaPublica) {
        if (!pagamentoFacade.getEmpenhoFacade().getDividaPublicaFacade().hasFonteConfiguradaNaDividaPublica(dividaPublica, tipoValidacao, pagamentoFacade.getSistemaFacade().getExercicioCorrente(), selecionado.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos(), selecionado.getSubConta())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A Fonte de Recurso e Conta Financeira selecionadas não estão configuradas no cadastro da dívida pública " + dividaPublica.getNumero() + " para o tipo de movimento '" + tipoValidacao.getDescricao() + "'.");
        }
        ve.lancarException();
    }

    public Boolean renderizarGuias() {
        return selecionado.getTipoDocPagto() != null && (TipoDocPagto.FATURA.equals(selecionado.getTipoDocPagto())
            || TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocPagto())
            || TipoDocPagto.GPS.equals(selecionado.getTipoDocPagto())
            || TipoDocPagto.DARF.equals(selecionado.getTipoDocPagto())
            || TipoDocPagto.DARF_SIMPLES.equals(selecionado.getTipoDocPagto())
            || TipoDocPagto.GRU.equals(selecionado.getTipoDocPagto()));
    }

    public void geraSaldoMigrados() {
        try {
            pagamentoFacade.geraSaldoMigracao(sistemaControlador.getExercicioCorrente());
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getTituloPanelPagamento() + " efetuado com sucesso.");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), ex.getMessage()));
        }
    }

    public List<PagamentoEstorno> getEstornoPagamentos() {
        try {
            if (selecionado.getId() != null) {
                return pagamentoFacade.getPagamentoEstornoFacade().listaEstornosPagamento(selecionado);
            }
            return new ArrayList<PagamentoEstorno>();
        } catch (Exception e) {
            return new ArrayList<PagamentoEstorno>();
        }
    }

    public List<ReceitaExtra> getReceitaExtras() {
        try {
            if (selecionado.getId() != null) {
                return pagamentoFacade.buscarReceitasExtrasPorPagamento(selecionado);
            }
            return new ArrayList<ReceitaExtra>();
        } catch (Exception e) {
            return new ArrayList<ReceitaExtra>();
        }
    }

    public List<ReceitaExtraEstorno> getEstornoReceitaExtras() {
        try {
            if (selecionado.getId() != null) {
                return pagamentoFacade.buscarReceitasExtrasEstornoPorPagamento(selecionado);
            }
            return new ArrayList<ReceitaExtraEstorno>();
        } catch (Exception e) {
            return new ArrayList<ReceitaExtraEstorno>();
        }
    }

    public List<PagamentoExtra> getDespesasExtra() {
        try {
            if (selecionado.getId() != null) {
                return pagamentoFacade.buscarDespesasExtrasPorPagamento(selecionado);
            }
            return new ArrayList<PagamentoExtra>();
        } catch (Exception e) {
            return new ArrayList<PagamentoExtra>();
        }
    }

    public List<PagamentoExtraEstorno> getEstornoDespesasExtra() {
        try {
            if (selecionado.getId() != null) {
                return pagamentoFacade.buscarEstornoDespesaExtraPorPagamento(selecionado);
            }
            return new ArrayList<PagamentoExtraEstorno>();
        } catch (Exception e) {
            return new ArrayList<PagamentoExtraEstorno>();
        }
    }

    public BigDecimal getSomaEstornoPagamentos() {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (PagamentoEstorno pe : getEstornoPagamentos()) {
            estornos = estornos.add(pe.getValor());
        }
        return estornos;
    }

    public BigDecimal getSomaReceitaExtra() {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (ReceitaExtra receitaExtra : getReceitaExtras()) {
            estornos = estornos.add(receitaExtra.getValor());
        }
        return estornos;
    }

    public BigDecimal getSomaEstornoReceitaExtra() {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (ReceitaExtraEstorno receitaExtraEstorno : getEstornoReceitaExtras()) {
            estornos = estornos.add(receitaExtraEstorno.getValor());
        }
        return estornos;
    }

    public BigDecimal getTotalDespesasExtra() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (PagamentoExtra pagamentoExtra : getDespesasExtra()) {
            valor = valor.add(pagamentoExtra.getValor());
        }
        return valor;
    }


    public BigDecimal getTotalEstornoDespesasExtra() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (PagamentoExtraEstorno pagamentoExtraEstorno : getEstornoDespesasExtra()) {
            valor = valor.add(pagamentoExtraEstorno.getValor());
        }
        return valor;
    }

    public void validaDeferirPagamento() {
        ValidacaoException ve = new ValidacaoException();

        if (DataUtil.dataSemHorario(getDataOperacao()).compareTo(DataUtil.dataSemHorario(selecionado.getLiquidacao().getDataLiquidacao())) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A data de pagamento deve ser superior ou igual a data da liquidação selecionada. Data da liquidação: <b>" + DataUtil.getDataFormatada(selecionado.getLiquidacao().getDataLiquidacao()) + "</b>.");
        }
        ConfiguracaoContabil configuracaoContabil = pagamentoFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
        if (configuracaoContabil.getContasDespesa().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar as Contas de Despesa para geração de Saldo da Dívida Pública na Configuração Contábil.");
        }
        if (isContaDespesaEmpenhoReinf()) {
            for (LiquidacaoDoctoFiscal doctoFiscalLiquidacao : selecionado.getLiquidacao().getDoctoFiscais()) {
                validarDataDocumentoFiscalDentroPerioroFase(ve, doctoFiscalLiquidacao);
            }
        }
        pagamentoFacade.validarConvenioDespesaVigente(selecionado, getDataOperacao());
        ve.lancarException();
    }

    private void validarDataDocumentoFiscalDentroPerioroFase(ValidacaoException ve, LiquidacaoDoctoFiscal doctoFiscalLiquidacao) {
        UnidadeOrganizacional uo = selecionado.getUnidadeOrganizacional() != null ? selecionado.getUnidadeOrganizacional() : pagamentoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente();
        Date dataDocto = doctoFiscalLiquidacao.getDoctoFiscalLiquidacao().getDataDocto();
        TipoDocumentoFiscal tipoDocumentoFiscal = doctoFiscalLiquidacao.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal();
        if (tipoDocumentoFiscal.getValidarCopetencia() && doctoFiscalLiquidacao.getRetencaoPrevidenciaria() &&
            pagamentoFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/orcamentario/pagamento/edita.xhtml", dataDocto, uo, pagamentoFacade.getExercicioFacade().getExercicioPorAno(DataUtil.getAno(dataDocto)))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O documento comprobatório da liquidação <b>nº " + doctoFiscalLiquidacao.getDoctoFiscalLiquidacao().getNumero() +
                "</b> com data de <b>" + DataUtil.getDataFormatada(dataDocto) + "</b> está fora do período fase para a unidade <b>" +
                pagamentoFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(dataDocto, uo, TipoHierarquiaOrganizacional.ORCAMENTARIA).toString() + "</b>");
        }
    }

    private void validarGuias() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getGuiaPagamento().isEmpty()) {
            if (selecionado.getValorFinal().compareTo(selecionado.getValorTotalGuiasPagamento()) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor total para as guias do tipo: " + selecionado.getTipoDocPagto().getDescricao() + " (" + selecionado.getTipoDocPagto().getCodigo() + ") de <b>" + Util.formataValor(selecionado.getValorTotalGuiasPagamento()) + "</b>, está diferente do valor final do pagamento de <b>" + Util.formataValor(selecionado.getValorFinal()) + "</b>.");
            }
        }
        ve.lancarException();
    }

    public void deferirPagamento() {
        try {
            selecionado = pagamentoFacade.recuperar(selecionado);
            validaDeferirPagamento();
            validarGuias();
            selecionado = pagamentoFacade.deferirPagamento(selecionado, getDataOperacao());
            pagamentoFacade.desbloquearPagamento(selecionado);
            finalizaDeferirPagamentoVerificandoOperacaoBaixaParaRegularizacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            selecionado.getLiquidacao().setSaldo(selecionado.getLiquidacao().getSaldo().add(selecionado.getValor()));
            pagamentoFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(e);
            pagamentoFacade.desbloquearPagamento(selecionado);
        } catch (Exception ex) {
            logger.error("error", ex);
            selecionado.getLiquidacao().setSaldo(selecionado.getLiquidacao().getSaldo().add(selecionado.getValor()));
            descobrirETratarException(ex);
            pagamentoFacade.desbloquearPagamento(selecionado);
        }
    }

    private void finalizaDeferirPagamentoVerificandoOperacaoBaixaParaRegularizacao() {
        if (TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO.equals(selecionado.getTipoOperacaoPagto())) {
            selecionado = pagamentoFacade.recuperar(selecionado.getId());
            baixar();
        } else {
            FacesUtil.executaJavaScript("dialogDeferir.hide()");
            FacesUtil.executaJavaScript("dialogImprimirNota.show()");
        }
    }

    public void prepararPagamentoParaDeferir() {
        try {
            realizarValidacoes();
            validarGuias();
            pagamentoFacade.validarConcorrencia(selecionado);
            selecionado = pagamentoFacade.atualizarRetornandoEmJdbc(selecionado);
            FacesUtil.executaJavaScript("dialogDeferir.show()");
        } catch (ValidacaoException ve) {
            selecionado.setDataPagamento(null);
            FacesUtil.executaJavaScript("atualizarPagamento.hide()");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            selecionado.setDataPagamento(null);
            FacesUtil.executaJavaScript("atualizarPagamento.hide()");
            descobrirETratarException(ex);
        }
    }

    public void cancelarDeferirPagamento() {
        selecionado.setDataPagamento(null);
        FacesUtil.executaJavaScript("dialogDeferir.hide()");
    }

    public Boolean verificaStatusPgto() {
        if (selecionado.getStatus() == null) {
            return false;
        }
        if (selecionado.getId() != null
            && selecionado.getStatus().equals(StatusPagamento.ABERTO)) {
            return true;
        }
        return false;
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            List<NotaExecucaoOrcamentaria> notas = pagamentoFacade.buscarNotaDePagamento(" AND NOTA.ID = " + selecionado.getId(), selecionado.getCategoriaOrcamentaria(), "NOTA DE PAGAMENTO");
            if (notas != null && !notas.isEmpty()) {
                pagamentoFacade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notas.get(0), CategoriaOrcamentaria.RESTO.equals(selecionado.getCategoriaOrcamentaria()) ? ModuloTipoDoctoOficial.NOTA_RESTO_PAGAMENTO : ModuloTipoDoctoOficial.NOTA_PAGAMENTO, selecionado, isDownload);
            }
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioDaNotaOrcamentaria() {
        try {
            List<NotaExecucaoOrcamentaria> notas = pagamentoFacade.buscarNotaDePagamento(" AND NOTA.ID = " + selecionado.getId(), selecionado.getCategoriaOrcamentaria(), "CONFERÊNCIA DA PRÉVIA DE PAGAMENTO");
            if (notas != null && !notas.isEmpty()) {
                pagamentoFacade.getNotaOrcamentariaFacade().gerarRelatorioDasNotasOrcamentarias(notas, CategoriaOrcamentaria.RESTO.equals(selecionado.getCategoriaOrcamentaria()) ? ModuloTipoDoctoOficial.NOTA_RESTO_PAGAMENTO : ModuloTipoDoctoOficial.NOTA_PAGAMENTO);
            }
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void cancelaImprimirNota() {
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getTituloPanelPagamento() + " " + selecionado.toString() + " foi salvo com sucesso.");
        redirecionarParaLista();
    }

    public Integer getNumeroMaiorReceitaExtra() {
        BigDecimal maior = new BigDecimal(pagamentoFacade.getReceitaExtraFacade().retornaUltimoNumeroReceitaExtra());
        maior = maior.add(BigDecimal.ONE);
        return maior.intValue();
    }

    @Override
    public AbstractFacade getFacede() {
        return pagamentoFacade;
    }

    public ConverterAutoComplete getConverterSubConta() {
        if (converterSubConta == null) {
            converterSubConta = new ConverterAutoComplete(SubConta.class, pagamentoFacade.getSubContaFacade());
        }
        return converterSubConta;
    }

    public List<SelectItem> getTiposDeDocumento() {
        List<SelectItem> toReturn = Lists.newArrayList();
        if (selecionado.getTipoOperacaoPagto() != null) {
            switch (selecionado.getTipoOperacaoPagto()) {
                case PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA:
                case PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA_NAO_CONTA_UNICA:
                    toReturn.add(new SelectItem(null, ""));
                    toReturn.add(new SelectItem(TipoDocPagto.FATURA, TipoDocPagto.FATURA.getDescricao()));
                    toReturn.add(new SelectItem(TipoDocPagto.CONVENIO, TipoDocPagto.CONVENIO.getDescricao()));
                    toReturn.add(new SelectItem(TipoDocPagto.GRU, TipoDocPagto.GRU.getDescricao()));
                    break;
                case PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA:
                case PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA_NAO_CONTA_UNICA:
                    toReturn.add(new SelectItem(null, ""));
                    toReturn.add(new SelectItem(TipoDocPagto.GPS, TipoDocPagto.GPS.getDescricao()));
                    toReturn.add(new SelectItem(TipoDocPagto.DARF, TipoDocPagto.DARF.getDescricao()));
                    toReturn.add(new SelectItem(TipoDocPagto.DARF_SIMPLES, TipoDocPagto.DARF_SIMPLES.getDescricao()));
                    break;
                case TRANSFERENCIA_DE_RECURSOS_OB_LISTA:
                case TRANSFERENCIA_DE_RECURSOS_OB_LISTA_NAO_CONTA_UNICA:
                    toReturn.add(new SelectItem(null, ""));
                    toReturn.add(new SelectItem(TipoDocPagto.GRU, TipoDocPagto.GRU.getDescricao()));
                    break;
                default:
                    toReturn.add(new SelectItem(null, ""));
                    toReturn.add(new SelectItem(TipoDocPagto.ORDEMPAGAMENTO, TipoDocPagto.ORDEMPAGAMENTO.getDescricao()));
                    break;
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTipoOperacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO, TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO.getDescricao()));
        if (selecionado.getSubConta() != null) {
            if (selecionado.getContaPgto() == null) {
                if (!selecionado.getSubConta().isContaUnica()) {
                    toReturn.add(new SelectItem(TipoOperacaoPagto.LIQUIDACAO_NO_CAIXA_NAO_CONTA_UNICA, TipoOperacaoPagto.LIQUIDACAO_NO_CAIXA_NAO_CONTA_UNICA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_NAO_AUTENTICADO_NAO_CONTA_UNICA, TipoOperacaoPagto.PAGAMENTO_NAO_AUTENTICADO_NAO_CONTA_UNICA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA_NAO_CONTA_UNICA, TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA_NAO_CONTA_UNICA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA_NAO_CONTA_UNICA, TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA_NAO_CONTA_UNICA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.TRANSFERENCIA_DE_RECURSOS_OB_LISTA_NAO_CONTA_UNICA, TipoOperacaoPagto.TRANSFERENCIA_DE_RECURSOS_OB_LISTA_NAO_CONTA_UNICA.getDescricao()));
                } else {
                    toReturn.add(new SelectItem(TipoOperacaoPagto.LIQUIDACAO_NO_CAIXA, TipoOperacaoPagto.LIQUIDACAO_NO_CAIXA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_COM_AUTENTICACAO, TipoOperacaoPagto.PAGAMENTO_COM_AUTENTICACAO.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA, TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA, TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.TRANSFERENCIA_DE_RECURSOS_OB_LISTA, TipoOperacaoPagto.TRANSFERENCIA_DE_RECURSOS_OB_LISTA.getDescricao()));
                }
            } else {
                if (isContasMesmoBanco()) {
                    if (!selecionado.getSubConta().isContaUnica()) {
                        toReturn.add(new SelectItem(TipoOperacaoPagto.CREDOR_MESMO_BANCO_NAO_CONTA_UNICA, TipoOperacaoPagto.CREDOR_MESMO_BANCO_NAO_CONTA_UNICA.getDescricao()));
                    } else {
                        toReturn.add(new SelectItem(TipoOperacaoPagto.CREDOR_MESMO_BANCO, TipoOperacaoPagto.CREDOR_MESMO_BANCO.getDescricao()));
                    }
                } else {
                    if (!selecionado.getSubConta().isContaUnica()) {
                        toReturn.add(new SelectItem(TipoOperacaoPagto.CREDOR_OUTRO_BANCO_NAO_CONTA_UNICA, TipoOperacaoPagto.CREDOR_OUTRO_BANCO_NAO_CONTA_UNICA.getDescricao()));
                    } else {
                        toReturn.add(new SelectItem(TipoOperacaoPagto.CREDOR_OUTRO_BANCO, TipoOperacaoPagto.CREDOR_OUTRO_BANCO.getDescricao()));
                    }
                }
            }
        }
        Collections.sort(toReturn, new Comparator<SelectItem>() {
            @Override
            public int compare(SelectItem o1, SelectItem o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });
        return toReturn;
    }

    private boolean isContasMesmoBanco() {
        try {
            return selecionado.getContaPgto().getAgencia().getBanco().equals(selecionado.getSubConta().getContaBancariaEntidade().getAgencia().getBanco());
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isGestorFinanceiro() {
        return pagamentoFacade.isGestorFinanceiro(sistemaControlador.getUsuarioCorrente(), sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getDataOperacao());
    }

    public void setaValorFinalPagto() {
        Pagamento pag = ((Pagamento) selecionado);
        BigDecimal valFinal = pag.getValor();
        if (pag.getRetencaoPgtos() != null) {
            for (RetencaoPgto rp : pag.getRetencaoPgtos()) {
                valFinal = valFinal.subtract(rp.getValor());
            }
        }
        pag.setValorFinal(valFinal);
    }

    public BigDecimal getSomaRetencoes() {
        Pagamento pag = ((Pagamento) selecionado);
        BigDecimal valor = BigDecimal.ZERO;
        for (RetencaoPgto rp : pag.getRetencaoPgtos()) {
            valor = valor.add(rp.getValor());
        }
        return valor;
    }

    public List<HistoricoContabil> completaHistoricoContabil(String parte) {
        return pagamentoFacade.getHistoricoContabilFacade().listaFiltrando(parte, "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterHistoricoContabil() {
        if (converterHistoricoContabil == null) {
            converterHistoricoContabil = new ConverterAutoComplete(HistoricoContabil.class, pagamentoFacade);
        }
        return converterHistoricoContabil;
    }

    public List<Liquidacao> completaLiquidacao(String parte) {
        if (getCategoriaOrcamentariaPagamento()) {
            return pagamentoFacade.getLiquidacaoFacade().buscarPorPessoa(parte.trim(), sistemaControlador.getExercicioCorrente().getAno(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        } else {
            return pagamentoFacade.getLiquidacaoFacade().buscarFiltrandoLiquidacaRestoPagarPorPessoa(parte.trim(), sistemaControlador.getExercicioCorrente().getAno(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        }
    }

    public ConverterAutoComplete getConverterLiquidacao() {
        if (converterLiquidacao == null) {
            converterLiquidacao = new ConverterAutoComplete(Liquidacao.class, pagamentoFacade.getLiquidacaoFacade());
        }
        return converterLiquidacao;
    }


    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public List<Conta> completaContaExtraorcamentaria(String parte) {
        return pagamentoFacade.getContaFacade().listaFiltrandoContaExtraPorTipoDeContaExtra(parte.trim(), sistemaControlador.getExercicioCorrente(), TipoContaExtraorcamentaria.DEPOSITOS_CONSIGNACOES);
    }

    public ConverterAutoComplete getConverterContaExtraorcamentaria() {
        if (converterContaExtraorcamentaria == null) {
            converterContaExtraorcamentaria = new ConverterAutoComplete(Conta.class, pagamentoFacade);
        }
        return converterContaExtraorcamentaria;
    }

    public List<FonteDeRecursos> completaFonteDeRecursos(String parte) {
        return pagamentoFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicioETipo(parte.trim(), sistemaControlador.getExercicioCorrente(), TipoFonteRecurso.RETENCAO);
    }

    public ConverterAutoComplete getConverterFonteDeRecursos() {
        if (converterFonteDeRecursos == null) {
            converterFonteDeRecursos = new ConverterAutoComplete(FonteDeRecursos.class, pagamentoFacade);
        }
        return converterFonteDeRecursos;
    }

    public UsuarioSistema getUsuarioSistema() {
        return sistemaControlador.getUsuarioCorrente();
    }

    public void verificaContaExtraNaRetencao() {
        Conta contaRetencao = null;
        Conta contaSelecionada = null;
        if (!selecionado.getRetencaoPgtos().isEmpty()) {
            for (RetencaoPgto r : selecionado.getRetencaoPgtos()) {
                contaRetencao = retencaoPgto.getContaExtraorcamentaria();
                contaSelecionada = r.getContaExtraorcamentaria();
            }
            if (contaRetencao.equals(contaSelecionada)) {
                FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " A Conta Extraorçamentária: " + retencaoPgto.getContaExtraorcamentaria() + " já foi associada a uma retenção adicionada na lista.");
            }
        }
    }

    public void adicionaRetencao() {
        try {
            if (!selecionado.getRetencaoPgtos().isEmpty()) {
                for (RetencaoPgto r : selecionado.getRetencaoPgtos()) {
                    BigDecimal b = new BigDecimal(r.getNumero());
                    retencaoPgto.setNumero(b.add(BigDecimal.ONE).toString());
                }
            } else {
                retencaoPgto.setNumero(pagamentoFacade.retornaUltimoNumeroRetencao());
            }
            retencaoPgto.setDataRetencao(((Pagamento) selecionado).getPrevistoPara());
            retencaoPgto.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
            retencaoPgto.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
            if (Util.validaCampos(retencaoPgto)
                && validaValorRetencao(retencaoPgto)) {
                verificaContaExtraNaRetencao();
                validarRetencaoREINF();
                setaTipoConsignacao();
                retencaoPgto.setPagamento(selecionado);
                retencaoPgto.setEfetivado(Boolean.FALSE);
                selecionado.getRetencaoPgtos().add(retencaoPgto);
                setaValorFinalPagto();
                retencaoPgto = new RetencaoPgto();
                retencaoPgto.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
                retencaoPgto.setPagamento(selecionado);
                lancouRetencao = Boolean.TRUE;
                retencaoPgto.setFonteDeRecursos(getContaDeDestinacaoEmpenho().getFonteDeRecursos());
                retencaoPgto.setSubConta(selecionado.getSubConta());
                recuperarValorRetencoes();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private Boolean validaValorRetencao(RetencaoPgto retencaoPgto) {
        Boolean retorno = false;
        if (retencaoPgto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor da Retenção deve ser maior que zero(0).");
            return false;
        }
        if (retencaoPgto.getValor() != null && selecionado.getValor() != null) {
            if ((getSomaRetencoes().add(retencaoPgto.getValor())).compareTo(selecionado.getValor()) <= 0) {
                retorno = true;
            } else {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " valor de " + Util.formataValor(retencaoPgto.getValor()) + " da(s) Retenção(ões) é maior que o valor de " + Util.formataValor(selecionado.getValorFinal()) + " do Pagamento.");
            }
        }
        return retorno;
    }

    public void setaTipoConsignacao() throws ExcecaoNegocioGenerica {
        retencaoPgto = atualizarTipoConsignacao(retencaoPgto);
    }

    public RetencaoPgto atualizarTipoConsignacao(RetencaoPgto retencaoPgto) throws ExcecaoNegocioGenerica {
        try {
            Empenho empenho = selecionado.getLiquidacao().getEmpenho();
            String elemento = empenho.getDespesaORC().getContaDeDespesa().getCodigoSemZerosAoFinal().substring(7, 9);
            if (empenho != null) {
                if (CategoriaOrcamentaria.NORMAL.equals(empenho.getCategoriaOrcamentaria())) {
                    if (TipoContaDespesa.PRECATORIO.equals(empenho.getTipoContaDespesa())) {
                        retencaoPgto.setTipoConsignacao(TipoConsignacao.CONSIGNACAO_AMORTIZACAO_PRECATORIO_EXERCICIO);
                    } else if (elemento.equals("71") && TipoContaDespesa.DIVIDA_PUBLICA.equals(empenho.getTipoContaDespesa()) && SubTipoDespesa.VALOR_PRINCIPAL.equals(empenho.getSubTipoDespesa())) {
                        retencaoPgto.setTipoConsignacao(TipoConsignacao.CONSIGNACAO_AMORTIZACAO_DIVIDA_PUBLICA_EXERCICIO);
                    } else if (elemento.equals("21") && TipoContaDespesa.DIVIDA_PUBLICA.equals(empenho.getTipoContaDespesa()) && SubTipoDespesa.JUROS.equals(empenho.getSubTipoDespesa())) {
                        retencaoPgto.setTipoConsignacao(TipoConsignacao.CONSIGNACAO_JUROS_DIVIDA_PUBLICA_EXERCICIO);
                    } else if (elemento.equals("22") && TipoContaDespesa.DIVIDA_PUBLICA.equals(empenho.getTipoContaDespesa()) && SubTipoDespesa.OUTROS_ENCARGOS.equals(empenho.getSubTipoDespesa())) {
                        retencaoPgto.setTipoConsignacao(TipoConsignacao.CONSIGNACAO_ENCARGOS_DIVIDA_PUBLICA_EXERCICIO);
                    } else {
                        retencaoPgto.setTipoConsignacao(TipoConsignacao.EXERCICIO);
                    }
                } else {
                    if (empenho.getTipoRestosProcessados().equals(TipoRestosProcessado.PROCESSADOS)) {
                        retencaoPgto.setTipoConsignacao(TipoConsignacao.RESTO_PAGAR_PROCESSADO);
                    }
                    if (empenho.getTipoRestosProcessados().equals(TipoRestosProcessado.NAO_PROCESSADOS)) {
                        retencaoPgto.setTipoConsignacao(TipoConsignacao.RESTO_PAGAR_NAO_PROCESSADO);
                    }
                }
            }
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Erro: " + e.getMessage());
        }
        return retencaoPgto;
    }

    public Boolean desabilitarTipoDocumentoParaGuias() {
        if (!selecionado.getGuiaPagamento().isEmpty()) {
            return true;
        }
        return false;
    }


    public void editarRetencao(RetencaoPgto retencao) {
        retencaoPgto = retencao;
        selecionado.getRetencaoPgtos().remove(retencao);
    }

    public void removeRetencao(ActionEvent evt) {
        RetencaoPgto re = (RetencaoPgto) evt.getComponent().getAttributes().get("ret");
        Pagamento p = ((Pagamento) selecionado);
        p.getRetencaoPgtos().remove(re);
        setaValorFinalPagto();
    }

    public void adicionarGuiaFatura() {
        try {
            Util.validarCampos(guiaPagamento.getGuiaFatura());
            validarCodigoBarrasGuiaFatura();
            adicionarGuiaPagamento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarGuiaConvenio() {
        try {
            Util.validarCampos(guiaPagamento.getGuiaConvenio());
            validarCodigoBarrasGuiaConvenio();
            adicionarGuiaPagamento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarGuiaGPS() {
        try {
            Util.validarCampos(guiaPagamento);
            Util.validarCampos(guiaPagamento.getGuiaGPS());
            adicionarGuiaPagamento();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:pessoaGps_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarGuiaDARF() {
        try {
            Util.validarCampos(guiaPagamento);
            Util.validarCampos(guiaPagamento.getGuiaDARF());
            adicionarGuiaPagamento();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:pessoaDarf_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarGuiaDARFSimples() {
        try {
            Util.validarCampos(guiaPagamento);
            Util.validarCampos(guiaPagamento.getGuiaDARFSimples());
            adicionarGuiaPagamento();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:pessoaDarfSimples_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarGuiaGRU() {
        try {
            validarGuiaGRU();
            adicionarGuiaPagamento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarGuiaGRU() {
        ValidacaoException ve = new ValidacaoException();
        if (guiaPagamento.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pessoa deve ser informado.");
        }
        ve.lancarException();
        Util.validarCampos(guiaPagamento.getGuiaGRU());
    }

    private void adicionarGuiaPagamento() {
        guiaPagamento.setPagamento(selecionado);
        Util.adicionarObjetoEmLista(selecionado.getGuiaPagamento(), guiaPagamento);
        cancelarGuiaPagamento();
    }

    public void getValorGuiaConvenio() {
        try {
            validarCodigoBarrasGuiaConvenio();
            if (TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocPagto())) {
                String valorCodigoBarras = guiaPagamento.getGuiaConvenio().getCodigoBarra().substring(4, 11);
                valorCodigoBarras += guiaPagamento.getGuiaConvenio().getCodigoBarra().substring(14, 18);
                valorCodigoBarras = valorCodigoBarras.substring(0, valorCodigoBarras.length() - 2) + "." + valorCodigoBarras.substring(valorCodigoBarras.length() - 2, valorCodigoBarras.length());
                guiaPagamento.getGuiaConvenio().setValor(new BigDecimal(valorCodigoBarras));

            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarGuiaPagamento(GuiaPagamento guia) {
        guiaPagamento = (GuiaPagamento) Util.clonarObjeto(guia);
    }

    public void removerGuiaPagamento(GuiaPagamento guia) {
        selecionado.getGuiaPagamento().remove(guia);
    }

    private void validarCodigoBarrasGuiaFatura() {
        ValidacaoException ve = new ValidacaoException();
        String codigoBarras = guiaPagamento.getGuiaFatura().getCodigoBarra();
        if (codigoBarras.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo código de barras deve ser informado.");
        }
        ve.lancarException();
        if (codigoBarras.startsWith("8")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O primeiro dígito informado(8), não pertence a uma guia do tipo " + selecionado.getTipoDocPagto().getDescricao() + ".");
        }
        if (codigoBarras.length() >= 3) {
            String tresPrimeirosDigitos = codigoBarras.substring(0, 3);
            if (!pagamentoFacade.getBancoFacade().verificarNumeroBanco(tresPrimeirosDigitos)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Os três primeiros dígitos informados<b>(" + tresPrimeirosDigitos + ")</b>, não fazem referência a um código dos bancos cadastrados no sistema.");
            }
        }
        if (codigoBarras.length() >= 4) {
            String quartoDigito = codigoBarras.substring(3, 4);
            if (!quartoDigito.equals("9")) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O quarto dígito informado(" + quartoDigito + "), não faz referência ao código da moeda(R$) estabelecido pelo Banco Central. Por padrão esse dígito dever ser '9'.");
            }
        }
        if (codigoBarras.length() < 54) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Código de barras não foi preenchido com todos os dígitos necessários.");
        }
        for (GuiaPagamento guia : selecionado.getGuiaPagamento()) {
            if (codigoBarras.equals(guia.getGuiaFatura().getCodigoBarra()) && !guia.equals(guiaPagamento)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Código de barras informado pertence a uma guia adicionada na lista.");
            }
        }
        ve.lancarException();
        String bloco01 = codigoBarras.substring(0, 10).replace(".", "");
        Integer digitoBloco01 = new Integer(codigoBarras.substring(10, 11));

        String bloco02 = codigoBarras.substring(12, 23).replace(".", "");
        Integer digitoBloco02 = new Integer(codigoBarras.substring(23, 24));

        String bloco03 = codigoBarras.substring(25, 36).replace(".", "");
        Integer digitoBloco03 = new Integer(codigoBarras.substring(36, 37));

        if (!digitoBloco01.equals(CarneUtil.modulo10(bloco01))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Primeiro bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
        }
        if (!digitoBloco02.equals(CarneUtil.modulo10(bloco02))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Segundo bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
        }
        if (!digitoBloco03.equals(CarneUtil.modulo10(bloco03))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Terceiro bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
        }
        ve.lancarException();
    }


    private void validarCodigoBarrasGuiaConvenio() {
        ValidacaoException ve = new ValidacaoException();
        String terceiroDigitoDoCodigo = "";
        String codigoBarras = guiaPagamento.getGuiaConvenio().getCodigoBarra();
        if (codigoBarras.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo código de barras deve ser informado.");
        }
        ve.lancarException();
        if (!codigoBarras.startsWith("8")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O primeiro dígito informado(" + codigoBarras.substring(0, 1) + "), não pertence a uma guia do tipo " + selecionado.getTipoDocPagto().getDescricao() + ".");
        }
        if (codigoBarras.length() >= 3) {
            terceiroDigitoDoCodigo = codigoBarras.substring(2, 3);
            if (!"6".equals(terceiroDigitoDoCodigo) && !"7".equals(terceiroDigitoDoCodigo) && !"8".equals(terceiroDigitoDoCodigo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O terceiro dígito informado(" + terceiroDigitoDoCodigo + "), não faz referência ao padrão de guia do tipo " + selecionado.getTipoDocPagto().getDescricao() + ". Por padrão, esse dígito deve ser '6' , '7' ou '8'");
            }
        }
        if (codigoBarras.length() < 55) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Código de barras não foi preenchido com todos os dígitos.");
        }
        for (GuiaPagamento guia : selecionado.getGuiaPagamento()) {
            if (codigoBarras.equals(guia.getGuiaConvenio().getCodigoBarra()) && !guia.equals(guiaPagamento)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Código de barras informado pertence a uma guia adicionada na lista.");
            }
        }
        ve.lancarException();
        if (!terceiroDigitoDoCodigo.startsWith("8")) {
            String bloco01 = codigoBarras.substring(0, 11).replace(".", "");
            Integer digitoBloco01 = new Integer(codigoBarras.substring(12, 13));

            String bloco02 = codigoBarras.substring(14, 25).replace(".", "");
            Integer digitoBloco02 = new Integer(codigoBarras.substring(26, 27));

            String bloco03 = codigoBarras.substring(28, 39).replace(".", "");
            Integer digitoBloco03 = new Integer(codigoBarras.substring(40, 41));

            String bloco04 = codigoBarras.substring(42, 53).replace(".", "");
            Integer digitoBloco04 = new Integer(codigoBarras.substring(54, 55));

            if (!digitoBloco01.equals(CarneUtil.modulo10(bloco01))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Primeiro bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
            }
            if (!digitoBloco02.equals(CarneUtil.modulo10(bloco02))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Segundo bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
            }
            if (!digitoBloco03.equals(CarneUtil.modulo10(bloco03))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Terceiro bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
            }
            if (!digitoBloco04.equals(CarneUtil.modulo10(bloco04))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Quarto bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
            }
        }
        ve.lancarException();
    }

    public void getValorGuiaFatura() {
        try {
            validarCodigoBarrasGuiaFatura();
            if (TipoDocPagto.FATURA.equals(selecionado.getTipoDocPagto())) {
                String valorCodigoBarras = guiaPagamento.getGuiaFatura().getCodigoBarra().substring(44, 54);
                valorCodigoBarras = valorCodigoBarras.substring(0, valorCodigoBarras.length() - 2) + "." + valorCodigoBarras.substring(valorCodigoBarras.length() - 2, valorCodigoBarras.length());
                guiaPagamento.getGuiaFatura().setValorNominal(new BigDecimal(valorCodigoBarras));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public void validarSaldoLiquidacao() {
        ValidacaoException ve = new ValidacaoException();
        BigDecimal valor = selecionado.getValor();
        if (selecionado.getId() == null) {
            if (selecionado.getLiquidacao().getSaldo().compareTo(valor) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Valor do pagamento de <b>" + Util.formataValor(valor) + "</b> é maior que o saldo de <b>" + Util.formataValor(selecionado.getLiquidacao().getSaldo()) + "</b> disponível na liquidação.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public Boolean validaCampoLiquidacao() {
        Boolean retorno = true;
        if (selecionado.getLiquidacao() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo " + getTituloLiquidacao() + " deve ser informado.");
            retorno = false;
        }
        return retorno;
    }

    public Boolean validaContaBancariaPessoa() {
        Boolean retorno = true;
        if (selecionado.getContaPgto() == null
            && gestorFinanceiro) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Conta Bancária da Pessoa deve ser informadoo.");
            retorno = false;
        }
        return retorno;
    }

    public void validarFinalidadePagamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getSubConta().getFinalidadeOB() != null
            && selecionado.getSubConta().getFinalidadeOB()) {
            if (selecionado.getFinalidadePagamento().getCodigoOB() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Finalidade do Pagamento informada: <b>" + selecionado.getFinalidadePagamento() + "</b>, não possui um Código da O.B. em seu cadastro.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void validaData(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        Calendar dataPagto = Calendar.getInstance();
        dataPagto.setTime(data);
        Integer ano = sistemaControlador.getExercicioCorrente().getAno();
        if (selecionado.getLiquidacao() != null) {
            if (selecionado.getLiquidacao().getDataLiquidacao().after(data)) {
                message.setDetail(" Data do Pagamento menor que data da Liquidação.");
                message.setSummary(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao());
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
            if (dataPagto.get(Calendar.YEAR) != ano) {
                message.setDetail("Ano diferente do exercício corrente.");
                message.setSummary(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao());
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public void validaCategoriaContaExtra(FacesContext context, UIComponent component, Object value) {
        FacesMessage msg = new FacesMessage();
        Conta cc = (Conta) value;
        if (cc != null) {
            cc = pagamentoFacade.getContaFacade().recuperar(cc.getId());
            if (cc.getCategoria() != null) {
                if (cc.getCategoria().equals(CategoriaConta.SINTETICA)) {
                    msg.setDetail(" Conta Sintetica, não pode ser utilizada.");
                    msg.setDetail(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao());
                    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(msg);
                }
            } else {
                msg.setDetail(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao());
                msg.setSummary(" Para utilizar a conta, defina sua categoria em seu cadastro.");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void setaSaldo() {
        ((Pagamento) selecionado).setSaldo(((Pagamento) selecionado).getValor());
        setaValorFinalPagto();
    }

    public void selecionarLiquidacao(ActionEvent evento) {
        selecionado.setLiquidacao((Liquidacao) evento.getComponent().getAttributes().get("objeto"));
        selecionado.setComplementoHistorico(selecionado.getLiquidacao().getComplemento());
        prepararVariaveis();
    }

    private void prepararVariaveis() {
        if (selecionado.getLiquidacao().getEmpenho().getDespesaORC() != null) {
            componenteTreeDespesaORC.setCodigo(selecionado.getLiquidacao().getEmpenho().getDespesaORC().getCodigoReduzido());
            componenteTreeDespesaORC.setDespesaORCSelecionada(selecionado.getLiquidacao().getEmpenho().getDespesaORC());
            componenteTreeDespesaORC.setDespesaSTR(pagamentoFacade.getEmpenhoFacade().getDespesaORCFacade().recuperaStrDespesaPorId(selecionado.getLiquidacao().getEmpenho().getDespesaORC().getId()).getConta());
            recuperarContaDoFornecedor();
            recuperarValores();
            recuperaEventoContabil();
            atribuirValorPagamento(BigDecimal.ZERO);
            verificaPagamentoAbertoParaMesmaLiquidacao();
            novoDetalhamento();
            buscarAdicionarDetalhamento();
            atualizarRetencaoPagtoComLiquidacao();
        }
    }

    private void atualizarRetencaoPagtoComLiquidacao() {
        ContaDeDestinacao contaDeDestinacaoEmpenho = getContaDeDestinacaoEmpenho();
        if (contaDeDestinacaoEmpenho != null) {
            retencaoPgto.setFonteDeRecursos(contaDeDestinacaoEmpenho.getFonteDeRecursos());
        }
        ConfiguracaoContabil configuracaoContabil = pagamentoFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
        if (isContaDespesaEmpenhoReinf() && configuracaoContabil != null &&
            !selecionado.getLiquidacao().getDoctoFiscais().isEmpty()) {
            LiquidacaoDoctoFiscal documentoFiscal = selecionado.getLiquidacao().getDoctoFiscais().get(0);
            int numeroRetencao = 1;
            if (documentoFiscal.getRetencaoPrevidenciaria()) {
                adicionarNovaRetencaoInssComDadosDoctFiscalLiquidacao(documentoFiscal, configuracaoContabil, numeroRetencao);
                numeroRetencao++;
            }
            if (!documentoFiscal.getOptanteSimplesNacional()) {
                adicionarNovaRetencaoIrrfComDadosDoctFiscalLiquidacao(documentoFiscal, configuracaoContabil, numeroRetencao);
            }
        }
    }

    private void adicionarNovaRetencaoInssComDadosDoctFiscalLiquidacao(LiquidacaoDoctoFiscal documentoFiscal, ConfiguracaoContabil configuracaoContabil, Integer numeroRetencao) {
        RetencaoPgto retInss = new RetencaoPgto();
        retInss.setFonteDeRecursos(retencaoPgto.getFonteDeRecursos());
        retInss.setContaExtraorcamentaria(documentoFiscal.getContaExtra());
        retInss.setPorcentagem(documentoFiscal.getPorcentagemRetencaoMaxima());
        retInss.setPessoa(configuracaoContabil.getPessoaInssPadraoDocLiq());
        retInss.setClasseCredor(configuracaoContabil.getClasseCredInssPadraoDocLiq());
        retInss.setValor(documentoFiscal.getValorRetido());
        retInss.setNumero(numeroRetencao.toString());
        retInss.setDataRetencao(selecionado.getPrevistoPara());
        retInss.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        retInss.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        retInss = atualizarTipoConsignacao(retInss);
        retInss.setPagamento(selecionado);
        retInss.setEfetivado(Boolean.FALSE);
        if (retInss.getValor() != null && retInss.getValor().compareTo(BigDecimal.ZERO) > 0) {
            selecionado.getRetencaoPgtos().add(retInss);
            setaValorFinalPagto();
            recuperarValorRetencoes();
            lancouRetencao = Boolean.TRUE;
        }
    }

    private void adicionarNovaRetencaoIrrfComDadosDoctFiscalLiquidacao(LiquidacaoDoctoFiscal documentoFiscal, ConfiguracaoContabil configuracaoContabil, Integer numeroRetencao) {
        RetencaoPgto retIrrf = new RetencaoPgto();
        retIrrf.setFonteDeRecursos(retencaoPgto.getFonteDeRecursos());
        retIrrf.setContaExtraorcamentaria(documentoFiscal.getContaExtraIrrf());
        retIrrf.setPorcentagem(documentoFiscal.getPorcentagemRetencaoMaximaIrrf());
        retIrrf.setPessoa(configuracaoContabil.getPessoaIrrfPadraoDocLiq());
        retIrrf.setClasseCredor(configuracaoContabil.getClasseCredIrrfPadraoDocLiq());
        retIrrf.setValor(documentoFiscal.getValorRetidoIrrf());
        retIrrf.setNumero(numeroRetencao.toString());
        retIrrf.setDataRetencao(selecionado.getPrevistoPara());
        retIrrf.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        retIrrf.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        retIrrf = atualizarTipoConsignacao(retIrrf);
        retIrrf.setPagamento(selecionado);
        retIrrf.setEfetivado(Boolean.FALSE);
        if (retIrrf.getValor() != null && retIrrf.getValor().compareTo(BigDecimal.ZERO) > 0) {
            selecionado.getRetencaoPgtos().add(retIrrf);
            setaValorFinalPagto();
            recuperarValorRetencoes();
            lancouRetencao = Boolean.TRUE;
        }
    }

    private void atribuirValorPagamento(BigDecimal valor) {
        selecionado.setValor(valor);
        selecionado.setValorFinal(valor);
        setaSaldo();
    }

    private void buscarAdicionarDetalhamento() {
        List<Desdobramento> desdobramentos = buscarDesdobramento("");
        if (desdobramentos != null && desdobramentos.size() == 1) {
            this.desdobramento.setDesdobramento(desdobramentos.get(0));
            atribuirValorSaldoDetalhamento();
        }
    }

    public void atribuirValorSaldoDetalhamento() {
        this.desdobramento.setValor(this.desdobramento.getDesdobramento().getSaldo());
        this.desdobramento.setSaldo(this.desdobramento.getDesdobramento().getSaldo());
    }

    private void verificaPagamentoAbertoParaMesmaLiquidacao() {
        List<Pagamento> pagamentos = pagamentoFacade.listaPagamentoAbertoPorLiquidacao(selecionado.getLiquidacao());
        if (!pagamentos.isEmpty()) {
            FacesUtil.addAtencao("Existe(m) " + pagamentos.size() + " pagamento(s) com situação ABERTO para a Liquidação " + selecionado.getLiquidacao() + ".");
        }
    }

    private void recuperarContaDoFornecedor() {
        Pessoa pessoa = pagamentoFacade.getPessoaFacade().recuperar(selecionado.getLiquidacao().getEmpenho().getFornecedor().getId());
        ClasseCredor classeCredor = pagamentoFacade.getClasseCredorFacade().recuperar(selecionado.getLiquidacao().getEmpenho().getClasseCredor().getId());
        ConfigContaBancariaPessoa configContaBancariaPessoa = pagamentoFacade.getConfigContaBancariaPessoaFacade().recuperarConfiguracaoContaBancariaPessoa(pessoa, classeCredor);
        if (configContaBancariaPessoa != null) {
            selecionado.setContaPgto(configContaBancariaPessoa.getContaCorrenteBancaria());
        } else {
            for (ContaCorrenteBancPessoa c : pessoa.getContaCorrenteBancPessoas()) {
                if (c.getPrincipal() != null) {
                    if (c.getPrincipal()) {
                        if (c.getContaCorrenteBancaria().getSituacao() != null) {
                            if (c.getContaCorrenteBancaria().getSituacao().equals(SituacaoConta.ATIVO)) {
                                selecionado.setContaPgto(c.getContaCorrenteBancaria());
                                selecionarContaBancaria(c);
                            }
                        }
                    }
                }
            }
        }
        if (selecionado.getContaPgto() == null) {
            FacesUtil.addWarn(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Não foi possivel encontrar uma Conta Bancária marcada como <b>Principal</b> para o(a) " + pessoa.getNomeCpfCnpj() + ".");
        }
    }

    public void setaLiquidacao(SelectEvent evt) {
        try {
            Liquidacao li = (Liquidacao) evt.getObject();
            selecionado.setLiquidacao(li);
            selecionado.setComplementoHistorico(li.getComplemento());
            prepararVariaveis();
        } catch (Exception ex) {
        }
    }

    public void recuperaEventoContabil() {
        try {
            selecionado.setEventoContabil(null);
            Empenho empenho = selecionado.getLiquidacao().getEmpenho();
            ContaDespesa conta = (ContaDespesa) empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa();
            if (getCategoriaOrcamentariaPagamento()) {
                ConfigPagamento configuracao = pagamentoFacade.getConfigPagamentoFacade().recuperaEventoPorContaDespesa(conta, TipoLancamento.NORMAL, empenho.getTipoContaDespesa(), selecionado.getPrevistoPara());
                Preconditions.checkNotNull(configuracao, "Configuração contábil não encontrada para os parametros informados.");
                selecionado.setEventoContabil(configuracao.getEventoContabil());
            } else {
                ConfigPagamentoRestoPagar configuracaoRestoPagar = pagamentoFacade.getConfigPagamentoRestoPagarFacade().recuperaEventoRestoPorContaDespesa(conta, TipoLancamento.NORMAL, selecionado.getPrevistoPara(), empenho.getTipoContaDespesa(), empenho.getTipoRestosProcessados());
                Preconditions.checkNotNull(configuracaoRestoPagar, "Configuração contábil não encontrada para os parametros informados.");
                selecionado.setEventoContabil(configuracaoRestoPagar.getEventoContabil());
            }
        } catch (Exception e) {
            FacesUtil.addError("Evento não Encontrado! ", e.getMessage());
        }
    }

    public void recuperarValores() throws ExcecaoNegocioGenerica {
        try {
            if (selecionado.getLiquidacao() != null) {
                if (selecionado.getLiquidacao().getEmpenho().getDespesaORC() != null) {
                    vwHierarquiaDespesaORCTemp = pagamentoFacade.getLiquidacaoFacade().getDespesaORCFacade().recuperaVwDespesaOrc(selecionado.getLiquidacao().getEmpenho().getDespesaORC(), sistemaControlador.getDataOperacao());
                }
            }
        } catch (ExcecaoNegocioGenerica e) {
            e.getStackTrace();
        }
    }

    private boolean validavwHierarquiaDespesaORCTemp() {
        return ((vwHierarquiaDespesaORCTemp != null) && (selecionado.getLiquidacao() != null));
    }

    public String getFunionalStr() {
        String toReturn = "";
        if (validavwHierarquiaDespesaORCTemp()) {
            toReturn = vwHierarquiaDespesaORCTemp.getSubAcao();
        }
        return toReturn;
    }

    public String getConta() {
        String toReturn = "";
        if (validavwHierarquiaDespesaORCTemp()) {
            toReturn = vwHierarquiaDespesaORCTemp.getConta();
        }
        return toReturn;
    }

    public String getHoOrgao() {
        String toReturn = "";
        if (validavwHierarquiaDespesaORCTemp()) {
            toReturn = vwHierarquiaDespesaORCTemp.getOrgao();
        }
        return toReturn;
    }

    public String getHoUnidade() {
        String toReturn = "";
        if (validavwHierarquiaDespesaORCTemp()) {
            toReturn = vwHierarquiaDespesaORCTemp.getUnidade();
        }
        return toReturn;
    }

    public String getDespesa() {
        String toReturn = "";
        if (validavwHierarquiaDespesaORCTemp()) {
            toReturn = vwHierarquiaDespesaORCTemp.getConta();
        }
        return toReturn;
    }

    public Boolean getVerificaEdicao() {
        if ((operacao.equals(Operacoes.EDITAR))
            && (selecionado.getStatus() != StatusPagamento.ABERTO)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean podeEditarGuias() {
        if (StatusPagamento.ABERTO.equals(selecionado.getStatus())
            || StatusPagamento.INDEFERIDO.equals(selecionado.getStatus())) {
            return false;
        }
        return true;
    }

    public Boolean getVisualizarEstornos() {
        if (selecionado.getId() != null
            && operacao.equals(Operacoes.EDITAR)) {
            return true;
        }
        return false;
    }

    public BigDecimal getSaldoLiquidacao() {
        BigDecimal saldo = new BigDecimal(BigInteger.ZERO);
        if (selecionado.getLiquidacao() != null) {
            saldo = selecionado.getLiquidacao().getSaldo();
            return saldo;
        }
        return saldo;
    }

    public ContaDeDestinacao getContaDeDestinacaoEmpenho() {
        try {
            if (selecionado.getLiquidacao() != null) {
                if (selecionado.getCategoriaOrcamentaria().isNormal()) {
                    return (ContaDeDestinacao) selecionado.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                } else {
                    return (ContaDeDestinacao) selecionado.getLiquidacao().getEmpenho().getContaDeDestinacao();
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar a conta de destinacao no pagamento: {}", e);
        }
        return null;
    }

    public void setarFocoClassePessoaRetencao() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:classePessaoRet_input')");
    }

    public List<Pessoa> completaPessoaRetencao(String parte) {
        return pagamentoFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return pagamentoFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte, retencaoPgto.getPessoa());

    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pagamentoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public void setaPessoa(SelectEvent evt) {
        Pessoa p = (Pessoa) evt.getObject();
        retencaoPgto.setPessoa(p);
        retencaoPgto.setClasseCredor(null);
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, pagamentoFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public void copiarHistoricoUsuario() {
        if (selecionado.getHistoricoContabil() != null) {
            selecionado.setComplementoHistorico(selecionado.getHistoricoContabil().toStringAutoComplete());
            selecionado.setHistoricoContabil(null);
        }
    }

    public void setarPessoa(ActionEvent evento) {
        retencaoPgto.setPessoa((Pessoa) evento.getComponent().getAttributes().get("objeto"));
        indiceAba = 2;
    }

    public void setarFinalidade(ActionEvent evento) {
        selecionado.setFinalidadePagamento((FinalidadePagamento) evento.getComponent().getAttributes().get("objeto"));
    }

    public void setaNull() {
        retencaoPgto.setContaExtraorcamentaria(null);
        retencaoPgto.setClasseCredor(null);
    }

    public void confirmaLancarRetencao() {
        indiceAba = 1;
        FacesUtil.executaJavaScript("dialogRetencao.hide()");
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:contaExtraorcamentariax_input')");
    }

    public List<ContaCorrenteBancPessoa> contaBancariaPessoaEmpenho() {
        if (selecionado.getLiquidacao() != null) {
            return pagamentoFacade.getContaCorrenteBancPessoaFacade().listaContasBancariasPorPessoa(selecionado.getLiquidacao().getEmpenho().getFornecedor(), SituacaoConta.ATIVO);
        }
        return new ArrayList<>();
    }

    public void selecionarContaBancaria(ContaCorrenteBancPessoa contaCorrenteBancPessoa) {
        selecionado.setContaPgto(contaCorrenteBancPessoa.getContaCorrenteBancaria());
        setaNullTipoOperacaoPagamento();
    }

    public void setaNullTipoOperacaoPagamento() {
        retencaoPgto.setSubConta(selecionado.getSubConta());
        selecionado.setTipoOperacaoPagto(null);
    }

    public void removeContaBancariaFornecedor() {
        selecionado.setContaPgto(null);
    }

    public void setarContaBancaria() {
        try {
            contaBancariaEntidade = selecionado.getSubConta().getContaBancariaEntidade();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:btnVisualizarContas')");
        } catch (Exception e) {

        }
    }

    public void listenerSetaValores() {
        setarContaFinanceiraNaRetencao();
        setarContaBancaria();
        alterarContaFinanceiraRetencao();
        codigoCO = null;
    }

    public void alterarContaFinanceiraRetencao() {
        if (!selecionado.getRetencaoPgtos().isEmpty()) {
            for (RetencaoPgto retencao : selecionado.getRetencaoPgtos()) {
                retencao.setSubConta(selecionado.getSubConta());
            }
        }
    }

    public boolean renderizarRetencao() {
        if (selecionado.getId() != null) {
            if (!selecionado.getStatus().equals(StatusPagamento.ABERTO)) {
                return true;
            }
        }
        return false;
    }


    public void abrirDialogConciliar() {
        FacesUtil.executaJavaScript("dialogConciliar.show()");
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public RetencaoPgto getRetencaoPgto() {
        return retencaoPgto;
    }

    public void setRetencaoPgto(RetencaoPgto retencaoPgto) {
        this.retencaoPgto = retencaoPgto;
    }

    public Boolean getViewPanel() {
        return viewPanel;
    }

    public void setViewPanel(Boolean viewPanel) {
        this.viewPanel = viewPanel;
    }

    public DespesaORC getDespesaORCFuncional() {
        return despesaORCFuncional;
    }

    public void setDespesaORCFuncional(DespesaORC despesaORCFuncional) {
        this.despesaORCFuncional = despesaORCFuncional;
    }

    public boolean isVisivelFuncional() {
        return operacao.equals(Operacoes.EDITAR);
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
    }

    public UnidadeOrganizacional getUnidadeUsuario() {
        return sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente();
    }

    public Boolean getLancouRetencao() {
        return lancouRetencao;
    }

    public void setLancouRetencao(Boolean lancouRetencao) {
        this.lancouRetencao = lancouRetencao;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public Boolean getPagarPagamento() {
        return pagarPagamento;
    }

    public void setPagarPagamento(Boolean pagarPagamento) {
        this.pagarPagamento = pagarPagamento;
    }

    public UsuarioUnidadeOrganizacional getUsuarioUnidadeOrganizacional() {
        return usuarioUnidadeOrganizacional;
    }

    public void setUsuarioUnidadeOrganizacional(UsuarioUnidadeOrganizacional usuarioUnidadeOrganizacional) {
        this.usuarioUnidadeOrganizacional = usuarioUnidadeOrganizacional;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public void setarContaFinanceiraNaRetencao() {
        retencaoPgto.setSubConta(selecionado.getSubConta());
    }

    public Boolean podeIndeferirPagamento() {
        if (selecionado.getId() != null && gestorFinanceiro) {
            if (selecionado.isPagamentoDeferido()
                || selecionado.isPagamentoEfetuado()) {
                return true;
            }
        }
        return false;
    }

    public Boolean mostrarBotaoBaixar() {
        if (selecionado.getId() != null) {
            if (selecionado.isPagamentoDeferido() || selecionado.isPagamentoIndeferido()) {
                return true;
            }
            if (gestorFinanceiro) {
                if (selecionado.isPagamentoIndeferido()
                    || selecionado.isPagamentoDeferido()
                    || selecionado.isPagamentoEfetuado()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean mostrarBotaoEstornarBaixar() {
        if (selecionado.getId() != null) {
            if (selecionado.isPagamentoPago()) {
                return true;
            }
            if (gestorFinanceiro && selecionado.isPagamentoPago()) {
                return true;
            }
        }
        return false;
    }

    public Boolean mostrarBotaoConciliar() {
        return isOperacaoEditar()
            && selecionado.getStatus().equals(StatusPagamento.PAGO)
            && selecionado.getDataConciliacao() == null;
    }

    public Boolean mostrarBotaoEstornoConciliacao() {
        return isOperacaoEditar()
            && selecionado.isPagamentoPago()
            && selecionado.getDataConciliacao() != null;
    }

    public Boolean mostrarPainelGuiaFatura() {
        return selecionado.getTipoDocPagto() != null && TipoDocPagto.FATURA.equals(selecionado.getTipoDocPagto());
    }

    public Boolean mostrarPainelGuiaConvenio() {
        return selecionado.getTipoDocPagto() != null && TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocPagto());
    }

    public Boolean mostrarPainelGuiaGPS() {
        return selecionado.getTipoDocPagto() != null && TipoDocPagto.GPS.equals(selecionado.getTipoDocPagto());
    }

    public Boolean mostrarPainelGuiaDARF() {
        return selecionado.getTipoDocPagto() != null && TipoDocPagto.DARF.equals(selecionado.getTipoDocPagto());
    }

    public Boolean mostrarPainelGuiaDARFSimples() {
        return selecionado.getTipoDocPagto() != null && TipoDocPagto.DARF_SIMPLES.equals(selecionado.getTipoDocPagto());
    }

    public Boolean mostrarPainelGuiaGRU() {
        return TipoDocPagto.GRU.equals(selecionado.getTipoDocPagto());
    }

    public void cancelarGuiaPagamento() {
        guiaPagamento = null;
    }

    public void novaGuiaPagamento() {
        guiaPagamento = new GuiaPagamento(selecionado);
    }

    public void baixar() {
        try {
            realizarValidacoes();
            validarDataConciliacao();
            pagamentoFacade.baixar(selecionado, StatusPagamento.PAGO);
            FacesUtil.addOperacaoRealizada(" O Pagamento N° <b>" + selecionado.getNumeroPagamento() + "</b> foi baixado com sucesso.");
            FacesUtil.executaJavaScript("dialogDeferir.hide()");
            FacesUtil.executaJavaScript("dialogImprimirNota.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    public void indeferirPagamento() {
        try {
            realizarValidacoes();
            validarGuias();
            pagamentoFacade.indeferirPagamento(selecionado);
            FacesUtil.addOperacaoRealizada(" O Pagamento N° <b>" + selecionado.getNumeroPagamento() + "</b> foi indeferido com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    @Override
    public void estornarBaixa() {
        try {
            realizarValidacoes();
            pagamentoFacade.estornarBaixa(selecionado, StatusPagamento.INDEFERIDO);
            FacesUtil.addOperacaoRealizada(" O Pagamento N° <b>" + selecionado.getNumeroPagamento() + "</b> foi estornado a baixa com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    //------------------------------------------------------------------RESTOS A PAGAR--------------------------------------------------------------------------

    public void cacularValorRetencao() {
        retencaoPgto = cacularValorRetencao(getValorBaseCalculo(), retencaoPgto);
    }

    public RetencaoPgto cacularValorRetencao(BigDecimal valorBaseCalculo, RetencaoPgto retencaoPgto) {
        try {
            if (retencaoPgto != null && retencaoPgto.getPorcentagem() != null && retencaoPgto.getPorcentagem().compareTo(BigDecimal.ZERO) != 0 &&
                valorBaseCalculo != null && valorBaseCalculo.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal porcentagem = retencaoPgto.getPorcentagem();
                if (porcentagem.compareTo(new BigDecimal("100")) > 0) {
                    porcentagem = new BigDecimal("100");
                }
                if (porcentagem.compareTo(BigDecimal.ZERO) < 0) {
                    porcentagem = BigDecimal.ZERO;
                }
                retencaoPgto.setValor((valorBaseCalculo.multiply(porcentagem)).divide(new BigDecimal("100"), 2, RoundingMode.DOWN));
            }
        } catch (Exception e) {
            retencaoPgto.setValor(BigDecimal.ZERO);
        }
        return retencaoPgto;
    }

    public void calcularPorcentagemRetencaoPagamento() {

        if (retencaoPgto.getValor() != null && retencaoPgto.getValor().compareTo(BigDecimal.ZERO) != 0)
        retencaoPgto.setPorcentagem(((retencaoPgto.getValor().multiply(BigDecimal.valueOf(100))).divide(getValorBaseCalculo(), 2, RoundingMode.UP)));
    }

    public BigDecimal getValorBaseCalculo() {
        if (retencaoPgto != null && retencaoPgto.getContaExtraorcamentaria() != null && !selecionado.getLiquidacao().getDoctoFiscais().isEmpty()) {
            LiquidacaoDoctoFiscal doctoFiscal = selecionado.getLiquidacao().getDoctoFiscais().get(0);
            return getValorBaseCalculo(doctoFiscal, retencaoPgto.getContaExtraorcamentaria());
        }
        return selecionado.getValor();
    }

    public BigDecimal getValorBaseCalculo(LiquidacaoDoctoFiscal doctoFiscal, Conta contaExtra) {
        ConfiguracaoContabil configuracaoContabil = pagamentoFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
        if (doctoFiscal != null && configuracaoContabil != null && isContaDespesaEmpenhoReinf()) {
            if (configuracaoContabil.getContaExtraInssPadraoDocLiq() != null &&
                configuracaoContabil.getContaExtraInssPadraoDocLiq().equals(contaExtra) &&
                doctoFiscal.getValorBaseCalculo() != null && doctoFiscal.getValorBaseCalculo().compareTo(BigDecimal.ZERO) != 0) {
                return doctoFiscal.getValorBaseCalculo();
            }
            if (configuracaoContabil.getContaExtraIrrfPadraoDocLiq() != null &&
                configuracaoContabil.getContaExtraIrrfPadraoDocLiq().equals(contaExtra) &&
                doctoFiscal.getValorBaseCalculoIrrf() != null && doctoFiscal.getValorBaseCalculoIrrf().compareTo(BigDecimal.ZERO) != 0) {
                return doctoFiscal.getValorBaseCalculoIrrf();
            }
        }
        return selecionado.getValor();
    }

    private void validarRetencaoREINF() {
        ValidacaoException ve = new ValidacaoException();
        if (isContaDespesaEmpenhoReinf()) {
            BigDecimal porcentagemRetencaoMaximaContaExtra = pagamentoFacade.getRetencaoMaximaContaExtra(retencaoPgto.getContaExtraorcamentaria());

            if (Util.isNotNull(porcentagemRetencaoMaximaContaExtra) && porcentagemRetencaoMaximaContaExtra.compareTo(BigDecimal.ZERO) != 0) {
                if (retencaoPgto.getPorcentagem().compareTo(porcentagemRetencaoMaximaContaExtra) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A porcentagem máxima de retenção permitida para essa Conta Extra Orçamentária é de " + porcentagemRetencaoMaximaContaExtra + "%.");
                }
            }
        }
        ve.lancarException();
    }

    public GuiaPagamento getGuiaPagamento() {
        return guiaPagamento;
    }

    public void setGuiaPagamento(GuiaPagamento guiaPagamento) {
        this.guiaPagamento = guiaPagamento;
    }

    public List<Pessoa> completaPessoaGuia(String parte) {
        return pagamentoFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public Converter getConverterPessoaGuia() {
        if (converterPessoaGuia == null) {
            converterPessoaGuia = new ConverterAutoComplete(Pessoa.class, pagamentoFacade.getPessoaFacade());
        }
        return converterPessoaGuia;
    }

    public List<SelectItem> getTiposIdentificacaoGuia() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        if (guiaPagamento != null) {
            IGuiaArquivoOBN600 guia = guiaPagamento;
            if (guia.getPessoa() != null) {
                if (guia.getPessoa() instanceof PessoaFisica) {
                    retorno.add(new SelectItem(TipoIdentificacaoGuia.CPF, TipoIdentificacaoGuia.CPF.getDescricao()));
                    if (!isBancoCaixaEconomica()) {
                        retorno.add(new SelectItem(TipoIdentificacaoGuia.PIS_PASEP, TipoIdentificacaoGuia.PIS_PASEP.getDescricao()));
                        retorno.add(new SelectItem(TipoIdentificacaoGuia.CEI, TipoIdentificacaoGuia.CEI.getDescricao()));
                        retorno.add(new SelectItem(TipoIdentificacaoGuia.IDENTIFICADOR, TipoIdentificacaoGuia.IDENTIFICADOR.getDescricao()));
                    }
                } else {
                    retorno.add(new SelectItem(TipoIdentificacaoGuia.CNPJ, TipoIdentificacaoGuia.CNPJ.getDescricao()));
                    if (!isBancoCaixaEconomica()) {
                        retorno.add(new SelectItem(TipoIdentificacaoGuia.CEI, TipoIdentificacaoGuia.CEI.getDescricao()));
                        retorno.add(new SelectItem(TipoIdentificacaoGuia.IDENTIFICADOR, TipoIdentificacaoGuia.IDENTIFICADOR.getDescricao()));
                    }
                }
            }
        }
        return retorno;
    }

    public void definirNullTipoIdentificacaoGuia() {
        if (guiaPagamento != null) {
            guiaPagamento.setTipoIdentificacaoGuia(null);
            guiaPagamento.setCodigoIdentificacao(null);
        }
    }

    public void atribuirCodigoIdentificacao() {
        if (guiaPagamento != null) {
            IGuiaArquivoOBN600 guia = guiaPagamento;
            if (guia.getPessoa() != null) {
                if (guia.getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.CPF) || guia.getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.CNPJ)) {
                    guiaPagamento.setCodigoIdentificacao(guiaPagamento.getPessoa().getCpf_Cnpj());
                }
                if (guia.getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.PIS_PASEP)) {
                    if (guiaPagamento.getPessoa() instanceof PessoaFisica) {
                        try {
                            guiaPagamento.setPessoa(pagamentoFacade.getPessoaFisicaFacade().recuperar(guia.getPessoa().getId()));
                            guiaPagamento.setCodigoIdentificacao(((PessoaFisica) guia.getPessoa()).getCarteiraDeTrabalho().getPisPasep());
                        } catch (Exception e) {
                            guiaPagamento.setCodigoIdentificacao("Pessoa não possui PIS/PASEP em seu cadastro.");
                        }
                    }
                }
                if (guia.getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.CEI) || guia.getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.IDENTIFICADOR)) {
                    guiaPagamento.setCodigoIdentificacao("");
                }
            }
        }
    }

    public void prepararGuiaParaImprimir(GuiaPagamento guia) {
        guia.getPagamento().setSubConta(pagamentoFacade.getSubContaFacade().recuperar(guia.getPagamento().getSubConta().getId()));
        guiaPagamentoItem = new GuiaPagamentoItem(guia, hierarquiaOrganizacionalFacade);
        imprimirDialog = true;
    }

    public void imprimirGuiaPagamento(GuiaPagamentoItem guiaPagamento) {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            HashMap parameters = new HashMap();
            abstractReport.setGeraNoDialog(imprimirDialog);
            String nomeArquivo = "RelatorioGuiaPagamentoAutenticada.jasper";
            parameters.put("IMAGEM", abstractReport.getCaminhoImagem());
            parameters.put("TIPO_DOCUMENTO", tipoDeDocumentoGuia());
            parameters.put("NOME_RELATORIO", headerDialogGuia());
            parameters.put("MODULO", "Execução Orçamentária");
            parameters.put("TITULO_GUIA", tituloGuiaPagamento());
            parameters.put("PESSOA_CODBARRA", renderizarGuiaComCodigoBarras() ? "Código de Barras " : "Pessoa ");
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            List<GuiaPagamentoItem> guiasPagamento = Lists.newArrayList();
            guiasPagamento.add(guiaPagamento);
            JRBeanCollectionDataSource jrb = new JRBeanCollectionDataSource(guiasPagamento);
            abstractReport.gerarRelatorioComDadosEmCollection(nomeArquivo, parameters, jrb);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String tipoDeDocumentoGuia() {
        return "Tipo de Documento: " + selecionado.getTipoDocPagto().getDescricao();
    }

    private String tituloGuiaPagamento() {
        if (CategoriaOrcamentaria.NORMAL.equals(selecionado.getCategoriaOrcamentaria())) {
            return "Nota de Pagamento de Despesa" + (selecionado.getNumeroPagamento() == null ? "" : " " + selecionado.getNumeroPagamento()) + ", de " + DataUtil.getDataFormatada(selecionado.getDataPagamento());
        }
        return "Nota de Pagamento de Restos a Pagar" + (selecionado.getNumeroPagamento() == null ? "" : " " + selecionado.getNumeroPagamento()) + ", de " + DataUtil.getDataFormatada(selecionado.getDataPagamento());
    }

    public void setarNullGuiaPagamentoItem() {
        guiaPagamentoItem = null;
    }

    public Boolean podeImprimirGuia(GuiaPagamento guiaPag) {
        return guiaPag.getNumeroAutenticacao() != null;
    }

    public String headerDialogGuia() {
        if (TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocPagto())
            || TipoDocPagto.FATURA.equals(selecionado.getTipoDocPagto())) {
            return "Pagamento de guia com código de barras";
        } else {
            return "Pagamento de guia sem código de barras";
        }
    }

    public Boolean renderizarGuiaComCodigoBarras() {
        return TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocPagto())
            || TipoDocPagto.FATURA.equals(selecionado.getTipoDocPagto());
    }

    public boolean renderizarDarfDarfSimples() {
        return renderizarCamposGuiaDarf() || renderizarCamposGuiaDarfSimples();
    }

    public boolean renderizarGpsDarfDarfSimples() {
        return renderizarCamposGuiaGps() || renderizarCamposGuiaDarf() || renderizarCamposGuiaDarfSimples();
    }

    public boolean renderizarCamposGuiaGps() {
        return TipoDocPagto.GPS.equals(selecionado.getTipoDocPagto());
    }

    public boolean renderizarCamposGuiaDarf() {
        return TipoDocPagto.DARF.equals(selecionado.getTipoDocPagto());
    }

    public boolean renderizarCamposGuiaDarfSimples() {
        return TipoDocPagto.DARF_SIMPLES.equals(selecionado.getTipoDocPagto());
    }

    public boolean renderizarGuiaNaoConvenio() {
        return !TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocPagto());
    }

    public GuiaPagamentoItem getGuiaPagamentoItem() {
        return guiaPagamentoItem;
    }

    public void setGuiaPagamentoItem(GuiaPagamentoItem guiaPagamentoItem) {
        this.guiaPagamentoItem = guiaPagamentoItem;
    }

    public BigDecimal getValorRetencoes() {
        return valorRetencoes;
    }

    public void setValorRetencoes(BigDecimal valorRetencoes) {
        this.valorRetencoes = valorRetencoes;
    }


    public ParametroEvento getParametroEvento() {
        return pagamentoFacade.getParametroEvento(selecionado);
    }

    public DesdobramentoPagamento getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(DesdobramentoPagamento desdobramento) {
        this.desdobramento = desdobramento;
    }

    public ConverterAutoComplete getConverterDesdobramentoLiquidacao() {
        if (converterDesdobramentoLiquidacao == null) {
            converterDesdobramentoLiquidacao = new ConverterAutoComplete(Desdobramento.class, pagamentoFacade.getDesdobramentoFacade());
        }
        return converterDesdobramentoLiquidacao;
    }

    public List<Desdobramento> buscarDesdobramento(String parte) {
        try {
            if (selecionado.getLiquidacao() != null) {
                return pagamentoFacade.getDesdobramentoFacade().buscarDesdobramentoPorLiquidacao(parte, selecionado.getLiquidacao());
            }
            return Lists.newArrayList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public void adicionarDetalhamento() {
        try {
            validarAdicionarDetalhamento();
            desdobramento.setPagamento(selecionado);
            desdobramento.setSaldo(desdobramento.getValor());
            Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento);
            verificarAdicionadoAlterandoValor();
            novoDetalhamento();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void verificarAdicionadoAlterandoValor() {
        BigDecimal valor = BigDecimal.ZERO;
        for (DesdobramentoPagamento desdobramentoPagamento : selecionado.getDesdobramentos()) {
            valor = valor.add(desdobramentoPagamento.getValor());
        }
        atribuirValorPagamento(valor);
    }

    private void novoDetalhamento() {
        this.desdobramento = new DesdobramentoPagamento();
    }

    private void validarAdicionarDetalhamento() {
        ValidacaoException ve = new ValidacaoException();
        if (desdobramento.getDesdobramento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Detalhamento deve ser informado");
        }
        if (desdobramento.getValor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo valor deve ser informado");
        } else {
            if (desdobramento.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero (0).");
            }
            BigDecimal saldo = desdobramento.getDesdobramento().getSaldo();
            if (desdobramento.getValor().compareTo(saldo) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor não deve ser maior que o saldo disponível do detalhamento da liquidação. <b> Saldo : " + Util.formataValor(saldo) + " </b>");
            }
        }
        ve.lancarException();
        validarDesdobramentoJaAdicionado(ve);
    }

    private void validarDesdobramentoJaAdicionado(ValidacaoException ve) {
        for (DesdobramentoPagamento desdobramentoPagamento : selecionado.getDesdobramentos()) {
            if (!desdobramentoPagamento.equals(this.desdobramento)
                && desdobramentoPagamento.getDesdobramento().equals(this.desdobramento.getDesdobramento())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Esse detalhamento já foi adicionado na lista.");
                ve.lancarException();
            }
        }
    }

    public void removerDetalhamento(DesdobramentoPagamento desd) {
        selecionado.getDesdobramentos().remove(desd);
        verificarAdicionadoAlterandoValor();
    }

    public void editarDetalhamento(DesdobramentoPagamento desd) {
        this.desdobramento = (DesdobramentoPagamento) Util.clonarObjeto(desd);
    }

    public Boolean isMostrarDesdobramento() {
        if (this.desdobramento == null) {
            return false;
        }
        return !getVerificaEdicao();
    }

    public Date getDataOperacao() {
        return sistemaControlador.getDataOperacao();
    }

    @Override
    public void cleanScoped() {
        super.cleanScoped();
        converterSubConta = null;
        converterHistoricoContabil = null;
        converterLiquidacao = null;
        converterContaExtraorcamentaria = null;
        converterFonteDeRecursos = null;
        converterPessoa = null;
        converterClasseCredor = null;
        converterPessoaGuia = null;
        moneyConverter = null;
        componenteTreeDespesaORC = null;
        retencaoPgto = null;
        viewPanel = null;
        despesaORCFuncional = null;
        usuarioUnidadeOrganizacional = null;
        vwHierarquiaDespesaORCTemp = null;
        lancouRetencao = null;
        pagarPagamento = null;
        valorRetencoes = null;
        indiceAba = null;
        contaBancariaEntidade = null;
        guiaPagamento = null;
        guiaPagamentoItem = null;
        imprimirDialog = null;
        gestorFinanceiro = null;
        desdobramento = null;
        converterDesdobramentoLiquidacao = null;
    }

    private boolean isContaDespesaEmpenhoReinf() {
        if (selecionado.getLiquidacao() != null && selecionado.getLiquidacao().getEmpenho() != null && selecionado.getLiquidacao().getEmpenho().getTipoContaDespesa() != null) {
            ConfiguracaoContabil configuracaoContabil = pagamentoFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
            if (configuracaoContabil != null && configuracaoContabil.getTiposContasDespesasReinf() != null && !configuracaoContabil.getTiposContasDespesasReinf().isEmpty()) {
                for (ConfiguracaoContabilTipoContaDespesaReinf ccTipo : configuracaoContabil.getTiposContasDespesasReinf()) {
                    if (selecionado.getLiquidacao().getEmpenho().getTipoContaDespesa().equals(ccTipo.getTipoContaDespesa())) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    public CodigoCO getCodigoCO() {
        return codigoCO;
    }

    public void setCodigoCO(CodigoCO codigoCO) {
        this.codigoCO = codigoCO;
    }

    public List<CodigoCO> completarCodigosCOs(String parte) {
        return pagamentoFacade.getCodigoCOFacade().buscarCodigosCOsPorFonteDeRecursosFiltrandoPorCodigoEDescricao(parte, selecionado.getLiquidacao().getEmpenho().getFonteDeRecursos());
    }

    private void atualizarCodigoCOEmpenho() {
        if (codigoCO != null) selecionado.getLiquidacao().getEmpenho().setCodigoCO(codigoCO);
    }

    private void validarCodigoBarrasGuiaGRU() {
        ValidacaoException ve = new ValidacaoException();

        String codigoBarras = guiaPagamento.getGuiaGRU().getCodigoBarra();
        if (codigoBarras.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo código de barras deve ser informado.");
        }
        ve.lancarException();
        if (!codigoBarras.startsWith("8")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O primeiro dígito informado(" + codigoBarras.substring(0, 1) + "), não pertence a uma guia do tipo " + selecionado.getTipoDocPagto().getDescricao() + ".");
        }
        if (codigoBarras.length() < 55) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Código de barras não foi preenchido com todos os dígitos.");
        }
        for (GuiaPagamento guia : selecionado.getGuiaPagamento()) {
            if (codigoBarras.equals(guia.getGuiaGRU().getCodigoBarra()) && !guia.equals(guiaPagamento)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Código de barras informado pertence a uma guia adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    public void getValorGuiaGRU() {
        try {
            validarCodigoBarrasGuiaGRU();
            if (TipoDocPagto.GRU.equals(selecionado.getTipoDocPagto())) {
                String valorCodigoBarras = guiaPagamento.getGuiaGRU().getCodigoBarra().substring(4, 11);
                valorCodigoBarras += guiaPagamento.getGuiaGRU().getCodigoBarra().substring(14, 18);
                valorCodigoBarras = valorCodigoBarras.substring(0, valorCodigoBarras.length() - 2) + "." + valorCodigoBarras.substring(valorCodigoBarras.length() - 2, valorCodigoBarras.length());
                guiaPagamento.getGuiaGRU().setValorPrincipal(new BigDecimal(valorCodigoBarras));

            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }
}
