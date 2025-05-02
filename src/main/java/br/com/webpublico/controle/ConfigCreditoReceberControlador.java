package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigCreditoReceber;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.OperacaoCreditoReceber;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigCreditoReceberFacade;
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
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.*;

/**
 * @author Fabio
 */
@ManagedBean(name = "configCreditoReceberControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-credito-receber", pattern = "/configuracao-credito-receber/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaocreditoreceber/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-credito-receber", pattern = "/configuracao-credito-receber/editar/#{configCreditoReceberControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaocreditoreceber/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-credito-receber", pattern = "/configuracao-credito-receber/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaocreditoreceber/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-credito-receber", pattern = "/configuracao-credito-receber/ver/#{configCreditoReceberControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaocreditoreceber/visualizar.xhtml")
})

public class ConfigCreditoReceberControlador extends ConfigEventoSuperControlador<ConfigCreditoReceber> implements Serializable, CRUD {

    @EJB
    private ConfigCreditoReceberFacade configCreditoReceberFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ConverterAutoComplete converterContaReceita;
    private ConfigCreditoReceber configCreditoReceberNaoAlterado;

    public ConfigCreditoReceberControlador() {
        super(ConfigCreditoReceber.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configCreditoReceberFacade;
    }

    @URLAction(mappingId = "novo-configuracao-credito-receber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "ver-configuracao-credito-receber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "edita-configuracao-credito-receber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-credito-receber/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void recuperarEditarVer() {
        configCreditoReceberNaoAlterado = configCreditoReceberFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)
                && validarConfiguracaoExistente()
                && validarEditarVigencia()) {
                configCreditoReceberFacade.meuSalvar(configCreditoReceberNaoAlterado, selecionado);
                FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
                redireciona();
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Detalhes do erro: " + e.getMessage() + ". Se o problema persistir contate o suporte.");
        }
    }

    private boolean validarConfiguracaoExistente() {
        ConfigCreditoReceber configuracaoEncontrada = configCreditoReceberFacade.verificarConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
        if (configuracaoEncontrada != null
            && configuracaoEncontrada.getId() != null) {
            FacesUtil.addOperacaoNaoPermitida("Configuração vigente encontrada para o "
                + " Tipo Lançamento: " + selecionado.getTipoLancamento() + ", "
                + " Operação: " + selecionado.getOperacaoCreditoReceber() + " e "
                + " Conta de Receita: " + selecionado.getContaReceita());
            return false;
        }
        return true;
    }

    public boolean validarEditarVigencia() {
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesUtil.addOperacaoNaoPermitida("Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ".  Para editar a configuração, a data de Início de Vigência não pode ser maior que a data Fim de Vigência.");
                return false;
            }
        }
        return true;
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> toReturn = new ArrayList<>();
        toReturn.addAll(Arrays.asList(TipoLancamento.values()));
        return toReturn;
    }

    public List<OperacaoCreditoReceber> getListaOperacaoDividaAtiva() {
        List<OperacaoCreditoReceber> lista = new ArrayList<OperacaoCreditoReceber>();
        for (OperacaoCreditoReceber op : OperacaoCreditoReceber.values()) {
            if (!op.equals(OperacaoCreditoReceber.RECEBIMENTO)) {
                lista.add(op);
            }
        }
        return lista;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        return configCreditoReceberFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.CREDITO_RECEBER, selecionado.getTipoLancamento());
    }

    public List<Conta> completaContaReceita(String parte) {
        return configCreditoReceberFacade.getContaFacade().listaFiltrandoContaReceitaPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configCreditoReceberFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public ConverterAutoComplete getConverterContaReceita() {
        if (this.converterContaReceita == null) {
            this.converterContaReceita = new ConverterAutoComplete(Conta.class, configCreditoReceberFacade.getContaFacade());
        }
        return this.converterContaReceita;
    }

    public void validarInicioVigencia(FacesContext context, UIComponent component, Object value) {
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
            configCreditoReceberFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void definirEventoContabilComoNull() {
        selecionado.setEventoContabil(null);
    }

    public void definirTipoContaReceita() {
        if (selecionado.getContaReceita() != null) {
            selecionado.setTiposCredito(selecionado.getContaReceita().getTiposCredito());
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

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
