/*
 * Codigo gerado automaticamente em Fri Mar 04 09:38:44 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Documento;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DocumentoFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "documentoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoDocumento", pattern = "/documento/novo/", viewId = "/faces/tributario/cadastromunicipal/documento/edita.xhtml"),
        @URLMapping(id = "editarDocumento", pattern = "/documento/editar/#{documentoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/documento/edita.xhtml"),
        @URLMapping(id = "listarDocumento", pattern = "/documento/listar/", viewId = "/faces/tributario/cadastromunicipal/documento/lista.xhtml"),
        @URLMapping(id = "verDocumento", pattern = "/documento/ver/#{documentoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/documento/visualizar.xhtml")
})
public class DocumentoControlador extends PrettyControlador<Documento> implements Serializable, CRUD {

    @EJB
    private DocumentoFacade documentoFacade;

    @Override
    public AbstractFacade getFacede() {
        return documentoFacade;
    }

    public DocumentoControlador() {
        super(Documento.class);
    }

    @URLAction(mappingId = "novoDocumento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verDocumento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarDocumento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/documento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
