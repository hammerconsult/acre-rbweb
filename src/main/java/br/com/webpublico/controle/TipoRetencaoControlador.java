/*
 * Codigo gerado automaticamente em Mon Jul 02 14:00:30 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoRetencao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoRetencaoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoRetencao", pattern = "/tipo-de-retencao/novo/", viewId = "/faces/financeiro/clp/tiporetencao/edita.xhtml"),
    @URLMapping(id = "listaTipoRetencao", pattern = "/tipo-de-retencao/listar/", viewId = "/faces/financeiro/clp/tiporetencao/lista.xhtml"),
    @URLMapping(id = "verTipoRetencao", pattern = "/tipo-de-retencao/ver/#{tipoRetencaoControlador.id}/", viewId = "/faces/financeiro/clp/tiporetencao/visualizar.xhtml"),
    @URLMapping(id = "editarTipoRetencao", pattern = "/tipo-de-retencao/editar/#{tipoRetencaoControlador.id}/", viewId = "/faces/financeiro/clp/tiporetencao/edita.xhtml"),
})
public class TipoRetencaoControlador extends PrettyControlador<TipoRetencao> implements Serializable, CRUD {

    @EJB
    private TipoRetencaoFacade tipoRetencaoFacade;

    public TipoRetencaoControlador() {
        super(TipoRetencao.class);
    }

    public TipoRetencaoFacade getFacade() {
        return tipoRetencaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoRetencaoFacade;
    }

    @URLAction(mappingId = "novoTipoRetencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoRetencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoRetencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-retencao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
