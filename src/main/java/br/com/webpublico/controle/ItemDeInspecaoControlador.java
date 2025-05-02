package br.com.webpublico.controle;

import br.com.webpublico.entidades.administrativo.frotas.ItemInspecao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.administrativo.TipoInspecao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.administrativo.frotas.ItemInspecaoFacade;
import br.com.webpublico.util.FacesUtil;
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
 * Created by Desenvolvimento on 05/04/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoItemInspecao", pattern = "/item-de-inpecao/novo/", viewId = "/faces/administrativo/frota/item-de-inspecao/edita.xhtml"),
    @URLMapping(id = "editarItemInspecao", pattern = "/item-de-inpecao/editar/#{itemDeInspecaoControlador.id}/", viewId = "/faces/administrativo/frota/item-de-inspecao/edita.xhtml"),
    @URLMapping(id = "listarItemInspecao", pattern = "/item-de-inpecao/listar/", viewId = "/faces/administrativo/frota/item-de-inspecao/lista.xhtml"),
    @URLMapping(id = "verItemInspecao", pattern = "/item-de-inpecao/ver/#{itemDeInspecaoControlador.id}/", viewId = "/faces/administrativo/frota/item-de-inspecao/visualizar.xhtml")
})
public class ItemDeInspecaoControlador extends PrettyControlador<ItemInspecao> implements Serializable, CRUD {

    @EJB
    private ItemInspecaoFacade itemInspecaoFacade;

    public ItemDeInspecaoControlador() {
        super(ItemInspecao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return itemInspecaoFacade;
    }

    @URLAction(mappingId = "novoItemInspecao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verItemInspecao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarItemInspecao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/item-de-inpecao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getItensTipoInspecao() {
        return Util.getListSelectItem(TipoInspecao.values());
    }

    @Override
    public void redireciona() {
        if (Operacoes.NOVO.equals(operacao)) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "novo/");
        } else {
            super.redireciona();
        }
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }
}
