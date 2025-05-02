package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigDespesaBens;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaDespesa;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigDespesaBensFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 18/02/14
 * Time: 18:13
 * To change this template use File | Settings | File Templates.
 */


@ManagedBean(name = "configDespesaBensControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-config-despesa-bens", pattern = "/configuracao-despesa-bens/novo/", viewId = "/faces/financeiro/orcamentario/bens/configdespesabens/edita.xhtml"),
    @URLMapping(id = "editar-config-despesa-bens", pattern = "/configuracao-despesa-bens/editar/#{configDespesaBensControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/configdespesabens/edita.xhtml"),
    @URLMapping(id = "ver-config-despesa-bens", pattern = "/configuracao-despesa-bens/ver/#{configDespesaBensControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/configdespesabens/visualizar.xhtml"),
    @URLMapping(id = "listar-config-despesa-bens", pattern = "/configuracao-despesa-bens/listar/", viewId = "/faces/financeiro/orcamentario/bens/configdespesabens/listar.xhtml"),
})
public class ConfigDespesaBensControlador extends ConfigEventoSuperControlador<ConfigDespesaBens> implements Serializable, CRUD {

    @EJB
    private ConfigDespesaBensFacade configDespesaBensFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterContaDespesa;
    private ConverterAutoComplete converterGrupoBem;

    public ConfigDespesaBensControlador() {
        super(ConfigDespesaBens.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configDespesaBensFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-despesa-bens/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-config-despesa-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "ver-config-despesa-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-config-despesa-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        selecionado = configDespesaBensFacade.recuperar(selecionado.getId());
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
                        configDespesaBensFacade.salvarNovo(selecionado);
                        FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
                    } else {
                        configDespesaBensFacade.salvar(selecionado);
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
        if (!configDespesaBensFacade.verificarContaDespesaPorGrupo(selecionado, sistemaControlador.getDataOperacao())) {
            FacesUtil.addOperacaoNaoPermitida(" A Conta de Despesa: " + selecionado.getContaDespesa() + " já está configurada para outro grupo.");
            return false;
        }
        return true;
    }


    public Boolean validaConfiguracaoDespesa() {
        Boolean valida = Boolean.TRUE;
        ConfigDespesaBens configuracaoEncotrada = configDespesaBensFacade.verificaConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
        if (configuracaoEncotrada != null && configuracaoEncotrada.getId() != null) {
            FacesUtil.addOperacaoNaoPermitida(" Já existe uma configuração vigente para o tipo de grupo: "
                + "<b>" + selecionado.getTipoGrupoBem().getDescricao() + "</b>" +
                " e grupo patrimonial: "
                + "<b>" + selecionado.getGrupoBem() + "</b>.");
            valida = Boolean.FALSE;
        }
        return valida;
    }

    public void encerrarVigencia() {
        try {
            if (selecionado.getFimVigencia() == null) {
                FacesUtil.addCampoObrigatorio(" O campo Fim de Vigência deve ser informado.");
                return;
            }
            if (selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
                FacesUtil.addOperacaoNaoPermitida(" A Data Fim de Vigência não pode ser menor que a data de início de vigência.");
                return;
            }
            configDespesaBensFacade.salvar(selecionado);
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
            converterContaDespesa = new ConverterAutoComplete(ContaDespesa.class, configDespesaBensFacade.getContaFacade());
        }
        return converterContaDespesa;
    }

    public ConverterAutoComplete getConverterGrupoBem() {
        if (converterGrupoBem == null) {
            converterGrupoBem = new ConverterAutoComplete(GrupoBem.class, configDespesaBensFacade.getGrupoBemFacade());
        }
        return converterGrupoBem;
    }

    public List<GrupoBem> completaGrupoBem(String parte) {
        return configDespesaBensFacade.getGrupoBemFacade().listaFiltrandoGrupoBemCodigoDescricao(parte.trim());
    }


    public List<Conta> completaContaDespesa(String parte) {
        return configDespesaBensFacade.getContaFacade().listaFiltrandoContaDespesaNivelDesdobramento(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<SelectItem> getListaGrupoBens() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoGrupo grupo : TipoGrupo.values()) {
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
