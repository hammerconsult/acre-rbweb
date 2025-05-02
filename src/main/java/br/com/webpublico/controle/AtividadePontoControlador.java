/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtividadePonto;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtividadePontoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "atividadePontoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoAtividadePontoComercial", pattern = "/atividade-dos-pontos-comerciais/novo/", viewId = "/faces/tributario/rendaspatrimoniais/atividadeponto/edita.xhtml"),
        @URLMapping(id = "editarAtividadePontoComercial", pattern = "/atividade-dos-pontos-comerciais/editar/#{atividadePontoControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/atividadeponto/edita.xhtml"),
        @URLMapping(id = "listarAtividadePontoComercial", pattern = "/atividade-dos-pontos-comerciais/listar/", viewId = "/faces/tributario/rendaspatrimoniais/atividadeponto/lista.xhtml"),
        @URLMapping(id = "verAtividadePontoComercial", pattern = "/atividade-dos-pontos-comerciais/ver/#{atividadePontoControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/atividadeponto/visualizar.xhtml")
})
public class AtividadePontoControlador extends PrettyControlador<AtividadePonto> implements Serializable, CRUD {

    @EJB
    private AtividadePontoFacade facade;

    public AtividadePontoControlador() {
        super(AtividadePonto.class);
    }

    public AtividadePontoFacade getFacade() {
        return facade;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoAtividadePontoComercial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(facade.retornaUltimoCodigoLong());
    }

    @URLAction(mappingId = "verAtividadePontoComercial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAtividadePontoComercial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/atividade-dos-pontos-comerciais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        selecionado.setCodigo(facade.retornaUltimoCodigoLong());
        if (validaCampos()) {
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean resultado = true;
        if (selecionado.getCodigo() == null) {
            resultado = false;
            FacesUtil.addError("Impossível continuar", "O código deve ser informado.");
        } else if (facade.codigoJaExiste(selecionado)) {
            resultado = false;
            FacesUtil.addError("Impossível continuar", "O código informado já existe cadastrado para outro registro.");
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().isEmpty()) {
            resultado = false;
            FacesUtil.addError("Impossível continuar", "A descricao deve ser informada.");
        }
        return resultado;
    }

}
