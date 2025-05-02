package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoVeiculo;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoVeiculoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 08/09/14
 * Time: 08:37
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "tipoVeiculoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "tipoVeiculoNovo", pattern = "/rbtrans/tipo-veiculo/novo/", viewId = "/faces/tributario/rbtrans/tipoveiculo/edita.xhtml"),
        @URLMapping(id = "tipoVeiculoListar", pattern = "/rbtrans/tipo-veiculo/listar/", viewId = "/faces/tributario/rbtrans/tipoveiculo/lista.xhtml"),
        @URLMapping(id = "tipoVeiculoEditar", pattern = "/rbtrans/tipo-veiculo/editar/#{tipoVeiculoControlador.id}/", viewId = "/faces/tributario/rbtrans/tipoveiculo/edita.xhtml"),
        @URLMapping(id = "tipoVeiculoVer", pattern = "/rbtrans/tipo-veiculo/ver/#{tipoVeiculoControlador.id}/", viewId = "/faces/tributario/rbtrans/tipoveiculo/visualizar.xhtml"),
})
public class TipoVeiculoControlador  extends PrettyControlador<TipoVeiculo> implements Serializable, CRUD {
    @EJB
    private TipoVeiculoFacade tipoVeiculoFacade;

    public TipoVeiculoControlador() {
        super(TipoVeiculo.class);
    }

    public TipoVeiculoFacade getTipoVeiculoFacade() {
        return tipoVeiculoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoVeiculoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rbtrans/tipo-veiculo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "tipoVeiculoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "tipoVeiculoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "tipoVeiculoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<TipoVeiculo> completaTipoVeiculo(String parte) {
        return tipoVeiculoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<TipoVeiculo> tiposVeiculo() {
        return tipoVeiculoFacade.listaDecrescente();
    }
}
