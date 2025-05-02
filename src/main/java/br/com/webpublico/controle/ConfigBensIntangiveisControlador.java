package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigBensIntangiveis;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensIntangiveis;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigBensIntangiveisFacade;
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
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 05/02/14
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configBensIntangiveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-bens-intangiveis", pattern = "/configuracao-bens-intangiveis/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensintangiveis/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-bens-intangiveis", pattern = "/configuracao-bens-intangiveis/editar/#{configBensIntangiveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensintangiveis/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-bens-intangiveis", pattern = "/configuracao-bens-intangiveis/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensintangiveis/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-bens-intangiveis", pattern = "/configuracao-bens-intangiveis/ver/#{configBensIntangiveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensintangiveis/visualizar.xhtml")
})

public class ConfigBensIntangiveisControlador extends ConfigEventoSuperControlador<ConfigBensIntangiveis> implements Serializable, CRUD {

    @EJB
    private ConfigBensIntangiveisFacade configBensIntangiveisFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ConfigBensIntangiveis configBensIntangiveisNaoAlterado;


    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-bens-intangiveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @Override
    public AbstractFacade getFacede() {
        return configBensIntangiveisFacade;
    }

    public ConfigBensIntangiveisControlador() {
        super(ConfigBensIntangiveis.class);
    }

    @URLAction(mappingId = "novo-configuracao-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
    }

    @URLAction(mappingId = "ver-configuracao-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        selecionado = configBensIntangiveisFacade.recuperar(selecionado.getId());
        configBensIntangiveisNaoAlterado = configBensIntangiveisFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado) && validaEditarVigencia()) {
                ConfigBensIntangiveis configuracaoEncontrada = configBensIntangiveisFacade.verificaConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação " + " Tipo Lançamento: " + selecionado.getTipoLancamento() +
                            " e Operação: " + selecionado.getOperacaoBensIntangiveis().getDescricao()));
                    return;
                }
                try {
                    configBensIntangiveisFacade.meuSalvar(configBensIntangiveisNaoAlterado, selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso. "));
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

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    private List<TipoOperacaoBensIntangiveis> excecoesDeItensListados() {
        List<TipoOperacaoBensIntangiveis> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA);
        lista.add(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA);
        lista.add(TipoOperacaoBensIntangiveis.AQUISICAO_BENS_INTANGIVEIS);
        lista.add(TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA);
        lista.add(TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA);
        return lista;
    }

    public List<SelectItem> getListaOperacoes() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoOperacaoBensIntangiveis tipo : TipoOperacaoBensIntangiveis.values()) {
            if (!excecoesDeItensListados().contains(tipo)) {
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return lista;
    }


    public List<EventoContabil> completaEventoContabil(String parte) {
        if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configBensIntangiveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.BENS_INTANGIVEIS, TipoLancamento.NORMAL);
        } else {
            return configBensIntangiveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.BENS_INTANGIVEIS, TipoLancamento.ESTORNO);
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


    public void setaEventoNull() {
        selecionado.setEventoContabil(null);
        selecionado.setOperacaoBensIntangiveis(null);
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configBensIntangiveisFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public void encerrarVigencia() {
        try {
            configBensIntangiveisFacade.encerrarVigencia(selecionado);
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

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
