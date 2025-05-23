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
import java.math.BigDecimal;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "vinculoDeContratoFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarVinculoDeContratoFP", pattern = "/vinculodecontrato/listar/", viewId = "/faces/rh/administracaodepagamento/vinculodecontratofp/lista.xhtml"),
    @URLMapping(id = "verVinculoDeContratoFP", pattern = "/vinculodecontrato/ver/#{vinculoDeContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/vinculodecontratofp/visualizar.xhtml")
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
            if(selecionado.getPercentualAposentadoria() == null) {
                selecionado.setPercentualAposentadoria(BigDecimal.ZERO);
            }
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
