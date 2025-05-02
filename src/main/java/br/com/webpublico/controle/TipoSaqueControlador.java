/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoSaque;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoSaqueFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "tipoSaqueControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoSaque", pattern = "/rh/tipo-de-saque/novo/", viewId = "/faces/rh/administracaodepagamento/tiposaque/edita.xhtml"),
        @URLMapping(id = "listaTipoSaque", pattern = "/rh/tipo-de-saque/listar/", viewId = "/faces/rh/administracaodepagamento/tiposaque/lista.xhtml"),
        @URLMapping(id = "verTipoSaque", pattern = "/rh/tipo-de-saque/ver/#{tipoSaqueControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tiposaque/visualizar.xhtml"),
        @URLMapping(id = "editarTipoSaque", pattern = "/rh/tipo-de-saque/editar/#{tipoSaqueControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tiposaque/edita.xhtml"),
})
public class TipoSaqueControlador extends PrettyControlador<TipoSaque> implements Serializable, CRUD {

    @EJB
    private TipoSaqueFacade tipoSaqueFacade;

    public TipoSaqueControlador() {
        super(TipoSaque.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoSaqueFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/tipo-de-saque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (tipoSaqueFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção !", "O Código informado já está cadastrado em outro Tipo de Saque !");
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoTipoSaque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoSaque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoSaque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }
}
