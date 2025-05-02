/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigPagamentoFacade;
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
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "configPagamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-pagamento", pattern = "/configuracao-pagamento/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaopagamento/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-pagamento", pattern = "/configuracao-pagamento/editar/#{configPagamentoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaopagamento/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-pagamento", pattern = "/configuracao-pagamento/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaopagamento/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-pagamento", pattern = "/configuracao-pagamento/ver/#{configPagamentoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaopagamento/visualizar.xhtml")
})
public class ConfigPagamentoControlador extends PrettyControlador<ConfigPagamento> implements Serializable, CRUD {

    @EJB
    private ConfigPagamentoFacade configPagamentoFacade;
    @EJB
    private ContaFacade contaFacade;
    private ConverterAutoComplete converterEventoContabil;
    private TipoContaDespesa tipoContaDespesa;
    private List<Conta> listaDeContas;
    private Conta[] arrayDeContas;
    private String palavra;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConfigPagamento configPagamentoNaoAlterado;
    private Conta contaDespesa;
    private ConverterAutoComplete converterConta;

    public ConfigPagamentoControlador() {
        super(ConfigPagamento.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configPagamentoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-pagamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-configuracao-pagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-pagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
        configPagamentoNaoAlterado = (ConfigPagamento) getFacede().recuperar(super.getId());
    }

    @URLAction(mappingId = "ver-configuracao-pagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    public void limpaCampos() {
        palavra = "";
        tipoContaDespesa = null;
        listaDeContas = new ArrayList<Conta>();
    }

    public List<Conta> getListaDeContas() {
        return listaDeContas;
    }

    private void recuperaEditarVer() {
        contaDespesa = selecionado.getConfigPagamentoContaDespesas().get(0).getContaDespesa();
    }

    public List<Conta> completaContasDespesa(String parte) {
        return contaFacade.listaFiltrandoContaDespesa(parte.trim(), sistemaControlador.getExercicioCorrente());
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

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<SelectItem> getListaTipoContaDespesa() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoContaDespesa contaDespesa : TipoContaDespesa.values()) {
            lista.add(new SelectItem(contaDespesa, contaDespesa.toString()));
        }
        return lista;
    }

    public SelectItem[] tiposContaDespesaView() {
        SelectItem[] opcoes = new SelectItem[TipoContaDespesa.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TipoContaDespesa tipo : TipoContaDespesa.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.getDescricao());
            i++;
        }
        return opcoes;
    }

    public void removeOrigem(ActionEvent evt) {

        ConfigPagamentoContaDesp configPagamento = (ConfigPagamentoContaDesp) evt.getComponent().getAttributes().get("objeto");
        selecionado.getConfigPagamentoContaDespesas().remove(configPagamento);
    }

    public List<Conta> filtrarContas() {
        listaDeContas = configPagamentoFacade.getContaFacade().listaFiltrandoContaDespesa(palavra, tipoContaDespesa, sistemaControlador.getExercicioCorrente());
        return listaDeContas;
    }

    public void addAllContas() {

        if (arrayDeContas.length <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "ATENÇÃO", "Selecione pelo menos 1 conta"));
        } else {
            for (int i = 0; i < arrayDeContas.length; i++) {
                if (podeAdicionarConta(arrayDeContas[i])) {
                    ConfigPagamentoContaDesp configPagamentoContaDesp = new ConfigPagamentoContaDesp();
                    configPagamentoContaDesp.setContaDespesa(arrayDeContas[i]);
                    configPagamentoContaDesp.setConfigPagamento(selecionado);
                    selecionado.getConfigPagamentoContaDespesas().add(configPagamentoContaDesp);
                } else {
                    FacesUtil.addError("Não foi possível adicionar  Conta.", " A conta " + arrayDeContas[i] + " já foi adicionada na lista.");
                }
            }
        }
    }

    private boolean podeAdicionarConta(Conta conta) {
        for (ConfigPagamentoContaDesp configPagamentoContaDesp : selecionado.getConfigPagamentoContaDespesas()) {
            if (configPagamentoContaDesp.getContaDespesa().equals(conta)) {
                return false;
            }
        }
        return true;
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
            configPagamentoFacade.encerrarVigencia(selecionado);
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

    public boolean verificaConfiguracaoExistente() {

        Boolean controle = true;
        StringBuilder str = new StringBuilder();
        List<Conta> contas = new ArrayList<Conta>();

//        for (ConfigPagamentoContaDesp objeto : selecionado.getConfigPagamentoContaDespesas()) {
        if (configPagamentoFacade.verificaContaExistente(contaDespesa, selecionado.getTipoLancamento(), selecionado, sistemaControlador.getDataOperacao())) {
            contas.add(contaDespesa);
        }
//        }
        if (!contas.isEmpty()) {
            String concatenacao = "";
            for (Conta c : contas) {
                str.append(concatenacao).append(c.getCodigo());
                concatenacao = " - ";
                str.append(concatenacao).append(c.getDescricao());
                concatenacao = " ; ";
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                " Já existe uma configuração vigente para a combinação: "
                    + " Tipo Lançamento: " + selecionado.getTipoLancamento()
                    + ", Tipo de Conta de Despesa: " + selecionado.getTipoContaDespesa()
                    + " e a Conta de Despesa: " + str));
            controle = false;
        }
        return controle;
    }

    public boolean validaCampos() {
        if (selecionado.getInicioVigencia() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Início de Vigência é obrigatório."));
            return false;
        }
        if (selecionado.getEventoContabil() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Evento Contábil é obrigatório."));
            return false;
        }
        if (contaDespesa == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Conta de Despesa é obrigatório."));
            return false;
        } else if (selecionado.getTipoContaDespesa() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Tipo de Conta é obrigatório."));
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

    @Override
    public void salvar() {
        if (validaCampos() && verificaConfiguracaoExistente()) {
            try {
                if (selecionado.getConfigPagamentoContaDespesas().isEmpty()) {
                    ConfigPagamentoContaDesp configPagamentoContaDesp = new ConfigPagamentoContaDesp();
                    configPagamentoContaDesp.setConfigPagamento(selecionado);
                    configPagamentoContaDesp.setContaDespesa(contaDespesa);
                    selecionado.getConfigPagamentoContaDespesas().add(configPagamentoContaDesp);
                } else {
                    getSelecionado().getConfigPagamentoContaDespesas().remove(0);
                    ConfigPagamentoContaDesp configPagamentoContaDesp = new ConfigPagamentoContaDesp();
                    configPagamentoContaDesp.setConfigPagamento(selecionado);
                    configPagamentoContaDesp.setContaDespesa(contaDespesa);
                    selecionado.getConfigPagamentoContaDespesas().add(configPagamentoContaDesp);
                }
                configPagamentoFacade.meuSalvar(configPagamentoNaoAlterado, selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, " Operação Realizada! ", " Registro salvo com sucesso."));
                redireciona();
            } catch (Exception e) {
                logger.debug(e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Problema ao salvar!" + e.getMessage(), "Problema ao salvar!" + e.getMessage()));
                return;
            }
        } else {
            return;
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

    public List<EventoContabil> completaEventoContabil(String parte) {
        return configPagamentoFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.PAGAMENTO_DESPESA, selecionado.getTipoLancamento());
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configPagamentoFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public void setaNullEvento() {
        this.selecionado.setEventoContabil(null);
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ConfigPagamento getConfigPagamentoNaoAlterado() {
        return configPagamentoNaoAlterado;
    }

    public void setConfigPagamentoNaoAlterado(ConfigPagamento configPagamentoNaoAlterado) {
        this.configPagamentoNaoAlterado = configPagamentoNaoAlterado;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterConta;
    }
}
