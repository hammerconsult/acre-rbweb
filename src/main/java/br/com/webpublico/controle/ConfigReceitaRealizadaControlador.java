/*
 * Codigo gerado automaticamente em Wed Oct 10 11:09:05 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TiposCredito;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigReceitaRealizadaFacade;
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

@ManagedBean(name = "configReceitaRealizadaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-receita-realizada", pattern = "/configuracao-receita-realizada/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoreceitarealizada/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-receita-realizada", pattern = "/configuracao-receita-realizada/editar/#{configReceitaRealizadaControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoreceitarealizada/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-receita-realizada", pattern = "/configuracao-receita-realizada/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoreceitarealizada/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-receita-realizada", pattern = "/configuracao-receita-realizada/ver/#{configReceitaRealizadaControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoreceitarealizada/visualizar.xhtml")
})

public class ConfigReceitaRealizadaControlador extends ConfigEventoSuperControlador<ConfigReceitaRealizada> implements Serializable, CRUD {

    @EJB
    private ConfigReceitaRealizadaFacade configReceitaRealizadaFacade;
    private String palavra;
    private List<Conta> listConta;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Conta[] arrayConta;
    private ConverterAutoComplete converterEventoContabil;
    private ConverterAutoComplete converterConta;
    private TiposCredito tiposCredito;
    private Date dataVigencia;
    private ModoListagem modoListagem;
    private List<ConfigReceitaRealizada> listaConfigReceitaRealizada;
    private ConfigReceitaRealizada configReceitaRealizada;
    private ConfigReceitaRealizada configRecRealizadaNaoAlterado;
    private ContaReceita contaReceita;

    public ConfigReceitaRealizadaControlador() {
        super(ConfigReceitaRealizada.class);
    }

    public List<Conta> completaContasReceita(String parte) {
        return configReceitaRealizadaFacade.getContaFacade().listaFiltrandoReceita(parte, sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(ContaReceita.class, configReceitaRealizadaFacade.getContaFacade());
        }
        return converterConta;
    }

    public ConfigReceitaRealizadaFacade getFacade() {
        return configReceitaRealizadaFacade;
    }

    public ConfigReceitaRealizada getConfigReceitaRealizada() {
        return configReceitaRealizada;
    }

    public void setConfigReceitaRealizada(ConfigReceitaRealizada configReceitaRealizada) {
        this.configReceitaRealizada = configReceitaRealizada;
    }

    public ConfigReceitaRealizada getConfigRecRealizadaNaoAlterado() {
        return configRecRealizadaNaoAlterado;
    }

    public void setConfigRecRealizadaNaoAlterado(ConfigReceitaRealizada configRecRealizadaNaoAlterado) {
        this.configRecRealizadaNaoAlterado = configRecRealizadaNaoAlterado;
    }

    @Override
    public AbstractFacade getFacede() {
        return configReceitaRealizadaFacade;
    }

    @URLAction(mappingId = "novo-configuracao-receita-realizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        listConta = new ArrayList<Conta>();
    }

    @URLAction(mappingId = "ver-configuracao-receita-realizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-receita-realizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void definirTipoContaReceita() {
        if (contaReceita != null) {
            tiposCredito = contaReceita.getTiposCredito();
        }
    }

    public void limpaCampos() {
        palavra = "";
        listConta = new ArrayList<Conta>();
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<>();
        for (TipoLancamento tipo : TipoLancamento.values()) {
            lista.add(tipo);
        }
        return lista;
    }

    public List<SelectItem> getListaOperacoesReceitaRealizada() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoReceita op : OperacaoReceita.values()) {
            toReturn.add(new SelectItem(op, op.getDescricao()));
        }
        return toReturn;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        ConfigReceitaRealizada cr = ((ConfigReceitaRealizada) selecionado);
        return configReceitaRealizadaFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.RECEITA_REALIZADA, cr.getTipoLancamento());

    }

    public List<Conta> filtrarContas() {
        listConta = configReceitaRealizadaFacade.getContaFacade().listaFiltrandoReceitaPorTipoCredito(palavra.trim(), tiposCredito, sistemaControlador.getExercicioCorrente());
        return listConta;
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (converterEventoContabil == null) {
            converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configReceitaRealizadaFacade.getEventoContabilFacade());
        }
        return converterEventoContabil;
    }

    public void removeOrigem(ActionEvent evt) {
        ConfigReceitaRealizada cr = ((ConfigReceitaRealizada) selecionado);
        ConfigRecRealizadaContaRec crrcr = (ConfigRecRealizadaContaRec) evt.getComponent().getAttributes().get("objeto");
        cr.getConfigRecRealizadaContaRecs().remove(crrcr);
    }

    public void addAllContas() {
        ConfigReceitaRealizada cr = ((ConfigReceitaRealizada) selecionado);
        if (arrayConta.length <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "ATENÇÃO", "Selecione pelo menos 1 conta"));
        } else {
            for (int i = 0; i < arrayConta.length; i++) {
                ConfigRecRealizadaContaRec configRecRealizadaContaRec = new ConfigRecRealizadaContaRec();
                configRecRealizadaContaRec.setContaReceita(arrayConta[i]);
                configRecRealizadaContaRec.setConfigReceitaRealizada(cr);
                cr.getConfigRecRealizadaContaRecs().add(configRecRealizadaContaRec);
            }
        }
    }

    public String retornaLabel(Conta cc) {
        ConfigReceitaRealizada cr = ((ConfigReceitaRealizada) selecionado);
        String s = "";
        for (ConfigRecRealizadaContaRec c : cr.getConfigRecRealizadaContaRecs()) {
            TiposCredito retornaTipoCredito = configReceitaRealizadaFacade.retornaTipoCredito(cc);
            s = retornaTipoCredito.getDescricao();
        }
        return s;
    }

    public boolean verificarConfiguracaoExistente() {
        ConfigReceitaRealizada cr = ((ConfigReceitaRealizada) selecionado);
        Boolean controle = true;
        StringBuilder str = new StringBuilder();
        List<Conta> contas = new ArrayList<Conta>();
        if (configReceitaRealizadaFacade.verificarContaExistente(contaReceita, cr.getTipoLancamento(), cr, sistemaControlador.getDataOperacao(), cr.getOperacaoReceitaRealizada())) {
            contas.add(contaReceita);
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
                " Já existe uma configuração vigente para a combinação: "
                    + " Operação: " + cr.getOperacaoReceitaRealizada().getDescricao()
                    + ", Tipo Lançamento: " + cr.getTipoLancamento()
                    + " e  Conta de Receita: " + str));
            controle = false;
        }
        return controle;
    }

    public void recuperaEditaVer() {
        configReceitaRealizada = configReceitaRealizadaFacade.recuperar(selecionado.getId());
        configRecRealizadaNaoAlterado = configReceitaRealizadaFacade.recuperar(selecionado.getId());
        selecionado = configReceitaRealizada;
        contaReceita = (ContaReceita) selecionado.getConfigRecRealizadaContaRecs().get(0).getContaReceita();
        definirTipoContaReceita();
    }

    @Override
    public void salvar() {
        ConfigReceitaRealizada cr = ((ConfigReceitaRealizada) selecionado);
        if (Util.validaCampos(cr) && validaCampoContaEVigencia() && verificarConfiguracaoExistente()) {
            try {
                if (selecionado.getConfigRecRealizadaContaRecs().isEmpty()) {
                    ConfigRecRealizadaContaRec configRecRealizadaContaRec = new ConfigRecRealizadaContaRec();
                    configRecRealizadaContaRec.setConfigReceitaRealizada(selecionado);
                    configRecRealizadaContaRec.setContaReceita(contaReceita);
                    selecionado.getConfigRecRealizadaContaRecs().add(configRecRealizadaContaRec);
                } else {
                    getSelecionado().getConfigRecRealizadaContaRecs().remove(0);
                    ConfigRecRealizadaContaRec configRecRealizadaContaRec = new ConfigRecRealizadaContaRec();
                    configRecRealizadaContaRec.setConfigReceitaRealizada(selecionado);
                    configRecRealizadaContaRec.setContaReceita(contaReceita);
                    selecionado.getConfigRecRealizadaContaRecs().add(configRecRealizadaContaRec);
                }
                configReceitaRealizadaFacade.meuSalvar(configRecRealizadaNaoAlterado, cr);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                redireciona();
            } catch (Exception e) {
                logger.error(e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Problema ao salvar!" + e.getMessage(), "Problema ao salvar!" + e.getMessage()));
                return;
            }
        } else {
            return;
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

    public void encerrarVigencia() {
        try {
            configReceitaRealizadaFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public boolean validaCampoContaEVigencia() {
        if (contaReceita == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Conta de Receita é obrigatório."));
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


    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public List<ModoListagem> getListaModoListagem() {
        List<ModoListagem> lista = new ArrayList<ModoListagem>();
        lista.addAll(Arrays.asList(ModoListagem.values()));
        return lista;
    }

    public ConfigReceitaRealizada esteSelecionado() {
        return (ConfigReceitaRealizada) selecionado;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
    }

    public boolean podeEditar() {
        ConfigReceitaRealizada config = (ConfigReceitaRealizada) selecionado;
        if (config.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(config.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public ConfigReceitaRealizadaControlador.ModoListagem getModoListagem() {
        return modoListagem;
    }

    public void setModoListagem(ConfigReceitaRealizadaControlador.ModoListagem modoListagem) {
        this.modoListagem = modoListagem;
    }

    public Date getDataVigencia() {
        return dataVigencia;
    }

    public void setDataVigencia(Date dataVigencia) {
        this.dataVigencia = dataVigencia;
    }

    public List<Conta> getListConta() {
        if (listConta == null) {
            listConta = new ArrayList<Conta>();
        }
        return listConta;
    }

    public void setListConta(List<Conta> listConta) {
        this.listConta = listConta;
    }

    public Conta[] getArrayConta() {
        return arrayConta;
    }

    public void setArrayConta(Conta[] arrayConta) {
        this.arrayConta = arrayConta;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public List<ConfigReceitaRealizada> getListaConfigReceitaRealizada() {
        return listaConfigReceitaRealizada;
    }

    public void setListaConfigReceitaRealizada(List<ConfigReceitaRealizada> listaConfigReceitaRealizada) {
        this.listaConfigReceitaRealizada = listaConfigReceitaRealizada;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-receita-realizada/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public enum ModoListagem {

        VIGENTE("Vigente"),
        ENCERRADO("Encerrado");
        private String descricao;

        private ModoListagem(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
