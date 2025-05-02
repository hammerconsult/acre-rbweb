package br.com.webpublico.controle;

import br.com.webpublico.entidades.Risco;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RiscoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by carlos on 17/06/15.
 */
@ManagedBean(name = "riscoControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarRisco", pattern = "/risco/listar/", viewId = "/faces/rh/administracaodepagamento/risco/lista.xhtml"),
                @URLMapping(id = "criarRisco", pattern = "/risco/novo/", viewId = "/faces/rh/administracaodepagamento/risco/edita.xhtml"),
                @URLMapping(id = "editarRisco", pattern = "/risco/editar/#{riscoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/risco/edita.xhtml"),
                @URLMapping(id = "verRisco", pattern = "/risco/ver/#{riscoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/risco/visualizar.xhtml")
        }
)
public class RiscoControlador extends PrettyControlador<Risco> implements CRUD {

    @EJB
    private RiscoFacade riscoFacade;

    @Override
    public String getCaminhoPadrao() {
        return "/risco/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return riscoFacade;
    }

    @Override
    @URLAction(mappingId = "criarRisco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verRisco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarRisco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public RiscoControlador() {
        super(Risco.class);
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public boolean validaCampos() {
        boolean valida = true;

        if (selecionado.getCodigo() == null) {
            FacesUtil.addCampoObrigatorio("O Código é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("A Descrição é um campo obrigatório.");
            valida = false;
        }

        return valida;
    }
}
