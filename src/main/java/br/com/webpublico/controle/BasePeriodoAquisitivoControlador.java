/*
 * Codigo gerado automaticamente em Fri Oct 21 11:57:23 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.BasePeriodoAquisitivo;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BasePeriodoAquisitivoFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "basePeriodoAquisitivoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoBasePeriodoAquisitivo", pattern = "/base-periodo-aquisitivo/novo/", viewId = "/faces/rh/administracaodepagamento/baseperiodoaquisitivo/edita.xhtml"),
    @URLMapping(id = "editarBasePeriodoAquisitivo", pattern = "/base-periodo-aquisitivo/editar/#{basePeriodoAquisitivoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/baseperiodoaquisitivo/edita.xhtml"),
    @URLMapping(id = "listarBasePeriodoAquisitivo", pattern = "/base-periodo-aquisitivo/listar/", viewId = "/faces/rh/administracaodepagamento/baseperiodoaquisitivo/lista.xhtml"),
    @URLMapping(id = "verBasePeriodoAquisitivo", pattern = "/base-periodo-aquisitivo/ver/#{basePeriodoAquisitivoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/baseperiodoaquisitivo/visualizar.xhtml")
})
public class BasePeriodoAquisitivoControlador extends PrettyControlador<BasePeriodoAquisitivo> implements Serializable, CRUD {

    @EJB
    private BasePeriodoAquisitivoFacade basePeriodoAquisitivoFacade;

    public BasePeriodoAquisitivoControlador() {
        super(BasePeriodoAquisitivo.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return basePeriodoAquisitivoFacade;
    }

    public List<SelectItem> getTipoPeriodo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPeriodoAquisitivo object : TipoPeriodoAquisitivo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "verBasePeriodoAquisitivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver(); //To change body of generated methods, choose Tools | Templates.
    }

    @URLAction(mappingId = "editarBasePeriodoAquisitivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar(); //To change body of generated methods, choose Tools | Templates.
    }

    @URLAction(mappingId = "novoBasePeriodoAquisitivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCaminhoPadrao() {
        return "/base-periodo-aquisitivo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
