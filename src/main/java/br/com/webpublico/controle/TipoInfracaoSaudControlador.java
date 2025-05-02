package br.com.webpublico.controle;

import br.com.webpublico.entidades.InfracaoSaud;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.TipoInfracaoSaud;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.MotoristaSaudFacade;
import br.com.webpublico.negocios.TipoInfracaoSaudFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarTipoInfracaoSaud",
        pattern = "/tributario/saud/tipo-infracao-saud/listar/",
        viewId = "/faces/tributario/saud/tipoinfracaosaud/lista.xhtml"),
    @URLMapping(id = "novoTipoInfracaoSaud",
        pattern = "/tributario/saud/tipo-infracao-saud/novo/",
        viewId = "/faces/tributario/saud/tipoinfracaosaud/edita.xhtml"),
    @URLMapping(id = "editarTipoInfracaoSaud",
        pattern = "/tributario/saud/tipo-infracao-saud/editar/#{tipoInfracaoSaudControlador.id}/",
        viewId = "/faces/tributario/saud/tipoinfracaosaud/edita.xhtml"),
    @URLMapping(id = "verTipoInfracaoSaud",
        pattern = "/tributario/saud/tipo-infracao-saud/ver/#{tipoInfracaoSaudControlador.id}/",
        viewId = "/faces/tributario/saud/tipoinfracaosaud/visualizar.xhtml")
})
public class TipoInfracaoSaudControlador extends PrettyControlador<TipoInfracaoSaud> implements Serializable, CRUD {

    @EJB
    private TipoInfracaoSaudFacade facade;

    public TipoInfracaoSaudControlador() {
        super(TipoInfracaoSaud.class);
    }

    @Override
    public TipoInfracaoSaudFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/saud/tipo-infracao-saud/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoTipoInfracaoSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarTipoInfracaoSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verTipoInfracaoSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<TipoInfracaoSaud> completaTipoInfracao(String parte) {
        return facade.listaFiltrando(parte.trim(), "descricao");
    }


}
