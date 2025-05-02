package br.com.webpublico.controle.administrativo.frotas;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.administrativo.frotas.ItemInspecaoEquipamento;
import br.com.webpublico.enums.administrativo.TipoInspecaoEquipamento;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.administrativo.frotas.ItemInspecaoEquipamentoFacade;
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

/**
 * Created by carlos on 24/05/17.
 */

@ManagedBean(name = "itemInspecaoEquipamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoItemInspecaoEquipamento", pattern = "/item-de-inpecao-equipamento/novo/", viewId = "/faces/administrativo/frota/item-de-inspecao-de-equipamento/edita.xhtml"),
    @URLMapping(id = "editarItemInspecaoEquipamento", pattern = "/item-de-inpecao-equipamento/editar/#{itemInspecaoEquipamentoControlador.id}/", viewId = "/faces/administrativo/frota/item-de-inspecao-de-equipamento/edita.xhtml"),
    @URLMapping(id = "listarItemInspecaoEquipamento", pattern = "/item-de-inpecao-equipamento/listar/", viewId = "/faces/administrativo/frota/item-de-inspecao-de-equipamento/lista.xhtml"),
    @URLMapping(id = "verItemInspecaoEquipamento", pattern = "/item-de-inpecao-equipamento/ver/#{itemInspecaoEquipamentoControlador.id}/", viewId = "/faces/administrativo/frota/item-de-inspecao-de-equipamento/visualizar.xhtml")
})
public class ItemInspecaoEquipamentoControlador extends PrettyControlador<ItemInspecaoEquipamento> implements Serializable, CRUD {

    @EJB
    private ItemInspecaoEquipamentoFacade itemInspecaoEquipamentoFacade;

    @Override
    public AbstractFacade getFacede() {
        return getItemInspecaoEquipamentoFacade();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/item-de-inpecao-equipamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ItemInspecaoEquipamentoFacade getItemInspecaoEquipamentoFacade() {
        return itemInspecaoEquipamentoFacade;
    }

    public ItemInspecaoEquipamentoControlador() {
        super(ItemInspecaoEquipamento.class);
    }

    @URLAction(mappingId = "novoItemInspecaoEquipamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verItemInspecaoEquipamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarItemInspecaoEquipamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getItensTipoInspecaoEquipamento() {
        return Util.getListSelectItem(TipoInspecaoEquipamento.values());
    }
}
