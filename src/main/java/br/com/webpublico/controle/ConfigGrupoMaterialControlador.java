package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigGrupoMaterialFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 27/05/14
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configGrupoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-config-grupo-material", pattern = "/configuracao-grupo-material/novo/", viewId = "/faces/financeiro/orcamentario/bens/configgrupomaterial/edita.xhtml"),
        @URLMapping(id = "editar-config-grupo-material", pattern = "/configuracao-grupo-material/editar/#{configGrupoMaterialControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/configgrupomaterial/edita.xhtml"),
        @URLMapping(id = "ver-config-grupo-material", pattern = "/configuracao-grupo-material/ver/#{configGrupoMaterialControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/configgrupomaterial/visualizar.xhtml"),
        @URLMapping(id = "listar-config-grupo-material", pattern = "/configuracao-grupo-material/listar/", viewId = "/faces/financeiro/orcamentario/bens/configgrupomaterial/listar.xhtml")
})
public class ConfigGrupoMaterialControlador extends PrettyControlador<ConfigGrupoMaterial> implements Serializable, CRUD {

    @EJB
    private ConfigGrupoMaterialFacade configGrupoMaterialFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterContaDespesa;
    private ConverterAutoComplete converterGrupoMaterial;

    public ConfigGrupoMaterialControlador() {
        super(ConfigGrupoMaterial.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-grupo-material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configGrupoMaterialFacade;
    }


    @URLAction(mappingId = "novo-config-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "ver-config-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-config-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        selecionado = configGrupoMaterialFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)) {
                if (validarContaDespesaPorGrupo()) {

                    if (!validaConfiguracaoDespesa()) {
                        return;
                    }
                    if (selecionado.getId() == null) {
                        configGrupoMaterialFacade.salvarNovo(selecionado);
                        FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
                    } else {
                        configGrupoMaterialFacade.salvar(selecionado);
                        FacesUtil.addOperacaoRealizada(" Registro alterado com sucesso.");
                    }
                    redireciona();
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private boolean validarContaDespesaPorGrupo() {
        if (!configGrupoMaterialFacade.verificarContaDespesaPorGrupo(selecionado, sistemaControlador.getDataOperacao())) {
            FacesUtil.addOperacaoNaoPermitida(" A Conta de Despesa: " + selecionado.getContaDespesa() + " já está configurada para outro grupo.");
            return false;
        }
        return true;
    }


    public Boolean validaConfiguracaoDespesa() {
        Boolean valida = Boolean.TRUE;
        ConfigGrupoMaterial configuracaoEncotrada = configGrupoMaterialFacade.verificaConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());

        if (configuracaoEncotrada != null && configuracaoEncotrada.getId() != null) {
            FacesUtil.addOperacaoNaoPermitida(" Já existe uma configuração com a Conta de Despesa: " + selecionado.getContaDespesa() +
                    " para o Grupo Material: " + selecionado.getGrupoMaterial() +
                    " com Início de Vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getInicioVigencia()));
            valida = Boolean.FALSE;
        }
        return valida;
    }


    public void encerrarVigencia() {
        try {
            if (selecionado.getFimVigencia() == null) {
                FacesUtil.addCampoObrigatorio(" O campo Fim de Vigência é obrigatório para encerrar a vigência.");
                return;
            }
            if (selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
                FacesUtil.addOperacaoNaoPermitida(" A Data Fim de Vigência não pode ser menor que a data de início de vigência.");
                return;
            }
            configGrupoMaterialFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(" Vigência encerrada com sucesso.");
            redireciona();

        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }


    public boolean podeEditarConfiguracao() {
        if (selecionado.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(selecionado.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public ConverterAutoComplete getConverterContaDespesa() {
        if (converterContaDespesa == null) {
            converterContaDespesa = new ConverterAutoComplete(ContaDespesa.class, configGrupoMaterialFacade.getContaFacade());
        }
        return converterContaDespesa;
    }

    public ConverterAutoComplete getConverterGrupoMaterial() {
        if (converterGrupoMaterial == null) {
            converterGrupoMaterial = new ConverterAutoComplete(GrupoMaterial.class, configGrupoMaterialFacade.getGrupoMaterialFacade());
        }
        return converterGrupoMaterial;
    }

    public List<GrupoMaterial> completaGrupoMaterial(String parte) {
        return configGrupoMaterialFacade.getGrupoMaterialFacade().listaFiltrandoGrupoDeMaterial(parte.trim());
    }

    public List<Conta> completaContaDespesa(String parte) {
        return configGrupoMaterialFacade.getContaFacade().listaFiltrandoContaDespesaNivelDesdobramento(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<SelectItem> getListaTipoEstoque() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoEstoque grupo : TipoEstoque.values()) {
            toReturn.add(new SelectItem(grupo, grupo.getDescricao()));
        }
        return toReturn;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
