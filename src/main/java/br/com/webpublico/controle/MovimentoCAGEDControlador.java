/*
 * Codigo gerado automaticamente em Tue Feb 07 09:41:47 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.MovimentoCAGED;
import br.com.webpublico.enums.TipoCAGED;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MovimentoCAGEDFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "movimentoCAGEDControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMovimentoCAGED", pattern = "/rh/movimento-caged/novo/", viewId = "/faces/rh/administracaodepagamento/movimentocaged/edita.xhtml"),
        @URLMapping(id = "listaMovimentoCAGED", pattern = "/rh/movimento-caged/listar/", viewId = "/faces/rh/administracaodepagamento/movimentocaged/lista.xhtml"),
        @URLMapping(id = "verMovimentoCAGED", pattern = "/rh/movimento-caged/ver/#{movimentoCAGEDControlador.id}/", viewId = "/faces/rh/administracaodepagamento/movimentocaged/visualizar.xhtml"),
        @URLMapping(id = "editarMovimentoCAGED", pattern = "/rh/movimento-caged/editar/#{movimentoCAGEDControlador.id}/", viewId = "/faces/rh/administracaodepagamento/movimentocaged/edita.xhtml"),
})
public class MovimentoCAGEDControlador extends PrettyControlador<MovimentoCAGED> implements Serializable, CRUD {

    @EJB
    private MovimentoCAGEDFacade movimentoCAGEDFacade;

    public MovimentoCAGEDControlador() {
        super(MovimentoCAGED.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/movimento-caged/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return movimentoCAGEDFacade;
    }

    public List<SelectItem> getTipoCAGED() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCAGED object : TipoCAGED.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Boolean validaCampos() {
        if (movimentoCAGEDFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção !", "O Código informado já está cadastrado em outro Movimento CAGED do tipo " + selecionado.getTipoCAGED());
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoMovimentoCAGED", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verMovimentoCAGED", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarMovimentoCAGED", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }
}
