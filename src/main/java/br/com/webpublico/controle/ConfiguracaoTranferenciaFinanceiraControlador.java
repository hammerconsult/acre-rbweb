/*
 * Codigo gerado automaticamente em Wed Dec 12 10:09:56 AMT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoTranferenciaFinanceira;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoTranferenciaFinanceiraFacade;
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

@ManagedBean(name = "configuracaoTranferenciaFinanceiraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-transferencia-financeira", pattern = "/configuracao-transferencia-financeira/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaotransferenciafinanceira/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-transferencia-financeira", pattern = "/configuracao-transferencia-financeira/editar/#{configuracaoTranferenciaFinanceiraControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaotransferenciafinanceira/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-transferencia-financeira", pattern = "/configuracao-transferencia-financeira/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaotransferenciafinanceira/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-transferencia-financeira", pattern = "/configuracao-transferencia-financeira/ver/#{configuracaoTranferenciaFinanceiraControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaotransferenciafinanceira/visualizar.xhtml")
})
public class ConfiguracaoTranferenciaFinanceiraControlador extends ConfigEventoSuperControlador<ConfiguracaoTranferenciaFinanceira> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoTranferenciaFinanceiraFacade configuracaoTranferenciaFinanceiraFacade;
    private ConverterAutoComplete converterEventoContabil;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConfiguracaoTranferenciaFinanceira configuracaoTranferenciaFinanceiraNaoAlterada;
    private ConfiguracaoTranferenciaFinanceira configuracaoTranferenciaFinanceira;

    public ConfiguracaoTranferenciaFinanceiraControlador() {
        super(ConfiguracaoTranferenciaFinanceira.class);
    }

    public ConfiguracaoTranferenciaFinanceiraFacade getFacade() {
        return configuracaoTranferenciaFinanceiraFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoTranferenciaFinanceiraFacade;
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (converterEventoContabil == null) {
            converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configuracaoTranferenciaFinanceiraFacade.getEventoContabilFacade());
        }
        return converterEventoContabil;
    }


    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @URLAction(mappingId = "novo-configuracao-transferencia-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ConfiguracaoTranferenciaFinanceira conf = ((ConfiguracaoTranferenciaFinanceira) selecionado);
        conf.setTipoLancamento(TipoLancamento.NORMAL);
        conf.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-transferencia-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-configuracao-transferencia-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> toReturn = new ArrayList<>();
        toReturn.addAll(Arrays.asList(TipoLancamento.values()));
        return toReturn;
    }

    public void recuperaEditaVer() {
        configuracaoTranferenciaFinanceira = configuracaoTranferenciaFinanceiraFacade.recuperar(selecionado.getId());
        configuracaoTranferenciaFinanceiraNaoAlterada = configuracaoTranferenciaFinanceiraFacade.recuperar(selecionado.getId());
        selecionado = configuracaoTranferenciaFinanceira;
    }

    public List<SelectItem> getValoresResultanteIndependente() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (ResultanteIndependente ri : ResultanteIndependente.values()) {
            lista.add(new SelectItem(ri, ri.getDescricao()));
        }
        return lista;
    }

    @Override
    public void salvar() {
        ConfiguracaoTranferenciaFinanceira transf = ((ConfiguracaoTranferenciaFinanceira) selecionado);

        try {
            if (Util.validaCampos(transf) && validaEditaVigencia()) {

                ConfiguracaoTranferenciaFinanceira configuracaoEncontrada = configuracaoTranferenciaFinanceiraFacade.verificaConfiguracaoExistente(transf, sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Existe uma configuração vigente para a combinação: "
                            + " Tipo Lançamento: " + transf.getTipoLancamento() + ", "
                            + " Operação: " + transf.getOperacao() + ", "
                            + " Dependência da Execução Orçamentária: " + transf.getResultanteIndependente() + " e "
                            + " Tipo de Transferência: " + transf.getTipoTransferencia()));
                    return;
                }
                try {
                    configuracaoTranferenciaFinanceiraFacade.meuSalvar(configuracaoTranferenciaFinanceiraNaoAlterada, transf);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                    redireciona();

                } catch (Exception e) {
                    logger.debug(e.getMessage());
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Salvar!", e.getMessage()));
                    return;
                }
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção de sistema", ex.getMessage()));
            return;
        }
        return;
    }

    public List<SelectItem> getTipoTransferenciaFinanceira() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoTransferenciaFinanceira ttf : TipoTransferenciaFinanceira.values()) {
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
            configuracaoTranferenciaFinanceiraFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
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


    public List<EventoContabil> completaEventoContabil(String parte) {
        ConfiguracaoTranferenciaFinanceira conf = ((ConfiguracaoTranferenciaFinanceira) selecionado);
        if (conf.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configuracaoTranferenciaFinanceiraFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_FINANCEIRA, TipoLancamento.NORMAL);
        } else {
            return configuracaoTranferenciaFinanceiraFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_FINANCEIRA, TipoLancamento.ESTORNO);
        }
    }

    public void valorLanc() {
        ConfiguracaoTranferenciaFinanceira conf = ((ConfiguracaoTranferenciaFinanceira) selecionado);
        conf.setEventoContabil(null);
    }

    public boolean podeEditar() {
        ConfiguracaoTranferenciaFinanceira conf = ((ConfiguracaoTranferenciaFinanceira) selecionado);
        if (conf.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(conf.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-transferencia-financeira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
