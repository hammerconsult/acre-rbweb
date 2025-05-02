package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilPassivoAtuarial;
import br.com.webpublico.enums.TipoPlano;
import br.com.webpublico.enums.conciliacaocontabil.TipoConfigConciliacaoContabil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilPassivoAtuarialFacade;
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
    @URLMapping(id = "config-conciliacao-contabil-passivo-atuarial-novo", pattern = "/configuracao-conciliacao-contabil/passivo-atuarial/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/passivo-atuarial/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-passivo-atuarial-listar", pattern = "/configuracao-conciliacao-contabil/passivo-atuarial/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/passivo-atuarial/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-passivo-atuarial-editar", pattern = "/configuracao-conciliacao-contabil/passivo-atuarial/editar/#{configConciliacaoContabilPassivoAtuarialControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/passivo-atuarial/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-passivo-atuarial-ver", pattern = "/configuracao-conciliacao-contabil/passivo-atuarial/ver/#{configConciliacaoContabilPassivoAtuarialControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/passivo-atuarial/visualizar.xhtml")
})
public class ConfigConciliacaoContabilPassivoAtuarialControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilPassivoAtuarialFacade facade;
    private ConfigConciliacaoContabilPassivoAtuarial configConciliacaoContabilPassivoAtuarial;

    @URLAction(mappingId = "config-conciliacao-contabil-passivo-atuarial-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
        selecionado.setTotalizador(TipoConfigConciliacaoContabil.PASSIVO_ATUARIAL);
    }

    @URLAction(mappingId = "config-conciliacao-contabil-passivo-atuarial-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-passivo-atuarial-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getPassivosAtuariais().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos um(1) Tipo de Plano na configuração.");
        }
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/passivo-atuarial/";
    }

    public void novoPassivoAtuarial() {
        configConciliacaoContabilPassivoAtuarial = new ConfigConciliacaoContabilPassivoAtuarial();
        configConciliacaoContabilPassivoAtuarial.setConfigConciliacaoContabil(selecionado);
    }

    public List<SelectItem> getTiposDePlanos() {
        return Util.getListSelectItem(TipoPlano.values());
    }

    public void cancelarTipoDePlano() {
        configConciliacaoContabilPassivoAtuarial = null;
    }

    public void adicionarTipoDePlano() {
        try {
            validarTipoDePlano();
            Util.adicionarObjetoEmLista(selecionado.getPassivosAtuariais(), configConciliacaoContabilPassivoAtuarial);
            cancelarTipoDePlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void validarTipoDePlano() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilPassivoAtuarial.getTipoPlano() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Plano deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilPassivoAtuarial configPassivoAtuarial : selecionado.getPassivosAtuariais()) {
            if (!configConciliacaoContabilPassivoAtuarial.equals(configPassivoAtuarial) && configPassivoAtuarial.getTipoPlano().equals(configConciliacaoContabilPassivoAtuarial.getTipoPlano())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de Plano selecionado já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public void removerConfigPassivo(ConfigConciliacaoContabilPassivoAtuarial config) {
        selecionado.getPassivosAtuariais().remove(config);
    }

    public void editarConfigPassivo(ConfigConciliacaoContabilPassivoAtuarial config) {
        configConciliacaoContabilPassivoAtuarial = (ConfigConciliacaoContabilPassivoAtuarial) Util.clonarObjeto(config);
    }

    public ConfigConciliacaoContabilPassivoAtuarial getConfigConciliacaoContabilPassivoAtuarial() {
        return configConciliacaoContabilPassivoAtuarial;
    }

    public void setConfigConciliacaoContabilPassivoAtuarial(ConfigConciliacaoContabilPassivoAtuarial configConciliacaoContabilPassivoAtuarial) {
        this.configConciliacaoContabilPassivoAtuarial = configConciliacaoContabilPassivoAtuarial;
    }
}
