package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigLiberacaoFinanceira;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigLiberacaoFinanceiraFacade;
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
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 13/02/14
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "configLiberacaoFinanceiraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-liberacao-financeira", pattern = "/configuracao-liberacao-financeira/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configliberacaofinanceira/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-liberacao-financeira", pattern = "/configuracao-liberacao-financeira/editar/#{configLiberacaoFinanceiraControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configliberacaofinanceira/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-liberacao-financeira", pattern = "/configuracao-liberacao-financeira/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configliberacaofinanceira/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-liberacao-financeira", pattern = "/configuracao-liberacao-financeira/ver/#{configLiberacaoFinanceiraControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configliberacaofinanceira/visualizar.xhtml")
})

public class ConfigLiberacaoFinanceiraControlador extends ConfigEventoSuperControlador<ConfigLiberacaoFinanceira> implements Serializable, CRUD {

    @EJB
    private ConfigLiberacaoFinanceiraFacade configLiberacaoFinanceiraFacade;
    private ConverterAutoComplete converterEventoContabil;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    private ConfigLiberacaoFinanceira configuracaoLiberacapFinanceiraNaoAlterada;

    public ConfigLiberacaoFinanceiraControlador() {
        super(ConfigLiberacaoFinanceira.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configLiberacaoFinanceiraFacade;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-liberacao-financeira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novo-configuracao-liberacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new ConfigLiberacaoFinanceira();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-liberacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-configuracao-liberacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }


    public ConverterAutoComplete getConverterEventoContabil() {
        if (converterEventoContabil == null) {
            converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configLiberacaoFinanceiraFacade.getEventoContabilFacade());
        }
        return converterEventoContabil;
    }

    public void validaDataVigencia(FacesContext context, UIComponent component, Object value) {
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
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (dataVigencia.compareTo(selecionado.getFimVigencia()) >= 0) {
                message.setSummary("Data invalida! ");
                message.setDetail("Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ".  Para editar a configuração, a data de Início de Vigência não pode ser maior que a data Fim de Vigência.");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> toReturn = new ArrayList<>();
        toReturn.addAll(Arrays.asList(TipoLancamento.values()));
        return toReturn;
    }

    public void recuperaEditaVer() {
        selecionado = configLiberacaoFinanceiraFacade.recuperar(selecionado.getId());
        configuracaoLiberacapFinanceiraNaoAlterada = configLiberacaoFinanceiraFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado) && validaEditaVigencia()) {

                ConfigLiberacaoFinanceira configuracaoEncontrada = configLiberacaoFinanceiraFacade.verificaConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação, Tipo Lançamento: " + selecionado.getTipoLancamento() +
                            ", Operação: " + selecionado.getOperacao().getDescricao() +
                            ", Dependência da Execução Orçamentária: " + selecionado.getResultanteIndependente().getDescricao() +
                            " e Tipo de Transferência: " + selecionado.getTipoTransferencia()));
                    return;
                }
                try {
                    configLiberacaoFinanceiraFacade.meuSalvar(configuracaoLiberacapFinanceiraNaoAlterada, selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso. "));
                    redireciona();

                } catch (Exception e) {
                    logger.debug(e.getMessage());
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Salvar!", e.getMessage()));
                    return;
                }
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Operação não Realizada! ", ex.getMessage()));
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

    public List<SelectItem> getTipoTransferenciaFinanceira() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoLiberacaoFinanceira ttf : TipoLiberacaoFinanceira.values()) {
            toReturn.add(new SelectItem(ttf, ttf.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getOrigemTipoTransferencia() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (OrigemTipoTransferencia ott : OrigemTipoTransferencia.values()) {
            toReturn.add(new SelectItem(ott, ott.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoResultanteIndependente() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (ResultanteIndependente obj : ResultanteIndependente.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public void encerrarVigencia() {
        try {
            configLiberacaoFinanceiraFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }


    public List<EventoContabil> completaEventoContabil(String parte) {
        if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configLiberacaoFinanceiraFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.LIBERACAO_FINANCEIRA, TipoLancamento.NORMAL);
        } else {
            return configLiberacaoFinanceiraFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.LIBERACAO_FINANCEIRA, TipoLancamento.ESTORNO);
        }
    }

    public void setaNullEvento() {
        selecionado.setEventoContabil(null);
        selecionado.setOperacao(null);
        selecionado.setTipoTransferencia(null);
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
