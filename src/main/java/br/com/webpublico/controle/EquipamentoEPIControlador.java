package br.com.webpublico.controle;

import br.com.webpublico.entidades.EquipamentoEPI;
import br.com.webpublico.entidades.ProtecaoEPI;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EquipamentoEPIFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 19/06/15.
 */
@ManagedBean(name = "equipamentoEPIControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarEquipamentoEPI", pattern = "/equipamento-epi/listar/", viewId = "/faces/rh/administracaodepagamento/equipamentoepi/lista.xhtml"),
                @URLMapping(id = "criarEquipamentoEPI", pattern = "/equipamento-epi/novo/", viewId = "/faces/rh/administracaodepagamento/equipamentoepi/edita.xhtml"),
                @URLMapping(id = "editarEquipamentoEPI", pattern = "/equipamento-epi/editar/#{equipamentoEPIControlador.id}/", viewId = "/faces/rh/administracaodepagamento/equipamentoepi/edita.xhtml"),
                @URLMapping(id = "verEquipamentoEPI", pattern = "/equipamento-epi/ver/#{equipamentoEPIControlador.id}/", viewId = "/faces/rh/administracaodepagamento/equipamentoepi/visualizar.xhtml")
        }
)
public class EquipamentoEPIControlador extends PrettyControlador<EquipamentoEPI> implements CRUD {

    @EJB
    private EquipamentoEPIFacade equipamentoEPIFacade;

    public EquipamentoEPIControlador() {
        super(EquipamentoEPI.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/equipamento-epi/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return equipamentoEPIFacade;
    }

    @Override
    @URLAction(mappingId = "criarEquipamentoEPI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verEquipamentoEPI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarEquipamentoEPI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    public Boolean validaCampos() {
        boolean valida = true;

        if (selecionado.getEquipamento() == null || selecionado.getEquipamento().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("O Equipamento é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getProtecaoEPI() == null) {
            FacesUtil.addCampoObrigatorio("Selecione um tipo de Proteção.");
            valida = false;
        }

        if (selecionado.getCategoria() == null || selecionado.getCategoria().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("A Categoria é um campo obrigatório.");
            valida = false;
        }

        return valida;
    }

    public List<SelectItem> tipoDeProtecaoSelectMenu() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));

        for (ProtecaoEPI protecaoEPI : equipamentoEPIFacade.getProtecaoEPIFacade().listaProtecaoEPI()) {
            tipo.add(new SelectItem(protecaoEPI, protecaoEPI.getNome()));
        }

        return tipo;
    }
}
