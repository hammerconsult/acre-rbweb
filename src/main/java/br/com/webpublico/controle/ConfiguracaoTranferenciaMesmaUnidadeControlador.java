/*
 * Codigo gerado automaticamente em Wed Dec 12 10:09:56 AMT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoTranferenciaMesmaUnidade;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoTranferenciaMesmaUnidadeFacade;
import br.com.webpublico.util.*;
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

@ManagedBean(name = "configuracaoTranferenciaMesmaUnidadeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-transferencia-mesma-unidade", pattern = "/configuracao-transferencia-mesma-unidade/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaotransferenciamesmaunidade/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-transferencia-mesma-unidade", pattern = "/configuracao-transferencia-mesma-unidade/editar/#{configuracaoTranferenciaMesmaUnidadeControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaotransferenciamesmaunidade/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-transferencia-mesma-unidade", pattern = "/configuracao-transferencia-mesma-unidade/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaotransferenciamesmaunidade/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-transferencia-mesma-unidade", pattern = "/configuracao-transferencia-mesma-unidade/ver/#{configuracaoTranferenciaMesmaUnidadeControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaotransferenciamesmaunidade/visualizar.xhtml")
})
public class ConfiguracaoTranferenciaMesmaUnidadeControlador extends ConfigEventoSuperControlador<ConfiguracaoTranferenciaMesmaUnidade> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoTranferenciaMesmaUnidadeFacade configuracaoTranferenciaMesmaUnidadeFacade;
    private ConverterAutoComplete converterEventoContabil;
    private String palavra;
    private List<Conta> listConta;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterGenerico converterTipoContaExtra;
    private ConfiguracaoTranferenciaMesmaUnidadeControlador.ModoListagem modoListagem;
    private List<ConfiguracaoTranferenciaMesmaUnidade> listaConfig;
    private Date dataFimVigencia;
    private ConfiguracaoTranferenciaMesmaUnidade configuracaoTranferenciaMesmaUnidadeNaoAlterada;
    private ConfiguracaoTranferenciaMesmaUnidade configuracaoTranferenciaMesmaUnidade;

    public ConfiguracaoTranferenciaMesmaUnidadeControlador() {
        super(ConfiguracaoTranferenciaMesmaUnidade.class);
    }

    public ConfiguracaoTranferenciaMesmaUnidadeFacade getFacade() {
        return configuracaoTranferenciaMesmaUnidadeFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoTranferenciaMesmaUnidadeFacade;
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (converterEventoContabil == null) {
            converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configuracaoTranferenciaMesmaUnidadeFacade.getEventoContabilFacade());
        }
        return converterEventoContabil;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public List<Conta> getListConta() {
        return listConta;
    }

    public void setListConta(List<Conta> listConta) {
        this.listConta = listConta;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void setListaConfig(List<ConfiguracaoTranferenciaMesmaUnidade> listaConfig) {
        this.listaConfig = listaConfig;
    }

    public Date getDataFimVigencia() {
        return dataFimVigencia;
    }

    public void setDataFimVigencia(Date dataFimVigencia) {
        this.dataFimVigencia = dataFimVigencia;
    }

    /*
     * copiados
     *
     */
    @URLAction(mappingId = "novo-configuracao-transferencia-mesma-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ConfiguracaoTranferenciaMesmaUnidade conf = ((ConfiguracaoTranferenciaMesmaUnidade) selecionado);
        operacao = Operacoes.NOVO;
        conf.setTipoLancamento(TipoLancamento.NORMAL);
        conf.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-transferencia-mesma-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-configuracao-transferencia-mesma-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    public void limpaCampos() {
        listConta = new ArrayList<Conta>();
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> toReturn = new ArrayList<>();
        toReturn.addAll(Arrays.asList(TipoLancamento.values()));
        return toReturn;
    }

    public void recuperaEditaVer() {
        configuracaoTranferenciaMesmaUnidade = configuracaoTranferenciaMesmaUnidadeFacade.recuperar(selecionado.getId());
        configuracaoTranferenciaMesmaUnidadeNaoAlterada = configuracaoTranferenciaMesmaUnidadeFacade.recuperar(selecionado.getId());
        selecionado = configuracaoTranferenciaMesmaUnidade;
    }

    @Override
    public void salvar() {
        ConfiguracaoTranferenciaMesmaUnidade transf = ((ConfiguracaoTranferenciaMesmaUnidade) selecionado);
        try {
            if (Util.validaCampos(transf) && validaEditaVigencia()) {
                ConfiguracaoTranferenciaMesmaUnidade configuracaoEncontrada = configuracaoTranferenciaMesmaUnidadeFacade.verificaConfiguracaoExistente(transf, sistemaControlador.getDataOperacao());
                if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação: "
                            + " Tipo de Lançamento: " + transf.getTipoLancamento() + ", "
                            + " Dependência da Execução Orçamentária: " + transf.getResultanteIndependente() + ", "
                            + " Operação: " + transf.getOperacao().getDescricao() + " e "
                            + " Tipo de Transferência: " + transf.getTipoTransferencia()));
                    return;
                }
                try {
                    configuracaoTranferenciaMesmaUnidadeFacade.meuSalvar(configuracaoTranferenciaMesmaUnidadeNaoAlterada, transf);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                    redireciona();

                } catch (Exception e) {
                    logger.debug(e.getMessage());
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Salvar!", e.getMessage()));
                    return;
                }
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção de sistema", ex.getMessage()));
            return;
        }
        return;
    }

    public List<TipoTransferenciaMesmaUnidade> getTipoTransferenciaMesmaUnidade() {
        List<TipoTransferenciaMesmaUnidade> toReturn = new ArrayList<TipoTransferenciaMesmaUnidade>();
        toReturn.addAll(Arrays.asList(TipoTransferenciaMesmaUnidade.values()));
        return toReturn;
    }

    public List<OrigemTipoTransferencia> getOrigemTipoTransferencia() {
        List<OrigemTipoTransferencia> toReturn = new ArrayList<OrigemTipoTransferencia>();
        toReturn.addAll(Arrays.asList(OrigemTipoTransferencia.values()));
        return toReturn;
    }

    public List<SelectItem> getValoresResultanteIndependente() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (ResultanteIndependente ri : ResultanteIndependente.values()) {
            lista.add(new SelectItem(ri, ri.getDescricao()));
        }
        return lista;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        ConfiguracaoTranferenciaMesmaUnidade conf = ((ConfiguracaoTranferenciaMesmaUnidade) selecionado);
        if (conf.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configuracaoTranferenciaMesmaUnidadeFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_MESMA_UNIDADE, TipoLancamento.NORMAL);
        } else {
            return configuracaoTranferenciaMesmaUnidadeFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_MESMA_UNIDADE, TipoLancamento.ESTORNO);
        }
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

    public ConfiguracaoTranferenciaMesmaUnidadeControlador.ModoListagem getModoListagem() {
        return modoListagem;
    }

    public void setModoListagem(ConfiguracaoTranferenciaMesmaUnidadeControlador.ModoListagem modoListagem) {
        this.modoListagem = modoListagem;
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
            configuracaoTranferenciaMesmaUnidadeFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public boolean validaEditaVigencia() {
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Inválida! ", " Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ".  Para editar a configuração, a data de Início de Vigência não pode ser maior que a data Fim de Vigência."));
                return false;
            }
        }
        return true;
    }

    public void setaData(DateSelectEvent event) {
        ((ConfiguracaoTranferenciaMesmaUnidade) selecionado).setFimVigencia((Date) event.getDate());
    }

    public void valorLanc() {

        ConfiguracaoTranferenciaMesmaUnidade conf = ((ConfiguracaoTranferenciaMesmaUnidade) selecionado);
        conf.setEventoContabil(null);
    }

    public boolean podeEditar() {
        ConfiguracaoTranferenciaMesmaUnidade obj = ((ConfiguracaoTranferenciaMesmaUnidade) selecionado);
        if (obj.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(obj.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-transferencia-mesma-unidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
