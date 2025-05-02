package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigDiariaCivil;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.OperacaoDiariaContabilizacao;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigDiariaCivilFacade;
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

/**
 * @author Fabio
 */
@ManagedBean(name = "configDiariaCivilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-diaria-civil", pattern = "/configuracao-diaria-civil/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodiariacivil/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-diaria-civil", pattern = "/configuracao-diaria-civil/editar/#{configDiariaCivilControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodiariacivil/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-diaria-civil", pattern = "/configuracao-diaria-civil/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodiariacivil/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-diaria-civil", pattern = "/configuracao-diaria-civil/ver/#{configDiariaCivilControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodiariacivil/visualizar.xhtml")
})

public class ConfigDiariaCivilControlador extends ConfigEventoSuperControlador<ConfigDiariaCivil> implements Serializable, CRUD {

    @EJB
    private ConfigDiariaCivilFacade configDiariaCivilFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ConfigDiariaCivil configDiariaCivil;
    private ConfigDiariaCivil configDiariaCivilNaoAlterado;

     @Override
    public AbstractFacade getFacede() {
        return configDiariaCivilFacade;
    }

    public ConfigDiariaCivilControlador() {
        super(ConfigDiariaCivil.class);
    }

    @URLAction(mappingId = "novo-configuracao-diaria-civil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((ConfigDiariaCivil) selecionado).setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "ver-configuracao-diaria-civil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-diaria-civil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        configDiariaCivil = configDiariaCivilFacade.recuperar(selecionado.getId());
        configDiariaCivilNaoAlterado = configDiariaCivilFacade.recuperar(selecionado.getId());
        selecionado = configDiariaCivil;
    }

    public ConfigDiariaCivil esteSelecionado() {
        return (ConfigDiariaCivil) selecionado;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
        esteSelecionado().setOperacaoDiariaContabilizacao(null);
    }

    @Override
    public void salvar() {
        ConfigDiariaCivil ccr = ((ConfigDiariaCivil) selecionado);
        try {
            if (Util.validaCampos(ccr) && validaEditaVigencia()) {
                if (!configDiariaCivilFacade.verificaConfiguracaoExistente((ConfigDiariaCivil) selecionado)) {
                    try {
                        configDiariaCivilFacade.meuSalvar(configDiariaCivilNaoAlterado, ccr);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                        redireciona();
                    } catch (Exception e) {
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação: "
                            + " Tipo Lançamento: " + esteSelecionado().getTipoLancamento() + " e "
                            + " Operação: " + esteSelecionado().getOperacaoDiariaContabilizacao()));
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
        return configDiariaCivilFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.DIARIAS_CIVIL, ((ConfigDiariaCivil) selecionado).getTipoLancamento());
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configDiariaCivilFacade.getEventoContabilFacade());
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
            configDiariaCivilFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public boolean podeEditar() {
        ConfigDiariaCivil ccr = ((ConfigDiariaCivil) selecionado);
        if (ccr.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(ccr.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-diaria-civil/";
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
