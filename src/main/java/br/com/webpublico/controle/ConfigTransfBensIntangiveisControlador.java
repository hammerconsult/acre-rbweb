package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigTransfBensIntang;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensIntangiveis;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigTransfBensIntangiveisFacade;
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
 * Date: 13/02/14
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "configTransfBensIntangiveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-config-transf-bens-intangiveis", pattern = "/config-transferencia-bens-intangiveis/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensintangiveis/edita.xhtml"),
    @URLMapping(id = "edita-config-transf-bens-intangiveis", pattern = "/config-transferencia-bens-intangiveis/editar/#{configTransfBensIntangiveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensintangiveis/edita.xhtml"),
    @URLMapping(id = "listar-config-transf-bens-intangiveis", pattern = "/config-transferencia-bens-intangiveis/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensintangiveis/lista.xhtml"),
    @URLMapping(id = "ver-config-transf-bens-intangiveis", pattern = "/config-transferencia-bens-intangiveis/ver/#{configTransfBensIntangiveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensintangiveis/visualizar.xhtml")
})

public class ConfigTransfBensIntangiveisControlador extends ConfigEventoSuperControlador<ConfigTransfBensIntang> implements Serializable, CRUD {

    @EJB
    private ConfigTransfBensIntangiveisFacade configTransfBensIntangiveisFacade;
    private ConverterAutoComplete converterEventoContabil;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConfigTransfBensIntang configuracaoNaoAlterada;

    public ConfigTransfBensIntangiveisControlador() {
        super(ConfigTransfBensIntang.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configTransfBensIntangiveisFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/config-transferencia-bens-intangiveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novo-config-transf-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-config-transf-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-config-transf-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }


    public ConverterAutoComplete getConverterEventoContabil() {
        if (converterEventoContabil == null) {
            converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configTransfBensIntangiveisFacade.getEventoContabilFacade());
        }
        return converterEventoContabil;
    }

    public void recuperaEditaVer() {
        selecionado = configTransfBensIntangiveisFacade.recuperar(selecionado.getId());
        configuracaoNaoAlterada = configTransfBensIntangiveisFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)
                && validarVigenciaAoEditar()) {
                if (verificarConfiguracaoExistente()) return;
                configTransfBensIntangiveisFacade.meuSalvar(configuracaoNaoAlterada, selecionado);
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private boolean verificarConfiguracaoExistente() {
        ConfigTransfBensIntang configuracaoEncontrada = configTransfBensIntangiveisFacade.verificarConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
        if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
            FacesUtil.addOperacaoNaoPermitida(" Existe uma configuração vigente para o "
                + " tipo de lançamento: " + selecionado.getTipoLancamento().getDescricao()
                + " e operação: " + selecionado.getTipoOperacaoBensIntangiveis().getDescricao());
            return true;
        }
        return false;
    }

    public boolean validarVigenciaAoEditar() {
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesUtil.addOperacaoNaoPermitida(" Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ". O data de Início de Vigência deve ser menor que a data de Fim de Vigência.");
                return false;
            }
        }
        return true;
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

    public void encerrarVigencia() {
        try {
            configTransfBensIntangiveisFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configTransfBensIntangiveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_BENS_INTANGIVEIS, TipoLancamento.NORMAL);
        } else {
            return configTransfBensIntangiveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_BENS_INTANGIVEIS, TipoLancamento.ESTORNO);
        }
    }

    public void setaEventoNull() {
        this.selecionado.setEventoContabil(null);
    }


    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> toReturn = new ArrayList<>();
        toReturn.addAll(Arrays.asList(TipoLancamento.values()));
        return toReturn;
    }

    public List<SelectItem> getListaOperacaoBensIntangiveis() {
        List<javax.faces.model.SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new javax.faces.model.SelectItem(null, " "));
        toReturn.add(new javax.faces.model.SelectItem(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA, TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA.getDescricao()));
        toReturn.add(new javax.faces.model.SelectItem(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA, TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA.getDescricao()));
        toReturn.add(new javax.faces.model.SelectItem(TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA, TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA.getDescricao()));
        toReturn.add(new javax.faces.model.SelectItem(TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA, TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA.getDescricao()));
        return toReturn;
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
