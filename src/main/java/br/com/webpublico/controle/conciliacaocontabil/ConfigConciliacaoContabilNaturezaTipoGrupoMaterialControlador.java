package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilNaturezaTipoGrupoMaterial;
import br.com.webpublico.enums.NaturezaTipoGrupoMaterial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilNaturezaTipoGrupoMaterialFacade;
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
    @URLMapping(id = "config-conciliacao-contabil-natureza-tipo-grupo-material-novo", pattern = "/configuracao-conciliacao-contabil/natureza-tipo-grupo-material/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/natureza-tipo-grupo-material/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-natureza-tipo-grupo-material-listar", pattern = "/configuracao-conciliacao-contabil/natureza-tipo-grupo-material/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/natureza-tipo-grupo-material/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-natureza-tipo-grupo-material-editar", pattern = "/configuracao-conciliacao-contabil/natureza-tipo-grupo-material/editar/#{configConciliacaoContabilNaturezaTipoGrupoMaterialControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/natureza-tipo-grupo-material/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-natureza-tipo-grupo-material-ver", pattern = "/configuracao-conciliacao-contabil/natureza-tipo-grupo-material/ver/#{configConciliacaoContabilNaturezaTipoGrupoMaterialControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/natureza-tipo-grupo-material/visualizar.xhtml")
})
public class ConfigConciliacaoContabilNaturezaTipoGrupoMaterialControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilNaturezaTipoGrupoMaterialFacade facade;
    private ConfigConciliacaoContabilNaturezaTipoGrupoMaterial configConciliacaoContabilNaturezaTipoGrupoMaterial;

    @URLAction(mappingId = "config-conciliacao-contabil-natureza-tipo-grupo-material-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
    }

    @URLAction(mappingId = "config-conciliacao-contabil-natureza-tipo-grupo-material-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-natureza-tipo-grupo-material-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getNaturezasTipoGrupoMaterial().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma(1) natureza do tipo de grupo de material na configuração.");
        }
    }

    public void adicionarNatureza() {
        try {
            validarNatureza();
            Util.adicionarObjetoEmLista(selecionado.getNaturezasTipoGrupoMaterial(), configConciliacaoContabilNaturezaTipoGrupoMaterial);
            cancelarNatureza();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarNatureza() {
        configConciliacaoContabilNaturezaTipoGrupoMaterial = null;
    }

    public void novoNatureza() {
        configConciliacaoContabilNaturezaTipoGrupoMaterial = new ConfigConciliacaoContabilNaturezaTipoGrupoMaterial();
        configConciliacaoContabilNaturezaTipoGrupoMaterial.setConfigConciliacaoContabil(selecionado);
    }

    public void removerNatureza(ConfigConciliacaoContabilNaturezaTipoGrupoMaterial config) {
        selecionado.getNaturezasTipoGrupoMaterial().remove(config);
    }

    public void editarNatureza(ConfigConciliacaoContabilNaturezaTipoGrupoMaterial config) {
        configConciliacaoContabilNaturezaTipoGrupoMaterial = (ConfigConciliacaoContabilNaturezaTipoGrupoMaterial) Util.clonarObjeto(config);
    }

    private void validarNatureza() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilNaturezaTipoGrupoMaterial.getNaturezaTipoGrupoMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Natureza do Tipo de Grupo de Material deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilNaturezaTipoGrupoMaterial configTipoConta : selecionado.getNaturezasTipoGrupoMaterial()) {
            if (!configConciliacaoContabilNaturezaTipoGrupoMaterial.equals(configTipoConta) && configTipoConta.getNaturezaTipoGrupoMaterial().equals(configConciliacaoContabilNaturezaTipoGrupoMaterial.getNaturezaTipoGrupoMaterial())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Natureza do Tipo de Grupo de Material selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getNaturezas() {
        return Util.getListSelectItemSemCampoVazio(NaturezaTipoGrupoMaterial.values());
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/natureza-tipo-grupo-material/";
    }

    public ConfigConciliacaoContabilNaturezaTipoGrupoMaterial getConfigConciliacaoContabilNaturezaTipoGrupoMaterial() {
        return configConciliacaoContabilNaturezaTipoGrupoMaterial;
    }

    public void setConfigConciliacaoContabilNaturezaTipoGrupoMaterial(ConfigConciliacaoContabilNaturezaTipoGrupoMaterial configConciliacaoContabilNaturezaTipoGrupoMaterial) {
        this.configConciliacaoContabilNaturezaTipoGrupoMaterial = configConciliacaoContabilNaturezaTipoGrupoMaterial;
    }
}
