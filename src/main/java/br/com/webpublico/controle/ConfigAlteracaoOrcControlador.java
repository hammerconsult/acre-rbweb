package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigAlteracaoOrc;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigAlteracaoOrcFacade;
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
@ManagedBean(name = "configAlteracaoOrcControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-alteracaoOrc", pattern = "/configuracao-evento/alteracao-orcamentaria/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configalteracaoorc/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-alteracaoOrc", pattern = "/configuracao-evento/alteracao-orcamentaria/editar/#{configAlteracaoOrcControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configalteracaoorc/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-alteracaoOrc", pattern = "/configuracao-evento/alteracao-orcamentaria/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configalteracaoorc/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-alteracaoOrc", pattern = "/configuracao-evento/alteracao-orcamentaria/ver/#{configAlteracaoOrcControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configalteracaoorc/visualizar.xhtml")
})

public class ConfigAlteracaoOrcControlador extends ConfigEventoSuperControlador<ConfigAlteracaoOrc> implements Serializable, CRUD {

    @EJB
    private ConfigAlteracaoOrcFacade configAlteracaoOrcFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterEventoContabil;
    private Date dataVigencia;
    private ConfigAlteracaoOrc configuracaoNaoAlterada;


    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-evento/alteracao-orcamentaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configAlteracaoOrcFacade;
    }

    public ConfigAlteracaoOrcControlador() {
        super(ConfigAlteracaoOrc.class);
    }

    @URLAction(mappingId = "novo-configuracao-alteracaoOrc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoConfigAlteracaoOrc(TipoConfigAlteracaoOrc.SUPLEMENTAR);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
    }

    @URLAction(mappingId = "ver-configuracao-alteracaoOrc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "edita-configuracao-alteracaoOrc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {

        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        configuracaoNaoAlterada = configAlteracaoOrcFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (validaCampos()) {
                if (validarConfiguracaoVigente()) return;
                configAlteracaoOrcFacade.meuSalvar(configuracaoNaoAlterada, selecionado);
                FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private boolean validarConfiguracaoVigente() {
        ConfigAlteracaoOrc configuracaoEncontrada = configAlteracaoOrcFacade.verificaConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
        if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
            FacesUtil.addOperacaoNaoPermitida(" Configuração Vigente encontrada para os pararâmetos informados.");
            return true;
        }
        return false;
    }

    public boolean validaCampos() {
        Boolean retorno = Boolean.TRUE;
        retorno = Util.validaCampos(selecionado);
        if (selecionado.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.SUPLEMENTAR)) {
            if (selecionado.getTipoDespesaORC() == null) {
                FacesUtil.addCampoObrigatorio("O campo Tipo de Crédito deve ser informado.");
                retorno = false;
            }
            if (selecionado.getOrigemSuplementacaoORC() == null) {
                FacesUtil.addCampoObrigatorio("O campo Origem do Recurso deve ser informado.");
                retorno = false;
            }
        } else if (selecionado.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.ANULACAO)) {
            if (selecionado.getTipoDespesaORC() == null) {
                FacesUtil.addCampoObrigatorio("O campo Tipo de Crédito deve ser informado.");
                retorno = false;
            }
        } else {
            if (selecionado.getTipoAlteracaoORC() == null) {
                FacesUtil.addCampoObrigatorio("O campo Tipo de Alteração deve ser informado.");
                retorno = false;
            }
            if (selecionado.getNumeroInicialContaReceita() == null) {
                FacesUtil.addCampoObrigatorio("O campo Inicio da Conta deve ser informado.");
                retorno = false;
            }
        }
        if (selecionado.getId() != null
            && selecionado.getFimVigencia() != null
            && selecionado.getInicioVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesUtil.addOperacaoNaoPermitida(" Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ".  Para editar a configuração, a data de Início de Vigência não pode ser maior que a data Fim de Vigência.");
                retorno = false;
            }
        }
        return retorno;
    }

    public String styleCampoTipoCredito() {
        if (selecionado.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.SUPLEMENTAR)) {
            return "margin-left: 5px";
        } else {
            return "margin-left: 23px";
        }
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<TipoConfigAlteracaoOrc> getTipoConfiguracaoEvento() {
        List<TipoConfigAlteracaoOrc> lista = new ArrayList<>();
        lista.addAll(Arrays.asList(TipoConfigAlteracaoOrc.values()));
        return lista;
    }

    public List<SelectItem> getListaTipoCredito() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoDespesaORC tipo : TipoDespesaORC.values()) {
            if (!tipo.equals(TipoDespesaORC.ORCAMENTARIA)) {
                toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoAlteracaoOrc() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoAlteracaoORC tipo : TipoAlteracaoORC.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaNumeroInicialConta() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (NumeroInicialContaReceita tipo : NumeroInicialContaReceita.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaOrigemRecurso() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (OrigemSuplementacaoORC tipo : OrigemSuplementacaoORC.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public Boolean renderizarCreditoAdicionalSuplementar() {
        if (selecionado.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.SUPLEMENTAR)) {
            return true;
        }
        return false;
    }

    public Boolean renderizarCreditoAdicionalAnulacao() {
        if (selecionado.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.ANULACAO)) {
            return true;
        }
        return false;
    }

    public Boolean renderizarCreditoAdicionalReceita() {
        if (selecionado.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.RECEITA)) {
            return true;
        }
        return false;
    }

    public List<EventoContabil> completaEventoContabilCreditoAdicional(String parte) {
        if (selecionado.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.SUPLEMENTAR)
            || selecionado.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.ANULACAO)) {
            if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
                return configAlteracaoOrcFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.CREDITO_ADICIONAL, TipoLancamento.NORMAL);
            } else {
                return configAlteracaoOrcFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.CREDITO_ADICIONAL, TipoLancamento.ESTORNO);
            }
        } else {
            if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
                return configAlteracaoOrcFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.PREVISAO_ADICIONAL_RECEITA, TipoLancamento.NORMAL);
            } else {
                return configAlteracaoOrcFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.PREVISAO_ADICIONAL_RECEITA, TipoLancamento.ESTORNO);
            }
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
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (this.converterEventoContabil == null) {
            this.converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configAlteracaoOrcFacade.getEventoContabilFacade());
        }
        return this.converterEventoContabil;
    }

    public void encerrarVigencia() {
        try {
            configAlteracaoOrcFacade.encerrarVigencia(selecionado);
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
