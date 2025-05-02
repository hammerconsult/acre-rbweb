package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilConta;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilContaFacade;
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
    @URLMapping(id = "config-conciliacao-contabil-conta-receita-novo", pattern = "/configuracao-conciliacao-contabil/conta-receita/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/conta-receita/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-conta-receita-listar", pattern = "/configuracao-conciliacao-contabil/conta-receita/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/conta-receita/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-conta-receita-editar", pattern = "/configuracao-conciliacao-contabil/conta-receita/editar/#{configConciliacaoContabilContaReceitaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/conta-receita/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-conta-receita-ver", pattern = "/configuracao-conciliacao-contabil/conta-receita/ver/#{configConciliacaoContabilContaReceitaControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/conta-receita/visualizar.xhtml")
})
public class ConfigConciliacaoContabilContaReceitaControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilContaFacade facade;
    private ConfigConciliacaoContabilConta configConciliacaoContabilContaReceita;

    @URLAction(mappingId = "config-conciliacao-contabil-conta-receita-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
    }

    @URLAction(mappingId = "config-conciliacao-contabil-conta-receita-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-conta-receita-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getContasDeReceita().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma(1) conta de receita na configuração.");
        }
    }

    public void adicionarContaDeReceita() {
        try {
            validarContaDeReceita();
            Util.adicionarObjetoEmLista(selecionado.getContas(), configConciliacaoContabilContaReceita);
            cancelarContaDeReceita();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarContaDeReceita() {
        configConciliacaoContabilContaReceita = null;
    }

    public void novoContaDeReceita() {
        configConciliacaoContabilContaReceita = new ConfigConciliacaoContabilConta();
        configConciliacaoContabilContaReceita.setConfigConciliacaoContabil(selecionado);
    }

    public void removerContaDeReceita(ConfigConciliacaoContabilConta config) {
        selecionado.getContas().remove(config);
    }

    public void editarContaDeReceita(ConfigConciliacaoContabilConta config) {
        configConciliacaoContabilContaReceita = (ConfigConciliacaoContabilConta) Util.clonarObjeto(config);
    }

    private void validarContaDeReceita() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilContaReceita.getConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Receita deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilConta configConta : selecionado.getContasDeReceita()) {
            if (!configConciliacaoContabilContaReceita.equals(configConta) && configConta.getConta().equals(configConciliacaoContabilContaReceita.getConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta de Receita selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public List<Conta> completarContasDeReceita(String filtro) {
        return facade.getContaFacade().listaFiltrandoReceita(filtro, facade.getSistemaFacade().getExercicioCorrente());
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/conta-receita/";
    }

    public ConfigConciliacaoContabilConta getConfigConciliacaoContabilContaReceita() {
        return configConciliacaoContabilContaReceita;
    }

    public void setConfigConciliacaoContabilContaReceita(ConfigConciliacaoContabilConta configConciliacaoContabilContaReceita) {
        this.configConciliacaoContabilContaReceita = configConciliacaoContabilContaReceita;
    }
}
