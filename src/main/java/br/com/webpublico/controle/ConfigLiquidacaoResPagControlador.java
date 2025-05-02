/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigLiquidacaoResPagarFacade;
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
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "configLiquidacaoResPagarControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-liquidacao-resto-pagar", pattern = "/configuracao-liquidacao-resto-pagar/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configliquidacaorestopagar/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-liquidacao-resto-pagar", pattern = "/configuracao-liquidacao-resto-pagar/editar/#{configLiquidacaoResPagarControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configliquidacaorestopagar/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-liquidacao-resto-pagar", pattern = "/configuracao-liquidacao-resto-pagar/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configliquidacaorestopagar/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-liquidacao-resto-pagar", pattern = "/configuracao-liquidacao-resto-pagar/ver/#{configLiquidacaoResPagarControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configliquidacaorestopagar/visualizar.xhtml")
})
public class ConfigLiquidacaoResPagControlador extends ConfigEventoSuperControlador<ConfigLiquidacaoResPagar> implements Serializable, CRUD {

    @EJB
    private ConfigLiquidacaoResPagarFacade configLiquidacaoResPagarFacade;
    @EJB
    private ContaFacade contaFacade;
    private Conta contaDespesa;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterEventoContabil;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<Conta> listaDeContas;
    private Conta[] arrayDeContas;
    private String palavra;
    private TipoContaDespesa tipoContaDespesa;
    private ConfigLiquidacaoResPagar configLiquidacaoResPagarNaoAlterado;

    @URLAction(mappingId = "novo-configuracao-liquidacao-resto-pagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        selecionado.setSubTipoDespesa(SubTipoDespesa.NAO_APLICAVEL);
    }

    @URLAction(mappingId = "edita-configuracao-liquidacao-resto-pagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "ver-configuracao-liquidacao-resto-pagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    private void recuperaEditarVer() {
        selecionado = configLiquidacaoResPagarFacade.recuperar(selecionado.getId());
        contaDespesa = selecionado.getConfigLiqResPagContDesp().get(0).getContaDespesa();
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado) && validaCampos() && verificaConfiguracaoExistente()) {
            try {
                if (selecionado.getConfigLiqResPagContDesp().isEmpty()) {
                    ConfigLiqResPagContDesp configLiquidacaoResPagarContaDesp = new ConfigLiqResPagContDesp();
                    configLiquidacaoResPagarContaDesp.setConfigLiquidacaoResPagar(selecionado);
                    configLiquidacaoResPagarContaDesp.setContaDespesa(contaDespesa);
                    selecionado.getConfigLiqResPagContDesp().add(configLiquidacaoResPagarContaDesp);
                } else {
                    getSelecionado().getConfigLiqResPagContDesp().remove(0);
                    ConfigLiqResPagContDesp configLiquidacaoResPagContaDesp = new ConfigLiqResPagContDesp();
                    configLiquidacaoResPagContaDesp.setConfigLiquidacaoResPagar(selecionado);
                    configLiquidacaoResPagContaDesp.setContaDespesa(contaDespesa);
                    selecionado.getConfigLiqResPagContDesp().add(configLiquidacaoResPagContaDesp);
                }
                configLiquidacaoResPagarFacade.meuSalvar(configLiquidacaoResPagarNaoAlterado, selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                redireciona();
            } catch (Exception e) {
                logger.error(e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Problema ao salvar!" + e.getMessage(), "Problema ao salvar!" + e.getMessage()));
                return;
            }
        }
    }


    public boolean validaCampos() {
        if (contaDespesa == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo conta de despesa é obrigatório."));
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

    public boolean podeEditar() {
        ConfigLiquidacaoResPagar conf = ((ConfigLiquidacaoResPagar) selecionado);
        if (conf.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(conf.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean verificaConfiguracaoExistente() {

        Boolean controle = true;
        StringBuilder str = new StringBuilder();
        List<Conta> contas = new ArrayList<Conta>();

        if (configLiquidacaoResPagarFacade.verificaContaExistente(contaDespesa, selecionado.getTipoLancamento(), selecionado, sistemaControlador.getDataOperacao(), selecionado.getTipoReconhecimento())) {
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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                " Já existe uma configuração vigente para a combinação, Tipo Lançamento: " + selecionado.getTipoLancamento() +
                    ", Sub-Tipo de Despesa: " + selecionado.getSubTipoDespesa() + ", Tipo de Reconhecimento:  " + selecionado.getTipoReconhecimento().getDescricao() + " e Conta de Despesa: " + str));
            controle = false;
        }
        return controle;
    }

    public void encerrarVigencia() {
        try {
            configLiquidacaoResPagarFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
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

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<SubTipoDespesa> getSubTipoContas() {
        List<SubTipoDespesa> toReturn = new ArrayList<>();
        for (SubTipoDespesa std : SubTipoDespesa.values()) {
            toReturn.add(std);
        }
        return toReturn;
    }

    public List<SelectItem> getTiposReconhecimento() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem("", null));
        for (TipoReconhecimentoObrigacaoPagar tipo : TipoReconhecimentoObrigacaoPagar.retornaTipoReconhecimentosLiquidacao()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public void limpaCampos() {
        palavra = "";
        tipoContaDespesa = null;
        listaDeContas = new ArrayList<Conta>();
    }

    public List<Conta> filtrarContas() {
        listaDeContas = configLiquidacaoResPagarFacade.getContaFacade().listaFiltrandoContaDespesa(palavra, tipoContaDespesa, sistemaControlador.getExercicioCorrente());
        return listaDeContas;
    }

    public List<Conta> completaContasDespesa(String parte) {
        return contaFacade.listaFiltrandoContaDespesa(parte, sistemaControlador.getExercicioCorrente());
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        return configLiquidacaoResPagarFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR, selecionado.getTipoLancamento());
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterConta;
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configLiquidacaoResPagarFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public ConfigLiquidacaoResPagControlador() {
        super(ConfigLiquidacaoResPagar.class);
    }

    public void setaNullEvento() {
        this.selecionado.setEventoContabil(null);
        this.contaDespesa = null;
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

    public List<Conta> getListaDeContas() {
        return listaDeContas;
    }

    public void setListaDeContas(List<Conta> listaDeContas) {
        this.listaDeContas = listaDeContas;
    }

    public Conta[] getArrayDeContas() {
        return arrayDeContas;
    }

    public void setArrayDeContas(Conta[] arrayDeContas) {
        this.arrayDeContas = arrayDeContas;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    @Override
    public AbstractFacade getFacede() {
        return configLiquidacaoResPagarFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-liquidacao-resto-pagar/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
