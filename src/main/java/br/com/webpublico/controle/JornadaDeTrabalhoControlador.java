/*
 * Codigo gerado automaticamente em Fri Aug 05 12:00:12 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.JornadaDeTrabalho;
import br.com.webpublico.enums.rh.esocial.TipoJornadaTrabalho;
import br.com.webpublico.enums.rh.esocial.TipoTempoParcialJornadaTrabalho;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.JornadaDeTrabalhoFacade;
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

@ManagedBean(name = "jornadaDeTrabalhoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novojornadadetrabalho", pattern = "/jornadadetrabalho/novo/", viewId = "/faces/rh/administracaodepagamento/jornadadetrabalho/edita.xhtml"),
    @URLMapping(id = "editarjornadadetrabalho", pattern = "/jornadadetrabalho/editar/#{jornadaDeTrabalhoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/jornadadetrabalho/edita.xhtml"),
    @URLMapping(id = "verjornadadetrabalho", pattern = "/jornadadetrabalho/ver/#{jornadaDeTrabalhoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/jornadadetrabalho/visualizar.xhtml"),
    @URLMapping(id = "listarjornadadetrabalho", pattern = "/jornadadetrabalho/listar/", viewId = "/faces/rh/administracaodepagamento/jornadadetrabalho/lista.xhtml")
})
public class JornadaDeTrabalhoControlador extends PrettyControlador<JornadaDeTrabalho> implements Serializable, CRUD {

    @EJB
    private JornadaDeTrabalhoFacade jornadaDeTrabalhoFacade;

    public JornadaDeTrabalhoControlador() {
        super(JornadaDeTrabalho.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return jornadaDeTrabalhoFacade;
    }


    @URLAction(mappingId = "novojornadadetrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verjornadadetrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarjornadadetrabalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/jornadadetrabalho/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    public List<SelectItem> getTipoTempoParcialJornadaTrabalho(){
        return Util.getListSelectItem(TipoTempoParcialJornadaTrabalho.values());
    }

    public List<SelectItem> getTipoJornadaTrabalho(){
        return Util.getListSelectItem(TipoJornadaTrabalho.values());
    }
}
