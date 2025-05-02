package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigBensEstoque;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigBensEstoqueFacade;
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
@ManagedBean(name = "configBensEstoqueControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-bens-estoque", pattern = "/configuracao-bens-estoque/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensestoque/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-bens-estoque", pattern = "/configuracao-bens-estoque/editar/#{configBensEstoqueControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensestoque/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-bens-estoque", pattern = "/configuracao-bens-estoque/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensestoque/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-bens-estoque", pattern = "/configuracao-bens-estoque/ver/#{configBensEstoqueControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configbensestoque/visualizar.xhtml")
})

public class ConfigBensEstoqueControlador extends ConfigEventoSuperControlador<ConfigBensEstoque> implements Serializable, CRUD {

    @EJB
    private ConfigBensEstoqueFacade configBensEstoqueFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private Date dataVigencia;
    private ConfigBensEstoque configBensMoveisNaoAlterado;


    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-bens-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @Override
    public AbstractFacade getFacede() {
        return configBensEstoqueFacade;
    }

    public ConfigBensEstoqueControlador() {
        super(ConfigBensEstoque.class);
    }

    @URLAction(mappingId = "novo-configuracao-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
    }

    @URLAction(mappingId = "ver-configuracao-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        selecionado = configBensEstoqueFacade.recuperar(selecionado.getId());
        configBensMoveisNaoAlterado = configBensEstoqueFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado) && validaEditarVigencia()) {
                ConfigBensEstoque configuracaoEncontrada = configBensEstoqueFacade.verificaConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para o : " + " Tipo Lançamento: " + selecionado.getTipoLancamento() +
                            " e Operação: " + selecionado.getOperacaoBensEstoque().getDescricao()));
                    return;
                }
                try {
                    configBensEstoqueFacade.meuSalvar(configBensMoveisNaoAlterado, selecionado);
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


    private List<TipoOperacaoBensEstoque> excecoesDeItensListados() {
        List<TipoOperacaoBensEstoque> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA);
        lista.add(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA);
        return lista;
    }

    public List<SelectItem> getListaOperacoes() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoOperacaoBensEstoque tipo : TipoOperacaoBensEstoque.values()) {
            if (!excecoesDeItensListados().contains(tipo)) {
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return lista;
    }


    public List<EventoContabil> completaEventoContabil(String parte) {
        if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configBensEstoqueFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.BENS_ESTOQUE, TipoLancamento.NORMAL);
        } else {
            return configBensEstoqueFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.BENS_ESTOQUE, TipoLancamento.ESTORNO);
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
        selecionado.setOperacaoBensEstoque(null);
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configBensEstoqueFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public void encerrarVigencia() {
        try {
            configBensEstoqueFacade.encerrarVigencia(selecionado);
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
