package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigDespesaExtra;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.entidades.TipoContaExtra;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigDespesaExtraFacade;
import br.com.webpublico.util.*;
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
 * @author Major
 */
@ManagedBean(name = "configDespesaExtraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-despesa-extra", pattern = "/configuracao-despesa-extra/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodespesaextra/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-despesa-extra", pattern = "/configuracao-despesa-extra/editar/#{configDespesaExtraControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodespesaextra/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-despesa-extra", pattern = "/configuracao-despesa-extra/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodespesaextra/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-despesa-extra", pattern = "/configuracao-despesa-extra/ver/#{configDespesaExtraControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaodespesaextra/visualizar.xhtml")
})
public class ConfigDespesaExtraControlador extends ConfigEventoSuperControlador<ConfigDespesaExtra> implements Serializable, CRUD {

    @EJB
    private ConfigDespesaExtraFacade configDespesaExtraFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ConverterGenerico converterTipoContaExtra;
    private ModoListagem modoListagem;
    private ConfigDespesaExtra configDespesaExtra;
    private ConfigDespesaExtra configDespExtraNaoAlterado;

    @Override
    public AbstractFacade getFacede() {
        return configDespesaExtraFacade;
    }

    public ConfigDespesaExtraControlador() {
        super(ConfigDespesaExtra.class);
    }

    public ConfigDespesaExtra getConfigDespesaExtra() {
        return configDespesaExtra;
    }

    public void setConfigDespesaExtra(ConfigDespesaExtra configDespesaExtra) {
        this.configDespesaExtra = configDespesaExtra;
    }

    public ConfigDespesaExtra getConfigDespExtraNaoAlterado() {
        return configDespExtraNaoAlterado;
    }

    public void setConfigDespExtraNaoAlterado(ConfigDespesaExtra configDespExtraNaoAlterado) {
        this.configDespExtraNaoAlterado = configDespExtraNaoAlterado;
    }

    public enum ModoListagem {

        VIGENTE("Vigente"),
        ENCERRADO("Encerrado");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private ModoListagem(String descricao) {
            this.descricao = descricao;
        }
    }

    public void carregaLista() {
        modoListagem = ModoListagem.VIGENTE;
    }

    @URLAction(mappingId = "novo-configuracao-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((ConfigDespesaExtra) selecionado).setInicioVigencia(sistemaControlador.getDataOperacao());
        ((ConfigDespesaExtra) selecionado).setTipoLancamento(TipoLancamento.NORMAL);
    }

    @URLAction(mappingId = "ver-configuracao-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        configDespesaExtra = configDespesaExtraFacade.recuperar(selecionado.getId());
        configDespExtraNaoAlterado = configDespesaExtraFacade.recuperar(selecionado.getId());
        selecionado = configDespesaExtra;
    }

    @Override
    public void salvar() {
        ConfigDespesaExtra cde = ((ConfigDespesaExtra) selecionado);
        try {
            if (Util.validaCampos(cde) && validaEditaVigencia()) {
                ConfigDespesaExtra configuracaoEncontrada = configDespesaExtraFacade.verificaConfiguracaoExistente(((ConfigDespesaExtra) selecionado), sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação: "
                            + " Tipo Lançamento: " + cde.getTipoLancamento() + " e "
                            + " Tipo de Conta Extra: " + cde.getTipoContaExtraorcamentaria()));
                    return;
                }
                try {
                    configDespesaExtraFacade.meuSalvar(configDespExtraNaoAlterado, cde);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                    redireciona();
                } catch (Exception e) {
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

    public List<SelectItem> getListaTipoContaExtra() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoContaExtraorcamentaria tp : TipoContaExtraorcamentaria.values()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        ConfigDespesaExtra conf = ((ConfigDespesaExtra) selecionado);
        if (conf.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configDespesaExtraFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.DESPESA_EXTRA_ORCAMENTARIA, TipoLancamento.NORMAL);
        } else {
            return configDespesaExtraFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.DESPESA_EXTRA_ORCAMENTARIA, TipoLancamento.ESTORNO);
        }
    }

    public ConverterGenerico getConverterTipoContaExtra() {
        if (this.converterTipoContaExtra == null) {
            this.converterTipoContaExtra = new ConverterGenerico(TipoContaExtra.class, configDespesaExtraFacade.getTipoContaExtraFacade());
        }
        return this.converterTipoContaExtra;
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
            configDespesaExtraFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public ConfigDespesaExtra esteSelecionado() {
        return (ConfigDespesaExtra) selecionado;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
        esteSelecionado().setTipoContaExtraorcamentaria(null);
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configDespesaExtraFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public ModoListagem getModoListagem() {
        return modoListagem;
    }

    public void setModoListagem(ModoListagem modoListagem) {
        this.modoListagem = modoListagem;
    }

    public boolean podeEditar() {
        ConfigDespesaExtra conf = (ConfigDespesaExtra) selecionado;
        if (conf.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(selecionado.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-despesa-extra/";
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
