//package br.com.webpublico.controle;
//
//import br.com.webpublico.controlerelatorio.AbstractReport;
//import br.com.webpublico.entidades.*;
//import br.com.webpublico.entidadesview.PagamentoEstornoRestoView;
//import br.com.webpublico.enums.CategoriaOrcamentaria;
//import br.com.webpublico.enums.StatusPagamento;
//import br.com.webpublico.enums.TipoLancamento;
//import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
//import br.com.webpublico.util.FacesUtil;
//import br.com.webpublico.enums.Operacoes;
//import br.com.webpublico.util.Util;
//import com.google.common.base.Preconditions;
//import com.ocpsoft.pretty.faces.annotation.URLAction;
//import com.ocpsoft.pretty.faces.annotation.URLMapping;
//import com.ocpsoft.pretty.faces.annotation.URLMappings;
//import net.sf.jasperreports.engine.JRException;
//
//import javax.faces.application.FacesMessage;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ViewScoped;
//import javax.faces.context.FacesContext;
//import javax.faces.event.ActionEvent;
//import java.io.IOException;
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//
///**
// * Created with IntelliJ IDEA.
// * User: Edi
// * Date: 12/11/13
// * Time: 15:19
// * To change this template use File | Settings | File Templates.
// */
//
//
//@ManagedBean(name = "pagamentoEstornoRestoControlador")
//@ViewScoped
//@URLMappings(mappings = {
//        @URLMapping(id = "novo-pagamento-estorno-resto", pattern = "/pagamento-estorno-resto/novo/", viewId = "/faces/financeiro/orcamentario/restosapagar/pagamentoestorno/edita.xhtml"),
//        @URLMapping(id = "editar-pagamento-estorno-resto", pattern = "/pagamento-estorno-resto/editar/#{pagamentoEstornoRestoControlador.id}/", viewId = "/faces/financeiro/orcamentario/restosapagar/pagamentoestorno/edita.xhtml"),
//        @URLMapping(id = "ver-pagamento-estorno-resto", pattern = "/pagamento-estorno-resto/ver/#{pagamentoEstornoRestoControlador.id}/", viewId = "/faces/financeiro/orcamentario/restosapagar/pagamentoestorno/visualizar.xhtml"),
//        @URLMapping(id = "listar-pagamento-estorno-resto", pattern = "/pagamento-estorno-resto/listar/", viewId = "/faces/financeiro/orcamentario/restosapagar/pagamentoestorno/lista.xhtml")
//})
//
//public class PagamentoEstornoRestoControlador extends PagamentoEstornoControlador implements Serializable {
//
//
//    @URLAction(mappingId = "novo-pagamento-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void novo() {
//        if (pagamentoEstornoFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
//            FacesUtil.addWarn("Aviso! ", pagamentoEstornoFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
//        }
//        super.novo();
//    }
//
//    @URLAction(mappingId = "ver-pagamento-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void ver() {
//        super.ver();
//        recuperarEditarVer();
//        recuperarValores();
//        recuperaEventoContabil();
//    }
//
//    @URLAction(mappingId = "editar-pagamento-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void editar() {
//        super.editar();
//        recuperarEditarVer();
//        recuperarValores();
//        recuperaEventoContabil();
//    }
//
//
//    @Override
//    public String getCaminhoPadrao() {
//        return "/pagamento-estorno-resto/";
//    }
//
//
//    public List getListaResto() {
//        List<PagamentoEstornoRestoView> listaView = new ArrayList<PagamentoEstornoRestoView>();
//        for (Object[] o : (List<Object[]>) pagamentoEstornoFacade.listaNoExercicioResto(super.getSistemaControlador().getExercicioCorrente())) {
//            listaView.add(new PagamentoEstornoRestoView(Long.parseLong(o[0].toString()), (Date) o[1], o[2].toString(), o[3].toString(), o[4].toString() + " - " + o[5].toString()));
//        }
//        return listaView;
//    }
//
//    public List<Pagamento> completaPagamentoResto(String parte) {
//        return pagamentoEstornoFacade.getPagamentoFacade().listapPagamentoRestoPorPessoaUnidadeStatus(parte.trim(), super.getSistemaControlador().getExercicioCorrente().getAno(), super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), StatusPagamento.DEFERIDO);
//    }
//
//    public void geraSaldoMigrados() {
//        try {
//            pagamentoEstornoFacade.geraSaldoMigracao(super.getSistemaControlador().getExercicioCorrente());
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Efetuado com sucesso!", "Efetuado com sucesso!"));
//        } catch (ExcecaoNegocioGenerica ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), ex.getMessage()));
//        }
//    }
//
//    public void recuperaEventoContabil() {
//        try {
//            selecionado.setEventoContabil(null);
//            ConfigPagamentoRestoPagar configuracaoResto = pagamentoEstornoFacade.getConfigPagamentoRestoPagarFacade().recuperaEventoRestoPorContaDespesa(selecionado.getPagamento().getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), TipoLancamento.ESTORNO, selecionado.getDataEstorno(), selecionado.getPagamento().getLiquidacao().getEmpenho().getTipoContaDespesa(),selecionado.getPagamento().getLiquidacao().getEmpenho().getTipoRestosProcessados());
//            Preconditions.checkNotNull(configuracaoResto, "Não foi encontrada uma Configuração para os Parametros informados ");
//            selecionado.setEventoContabil(configuracaoResto.getEventoContabil());
//        } catch (Exception e) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
//        }
//    }
//
//
//    @Override
//    public void salvar() {
//        PagamentoEstorno pe = ((PagamentoEstorno) selecionado);
//        if (pe.getId() == null) {
//            pe.setNumero(getNumeroMaiorEmpenho().toString());
//            pe.setUnidadeOrganizacional(super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente());
//            pe.setUnidadeOrganizacionalAdm(super.getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
//        }
//        if (Util.validaCampos(pe)) {
//            if (pe.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Valor Incorreto! ", " O valor do estorno deve ser maior que Zero"));
//            } else if (pe.getValor().compareTo(pe.getPagamento().getSaldo()) > 0) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Valor Incorreto! ", " O valor do estorno não pode ser maior que o saldo"));
//            } else if (pe.getValor().compareTo(pe.getPagamento().getValor()) > 0) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Valor Incorreto! ", " O valor do estorno não pode ser maior que o valor do pagamento"));
//            } else {
//                try {
//                    pagamentoEstornoFacade.getPagamentoFacade().getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(pe.getUnidadeOrganizacionalAdm(), pe.getUnidadeOrganizacional(), pe.getDataEstorno());
//                    selecionado.gerarHistoricos();
//                    if (operacao.equals(Operacoes.NOVO)) {
//                        pe.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
//                        pagamentoEstornoFacade.estornoRetencoes(pe, estornoRet());
//                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada ", " O Estorno do Pagamento de Restos a Pagar " + pe.getNumero() + " foi salvo com sucesso!"));
//                        redireciona();
//                    } else {
//                        pagamentoEstornoFacade.salvar(pe);
//                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada ", " O Estorno do Pagamento de Restos a Pagar" + pe.getNumero() + " foi alterado com sucesso!"));
//                        redireciona();
//                    }
//                } catch (ExcecaoNegocioGenerica ex) {
//                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), ex.getMessage()));
//                }
//            }
//
//        } else {
//            return;
//        }
//    }
//
//    public void cancelar() {
//        super.cancelar();
//    }
//
//    public void geraNotaPagamentoEstornoResto(ActionEvent evento) throws JRException, IOException {
//        PagamentoEstorno pagEstorno = (PagamentoEstorno) evento.getComponent().getAttributes().get("objeto");
//        AbstractReport abstractReport = AbstractReport.getAbstractReport();
//        String nomeArquivo = "NotaDeEstornoPagamentoResto.jasper";
//        HashMap parameters = new HashMap();
//        parameters.put("SQL", "AND NOTA.ID=" + pagEstorno.getId());
//        abstractReport.gerarRelatorio(nomeArquivo, parameters);
//    }
//
//}
