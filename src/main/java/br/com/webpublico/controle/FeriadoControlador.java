package br.com.webpublico.controle;

import br.com.webpublico.entidades.Feriado;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FeriadoFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "feriadoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoFeriado", pattern = "/cadastromunicipal/feriado/novo/",
        viewId = "/faces/tributario/cadastromunicipal/feriado/edita.xhtml"),

    @URLMapping(id = "editarFeriado", pattern = "/cadastromunicipal/feriado/editar/#{feriadoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/feriado/edita.xhtml"),

    @URLMapping(id = "listarFeriado", pattern = "/cadastromunicipal/feriado/listar/",
        viewId = "/faces/tributario/cadastromunicipal/feriado/lista.xhtml"),

    @URLMapping(id = "verFeriado", pattern = "/cadastromunicipal/feriado/ver/#{feriadoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/feriado/visualizar.xhtml")})
public class FeriadoControlador extends PrettyControlador<Feriado> implements Serializable, CRUD {

    @EJB
    private FeriadoFacade feriadoFacade;
    protected ConverterGenerico converterFeriado;

    public FeriadoControlador() {
        super(Feriado.class);
        metadata = new EntidadeMetaData(Feriado.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return feriadoFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public List<SelectItem> getFeriado() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Feriado object : feriadoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " : " + object.getDataFeriado()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterFeriado() {
        if (converterFeriado == null) {
            converterFeriado = new ConverterGenerico(Feriado.class, feriadoFacade);
        }
        return (ConverterGenerico) converterFeriado;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cadastromunicipal/feriado/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoFeriado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verFeriado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarFeriado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }
}
