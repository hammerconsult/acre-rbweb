package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoNegociacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoNegociacaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "tipoNegociacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoNegociacao", pattern = "/tributario/tiponegociacao/novo/", viewId = "/faces/tributario/itbi/tiponegociacao/edita.xhtml"),
    @URLMapping(id = "editarTipoNegociacao", pattern = "/tributario/tiponegociacao/editar/#{tipoNegociacaoControlador.id}/", viewId = "/faces/tributario/itbi/tiponegociacao/edita.xhtml"),
    @URLMapping(id = "listarTipoNegociacao", pattern = "/tributario/tiponegociacao/listar/", viewId = "/faces/tributario/itbi/tiponegociacao/lista.xhtml"),
    @URLMapping(id = "verTipoNegociacao", pattern = "/tributario/tiponegociacao/ver/#{tipoNegociacaoControlador.id}/", viewId = "/faces/tributario/itbi/tiponegociacao/visualizar.xhtml")
})
public class TipoNegociacaoControlador extends PrettyControlador<TipoNegociacao> implements Serializable, CRUD {

    @EJB
    private TipoNegociacaoFacade tipoNegociacaoFacade;

    public TipoNegociacaoControlador() {
        super(TipoNegociacao.class);
    }

    @URLAction(mappingId = "novoTipoNegociacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();

        }

 @URLAction(mappingId = "editarTipoNegociacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verTipoNegociacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public TipoNegociacaoFacade getFacade() {
        return tipoNegociacaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoNegociacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
            return "/tributario/tiponegociacao/";
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
