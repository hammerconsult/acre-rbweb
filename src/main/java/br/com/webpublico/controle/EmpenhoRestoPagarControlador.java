//package br.com.webpublico.controle;
//
//import br.com.webpublico.controlerelatorio.AbstractReport;
//import br.com.webpublico.entidades.Empenho;
//import br.com.webpublico.entidadesauxiliares.VwHierarquiaDespesaORC;
//import br.com.webpublico.util.ConverterAutoComplete;
//import br.com.webpublico.util.FacesUtil;
//import br.com.webpublico.enums.Operacoes;
//import br.com.webpublico.util.Util;
//import com.ocpsoft.pretty.faces.annotation.URLAction;
//import com.ocpsoft.pretty.faces.annotation.URLMapping;
//import com.ocpsoft.pretty.faces.annotation.URLMappings;
//import net.sf.jasperreports.engine.JRException;
//import org.primefaces.event.SelectEvent;
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
//import java.util.List;
//
///**
// * Created with IntelliJ IDEA.
// * User: Romanini
// * Date: 09/10/13
// * Time: 11:16
// * To change this template use File | Settings | File Templates.
// */
//@ManagedBean(name = "empenhoRestoPagarControlador")
//@ViewScoped
//@URLMappings(mappings = {
//        @URLMapping(id = "novo-empenho-resto", pattern = "/empenho-resto-pagar/novo/", viewId = "/faces/financeiro/orcamentario/restosapagar/empenho/edita.xhtml"),
//        @URLMapping(id = "editar-empenho-resto", pattern = "/empenho-resto-pagar/editar/#{empenhoRestoPagarControlador.id}/", viewId = "/faces/financeiro/orcamentario/restosapagar/empenho/edita.xhtml"),
//        @URLMapping(id = "ver-empenho-resto", pattern = "/empenho-resto-pagar/ver/#{empenhoRestoPagarControlador.id}/", viewId = "/faces/financeiro/orcamentario/restosapagar/empenho/visualizar.xhtml"),
//        @URLMapping(id = "listar-empenho-resto", pattern = "/empenho-resto-pagar/listar/", viewId = "/faces/financeiro/orcamentario/restosapagar/empenho/lista.xhtml")
//})
//public class EmpenhoRestoPagarControlador extends EmpenhoControlador implements Serializable {
//
//    private ConverterAutoComplete empenhoConverter;
//    private VwHierarquiaDespesaORC vwHierarquiaDespesaORC;
//
//    @URLAction(mappingId = "ver-empenho-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void ver() {
//        super.ver();
//    }
//
//    @URLAction(mappingId = "editar-empenho-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
//    public void editar() {
//        super.editar();
//    }
//
//    @Override
//    @URLAction(mappingId = "novo-empenho-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    public void novo() {
//        super.novo();
//        if (empenhoFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
//            FacesUtil.addWarn("Aviso! ", empenhoFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
//        }
//    }
//
//    @Override
//    public String getCaminhoPadrao() {
//        return "/empenho-resto-pagar/";
//    }
//
//    public VwHierarquiaDespesaORC getVwHierarquiaDespesaORC() {
//        return vwHierarquiaDespesaORC;
//    }
//
//    public void setVwHierarquiaDespesaORC(VwHierarquiaDespesaORC vwHierarquiaDespesaORC) {
//        this.vwHierarquiaDespesaORC = vwHierarquiaDespesaORC;
//    }
//
//    public ConverterAutoComplete getEmpenhoConverter() {
//        if (empenhoConverter == null) {
//            empenhoConverter = new ConverterAutoComplete(Empenho.class, empenhoFacade);
//        }
//        return empenhoConverter;
//    }
//
//    public List<Empenho> completaEmpenho(String parte) {
//        return empenhoFacade.listaEmpenhoAnterioresAoExercicio(parte.trim(), getSistemaControlador().getExercicio(), getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente());
//
//    }
//
//    @Override
//    public void setaEvento() {
//        try {
//            selecionado.setEventoContabil(null);
//        } catch (Exception e) {
//        }
//    }
//
//    public void setaEmpenho(SelectEvent evt) {
//        Empenho e = (Empenho) evt.getObject();
//        vwHierarquiaDespesaORC = empenhoFacade.getDespesaORCFacade().recuperaStrDespesaPorId(e.getDespesaORC().getId());
//    }
//
//    @Override
//    public void salvar() {
//        try {
//            Empenho e = ((Empenho) selecionado);
//            if (e.getEmpenho() == null) {
//                FacesUtil.addError("Não foi possível salvar!", "Selecione um Empenho.");
//                return;
//            }
//            e.setUsuarioSistema(getUsuarioSistema());
//            e.setUnidadeOrganizacional(getUnidadeOrganizacional());
//            e.setUnidadeOrganizacionalAdm(getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
//            e.setDespesaORC(super.getComponenteTreeDespesaORC().getDespesaORCSelecionada());
//            e.setNumero(getNumeroMaiorEmpenhoResto().toString());
//            e.setExercicio(super.getSistemaControlador().getExercicioCorrente());
//            if (Util.validaCampos(e)) {
//                if (validaIntegracao()) {
//                    if (e.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) < 0) {
//                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Valor Incorreto.", "O valor do Empenho deve ser maior ou igual a 0 (ZERO)."));
//                        return;
//                    } else {
//                        if (operacao.equals(Operacoes.NOVO)) {
//
//                            empenhoFacade.salvarNovoResto(e);
//                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com Sucesso!", "O Empenho " + this.selecionado + " foi salvo com sucesso."));
//                            redireciona();
//                        } else {
//                            empenhoFacade.salvar(e);
//                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Alterado com Sucesso!", "O Empenho " + this.selecionado + " foi alterado com sucesso."));
//                            redireciona();
//                        }
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            FacesUtil.addError("Não foi possível salvar.", ex.getMessage());
//        }
//
//    }
//
//    public void geraNotaRestoEmpenho() throws JRException, IOException {
//        AbstractReport abstractReport = AbstractReport.getAbstractReport();
//        String nomeArquivo = "NotaDeRestoDeEmpenho.jasper";
//        HashMap parameters = new HashMap();
//        parameters.put("SQL", "EMP.ID=" + selecionado.getId());
//        abstractReport.gerarRelatorio(nomeArquivo, parameters);
//    }
//
//}
