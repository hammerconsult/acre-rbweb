package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigAjusteDeposito;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoAjuste;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigAjusteDepositoFacade;
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
 * @author Major
 */
@ManagedBean(name = "configAjusteDepositoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-ajuste-deposito", pattern = "/configuracao-ajuste-deposito/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoajustedeposito/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-ajuste-deposito", pattern = "/configuracao-ajuste-deposito/editar/#{configAjusteDepositoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoajustedeposito/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-ajuste-deposito", pattern = "/configuracao-ajuste-deposito/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoajustedeposito/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-ajuste-deposito", pattern = "/configuracao-ajuste-deposito/ver/#{configAjusteDepositoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoajustedeposito/visualizar.xhtml")
})
public class ConfigAjusteDepositoControlador extends ConfigEventoSuperControlador<ConfigAjusteDeposito> implements Serializable, CRUD {

    @EJB
    private ConfigAjusteDepositoFacade configAjusteDepositoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ModoListagem modoListagem;
    private ConfigAjusteDeposito configAjusteDeposito;
    private ConfigAjusteDeposito configAjusDepNaoAlterado;

    public ConfigAjusteDepositoControlador() {
        super(ConfigAjusteDeposito.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configAjusteDepositoFacade;
    }

    public ConfigAjusteDeposito esteSelecionado() {
        return (ConfigAjusteDeposito) selecionado;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
        esteSelecionado().setTipoAjuste(null);
        esteSelecionado().setTipoContaExtraorcamentaria(null);
    }

    public void recuperaEditaVer() {
        configAjusteDeposito = configAjusteDepositoFacade.recuperar(selecionado.getId());
        configAjusDepNaoAlterado = configAjusteDepositoFacade.recuperar(selecionado.getId());
        selecionado = configAjusteDeposito;
    }

    //    public void excluirSelecionado(){
//        ConfigAjusteDeposito config = (ConfigAjusteDeposito) selecionado;
//        configAjusteDepositoFacade.remover(config);
//        carregaLista();
//    }
//
    @URLAction(mappingId = "novo-configuracao-ajuste-deposito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "ver-configuracao-ajuste-deposito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-ajuste-deposito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @Override
    public void salvar() {
        ConfigAjusteDeposito cad = ((ConfigAjusteDeposito) selecionado);
        try {
            if (Util.validaCampos(cad) && validaEditarVigencia()) {
                ConfigAjusteDeposito configuracaoEncontrada = configAjusteDepositoFacade.verificaConfiguracaoExistente(((ConfigAjusteDeposito) selecionado), sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação: "
                            + " Tipo Lançamento: " + cad.getTipoLancamento() + ", "
                            + " Tipo de Ajuste: " + cad.getTipoAjuste() + " e "
                            + " Tipo de Conta Extra: " + cad.getTipoContaExtraorcamentaria()));
                    return;
                }
                try {
                    configAjusteDepositoFacade.meuSalvar(configAjusDepNaoAlterado, cad);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada!", " Registro salvo com sucesso."));
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

    public boolean validaEditarVigencia() {
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Inválida! ", " Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ".  Para editar a configuração, a data de Início de Vigência não pode ser maior que a data Fim de Vigência."));
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

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<SelectItem> getListaTipoAjuste() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAjuste tp : TipoAjuste.values()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
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
        ConfigAjusteDeposito conf = ((ConfigAjusteDeposito) selecionado);
        if (conf.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configAjusteDepositoFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.AJUSTE_DEPOSITO, TipoLancamento.NORMAL);
        } else {
            return configAjusteDepositoFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.AJUSTE_DEPOSITO, TipoLancamento.ESTORNO);
        }
    }

    public boolean podeEditar() {
        ConfigAjusteDeposito config = (ConfigAjusteDeposito) selecionado;
        if (config.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(config.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void encerrarVigencia() {
        try {
            configAjusteDepositoFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configAjusteDepositoFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public ModoListagem getModoListagem() {
        return modoListagem;
    }

    public void setModoListagem(ModoListagem modoListagem) {
        this.modoListagem = modoListagem;
    }

    public ConfigAjusteDeposito getConfigAjusteDeposito() {
        return configAjusteDeposito;
    }

    public void setConfigAjusteDeposito(ConfigAjusteDeposito configAjusteDeposito) {
        this.configAjusteDeposito = configAjusteDeposito;
    }

    public ConfigAjusteDeposito getConfigAjusDepNaoAlterado() {
        return configAjusDepNaoAlterado;
    }

    public void setConfigAjusDepNaoAlterado(ConfigAjusteDeposito configAjusDepNaoAlterado) {
        this.configAjusDepNaoAlterado = configAjusDepNaoAlterado;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-ajuste-deposito/";
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
