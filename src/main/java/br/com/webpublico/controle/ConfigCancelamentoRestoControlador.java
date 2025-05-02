package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PatrimonioLiquido;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigCancelamentoRestoFacade;
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
@ManagedBean(name = "configCancelamentoRestoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-cancelamento-resto", pattern = "/configuracao-cancelamento-restos-a-pagar/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configcancelamentoresto/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-cancelamento-resto", pattern = "/configuracao-cancelamento-restos-a-pagar/editar/#{configCancelamentoRestoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configcancelamentoresto/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-cancelamento-resto", pattern = "/configuracao-cancelamento-restos-a-pagar/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configcancelamentoresto/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-cancelamento-resto", pattern = "/configuracao-cancelamento-restos-a-pagar/ver/#{configCancelamentoRestoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configcancelamentoresto/visualizar.xhtml")
})
public class ConfigCancelamentoRestoControlador extends ConfigEventoSuperControlador<ConfigCancelamentoResto> implements Serializable, CRUD {

    @EJB
    private ConfigCancelamentoRestoFacade configCancelamentoRestoFacade;
    private Conta contaDespesa;
    private ConfigCancelamentoResto configuracaoNaoAlterada;
    private ConverterAutoComplete converterContaDesp;
    private ConverterAutoComplete converterEventoContabil;

    public ConfigCancelamentoRestoControlador() {
        super(ConfigCancelamentoResto.class);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-cancelamento-restos-a-pagar/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configCancelamentoRestoFacade;
    }

    @URLAction(mappingId = "novo-configuracao-cancelamento-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setTipoRestosProcessados(TipoRestosProcessado.PROCESSADOS);
        selecionado.setInicioVigencia(getSistemaControlador().getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-cancelamento-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
        configuracaoNaoAlterada = (ConfigCancelamentoResto) getFacede().recuperar(super.getId());
    }

    @URLAction(mappingId = "ver-configuracao-cancelamento-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        selecionado = configCancelamentoRestoFacade.recuperar(selecionado.getId());
        configuracaoNaoAlterada = configCancelamentoRestoFacade.recuperar(selecionado.getId());
        contaDespesa = selecionado.getConfigCancelamentoRestoContaDespesas().get(0).getContaDespesa();
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
                configCancelamentoRestoFacade.meuSalvar(configuracaoNaoAlterada, selecionado);
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
                redireciona();
            } catch (Exception e) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            }
        }
    }

    private void validaConfiguracaoContaDespesa() {
        if (selecionado.getConfigCancelamentoRestoContaDespesas().isEmpty()) {
            ConfigCancRestoContaDesp configCancRestoContaDesp = new ConfigCancRestoContaDesp();
            configCancRestoContaDesp.setConfigCancelamentoResto(selecionado);
            configCancRestoContaDesp.setContaDespesa(contaDespesa);
            selecionado.getConfigCancelamentoRestoContaDespesas().add(configCancRestoContaDesp);
        } else {
            getSelecionado().getConfigCancelamentoRestoContaDespesas().remove(0);
            ConfigCancRestoContaDesp configCancRestoContaDesp = new ConfigCancRestoContaDesp();
            configCancRestoContaDesp.setConfigCancelamentoResto(selecionado);
            configCancRestoContaDesp.setContaDespesa(contaDespesa);
            selecionado.getConfigCancelamentoRestoContaDespesas().add(configCancRestoContaDesp);
        }
    }

    private boolean validaCampos() {
        return Util.validaCampos(selecionado)
            && validaCampoConta()
            && verificaConfiguracaoExistente();
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

    public List<TipoEmpenhoEstorno> getTiposEmpenhoEstorno() {
        return Arrays.asList(TipoEmpenhoEstorno.values());
    }

    public List<PatrimonioLiquido> getTiposPatrimoLiquido() {
        return Arrays.asList(PatrimonioLiquido.values());
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        return configCancelamentoRestoFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.CANCELAMENTO_RESTO_PAGAR, selecionado.getTipoLancamento());
    }

    public List<Conta> completaContasDespesa(String parte) {
        return configCancelamentoRestoFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public void setaContaDespesa(SelectEvent evt) {
        contaDespesa = (Conta) evt.getObject();
    }

    public ConverterAutoComplete getConverterContaDesp() {
        if (this.converterContaDesp == null) {
            this.converterContaDesp = new ConverterAutoComplete(Conta.class, configCancelamentoRestoFacade.getContaFacade());
        }
        return this.converterContaDesp;
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configCancelamentoRestoFacade.getEventoContabilFacade());
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
        Integer ano = getSistemaControlador().getExercicioCorrente().getAno();
        if (dataInicioVigencia.get(Calendar.YEAR) != ano) {
            message.setSummary(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao());
            message.setDetail(" Ano de início de vigência está diferente do exercício corrente.");
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
            configCancelamentoRestoFacade.encerrarVigencia(selecionado);
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
        if (Util.getDataHoraMinutoSegundoZerado(selecionado.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(getSistemaControlador().getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean verificaConfiguracaoExistente() {
        Boolean controle = true;
        StringBuilder str = new StringBuilder();
        List<Conta> contas = new ArrayList<Conta>();
        if (configCancelamentoRestoFacade.hasConfiguracaoExistente(contaDespesa, selecionado.getTipoLancamento(), selecionado, getSistemaControlador().getDataOperacao(), selecionado.getTipoRestosProcessados(), selecionado.getCancelamentoPrescricao(), selecionado.getPatrimonioLiquido())) {
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
                + ", Cancelamento/Prescrição: " + selecionado.getCancelamentoPrescricao()
                + ", Patrimonio Líquido: " + selecionado.getPatrimonioLiquido()
                + " e Conta de Despesa: " + str);
            controle = false;
        }
        return controle;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public ConfigCancelamentoResto getConfiguracaoNaoAlterada() {
        return configuracaoNaoAlterada;
    }

    public void setConfiguracaoNaoAlterada(ConfigCancelamentoResto configuracaoNaoAlterada) {
        this.configuracaoNaoAlterada = configuracaoNaoAlterada;
    }

    public void setaEventoNull() {
        selecionado.setEventoContabil(null);
    }
}




