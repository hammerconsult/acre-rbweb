///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package br.com.webpublico.controle;
//
//import br.com.webpublico.controlerelatorio.AbstractReport;
//import br.com.webpublico.enums.CategoriaOrcamentaria;
//import br.com.webpublico.entidades.ConfigPagamentoRestoPagar;
//import br.com.webpublico.entidades.Pagamento;
//import br.com.webpublico.entidades.RetencaoPgto;
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
//import java.io.IOException;
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.HashMap;
//import javax.faces.application.FacesMessage;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ViewScoped;
//import javax.faces.context.FacesContext;
//import javax.faces.event.ActionEvent;
//
//@ManagedBean
//@ViewScoped
//@URLMappings(mappings = {
//    @URLMapping(id = "novo-pagamento-resto-pagar", pattern = "/pagamento-resto-pagar/novo/", viewId = "/faces/financeiro/orcamentario/restosapagar/pagamento/edita.xhtml"),
//    @URLMapping(id = "editar-pagamento-resto-pagar", pattern = "/pagamento-resto-pagar/editar/#{pagamentoRestoPagarControlador.id}/", viewId = "/faces/financeiro/orcamentario/restosapagar/pagamento/edita.xhtml"),
//    @URLMapping(id = "ver-pagamento-resto-pagar", pattern = "/pagamento-resto-pagar/ver/#{pagamentoRestoPagarControlador.id}/", viewId = "/faces/financeiro/orcamentario/restosapagar/pagamento/visualizar.xhtml"),
//    @URLMapping(id = "listar-pagamento-resto-pagar", pattern = "/pagamento-resto-pagar/listar/", viewId = "/faces/financeiro/orcamentario/restosapagar/pagamento/lista.xhtml")
//})
//public class PagamentoRestoPagarControlador extends PagamentoControlador implements Serializable {
//
//    private BigDecimal valor;
//
//    @URLAction(mappingId = "novo-pagamento-resto-pagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void novo() {
//        super.novo();
//        super.recuperarValores();
//
//        if (pagamentoFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
//            FacesUtil.addWarn("Aviso! ", pagamentoFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
//        }
//    }
//
//    @URLAction(mappingId = "ver-pagamento-resto-pagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void ver() {
//        super.ver();
//        super.recuperarValores();
//    }
//
//    @URLAction(mappingId = "editar-pagamento-resto-pagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void editar() {
//        super.editar();
//        valor = selecionado.getValor();
//        super.recuperarValores();
//    }
//
//    public BigDecimal getValor() {
//        return valor;
//    }
//
//    @Override
//    public void salvar() {
//        try {
//            Pagamento pgto = ((Pagamento) selecionado);
//            pgto.setDataRegistro(getSistemaControlador().getDataOperacao());
//            pgto.setUsuarioSistema(super.getSistemaControlador().getUsuarioCorrente());
//            pgto.setDataPagamento(null);
//            pgto.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
//            pgto.setExercicio(super.getSistemaControlador().getExercicioCorrente());
//            pgto.setUnidadeOrganizacional(super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente());
//            pgto.setUnidadeOrganizacionalAdm(super.getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
//            if (Util.validaCampos(selecionado) && validaValores()) {
//                if (pgto.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
//                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Valor Incorreto! Deve ser maior que zero!", " "));
//                } else {
//                    if (operacao.equals(Operacoes.NOVO)) {
//                        pgto.setExercicio(super.getSistemaControlador().getExercicioCorrente());
//                        pgto.setNumeroPagamento(getNumeroMaiorPagamento().toString());
//                        pgto.setStatus(StatusPagamento.ABERTO);
//                        pgto.gerarHistoricos();
//                        pagamentoFacade.salvarNovoResto(pgto);
//                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com Sucesso!", "Salvo com Sucesso!"));
//
//                    } else {
//
//                        pagamentoFacade.salvarPagamentoResto(pgto, getValor());
//
//                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com Sucesso!", "Salvo com Sucesso!"));
//                    }
//                    redireciona();
//                }
//            }
//        } catch (Exception e) {
//            throw new ExcecaoNegocioGenerica(e.getMessage());
//        }
//
//    }
//
//    @Override
//    public void efetivarPagamento() {
//        Pagamento pag = ((Pagamento) selecionado);
//        if (validaDeferirPagamento() && verificaSaldoContaFinanceira()) {
//            try {
//                pagamentoFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(pag.getUnidadeOrganizacionalAdm(), pag.getUnidadeOrganizacional(), pag.getDataPagamento());
//                pagamentoFacade.efetivarPagamentoRestoPagar(pag);
//
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Pagamento Efetivado com Sucesso!", "Pagamento Efetivado com Sucesso!"));
//                redireciona();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
//            }
//        }
//    }
//
//    @Override
//    public Boolean validaCamposRetencao(RetencaoPgto re) {
//        Boolean retorno = true;
//        if (re.getDataRetencao() == null) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "O campo Data é obrigatório!"));
//            retorno = false;
//        }
//        if (re.getContaExtraorcamentaria() == null) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "O campo Conta Extraorçamentária é obrigatório!"));
//            retorno = false;
//        }
//        if (re.getComplementoHistorico() == null) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "O campo Histórico é obrigatório!"));
//            retorno = false;
//        }
//        if (re.getFonteDeRecursos() == null) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "O campo Fonte de Recursos é obrigatório!"));
//            retorno = false;
//        }
//        if (re.getValor().compareTo(BigDecimal.ZERO) <= 0) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "O campo Valor deve ser maior que zero(0)!"));
//            retorno = false;
//        }
//        return retorno;
//    }
//
//    @Override
//    public void recuperaEventoContabil() {
//        try {
//            selecionado.setEventoContabil(null);
//            ConfigPagamentoRestoPagar configuracao;
//            configuracao = pagamentoFacade.getConfigPagamentoRestoPagarFacade().recuperaEventoRestoPorContaDespesa(selecionado.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), TipoLancamento.NORMAL, selecionado.getPrevistoPara(), selecionado.getLiquidacao().getEmpenho().getTipoContaDespesa(),selecionado.getLiquidacao().getEmpenho().getTipoRestosProcessados());
//            Preconditions.checkNotNull(configuracao, "Não foi encontrada uma Configuração para os Parametros informados ");
//            selecionado.setEventoContabil(configuracao.getEventoContabil());
//        } catch (Exception e) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
//        }
//    }
//
//    @Override
//    public void excluir() {
//        pagamentoFacade.excluirResto(selecionado);
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Registro excluído com sucesso!"));
//    }
//
//    @Override
//    public Boolean validaValores() {
//        Boolean retorno = true;
//        Pagamento pag = ((Pagamento) selecionado);
//        BigDecimal valor = pag.getValor();
//        if (pag.getLiquidacao() != null) {
//            if (pag.getLiquidacao().getValor().compareTo(valor) < 0) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Valor do Pagamento de Resto a Pagar é maior que o saldo disponível na Liquidação de Resto a Pagar!"));
//                retorno = false;
//            } else {
//                retorno = true;
//            }
//        }
//        return retorno;
//    }
//
//    public void geraNotaPagamentoResto(ActionEvent evento) throws JRException, IOException {
//        Pagamento pag = (Pagamento) evento.getComponent().getAttributes().get("objeto");
//        AbstractReport abstractReport = AbstractReport.getAbstractReport();
//        String nomeArquivo = "NotaDePagamentoResto.jasper";
//        HashMap parameters = new HashMap();
//        parameters.put("SQL", "NOTA.ID=" + pag.getId());
//        abstractReport.gerarRelatorio(nomeArquivo, parameters);
//    }
//
////    @Override
////    public Boolean getVerificaEdicao() {
////        if ((operacao.equals(Operacoes.EDITAR)) && (selecionado.getStatus() != StatusPagamento.ABERTO)) {
////            return true;
////        } else {
////            return false;
////        }
////    }
//    @Override
//    public String getCaminhoPadrao() {
//        return "/pagamento-resto-pagar/";
//    }
//
//
//}
