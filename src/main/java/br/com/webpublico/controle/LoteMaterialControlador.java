/*
 * Codigo gerado automaticamente em Tue May 24 13:43:55 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.LoteMaterial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LoteMaterialFacade;
import br.com.webpublico.negocios.MaterialFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "loteMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoLoteMaterial", pattern = "/lote-de-material/novo/", viewId = "/faces/administrativo/materiais/lotematerial/edita.xhtml"),
        @URLMapping(id = "editarLoteMaterial", pattern = "/lote-de-material/editar/#{loteMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/lotematerial/edita.xhtml"),
        @URLMapping(id = "listarLoteMaterial", pattern = "/lote-de-material/listar/", viewId = "/faces/administrativo/materiais/lotematerial/lista.xhtml"),
        @URLMapping(id = "verLoteMaterial", pattern = "/lote-de-material/ver/#{loteMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/lotematerial/visualizar.xhtml")
})
public class LoteMaterialControlador extends PrettyControlador<LoteMaterial> implements Serializable, CRUD {

    @EJB
    private LoteMaterialFacade loteMaterialFacade;
    @EJB
    private MaterialFacade materialFacade;

    public LoteMaterialControlador() {
        metadata = new EntidadeMetaData(LoteMaterial.class);
    }

    public LoteMaterialFacade getFacade() {
        return loteMaterialFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return loteMaterialFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/lote-de-material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoLoteMaterial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarLoteMaterial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verLoteMaterial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<LoteMaterial> completaLoteMaterial(String filtro){
        return loteMaterialFacade.listaFiltrando(filtro.trim(), "identificacao");
    }
}
