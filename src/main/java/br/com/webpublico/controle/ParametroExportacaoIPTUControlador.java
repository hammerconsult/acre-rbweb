/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.ParametroExportacaoIPTU;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroExportacaoIPTUFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "parametroExportacaoIPTUControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoParametroExportacaoIPTU", pattern = "/tributario/parametro-exportacao-iptu/novo/",
                viewId = "/faces/tributario/iptu/exportacao/parametro/edita.xhtml"),
        @URLMapping(id = "editarParametroExportacaoIPTU", pattern = "/tributario/parametro-exportacao-iptu/editar/#{parametroExportacaoIPTUControlador.id}/",
                viewId = "/faces/tributario/iptu/exportacao/parametro/edita.xhtml"),
        @URLMapping(id = "listarParametroExportacaoIPTU", pattern = "/tributario/parametro-exportacao-iptu/listar/",
                viewId = "/faces/tributario/iptu/exportacao/parametro/lista.xhtml"),
        @URLMapping(id = "verParametroExportacaoIPTU", pattern = "/tributario/parametro-exportacao-iptu/ver/#{parametroExportacaoIPTUControlador.id}/",
                viewId = "/faces/tributario/iptu/exportacao/parametro/visualizar.xhtml")})
public class ParametroExportacaoIPTUControlador extends PrettyControlador<ParametroExportacaoIPTU> implements Serializable, CRUD {

    @EJB
    private ParametroExportacaoIPTUFacade parametroExportacaoIPTUFacade;

    public ParametroExportacaoIPTUControlador() {
        super(ParametroExportacaoIPTU.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroExportacaoIPTUFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/parametro-exportacao-iptu/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoParametroExportacaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verParametroExportacaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroExportacaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    public List<SelectItem> getTiposParametro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ParametroExportacaoIPTU.TipoParametroExportacaoIPTU object : ParametroExportacaoIPTU.TipoParametroExportacaoIPTU.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getMoedasConvenio() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ParametroExportacaoIPTU.MoedaConvenio object : ParametroExportacaoIPTU.MoedaConvenio.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getFormatosData() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ParametroExportacaoIPTU.FormatoData object : ParametroExportacaoIPTU.FormatoData.values()) {
            toReturn.add(new SelectItem(object, object.name()));
        }
        return toReturn;
    }
}
