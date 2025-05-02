package br.com.webpublico.controle;

import br.com.webpublico.entidades.UF;
import br.com.webpublico.entidades.VaraCivel;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.UFFacade;
import br.com.webpublico.negocios.VaraCivelFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "varaCivelControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaVaraCivel", pattern = "/tributario/varacivel/novo/", viewId = "/faces/tributario/dividaativa/varacivel/edita.xhtml"),
        @URLMapping(id = "editarVaraCivel", pattern = "/tributario/varacivel/editar/#{varaCivelControlador.id}/", viewId = "/faces/tributario/dividaativa/varacivel/edita.xhtml"),
        @URLMapping(id = "listarVaraCivel", pattern = "/tributario/varacivel/listar/", viewId = "/faces/tributario/dividaativa/varacivel/lista.xhtml"),
        @URLMapping(id = "verVaraCivel", pattern = "/tributario/varacivel/ver/#{varaCivelControlador.id}/", viewId = "/faces/tributario/dividaativa/varacivel/visualizar.xhtml")
})
public class VaraCivelControlador extends PrettyControlador<VaraCivel> implements Serializable, CRUD {

    @EJB
    private VaraCivelFacade facade;
    @EJB
    private UFFacade ufFacade;
    protected ConverterGenerico converterUf;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public VaraCivelControlador() {
        super(VaraCivel.class);
    }

    @URLAction(mappingId = "novaVaraCivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verVaraCivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarVaraCivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/varacivel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ConverterGenerico getConverterUf() {
        if (converterUf == null) {
            converterUf = new ConverterGenerico(UF.class, ufFacade);
        }
        return converterUf;
    }

    public List<SelectItem> getEstados() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UF object : ufFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getUf()));
        }
        return toReturn;
    }
}
