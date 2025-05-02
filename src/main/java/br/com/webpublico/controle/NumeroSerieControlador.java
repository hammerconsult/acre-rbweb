/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.NumeroSerie;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.NumeroSerieFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author gustavo
 */
@ManagedBean(name = "numeroSerieControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoNumeroSerie", pattern = "/numero-de-serie/novo/", viewId = "/faces/tributario/cadastromunicipal/numeroserie/edita.xhtml"),
        @URLMapping(id = "editarNumeroSerie", pattern = "/numero-de-serie/editar/#{numeroSerieControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/numeroserie/edita.xhtml"),
        @URLMapping(id = "listarNumeroSerie", pattern = "/numero-de-serie/listar/", viewId = "/faces/tributario/cadastromunicipal/numeroserie/lista.xhtml"),
        @URLMapping(id = "verNumeroSerie", pattern = "/numero-de-serie/ver/#{numeroSerieControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/numeroserie/visualizar.xhtml")
})
public class NumeroSerieControlador extends PrettyControlador<NumeroSerie> implements Serializable, CRUD {

    @EJB
    private NumeroSerieFacade facade;
    private List<NumeroSerie> lista;

    public NumeroSerieControlador() {
        super(NumeroSerie.class);
    }

    public List<NumeroSerie> getLista() {
        if (lista == null) {
            lista = facade.lista();
        }
        return lista;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/numero-de-serie/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    @URLAction(mappingId = "novoNumeroSerie", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarNumeroSerie", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verNumeroSerie", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }


    @Override
    public void salvar() {
        if(validaCampos()){
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (facade.serieJaExite(selecionado)) {
            FacesUtil.addError("Atenção!", "A série já existe!");
            retorno =  false;
        }
        if (facade.descricaoJaExite(selecionado)) {
            FacesUtil.addError("Atenção!", "A descrição já existe!");
            retorno =  false;
        }
        return retorno;
    }
}
