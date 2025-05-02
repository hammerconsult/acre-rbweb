package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SolicitacaoUnidadeRequerente;
import br.com.webpublico.entidades.UnidadeDistribuidora;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UnidadeDistribuidoraFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mga on 01/08/19.
 */

@ManagedBean(name = "unidadeDistribuidoraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoUnidadeDistribuidora", pattern = "/unidade-distribuidora/novo/", viewId = "/faces/administrativo/materiais/unidade-distribuidora/edita.xhtml"),
    @URLMapping(id = "editarUnidadeDistribuidora", pattern = "/unidade-distribuidora/editar/#{unidadeDistribuidoraControlador.id}/", viewId = "/faces/administrativo/materiais/unidade-distribuidora/edita.xhtml"),
    @URLMapping(id = "listarUnidadeDistribuidora", pattern = "/unidade-distribuidora/listar/", viewId = "/faces/administrativo/materiais/unidade-distribuidora/lista.xhtml"),
    @URLMapping(id = "verUnidadeDistribuidora", pattern = "/unidade-distribuidora/ver/#{unidadeDistribuidoraControlador.id}/", viewId = "/faces/administrativo/materiais/unidade-distribuidora/visualizar.xhtml")
})
public class UnidadeDistribuidoraControlador extends PrettyControlador<UnidadeDistribuidora> implements Serializable, CRUD {

    @EJB
    private UnidadeDistribuidoraFacade facade;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<SolicitacaoUnidadeRequerente> solicitacoes;

    public UnidadeDistribuidoraControlador() {
        metadata = new EntidadeMetaData(UnidadeDistribuidora.class);
    }

    @Override
    @URLAction(mappingId = "novoUnidadeDistribuidora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarUnidadeDistribuidora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verUnidadeDistribuidora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver(){
        super.ver();
        buscarSolicitacoes();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/unidade-distribuidora/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void salvar(){
        try {
            validarUnidadeDistribuidora();
            facade.salvarUnidadeDistribuidora(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex){
            descobrirETratarException(ex);
        }
    }

    public void buscarSolicitacoes(){
        solicitacoes = facade.getSolicitacaoUnidadeRequerenteFacade().buscarSolicitacaoPorUnidadeDistribuidora(selecionado);
    }

    public boolean hasUnidadeDistribuidoraSolicitadas(){
        return solicitacoes !=null && !solicitacoes.isEmpty();
    }

    private void validarUnidadeDistribuidora() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (facade.buscarUnidadeDistribuidora(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma Unidade Distribuidora cadastrada com a Unidade: " + selecionado + ".");
        }
        ve.lancarException();
    }

    public List<HierarquiaOrganizacional> completarHierarquiasAdministrativas(String filtro) {
        return facade.getHierarquiaOrganizacionalFacade().listaTodasHierarquiaHorganizacionalTipo(filtro.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getDataOperacao());
    }

    public List<SolicitacaoUnidadeRequerente> getSolicitacoes() {
        return solicitacoes;
    }

    public void setSolicitacoes(List<SolicitacaoUnidadeRequerente> solicitacoes) {
        this.solicitacoes = solicitacoes;
    }
}
