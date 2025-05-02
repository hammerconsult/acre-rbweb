package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoAtributo;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoAtributoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoGrupoAtributo", pattern = "/grupo-atributo/novo/", viewId = "/faces/tributario/cadastromunicipal/grupoatributo/edita.xhtml"),
    @URLMapping(id = "editarGrupoAtributo", pattern = "/grupo-atributo/editar/#{grupoAtributoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/grupoatributo/edita.xhtml"),
    @URLMapping(id = "listarGrupoAtributo", pattern = "/grupo-atributo/listar/", viewId = "/faces/tributario/cadastromunicipal/grupoatributo/lista.xhtml"),
    @URLMapping(id = "verGrupoAtributo", pattern = "/grupo-atributo/ver/#{grupoAtributoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/grupoatributo/visualizar.xhtml")
})
public class GrupoAtributoControlador extends PrettyControlador<GrupoAtributo> implements Serializable, CRUD {

    @EJB
    private GrupoAtributoFacade facade;

    public GrupoAtributoControlador() {
        super(GrupoAtributo.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grupo-atributo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoGrupoAtributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarGrupoAtributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verGrupoAtributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public List<GrupoAtributo> completarEstaEntidade(String parte) {
        return facade.buscarGrupoAtributoAtivoPorCodigoOrDescricao(parte);
    }
}
