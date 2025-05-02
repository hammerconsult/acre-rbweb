package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigProvMatematicaPrev;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigProvMatematicaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.DateSelectEvent;

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
 * @author Fabio
 */
@ManagedBean(name = "configProvMatematicaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-provisao-matematica", pattern = "/configuracao-provisao-matematica/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoprovisaomatematica/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-provisao-matematica", pattern = "/configuracao-provisao-matematica/editar/#{configProvMatematicaControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoprovisaomatematica/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-provisao-matematica", pattern = "/configuracao-provisao-matematica/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoprovisaomatematica/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-provisao-matematica", pattern = "/configuracao-provisao-matematica/ver/#{configProvMatematicaControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoprovisaomatematica/visualizar.xhtml")
})

public class ConfigProvMatematicaControlador extends ConfigEventoSuperControlador<ConfigProvMatematicaPrev> implements Serializable, CRUD {

    @EJB
    private ConfigProvMatematicaFacade configProvMatematicaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ModoListagem modoListagem;
    private Date dataVigencia;
    private List<ConfigProvMatematicaPrev> listaConfigProvMatematicaPrev;
    private ConfigProvMatematicaPrev configProvMatematicaPrevNaoAlterada;
    private ConfigProvMatematicaPrev configProvMatematicaPrev;

    @Override
    public AbstractFacade getFacede() {
        return configProvMatematicaFacade;
    }

    public ConfigProvMatematicaControlador() {
        super(ConfigProvMatematicaPrev.class);
    }

    public ModoListagem getModoListagem() {
        return modoListagem;
    }

    public void setModoListagem(ModoListagem modoListagem) {
        this.modoListagem = modoListagem;
    }

    public Date getDataVigencia() {
        return dataVigencia;
    }

    public void setDataVigencia(Date dataVigencia) {
        this.dataVigencia = dataVigencia;
    }

    public List<ConfigProvMatematicaPrev> getListaConfigProvMatematicaPrev() {
        return listaConfigProvMatematicaPrev;
    }

    @URLAction(mappingId = "novo-configuracao-provisao-matematica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new ConfigProvMatematicaPrev();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-provisao-matematica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recupaEditaVer();
    }

    @URLAction(mappingId = "ver-configuracao-provisao-matematica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recupaEditaVer();
    }

    public void carregaLista() {
        dataVigencia = null;
        modoListagem = ModoListagem.VIGENTE;
//        listagem();
    }

    public void recupaEditaVer() {
        configProvMatematicaPrev = configProvMatematicaFacade.recuperar(selecionado.getId());
        configProvMatematicaPrevNaoAlterada = configProvMatematicaFacade.recuperar(selecionado.getId());
        selecionado = configProvMatematicaPrev;
    }

    public ConfigProvMatematicaPrev esteSelecionado() {
        return (ConfigProvMatematicaPrev) selecionado;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
    }

    @Override
    public void salvar() {
        ConfigProvMatematicaPrev config = ((ConfigProvMatematicaPrev) selecionado);
        try {
            if (Util.validaCampos(config) && validaEditarDataVigencia()) {
                ConfigProvMatematicaPrev configuracaoEncontrada = configProvMatematicaFacade.verificaConfiguracaoExistente(((ConfigProvMatematicaPrev) selecionado), sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação: "
                            + " Tipo Lançamento: " + config.getTipoLancamento() + ", "
                            + " Tipo de Operação: " + config.getTipoOperacaoAtuarial() + ", "
                            + " Tipo de Plano: " + config.getTipoPlano() + " e "
                            + " Tipo de Provisão: " + config.getTipoProvisao()));
                    return;
                }
                try {
                    configProvMatematicaFacade.meuSalvar(configProvMatematicaPrevNaoAlterada, config);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
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


    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<SelectItem> getListaTipoOperacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoOperacaoAtuarial toa : TipoOperacaoAtuarial.values()) {
            toReturn.add(new SelectItem(toa, toa.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoPlano() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPlano tp : TipoPlano.values()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoProvisao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoProvisao tp : TipoProvisao.values()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        ConfigProvMatematicaPrev conf = ((ConfigProvMatematicaPrev) selecionado);
        if (conf.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configProvMatematicaFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.PROVISAO_MATEMATICA_PREVIDENCIARIA, TipoLancamento.NORMAL);
        } else {
            return configProvMatematicaFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.PROVISAO_MATEMATICA_PREVIDENCIARIA, TipoLancamento.ESTORNO);
        }
    }

    public boolean validaEditarDataVigencia() {
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

    public void encerrarVigencia() {
        try {
            configProvMatematicaFacade.encerrarVigencia(selecionado);
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
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configProvMatematicaFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public enum ModoListagem {

        VIGENTE("Vigente"),
        ENCERRADO("Encerrado");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private ModoListagem(String descricao) {
            this.descricao = descricao;
        }
    }

    public List<ModoListagem> getListaModoListagem() {
        List<ModoListagem> lista = new ArrayList<ModoListagem>();
        lista.addAll(Arrays.asList(ModoListagem.values()));
        return lista;
    }

    //    public List<ConfigProvMatematicaPrev> getListaVigentesDecrescente() {
//        return configProvMatematicaFacade.getListaVigentesDecrescente();
//    }
//    public void listagem() {
//        listaConfigProvMatematicaPrev = new ArrayList<ConfigProvMatematicaPrev>();
//        if (modoListagem == null) {
//            modoListagem = ModoListagem.VIGENTE;
//        }
//        if (modoListagem.equals(ModoListagem.VIGENTE)) {
//            listaConfigProvMatematicaPrev = configProvMatematicaFacade.getListaVigentesDecrescente();
//        } else if (modoListagem.equals(ModoListagem.ENCERRADO) && dataVigencia != null) {
//            listaConfigProvMatematicaPrev = configProvMatematicaFacade.getListaEncerradosDecrescente(dataVigencia);
//        }
//    }
    public void setaDataParaListagem(DateSelectEvent evento) {
        dataVigencia = evento.getDate();
//        listagem();
    }

    public boolean podeEditar() {
        ConfigProvMatematicaPrev config = (ConfigProvMatematicaPrev) selecionado;
        if (config.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(config.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-provisao-matematica/";
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
}
