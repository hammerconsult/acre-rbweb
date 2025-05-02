package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigInvestimento;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.OperacaoInvestimento;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigInvestimentoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mateus on 19/10/17.
 */
@ManagedBean(name = "configInvestimentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-investimento", pattern = "/configuracao-investimento/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configinvestimento/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-investimento", pattern = "/configuracao-investimento/editar/#{configInvestimentoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configinvestimento/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-investimento", pattern = "/configuracao-investimento/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configinvestimento/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-investimento", pattern = "/configuracao-investimento/ver/#{configInvestimentoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configinvestimento/visualizar.xhtml")
})

public class ConfigInvestimentoControlador extends ConfigEventoSuperControlador<ConfigInvestimento> implements Serializable, CRUD {

    @EJB
    private ConfigInvestimentoFacade facade;
    private ConfigInvestimento configuracaoNaoAlterada;

    public ConfigInvestimentoControlador() {
        super(ConfigInvestimento.class);
    }

    @URLAction(mappingId = "novo-configuracao-investimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
    }

    @URLAction(mappingId = "ver-configuracao-investimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "edita-configuracao-investimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        configuracaoNaoAlterada = (ConfigInvestimento) facade.recuperar(super.getId());
    }

    public List<EventoContabil> completarEventoContabil(String parte) {
        return facade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.INVESTIMENTO, selecionado.getTipoLancamento());
    }

    public void definirEventoComoNull() {
        selecionado.setEventoContabil(null);
    }

    public List<TipoLancamento> getTiposDeLancamento() {
        return Arrays.asList(TipoLancamento.values());
    }

    public List<OperacaoInvestimento> getOperacoes() {
        return Arrays.asList(OperacaoInvestimento.values());
    }

    @Override
    public void salvar() {
        try {
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(configuracaoNaoAlterada, selecionado);
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
        return "/configuracao-investimento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ConfigInvestimento getConfiguracaoNaoAlterada() {
        return configuracaoNaoAlterada;
    }

    public void setConfiguracaoNaoAlterada(ConfigInvestimento configuracaoNaoAlterada) {
        this.configuracaoNaoAlterada = configuracaoNaoAlterada;
    }
}
