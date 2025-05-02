package br.com.webpublico.controle.comum;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ConfiguracaoGovBr;
import br.com.webpublico.enums.Ambiente;
import br.com.webpublico.enums.Sistema;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.comum.ConfiguracaoGovBrFacade;
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


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoConfiguracaoGovBr", pattern = "/configuracao-gov-br/novo/",
        viewId = "/faces/comum/configuracao-gov-br/edita.xhtml"),
    @URLMapping(id = "editarConfiguracaoGovBr", pattern = "/configuracao-gov-br/editar/#{configuracaoGovBrControlador.id}/",
        viewId = "/faces/comum/configuracao-gov-br/edita.xhtml"),
    @URLMapping(id = "verConfiguracaoGovBr", pattern = "/configuracao-gov-br/ver/#{configuracaoGovBrControlador.id}/",
        viewId = "/faces/comum/configuracao-gov-br/visualizar.xhtml"),
    @URLMapping(id = "listarConfiguracaoGovBr", pattern = "/configuracao-gov-br/listar/",
        viewId = "/faces/comum/configuracao-gov-br/lista.xhtml")
})

public class ConfiguracaoGovBrControlador extends PrettyControlador<ConfiguracaoGovBr> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoGovBrFacade facade;

    public ConfiguracaoGovBrControlador() {
        super(ConfiguracaoGovBr.class);
    }

    @Override
    public ConfiguracaoGovBrFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-gov-br/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoConfiguracaoGovBr", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarConfiguracaoGovBr", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verConfiguracaoGovBr", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getSistemas() {
        return Util.getListSelectItem(Sistema.values());
    }

    public List<SelectItem> getAmbientes() {
        return Util.getListSelectItem(Ambiente.values());
    }

}
