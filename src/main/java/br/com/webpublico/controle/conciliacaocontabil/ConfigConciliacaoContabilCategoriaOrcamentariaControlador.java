package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilCategoriaOrcamentaria;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilConta;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilCategoriaOrcamentariaFacade;
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
    @URLMapping(id = "config-conciliacao-contabil-categoria-orcamentaria-novo", pattern = "/configuracao-conciliacao-contabil/categoria-orcamentaria/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/categoria-orcamentaria/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-categoria-orcamentaria-listar", pattern = "/configuracao-conciliacao-contabil/categoria-orcamentaria/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/categoria-orcamentaria/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-categoria-orcamentaria-editar", pattern = "/configuracao-conciliacao-contabil/categoria-orcamentaria/editar/#{configConciliacaoContabilCategoriaOrcamentariaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/categoria-orcamentaria/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-categoria-orcamentaria-ver", pattern = "/configuracao-conciliacao-contabil/categoria-orcamentaria/ver/#{configConciliacaoContabilCategoriaOrcamentariaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/categoria-orcamentaria/visualizar.xhtml")
})
public class ConfigConciliacaoContabilCategoriaOrcamentariaControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilCategoriaOrcamentariaFacade facade;
    private ConfigConciliacaoContabilCategoriaOrcamentaria configConciliacaoContabilCategoriaOrcamentaria;
    private ConfigConciliacaoContabilConta configConciliacaoContabilContaDespesa;

    @URLAction(mappingId = "config-conciliacao-contabil-categoria-orcamentaria-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
    }

    @URLAction(mappingId = "config-conciliacao-contabil-categoria-orcamentaria-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-categoria-orcamentaria-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getCategoriasOrcamentarias().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma(1) categoria orçamentária na configuração.");
        }
        if (selecionado.isObrasInstalacoesEAquisicoesDeImoveis() && selecionado.getContasDeDespesa().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma(1) conta de despesa na configuração.");
        }
    }

    public void adicionarCategoria() {
        try {
            validarCategoria();
            Util.adicionarObjetoEmLista(selecionado.getCategoriasOrcamentarias(), configConciliacaoContabilCategoriaOrcamentaria);
            cancelarCategoria();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarCategoria() {
        configConciliacaoContabilCategoriaOrcamentaria = null;
    }

    public void novoCategoria() {
        configConciliacaoContabilCategoriaOrcamentaria = new ConfigConciliacaoContabilCategoriaOrcamentaria();
        configConciliacaoContabilCategoriaOrcamentaria.setConfigConciliacaoContabil(selecionado);
    }

    public void removerCategoria(ConfigConciliacaoContabilCategoriaOrcamentaria config) {
        selecionado.getCategoriasOrcamentarias().remove(config);
    }

    public void editarCategoria(ConfigConciliacaoContabilCategoriaOrcamentaria config) {
        configConciliacaoContabilCategoriaOrcamentaria = (ConfigConciliacaoContabilCategoriaOrcamentaria) Util.clonarObjeto(config);
    }

    private void validarCategoria() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilCategoriaOrcamentaria.getCategoriaOrcamentaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Categoria Orçamentária deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilCategoriaOrcamentaria configCategorias : selecionado.getCategoriasOrcamentarias()) {
            if (!configConciliacaoContabilCategoriaOrcamentaria.equals(configCategorias) && configCategorias.getCategoriaOrcamentaria().equals(configConciliacaoContabilCategoriaOrcamentaria.getCategoriaOrcamentaria())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Categoria Orçamentária selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getCategorias() {
        return Util.getListSelectItemSemCampoVazio(CategoriaOrcamentaria.values());
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


    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/categoria-orcamentaria/";
    }

    public ConfigConciliacaoContabilCategoriaOrcamentaria getConfigConciliacaoContabilCategoriaOrcamentaria() {
        return configConciliacaoContabilCategoriaOrcamentaria;
    }

    public void setConfigConciliacaoContabilCategoriaOrcamentaria(ConfigConciliacaoContabilCategoriaOrcamentaria configConciliacaoContabilCategoriaOrcamentaria) {
        this.configConciliacaoContabilCategoriaOrcamentaria = configConciliacaoContabilCategoriaOrcamentaria;
    }

    public ConfigConciliacaoContabilConta getConfigConciliacaoContabilContaDespesa() {
        return configConciliacaoContabilContaDespesa;
    }

    public void setConfigConciliacaoContabilContaDespesa(ConfigConciliacaoContabilConta configConciliacaoContabilContaDespesa) {
        this.configConciliacaoContabilContaDespesa = configConciliacaoContabilContaDespesa;
    }
}
