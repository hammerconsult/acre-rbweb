//package br.com.webpublico.controle;
//
//import br.com.webpublico.controlerelatorio.AbstractReport;
//import br.com.webpublico.entidades.*;
//import br.com.webpublico.enums.TipoLancamento;
//import br.com.webpublico.util.FacesUtil;
//import com.ocpsoft.pretty.faces.annotation.URLAction;
//import com.ocpsoft.pretty.faces.annotation.URLMapping;
//import com.ocpsoft.pretty.faces.annotation.URLMappings;
//
//import java.io.IOException;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ViewScoped;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.HashMap;
//import javax.faces.event.ActionEvent;
//
//import net.sf.jasperreports.engine.JRException;
//
///**
// * Created with IntelliJ IDEA.
// * User: Buzatto
// * Date: 10/10/13
// * Time: 15:19
// * To change this template use File | Settings | File Templates.
// */
//@ManagedBean
//@ViewScoped
//@URLMappings(mappings = {
//        @URLMapping(id = "novoLiquidacaoRestoPagar", pattern = "/liquidacao-resto-pagar/novo/", viewId = "/faces/financeiro/orcamentario/restosapagar/liquidacao/edita.xhtml"),
//        @URLMapping(id = "editarLiquidacaoRestoPagar", pattern = "/liquidacao-resto-pagar/editar/#{liquidacaoRestoPagarControlador.id}/", viewId = "/faces/financeiro/orcamentario/restosapagar/liquidacao/edita.xhtml"),
//        @URLMapping(id = "verLiquidacaoRestoPagar", pattern = "/liquidacao-resto-pagar/ver/#{liquidacaoRestoPagarControlador.id}/", viewId = "/faces/financeiro/orcamentario/restosapagar/liquidacao/visualizar.xhtml"),
//        @URLMapping(id = "listarLiquidacaoRestoPagar", pattern = "/liquidacao-resto-pagar/listar/", viewId = "/faces/financeiro/orcamentario/restosapagar/liquidacao/lista.xhtml")
//})
//public class LiquidacaoRestoPagarControlador extends LiquidacaoControlador {
//
//    public LiquidacaoRestoPagarControlador() {
//        super();
//    }
//
//    @URLAction(mappingId = "novoLiquidacaoRestoPagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void novo() {
//        super.novo();
//        selecionado.setTransportado(Boolean.FALSE);
//        if (getLiquidacaoFacade().getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
//            FacesUtil.addWarn("Aviso! ", getLiquidacaoFacade().getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
//        }
//    }
//
//    @URLAction(mappingId = "verLiquidacaoRestoPagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void ver() {
//        super.ver();
//    }
//
//    @URLAction(mappingId = "editarLiquidacaoRestoPagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void editar() {
//        super.editar();
//    }
//
//    @Override
//    public String getCaminhoPadrao() {
//        return "/liquidacao-resto-pagar/";
//    }
//
//    public BigDecimal getSaldoLiquidacao() {
//        BigDecimal saldo = new BigDecimal(BigInteger.ZERO);
//        if (selecionado.getLiquidacao() != null) {
//            saldo = selecionado.getLiquidacao().getSaldo();
//            return saldo;
//        }
//        return saldo;
//    }
//
//    @Override
//    public void adicionaContaDesdobrada() {
//        if (validaAdicionaContaDesdobrada()) {
//            try {
//                ConfigLiquidacaoResPagar configLiquidacao = getLiquidacaoFacade().getConfigLiquidacaoResPagarFacade().recuperaEventoPorContaDespesa(getDesdobramento().getConta(), TipoLancamento.NORMAL, selecionado.getDataLiquidacao(), selecionado.getEmpenho().getSubTipoDespesa());
//                if (configLiquidacao != null) {
//                    getDesdobramento().setEventoContabil(configLiquidacao.getEventoContabil());
//                } else {
//                    FacesUtil.addError("Não foi possível adicionar o Desdobramento!", " Não foi encontrado configuração vigente para a Conta " + getDesdobramento().getConta().toString() + "; Tipo Lançamento : Normal. ");
//                    return;
//                }
//
//                if (selecionado.getDesdobramentos().contains(getDesdobramento())) {
//                    selecionado.getDesdobramentos().set(selecionado.getDesdobramentos().indexOf(getDesdobramento()), getDesdobramento());
//                } else {
//                    getDesdobramento().setLiquidacao(selecionado);
//                    selecionado.getDesdobramentos().add(getDesdobramento());
//                    selecionado.setValor(selecionado.getValor().add(getDesdobramento().getValor()));
//                    Desdobramento desdobramento = getDesdobramento();
//                    desdobramento = new Desdobramento();
//                }
//            } catch (Exception e) {
//                FacesUtil.addError("Não foi possível adicionar o Desdobramento!", e.getMessage());
//            }
//        }
//    }
//
//
//
////    @Override
////    public void salvar() {
////        super.salvarNovaLiquidacaoResto();
////    }
//
//    @Override
//    public Boolean getVerificaEdicao() {
//        return super.getVerificaEdicao();
//    }
//}
