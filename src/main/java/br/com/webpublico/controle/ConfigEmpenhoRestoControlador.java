package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigEmpenhoRestoContaDesp;
import br.com.webpublico.entidades.ConfigEmpenhoRestoPagar;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigEmpenhoRestoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 27/09/13
 * Time: 09:36
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configEmpenhoRestoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-empenho-resto", pattern = "/configuracao-empenho-resto-a-pagar/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoempenhoresto/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-empenho-resto", pattern = "/configuracao-empenho-resto-a-pagar/editar/#{configEmpenhoRestoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoempenhoresto/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-empenho-resto", pattern = "/configuracao-empenho-resto-a-pagar/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoempenhoresto/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-empenho-resto", pattern = "/configuracao-empenho-resto-a-pagar/ver/#{configEmpenhoRestoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoempenhoresto/visualizar.xhtml")
})

public class ConfigEmpenhoRestoControlador extends ConfigEventoSuperControlador<ConfigEmpenhoRestoPagar> implements Serializable, CRUD {

    @EJB
    private ConfigEmpenhoRestoFacade configEmpenhoRestoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Conta contaDespesa;
    private ConfigEmpenhoRestoPagar configEmpenhoNaoAlterado;
    private ConverterAutoComplete converterContaDesp;
    private ConverterAutoComplete converterEventoContabil;

    public ConfigEmpenhoRestoControlador() {
        super(ConfigEmpenhoRestoPagar.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-empenho-resto-a-pagar/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configEmpenhoRestoFacade;
    }

    @URLAction(mappingId = "novo-configuracao-empenho-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setTipoRestosProcessados(TipoRestosProcessado.PROCESSADOS);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-empenho-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
        configEmpenhoNaoAlterado = (ConfigEmpenhoRestoPagar) getFacede().recuperar(super.getId());
    }

    @URLAction(mappingId = "ver-configuracao-empenho-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        selecionado = configEmpenhoRestoFacade.recuperar(selecionado.getId());
        configEmpenhoNaoAlterado = configEmpenhoRestoFacade.recuperar(selecionado.getId());
        contaDespesa = selecionado.getConfigEmpenhoRestoContaDespesas().get(0).getContaDespesa();
    }

    public boolean validaCampoConta() {
        if (contaDespesa == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Conta de Despesa deve ser informado.");
            return false;
        }
        return true;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                validaConfiguracaoContaDespesa();
                configEmpenhoRestoFacade.meuSalvar(configEmpenhoNaoAlterado, selecionado);
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
                redireciona();
            } catch (Exception e) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            }
        }
    }

    private void validaConfiguracaoContaDespesa() {
        if (selecionado.getConfigEmpenhoRestoContaDespesas().isEmpty()) {
            ConfigEmpenhoRestoContaDesp configEmpenhoRestoContaDesp = new ConfigEmpenhoRestoContaDesp();
            configEmpenhoRestoContaDesp.setConfigEmpenhoResto(selecionado);
            configEmpenhoRestoContaDesp.setContaDespesa(contaDespesa);
            selecionado.getConfigEmpenhoRestoContaDespesas().add(configEmpenhoRestoContaDesp);
        } else {
            getSelecionado().getConfigEmpenhoRestoContaDespesas().remove(0);
            ConfigEmpenhoRestoContaDesp configEmpenhoRestoContaDesp = new ConfigEmpenhoRestoContaDesp();
            configEmpenhoRestoContaDesp.setConfigEmpenhoResto(selecionado);
            configEmpenhoRestoContaDesp.setContaDespesa(contaDespesa);
            selecionado.getConfigEmpenhoRestoContaDespesas().add(configEmpenhoRestoContaDesp);
        }
    }

    private boolean validaCampos() {
        return Util.validaCampos(selecionado) && validaCampoConta() && hasConfiguracaoExistente();
    }

    public List<TipoLancamento> getListaTipoLancamentos() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<TipoRestosProcessado> getListaTipoRestosProcessado() {
        List<TipoRestosProcessado> lista = new ArrayList<TipoRestosProcessado>();
        lista.addAll(Arrays.asList(TipoRestosProcessado.values()));
        return lista;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        return configEmpenhoRestoFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.RESTO_PAGAR, selecionado.getTipoLancamento());
    }

    public List<Conta> completaContasDespesa(String parte) {
        return configEmpenhoRestoFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public void setaContaDespesa(SelectEvent evt) {
        contaDespesa = (Conta) evt.getObject();
    }

    public ConverterAutoComplete getConverterContaDesp() {
        if (this.converterContaDesp == null) {
            this.converterContaDesp = new ConverterAutoComplete(Conta.class, configEmpenhoRestoFacade.getContaFacade());
        }
        return this.converterContaDesp;
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configEmpenhoRestoFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public void setaEventoNull(AjaxBehaviorEvent event) {
        selecionado.setEventoContabil(null);
    }

    public void validaDataInicioVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date dataVigencia = (Date) value;
        Calendar dataInicioVigencia = Calendar.getInstance();
        dataInicioVigencia.setTime(dataVigencia);
        Integer ano = sistemaControlador.getExercicioCorrente().getAno();
        if (dataInicioVigencia.get(Calendar.YEAR) != ano) {
            message.setSummary(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao());
            message.setDetail(" Ano diferente do exercício corrente!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (dataVigencia.compareTo(selecionado.getFimVigencia()) > 0) {
                message.setSummary(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao());
                message.setDetail("Vigência já encerrada na data <b>" + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + "</b>.  Para editar a configuração, a data de Início de Vigência deve ser menor que a data Fim de Vigência.");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public void encerrarVigencia() {
        try {
            configEmpenhoRestoFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public boolean podeEditar() {
        if (selecionado.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(selecionado.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasConfiguracaoExistente() {
        Boolean controle = true;
        StringBuilder str = new StringBuilder();
        List<Conta> contas = new ArrayList<Conta>();
        if (configEmpenhoRestoFacade.hasConfiguracaoExistente(contaDespesa, selecionado.getTipoLancamento(), selecionado, sistemaControlador.getDataOperacao(), selecionado.getTipoRestosProcessados(), selecionado.getEmLiquidacao())) {
            contas.add(contaDespesa);
        }
        if (!contas.isEmpty()) {
            String concatenacao = "";
            for (Conta c : contas) {
                str.append(concatenacao).append(c.getCodigo());
                concatenacao = " - ";
                str.append(concatenacao).append(c.getDescricao());
                concatenacao = " ; ";
            }
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma configuração vigente, configurada como: "
                + " Tipo de Lançamento: " + selecionado.getTipoLancamento()
                + ", Tipo de Resto: " + selecionado.getTipoRestosProcessados()
                + ", Em Liquidação: " + Util.converterBooleanSimOuNao(selecionado.getEmLiquidacao())
                + " e Conta de Despesa: " + str);
            controle = false;
        }
        return controle;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public ConfigEmpenhoRestoPagar getConfigEmpenhoNaoAlterado() {
        return configEmpenhoNaoAlterado;
    }

    public void setConfigEmpenhoNaoAlterado(ConfigEmpenhoRestoPagar configEmpenhoNaoAlterado) {
        this.configEmpenhoNaoAlterado = configEmpenhoNaoAlterado;
    }

    public void setaEventoNull() {
        selecionado.setEventoContabil(null);
    }
}




