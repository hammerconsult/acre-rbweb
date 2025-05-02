package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilGrupoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilGrupoBemImovelFacade;
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
    @URLMapping(id = "config-conciliacao-contabil-grupo-bem-imovel-novo", pattern = "/configuracao-conciliacao-contabil/grupo-bem-imovel/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-bem-imovel/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-grupo-bem-imovel-listar", pattern = "/configuracao-conciliacao-contabil/grupo-bem-imovel/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-bem-imovel/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-grupo-bem-imovel-editar", pattern = "/configuracao-conciliacao-contabil/grupo-bem-imovel/editar/#{configConciliacaoContabilGrupoBemImovelControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-bem-imovel/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-grupo-bem-imovel-ver", pattern = "/configuracao-conciliacao-contabil/grupo-bem-imovel/ver/#{configConciliacaoContabilGrupoBemImovelControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-bem-imovel/visualizar.xhtml")
})
public class ConfigConciliacaoContabilGrupoBemImovelControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilGrupoBemImovelFacade facade;
    private ConfigConciliacaoContabilGrupoBem configConciliacaoContabilGrupoBemImovel;

    @URLAction(mappingId = "config-conciliacao-contabil-grupo-bem-imovel-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
    }

    @URLAction(mappingId = "config-conciliacao-contabil-grupo-bem-imovel-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-grupo-bem-imovel-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getGruposBens().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos um(1) grupo patrimonial na configuração.");
        }
    }

    public void adicionarGrupoBemImovel() {
        try {
            validarNatureza();
            Util.adicionarObjetoEmLista(selecionado.getGruposBens(), configConciliacaoContabilGrupoBemImovel);
            cancelarGrupoBemImovel();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarGrupoBemImovel() {
        configConciliacaoContabilGrupoBemImovel = null;
    }

    public void novoGrupoBemImovel() {
        configConciliacaoContabilGrupoBemImovel = new ConfigConciliacaoContabilGrupoBem();
        configConciliacaoContabilGrupoBemImovel.setConfigConciliacaoContabil(selecionado);
    }

    public void removerGrupoBemImovel(ConfigConciliacaoContabilGrupoBem config) {
        selecionado.getGruposBens().remove(config);
    }

    public void editarGrupoBemImovel(ConfigConciliacaoContabilGrupoBem config) {
        configConciliacaoContabilGrupoBemImovel = (ConfigConciliacaoContabilGrupoBem) Util.clonarObjeto(config);
    }

    private void validarNatureza() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilGrupoBemImovel.getGrupoBem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grupo Patrimonial deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilGrupoBem configGrupoBemImovel : selecionado.getGruposBens()) {
            if (!configConciliacaoContabilGrupoBemImovel.equals(configGrupoBemImovel) && configGrupoBemImovel.getGrupoBem().equals(configConciliacaoContabilGrupoBemImovel.getGrupoBem())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Grupo Patrimonial selecionado já está adicionado.");
            }
        }
        ve.lancarException();
    }

    public List<GrupoBem> completarGruposPatrimoniaisMoveis(String parte) {
        return facade.buscarGruposPatrimoniaisImoveis(parte);
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/grupo-bem-imovel/";
    }

    public ConfigConciliacaoContabilGrupoBem getConfigConciliacaoContabilGrupoBemImovel() {
        return configConciliacaoContabilGrupoBemImovel;
    }

    public void setConfigConciliacaoContabilGrupoBemImovel(ConfigConciliacaoContabilGrupoBem configConciliacaoContabilGrupoBemImovel) {
        this.configConciliacaoContabilGrupoBemImovel = configConciliacaoContabilGrupoBemImovel;
    }
}
