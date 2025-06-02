package br.com.webpublico.controle;

import br.com.webpublico.entidades.VinculoDeContratoFP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.VinculoDeContratoFPFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "vinculoDeContratoFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarVinculoDeContratoFP", pattern = "/vinculodecontrato/listar/", viewId = "/faces/rh/administracaodepagamento/vinculodecontratofp/lista.xhtml"),
    @URLMapping(id = "verVinculoDeContratoFP", pattern = "/vinculodecontrato/ver/#{vinculoDeContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/vinculodecontratofp/visualizar.xhtml"),
    @URLMapping(id = "editarVinculoDeContratoFP", pattern = "/vinculodecontrato/editar/#{vinculoDeContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/vinculodecontratofp/edita.xhtml"),
    @URLMapping(id = "novoVinculoDeContratoFP", pattern = "/vinculodecontrato/novo/", viewId = "/faces/rh/administracaodepagamento/vinculodecontratofp/edita.xhtml")
})
public class VinculoDeContratoFPControlador extends PrettyControlador<VinculoDeContratoFP> implements Serializable, CRUD {

    @EJB
    private VinculoDeContratoFPFacade vinculoDeContratoFPFacade;

    public VinculoDeContratoFPControlador() {
        super(VinculoDeContratoFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return vinculoDeContratoFPFacade;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        VinculoDeContratoFP tipoSelecionado = (VinculoDeContratoFP) selecionado;
        if (vinculoDeContratoFPFacade.existeCodigo(tipoSelecionado)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção !", "O Código informado já está cadastrado em outro Vínculo de Contrato");
            FacesContext.getCurrentInstance().addMessage("msg", msg);
            return false;
        }
        return true;
    }
    @URLAction(mappingId = "novoVinculoDeContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarVinculoDeContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verVinculoDeContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/vinculodecontrato/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
