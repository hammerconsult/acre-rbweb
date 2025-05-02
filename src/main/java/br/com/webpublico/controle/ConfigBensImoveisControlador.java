package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigBensImoveis;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensImoveis;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigBensImoveisFacade;
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
@ManagedBean(name = "configBensImoveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-bens-imoveis", pattern = "/configuracao-bens-imoveis/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensimoveis/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-bens-imoveis", pattern = "/configuracao-bens-imoveis/editar/#{configBensImoveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensimoveis/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-bens-imoveis", pattern = "/configuracao-bens-imoveis/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensimoveis/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-bens-imoveis", pattern = "/configuracao-bens-imoveis/ver/#{configBensImoveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensimoveis/visualizar.xhtml")
})

public class ConfigBensImoveisControlador extends ConfigEventoSuperControlador<ConfigBensImoveis> implements Serializable, CRUD {

    @EJB
    private ConfigBensImoveisFacade configBensImoveisFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private Date dataVigencia;
    private ConfigBensImoveis configBensMoveisNaoAlterado;


    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-bens-imoveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @Override
    public AbstractFacade getFacede() {
        return configBensImoveisFacade;
    }

    public ConfigBensImoveisControlador() {
        super(ConfigBensImoveis.class);
    }

    @URLAction(mappingId = "novo-configuracao-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
    }

    @URLAction(mappingId = "ver-configuracao-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }


    public void recuperaEditaVer() {
        selecionado = configBensImoveisFacade.recuperar(selecionado.getId());
        configBensMoveisNaoAlterado = configBensImoveisFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado) && validaEditarVigencia()) {
                ConfigBensImoveis configuracaoEncontrada = configBensImoveisFacade.verificaConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação: Tipo Lançamento: " + selecionado.getTipoLancamento() +
                            " e Operação: " + selecionado.getOperacaoBensImoveis().getDescricao()));
                    return;
                }
                try {
                    configBensImoveisFacade.meuSalvar(configBensMoveisNaoAlterado, selecionado);
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
            logger.debug(ex.getMessage());
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


    private List<TipoOperacaoBensImoveis> excecoesDeItensListados() {
        List<TipoOperacaoBensImoveis> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA);
        return lista;
    }

    public List<SelectItem> getOperacoesBensImoveis() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoOperacaoBensImoveis tipo : TipoOperacaoBensImoveis.values()) {
            if (!excecoesDeItensListados().contains(tipo)) {
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return lista;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configBensImoveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.BENS_IMOVEIS, TipoLancamento.NORMAL);
        } else {
            return configBensImoveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.BENS_IMOVEIS, TipoLancamento.ESTORNO);
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
        selecionado.setOperacaoBensImoveis(null);
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configBensImoveisFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public void encerrarVigencia() {
        try {
            configBensImoveisFacade.encerrarVigencia(selecionado);
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
