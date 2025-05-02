package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoStorage;
import br.com.webpublico.enums.Ambiente;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoStorageFacade;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-configuracao-storage", pattern = "/configuracao-storage/novo/", viewId = "/faces/admin/configuracaostorage/edita.xhtml"),
    @URLMapping(id = "editar-configuracao-storage", pattern = "/configuracao-storage/editar/#{configuracaoStorageControlador.id}/", viewId = "/faces/admin/configuracaostorage/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-storage", pattern = "/configuracao-storage/listar/", viewId = "/faces/admin/configuracaostorage/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-storage", pattern = "/configuracao-storage/ver/#{configuracaoStorageControlador.id}/", viewId = "/faces/admin/configuracaostorage/visualizar.xhtml")
})
public class ConfiguracaoStorageControlador extends PrettyControlador<ConfiguracaoStorage> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoStorageFacade facade;

    public ConfiguracaoStorageControlador() {
        super(ConfiguracaoStorage.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-storage/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "nova-configuracao-storage", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-configuracao-storage", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-configuracao-storage", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();

    }



    public List<SelectItem> getAmbientes(){
        return Util.getListSelectItem(Ambiente.values());
    }
}
