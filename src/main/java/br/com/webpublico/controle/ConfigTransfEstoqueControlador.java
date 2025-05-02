package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigTransfEstoque;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigTransfEstoqueFacade;
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

@ManagedBean(name = "configTransfEstoqueControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-config-transf-estoque", pattern = "/config-transferencia-estoque/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfestoque/edita.xhtml"),
    @URLMapping(id = "edita-config-transf-estoque", pattern = "/config-transferencia-estoque/editar/#{configTransfEstoqueControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfestoque/edita.xhtml"),
    @URLMapping(id = "listar-config-transf-estoque", pattern = "/config-transferencia-estoque/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfestoque/lista.xhtml"),
    @URLMapping(id = "ver-config-transf-estoque", pattern = "/config-transferencia-estoque/ver/#{configTransfEstoqueControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configtransfestoque/visualizar.xhtml")
})

public class ConfigTransfEstoqueControlador extends ConfigEventoSuperControlador<ConfigTransfEstoque> implements Serializable, CRUD {

    @EJB
    private ConfigTransfEstoqueFacade configTransfEstoqueFacade;
    private ConverterAutoComplete converterEventoContabil;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConfigTransfEstoque configuracaoNaoAlterada;

    public ConfigTransfEstoqueControlador() {
        super(ConfigTransfEstoque.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configTransfEstoqueFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/config-transferencia-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novo-config-transf-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "edita-config-transf-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-config-transf-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (converterEventoContabil == null) {
            converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, configTransfEstoqueFacade.getEventoContabilFacade());
        }
        return converterEventoContabil;
    }

    public void recuperaEditaVer() {
        selecionado = configTransfEstoqueFacade.recuperar(selecionado.getId());
        configuracaoNaoAlterada = configTransfEstoqueFacade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)
                && validarVigenciaAoEditar()) {
                if (validarConfiguracaoExistente()) return;
                configTransfEstoqueFacade.meuSalvar(configuracaoNaoAlterada, selecionado);
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private boolean validarConfiguracaoExistente() {
        ConfigTransfEstoque configuracaoEncontrada = configTransfEstoqueFacade.verificarConfiguracaoExistente(selecionado, sistemaControlador.getDataOperacao());
        if (configuracaoEncontrada != null && configuracaoEncontrada.getId() != null) {
            FacesUtil.addOperacaoNaoPermitida(" Existe uma configuração vigente para o "
                + " tipo de lançamento: " + selecionado.getTipoLancamento().getDescricao()
                + " e operação: " + selecionado.getTipoOperacaoBensEstoque().getDescricao());
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
            configTransfEstoqueFacade.encerrarVigencia(selecionado);
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
            return configTransfEstoqueFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_BENS_ESTOQUE, TipoLancamento.NORMAL);
        } else {
            return configTransfEstoqueFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.TRANSFERENCIA_BENS_ESTOQUE, TipoLancamento.ESTORNO);
        }
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> toReturn = new ArrayList<>();
        toReturn.addAll(Arrays.asList(TipoLancamento.values()));
        return toReturn;
    }

    public List<SelectItem> getListaOperacaoBensEstoque() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        toReturn.add(new SelectItem(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA, TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA.getDescricao()));
        toReturn.add(new SelectItem(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA, TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA.getDescricao()));
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
