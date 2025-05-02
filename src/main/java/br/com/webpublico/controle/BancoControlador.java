/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.enums.Situacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BancoFacade;
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

@ManagedBean(name = "bancoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoBanco", pattern = "/banco/novo/", viewId = "/faces/tributario/cadastromunicipal/banco/edita.xhtml"),
        @URLMapping(id = "editarBanco", pattern = "/banco/editar/#{bancoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/banco/edita.xhtml"),
        @URLMapping(id = "listarBanco", pattern = "/banco/listar/", viewId = "/faces/tributario/cadastromunicipal/banco/lista.xhtml"),
        @URLMapping(id = "verBanco", pattern = "/banco/ver/#{bancoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/banco/visualizar.xhtml")
})
public class BancoControlador extends PrettyControlador<Banco> implements Serializable, CRUD {

    @EJB
    private BancoFacade facade;

    public BancoControlador() {
        super(Banco.class);
    }

    public List<SelectItem> getSituacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Situacao object : Situacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoBanco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verBanco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarBanco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/banco/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
