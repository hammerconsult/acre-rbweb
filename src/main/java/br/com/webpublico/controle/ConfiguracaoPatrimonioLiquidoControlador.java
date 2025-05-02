package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigPatrimonioLiquido;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.OperacaoPatrimonioLiquido;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoPatrimonioLiquidoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mga on 18/10/2017.
 */
@ManagedBean(name = "configuracaoPatrimonioLiquidoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-config-patrimonioliquido", pattern = "/configuracao-patrimonio-liquido/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configpatrimonioliquido/edita.xhtml"),
    @URLMapping(id = "edita-config-patrimonioliquido", pattern = "/configuracao-patrimonio-liquido/editar/#{configuracaoPatrimonioLiquidoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configpatrimonioliquido/edita.xhtml"),
    @URLMapping(id = "listar-config-patrimonioliquido", pattern = "/configuracao-patrimonio-liquido/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configpatrimonioliquido/lista.xhtml"),
    @URLMapping(id = "ver-config-patrimonioliquido", pattern = "/configuracao-patrimonio-liquido/ver/#{configuracaoPatrimonioLiquidoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configpatrimonioliquido/visualizar.xhtml")
})
public class ConfiguracaoPatrimonioLiquidoControlador extends ConfigEventoSuperControlador<ConfigPatrimonioLiquido> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoPatrimonioLiquidoFacade facade;
    private ConfigPatrimonioLiquido configuracaoNaoAlterada;

    public ConfiguracaoPatrimonioLiquidoControlador() {
        super(ConfigPatrimonioLiquido.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-patrimonio-liquido/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "nova-config-patrimonioliquido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "edita-config-patrimonioliquido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        configuracaoNaoAlterada = (ConfigPatrimonioLiquido) facade.recuperar(super.getId());
    }

    @URLAction(mappingId = "ver-config-patrimonioliquido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
                facade.salvar(configuracaoNaoAlterada, selecionado);
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

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<SelectItem> getOperacoes() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem("", null));
        for (OperacaoPatrimonioLiquido tipo : OperacaoPatrimonioLiquido.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }


    public List<EventoContabil> completarEventoContabil(String parte) {
        return facade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.PATRIMONIO_LIQUIDO, selecionado.getTipoLancamento());
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

}
