package br.com.webpublico.controle;

import br.com.webpublico.entidades.ResponsavelTecnicoFiscal;
import br.com.webpublico.entidades.SolicitacaoResponsaveltecnicoFiscal;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoSolicitacaoFiscal;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SolicitacaoResponsavelTecnicoFiscalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
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
 * Created by Desenvolvimento on 30/03/2016.
 */


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoSolictacaoResponsavelTecnicoFiscal", pattern = "/solicitacao-fiscal/novo/", viewId = "/faces/administrativo/obras/solicitatacaoresponsaveltecnicofiscal/edita.xhtml"),
        @URLMapping(id = "editarSolictacaoResponsavelTecnicoFiscal", pattern = "/solicitacao-fiscal/editar/#{solicitacaoResponsavelTecnicoFiscalControlador.id}/", viewId = "/faces/administrativo/obras/solicitatacaoresponsaveltecnicofiscal/edita.xhtml"),
        @URLMapping(id = "listarSolictacaoResponsavelTecnicoFiscal", pattern = "/solicitacao-fiscal/listar/", viewId = "/faces/administrativo/obras/solicitatacaoresponsaveltecnicofiscal/lista.xhtml"),
        @URLMapping(id = "verSolictacaoResponsavelTecnicoFiscal", pattern = "/solicitacao-fiscal/ver/#{solicitacaoResponsavelTecnicoFiscalControlador.id}/", viewId = "/faces/administrativo/obras/solicitatacaoresponsaveltecnicofiscal/visualizar.xhtml")
})
public class SolicitacaoResponsavelTecnicoFiscalControlador extends PrettyControlador<SolicitacaoResponsaveltecnicoFiscal> implements Serializable, CRUD {

    @EJB
    private SolicitacaoResponsavelTecnicoFiscalFacade solicitacaoResponsavelTecnicoFiscalFacade;

    public SolicitacaoResponsavelTecnicoFiscalControlador() {
        super(SolicitacaoResponsaveltecnicoFiscal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return solicitacaoResponsavelTecnicoFiscalFacade;
    }

    @URLAction(mappingId = "novoSolictacaoResponsavelTecnicoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataSolicitacao(solicitacaoResponsavelTecnicoFiscalFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(solicitacaoResponsavelTecnicoFiscalFacade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<SelectItem> getTiposPrincipalSubstituto() {
        return Util.getListSelectItemSemCampoVazio(SolicitacaoResponsaveltecnicoFiscal.PrincipalSubstituto.values(), false);
    }

    @URLAction(mappingId = "verSolictacaoResponsavelTecnicoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    @URLAction(mappingId = "editarSolictacaoResponsavelTecnicoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if(SituacaoSolicitacaoFiscal.APROVADA.equals(selecionado.getSituacaoSolicitacao())){
            FacesUtil.addOperacaoNaoPermitida("A solicitação de fiscal está aprovada e não poderá ser editada.");
            redireciona();
        }
        selecionado.setTipoResponsavel(selecionado.getResponsavelTecnicoFiscal().getTipoResponsavel());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ResponsavelTecnicoFiscal> completarResponsavelTecnicoFiscal(String parte) {
        if (selecionado.getTipoResponsavel() != null) {
            return solicitacaoResponsavelTecnicoFiscalFacade.getResponsavelTecnicoFiscalFacade().buscarResponsavelPorTiposVigentes(parte, selecionado.getTipoResponsavel());
        }
        return Lists.newArrayList();
    }

    public void limparFiscal() {
        selecionado.setResponsavelTecnicoFiscal(null);
    }

    public List<SolicitacaoResponsaveltecnicoFiscal> completaSolicitacaoSemAprovacao(String parte){
        return solicitacaoResponsavelTecnicoFiscalFacade.buscarSolicitacoesSemAprovacao(parte);
    }
}
