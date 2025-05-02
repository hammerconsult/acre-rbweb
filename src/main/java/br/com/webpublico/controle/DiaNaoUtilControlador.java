/*
 * Codigo gerado automaticamente em Thu Sep 15 16:00:55 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.DiaNaoUtil;
import br.com.webpublico.enums.TipoDiaNaoUtil;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DiaNaoUtilFacade;
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

@ManagedBean(name = "diaNaoUtilControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novodianaoutil", pattern = "/dia-nao-util/novo/", viewId = "/faces/rh/administracaodepagamento/diasnaouteis/edita.xhtml"),
        @URLMapping(id = "editardianaoutil", pattern = "/dia-nao-util/editar/#{diaNaoUtilControlador.id}/", viewId = "/faces/rh/administracaodepagamento/diasnaouteis/edita.xhtml"),
        @URLMapping(id = "verdianaoutil", pattern = "/dia-nao-util/ver/#{diaNaoUtilControlador.id}/", viewId = "/faces/rh/administracaodepagamento/diasnaouteis/visualizar.xhtml"),
        @URLMapping(id = "listardianaoutil", pattern = "/dia-nao-util/listar/", viewId = "/faces/rh/administracaodepagamento/diasnaouteis/lista.xhtml")
})
public class DiaNaoUtilControlador extends PrettyControlador<DiaNaoUtil> implements Serializable, CRUD {

    @EJB
    private DiaNaoUtilFacade diaNaoUtilFacade;

    public DiaNaoUtilControlador() {
        super(DiaNaoUtil.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return diaNaoUtilFacade;
    }

    @URLAction(mappingId = "novodianaoutil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verdianaoutil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editardianaoutil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getTipodiasDiaNaoUtils() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoDiaNaoUtil tp : TipoDiaNaoUtil.values()) {
            toReturn.add(new SelectItem(tp, tp.toString()));
        }
        return toReturn;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/dia-nao-util/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
