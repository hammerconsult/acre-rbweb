package br.com.webpublico.controle;

import br.com.webpublico.entidades.SolicitacaoUnidadeRequerente;
import br.com.webpublico.entidades.SolicitacaoUnidadeRequerenteUnidade;
import br.com.webpublico.entidades.UnidadeDistribuidora;
import br.com.webpublico.enums.SituacaoSolicitacaoUnidadeRequerente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SolicitacaoUnidadeRequerenteFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by mga on 02/08/19.
 */

@ManagedBean(name = "solicitacaoUnidadeRequerenteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoSolicitacaoRequerente", pattern = "/solicitacao-unidade-requerente/novo/", viewId = "/faces/administrativo/materiais/solicitacao-unidade-requerente/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoRequerente", pattern = "/solicitacao-unidade-requerente/editar/#{solicitacaoUnidadeRequerenteControlador.id}/", viewId = "/faces/administrativo/materiais/solicitacao-unidade-requerente/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoRequerente", pattern = "/solicitacao-unidade-requerente/listar/", viewId = "/faces/administrativo/materiais/solicitacao-unidade-requerente/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoRequerente", pattern = "/solicitacao-unidade-requerente/ver/#{solicitacaoUnidadeRequerenteControlador.id}/", viewId = "/faces/administrativo/materiais/solicitacao-unidade-requerente/visualizar.xhtml")
})
public class SolicitacaoUnidadeRequerenteControlador extends PrettyControlador<SolicitacaoUnidadeRequerente> implements Serializable, CRUD {

    @EJB
    private SolicitacaoUnidadeRequerenteFacade facade;
    private SolicitacaoUnidadeRequerenteUnidade unidadeRequerente;

    public SolicitacaoUnidadeRequerenteControlador() {
        super(SolicitacaoUnidadeRequerente.class);
    }

    private void validarSolicitacao() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (selecionado.getUnidadesRequerentes() == null || selecionado.getUnidadesRequerentes().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Para continuar adicione unidades requerentes a solicitação.");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarSolicitacao();
            selecionado = facade.salvarSolicitacao(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }catch (Exception ex){
            descobrirETratarException(ex);
        }
    }

    @Override
    @URLAction(mappingId = "novoSolicitacaoRequerente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataSolicitacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setSituacao(SituacaoSolicitacaoUnidadeRequerente.EM_ELABORACAO);
        novaUnidadeRequerente();
    }

    @Override
    @URLAction(mappingId = "editarSolicitacaoRequerente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        novaUnidadeRequerente();
    }

    @URLAction(mappingId = "verSolicitacaoRequerente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<UnidadeDistribuidora> completaUnidadesDistribuidoras(String filtro) {
        return facade.getUnidadeDistribuidoraFacade().buscarUnidadesDistribuidoraPorCodigoOuDescricao(filtro);
    }

    public void removerUnidadeRequerente(SolicitacaoUnidadeRequerenteUnidade unidade) {
        selecionado.getUnidadesRequerentes().remove(unidade);
    }

    private void validarUnidadeRequerente() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeRequerente.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("o campo Unidade Requerente deve ser informado.");
        } else {
            for (SolicitacaoUnidadeRequerenteUnidade unidade : selecionado.getUnidadesRequerentes()) {
                if (unidade.getHierarquiaOrganizacional().equals(unidadeRequerente.getHierarquiaOrganizacional())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Unidade selecionada já está na lista de Unidades Requerentes.");
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarUnidadeOrganizacionalRequerente() {
        try {
            validarUnidadeRequerente();
            unidadeRequerente.setUnidadeOrganizacional(unidadeRequerente.getHierarquiaOrganizacional().getSubordinada());
            unidadeRequerente.setSolicitacao(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getUnidadesRequerentes(), unidadeRequerente);
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabRequerente:undsRequerentes_input')");
            Collections.sort(selecionado.getUnidadesRequerentes());
            novaUnidadeRequerente();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void novaUnidadeRequerente() {
        unidadeRequerente = new SolicitacaoUnidadeRequerenteUnidade();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-unidade-requerente/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public SolicitacaoUnidadeRequerenteUnidade getUnidadeRequerente() {
        return unidadeRequerente;
    }

    public void setUnidadeRequerente(SolicitacaoUnidadeRequerenteUnidade unidadeRequerente) {
        this.unidadeRequerente = unidadeRequerente;
    }
}
