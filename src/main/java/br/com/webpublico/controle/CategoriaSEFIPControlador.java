/*
 * Codigo gerado automaticamente em Tue Jan 03 15:36:34 BRST 2012
 * Gerador de Controlador
*/

package br.com.webpublico.controle;


import br.com.webpublico.entidades.CategoriaSEFIP;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CategoriaSEFIPFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "categoriaSEFIPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCategoriaSEFIP", pattern = "/rh/categoria-sefip/novo/", viewId = "/faces/rh/administracaodepagamento/categoriasefip/edita.xhtml"),
    @URLMapping(id = "listaCategoriaSEFIP", pattern = "/rh/categoria-sefip/listar/", viewId = "/faces/rh/administracaodepagamento/categoriasefip/lista.xhtml"),
    @URLMapping(id = "verCategoriaSEFIP", pattern = "/rh/categoria-sefip/ver/#{categoriaSEFIPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/categoriasefip/visualizar.xhtml"),
    @URLMapping(id = "editarCategoriaSEFIP", pattern = "/rh/categoria-sefip/editar/#{categoriaSEFIPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/categoriasefip/edita.xhtml"),
})
public class CategoriaSEFIPControlador extends PrettyControlador<CategoriaSEFIP> implements Serializable, CRUD {

    @EJB
    private CategoriaSEFIPFacade categoriaSEFIPFacade;

    public CategoriaSEFIPControlador() {
        super(CategoriaSEFIP.class);
    }

    public CategoriaSEFIPFacade getFacade() {
        return categoriaSEFIPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return categoriaSEFIPFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/categoria-sefip/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoCategoriaSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verCategoriaSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarCategoriaSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        super.salvar();
    }
}
