/*
 * Codigo gerado automaticamente em Wed Dec 14 09:52:41 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamento;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamentoEstorno;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.VwHierarquiaDespesaORC;
import br.com.webpublico.entidadesview.PagamentoEstornoView;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PagamentoEstornoFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "pagamentoEstornoControlador")
@ViewScoped
@URLMappings(mappings = {
//        Mapeamento de Estorno de Pagamento Normal
    @URLMapping(id = "novo-pagamento-estorno", pattern = "/pagamento-estorno/novo/", viewId = "/faces/financeiro/orcamentario/pagamentoestorno/edita.xhtml"),
    @URLMapping(id = "editar-pagamento-estorno", pattern = "/pagamento-estorno/editar/#{pagamentoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/pagamentoestorno/edita.xhtml"),
    @URLMapping(id = "ver-pagamento-estorno", pattern = "/pagamento-estorno/ver/#{pagamentoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/pagamentoestorno/visualizar.xhtml"),
    @URLMapping(id = "listar-pagamento-estorno", pattern = "/pagamento-estorno/listar/", viewId = "/faces/financeiro/orcamentario/pagamentoestorno/lista.xhtml"),

//        Mapeamento de Estorno de Pagamento de Resto a Pagar
    @URLMapping(id = "novo-pagamento-estorno-resto", pattern = "/pagamento-estorno/resto-a-pagar/novo/", viewId = "/faces/financeiro/orcamentario/pagamentoestorno/edita.xhtml"),
    @URLMapping(id = "editar-pagamento-estorno-resto", pattern = "/pagamento-estorno/resto-a-pagar/editar/#{pagamentoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/pagamentoestorno/edita.xhtml"),
    @URLMapping(id = "ver-pagamento-estorno-resto", pattern = "/pagamento-estorno/resto-a-pagar/ver/#{pagamentoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/pagamentoestorno/visualizar.xhtml"),
    @URLMapping(id = "listar-pagamento-estorno-resto", pattern = "/pagamento-estorno/resto-a-pagar/listar/", viewId = "/faces/financeiro/orcamentario/pagamentoestorno/listarestopagar.xhtml")
})
public class PagamentoEstornoControlador extends PrettyControlador<PagamentoEstorno> implements Serializable, CRUD {

    @EJB
    protected PagamentoEstornoFacade pagamentoEstornoFacade;
    private ConverterAutoComplete converterPagamento;
    private ConverterAutoComplete converterHistoricoContabil;
    private MoneyConverter moneyConverter;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<ReceitaExtra> receitaExtras;
    private List<ReceitaExtra> receitaExtrasSelecionadas;
    private List<ReceitaExtraEstorno> receitaExtraEstornos;
    private DespesaORC despesaORCFuncional;
    private VwHierarquiaDespesaORC vwHierarquiaDespesaORCTemp;
    private Integer indiceAba;
    private Boolean lancouRetencao;
    private BigDecimal valorRetencoes;
    //Desdobramento
    private DesdobramentoPagamentoEstorno desdobramento;
    private ConverterAutoComplete converterDesdobramentoPagamento;

    public PagamentoEstornoControlador() {
        super(PagamentoEstorno.class);
    }

    public PagamentoEstornoFacade getFacade() {
        return pagamentoEstornoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return pagamentoEstornoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        if (getCategoriaOrcamentariaPagamento()) {
            return "/pagamento-estorno/";
        } else {
            return "/pagamento-estorno/resto-a-pagar/";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-pagamento-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
    }

    @URLAction(mappingId = "ver-pagamento-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-pagamento-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "novo-pagamento-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRestoPagar() {
        super.novo();
        parametrosIniciais();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
    }

    @URLAction(mappingId = "ver-pagamento-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRestoPagar() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-pagamento-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarRestoPagar() {
        super.editar();
        recuperarEditarVer();
    }

    public void redirecionaParaLista() {
        if (getCategoriaOrcamentariaPagamento()) {
            FacesUtil.redirecionamentoInterno("/pagamento-estorno/listar/");
        } else {
            FacesUtil.redirecionamentoInterno("/pagamento-estorno/resto-a-pagar/listar/");
        }
    }

    public String getTituloEstornoPagamento() {
        if (getCategoriaOrcamentariaPagamento()) {
            return "Estorno de Pagamento";
        } else {
            return "Estorno de Pagamento de Resto a Pagar";
        }
    }

    public String getTituloPagamento() {
        if (getCategoriaOrcamentariaPagamento()) {
            return "Pagamento";
        } else {
            return "Pagamento de Resto a Pagar";
        }
    }

    public String getAlinhaCampos() {
        if (getCategoriaOrcamentariaPagamento()) {
            return "margin-right: 27px";
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

    private void parametrosIniciais() {
        selecionado = new PagamentoEstorno();
        despesaORCFuncional = new DespesaORC();
        receitaExtraEstornos = new ArrayList<>();
        receitaExtras = new ArrayList<>();
        receitaExtrasSelecionadas = new ArrayList<>();
        valorRetencoes = BigDecimal.ZERO;
        selecionado.setDataEstorno(sistemaControlador.getDataOperacao());
        selecionado.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        indiceAba = 0;
        lancouRetencao = Boolean.FALSE;
        novoDetalhamento();
        atribuirValor(BigDecimal.ZERO);
        if (pagamentoEstornoFacade.getPagamentoFacade().getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", pagamentoEstornoFacade.getPagamentoFacade().getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    public void buscaDados(SelectEvent evt) {
        Pagamento pag = pagamentoEstornoFacade.getPagamentoFacade().recuperar(selecionado.getPagamento().getId());
        selecionado.setPagamento(pag);
        recuperarReceitaExtra(pag);
        recuperarValores();
        recuperaEventoContabil();
        atribuirValor(BigDecimal.ZERO);
        atribuirHistorico();
        buscarAdicionarDetalhamento();
    }

    private void atribuirValor(BigDecimal valor) {
        selecionado.setValorFinal(valor);
        selecionado.setValor(valor);
    }

    private void recuperarReceitaExtra(Pagamento pag) {
        receitaExtras = pagamentoEstornoFacade.listaReceitaExtraPgtoNaoEstornada(pag);
    }

    public void selecionarPagamento(ActionEvent evento) {
        selecionado.setPagamento((Pagamento) evento.getComponent().getAttributes().get("objeto"));
        selecionado.getPagamento().getLiquidacao().setEmpenho(pagamentoEstornoFacade.getLiquidacaoFacade().getEmpenhoFacade().recuperar(selecionado.getPagamento().getLiquidacao().getEmpenho().getId()));
        atribuirValor(BigDecimal.ZERO);
        atribuirHistorico();
        recuperarReceitaExtra(selecionado.getPagamento());
        recuperarValores();
        recuperaEventoContabil();
        buscarAdicionarDetalhamento();
    }

    private void atribuirHistorico() {
        selecionado.setComplementoHistorico(selecionado.getPagamento().getComplementoHistorico());
    }

    public void recuperarValores() {
        try {
            if (selecionado.getPagamento() != null) {
                vwHierarquiaDespesaORCTemp = new VwHierarquiaDespesaORC();
                if (selecionado.getPagamento().getLiquidacao().getEmpenho().getDespesaORC() != null) {
                    vwHierarquiaDespesaORCTemp = pagamentoEstornoFacade.getPagamentoFacade().getLiquidacaoFacade().getDespesaORCFacade().recuperaVwDespesaOrc(selecionado.getPagamento().getLiquidacao().getEmpenho().getDespesaORC(), sistemaControlador.getDataOperacao());
                }
            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " A Despesa Orçamentária do Empenho referenciado no Pagamento está vazia. ");
        }
    }

    private boolean validavwHierarquiaDespesaORCTemp() {
        return ((vwHierarquiaDespesaORCTemp != null) && (selecionado.getPagamento().getLiquidacao().getEmpenho() != null));
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

    public String getDespesa() {
        String toReturn = "";
        if (validavwHierarquiaDespesaORCTemp()) {
            toReturn = vwHierarquiaDespesaORCTemp.getConta();
        }
        return toReturn;
    }

    public void cancelaImprimirNota() {
        redirecionaParaLista();
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getTituloEstornoPagamento() + " " + selecionado.getPagamento() + " foi salvo com sucesso.");
    }

    public void recuperarEditarVer() {
        selecionado = pagamentoEstornoFacade.recuperar(selecionado.getId());
        despesaORCFuncional = selecionado.getPagamento().getLiquidacao().getEmpenho().getDespesaORC();
        recuperarValores();
        recuperaEventoContabil();
        recuperarValorRetencoes();
        novoDetalhamento();
    }

    private void recuperarValorRetencoes() {
        try {
            valorRetencoes = getSomaReceitasExtraEstornadas();
        } catch (Exception e) {
            valorRetencoes = BigDecimal.ZERO;
        }
    }

    public boolean renderizaSelecionarReceita() {
        if (operacao.equals(Operacoes.VER)) {
            return false;
        }
        if (operacao.equals(Operacoes.EDITAR) && !operacao.equals(Operacoes.NOVO)) {
            return false;
        }
        return true;
    }

    public List<ReceitaExtraEstorno> listaReceitaExtraEstornoNoPagamentoEstorno() {
        if (selecionado.getId() != null) {
            try {
                return pagamentoEstornoFacade.listaReceitaExtraEstornadaPorEstornoPagamento(selecionado);
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    public List<ReceitaExtra> listaReceitaExtraNoPagamentoEstorno() {
        try {
            return pagamentoEstornoFacade.listaReceitaExtraPorEstornoPagamento(selecionado);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<ReceitaExtra> getReceitaExtraEstornadaNoPagamentoEstorno() {
        try {
            return pagamentoEstornoFacade.listaReceitaExtraPgtoEstornadas(selecionado);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<ReceitaExtra> listaReceitas() {
        if (operacao.equals(Operacoes.NOVO) && !receitaExtras.isEmpty()) {
            return receitaExtras;
        }
        if (operacao.equals(Operacoes.EDITAR) && !operacao.equals(Operacoes.NOVO)) {
            return getReceitaExtraEstornadaNoPagamentoEstorno();
        }
        return new ArrayList<>();
    }

    public List<ReceitaExtraEstorno> estornoRet() {
        PagamentoEstorno p = ((PagamentoEstorno) selecionado);
        ReceitaExtraEstorno receitaestorno;
        receitaExtraEstornos = new ArrayList<ReceitaExtraEstorno>();
        for (ReceitaExtra re : receitaExtrasSelecionadas) {
            receitaestorno = new ReceitaExtraEstorno(re.getNumero(), re.getValor(), p.getDataEstorno(), re.getComplementoHistorico(), re, sistemaControlador.getUsuarioCorrente(), re.getUnidadeOrganizacional());
            receitaExtraEstornos.add(receitaestorno);
        }
        lancouRetencao = Boolean.TRUE;
        return receitaExtraEstornos;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            salvarDataConciliacao();
            if (isOperacaoNovo()) {
                pagamentoEstornoFacade.getPagamentoFacade().getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(selecionado.getUnidadeOrganizacionalAdm(), selecionado.getUnidadeOrganizacional(), selecionado.getDataEstorno());
                salvarNovoEstornoPagamento();
            } else {
                pagamentoEstornoFacade.salvar(selecionado);
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getTituloEstornoPagamento() + " " + selecionado.getPagamento() + " foi alterado com sucesso.");
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            pagamentoEstornoFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
            pagamentoEstornoFacade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getPagamento());
        } catch (Exception e) {
            pagamentoEstornoFacade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getPagamento());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void salvarNovoEstornoPagamento() {
        if (lancouRetencao) {
            selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
            pagamentoEstornoFacade.estornoRetencoes(selecionado, estornoRet(), receitaExtrasSelecionadas);
            FacesUtil.executaJavaScript("dialogImprimirNota.show()");
        } else {
            if (receitaExtras.size() > 0) {
                FacesUtil.executaJavaScript("dialogRetencao.show()");
            } else {
                pagamentoEstornoFacade.salvarNovoEstornoPagamento(selecionado, receitaExtrasSelecionadas);
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getTituloEstornoPagamento() + " " + selecionado.getPagamento() + " foi salvo com sucesso.");
                FacesUtil.executaJavaScript("dialogImprimirNota.show()");
            }
        }
    }

    public BigDecimal getSomaReceitasExtras() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        if (operacao.equals(Operacoes.NOVO) && !receitaExtras.isEmpty()) {
            for (ReceitaExtra receitaExtra : receitaExtras) {
                valor = valor.add(receitaExtra.getValor());
            }
        }
        if (operacao.equals(Operacoes.EDITAR)) {
            for (ReceitaExtra receitaExtra : getReceitaExtraEstornadaNoPagamentoEstorno()) {
                valor = valor.add(receitaExtra.getValor());
            }
        }
        return valor;
    }

    public BigDecimal getSomaReceitasExtraEstornadas() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (ReceitaExtraEstorno receitaExtraEstorno : listaReceitaExtraEstornoNoPagamentoEstorno()) {
            valor = valor.add(receitaExtraEstorno.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaReceitasExtrasSelecionadas() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        if (receitaExtrasSelecionadas != null) {
            for (ReceitaExtra receitaExtra : receitaExtrasSelecionadas) {
                valor = valor.add(receitaExtra.getValor());
            }
        }
        return valor;
    }

    private void validarCampos() {
        selecionado.realizarValidacoes();
        validarCampoPagamento();
    }

    public void copiarHistoricoUsuario() {
        if (selecionado.getHistoricoContabil() != null) {
            selecionado.setComplementoHistorico(selecionado.getHistoricoContabil().toStringAutoComplete());
            selecionado.setHistoricoContabil(null);
        }
    }

    public List<Pagamento> completaPagamento(String parte) {
        if (getCategoriaOrcamentariaPagamento()) {
            return pagamentoEstornoFacade.getPagamentoFacade().listaPorPessoaUnidadeStatusDeferidoIndeferidoPagoCategoriaNormal(parte.trim(), sistemaControlador.getExercicioCorrente().getAno(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        } else {
            return pagamentoEstornoFacade.getPagamentoFacade().listaPorPessoaUnidadeStatusDeferidoIndeferidoPagoCategoriaResto(parte.trim(), sistemaControlador.getExercicioCorrente().getAno(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        }
    }

    public ConverterAutoComplete getConverterPagamento() {
        if (converterPagamento == null) {
            converterPagamento = new ConverterAutoComplete(Pagamento.class, pagamentoEstornoFacade.getPagamentoFacade());
        }
        return converterPagamento;
    }

    public List<HistoricoContabil> completaHistoricoContabil(String parte) {
        return pagamentoEstornoFacade.getHistoricoContabilFacade().listaFiltrando(parte, "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterHistoricoContabil() {
        if (converterHistoricoContabil == null) {
            converterHistoricoContabil = new ConverterAutoComplete(HistoricoContabil.class, pagamentoEstornoFacade.getHistoricoContabilFacade());
        }
        return converterHistoricoContabil;
    }

    public void recuperaEventoContabil() {
        try {
            selecionado.setEventoContabil(null);
            Empenho empenho = selecionado.getPagamento().getLiquidacao().getEmpenho();
            Conta conta = empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa();

            if (getCategoriaOrcamentariaPagamento()) {
                ConfigPagamento configuracao = pagamentoEstornoFacade.getConfiguracaoFacade().recuperaEventoPorContaDespesa(conta, TipoLancamento.ESTORNO, empenho.getTipoContaDespesa(), selecionado.getDataEstorno());
                Preconditions.checkNotNull(configuracao, "Não foi encontrada uma Configuração para os Parametros informados ");
                selecionado.setEventoContabil(configuracao.getEventoContabil());
            } else {
                ConfigPagamentoRestoPagar configRestoPagar = pagamentoEstornoFacade.getConfigPagamentoRestoPagarFacade().recuperaEventoRestoPorContaDespesa(conta, TipoLancamento.ESTORNO, selecionado.getDataEstorno(), empenho.getTipoContaDespesa(), empenho.getTipoRestosProcessados());
                Preconditions.checkNotNull(configRestoPagar, "Não foi encontrada uma Configuração para os Parametros informados ");
                selecionado.setEventoContabil(configRestoPagar.getEventoContabil());
            }
        } catch (Exception e) {
            FacesUtil.addError("Evento não Encontrado! ", e.getMessage());
        }
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public BigDecimal getSaldoPagamento() {
        BigDecimal saldo = new BigDecimal(BigInteger.ZERO);
        PagamentoEstorno pe = ((PagamentoEstorno) selecionado);
        if (pe.getPagamento() != null) {
            saldo = pe.getPagamento().getSaldo();
        }
        return saldo;
    }


    public void validaDataPagamentoEstorno(FacesContext context, UIComponent component, Object value) {
        PagamentoEstorno pe = ((PagamentoEstorno) selecionado);
        if (selecionado.getDataEstorno() != null) {
            FacesMessage message = new FacesMessage();
            Date data = (Date) value;
            Calendar dtPgEst = Calendar.getInstance();
            dtPgEst.setTime(data);
            Integer ano = sistemaControlador.getExercicioCorrente().getAno();
            if (pe.getPagamento() != null) {
                if (data.before(pe.getPagamento().getPrevistoPara())) {
                    message.setDetail("Data do estorno não pode ser menor que do Pagamento!");
                    message.setSummary(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao());
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(message);
                }
                if (dtPgEst.get(Calendar.YEAR) != ano) {
                    message.setDetail("Ano é diferente do exercício corrente!");
                    message.setSummary(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao());
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);
                    throw new ValidatorException(message);
                }
            }
        }
    }

    public Boolean getVerificaEdicao() {
        try {
            if (operacao.equals(Operacoes.EDITAR)) {
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }

    public List getLista() {
        List<PagamentoEstornoView> listaView = new ArrayList<PagamentoEstornoView>();
        for (Object[] o : (List<Object[]>) pagamentoEstornoFacade.listaNoExercicio(sistemaControlador.getExercicioCorrente())) {
            listaView.add(new PagamentoEstornoView(Long.parseLong(o[0].toString()), (Date) o[1], o[2].toString(), o[3].toString(), o[4].toString() + " - " + o[5].toString()));
        }
        return listaView;
    }

    public DespesaORC getDespesaORCFuncional() {
        return despesaORCFuncional;
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            List<NotaExecucaoOrcamentaria> notas = pagamentoEstornoFacade.buscarNotaDeEstornoPagamento(" and nota.id = " + selecionado.getId(), selecionado.getCategoriaOrcamentaria());
            if (notas != null && !notas.isEmpty()) {
                pagamentoEstornoFacade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notas.get(0), CategoriaOrcamentaria.RESTO.equals(selecionado.getCategoriaOrcamentaria()) ? ModuloTipoDoctoOficial.NOTA_RESTO_ESTORNO_PAGAMENTO : ModuloTipoDoctoOficial.NOTA_ESTORNO_PAGAMENTO, selecionado, isDownload);
            }
        } catch (Exception ex) {
            logger.error("Erro ao gerar nota de estorno de pagamento: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void setarFocoAbaRetencao() {
        indiceAba = 1;
        FacesUtil.executaJavaScript("dialogRetencao.hide()");
    }

    public void setarTrueLancouRentecaoESalvar() {
        FacesUtil.executaJavaScript("dialogRetencao.hide()");
        pagamentoEstornoFacade.salvarNovoEstornoPagamento(selecionado, receitaExtrasSelecionadas);
        FacesUtil.executaJavaScript("dialogImprimirNota.show()");
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getTituloEstornoPagamento() + " " + selecionado.getPagamento() + " foi salvo com sucesso.");
    }

    public void setaTrueLancouRetencao() {
        this.lancouRetencao = Boolean.TRUE;
        if (receitaExtrasSelecionadas.size() > 0) {
            valorRetencoes = getSomaReceitasExtrasSelecionadas();
        }
        indiceAba = 1;
    }

    public void setaFalseLancouRetencao() {
        if (receitaExtrasSelecionadas.size() == 0) {
            this.lancouRetencao = Boolean.FALSE;
            indiceAba = 1;
        }
        valorRetencoes = getSomaReceitasExtrasSelecionadas();
    }

    private void validarCampoPagamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getId() == null) {
            Pagamento pagamento = pagamentoEstornoFacade.getPagamentoFacade().recuperar(selecionado.getPagamento().getId());
            if (selecionado.getValorFinal().compareTo(pagamento.getSaldoFinal()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor final a ser estornado de <b>" + Util.formataValor(selecionado.getValorFinal()) + "</b> deve ser menor ou igual ao saldo final de <b>" + Util.formataValor(pagamento.getSaldoFinal()) + "</b> para este Pagamento.");
            }
            if (selecionado.getValor().compareTo(pagamento.getSaldo()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor a ser estornado de <b>" + Util.formataValor(selecionado.getValor()) + "</b> deve ser menor ou igual ao saldo de <b>" + Util.formataValor(pagamento.getSaldoFinal()) + "</b> para este Pagamento.");
            }
            if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0
                && selecionado.getValorFinal().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor ou Valor Final deve ser maior que zero(0).");
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para estornar o valor integral do pagamento é necessário selecionar suas retenções na aba <b>'Retenções'</b>.");
            }
            if (DataUtil.dataSemHorario(selecionado.getDataEstorno()).compareTo(DataUtil.dataSemHorario(selecionado.getPagamento().getDataPagamento())) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A data do Estorno de Pagamento (" + DataUtil.getDataFormatada(selecionado.getDataEstorno()) + ") deve ser maior ou igual a data do pagamento. Data do Pagamento: <b>" + DataUtil.getDataFormatada(selecionado.getPagamento().getDataPagamento()) + "</b>.");
            }
            if (selecionado.getPagamento() != null) {
                pagamentoEstornoFacade.getPagamentoFacade().getLiquidacaoFacade().getHierarquiaOrganizacionalFacade()
                    .validarUnidadesIguais(selecionado.getPagamento().getUnidadeOrganizacional(), selecionado.getUnidadeOrganizacional()
                        , ve
                        , "A Unidade Organizacional do estorno de pagamento deve ser a mesma do pagamento.");
            }
        }
        ve.lancarException();
    }

    public BigDecimal getValorRetencoes() {
        return valorRetencoes;
    }

    public void setValorRetencoes(BigDecimal valorRetencoes) {
        this.valorRetencoes = valorRetencoes;
    }

    public String icone(ReceitaExtra receitaExtra) {
        if (receitaExtrasSelecionadas.contains(receitaExtra)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String title(ReceitaExtra receitaExtra) {
        if (receitaExtrasSelecionadas.contains(receitaExtra)) {
            return "Clique para deselecionar esta receita extra.";
        }
        return "Clique para selecionar esta receita extra.";
    }

    public String iconeTodos() {
        if (receitaExtrasSelecionadas.size() == receitaExtras.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleTodos() {
        if (receitaExtrasSelecionadas.size() == receitaExtras.size()) {
            return "Clique para deselecionar todas receitas extra.";
        }
        return "Clique para selecionar todas receitas extra.";
    }

    public void selecionarReceita(ReceitaExtra receitaExtra) {
        if (receitaExtrasSelecionadas.contains(receitaExtra)) {
            receitaExtrasSelecionadas.remove(receitaExtra);
            setaFalseLancouRetencao();
            verificarAdicionadoAlterandoValor();
        } else {
            receitaExtra.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
            receitaExtrasSelecionadas.add(receitaExtra);
            setaTrueLancouRetencao();
            verificarAdicionadoAlterandoValor();
        }
    }

    public void selecionarTodasReceitas() {
        if (receitaExtrasSelecionadas.size() == receitaExtras.size()) {
            receitaExtrasSelecionadas.removeAll(receitaExtras);
            setaFalseLancouRetencao();
            verificarAdicionadoAlterandoValor();
        } else {
            for (ReceitaExtra receitaExtra : receitaExtras) {
                selecionarReceita(receitaExtra);
            }
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<ReceitaExtra> getReceitaExtras() {
        return receitaExtras;
    }

    public void setReceitaExtras(List<ReceitaExtra> receitaExtras) {
        this.receitaExtras = receitaExtras;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public Boolean getLancouRetencao() {
        return lancouRetencao;
    }

    public void setLancouRetencao(Boolean lancouRetencao) {
        this.lancouRetencao = lancouRetencao;
    }

    public DesdobramentoPagamentoEstorno getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(DesdobramentoPagamentoEstorno desdobramento) {
        this.desdobramento = desdobramento;
    }

    public ConverterAutoComplete getConverterDesdobramentoPagamento() {
        if (converterDesdobramentoPagamento == null) {
            converterDesdobramentoPagamento = new ConverterAutoComplete(DesdobramentoPagamento.class, pagamentoEstornoFacade.getDesdobramentoPagamentoFacade());
        }
        return converterDesdobramentoPagamento;
    }

    public List<DesdobramentoPagamento> buscarDesdobramento(String parte) {
        try {
            if (selecionado.getPagamento() != null) {
                return pagamentoEstornoFacade.getDesdobramentoPagamentoFacade().buscarDesdobramentoPorPagamento(parte, selecionado.getPagamento());
            }
            return Lists.newArrayList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public void adicionarDetalhamento() {
        try {
            validarAdicionarDetalhamento();
            desdobramento.setPagamentoEstorno(selecionado);
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
        for (DesdobramentoPagamentoEstorno desdobramentoPagamento : selecionado.getDesdobramentos()) {
            valor = valor.add(desdobramentoPagamento.getValor());
        }
        selecionado.setValorFinal(valor.subtract(valorRetencoes));
        selecionado.setValor(valor);
    }

    private void novoDetalhamento() {
        this.desdobramento = new DesdobramentoPagamentoEstorno();
    }

    private void validarAdicionarDetalhamento() {
        ValidacaoException ve = new ValidacaoException();
        if (desdobramento.getDesdobramentoPagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Detalhamento deve ser informado");
        }
        if (desdobramento.getValor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo valor deve ser informado");
        } else {
            if (desdobramento.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero (0).");
            }
            BigDecimal saldo = desdobramento.getDesdobramentoPagamento().getSaldo();
            if (desdobramento.getValor().compareTo(saldo) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor não deve ser maior que o saldo disponível do detalhamento do Pagamento. <b> Saldo : " + Util.formataValor(saldo) + " </b>");
            }
        }
        ve.lancarException();
        for (DesdobramentoPagamentoEstorno desdobramentoPagamento : selecionado.getDesdobramentos()) {
            if (!desdobramentoPagamento.equals(this.desdobramento)
                && desdobramentoPagamento.getDesdobramentoPagamento().equals(this.desdobramento.getDesdobramentoPagamento())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Esse detalhamento já foi adicionado na lista.");
            }
        }
        ve.lancarException();

    }

    private void salvarDataConciliacao() {
        if (selecionado.getPagamento().getDataConciliacao() == null && selecionado.getDataConciliacao() == null && selecionado.getPagamento().getValorFinal().compareTo(selecionado.getValorFinal()) == 0) {
            Exercicio exercicio = pagamentoEstornoFacade.getConciliacaoBancariaFacade().getExercicioFacade().getExercicioPorAno(DataUtil.getAno(selecionado.getDataEstorno()));
            if (pagamentoEstornoFacade.getConciliacaoBancariaFacade().getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/orcamentario/pagamentoestorno/edita.xhtml", selecionado.getDataConciliacao(), selecionado.getUnidadeOrganizacional(), exercicio)) {
                selecionado.setDataConciliacao(selecionado.getDataEstorno());
                selecionado.getPagamento().setDataConciliacao(selecionado.getDataEstorno());
            }
        }
    }

    public void removerDetalhamento(DesdobramentoPagamentoEstorno desd) {
        selecionado.getDesdobramentos().remove(desd);
        verificarAdicionadoAlterandoValor();
    }

    public void editarDetalhamento(DesdobramentoPagamentoEstorno desd) {
        this.desdobramento = (DesdobramentoPagamentoEstorno) Util.clonarObjeto(desd);
    }

    public Boolean isMostrarDesdobramento() {
        if (this.desdobramento == null) {
            return false;
        }
        return !getVerificaEdicao();
    }

    public void atribuirValorSaldoDetalhamento() {
        this.desdobramento.setValor(this.desdobramento.getDesdobramentoPagamento().getSaldo());
    }

    private void buscarAdicionarDetalhamento() {
        List<DesdobramentoPagamento> desdobramentos = buscarDesdobramento("");
        if (desdobramentos != null && desdobramentos.size() == 1) {
            this.desdobramento.setDesdobramentoPagamento(desdobramentos.get(0));
            atribuirValorSaldoDetalhamento();
        }
    }
}
