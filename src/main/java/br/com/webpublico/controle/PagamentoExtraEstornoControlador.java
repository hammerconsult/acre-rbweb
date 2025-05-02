/*
 * Codigo gerado automaticamente em Wed Jan 04 09:24:44 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.PagamentoExtra;
import br.com.webpublico.entidades.PagamentoExtraEstorno;
import br.com.webpublico.entidades.ReceitaExtra;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PagamentoExtraEstornoFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
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

@ManagedBean(name = "pagamentoExtraEstornoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-estorno-despesa-extra", pattern = "/estorno-despesa-extra/novo/", viewId = "/faces/financeiro/extraorcamentario/pagamentoextraestorno/edita.xhtml"),
    @URLMapping(id = "editar-estorno-despesa-extra", pattern = "/estorno-despesa-extra/editar/#{pagamentoExtraEstornoControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/pagamentoextraestorno/edita.xhtml"),
    @URLMapping(id = "ver-estorno-despesa-extra", pattern = "/estorno-despesa-extra/ver/#{pagamentoExtraEstornoControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/pagamentoextraestorno/visualizar.xhtml"),
    @URLMapping(id = "listar-estorno-despesa-extra", pattern = "/estorno-despesa-extra/listar/", viewId = "/faces/financeiro/extraorcamentario/pagamentoextraestorno/lista.xhtml")
})
public class PagamentoExtraEstornoControlador extends PrettyControlador<PagamentoExtraEstorno> implements Serializable, CRUD {

    @EJB
    private PagamentoExtraEstornoFacade pagamentoExtraEstornoFacade;
    private List<ReceitaExtra> listaReceitasExtraDoPagamento = new ArrayList<>();
    private List<ReceitaExtra> listaRecExtraSelecionadas = new ArrayList<>();

    public PagamentoExtraEstornoControlador() {
        metadata = new EntidadeMetaData(PagamentoExtraEstorno.class);
    }

    public PagamentoExtraEstornoFacade getFacade() {
        return pagamentoExtraEstornoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return pagamentoExtraEstornoFacade;
    }

    @URLAction(mappingId = "novo-estorno-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        atribuirValoresIniciais();
    }

    @URLAction(mappingId = "ver-estorno-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-estorno-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    private void atribuirValoresIniciais() {
        selecionado = new PagamentoExtraEstorno();
        PagamentoExtraEstorno pee = ((PagamentoExtraEstorno) selecionado);
        pee.setUsuarioSistema(pagamentoExtraEstornoFacade.getSistemaFacade().getUsuarioCorrente());
        pee.setUnidadeOrganizacional(pagamentoExtraEstornoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        pee.setUnidadeOrganizacionalAdm(pagamentoExtraEstornoFacade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        pee.setExercicio(pagamentoExtraEstornoFacade.getSistemaFacade().getExercicioCorrente());
        pee.setDataEstorno(pagamentoExtraEstornoFacade.getSistemaFacade().getDataOperacao());

        if (pagamentoExtraEstornoFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", pagamentoExtraEstornoFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    public void setaEvento() {
        try {
            pagamentoExtraEstornoFacade.setaEvento(selecionado);
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            validarUnidade();
            if (Operacoes.NOVO.equals(operacao)) {
                pagamentoExtraEstornoFacade.salvarNovo(selecionado, listaRecExtraSelecionadas);
                FacesUtil.addOperacaoRealizada(" Registro " + selecionado.toString() + " salvo com sucesso.");
                redirecionarParaVerOrEditar(selecionado.getId(), "editar");
            } else {
                pagamentoExtraEstornoFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada(" Registro " + selecionado.toString() + " alterado com sucesso.");
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            pagamentoExtraEstornoFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPagamentoExtra() != null) {
            pagamentoExtraEstornoFacade.getReceitaExtraFacade().getHierarquiaOrganizacionalFacade()
                .validarUnidadesIguais(selecionado.getPagamentoExtra().getUnidadeOrganizacional(), selecionado.getUnidadeOrganizacional()
                    , ve
                    , "A Unidade Organizacional do estorno de despesa extra deve ser a mesma da despesa extra.");
        }
        ve.lancarException();
    }

    public List<PagamentoExtra> completaPagamentoExtra(String parte) {
        return pagamentoExtraEstornoFacade.getPagamentoExtraFacade().listaFiltrandoDespesaExtraPorUnidadeExercicio(parte.trim(), pagamentoExtraEstornoFacade.getSistemaFacade().getExercicioCorrente(), pagamentoExtraEstornoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public void carregarListaReceitasExtraDoPagamento() {
        instanciarListaRecExtraSelecionadas();
        listaReceitasExtraDoPagamento = new ArrayList<ReceitaExtra>();
        if (((PagamentoExtraEstorno) selecionado).getPagamentoExtra() != null) {
            listaReceitasExtraDoPagamento = pagamentoExtraEstornoFacade.getReceitaExtraFacade().listaPorPagamentoExtra(((PagamentoExtraEstorno) selecionado).getPagamentoExtra());
            setaEvento();
            selecionado.setHistorico(selecionado.getPagamentoExtra().getComplementoHistorico());
            selecionado.zerarValor();
        }
    }

    private void instanciarListaRecExtraSelecionadas() {
        listaRecExtraSelecionadas = new ArrayList<>();
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            NotaExecucaoOrcamentaria notaExecucaoOrcamentaria = pagamentoExtraEstornoFacade.buscarNotaPagamentoExtraEstorno(selecionado);
            if (notaExecucaoOrcamentaria != null) {
                pagamentoExtraEstornoFacade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notaExecucaoOrcamentaria, ModuloTipoDoctoOficial.NOTA_PAGAMENTO_EXTRA_ESTORNO, selecionado, isDownload);
            }
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public BigDecimal getSaldoPagamentoExtra() {
        BigDecimal saldo = new BigDecimal(BigInteger.ZERO);
        PagamentoExtraEstorno pee = ((PagamentoExtraEstorno) selecionado);
        if (pee.getPagamentoExtra() != null) {
            saldo = pee.getPagamentoExtra().getSaldo();
        }
        return saldo;
    }

    public void validaDataPagamentoExtraEstorno(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        PagamentoExtraEstorno pee = ((PagamentoExtraEstorno) selecionado);
        Calendar dataEstorno = Calendar.getInstance();
        dataEstorno.setTime(data);
        Integer ano = pagamentoExtraEstornoFacade.getSistemaFacade().getExercicioCorrente().getAno();
        if (pee.getPagamentoExtra() != null) {
            if (data.before(pee.getPagamentoExtra().getPrevistoPara())) {
                message.setDetail("Data não pode ser menor que do Pagamento Extraorçamentário!");
                message.setSummary("Operação não Permitida!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
            if (dataEstorno.get(Calendar.YEAR) != ano) {
                message.setDetail("Ano diferente do exercício corrente!");
                message.setSummary("Ano diferente do exercício corrente!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public Boolean getVerificaEdicao() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        }
        return false;
    }

    public void selecionarDespesa() {
        carregarListaReceitasExtraDoPagamento();
        setaEvento();
    }

    public void selecionarDespesaExtra(ActionEvent evento) {
        PagamentoExtra pagamentoExtra = (PagamentoExtra) evento.getComponent().getAttributes().get("objeto");
        pagamentoExtra = pagamentoExtraEstornoFacade.getPagamentoExtraFacade().recuperar(pagamentoExtra.getId());
        ((PagamentoExtraEstorno) selecionado).setPagamentoExtra(pagamentoExtra);
        selecionarDespesa();
    }

    public String actionSelecionar() {
        return "edita";
    }

    public String iconeTableReceitas(ReceitaExtra re) {
        if (listaRecExtraSelecionadas.contains(re)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleTableReceitas(ReceitaExtra re) {
        if (listaRecExtraSelecionadas.contains(re)) {
            return "Clique para deselecionar esta receita extraorçamentária.";
        }
        return "Clique para selecionar esta receita extraorçamentária";
    }

    public String iconeCorTableReceitas(ReceitaExtra re) {
        if (listaRecExtraSelecionadas.contains(re)) {
            return "botao_azul icone_branco";
        }
        return "";
    }

    public void atualizaListaReceitasSelecionadas(ReceitaExtra re) {
        if (listaRecExtraSelecionadas.contains(re)) {
            listaRecExtraSelecionadas.remove(re);
        } else {
            listaRecExtraSelecionadas.add(re);
        }
        somaValoresReceita(re);
    }


    public void somaValoresReceita(ReceitaExtra receitaExtra) {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (ReceitaExtra re : listaRecExtraSelecionadas) {
            soma = soma.add(re.getValor().subtract(re.getValorEstornado()));
        }
        if (receitaExtra.getSaldo().compareTo(receitaExtra.getValorEstornado()) != 0 && listaRecExtraSelecionadas.contains(receitaExtra)) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " A Receita Extra: " + receitaExtra + " possui estorno(s) no valor de <b>" + Util.formataValor(receitaExtra.getValorEstornado()) + "</b>. Dessa forma, o valor adicionado será de <b>" + Util.formataValor(valorFinalReceitaExtra(receitaExtra)) + "</b>.");
        }
        ((PagamentoExtraEstorno) selecionado).setValor(soma);
    }

    public BigDecimal valorFinalReceitaExtra(ReceitaExtra receitaExtra) {
        BigDecimal valorFinal = new BigDecimal(BigInteger.ZERO);
        valorFinal = receitaExtra.getValor().subtract(receitaExtra.getValorEstornado());
        return valorFinal;
    }

    public List<ReceitaExtra> getListaRecExtraSelecionadas() {
        return listaRecExtraSelecionadas;
    }

    public void setListaRecExtraSelecionadas(List<ReceitaExtra> listaRecExtraSelecionadas) {
        this.listaRecExtraSelecionadas = listaRecExtraSelecionadas;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estorno-despesa-extra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String getContaBancaria() {
        try {
            return selecionado.getPagamentoExtra().getSubConta().getContaBancariaEntidade().toStringAutoCompleteContaBancaria();
        } catch (Exception ex) {
            return "";
        }
    }

    public PagamentoExtraEstornoFacade getPagamentoExtraEstornoFacade() {
        return pagamentoExtraEstornoFacade;
    }

    public void setPagamentoExtraEstornoFacade(PagamentoExtraEstornoFacade pagamentoExtraEstornoFacade) {
        this.pagamentoExtraEstornoFacade = pagamentoExtraEstornoFacade;
    }

    public List<ReceitaExtra> getListaReceitasExtraDoPagamento() {
        return listaReceitasExtraDoPagamento;
    }

    public void setListaReceitasExtraDoPagamento(List<ReceitaExtra> listaReceitasExtraDoPagamento) {
        this.listaReceitasExtraDoPagamento = listaReceitasExtraDoPagamento;
    }
}
