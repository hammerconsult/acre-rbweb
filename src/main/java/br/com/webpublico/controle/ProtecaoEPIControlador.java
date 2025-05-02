package br.com.webpublico.controle;

import br.com.webpublico.entidades.ProtecaoEPI;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProtecaoEPIFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by carlos on 19/06/15.
 */
@ManagedBean(name = "protecaoEPIControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarProtecaoEPI", pattern = "/protecao-epi/listar/", viewId = "/faces/rh/administracaodepagamento/protecaoepi/lista.xhtml"),
                @URLMapping(id = "criarProtecaoEPI", pattern = "/protecao-epi/novo/", viewId = "/faces/rh/administracaodepagamento/protecaoepi/edita.xhtml"),
                @URLMapping(id = "editarProtecaoEPI", pattern = "/protecao-epi/editar/#{protecaoEPIControlador.id}/", viewId = "/faces/rh/administracaodepagamento/protecaoepi/edita.xhtml"),
                @URLMapping(id = "verProtecaoEPI", pattern = "/protecao-epi/ver/#{protecaoEPIControlador.id}/", viewId = "/faces/rh/administracaodepagamento/protecaoepi/visualizar.xhtml")
        }
)
public class ProtecaoEPIControlador extends PrettyControlador<ProtecaoEPI> implements CRUD {

    @EJB
    private ProtecaoEPIFacade protecaoEPIFacade;

    public ProtecaoEPIControlador() {
        super(ProtecaoEPI.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/protecao-epi/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return protecaoEPIFacade;
    }

    @Override
    @URLAction(mappingId = "criarProtecaoEPI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verProtecaoEPI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarProtecaoEPI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        boolean valida = true;

        if (selecionado.getNome() == null || selecionado.getNome().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("É necessário informar a Proteção.");
            valida = false;
        }

        return valida;
    }
}
