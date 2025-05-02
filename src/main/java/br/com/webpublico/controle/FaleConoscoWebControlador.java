package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.enums.SituacaoFaleConosco;
import br.com.webpublico.enums.TipoFaleConosco;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FaleConoscoWebFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarFaleConoscoWeb", pattern = "/fale-conosco-web/listar/", viewId = "/faces/comum/fale-conosco-web/lista.xhtml"),
    @URLMapping(id = "verFaleConoscoWebSuporte", pattern = "/fale-conosco-web/ver/#{faleConoscoWebControlador.id}/", viewId = "/faces/comum/fale-conosco-web/visualizar.xhtml"),
    @URLMapping(id = "verFaleConoscoWebUsuario", pattern = "/fale-conosco-usuario/ver/#{faleConoscoWebControlador.id}/", viewId = "/faces/comum/fale-conosco-web/visualizar.xhtml")
})
public class FaleConoscoWebControlador extends PrettyControlador<FaleConoscoWeb> implements Serializable, CRUD {

    @EJB
    private FaleConoscoWebFacade facade;
    private FaleConoscoWebResposta resposta;
    private FaleConoscoWebResposta respostaSelecionada;
    private FaleConoscoWebResposta ultimaResposta;
    private FaleConoscoWebOcorrencia ocorrencia;
    private String filtro = "";
    private Boolean suporte;
    private List<FaleConoscoWeb> faleConoscoWebList;

    public FaleConoscoWebControlador() {
        super(FaleConoscoWeb.class);
    }

    @URLAction(mappingId = "verFaleConoscoWebSuporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verSuporte() {
        verFaleConosco();
        suporte = true;
    }

    @URLAction(mappingId = "verFaleConoscoWebUsuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verUsuario() {
        verFaleConosco();
        suporte = false;
    }

    private void verFaleConosco() {
        super.ver();
        Collections.sort(selecionado.getRespostas());
        Collections.sort(selecionado.getOcorrencias());
        responderFaleConoscoSuporte();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/fale-conosco-web/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void validarResposta() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(resposta);
        ve.lancarException();
    }

    private void validarOcorrencia() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(ocorrencia);
        ve.lancarException();
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        ve.lancarException();
    }

    public UsuarioSistema getUsuarioiSistema() {
        return facade.getSistemaFacade().getUsuarioCorrente();
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            facade.salvarNovoFaleConosco(selecionado);
            FacesUtil.addOperacaoRealizada("Mensagem enviada com sucesso.");
            FacesUtil.executaJavaScript("dlgFaleConosco.hide()");
            cancelarFaleConosco();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void salvarOcorrencia() {
        try {
            validarOcorrencia();
            facade.salvarOcorrencia(ocorrencia, selecionado);
            redirecionarParaVer();
            FacesUtil.addOperacaoRealizada("Ocorrência salva com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void salvarResposta() {
        try {
            validarResposta();
            selecionado = facade.salvarResposta(resposta, selecionado, ultimaResposta, suporte);
            redirecionarParaVer();
            FacesUtil.addOperacaoRealizada("Resposta enviada com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void salvarRespostaUsuario() {
        try {
            validarResposta();
            facade.salvarRespostaUsuario(resposta, ultimaResposta);
            FacesUtil.addOperacaoRealizada("Resposta enviada com sucesso.");
            cancelarResponder();
            FacesUtil.atualizarComponente("formFaleConosco");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void filtrar() {
        faleConoscoWebList = facade.buscarPorUsuario(getUsuarioiSistema(), filtro.trim());
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getId() + "/");
    }

    public void abrirDlgFaleConosco() {
        faleConoscoWebList = facade.buscarPorUsuario(getUsuarioiSistema(), filtro.trim());
        FacesUtil.executaJavaScript("dlgFaleConosco.show()");
        selecionado = null;
        resposta = null;
    }

    public void novoFaleConosco() {
        selecionado = new FaleConoscoWeb();
        selecionado.setDataEnvio(getDataOperacao());
        selecionado.setDataLancamento(new Date());
        selecionado.setUnidadeAdministrativa(getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUnidadeOrcamentaria(getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUsuario(getUsuarioiSistema());
        selecionado.setHierarquiaAdministrativa(getHierarquiaDaUnidade(selecionado.getUnidadeAdministrativa(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        selecionado.setHierarquiaOrcametaria(getHierarquiaDaUnidade(selecionado.getUnidadeOrcamentaria(), TipoHierarquiaOrganizacional.ORCAMENTARIA));
        selecionado.setPagina(getUrlAtual());
        selecionado.setRecurso(getCaminhoAtual());
        atribuirModuloSistema();
    }

    public void cancelarFaleConosco() {
        selecionado = null;
    }

    public List<SelectItem> getModulosSistema() {
        return Util.getListSelectItem(ModuloSistema.values());
    }

    public List<SelectItem> getSituacoesOcorrencia() {
        return Util.getListSelectItem(SituacaoFaleConosco.values());
    }

    public List<SelectItem> getTiposFaleConosco() {
        return Util.getListSelectItem(TipoFaleConosco.values());
    }

    private void atribuirModuloSistema() {
        if (selecionado.getRecurso() != null) {
            RecursoSistema recursoSistema = facade.recuperarRecursoSistema(selecionado.getRecurso());
            if (recursoSistema != null) {
                selecionado.setModulo(recursoSistema.getModulo());
            }
        }
    }

    private SistemaFacade getSistemaFacade() {
        return facade.getSistemaFacade();
    }

    private HierarquiaOrganizacional getHierarquiaDaUnidade(UnidadeOrganizacional unidade, TipoHierarquiaOrganizacional tipo) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(tipo.name(), unidade, getDataOperacao());
    }

    private Date getDataOperacao() {
        return getSistemaFacade().getDataOperacao();
    }

    public void cancelarOcorrencia() {
        ocorrencia = null;
    }

    public void cancelarResponder() {
        resposta = null;
        ultimaResposta = null;
        selecionado = null;
    }

    public void responderFaleConoscoUsuario(FaleConoscoWeb faleConosco) {
        this.selecionado = facade.recuperarComDependenciaRespostas(faleConosco.getId());
        respoderFaleConosco();
    }

    @Override
    public void cancelar() {
        if (suporte) {
            super.cancelar();
        }
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath() + "/home");
        } catch (IOException e) {
            logger.error("Erro ao voltar para home fale conosco {}", e);
        }
    }

    public void responderFaleConoscoSuporte() {
        respoderFaleConosco();
    }

    private void respoderFaleConosco() {
        novaResposta(this.selecionado.getRespostaAtual());
        ultimaResposta.setVisualizadaEm(new Date());
        resposta.setFaleConoscoWeb(this.selecionado);
        this.selecionado.getRespostas().add(resposta);
    }

    public void novaOcorrencia() {
        ocorrencia = new FaleConoscoWebOcorrencia();
        ocorrencia.setDataOcorrencia(new Date());
        ocorrencia.setFaleConoscoWeb(selecionado);
        ocorrencia.setUsuario(getUsuarioiSistema());
        FacesUtil.executaJavaScript("dlgOcorrencia.show()");
    }

    public void verOcorrencia(FaleConoscoWebOcorrencia ocorrencia) {
        this.ocorrencia = facade.recuperarOcorrenciaAnexos(ocorrencia);
        FacesUtil.executaJavaScript("dlgOcorrenciaDetalhes.show()");
    }

    public void novaResposta(FaleConoscoWebResposta respostaOrigem) {
        this.ultimaResposta = respostaOrigem;
        resposta = new FaleConoscoWebResposta();
        resposta.setEnviadaEm(new Date());
        resposta.setFaleConoscoWeb(selecionado);
        resposta.setUsuario(getUsuarioiSistema());
    }

    public void verFaleConosco(FaleConoscoWeb faleConosco) {
        FacesUtil.redirecionamentoInterno("/fale-conosco-usuario/ver/" + faleConosco.getId() + "/");
    }

    public void selecionarResposta(FaleConoscoWebResposta resposta){
        this.respostaSelecionada = facade.recuperarRespostaAnexos(resposta);
    }

    public boolean renderFaleConosco() {
        return selecionado != null && resposta == null;
    }

    public boolean renderResposta() {
        return selecionado != null && resposta != null;
    }

    public boolean renderListFaleConosco() {
        return selecionado == null && resposta == null;
    }

    public boolean renderFaleConoscoComRegistros() {
        return faleConoscoWebList != null && !faleConoscoWebList.isEmpty();
    }

    public FaleConoscoWebResposta getResposta() {
        return resposta;
    }

    public void setResposta(FaleConoscoWebResposta resposta) {
        this.resposta = resposta;
    }

    public FaleConoscoWebOcorrencia getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(FaleConoscoWebOcorrencia ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<FaleConoscoWeb> getFaleConoscoWebList() {
        return faleConoscoWebList;
    }

    public void setFaleConoscoWebList(List<FaleConoscoWeb> faleConoscoWebList) {
        this.faleConoscoWebList = faleConoscoWebList;
    }

    public FaleConoscoWebResposta getUltimaResposta() {
        return ultimaResposta;
    }

    public void setUltimaResposta(FaleConoscoWebResposta ultimaResposta) {
        this.ultimaResposta = ultimaResposta;
    }

    public Boolean getSuporte() {
        return suporte;
    }

    public void setSuporte(Boolean suporte) {
        this.suporte = suporte;
    }

    public FaleConoscoWebResposta getRespostaSelecionada() {
        return respostaSelecionada;
    }

    public void setRespostaSelecionada(FaleConoscoWebResposta respostaSelecionada) {
        this.respostaSelecionada = respostaSelecionada;
    }
}
