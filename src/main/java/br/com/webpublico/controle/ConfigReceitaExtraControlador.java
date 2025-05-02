/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigReceitaExtra;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.entidades.TipoContaExtra;
import br.com.webpublico.enums.TipoConsignacao;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigReceitaExtraFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Paschualleto
 */
@ManagedBean(name = "configReceitaExtraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-receita-extra", pattern = "/configuracao-receita-extra/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoreceitaextra/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-receita-extra", pattern = "/configuracao-receita-extra/editar/#{configReceitaExtraControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoreceitaextra/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-receita-extra", pattern = "/configuracao-receita-extra/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoreceitaextra/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-receita-extra", pattern = "/configuracao-receita-extra/ver/#{configReceitaExtraControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoreceitaextra/visualizar.xhtml")
})
public class ConfigReceitaExtraControlador extends ConfigEventoSuperControlador<ConfigReceitaExtra> implements Serializable, CRUD {

    @EJB
    private ConfigReceitaExtraFacade configReceitaExtraFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ConverterGenerico converterTipoContaExtra;
    private ConfigReceitaExtra configReceitaExtraNaoAlterada;
    private ConfigReceitaExtra configReceitaExtra;

    public ConfigReceitaExtraControlador() {
        super(ConfigReceitaExtra.class);
    }

    public ConfigReceitaExtraFacade getFacade() {
        return configReceitaExtraFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @Override
    public AbstractFacade getFacede() {
        return configReceitaExtraFacade;
    }

    @URLAction(mappingId = "novo-configuracao-receita-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new ConfigReceitaExtra();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
    }

    @URLAction(mappingId = "ver-configuracao-receita-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-receita-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<>();
        for (TipoLancamento tipo : TipoLancamento.values()) {
            lista.add(tipo);
        }
        return lista;
    }

    public boolean verificaConfiguracaoExistente() {
        ConfigReceitaExtra cr = ((ConfigReceitaExtra) selecionado);
        Boolean controle = true;
        if (configReceitaExtraFacade.verificaEventoExistente(cr, sistemaControlador.getDataOperacao())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                " Já existe uma configuração vigente para a combinação: "
                    + " Tipo Lançamento: " + cr.getTipoLancamento() + ", "
                    + " Tipo de Conta Extra: " + cr.getTipoContaExtraorcamentaria()
                    + " e Tipo de Consignação: " + cr.getTipoConsignacao()));
            controle = false;
        }

        return controle;
    }

    public void recuperaEditaVer() {
        configReceitaExtra = configReceitaExtraFacade.recuperar(selecionado.getId());
        configReceitaExtraNaoAlterada = configReceitaExtraFacade.recuperar(selecionado.getId());
        selecionado = configReceitaExtra;
    }

    @Override
    public void salvar() {
        ConfigReceitaExtra cr = ((ConfigReceitaExtra) selecionado);
        if (Util.validaCampos(cr) && validaEditarDataVigencia() && verificaConfiguracaoExistente()) {
            try {
                configReceitaExtraFacade.meuSalvar(configReceitaExtraNaoAlterada, cr);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                redireciona();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Problema ao salvar!" + e.getMessage(), "Problema ao salvar!" + e.getMessage()));
                return;
            }
            return;
        }
    }

    public ConfigReceitaExtra esteSelecionado() {
        return (ConfigReceitaExtra) selecionado;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        return configReceitaExtraFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA, esteSelecionado().getTipoLancamento());
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configReceitaExtraFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public ConverterGenerico getConverterTipoContaExtra() {
        if (converterTipoContaExtra == null) {
            converterTipoContaExtra = new ConverterGenerico(TipoContaExtra.class, configReceitaExtraFacade.getTipoContaExtraFacade());
        }
        return converterTipoContaExtra;
    }

    public List<SelectItem> getListaTipoContaExtra() {
        List<SelectItem> tipoContaExtra = new ArrayList<SelectItem>();
        tipoContaExtra.add(new SelectItem(null, ""));
        for (TipoContaExtraorcamentaria tce : TipoContaExtraorcamentaria.values()) {
            tipoContaExtra.add(new SelectItem(tce, tce.getDescricao()));
        }
        return tipoContaExtra;
    }

    public List<SelectItem> getListaTipoConsignacao() {
        List<SelectItem> tipoConsignacao = new ArrayList<SelectItem>();
        tipoConsignacao.add(new SelectItem(null, ""));
        if (selecionado.getTipoContaExtraorcamentaria() == null) {
            return new ArrayList<SelectItem>();
        }
        if (selecionado.getTipoContaExtraorcamentaria().equals(TipoContaExtraorcamentaria.DEPOSITOS_CONSIGNACOES)) {
            for (TipoConsignacao tc : TipoConsignacao.values()) {
                if (tc != TipoConsignacao.NAO_APLICAVEL) {
                    tipoConsignacao.add(new SelectItem(tc, tc.getDescricao()));
                }
            }
        } else {
            tipoConsignacao.add(new SelectItem(TipoConsignacao.NAO_APLICAVEL));
        }
        return tipoConsignacao;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
    }

    public void validaDataInicioVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date dataVigencia = (Date) value;
        Calendar dataInicioVigencia = Calendar.getInstance();
        dataInicioVigencia.setTime(dataVigencia);
        Integer ano = sistemaControlador.getExercicioCorrente().getAno();
        if (dataInicioVigencia.get(Calendar.YEAR) != ano) {
            message.setSummary("Data inválida! ");
            message.setDetail(" Ano diferente do exercício corrente!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void encerrarVigencia() {
        try {
            configReceitaExtraFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public boolean validaEditarDataVigencia() {
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Inválida! ", " Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ".  Para editar a configuração, a data de Início de Vigência não pode ser maior que a data Fim de Vigência."));
                return false;
            }
        }
        return true;
    }

    public boolean podeEditar() {
        ConfigReceitaExtra config = (ConfigReceitaExtra) selecionado;
        if (config.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(config.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-receita-extra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
