/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigPagamentoRestoPagarFacade;
import br.com.webpublico.negocios.ContaFacade;
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

@ManagedBean(name = "configPagamentoRestoPagarControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-cde-pagrestopagar", pattern = "/configuracao-pagamento-resto-pagar/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configpagamentorestopagar/edita.xhtml"),
    @URLMapping(id = "edita-cde-pagrestopagar", pattern = "/configuracao-pagamento-resto-pagar/editar/#{configPagamentoRestoPagarControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configpagamentorestopagar/edita.xhtml"),
    @URLMapping(id = "listar-cde-pagrestopagar", pattern = "/configuracao-pagamento-resto-pagar/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configpagamentorestopagar/lista.xhtml"),
    @URLMapping(id = "ver-cde-pagrestopagar", pattern = "/configuracao-pagamento-resto-pagar/ver/#{configPagamentoRestoPagarControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configpagamentorestopagar/visualizar.xhtml")
})
public class ConfigPagamentoRestoPagarControlador extends ConfigEventoSuperControlador<ConfigPagamentoRestoPagar> implements Serializable, CRUD {

    @EJB
    private ConfigPagamentoRestoPagarFacade configPagamentoRestoPagarFacade;
    @EJB
    private ContaFacade contaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ConfigPagamentoRestoPagar configPagamentRestoPagarNaoAlterado;
    private Conta contaDespesa;
    private ConverterAutoComplete converterConta;

    public ConfigPagamentoRestoPagarControlador() {
        super(ConfigPagamentoRestoPagar.class);
    }

    @URLAction(mappingId = "novo-cde-pagrestopagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setTipoRestosProcessados(TipoRestosProcessado.PROCESSADOS);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "ver-cde-pagrestopagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-cde-pagrestopagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        selecionado = configPagamentoRestoPagarFacade.recuperar(selecionado.getId());
        configPagamentRestoPagarNaoAlterado = configPagamentoRestoPagarFacade.recuperar(selecionado.getId());
        contaDespesa = selecionado.getConfigPagResPagContDesp().get(0).getContaDespesa();
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado) && validaCampos() && verificaConfiguracaoExistente()) {
            try {
                if (selecionado.getConfigPagResPagContDesp().isEmpty()) {
                    ConfigPagResPagContDesp configPagResPagContDesp = new ConfigPagResPagContDesp();
                    configPagResPagContDesp.setConfigPagamentoRestoPagar(selecionado);
                    configPagResPagContDesp.setContaDespesa(contaDespesa);
                    selecionado.getConfigPagResPagContDesp().add(configPagResPagContDesp);
                } else {
                    getSelecionado().getConfigPagResPagContDesp().remove(0);
                    ConfigPagResPagContDesp configPagResPagContDesp = new ConfigPagResPagContDesp();
                    configPagResPagContDesp.setConfigPagamentoRestoPagar(selecionado);
                    configPagResPagContDesp.setContaDespesa(contaDespesa);
                    selecionado.getConfigPagResPagContDesp().add(configPagResPagContDesp);
                }
                configPagamentoRestoPagarFacade.meusalvar(configPagamentRestoPagarNaoAlterado, selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                redireciona();
            } catch (Exception e) {
                logger.debug(e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Problema ao salvar!" + e.getMessage(), "Problema ao salvar!" + e.getMessage()));
            }
        }
    }


    public List<TipoContaDespesa> getTipoContas() {
        List<TipoContaDespesa> toReturn = new ArrayList<>();
        if (selecionado != null) {
            if (contaDespesa != null) {
                TipoContaDespesa tipo = ((ContaDespesa) contaDespesa).getTipoContaDespesa();
                if (!TipoContaDespesa.NAO_APLICAVEL.equals(tipo) && tipo != null) {
                    selecionado.setTipoContaDespesa(tipo);
                    toReturn.add(tipo);
                } else {
                    List<TipoContaDespesa> busca = contaFacade.buscarTiposContasDespesaNosFilhosDaConta(((ContaDespesa) contaDespesa));
                    if (!busca.isEmpty()) {
                        for (TipoContaDespesa tp : busca) {
                            if (!tp.equals(TipoContaDespesa.NAO_APLICAVEL)) {
                                toReturn.add(tp);
                            }
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    public boolean validaCampos() {
        if (contaDespesa == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Conta de Despesa é obrigatório."));
            return false;
        }
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Inválida! ", " Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ".  Para editar a configuração, a data de Início de Vigência não pode ser maior que a data Fim de Vigência."));
                return false;
            }
        }
        return true;
    }

    private Boolean verificaConfiguracaoExistente() {
        Boolean controle = true;
        StringBuilder str = new StringBuilder();
        List<Conta> contas = new ArrayList<Conta>();

        if (configPagamentoRestoPagarFacade.verificaConfiguracaoExistente(contaDespesa, selecionado.getTipoLancamento(), selecionado, selecionado.getEventoContabil(), sistemaControlador.getDataOperacao(), selecionado.getTipoContaDespesa(), selecionado.getTipoRestosProcessados())) {
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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente!  ",
                "Já existe uma configuração vigente para a combinação: "
                    + " Tipo de Lançamento: " + selecionado.getTipoLancamento()
                    + " ,Evento Contábil: " + selecionado.getEventoContabil()
                    + ", Tipo de Resto: " + selecionado.getTipoRestosProcessados()
                    + ", Conta de Despesa: " + str
                    + " e  Tipo de Despesa: " + selecionado.getTipoContaDespesa().getDescricao()));
            controle = false;
        }
        return controle;
    }

    public List<Conta> completaContasDespesa(String parte) {
        return contaFacade.listaFiltrandoContaDespesa(parte, sistemaControlador.getExercicioCorrente());
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<TipoRestosProcessado> getListaTipoRestosProcessado() {
        List<TipoRestosProcessado> lista = new ArrayList<TipoRestosProcessado>();
        lista.addAll(Arrays.asList(TipoRestosProcessado.values()));
        return lista;
    }

    public ConfigPagamentoRestoPagar esteSelecionado() {
        return (ConfigPagamentoRestoPagar) selecionado;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
        esteSelecionado().setTipoRestosProcessados(null);
        contaDespesa = null;
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configPagamentoRestoPagarFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterConta;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        return configPagamentoRestoPagarFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.PAGAMENTO_RESTO_PAGAR, selecionado.getTipoLancamento());
    }

    public void encerrarVigencia() {
        try {
            configPagamentoRestoPagarFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
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

    @Override
    public AbstractFacade getFacede() {
        return configPagamentoRestoPagarFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-pagamento-resto-pagar/";
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

    public ConfigPagamentoRestoPagar getConfigPagamentRestoPagarNaoAlterado() {
        return configPagamentRestoPagarNaoAlterado;
    }

    public void setConfigPagamentRestoPagarNaoAlterado(ConfigPagamentoRestoPagar configPagamentRestoPagarNaoAlterado) {
        this.configPagamentRestoPagarNaoAlterado = configPagamentRestoPagarNaoAlterado;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }
}
