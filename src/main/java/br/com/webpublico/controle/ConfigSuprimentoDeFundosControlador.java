/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigSuprimentoDeFundos;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.OperacaoDiariaContabilizacao;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigSuprimentoDeFundosFacade;
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

@ManagedBean(name = "configSuprimentoDeFundosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-suprimento-fundos", pattern = "/configuracao-suprimento-fundos/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configsuprimentodefundos/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-suprimento-fundos", pattern = "/configuracao-suprimento-fundos/editar/#{configSuprimentoDeFundosControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configsuprimentodefundos/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-suprimento-fundos", pattern = "/configuracao-suprimento-fundos/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configsuprimentodefundos/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-suprimento-fundos", pattern = "/configuracao-suprimento-fundos/ver/#{configSuprimentoDeFundosControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configsuprimentodefundos/visualizar.xhtml")
})
public class ConfigSuprimentoDeFundosControlador extends ConfigEventoSuperControlador<ConfigSuprimentoDeFundos> implements Serializable, CRUD {

    @EJB
    private ConfigSuprimentoDeFundosFacade configSuprimentoDeFundosFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private ConfigSuprimentoDeFundos configSuprimentoDeFundos;
    private ConfigSuprimentoDeFundos configSupriFundosNaoAlterado;

    public ConfigSuprimentoDeFundosControlador() {
        super(ConfigSuprimentoDeFundos.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configSuprimentoDeFundosFacade;
    }

    @URLAction(mappingId = "novo-configuracao-suprimento-fundos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new ConfigSuprimentoDeFundos();
        ((ConfigSuprimentoDeFundos) selecionado).setInicioVigencia(sistemaControlador.getDataOperacao());
        ((ConfigSuprimentoDeFundos) selecionado).setTipoLancamento(TipoLancamento.NORMAL);
    }

    @URLAction(mappingId = "edita-configuracao-suprimento-fundos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-configuracao-suprimento-fundos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        configSuprimentoDeFundos = configSuprimentoDeFundosFacade.recuperar(selecionado.getId());
        configSupriFundosNaoAlterado = configSuprimentoDeFundosFacade.recuperar(selecionado.getId());
        selecionado = configSuprimentoDeFundos;
    }

    public ConfigSuprimentoDeFundos esteSelecionado() {
        return (ConfigSuprimentoDeFundos) selecionado;
    }

    public void setaEventoNull() {
        esteSelecionado().setEventoContabil(null);
        esteSelecionado().setOperacaoDiariaContabilizacao(null);
    }

    @Override
    public void salvar() {
        ConfigSuprimentoDeFundos csf = ((ConfigSuprimentoDeFundos) selecionado);
        try {
            if (Util.validaCampos(csf) && validaEditaVigencia()) {
                if (!configSuprimentoDeFundosFacade.verificaConfiguracaoExistente(((ConfigSuprimentoDeFundos) selecionado), sistemaControlador.getDataOperacao())) {

                    try {
                        configSuprimentoDeFundosFacade.meuSalvar(configSupriFundosNaoAlterado, csf);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                        redireciona();
                    } catch (Exception e) {
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configuração existente! ",
                        " Já existe uma configuração vigente para a combinação: "
                            + " Tipo de Lançamento: " + esteSelecionado().getTipoLancamento()
                            + " e Operação: " + esteSelecionado().getOperacaoDiariaContabilizacao()));
                    return;
                }
            }

        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Operação não Realizada! ", ex.getMessage()));
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

    public boolean validaEditaVigencia() {
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

    public List<SelectItem> getListaOperacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoDiariaContabilizacao odc : OperacaoDiariaContabilizacao.values()) {
            toReturn.add(new SelectItem(odc, odc.getDescricao()));
        }
        return toReturn;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        return configSuprimentoDeFundosFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.SUPRIMENTO_FUNDO, ((ConfigSuprimentoDeFundos) selecionado).getTipoLancamento());
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configSuprimentoDeFundosFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public void encerrarVigencia() {
        try {
            configSuprimentoDeFundosFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public boolean podeEditar() {
        ConfigSuprimentoDeFundos csf = ((ConfigSuprimentoDeFundos) selecionado);
        if (csf.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(csf.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-suprimento-fundos/";
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
