package br.com.webpublico.controle;

import br.com.webpublico.entidades.AprovacaoResponsavelTecnicoFiscal;
import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AprovacaoResponsavelTecnicoFiscalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Desenvolvimento on 31/03/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-aprovacao-tecnico-fiscal-obra", pattern = "/aprovacao-fiscal/novo/", viewId = "/faces/administrativo/obras/aprovacaoresponsaveltecnicofiscal/edita.xhtml"),
    @URLMapping(id = "editar-aprovacao-tecnico-fiscal-obra", pattern = "/aprovacao-fiscal/editar/#{aprovacaoResponsavelTecnicoFiscalControlador.id}/", viewId = "/faces/administrativo/obras/aprovacaoresponsaveltecnicofiscal/edita.xhtml"),
    @URLMapping(id = "ver-aprovacao-tecnico-fiscal-obra", pattern = "/aprovacao-fiscal/ver/#{aprovacaoResponsavelTecnicoFiscalControlador.id}/", viewId = "/faces/administrativo/obras/aprovacaoresponsaveltecnicofiscal/visualizar.xhtml"),
    @URLMapping(id = "listar-aprovacao-tecnico-fiscal-obra", pattern = "/aprovacao-fiscal/listar/", viewId = "/faces/administrativo/obras/aprovacaoresponsaveltecnicofiscal/lista.xhtml")
})
public class AprovacaoResponsavelTecnicoFiscalControlador extends PrettyControlador<AprovacaoResponsavelTecnicoFiscal> implements Serializable, CRUD {

    @EJB
    private AprovacaoResponsavelTecnicoFiscalFacade aprovacaoFacade;

    public AprovacaoResponsavelTecnicoFiscalControlador() {
        super(AprovacaoResponsavelTecnicoFiscal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return aprovacaoFacade;
    }

    @URLAction(mappingId = "novo-aprovacao-tecnico-fiscal-obra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(aprovacaoFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataAprovacao(aprovacaoFacade.getSistemaFacade().getDataOperacao());
    }

    public void selecionarAtoLegal(ActionEvent evento) {
        selecionado.setAtoLegal((AtoLegal) evento.getComponent().getAttributes().get("objeto"));
    }

    @URLAction(mappingId = "ver-aprovacao-tecnico-fiscal-obra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    @URLAction(mappingId = "editar-aprovacao-tecnico-fiscal-obra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        FacesUtil.addOperacaoNaoPermitida("Aprovação de fiscal não poderá ser editada.");
        redireciona();
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return aprovacaoFacade.getAtoLegalFacade().buscarAtosLegais(parte.trim());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aprovacao-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(AprovacaoResponsavelTecnicoFiscal.SituacaoAprovacao.values());
    }

    @Override
    public boolean validaRegrasEspecificas() {
        try {
            selecionado.validarConfirmacao();
            return true;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return ve.validou;
        }
    }
}
