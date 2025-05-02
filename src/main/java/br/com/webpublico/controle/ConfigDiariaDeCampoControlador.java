/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigDiariaDeCampo;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.OperacaoDiariaContabilizacao;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigDiariaDeCampoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
import java.util.*;

@ManagedBean(name = "configDiariaDeCampoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-diaria-campo", pattern = "/configuracao-diaria-campo/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodiariadecampo/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-diaria-campo", pattern = "/configuracao-diaria-campo/editar/#{configDiariaDeCampoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodiariadecampo/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-diaria-campo", pattern = "/configuracao-diaria-campo/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodiariadecampo/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-diaria-campo", pattern = "/configuracao-diaria-campo/ver/#{configDiariaDeCampoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodiariadecampo/visualizar.xhtml")
})

public class ConfigDiariaDeCampoControlador extends ConfigEventoSuperControlador<ConfigDiariaDeCampo> implements Serializable, CRUD {

    @EJB
    private ConfigDiariaDeCampoFacade configDiariaDeCampoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ConfigDiariaDeCampo configDiariaDeCampo;
    private ConfigDiariaDeCampo configDiariaDeCampoNaoAlterado;

    @Override
    public AbstractFacade getFacede() {
        return configDiariaDeCampoFacade;
    }

    public ConfigDiariaDeCampoControlador() {
        super(ConfigDiariaDeCampo.class);
    }

    @URLAction(mappingId = "novo-configuracao-diaria-campo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((ConfigDiariaDeCampo) selecionado).setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-diaria-campo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-configuracao-diaria-campo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        configDiariaDeCampo = configDiariaDeCampoFacade.recuperar(selecionado.getId());
        configDiariaDeCampoNaoAlterado = configDiariaDeCampoFacade.recuperar(selecionado.getId());
        selecionado = configDiariaDeCampo;
    }

    public ConfigDiariaDeCampo esteSelecionado() {
        return (ConfigDiariaDeCampo) selecionado;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
    }

    @Override
    public void salvar() {
        ConfigDiariaDeCampo ccr = ((ConfigDiariaDeCampo) selecionado);
        try {
            if (Util.validaCampos(ccr) && validaEditaVigencia()) {
                if (!configDiariaDeCampoFacade.verificaConfiguracaoExistente(((ConfigDiariaDeCampo) selecionado), sistemaControlador.getDataOperacao())) {

                    try {
                        configDiariaDeCampoFacade.meuSalvar(configDiariaDeCampoNaoAlterado, ccr);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso"));
                        redireciona();
                    } catch (Exception e) {
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação: "
                            + " Tipo Lançamento: " + esteSelecionado().getTipoLancamento() + " e "
                            + "Operação: " + esteSelecionado().getOperacaoDiariaContabilizacao()));
                    return;
                }
            }

        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção de sistema", ex.getMessage()));
            return;
        }
        return;
    }

    public boolean validaEditaVigencia() {
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Inválida! ", " Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ".  Para editar a configuração, a data de Início de Vigência não pode ser maior que a data Fim de Vigência."));
                return false;
            }
        }
        return true;
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<SelectItem> getListaOperacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoDiariaContabilizacao odc : OperacaoDiariaContabilizacao.values()) {
            if (!odc.equals(OperacaoDiariaContabilizacao.APROPRIACAO)) {
                toReturn.add(new SelectItem(odc, odc.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        return configDiariaDeCampoFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.DIARIA_CAMPO, ((ConfigDiariaDeCampo) selecionado).getTipoLancamento());
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configDiariaDeCampoFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
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
            configDiariaDeCampoFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }
  
    public boolean podeEditar() {
        ConfigDiariaDeCampo cdc = ((ConfigDiariaDeCampo) selecionado);
        if (cdc.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(cdc.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-diaria-campo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
