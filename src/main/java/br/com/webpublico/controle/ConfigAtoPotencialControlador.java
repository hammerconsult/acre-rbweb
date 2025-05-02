package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigAtoPotencial;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoAtoPotencial;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoAtoPotencial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigAtoPotencialFacade;
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
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-ato-potencial", pattern = "/configuracao-ato-potencial/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configatopotencial/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-ato-potencial", pattern = "/configuracao-ato-potencial/editar/#{configAtoPotencialControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configatopotencial/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-ato-potencial", pattern = "/configuracao-ato-potencial/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configatopotencial/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-ato-potencial", pattern = "/configuracao-ato-potencial/ver/#{configAtoPotencialControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configatopotencial/visualizar.xhtml")
})
public class ConfigAtoPotencialControlador extends ConfigEventoSuperControlador<ConfigAtoPotencial> implements Serializable, CRUD {

    @EJB
    private ConfigAtoPotencialFacade facade;

    public ConfigAtoPotencialControlador() {
        super(ConfigAtoPotencial.class);
    }

    @URLAction(mappingId = "novo-configuracao-ato-potencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-ato-potencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-configuracao-ato-potencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<TipoLancamento> getTiposDeLancamentos() {
        return Arrays.asList(TipoLancamento.values());
    }

    public void atualizarOperacao() {
        selecionado.setTipoOperacaoAtoPotencial(null);
    }

    public List<SelectItem> getTiposDeAtoPotencial() {
        return Util.getListSelectItem(TipoAtoPotencial.values(), false);
    }

    public List<SelectItem> getTiposDeOperacoesAtoPotencial() {
        return Util.getListSelectItem(TipoOperacaoAtoPotencial.buscarOperacoesPorTipoAtoPotencial(selecionado.getTipoAtoPotencial()), false);
    }

    public List<EventoContabil> completarEventosContabeis(String parte) {
        return facade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.ATO_POTENCIAL, selecionado.getTipoLancamento());
    }

    public void definirEventoComoNull() {
        selecionado.setEventoContabil(null);
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
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-ato-potencial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
