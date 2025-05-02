package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoComissaoExtraCurricular;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoComissaoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by mga on 01/06/2017.
 */
@ManagedBean(name = "tipoComissaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoComissao", pattern = "/tipo-comissao/novo/", viewId = "/faces/rh/concursos/tipocomissao/edita.xhtml"),
    @URLMapping(id = "editarTipoComissao", pattern = "/tipo-comissao/editar/#{tipoComissaoControlador.id}/", viewId = "/faces/rh/concursos/tipocomissao/edita.xhtml"),
    @URLMapping(id = "verTipoComissao", pattern = "/tipo-comissao/ver/#{tipoComissaoControlador.id}/", viewId = "/faces/rh/concursos/tipocomissao/visualizar.xhtml"),
    @URLMapping(id = "listarTipoComissao", pattern = "/tipo-comissao/listar/", viewId = "/faces/rh/concursos/tipocomissao/lista.xhtml")
})
public class TipoComissaoControlador extends PrettyControlador<TipoComissaoExtraCurricular> implements Serializable, CRUD {

    @EJB
    private TipoComissaoFacade facade;

    public TipoComissaoControlador() {
        super(TipoComissaoExtraCurricular.class);
    }

    @URLAction(mappingId = "novoTipoComissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoComissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoComissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-comissao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }
}
