/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle.tributario;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.UnidadeMedidaAmbiental;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.tributario.UnidadeMedidaAmbientalFacade;
import com.google.common.collect.Lists;
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
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarUnidadeMedidaLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/unidade-medida/listar/",
        viewId = "/faces/tributario/licenciamentoambiental/unidademedida/lista.xhtml"),
    @URLMapping(id = "editarUnidadeMedidaLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/unidade-medida/editar/#{unidadeMedidaAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/unidademedida/edita.xhtml"),
    @URLMapping(id = "verUnidadeMedidaLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/unidade-medida/ver/#{unidadeMedidaAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/unidademedida/visualizar.xhtml"),
    @URLMapping(id = "novoUnidadeMedidaLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/unidade-medida/novo/",
        viewId = "/faces/tributario/licenciamentoambiental/unidademedida/edita.xhtml")
})
public class UnidadeMedidaAmbientalControlador extends PrettyControlador<UnidadeMedidaAmbiental> implements Serializable, CRUD {

    @EJB
    private UnidadeMedidaAmbientalFacade facade;

    public UnidadeMedidaAmbientalControlador() {
        super(UnidadeMedidaAmbiental.class);
    }

    @Override
    public UnidadeMedidaAmbientalFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/licenciamento-ambiental/unidade-medida/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoUnidadeMedidaLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarUnidadeMedidaLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verUnidadeMedidaLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getTipoUnidade() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (UnidadeMedidaAmbiental.TipoUnidade tipo : UnidadeMedidaAmbiental.TipoUnidade.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }
}
