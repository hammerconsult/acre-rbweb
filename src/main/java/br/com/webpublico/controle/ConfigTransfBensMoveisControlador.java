package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigTransfBensMoveis;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensMoveis;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigTransfBensMoveisFacade;
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

@ManagedBean(name = "configTransfBensMoveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-config-transf-bens-moveis", pattern = "/config-transferencia-bens-moveis/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensmoveis/edita.xhtml"),
    @URLMapping(id = "edita-config-transf-bens-moveis", pattern = "/config-transferencia-bens-moveis/editar/#{configTransfBensMoveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensmoveis/edita.xhtml"),
    @URLMapping(id = "listar-config-transf-bens-moveis", pattern = "/config-transferencia-bens-moveis/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensmoveis/lista.xhtml"),
    @URLMapping(id = "ver-config-transf-bens-moveis", pattern = "/config-transferencia-bens-moveis/ver/#{configTransfBensMoveisControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfbensmoveis/visualizar.xhtml")
})

public class ConfigTransfBensMoveisControlador extends ConfigEventoSuperControlador<ConfigTransfBensMoveis> implements Serializable, CRUD {

    @EJB
    private ConfigTransfBensMoveisFacade configTransfBensMoveisFacade;
    private ConverterAutoComplete converterEventoContabil;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConfigTransfBensMoveis configuracaoNaoAlterada;

    public ConfigTransfBensMoveisControlador() {
        super(ConfigTransfBensMoveis.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configTransfBensMoveisFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/config-transferencia-bens-moveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novo-config-transf-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-config-transf-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-config-transf-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)
                && validarVigenciaAoEditar()) {
                if (verificarConfiguracaoExistente()) return;
                configTransfBensMoveisFacade.salvar(configuracaoNaoAlterada, selecionado);
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso. ");
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private boolean verificarConfiguracaoExistente() {
        ConfigTransfBensMoveis configuracaoEncontrada = configTransfBensMoveisFacade.verificarConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
        if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
            FacesUtil.addOperacaoNaoPermitida(" Existe uma configuração vigente para o "
                + " tipo de lançamento: " + selecionado.getTipoLancamento().getDescricao()
                + " e operação: " + selecionado.getTipoOperacaoBensMoveis().getDescricao());
            return true;
        }
        return false;
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (converterEventoContabil == null) {
            converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configTransfBensMoveisFacade.getEventoContabilFacade());
        }
        return converterEventoContabil;
    }

    public void recuperaEditaVer() {
        selecionado = configTransfBensMoveisFacade.recuperar(selecionado.getId());
        configuracaoNaoAlterada = configTransfBensMoveisFacade.recuperar(selecionado.getId());
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
            message.setSummary("Operação não Permitida!");
            message.setDetail(" Ano diferente do exercício corrente.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void encerrarVigencia() {
        try {
            configTransfBensMoveisFacade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<EventoContabil> completarEventoContabil(String parte) {
        if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            return configTransfBensMoveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_BENS_MOVEIS, TipoLancamento.NORMAL);
        } else {
            return configTransfBensMoveisFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_BENS_MOVEIS, TipoLancamento.ESTORNO);
        }
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> toReturn = new ArrayList<>();
        toReturn.addAll(Arrays.asList(TipoLancamento.values()));
        return toReturn;
    }

    public List<SelectItem> getListaOperacaoBensMoveis() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        toReturn.add(new SelectItem(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_CONCEDIDA, TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_CONCEDIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensMoveis.TRANFERENCIA_BENS_MOVEIS_RECEBIDA, TipoOperacaoBensMoveis.TRANFERENCIA_BENS_MOVEIS_RECEBIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA, TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA, TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_CONCEDIDA, TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_CONCEDIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_RECEBIDA, TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_RECEBIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_EXAUSTAO_CONCEDIDA, TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_EXAUSTAO_CONCEDIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_EXAUSTAO_RECEBIDA, TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_EXAUSTAO_RECEBIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_REDUCAO_CONCEDIDA, TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_REDUCAO_CONCEDIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_REDUCAO_RECEBIDA, TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_REDUCAO_RECEBIDA.getDescricao()));

        return toReturn;
    }


    public void setaEventoNull() {
        selecionado.setEventoContabil(null);
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
