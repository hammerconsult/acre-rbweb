package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilGrupoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilGrupoBemMovelFacade;
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
    @URLMapping(id = "config-conciliacao-contabil-grupo-bem-movel-novo", pattern = "/configuracao-conciliacao-contabil/grupo-bem-movel/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-bem-movel/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-grupo-bem-movel-listar", pattern = "/configuracao-conciliacao-contabil/grupo-bem-movel/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-bem-movel/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-grupo-bem-movel-editar", pattern = "/configuracao-conciliacao-contabil/grupo-bem-movel/editar/#{configConciliacaoContabilGrupoBemMovelControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-bem-movel/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-grupo-bem-movel-ver", pattern = "/configuracao-conciliacao-contabil/grupo-bem-movel/ver/#{configConciliacaoContabilGrupoBemMovelControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-bem-movel/visualizar.xhtml")
})
public class ConfigConciliacaoContabilGrupoBemMovelControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilGrupoBemMovelFacade facade;
    private ConfigConciliacaoContabilGrupoBem configConciliacaoContabilGrupoBemMovel;

    @URLAction(mappingId = "config-conciliacao-contabil-grupo-bem-movel-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
    }

    @URLAction(mappingId = "config-conciliacao-contabil-grupo-bem-movel-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-grupo-bem-movel-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getGruposBensMoveis().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos um(1) grupo patrimonial na configuração.");
        }
    }

    public void adicionarGrupoBemMovel() {
        try {
            validarNatureza();
            Util.adicionarObjetoEmLista(selecionado.getGruposBens(), configConciliacaoContabilGrupoBemMovel);
            cancelarGrupoBemMovel();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarGrupoBemMovel() {
        configConciliacaoContabilGrupoBemMovel = null;
    }

    public void novoGrupoBemMovel() {
        configConciliacaoContabilGrupoBemMovel = new ConfigConciliacaoContabilGrupoBem();
        configConciliacaoContabilGrupoBemMovel.setConfigConciliacaoContabil(selecionado);
    }

    public void removerGrupoBemMovel(ConfigConciliacaoContabilGrupoBem config) {
        selecionado.getGruposBens().remove(config);
    }

    public void editarGrupoBemMovel(ConfigConciliacaoContabilGrupoBem config) {
        configConciliacaoContabilGrupoBemMovel = (ConfigConciliacaoContabilGrupoBem) Util.clonarObjeto(config);
    }

    private void validarNatureza() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilGrupoBemMovel.getGrupoBem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grupo Patrimonial deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilGrupoBem configGrupoBemMovel : selecionado.getGruposBensMoveis()) {
            if (!configConciliacaoContabilGrupoBemMovel.equals(configGrupoBemMovel) && configGrupoBemMovel.getGrupoBem().equals(configConciliacaoContabilGrupoBemMovel.getGrupoBem())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Grupo Patrimonial selecionado já está adicionado.");
            }
        }
        ve.lancarException();
    }

    public List<GrupoBem> completarGruposPatrimoniaisMoveis(String parte) {
        return facade.buscarGruposPatrimoniaisMoveis(parte);
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/grupo-bem-movel/";
    }

    public ConfigConciliacaoContabilGrupoBem getConfigConciliacaoContabilGrupoBemMovel() {
        return configConciliacaoContabilGrupoBemMovel;
    }

    public void setConfigConciliacaoContabilGrupoBemMovel(ConfigConciliacaoContabilGrupoBem configConciliacaoContabilGrupoBemMovel) {
        this.configConciliacaoContabilGrupoBemMovel = configConciliacaoContabilGrupoBemMovel;
    }
}
