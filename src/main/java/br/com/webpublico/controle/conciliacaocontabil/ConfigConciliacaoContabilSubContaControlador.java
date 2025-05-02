package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilSubConta;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilSubContaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "config-conciliacao-contabil-conta-financeira-novo", pattern = "/configuracao-conciliacao-contabil/conta-financeira/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/conta-financeira/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-conta-financeira-listar", pattern = "/configuracao-conciliacao-contabil/conta-financeira/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/conta-financeira/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-conta-financeira-editar", pattern = "/configuracao-conciliacao-contabil/conta-financeira/editar/#{configConciliacaoContabilSubContaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/conta-financeira/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-conta-financeira-ver", pattern = "/configuracao-conciliacao-contabil/conta-financeira/ver/#{configConciliacaoContabilSubContaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/conta-financeira/visualizar.xhtml")
})
public class ConfigConciliacaoContabilSubContaControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilSubContaFacade facade;
    private ConfigConciliacaoContabilSubConta configConciliacaoContabilSubConta;

    @URLAction(mappingId = "config-conciliacao-contabil-conta-financeira-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
    }

    @URLAction(mappingId = "config-conciliacao-contabil-conta-financeira-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-conta-financeira-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getContasFinanceiras().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma(1) conta financeira na configuração.");
        }
    }

    public void adicionarTodasContasFinanceirasAtivas() {
        for (SubConta subConta : completarContasFinanceiras("")) {
            novoContaFinanceira();
            configConciliacaoContabilSubConta.setSubConta(subConta);
            adicionarContaFinanceira();
            cancelarContaFinanceira();
        }
    }

    public void adicionarContaFinanceira() {
        try {
            validarContaFinanceira();
            Util.adicionarObjetoEmLista(selecionado.getContasFinanceiras(), configConciliacaoContabilSubConta);
            cancelarContaFinanceira();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarContaFinanceira() {
        configConciliacaoContabilSubConta = null;
    }

    public void novoContaFinanceira() {
        configConciliacaoContabilSubConta = new ConfigConciliacaoContabilSubConta();
        configConciliacaoContabilSubConta.setConfigConciliacaoContabil(selecionado);
    }

    public void removerContaFinanceira(ConfigConciliacaoContabilSubConta config) {
        selecionado.getContasFinanceiras().remove(config);
    }

    public void editarContaFinanceira(ConfigConciliacaoContabilSubConta config) {
        configConciliacaoContabilSubConta = (ConfigConciliacaoContabilSubConta) Util.clonarObjeto(config);
    }

    private void validarContaFinanceira() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilSubConta.getSubConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Financeira deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilSubConta configSubConta : selecionado.getContasFinanceiras()) {
            if (!configConciliacaoContabilSubConta.equals(configSubConta) && configSubConta.getSubConta().equals(configConciliacaoContabilSubConta.getSubConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta Financeira selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public List<SubConta> completarContasFinanceiras(String filtro) {
        return facade.getSubContaFacade().buscarContasFinanceirasAtivas(filtro);
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/conta-financeira/";
    }

    public ConfigConciliacaoContabilSubConta getConfigConciliacaoContabilSubConta() {
        return configConciliacaoContabilSubConta;
    }

    public void setConfigConciliacaoContabilSubConta(ConfigConciliacaoContabilSubConta configConciliacaoContabilSubConta) {
        this.configConciliacaoContabilSubConta = configConciliacaoContabilSubConta;
    }
}
