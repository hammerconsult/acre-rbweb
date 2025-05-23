package br.com.webpublico.controle;

import br.com.webpublico.entidades.AprovacaoSolicitacaoIncorporacaoMovel;
import br.com.webpublico.entidades.SolicitacaoIncorporacaoMovel;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.SituacaoAprovacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AprovacaoSolicitacaoIncorporacaoMovelFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Desenvolvimento on 02/02/2016.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAprovacaoIncorporacao", pattern = "/aprovacao-solicitacao-incorporacao-movel/novo/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/aprovacao/edita.xhtml"),
    @URLMapping(id = "editarAprovacaoIncorporacao", pattern = "/aprovacao-solicitacao-incorporacao-movel/editar/#{aprovacaoSolicitacaoIncorporacaoMovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/aprovacao/edita.xhtml"),
    @URLMapping(id = "listarAprovacaoIncorporacao", pattern = "/aprovacao-solicitacao-incorporacao-movel/listar/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/aprovacao/lista.xhtml"),
    @URLMapping(id = "verAprovacaoIncorporacao", pattern = "/aprovacao-solicitacao-incorporacao-movel/ver/#{aprovacaoSolicitacaoIncorporacaoMovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/aprovacao/visualizar.xhtml")
})
public class AprovacaoSolicitacaoIncorporacaoMovelControlador extends PrettyControlador<AprovacaoSolicitacaoIncorporacaoMovel> implements Serializable, CRUD {

    @EJB
    private AprovacaoSolicitacaoIncorporacaoMovelFacade facade;
    private SolicitacaoIncorporacaoMovel solicitacao;

    public AprovacaoSolicitacaoIncorporacaoMovelControlador() {
        super(AprovacaoSolicitacaoIncorporacaoMovel.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aprovacao-solicitacao-incorporacao-movel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public SolicitacaoIncorporacaoMovel getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoIncorporacaoMovel solicitacao) {
        this.solicitacao = solicitacao;
    }

    @URLAction(mappingId = "novaAprovacaoIncorporacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataAprovacao(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "verAprovacaoIncorporacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarSolicitacaoIncorporacaoMovel();
    }

    @URLAction(mappingId = "editarAprovacaoIncorporacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarSolicitacaoIncorporacaoMovel();
    }

    public List<SelectItem> listaDeSituacoes() {
        return Util.getListSelectItem(SituacaoAprovacao.values());
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            verificarSeUsuarioIsGestorPatrimonioDaUnidade();
            validarMotivoDeRejeicao();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    @Override
    public void excluir() {
        this.selecionado.getSolicitacao().setSituacao(SituacaoEventoBem.CONCLUIDO);
        facade.getIncorporacaoMovelFacade().salvar(this.selecionado.getSolicitacao());
        super.excluir();
    }

    private void validarMotivoDeRejeicao() {
        ValidacaoException ve = new ValidacaoException();
        if (SituacaoAprovacao.REJEITADO.equals(selecionado.getSituacaoAprovacao())
            && (selecionado.getMotivo() == null || selecionado.getMotivo().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo deve ser informado.");
        }
        ve.lancarException();
    }

    public void recuperarSolicitacaoIncorporacaoMovel() {
        solicitacao = facade.getIncorporacaoMovelFacade().recuperar(selecionado.getSolicitacao().getId());
    }

    public Boolean canExcluirAprovacao() {
        return facade.isAprovacaoIncorporacaoEfetivada(selecionado.getSolicitacao());
    }

    public void verificarSeUsuarioIsGestorPatrimonioDaUnidade() {
        ValidacaoException ve = new ValidacaoException();
        List<UnidadeOrganizacional> unidades = facade.getHierarquiaOrganizacionalFacade().buscarUnidadesOndeUsuarioEhGestorPatrimonio(
            facade.getSistemaFacade().getUsuarioCorrente(),
            facade.getSistemaFacade().getDataOperacao(), solicitacao.getUnidadeAdministrativa());
        if (unidades.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O usuário deve ser Gestor da unidade de Solicitação de Incorporação de bem Móvel.");
        }
        ve.lancarException();
    }
}
