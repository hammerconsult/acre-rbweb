package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigAberturaFechamentoExercicio;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.PatrimonioLiquido;
import br.com.webpublico.enums.TipoMovimentoContabil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigAberturaFechamentoExercicioFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mateus on 12/12/17.
 */
@ManagedBean(name = "configAberturaFechamentoExercicioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-abertura-fechamento-exercicio", pattern = "/configuracao-abertura-fechamento-exercicios/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configaberturafechamento/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-abertura-fechamento-exercicio", pattern = "/configuracao-abertura-fechamento-exercicios/editar/#{configAberturaFechamentoExercicioControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configaberturafechamento/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-abertura-fechamento-exercicio", pattern = "/configuracao-abertura-fechamento-exercicios/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configaberturafechamento/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-abertura-fechamento-exercicio", pattern = "/configuracao-abertura-fechamento-exercicios/ver/#{configAberturaFechamentoExercicioControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configaberturafechamento/visualizar.xhtml")
})
public class ConfigAberturaFechamentoExercicioControlador extends ConfigEventoSuperControlador<ConfigAberturaFechamentoExercicio> implements Serializable, CRUD {

    @EJB
    private ConfigAberturaFechamentoExercicioFacade facade;

    public ConfigAberturaFechamentoExercicioControlador() {
        super(ConfigAberturaFechamentoExercicio.class);
    }

    public List<SelectItem> getTiposDeMovimentoContabil() {
        return Util.getListSelectItemSemCampoVazio(TipoMovimentoContabil.values(), false);
    }

    public List<SelectItem> getInternosInternos() {
        return Util.getListSelectItemSemCampoVazio(PatrimonioLiquido.values(), false);
    }

    public List<EventoContabil> completarEventoContabil(String parte) {
        return facade.getEventoContabilFacade().buscarEventosContabeisPorCodigoOrDescricao(parte.trim());
    }

    @URLAction(mappingId = "novo-configuracao-abertura-fechamento-exercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-abertura-fechamento-exercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-configuracao-abertura-fechamento-exercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void encerrarVigencia() {
        try {
            facade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-abertura-fechamento-exercicios/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }
}
