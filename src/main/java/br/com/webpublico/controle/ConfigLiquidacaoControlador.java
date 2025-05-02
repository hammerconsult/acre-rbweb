package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigLiquidacao;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoReconhecimentoObrigacaoPagar;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigLiquidacaoFacade;
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
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 21/06/13
 * Time: 09:52
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configLiquidacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-liquidacao", pattern = "/configuracao-liquidacao/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoliquidacao/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-liquidacao", pattern = "/configuracao-liquidacao/editar/#{configLiquidacaoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoliquidacao/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-liquidacao", pattern = "/configuracao-liquidacao/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoliquidacao/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-liquidacao", pattern = "/configuracao-liquidacao/ver/#{configLiquidacaoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoliquidacao/visualizar.xhtml")
})
public class ConfigLiquidacaoControlador extends PrettyControlador<ConfigLiquidacao> implements Serializable, CRUD {

    @EJB
    private ConfigLiquidacaoFacade facade;
    private ConfigLiquidacao configuracaoNaoAlterada;

    public ConfigLiquidacaoControlador() {
        super(ConfigLiquidacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-liquidacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-configuracao-liquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
        selecionado.setSubTipoDespesa(SubTipoDespesa.NAO_APLICAVEL);
    }

    @URLAction(mappingId = "edita-configuracao-liquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        configuracaoNaoAlterada = (ConfigLiquidacao) getFacede().recuperar(super.getId());
        selecionado.setContaDespesa(selecionado.getConfigLiquidacaoContaDespesas().get(0).getContaDespesa());
    }

    @URLAction(mappingId = "ver-configuracao-liquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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

    public boolean isVigenciaEncerrada() {
        return selecionado.getFimVigencia() != null && facade.isVigenciaEncerrada(selecionado.getFimVigencia());
    }

    public List<TipoLancamento> getTiposLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<SelectItem> getTiposReconhecimento() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem("", null));
        for (TipoReconhecimentoObrigacaoPagar tipo : TipoReconhecimentoObrigacaoPagar.retornaTipoReconhecimentosLiquidacao()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<Conta> completarContasDespesa(String parte) {
        return facade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), facade.getSistemaFacade().getExercicioCorrente());
    }

    public List<EventoContabil> completarEventoContabil(String parte) {
        return facade.getEventoContabilFacade().buscarFiltrandoPorTipoEventoAndTipoLancamento(
            parte.trim(),
            TipoEventoContabil.LIQUIDACAO_DESPESA,
            selecionado.getTipoLancamento(),
            50);
    }

    public void definirEventoComoNull() {
        selecionado.setEventoContabil(null);
    }

    public List<SubTipoDespesa> getSubTipoContas() {
        List<SubTipoDespesa> toReturn = new ArrayList<>();
        for (SubTipoDespesa std : SubTipoDespesa.values()) {
            toReturn.add(std);
        }
        return toReturn;
    }

}
