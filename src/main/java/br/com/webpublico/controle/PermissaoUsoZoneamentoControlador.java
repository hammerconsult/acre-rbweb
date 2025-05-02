package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClassificacaoUso;
import br.com.webpublico.entidades.ClassificacaoUsoItem;
import br.com.webpublico.entidades.PermissaoUsoZoneamento;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PermissaoUsoZoneamentoFacade;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(
                id = "inserirPermissaoUsoZoneamento",
                pattern = "/cadastro-imobiliario/alvara-imediato/permissao-uso-zoneamento/novo/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/permissaousozoneamento/edita.xhtml"),
        @URLMapping(
                id = "editarPermissaoUsoZoneamento",
                pattern = "/cadastro-imobiliario/alvara-imediato/permissao-uso-zoneamento/editar/#{permissaoUsoZoneamentoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/permissaousozoneamento/edita.xhtml"),
        @URLMapping(
                id = "verPermissaoUsoZoneamento",
                pattern = "/cadastro-imobiliario/alvara-imediato/permissao-uso-zoneamento/ver/#{permissaoUsoZoneamentoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/permissaousozoneamento/visualiza.xhtml"),
        @URLMapping(
                id = "listarPermissaoUsoZoneamento",
                pattern = "/cadastro-imobiliario/alvara-imediato/permissao-uso-zoneamento/listar/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/permissaousozoneamento/lista.xhtml")
})
public class PermissaoUsoZoneamentoControlador extends PrettyControlador<PermissaoUsoZoneamento> implements CRUD {

    @EJB
    private PermissaoUsoZoneamentoFacade facade;
    private List<SelectItem> siClassificacaoUsoItem;

    public PermissaoUsoZoneamentoControlador() {
        super(PermissaoUsoZoneamento.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cadastro-imobiliario/alvara-imediato/permissao-uso-zoneamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getSiClassificacaoUsoItem() {
        return siClassificacaoUsoItem;
    }

    @URLAction(mappingId = "inserirPermissaoUsoZoneamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarPermissaoUsoZoneamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verPermissaoUsoZoneamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        carregarClassificacaoUsoItem();
    }

    public void changeClassificacaoUso() {
        carregarClassificacaoUsoItem();
    }

    private void carregarClassificacaoUsoItem() {
        siClassificacaoUsoItem = Lists.newArrayList();
        if (selecionado.getClassificacaoUso() != null) {
            ClassificacaoUso classificacaoUso = facade.getClassificacaoUsoFacade().recuperar(selecionado.getClassificacaoUso().getId());
            for (ClassificacaoUsoItem classificacaoUsoItem : classificacaoUso.getItemList()) {
                siClassificacaoUsoItem.add(new SelectItem(classificacaoUsoItem, classificacaoUsoItem.toString()));
            }
        }
    }
}
