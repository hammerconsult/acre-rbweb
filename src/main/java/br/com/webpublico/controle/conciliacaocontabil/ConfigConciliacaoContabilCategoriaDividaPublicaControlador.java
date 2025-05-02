package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.CategoriaDividaPublica;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilCategoriaDividaPublica;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilConta;
import br.com.webpublico.enums.conciliacaocontabil.TipoMovimentoSaldo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilCategoriaDividaPublicaFacade;
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
    @URLMapping(id = "config-conciliacao-contabil-categoria-divida-publica-novo", pattern = "/configuracao-conciliacao-contabil/categoria-divida-publica/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/categoria-divida-publica/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-categoria-divida-publica-listar", pattern = "/configuracao-conciliacao-contabil/categoria-divida-publica/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/categoria-divida-publica/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-categoria-divida-publica-editar", pattern = "/configuracao-conciliacao-contabil/categoria-divida-publica/editar/#{configConciliacaoContabilCategoriaDividaPublicaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/categoria-divida-publica/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-categoria-divida-publica-ver", pattern = "/configuracao-conciliacao-contabil/categoria-divida-publica/ver/#{configConciliacaoContabilCategoriaDividaPublicaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/categoria-divida-publica/visualizar.xhtml")
})
public class ConfigConciliacaoContabilCategoriaDividaPublicaControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilCategoriaDividaPublicaFacade facade;
    private ConfigConciliacaoContabilCategoriaDividaPublica configConciliacaoContabilCategoriaDividaPublica;
    private ConfigConciliacaoContabilConta configConciliacaoContabilContaDespesa;

    @URLAction(mappingId = "config-conciliacao-contabil-categoria-divida-publica-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
        selecionado.setTipoMovimentoSaldo(TipoMovimentoSaldo.SALDO);
    }

    @URLAction(mappingId = "config-conciliacao-contabil-categoria-divida-publica-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-categoria-divida-publica-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getCategoriasDaDividaPublica().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma(1) categoria da dívida pública na configuração.");
        }
        if (selecionado.getTotalizador().isDividaPublicaEPrecatorio() &&
            !selecionado.getTipoMovimentoSaldo().isSaldo() &&
            selecionado.getContasDeDespesa().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma(1) conta de despesa na configuração.");
        }
    }

    public void adicionarCategoria() {
        try {
            validarCategoria();
            Util.adicionarObjetoEmLista(selecionado.getCategoriasDaDividaPublica(), configConciliacaoContabilCategoriaDividaPublica);
            cancelarCategoria();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarCategoria() {
        configConciliacaoContabilCategoriaDividaPublica = null;
    }

    public void novaCategoria() {
        configConciliacaoContabilCategoriaDividaPublica = new ConfigConciliacaoContabilCategoriaDividaPublica();
        configConciliacaoContabilCategoriaDividaPublica.setConfigConciliacaoContabil(selecionado);
    }

    public void removerCategoria(ConfigConciliacaoContabilCategoriaDividaPublica config) {
        selecionado.getCategoriasDaDividaPublica().remove(config);
    }

    public void editarCategoria(ConfigConciliacaoContabilCategoriaDividaPublica config) {
        configConciliacaoContabilCategoriaDividaPublica = (ConfigConciliacaoContabilCategoriaDividaPublica) Util.clonarObjeto(config);
    }

    private void validarCategoria() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilCategoriaDividaPublica.getCategoriaDividaPublica() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Categoria da Dívida Pública deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilCategoriaDividaPublica configTipoConta : selecionado.getCategoriasDaDividaPublica()) {
            if (!configConciliacaoContabilCategoriaDividaPublica.equals(configTipoConta) && configTipoConta.getCategoriaDividaPublica().equals(configConciliacaoContabilCategoriaDividaPublica.getCategoriaDividaPublica())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Categoria da Dívida Pública selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public List<CategoriaDividaPublica> completarCategoriasDaDividaPublica(String parte) {
        return facade.getCategoriaDividaPublicaFacade().buscarFiltrandoCategoriaDividaPublica(parte);
    }

    public ConfigConciliacaoContabilCategoriaDividaPublica getConfigConciliacaoContabilCategoriaDividaPublica() {
        return configConciliacaoContabilCategoriaDividaPublica;
    }

    public void setConfigConciliacaoContabilCategoriaDividaPublica(ConfigConciliacaoContabilCategoriaDividaPublica configConciliacaoContabilCategoriaDividaPublica) {
        this.configConciliacaoContabilCategoriaDividaPublica = configConciliacaoContabilCategoriaDividaPublica;
    }

    public void adicionarContaDespesa() {
        try {
            validarContaDespesa();
            Util.adicionarObjetoEmLista(selecionado.getContas(), configConciliacaoContabilContaDespesa);
            cancelarContaDespesa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarContaDespesa() {
        configConciliacaoContabilContaDespesa = null;
    }

    public void novoContaDespesa() {
        configConciliacaoContabilContaDespesa = new ConfigConciliacaoContabilConta();
        configConciliacaoContabilContaDespesa.setConfigConciliacaoContabil(selecionado);
    }

    public void removerContaDespesa(ConfigConciliacaoContabilConta config) {
        selecionado.getContas().remove(config);
    }

    public void editarContaDespesa(ConfigConciliacaoContabilConta config) {
        configConciliacaoContabilContaDespesa = (ConfigConciliacaoContabilConta) Util.clonarObjeto(config);
    }

    private void validarContaDespesa() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilContaDespesa.getConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilConta configConta : selecionado.getContasDeDespesa()) {
            if (!configConciliacaoContabilContaDespesa.equals(configConta) && configConta.getConta().equals(configConciliacaoContabilContaDespesa.getConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta de Despesa selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public List<Conta> completarContasDeDespesa(String filtro) {
        return facade.getContaFacade().listaFiltrandoContaDespesa(filtro, facade.getSistemaFacade().getExercicioCorrente());
    }

    public ConfigConciliacaoContabilConta getConfigConciliacaoContabilContaDespesa() {
        return configConciliacaoContabilContaDespesa;
    }

    public void setConfigConciliacaoContabilContaDespesa(ConfigConciliacaoContabilConta configConciliacaoContabilContaDespesa) {
        this.configConciliacaoContabilContaDespesa = configConciliacaoContabilContaDespesa;
    }

    public void limparContasDeDespesa() {
        for (ConfigConciliacaoContabilConta configConciliacaoContabilConta : selecionado.getContasDeDespesa()) {
            selecionado.getContas().remove(configConciliacaoContabilConta);
        }
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/categoria-divida-publica/";
    }
}
