//package br.com.webpublico.controle;
//
//import br.com.webpublico.controlerelatorio.AbstractReport;
//import br.com.webpublico.entidades.*;
//import br.com.webpublico.enums.CategoriaOrcamentaria;
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
//import java.io.IOException;
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.HashMap;
//
///**
// * Created with IntelliJ IDEA.
// * User: wiplash
// * Date: 12/11/13
// * Time: 15:44
// * To change this template use File | Settings | File Templates.
// */
//@ManagedBean(name = "empenhoRestoPagarEstornoControlador")
//@ViewScoped
//@URLMappings(mappings = {
//        @URLMapping(id = "novo-empenho-estorno-resto", pattern = "/empenho-estorno-resto-pagar/novo/", viewId = "/faces/financeiro/orcamentario/restosapagar/empenhoestorno/edita.xhtml"),
//        @URLMapping(id = "editar-empenho-estorno-resto", pattern = "/empenho-estorno-resto-pagar/editar/#{empenhoRestoPagarEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/restosapagar/empenhoestorno/edita.xhtml"),
//        @URLMapping(id = "ver-empenho-estorno-resto", pattern = "/empenho-estorno-resto-pagar/ver/#{empenhoRestoPagarEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/restosapagar/empenhoestorno/visualizar.xhtml"),
//        @URLMapping(id = "listar-empenho-estorno-resto", pattern = "/empenho-estorno-resto-pagar/listar/", viewId = "/faces/financeiro/orcamentario/restosapagar/empenhoestorno/lista.xhtml")
//})
//public class EmpenhoRestoPagarEstornoControlador extends EmpenhoEstornoControlador implements Serializable {
//
//    @URLAction(mappingId = "ver-empenho-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void ver() {
//        super.ver();
//        recuperaEditarVer();
//    }
//
//    @URLAction(mappingId = "editar-empenho-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void editar() {
//        super.editar();
//        recuperaEditarVer();
//    }
//
//    @Override
//    public void recuperarEventoContabil() {
//        try {
//            selecionado.setEventoContabil(null);
//            ConfigEmpenhoRestoPagar configuracao = getEmpenhoEstornoFacade().getEmpenhoFacade().getConfigEmpenhoRestoFacade().recuperaEventoPorContaDespesa(selecionado.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), TipoLancamento.ESTORNO, selecionado.getDataEstorno());
//            Preconditions.checkNotNull(configuracao, "Não foi encontrada uma Configuração para os Parametros informados ");
//            selecionado.setEventoContabil(configuracao.getEventoContabil());
//        } catch (Exception e) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
//        }
//    }
//
//    @Override
//    @URLAction(mappingId = "novo-empenho-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    public void novo() {
//        super.novo();
//        selecionado.setDataEstorno(getSistemaControlador().getDataOperacao());
//
//        if (getEmpenhoEstornoFacade().getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
//            FacesUtil.addWarn("Aviso! ", getEmpenhoEstornoFacade().getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
//        }
//    }
//
//    @Override
//    public String getCaminhoPadrao() {
//        return "/empenho-estorno-resto-pagar/";
//    }
//
//    private void recuperaEditarVer() {
//        selecionado = getFacade().recuperar(getId());
//    }
//
//    @Override
//    public void salvar() {
//        selecionado.setUnidadeOrganizacional(getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente());
//        selecionado.setUnidadeOrganizacionalAdm(getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
//        if (Util.validaCampos(selecionado)) {
//            selecionado.gerarHistoricos();
//            if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Valor Incorreto! Deve ser maior que zero(0)!", " "));
//                return;
//            } else {
//                try {
//                    getEmpenhoEstornoFacade().getEmpenhoFacade().getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(selecionado.getUnidadeOrganizacionalAdm(), selecionado.getUnidadeOrganizacional(), selecionado.getDataEstorno());
//                    if (operacao.equals(Operacoes.NOVO)) {
////                        selecionado.setNumero(getNumeroMaiorEmpenho().toString());
//                        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
//                        getEmpenhoEstornoFacade().salvarNovo(selecionado);
//                    } else {
//                        getEmpenhoEstornoFacade().salvar(selecionado);
//                    }
//                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com sucesso!", "O Empenho De Estorno De Resto a Pagar " + selecionado.getNumero()));
//                    redireciona();
//                } catch (ExcecaoNegocioGenerica ex) {
//                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ""));
//                }
//            }
//        }
//    }
//
//    public void geraNotaRestoEmpenhoEstorno() throws JRException, IOException {
//        AbstractReport abstractReport = AbstractReport.getAbstractReport();
//        String nomeArquivo = "NotaDeEstornoDeRestoDeEmpenho.jasper";
//        HashMap parameters = new HashMap();
//        parameters.put("SQL", " ESTORNO.ID=" + selecionado.getId());
//        abstractReport.gerarRelatorio(nomeArquivo, parameters);
//    }
//}
