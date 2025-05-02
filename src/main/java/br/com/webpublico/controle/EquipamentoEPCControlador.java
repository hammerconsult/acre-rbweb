package br.com.webpublico.controle;

import br.com.webpublico.entidades.EquipamentoEPC;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EquipamentoEPCFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by carlos on 04/09/15.
 */
@ManagedBean(name = "equipamentoEPCControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarEquipamentoEPC", pattern = "/equipamento-epc/listar/", viewId = "/faces/rh/administracaodepagamento/equipamentoepc/lista.xhtml"),
                @URLMapping(id = "criarEquipamentoEPC", pattern = "/equipamento-epc/novo/", viewId = "/faces/rh/administracaodepagamento/equipamentoepc/edita.xhtml"),
                @URLMapping(id = "editarEquipamentoEPC", pattern = "/equipamento-epc/editar/#{equipamentoEPCControlador.id}/", viewId = "/faces/rh/administracaodepagamento/equipamentoepc/edita.xhtml"),
                @URLMapping(id = "verEquipamentoEPC", pattern = "/equipamento-epc/ver/#{equipamentoEPCControlador.id}/", viewId = "/faces/rh/administracaodepagamento/equipamentoepc/visualizar.xhtml")
        }
)
public class EquipamentoEPCControlador extends PrettyControlador<EquipamentoEPC> implements CRUD {

    @EJB
    private EquipamentoEPCFacade equipamentoEPCFacade;

    public EquipamentoEPCControlador() {
        super(EquipamentoEPC.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/equipamento-epc/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return equipamentoEPCFacade;
    }

    @Override
    @URLAction(mappingId = "criarEquipamentoEPC", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verEquipamentoEPC", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarEquipamentoEPC", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (isValidaCampos()) {
            super.salvar();
        }
    }

    public Boolean isValidaCampos() {
        boolean valida = true;
        if (!Util.validaCampos(selecionado)) {
            valida = false;
        }
        if (equipamentoEPCFacade.existeEPCPorCodigo(selecionado.getCodigo())) {
            FacesUtil.addOperacaoNaoPermitida("O código informado já está cadastrado.");
            valida = false;
        }
        return valida;
    }
}
