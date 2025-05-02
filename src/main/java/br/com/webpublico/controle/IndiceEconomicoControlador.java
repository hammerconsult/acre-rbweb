/*
 * Codigo gerado automaticamente em Fri Apr 08 10:50:19 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.IndiceEconomico;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.IndiceEconomicoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "indiceEconomicoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoIndiceEconomico", pattern = "/tributario/indice-economico/novo/",
        viewId = "/faces/tributario/cadastromunicipal/indiceeconomico/edita.xhtml"),

    @URLMapping(id = "editarIndiceEconomico", pattern = "/tributario/indice-economico/editar/#{indiceEconomicoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/indiceeconomico/edita.xhtml"),

    @URLMapping(id = "listarIndiceEconomico", pattern = "/tributario/indice-economico/listar/",
        viewId = "/faces/tributario/cadastromunicipal/indiceeconomico/lista.xhtml"),

    @URLMapping(id = "verIndiceEconomico", pattern = "/tributario/indice-economico/ver/#{indiceEconomicoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/indiceeconomico/visualizar.xhtml")})

public class IndiceEconomicoControlador extends PrettyControlador<IndiceEconomico> implements Serializable, CRUD {

    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    private ConverterAutoComplete converterIndice;

    public IndiceEconomicoControlador() {
        super(IndiceEconomico.class);
    }

    public IndiceEconomicoFacade getFacade() {
        return indiceEconomicoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return indiceEconomicoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/indice-economico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoIndiceEconomico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verIndiceEconomico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarIndiceEconomico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    public Converter getConverterIndiceEconomico() {
        if (converterIndice == null) {
            converterIndice = new ConverterAutoComplete(IndiceEconomico.class, indiceEconomicoFacade);
        }
        return converterIndice;
    }

    public List<SelectItem> listarIndicesEconomicos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (IndiceEconomico object : indiceEconomicoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }
}
