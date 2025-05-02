package br.com.webpublico.controle;

import br.com.webpublico.entidades.ViagemCondutorOtt;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ViagemCondutorOttFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "viagemCondutorOttControlador")
@ViewScoped
@URLMappings( mappings = {
    @URLMapping(id = "verViagemCondutorOtt", pattern = "/viagem-condutor-ott/ver/#{viagemCondutorOttControlador.id}/",
        viewId = "/faces/tributario/rbtrans/viagemcondutorott/visualizar.xhtml"),
        @URLMapping(id = "listarViagemCondutorOtt", pattern = "/viagem-condutor-ott/listar/", viewId = "/faces/tributario/rbtrans/viagemcondutorott/lista.xhtml")
    }
)
public class ViagemCondutorOttControlador extends PrettyControlador<ViagemCondutorOtt> implements Serializable, CRUD {

    @EJB
    private ViagemCondutorOttFacade viagemCondutorOttFacade;

    public ViagemCondutorOttControlador(){
        super(ViagemCondutorOtt.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return viagemCondutorOttFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/viagem-condutor-ott/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verViagemCondutorOtt", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }
}
