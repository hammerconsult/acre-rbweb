/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoDocumento;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoDocumentoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author peixe
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoDocumento", pattern = "/rh/tipo-de-documento/novo/", viewId = "/faces/rh/administracaodepagamento/tipodocumento/edita.xhtml"),
        @URLMapping(id = "editarTipoDocumento", pattern = "/rh/tipo-de-documento/editar/#{tipoDocumentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipodocumento/edita.xhtml"),
        @URLMapping(id = "listarTipoDocumento", pattern = "/rh/tipo-de-documento/listar/", viewId = "/faces/rh/administracaodepagamento/tipodocumento/lista.xhtml"),
        @URLMapping(id = "verTipoDocumento", pattern = "/rh/tipo-de-documento/ver/#{tipoDocumentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipodocumento/visualizar.xhtml")
})
public class TipoDocumentoControlador extends PrettyControlador<TipoDocumento> implements Serializable, CRUD {

    @EJB
    private TipoDocumentoFacade tipoDocumentoFacade;

    public TipoDocumentoControlador() {
        super(TipoDocumento.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoDocumentoFacade;
    }

    @URLAction(mappingId = "novoTipoDocumento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoDocumento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoDocumento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos())
            super.salvar();
    }

    public Boolean validaCampos() {
        if (tipoDocumentoFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção.", "O Código informado já está cadastrado em outro Tipo de Documento");
            return false;
        }
        return true;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/tipo-de-documento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
