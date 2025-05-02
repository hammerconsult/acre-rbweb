package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigAjusteAtivoDisponivel;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoAjusteDisponivel;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigAjusteDisponivelFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
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
 * @author Rafael Major
 */
@ManagedBean(name = "configAjusteDisponivelControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-ajuste-ativo-disponivel", pattern = "/configuracao-ajuste-ativo-disponivel/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoajusteativodisponivel/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-ajuste-ativo-disponivel", pattern = "/configuracao-ajuste-ativo-disponivel/editar/#{configAjusteDisponivelControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoajusteativodisponivel/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-ajuste-ativo-disponivel", pattern = "/configuracao-ajuste-ativo-disponivel/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoajusteativodisponivel/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-ajuste-ativo-disponivel", pattern = "/configuracao-ajuste-ativo-disponivel/ver/#{configAjusteDisponivelControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoajusteativodisponivel/visualizar.xhtml")
})
public class ConfigAjusteDisponivelControlador extends ConfigEventoSuperControlador<ConfigAjusteAtivoDisponivel> implements Serializable, CRUD {

    @EJB
    private ConfigAjusteDisponivelFacade configAjusteDisponivelFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private List<ConfigAjusteAtivoDisponivel> listaConfigAjusteDisponivel;
    private ConfigAjusteAtivoDisponivel configAjusteAtivoDisponivel;
    private ConfigAjusteAtivoDisponivel confgAjustAtiDisNaoAlterado;

     @Override
    public AbstractFacade getFacede() {
        return configAjusteDisponivelFacade;
    }

    public ConfigAjusteDisponivelControlador() {
        super(ConfigAjusteAtivoDisponivel.class);
    }

    public List<ConfigAjusteAtivoDisponivel> getListaConfigAjusteDisponivel() {
        return listaConfigAjusteDisponivel;
    }

    public ConfigAjusteAtivoDisponivel getConfigAjusteAtivoDisponivel() {
        return configAjusteAtivoDisponivel;
    }

    public void setConfigAjusteAtivoDisponivel(ConfigAjusteAtivoDisponivel configAjusteAtivoDisponivel) {
        this.configAjusteAtivoDisponivel = configAjusteAtivoDisponivel;
    }

    public ConfigAjusteAtivoDisponivel getConfgAjustAtiDisNaoAlterado() {
        return confgAjustAtiDisNaoAlterado;
    }

    public void setConfgAjustAtiDisNaoAlterado(ConfigAjusteAtivoDisponivel confgAjustAtiDisNaoAlterado) {
        this.confgAjustAtiDisNaoAlterado = confgAjustAtiDisNaoAlterado;
    }

    @URLAction(mappingId = "novo-configuracao-ajuste-ativo-disponivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        ((ConfigAjusteAtivoDisponivel) selecionado).setTipoLancamento(TipoLancamento.NORMAL);
    }

    @URLAction(mappingId = "ver-configuracao-ajuste-ativo-disponivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-ajuste-ativo-disponivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        configAjusteAtivoDisponivel = configAjusteDisponivelFacade.recuperar(selecionado.getId());
        confgAjustAtiDisNaoAlterado = configAjusteDisponivelFacade.recuperar(selecionado.getId());
        selecionado = configAjusteAtivoDisponivel;
    }


    @Override
    public void salvar() {
        ConfigAjusteAtivoDisponivel caad = ((ConfigAjusteAtivoDisponivel) selecionado);
        try {
            if (Util.validaCampos(caad) && validaEditarVigencia()) {
                ConfigAjusteAtivoDisponivel configuracaoEncontrada = configAjusteDisponivelFacade.verificaConfiguracaoExistente(caad, sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação: "
                            + " Tipo Lançamento: " + caad.getTipoLancamento() + " , "
                            + "Tipo Ajuste Disponível: " + caad.getTipoAjusteDisponivel()));
                    return;
                }
                try {
                    configAjusteDisponivelFacade.meuSalvar(confgAjustAtiDisNaoAlterado, caad);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada!", " Registro salvo com sucesso."));
                    redireciona();

                } catch (ExcecaoNegocioGenerica e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Salvar!", e.getMessage()));
                    return;

                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Salvar!", e.getMessage()));
                    return;
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção de sistema", ex.getMessage()));
        }
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

    public List<TipoAjusteDisponivel> getListaTipoAjuste() {
        List<TipoAjusteDisponivel> lista = new ArrayList<TipoAjusteDisponivel>();
        lista.addAll(Arrays.asList(TipoAjusteDisponivel.values()));
        return lista;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        ConfigAjusteAtivoDisponivel cad = (ConfigAjusteAtivoDisponivel) selecionado;
        if (cad.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configAjusteDisponivelFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.AJUSTE_ATIVO_DISPONIVEL, TipoLancamento.NORMAL);
        } else {
            return configAjusteDisponivelFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.AJUSTE_ATIVO_DISPONIVEL, TipoLancamento.ESTORNO);
        }
    }

    public ConfigAjusteAtivoDisponivel esteSelecionado() {
        return (ConfigAjusteAtivoDisponivel) selecionado;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configAjusteDisponivelFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public void encerrarVigencia() {
        try {
            configAjusteDisponivelFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public boolean podeEditar() {
        ConfigAjusteAtivoDisponivel config = (ConfigAjusteAtivoDisponivel) selecionado;
        if (config.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(selecionado.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-ajuste-ativo-disponivel/";
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
