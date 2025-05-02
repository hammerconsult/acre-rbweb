package br.com.webpublico.controle;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.ConfiguracaoDivida;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoDividaFacade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConfiguracaoDivida", pattern = "/tributario/configuracao-divida/novo/",
        viewId = "/faces/tributario/configuracaodivida/edita.xhtml"),

    @URLMapping(id = "editarConfiguracaoDivida", pattern = "/tributario/configuracao-divida/editar/#{configuracaoDividaControlador.id}/",
        viewId = "/faces/tributario/configuracaodivida/edita.xhtml"),

    @URLMapping(id = "listarConfiguracaoDivida", pattern = "/tributario/configuracao-divida/listar/",
        viewId = "/faces/tributario/configuracaodivida/lista.xhtml"),

    @URLMapping(id = "verConfiguracaoDivida", pattern = "/tributario/configuracao-divida/ver/#{configuracaoDividaControlador.id}/",
        viewId = "/faces/tributario/configuracaodivida/visualizar.xhtml")})
public class ConfiguracaoDividaControlador extends PrettyControlador<ConfiguracaoDivida> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoDividaFacade facade;

    public ConfiguracaoDividaControlador() {
        super(ConfiguracaoDivida.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/configuracao-divida/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaConfiguracaoDivida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verConfiguracaoDivida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarConfiguracaoDivida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public void limparDivida() {
        selecionado.setDivida(null);
    }

    public List<SelectItem> getTiposCalculo() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (Calculo.TipoCalculo tipoCalculo : Calculo.TipoCalculo.values()) {
            toReturn.add(new SelectItem(tipoCalculo, tipoCalculo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposCadastroTributario() {
        return Util.getListSelectItem(TipoCadastroTributario.values());
    }

    public List<Divida> completarDividas(String parte) {
        if (selecionado.getEntidade() == null) {
            return Lists.newArrayList();
        }
        return facade.getDividaFacade().buscarDividasPorEntidadeAndTipoCadastroTributario(selecionado.getEntidade(),
            selecionado.getTipoCadastroTributario(), parte);
    }
}
