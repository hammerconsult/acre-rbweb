package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilRecPatrimonialXRecOrc;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilRecPatrimonialXRecOrcFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "config-conciliacao-contabil-receita-patrimonial-x-receita-orcamentaria-novo", pattern = "/configuracao-conciliacao-contabil/receita-patrimonial-x-receita-orcamentaria/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/receita-patrimonial-x-receita-orcamentaria/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-receita-patrimonial-x-receita-orcamentaria-listar", pattern = "/configuracao-conciliacao-contabil/receita-patrimonial-x-receita-orcamentaria/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/receita-patrimonial-x-receita-orcamentaria/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-receita-patrimonial-x-receita-orcamentaria-editar", pattern = "/configuracao-conciliacao-contabil/receita-patrimonial-x-receita-orcamentaria/editar/#{configConciliacaoContabilRecPatrimonialXRecOrcControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/receita-patrimonial-x-receita-orcamentaria/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-receita-patrimonial-x-receita-orcamentaria-ver", pattern = "/configuracao-conciliacao-contabil/receita-patrimonial-x-receita-orcamentaria/ver/#{configConciliacaoContabilRecPatrimonialXRecOrcControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/receita-patrimonial-x-receita-orcamentaria/visualizar.xhtml")
})
public class ConfigConciliacaoContabilRecPatrimonialXRecOrcControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilRecPatrimonialXRecOrcFacade facade;
    private ConfigConciliacaoContabilRecPatrimonialXRecOrc configConciliacaoContabilRecPatrimonialXRecOrc;

    @URLAction(mappingId = "config-conciliacao-contabil-receita-patrimonial-x-receita-orcamentaria-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
    }

    @URLAction(mappingId = "config-conciliacao-contabil-receita-patrimonial-x-receita-orcamentaria-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-receita-patrimonial-x-receita-orcamentaria-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getRecPatrimoniaisXRecOrcs().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos um(1) receita patrimonial x receita orçamentária na configuração.");
        }
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/receita-patrimonial-x-receita-orcamentaria/";
    }

    public ConfigConciliacaoContabilRecPatrimonialXRecOrc getConfigConciliacaoContabilRecPatrimonialXRecOrc() {
        return configConciliacaoContabilRecPatrimonialXRecOrc;
    }

    public void setConfigConciliacaoContabilRecPatrimonialXRecOrc(ConfigConciliacaoContabilRecPatrimonialXRecOrc configConciliacaoContabilRecPatrimonialXRecOrc) {
        this.configConciliacaoContabilRecPatrimonialXRecOrc = configConciliacaoContabilRecPatrimonialXRecOrc;
    }
}
