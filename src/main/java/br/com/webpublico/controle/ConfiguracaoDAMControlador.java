/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoDAM;
import br.com.webpublico.entidades.ConfiguracaoPix;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoDAMFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "configuracaoDAMControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoConfiguracaoDAM", pattern = "/configuracaodam/novo/", viewId = "/faces/tributario/configuracaodam/edita.xhtml"),
        @URLMapping(id = "editarConfiguracaoDAM", pattern = "/configuracaodam/editar/#{configuracaoDAMControlador.id}/", viewId = "/faces/tributario/configuracaodam/edita.xhtml"),
        @URLMapping(id = "listarConfiguracaoDAM", pattern = "/configuracaodam/listar/", viewId = "/faces/tributario/configuracaodam/lista.xhtml"),
        @URLMapping(id = "verConfiguracaoDAM", pattern = "/configuracaodam/ver/#{configuracaoDAMControlador.id}/", viewId = "/faces/tributario/configuracaodam/visualizar.xhtml")
})
public class ConfiguracaoDAMControlador extends PrettyControlador<ConfiguracaoDAM> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoDAMFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ConfiguracaoDAMControlador() {
        super(ConfiguracaoDAM.class);
    }

    @URLAction(mappingId = "novoConfiguracaoDAM", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verConfiguracaoDAM", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarConfiguracaoDAM", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (valida()) {
            selecionado.setCodigoFormatado(selecionado.getCodigoFebraban());
            super.salvar();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracaodam/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private boolean valida() {
        boolean valida = true;
        if (selecionado.getDescricao() == null || facade.existeCondiguracaoDAMComEstaDescricao(selecionado)) {
            FacesUtil.addError("Impossível Continuar", "A descrição informada já existe para outro registro de Configuração de DAM");
            valida = false;
        }

        if (selecionado.getIdentificacaoSegmento() == null
                || selecionado.getIdentificacaoSegmento().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O Identificador de Segmento é um campo obrigatório");
        valida = false;
        }
        return valida;
    }

    public List<Divida> completaDivida(String parte) {
        return facade.getDividaFacade().listaFiltrandoDividas(parte.trim(), "descricao");
    }

    public List<Tributo> completaTributo(String parte) {
        return facade.getTributoFacade().listaFiltrando(parte.trim(), "descricao");
    }

}
