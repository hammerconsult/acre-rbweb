package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilNaturezaDividaPublica;
import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilNaturezaDividaPublicaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "config-conciliacao-contabil-natureza-divida-publica-novo", pattern = "/configuracao-conciliacao-contabil/natureza-divida-publica/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/natureza-divida-publica/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-natureza-divida-publica-listar", pattern = "/configuracao-conciliacao-contabil/natureza-divida-publica/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/natureza-divida-publica/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-natureza-divida-publica-editar", pattern = "/configuracao-conciliacao-contabil/natureza-divida-publica/editar/#{configConciliacaoContabilNaturezaDividaPublicaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/natureza-divida-publica/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-natureza-divida-publica-ver", pattern = "/configuracao-conciliacao-contabil/natureza-divida-publica/ver/#{configConciliacaoContabilNaturezaDividaPublicaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/natureza-divida-publica/visualizar.xhtml")
})
public class ConfigConciliacaoContabilNaturezaDividaPublicaControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilNaturezaDividaPublicaFacade facade;
    private ConfigConciliacaoContabilNaturezaDividaPublica configConciliacaoContabilNaturezaDividaPublica;

    @URLAction(mappingId = "config-conciliacao-contabil-natureza-divida-publica-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
    }

    @URLAction(mappingId = "config-conciliacao-contabil-natureza-divida-publica-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-natureza-divida-publica-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getNaturezasDaDividaPublica().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma(1) natureza da dívida pública na configuração.");
        }
    }

    public void adicionarNatureza() {
        try {
            validarNatureza();
            Util.adicionarObjetoEmLista(selecionado.getNaturezasDaDividaPublica(), configConciliacaoContabilNaturezaDividaPublica);
            cancelarNatureza();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarNatureza() {
        configConciliacaoContabilNaturezaDividaPublica = null;
    }

    public void novoNatureza() {
        configConciliacaoContabilNaturezaDividaPublica = new ConfigConciliacaoContabilNaturezaDividaPublica();
        configConciliacaoContabilNaturezaDividaPublica.setConfigConciliacaoContabil(selecionado);
    }

    public void removerNatureza(ConfigConciliacaoContabilNaturezaDividaPublica config) {
        selecionado.getNaturezasDaDividaPublica().remove(config);
    }

    public void editarNatureza(ConfigConciliacaoContabilNaturezaDividaPublica config) {
        configConciliacaoContabilNaturezaDividaPublica = (ConfigConciliacaoContabilNaturezaDividaPublica) Util.clonarObjeto(config);
    }

    private void validarNatureza() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilNaturezaDividaPublica.getNaturezaDividaPublica() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Natureza da Dívida Pública deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilNaturezaDividaPublica configTipoConta : selecionado.getNaturezasDaDividaPublica()) {
            if (!configConciliacaoContabilNaturezaDividaPublica.equals(configTipoConta) && configTipoConta.getNaturezaDividaPublica().equals(configConciliacaoContabilNaturezaDividaPublica.getNaturezaDividaPublica())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Natureza da Dívida Pública selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getNaturezas() {
        return Util.getListSelectItemSemCampoVazio(NaturezaDividaPublica.values());
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/natureza-divida-publica/";
    }

    public ConfigConciliacaoContabilNaturezaDividaPublica getConfigConciliacaoContabilNaturezaDividaPublica() {
        return configConciliacaoContabilNaturezaDividaPublica;
    }

    public void setConfigConciliacaoContabilNaturezaDividaPublica(ConfigConciliacaoContabilNaturezaDividaPublica configConciliacaoContabilNaturezaTipoGrupoMaterial) {
        this.configConciliacaoContabilNaturezaDividaPublica = configConciliacaoContabilNaturezaTipoGrupoMaterial;
    }
}
