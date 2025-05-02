package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoVinculoBem;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoVinculoBemFacade;
import br.com.webpublico.util.FacesUtil;
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
 * User: Desenvolvimento
 * Date: 08/06/15
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoVinculo", pattern = "/tipo-vinculo-bem/novo/", viewId = "/faces/administrativo/patrimonio/tipovinculobem/edita.xhtml"),
        @URLMapping(id = "editarTipoVinculo", pattern = "/tipo-vinculo-bem/editar/#{tipoVinculoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/tipovinculobem/edita.xhtml"),
        @URLMapping(id = "verTipoVinculo", pattern = "/tipo-vinculo-bem/ver/#{tipoVinculoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/tipovinculobem/visualizar.xhtml"),
        @URLMapping(id = "listarTipoVinculo", pattern = "/tipo-vinculo-bem/listar/", viewId = "/faces/administrativo/patrimonio/tipovinculobem/lista.xhtml")
})
public class TipoVinculoBemControlador extends PrettyControlador<TipoVinculoBem> implements Serializable, CRUD {

    @EJB
    private TipoVinculoBemFacade tipoVinculoBemFacade;

    public TipoVinculoBemControlador() {
        super(TipoVinculoBem.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoVinculoBemFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-vinculo-bem/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoTipoVinculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verTipoVinculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarTipoVinculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        if (selecionado.getId() == null && selecionado.getDescricao() != null && !selecionado.getDescricao().isEmpty()) {
            TipoVinculoBem tipoVinculoBem = tipoVinculoBemFacade.recuperarPorDescricao(selecionado.getDescricao());
            if (tipoVinculoBem != null) {
                FacesUtil.addOperacaoNaoPermitida("já existe um tipo de vínculo com essa descrição!");
                return false;
            }
        }
        return true;
    }

    public List<TipoVinculoBem> completaTipoVinculoBem(String filtro) {
        return tipoVinculoBemFacade.listaFiltrando(filtro.trim(), "descricao");
    }
}
