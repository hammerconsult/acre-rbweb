package br.com.webpublico.controle.rh.esocial;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.esocial.CategoriaTrabalhador;
import br.com.webpublico.enums.rh.esocial.TipoGrupoCategoriaTrabalhador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.esocial.CategoriaTrabalhadorFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by William on 05/09/2018.
 */
@ManagedBean(name = "categoriaTrabalhadorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-categoria-trabalhador", pattern = "/e-social/categoria-trabalhador/novo/", viewId = "/faces/rh/esocial/categoria-trabalhador/edita.xhtml"),
    @URLMapping(id = "editar-categoria-trabalhador", pattern = "/e-social/categoria-trabalhador/editar/#{categoriaTrabalhadorControlador.id}/", viewId = "/faces/rh/esocial/categoria-trabalhador/edita.xhtml"),
    @URLMapping(id = "ver-categoria-trabalhador", pattern = "/e-social/categoria-trabalhador/ver/#{categoriaTrabalhadorControlador.id}/", viewId = "/faces/rh/esocial/categoria-trabalhador/visualizar.xhtml"),
    @URLMapping(id = "listar-categoria-trabalhador", pattern = "/e-social/categoria-trabalhador/listar/", viewId = "/faces/rh/esocial/categoria-trabalhador/lista.xhtml")
})
public class CategoriaTrabalhadorControlador extends PrettyControlador<CategoriaTrabalhador> implements CRUD {


    @EJB
    private CategoriaTrabalhadorFacade categoriaTrabalhadorFacade;

    public CategoriaTrabalhadorControlador() {
        super(CategoriaTrabalhador.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return categoriaTrabalhadorFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/e-social/categoria-trabalhador/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "nova-categoria-trabalhador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editar-categoria-trabalhador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-categoria-trabalhador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getGrupo() {
        return Util.getListSelectItem(TipoGrupoCategoriaTrabalhador.values());
    }
}
