/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigMovDividaPublica;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigMovDividaPublicaFacade;
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

@ManagedBean(name = "configMovDividaPublicaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-movimento-divida-publica", pattern = "/configuracao-movimento-divida-publica/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configmovdividapublica/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-movimento-divida-publica", pattern = "/configuracao-movimento-divida-publica/editar/#{configMovDividaPublicaControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configmovdividapublica/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-movimento-divida-publica", pattern = "/configuracao-movimento-divida-publica/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configmovdividapublica/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-movimento-divida-publica", pattern = "/configuracao-movimento-divida-publica/ver/#{configMovDividaPublicaControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configmovdividapublica/visualizar.xhtml")
})
public class ConfigMovDividaPublicaControlador extends ConfigEventoSuperControlador<ConfigMovDividaPublica> implements Serializable, CRUD {

    @EJB
    private ConfigMovDividaPublicaFacade configMovDividaPublicaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ConfigMovDividaPublica configMovDividaPublica;
    private ConfigMovDividaPublica configMovDividaPublicaNaoAlterado;

    public ConfigMovDividaPublicaControlador() {
        super(ConfigMovDividaPublica.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configMovDividaPublicaFacade;
    }

    @URLAction(mappingId = "novo-configuracao-movimento-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((ConfigMovDividaPublica) selecionado).setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-movimento-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        configMovDividaPublica = configMovDividaPublicaFacade.recuperar(selecionado.getId());
        configMovDividaPublicaNaoAlterado = configMovDividaPublicaFacade.recuperar(selecionado.getId());
        selecionado = configMovDividaPublica;
    }

    @URLAction(mappingId = "ver-configuracao-movimento-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        configMovDividaPublica = configMovDividaPublicaFacade.recuperar(selecionado.getId());
        configMovDividaPublicaNaoAlterado = configMovDividaPublicaFacade.recuperar(selecionado.getId());
        selecionado = configMovDividaPublica;

    }

    @Override
    public void salvar() {
        ConfigMovDividaPublica cmdp = ((ConfigMovDividaPublica) selecionado);
        try {
            if (Util.validaCampos(cmdp) && validaEditaVigencia()) {
                if (!configMovDividaPublicaFacade.verificaConfiguracaoExistente(((ConfigMovDividaPublica) selecionado), sistemaControlador.getDataOperacao())) {

                    try {
                        configMovDividaPublicaFacade.meuSalvar(configMovDividaPublicaNaoAlterado, cmdp);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                        redireciona();
                    } catch (Exception e) {
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        "Já existe uma configuração vigente para a combinação, Tipo Lançamento: " + esteSelecionado().getTipoLancamento() +
                            ",  Operação: " + esteSelecionado().getOperacaoMovimentoDividaPublica() +
                            " e Tipo Natureza: " + esteSelecionado().getNaturezaDividaPublica()));
                    return;
                }
            }

        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Operação não Realizada! ", ex.getMessage()));
            return;
        }
        return;
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

    public List<SelectItem> getNaturezaDivida() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaDividaPublica object : NaturezaDividaPublica.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getOperacoes() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (OperacaoMovimentoDividaPublica operacao : OperacaoMovimentoDividaPublica.values()) {
            if (!(operacao.equals(OperacaoMovimentoDividaPublica.PAGAMENTO_AMORTIZACAO)
                || operacao.equals(OperacaoMovimentoDividaPublica.RECEITA_OPERACAO_CREDITO))) {
                retorno.add(new SelectItem(operacao, operacao.getDescricao()));
            }
        }
        return retorno;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        return configMovDividaPublicaFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.DIVIDA_PUBLICA, ((ConfigMovDividaPublica) selecionado).getTipoLancamento());
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configMovDividaPublicaFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public ConfigMovDividaPublica esteSelecionado() {
        return (ConfigMovDividaPublica) selecionado;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
        esteSelecionado().setNaturezaDividaPublica(null);
        esteSelecionado().setOperacaoMovimentoDividaPublica(null);
    }

    public void encerrarVigencia() {
        try {
            configMovDividaPublicaFacade.encerrarVigencia(selecionado);
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

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-movimento-divida-publica/";
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
