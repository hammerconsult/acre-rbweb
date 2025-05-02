package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilConta;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilGrupoMaterial;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConfigConciliacaoContabilMaterialConsumoFacade;
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
    @URLMapping(id = "config-conciliacao-contabil-grupo-material-novo", pattern = "/configuracao-conciliacao-contabil/grupo-material/novo/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-material/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-grupo-material-listar", pattern = "/configuracao-conciliacao-contabil/grupo-material/listar/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-material/lista.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-grupo-material-editar", pattern = "/configuracao-conciliacao-contabil/grupo-material/editar/#{configConciliacaoContabilGrupoMaterialControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-material/edita.xhtml"),
    @URLMapping(id = "config-conciliacao-contabil-grupo-material-ver", pattern = "/configuracao-conciliacao-contabil/grupo-material/ver/#{configConciliacaoContabilGrupoMaterialControlador.id}/", viewId = "/faces/financeiro/configuracao-conciliacao-contabil/grupo-material/visualizar.xhtml")
})
public class ConfigConciliacaoContabilGrupoMaterialControlador extends AbstractConfigConciliacaoContabilControlador {

    @EJB
    private ConfigConciliacaoContabilMaterialConsumoFacade facade;
    private ConfigConciliacaoContabilGrupoMaterial configConciliacaoContabilGrupoMaterial;
    private ConfigConciliacaoContabilConta configConciliacaoContabilContaDespesa;
    private CategoriaOrcamentaria categoriaOrcamentaria;

    @URLAction(mappingId = "config-conciliacao-contabil-grupo-material-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataInicial(new Date());
        categoriaOrcamentaria = CategoriaOrcamentaria.NORMAL;
    }

    @URLAction(mappingId = "config-conciliacao-contabil-grupo-material-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarCategoria();
    }

    @URLAction(mappingId = "config-conciliacao-contabil-grupo-material-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarCategoria();
    }

    private void recuperarCategoria() {
        if (selecionado.getGruposMateriais() != null && !selecionado.getGruposMateriais().isEmpty()) {
            categoriaOrcamentaria = selecionado.getGruposMateriais().get(0).getCategoriaOrcamentaria();
        }
    }

    @Override
    public void realizarValidacoes(ValidacaoException ve) {
        if (selecionado.getGruposMateriais().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos um(1) grupo de material na configuração.");
        }
        if (selecionado.getContasDeDespesa().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma(1) conta de despesa na configuração.");
        }
    }

    public void adicionarGM() {
        try {
            validarGM();
            configConciliacaoContabilGrupoMaterial.setCategoriaOrcamentaria(categoriaOrcamentaria);
            Util.adicionarObjetoEmLista(selecionado.getGruposMateriais(), configConciliacaoContabilGrupoMaterial);
            cancelarGM();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarGM() {
        configConciliacaoContabilGrupoMaterial = null;
    }

    public void novoGM() {
        configConciliacaoContabilGrupoMaterial = new ConfigConciliacaoContabilGrupoMaterial();
        configConciliacaoContabilGrupoMaterial.setConfigConciliacaoContabil(selecionado);
    }

    public void removerGM(ConfigConciliacaoContabilGrupoMaterial config) {
        selecionado.getGruposMateriais().remove(config);
    }

    public void editarGM(ConfigConciliacaoContabilGrupoMaterial config) {
        configConciliacaoContabilGrupoMaterial = (ConfigConciliacaoContabilGrupoMaterial) Util.clonarObjeto(config);
    }

    private void validarGM() {
        ValidacaoException ve = new ValidacaoException();
        if (categoriaOrcamentaria == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Categoria Orçamentária deve ser informado.");
        }
        if (configConciliacaoContabilGrupoMaterial.getGrupoMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grupo de Material deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilGrupoMaterial configMaterialConsumo : selecionado.getGruposMateriais()) {
            if (!configConciliacaoContabilGrupoMaterial.equals(configMaterialConsumo) && configMaterialConsumo.getGrupoMaterial().equals(configConciliacaoContabilGrupoMaterial.getGrupoMaterial())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Grupo de Material selecionado já está adicionado.");
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

    public List<GrupoMaterial> completarGruposMateriais(String parte) {
        return facade.getGrupoMaterialFacade().listaFiltrandoGrupoDeMaterialAtivo(parte.trim());
    }

    @Override
    public AbstractConfigConciliacaoContabilFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-conciliacao-contabil/grupo-material/";
    }

    public ConfigConciliacaoContabilGrupoMaterial getConfigConciliacaoContabilGrupoMaterial() {
        return configConciliacaoContabilGrupoMaterial;
    }

    public void setConfigConciliacaoContabilGrupoMaterial(ConfigConciliacaoContabilGrupoMaterial configConciliacaoContabilGrupoMaterial) {
        this.configConciliacaoContabilGrupoMaterial = configConciliacaoContabilGrupoMaterial;
    }

    public ConfigConciliacaoContabilConta getConfigConciliacaoContabilContaDespesa() {
        return configConciliacaoContabilContaDespesa;
    }

    public void setConfigConciliacaoContabilContaDespesa(ConfigConciliacaoContabilConta configConciliacaoContabilContaDespesa) {
        this.configConciliacaoContabilContaDespesa = configConciliacaoContabilContaDespesa;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }
}
