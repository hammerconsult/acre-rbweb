package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigBensMoveis;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensMoveis;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigBensMoveisFacade;
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
@ManagedBean(name = "configBensMoveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-bens-moveis", pattern = "/configuracao-bens-moveis/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensmoveis/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-bens-moveis", pattern = "/configuracao-bens-moveis/editar/#{configBensMoveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensmoveis/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-bens-moveis", pattern = "/configuracao-bens-moveis/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensmoveis/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-bens-moveis", pattern = "/configuracao-bens-moveis/ver/#{configBensMoveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensmoveis/visualizar.xhtml")
})

public class ConfigBensMoveisControlador extends ConfigEventoSuperControlador<ConfigBensMoveis> implements Serializable, CRUD {

    @EJB
    private ConfigBensMoveisFacade configBensMoveisFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private Date dataVigencia;
    private ConfigBensMoveis configBensMoveisNaoAlterado;


    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-bens-moveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @Override
    public AbstractFacade getFacede() {
        return configBensMoveisFacade;
    }

    public ConfigBensMoveisControlador() {
        super(ConfigBensMoveis.class);
    }

    @URLAction(mappingId = "novo-configuracao-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
    }

    @URLAction(mappingId = "ver-configuracao-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        selecionado = configBensMoveisFacade.recuperar(selecionado.getId());
        configBensMoveisNaoAlterado = configBensMoveisFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado) && validaEditarVigencia()) {
                ConfigBensMoveis configuracaoEncontrada = configBensMoveisFacade.verificaConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para o : " + " Tipo Lançamento: " + selecionado.getTipoLancamento() +
                            " e Operação: " + selecionado.getOperacaoBensMoveis().getDescricao()));
                    return;
                }
                try {
                    configBensMoveisFacade.meuSalvar(configBensMoveisNaoAlterado, selecionado);
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

    private List<TipoOperacaoBensMoveis> getExcecoesDeItensListadosParaOperacaoBensMoveis() {
        List<TipoOperacaoBensMoveis> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_CONCEDIDA);
        lista.add(TipoOperacaoBensMoveis.TRANFERENCIA_BENS_MOVEIS_RECEBIDA);
        lista.add(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA);
        lista.add(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA);
        return lista;
    }

    public List<SelectItem> getListaOperacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoOperacaoBensMoveis tipo : TipoOperacaoBensMoveis.values()) {
            if (!getExcecoesDeItensListadosParaOperacaoBensMoveis().contains(tipo)) {
                toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configBensMoveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.BENS_MOVEIS, TipoLancamento.NORMAL);
        } else {
            return configBensMoveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.BENS_MOVEIS, TipoLancamento.ESTORNO);
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
        selecionado.setOperacaoBensMoveis(null);
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configBensMoveisFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public void encerrarVigencia() {
        try {
            configBensMoveisFacade.encerrarVigencia(selecionado);
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

    public Date getDataVigencia() {
        return dataVigencia;
    }

    public void setDataVigencia(Date dataVigencia) {
        this.dataVigencia = dataVigencia;
    }
}
