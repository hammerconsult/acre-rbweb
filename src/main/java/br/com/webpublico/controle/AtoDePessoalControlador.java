/*
 * Codigo gerado automaticamente em Thu Oct 06 09:05:34 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtoDePessoal;
import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtoDePessoalFacade;
import br.com.webpublico.negocios.AtoLegalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "atoDePessoalControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoAtoPessoal", pattern = "/rh/ato-de-pessoal/novo/", viewId = "/faces/rh/administracaodepagamento/atodepessoal/edita.xhtml"),
        @URLMapping(id = "listaAtoPessoal", pattern = "/rh/ato-de-pessoal/listar/", viewId = "/faces/rh/administracaodepagamento/atodepessoal/lista.xhtml"),
        @URLMapping(id = "verAtoPessoal", pattern = "/rh/ato-de-pessoal/ver/#{atoDePessoalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/atodepessoal/visualizar.xhtml"),
        @URLMapping(id = "editarAtoPessoal", pattern = "/rh/ato-de-pessoal/editar/#{atoDePessoalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/atodepessoal/edita.xhtml"),
})
public class AtoDePessoalControlador extends PrettyControlador<AtoDePessoal> implements Serializable, CRUD {

    @EJB
    private AtoDePessoalFacade atoDePessoalFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;

    public AtoDePessoalControlador() {
        super(AtoDePessoal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return atoDePessoalFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/ato-de-pessoal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getAtoLegal() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (AtoLegal object : atoLegalFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte, PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    @Override
    @URLAction(mappingId = "novoAtoPessoal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }


    @Override
    @URLAction(mappingId = "editarAtoPessoal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verAtoPessoal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}
