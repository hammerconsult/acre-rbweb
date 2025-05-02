package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigTipoViagemContaDespesaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 11/09/14
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configTipoViagemContaDespesaControlador")
@ViewScoped

@URLMappings(mappings = {
        @URLMapping(id = "novo-configuracao-tipoviagem-contadespesa", pattern = "/configuracao-tipoviagem-contadespesa/novo/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-tipoviagem-contadespesa/edita.xhtml"),
        @URLMapping(id = "editar-configuracao-tipoviagem-contadespesa", pattern = "/configuracao-tipoviagem-contadespesa/editar/#{configTipoViagemContaDespesaControlador.id}/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-tipoviagem-contadespesa/edita.xhtml"),
        @URLMapping(id = "ver-configuracao-tipoviagem-contadespesa", pattern = "/configuracao-tipoviagem-contadespesa/ver/#{configTipoViagemContaDespesaControlador.id}/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-tipoviagem-contadespesa/visualizar.xhtml"),
        @URLMapping(id = "listar-configuracao-tipoviagem-contadespesa", pattern = "/configuracao-tipoviagem-contadespesa/listar/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-tipoviagem-contadespesa/lista.xhtml")
})

public class ConfigTipoViagemContaDespesaControlador extends PrettyControlador<ConfigTipoViagemContaDespesa> implements Serializable, CRUD {
    @EJB
    private ConfigTipoViagemContaDespesaFacade configTipoViagemContaDespesaFacade;
    private ConverterAutoComplete converterContaDespesa;
    private TipoViagemContaDespesa tipoViagemContaDespesa;

    public ConfigTipoViagemContaDespesaControlador() {
        super(ConfigTipoViagemContaDespesa.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-tipoviagem-contadespesa/";
    }

    @Override
    @URLAction(mappingId = "novo-configuracao-tipoviagem-contadespesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        tipoViagemContaDespesa = new TipoViagemContaDespesa();
        setOperacao(Operacoes.NOVO);
        SistemaControlador sistemaControlador = getSistemaControlador();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    @URLAction(mappingId = "ver-configuracao-tipoviagem-contadespesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        tipoViagemContaDespesa = new TipoViagemContaDespesa();
    }

    @Override
    @URLAction(mappingId = "editar-configuracao-tipoviagem-contadespesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        tipoViagemContaDespesa = new TipoViagemContaDespesa();
    }

    @Override
    public void salvar() {
        try {
            if (validaSalvar()) {
                if (operacao.equals(Operacoes.NOVO)) {
                    configTipoViagemContaDespesaFacade.salvarNovo(selecionado);
                } else {
                    configTipoViagemContaDespesaFacade.salvar(selecionado);
                }
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Registro salvo com sucesso.");
                redireciona();
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), ex.getMessage());

        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    private boolean validaSalvar() {
        if (selecionado.getInicioVigencia() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Início de Vigência deve ser informado.");
            return false;
        }
        if (tipoViagemContaDespesa.getTipoViagem() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Tipo de Viagem deve ser informado.");
            return false;
        }
        if (selecionado.getListaContaDespesa().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " É obrigatório ter pelo menos uma <b>Configuração</b> adicionada na lista para salvar.");
            return false;
        }
        if (!validarVigencia()) {
            return false;
        }
        return true;
    }

    private Boolean validarVigencia() {
        try {
            configTipoViagemContaDespesaFacade.validarConfiguracaoVigente(selecionado, tipoViagemContaDespesa.getTipoViagem());
            return Boolean.TRUE;
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), e.getMessage());
            return Boolean.FALSE;
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configTipoViagemContaDespesaFacade;
    }

    public List<SelectItem> getLitaTipoViagem() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (TipoViagem tipoViagem : TipoViagem.values()) {
            retorno.add(new SelectItem(tipoViagem, tipoViagem.getDescricao()));
        }
        return Util.ordenaSelectItem(retorno);
    }

    public ConverterAutoComplete getConverterContaDespesa() {
        if (converterContaDespesa == null) {
            converterContaDespesa = new ConverterAutoComplete(ContaDespesa.class, configTipoViagemContaDespesaFacade.getContaDespesaFacade());
        }
        return converterContaDespesa;
    }

    public List<Conta> listaContaDespesa(String parte) {
        return configTipoViagemContaDespesaFacade.getContaDespesaFacade().listaFiltrandoContaDespesaNivelDesdobramentoEhTipoDespesa(parte.trim(), getSistemaControlador().getExercicioCorrente(), TipoContaDespesa.DIARIA_CIVIL);
    }

    public void adicionarContaDespesa() {
        if (podeAdicionarContaDespesa(selecionado, tipoViagemContaDespesa.getContaDespesa(), tipoViagemContaDespesa.getTipoViagem())) {
            tipoViagemContaDespesa.setConfigTipoViagemContaDesp(selecionado);
            this.selecionado.setListaContaDespesa(Util.adicionarObjetoEmLista(selecionado.getListaContaDespesa(), tipoViagemContaDespesa));
            tipoViagemContaDespesa = new TipoViagemContaDespesa();
        }
    }

    private Boolean podeAdicionarContaDespesa(ConfigTipoViagemContaDespesa selecionado, ContaDespesa contaDesp, TipoViagem tipoViagem) {
        if (tipoViagemContaDespesa.getContaDespesa() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A campo Conta de Despesa deve ser informado para adicionar.");
            return false;
        }
        if (tipoViagemContaDespesa.getTipoViagem() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A campo Tipo de Viagem deve ser informado para adicionar.");
            return false;
        }
        for (TipoViagemContaDespesa tipoViagemContaDespesa : selecionado.getListaContaDespesa()) {
            if (tipoViagemContaDespesa.getContaDespesa().equals(contaDesp) && tipoViagemContaDespesa.getTipoViagem().equals(tipoViagem)) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(),
                        " A configuração com o tipo de viagem: <b>"
                        + tipoViagemContaDespesa.getTipoViagem().getDescricao()
                        + "</b> e conta de despesa: <b>"
                        + tipoViagemContaDespesa.getContaDespesa().toString()
                        + " </b> já foi adicionada na lista.");
                return false;
            }
        }
        return true;
    }

    public void removerContaDespesa(TipoViagemContaDespesa tipoViagemContaDespesa) {
        this.selecionado.getListaContaDespesa().remove(tipoViagemContaDespesa);
    }

    public TipoViagemContaDespesa getTipoViagemContaDespesa() {
        return tipoViagemContaDespesa;
    }

    public void setTipoViagemContaDespesa(TipoViagemContaDespesa tipoViagemContaDespesa) {
        this.tipoViagemContaDespesa = tipoViagemContaDespesa;
    }
}
