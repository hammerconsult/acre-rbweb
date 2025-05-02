/*
 * Codigo gerado automaticamente em Mon Sep 05 15:28:59 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoExtincaoCargo;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "extincaoCargoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoExtincaoCargo", pattern = "/extincao-cargo/novo/", viewId = "/faces/rh/administracaodepagamento/extincaocargo/edita.xhtml"),
        @URLMapping(id = "editarExtincaoCargo", pattern = "/extincao-cargo/editar/#{extincaoCargoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/extincaocargo/edita.xhtml"),
        @URLMapping(id = "listarExtincaoCargo", pattern = "/extincao-cargo/listar/", viewId = "/faces/rh/administracaodepagamento/extincaocargo/lista.xhtml"),
        @URLMapping(id = "verExtincaoCargo", pattern = "/extincao-cargo/ver/#{extincaoCargoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/extincaocargo/visualizar.xhtml")
})
public class ExtincaoCargoControlador extends PrettyControlador<ExtincaoCargo> implements Serializable, CRUD {

    @EJB
    private ExtincaoCargoFacade extincaoCargoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private CargoFacade cargoFacade;
    private ItemExtincaoCargo itemExtincaoCargo;
    private ConverterAutoComplete converterExtincaoCargo;

    public ExtincaoCargoControlador() {
        super(ExtincaoCargo.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return extincaoCargoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/extincao-cargo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegalPorPropositoAtoLegal(parte.trim(), PropositoAtoLegal.ATO_DE_CARGO);
    }

    public ItemExtincaoCargo getItemExtincaoCargo() {
        return itemExtincaoCargo;
    }

    public void setItemExtincaoCargo(ItemExtincaoCargo itemExtincaoCargo) {
        this.itemExtincaoCargo = itemExtincaoCargo;
    }

    public List<SelectItem> getTiposDeExtincao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoExtincaoCargo object : TipoExtincaoCargo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void novoItemExtincaoCargo() {
        this.itemExtincaoCargo = new ItemExtincaoCargo();
    }

    public void cancelarItemExtincaoCargo() {
        this.itemExtincaoCargo = null;
    }

    @Override
    @URLAction(mappingId = "novoExtincaoCargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataExtincao(new Date());
        carregarDependencias();
    }

    public void carregarDependencias() {
        this.itemExtincaoCargo = (ItemExtincaoCargo) Web.pegaDaSessao(ItemExtincaoCargo.class);
        AtoLegal al = (AtoLegal) Web.pegaDaSessao(AtoLegal.class);
        selecionado.setAtoLegal(al != null ? al : selecionado.getAtoLegal());
    }

    @Override
    @URLAction(mappingId = "editarExtincaoCargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarDependencias();
    }

    @Override
    @URLAction(mappingId = "verExtincaoCargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void confirmarItemExtincaoCargo() {
        if (!Util.validaCampos(this.itemExtincaoCargo)) {
            return;
        }
        if (!Util.validaCampos(selecionado)) {
            return;
        }
        if (cargoJaAdicionado()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O cargo informado já foi relacionado abaixo.");
            return;
        }

        this.itemExtincaoCargo.getCargo().setFinalVigencia(selecionado.getDataExtincao());
        this.itemExtincaoCargo.setExtincaoCargo(selecionado);
        selecionado.setItensExtincaoCargo(Util.adicionarObjetoEmLista(selecionado.getItensExtincaoCargo(), this.itemExtincaoCargo));
        this.itemExtincaoCargo = new ItemExtincaoCargo();
    }

    private boolean cargoJaAdicionado() {
        if (selecionado.getItensExtincaoCargo() == null || selecionado.getItensExtincaoCargo().isEmpty()) {
            return false;
        }
        for (ItemExtincaoCargo iec : selecionado.getItensExtincaoCargo()) {
            if (this.itemExtincaoCargo.getCargo().equals(iec.getCargo())) {
                return true;
            }
        }
        return false;
    }

    public void novoAtoLegal() {
        Web.poeNaSessao(selecionado);
        Web.poeNaSessao(itemExtincaoCargo);
        Web.navegacao(getUrlAtual(), "/atolegal/novo/?sessao=true&propositoAtoLegal=ATO_DE_CARGO", selecionado, itemExtincaoCargo);
    }

    public void removerItemExtincaoCargo(ActionEvent ev) {
        ItemExtincaoCargo aRemover = (ItemExtincaoCargo) ev.getComponent().getAttributes().get("item");
        selecionado.getItensExtincaoCargo().remove(aRemover);
        aRemover.getCargo().setFinalVigencia(null);
        cargoFacade.salvar(aRemover.getCargo());
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "O cargo " + aRemover.getCargo() + " está vigente novamente.");
    }

    @Override
    public void excluir() {
        for (ItemExtincaoCargo iec : selecionado.getItensExtincaoCargo()) {
            iec.getCargo().setFinalVigencia(null);
            cargoFacade.salvar(iec.getCargo());
        }
        super.excluir();
    }

    public ConverterAutoComplete getConverterExtincaoCargo() {
        if (converterExtincaoCargo == null) {
            return new ConverterAutoComplete(ExtincaoCargo.class, extincaoCargoFacade);
        }
        return converterExtincaoCargo;
    }
}
