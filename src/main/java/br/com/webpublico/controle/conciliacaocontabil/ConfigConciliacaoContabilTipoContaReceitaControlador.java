package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilTipoContaReceita;
import br.com.webpublico.enums.TiposCredito;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilTipoContaReceitaFacade;
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
    @URLMapping(id = "config-conciliacao-contabil-tipo-conta-receita-novo", pattern = "/configuracao-conciliacao-contabil/tipo-conta-receita/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/tipo-conta-receita/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-tipo-conta-receita-listar", pattern = "/configuracao-conciliacao-contabil/tipo-conta-receita/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/tipo-conta-receita/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-tipo-conta-receita-editar", pattern = "/configuracao-conciliacao-contabil/tipo-conta-receita/editar/#{configConciliacaoContabilTipoContaReceitaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/tipo-conta-receita/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-tipo-conta-receita-ver", pattern = "/configuracao-conciliacao-contabil/tipo-conta-receita/ver/#{configConciliacaoContabilTipoContaReceitaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/tipo-conta-receita/visualizar.xhtml")
})
public class ConfigConciliacaoContabilTipoContaReceitaControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilTipoContaReceitaFacade facade;
    private ConfigConciliacaoContabilTipoContaReceita configConciliacaoContabilTipoContaReceita;

    @URLAction(mappingId = "config-conciliacao-contabil-tipo-conta-receita-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
    }

    @URLAction(mappingId = "config-conciliacao-contabil-tipo-conta-receita-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-tipo-conta-receita-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getTiposDeContaDeReceita().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos um(1) tipo de conta de receita na configuração.");
        }
    }

    public void adicionarTipoDeContaDeReceita() {
        try {
            validarTipoDeConta();
            Util.adicionarObjetoEmLista(selecionado.getTiposDeContaDeReceita(), configConciliacaoContabilTipoContaReceita);
            cancelarTipoDeConta();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarTipoDeConta() {
        configConciliacaoContabilTipoContaReceita = null;
    }

    public void novoTipoDeConta() {
        configConciliacaoContabilTipoContaReceita = new ConfigConciliacaoContabilTipoContaReceita();
        configConciliacaoContabilTipoContaReceita.setConfigConciliacaoContabil(selecionado);
    }

    public void removerTipoDeConta(ConfigConciliacaoContabilTipoContaReceita config) {
        selecionado.getTiposDeContaDeReceita().remove(config);
    }

    public void editarTipoDeConta(ConfigConciliacaoContabilTipoContaReceita config) {
        configConciliacaoContabilTipoContaReceita = (ConfigConciliacaoContabilTipoContaReceita) Util.clonarObjeto(config);
    }

    private void validarTipoDeConta() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilTipoContaReceita.getTiposCredito() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Conta de Receita deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilTipoContaReceita configTipoConta : selecionado.getTiposDeContaDeReceita()) {
            if (!configConciliacaoContabilTipoContaReceita.equals(configTipoConta) && configTipoConta.getTiposCredito().equals(configConciliacaoContabilTipoContaReceita.getTiposCredito())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de Conta de Receita selecionado já está adicionado.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposDeContaDeReceita() {
        return Util.getListSelectItemSemCampoVazio(TiposCredito.values());
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/tipo-conta-receita/";
    }

    public ConfigConciliacaoContabilTipoContaReceita getConfigConciliacaoContabilTipoContaReceita() {
        return configConciliacaoContabilTipoContaReceita;
    }

    public void setConfigConciliacaoContabilTipoContaReceita(ConfigConciliacaoContabilTipoContaReceita configConciliacaoContabilTipoContaReceita) {
        this.configConciliacaoContabilTipoContaReceita = configConciliacaoContabilTipoContaReceita;
    }
}
