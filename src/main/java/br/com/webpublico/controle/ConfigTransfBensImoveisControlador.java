package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigTransfBensImoveis;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensImoveis;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigTransfBensImoveisFacade;
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

@ManagedBean(name = "configTransfBensImoveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-config-transf-bens-imoveis", pattern = "/config-transferencia-bens-imoveis/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensimoveis/edita.xhtml"),
    @URLMapping(id = "edita-config-transf-bens-imoveis", pattern = "/config-transferencia-bens-imoveis/editar/#{configTransfBensImoveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensimoveis/edita.xhtml"),
    @URLMapping(id = "listar-config-transf-bens-imoveis", pattern = "/config-transferencia-bens-imoveis/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensimoveis/lista.xhtml"),
    @URLMapping(id = "ver-config-transf-bens-imoveis", pattern = "/config-transferencia-bens-imoveis/ver/#{configTransfBensImoveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensimoveis/visualizar.xhtml")
})

public class ConfigTransfBensImoveisControlador extends ConfigEventoSuperControlador<ConfigTransfBensImoveis> implements Serializable, CRUD {

    @EJB
    private ConfigTransfBensImoveisFacade configTransfBensImoveisFacade;
    private ConverterAutoComplete converterEventoContabil;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConfigTransfBensImoveis configuracaoNaoAlterada;

    public ConfigTransfBensImoveisControlador() {
        super(ConfigTransfBensImoveis.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configTransfBensImoveisFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/config-transferencia-bens-imoveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novo-config-transf-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-config-transf-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-config-transf-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (converterEventoContabil == null) {
            converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configTransfBensImoveisFacade.getEventoContabilFacade());
        }
        return converterEventoContabil;
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

    public void setEventoNull() {
        this.selecionado.setEventoContabil(null);
    }


    public void recuperaEditaVer() {
        selecionado = configTransfBensImoveisFacade.recuperar(selecionado.getId());
        configuracaoNaoAlterada = configTransfBensImoveisFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)
                && validaEditarVigencia()) {
                if (verificarConfiguracaoExistente()) return;
                configTransfBensImoveisFacade.meuSalvar(configuracaoNaoAlterada, selecionado);
                FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso. ");
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private boolean verificarConfiguracaoExistente() {
        ConfigTransfBensImoveis configuracaoEncontrada = configTransfBensImoveisFacade.verificarConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
        if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
            FacesUtil.addOperacaoNaoPermitida(" Existe uma configuração vigente para o "
                + " tipo de lançamento: " + selecionado.getTipoLancamento().getDescricao()
                + " e operação: " + selecionado.getTipoOperacaoBensImoveis().getDescricao());
            return true;
        }
        return false;
    }

    public boolean validaEditarVigencia() {
        if (selecionado.getId() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesUtil.addOperacaoNaoPermitida(" Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ". A data de Início de Vigência deve ser menor que a data de Fim de Vigência.");
                return false;
            }
        }
        return true;
    }

    public List<EventoContabil> completaEventoContabil(String parte) {
        if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configTransfBensImoveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_BENS_IMOVEIS, TipoLancamento.NORMAL);
        } else {
            return configTransfBensImoveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_BENS_IMOVEIS, TipoLancamento.ESTORNO);
        }
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> toReturn = new ArrayList<>();
        toReturn.addAll(Arrays.asList(TipoLancamento.values()));
        return toReturn;
    }

    public List<SelectItem> getListaOperacaoBensImoveis() {
        return montarSelectPelosEnums(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA,
            TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA,
            TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA,
            TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA,
            TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA,
            TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA);
    }

    private List<SelectItem> montarSelectPelosEnums(TipoOperacaoBensImoveis... tipos) {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoOperacaoBensImoveis tipo : tipos) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public void encerrarVigencia() {
        try {
            configTransfBensImoveisFacade.encerrarVigencia(selecionado);
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
