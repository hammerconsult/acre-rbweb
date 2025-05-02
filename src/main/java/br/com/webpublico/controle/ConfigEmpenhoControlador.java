package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigEmpenho;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaDespesa;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigEmpenhoFacade;
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
 * Date: 20/06/13
 * Time: 15:11
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configEmpenhoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-empenho", pattern = "/configuracao-empenho/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoempenho/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-empenho", pattern = "/configuracao-empenho/editar/#{configEmpenhoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoempenho/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-empenho", pattern = "/configuracao-empenho/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoempenho/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-empenho", pattern = "/configuracao-empenho/ver/#{configEmpenhoControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configuracaoempenho/visualizar.xhtml")
})
public class ConfigEmpenhoControlador extends PrettyControlador<ConfigEmpenho> implements Serializable, CRUD {

    @EJB
    private ConfigEmpenhoFacade facade;
    private ConfigEmpenho configuracaoNaoAlterada;

    public ConfigEmpenhoControlador() {
        super(ConfigEmpenho.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-empenho/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-configuracao-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado.setContaDespesa(selecionado.getConfigEmpenhoContaDespesas().get(0).getContaDespesa());
        configuracaoNaoAlterada = (ConfigEmpenho) getFacede().recuperar(super.getId());
    }

    @URLAction(mappingId = "ver-configuracao-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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

    public List<TipoLancamento> getTiposLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<EventoContabil> completarEventoContabil(String parte) {
        return facade.getEventoContabilFacade().buscarFiltrandoPorTipoEventoAndTipoLancamento(
            parte.trim(),
            TipoEventoContabil.EMPENHO_DESPESA,
            selecionado.getTipoLancamento(),
            10);
    }

    public List<SelectItem> getTiposReconhecimento() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem("", null));
        for (TipoReconhecimentoObrigacaoPagar tipo : TipoReconhecimentoObrigacaoPagar.retornaTipoReconhecimentosEmpenho()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }


    public List<Conta> completarContasDespesa(String parte) {
        return facade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), facade.getSistemaFacade().getExercicioCorrente());
    }

    public List<TipoContaDespesa> getTipoContas() {
        List<TipoContaDespesa> toReturn = new ArrayList<>();
        if (selecionado != null && selecionado.getContaDespesa() != null) {
            TipoContaDespesa tipo = ((ContaDespesa) selecionado.getContaDespesa()).getTipoContaDespesa();
            if (!TipoContaDespesa.NAO_APLICAVEL.equals(tipo) && tipo != null) {
                selecionado.setTipoContaDespesa(tipo);
                toReturn.add(tipo);
            } else {
                List<TipoContaDespesa> busca = facade.getContaFacade().buscarTiposContasDespesaNosFilhosDaConta(((ContaDespesa) selecionado.getContaDespesa()));
                if (!busca.isEmpty()) {
                    for (TipoContaDespesa tp : busca) {
                        if (!tp.equals(TipoContaDespesa.NAO_APLICAVEL)) {
                            toReturn.add(tp);
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    public void definirSubTipoDespesaPorTipoDespesa() {
        TipoContaDespesa tipo = selecionado.getTipoContaDespesa();
        if (!tipo.isPessoaEncargos() && !tipo.isDividaPublica() && !tipo.isPrecatorio()) {
            selecionado.setSubTipoDespesa(SubTipoDespesa.NAO_APLICAVEL);

        } else if (tipo.isPessoaEncargos()) {
            selecionado.setSubTipoDespesa(SubTipoDespesa.RGPS);

        } else if (tipo.isDividaPublica() || tipo.isPrecatorio()) {
            selecionado.setSubTipoDespesa(SubTipoDespesa.VALOR_PRINCIPAL);
        }
    }

    public List<SubTipoDespesa> getSubTipoContas() {
        List<SubTipoDespesa> toReturn = new ArrayList<>();
        if (selecionado != null && selecionado.getTipoContaDespesa() != null) {
            TipoContaDespesa tipo = selecionado.getTipoContaDespesa();
            if (tipo.isPessoaEncargos()) {
                toReturn.add(SubTipoDespesa.RGPS);
                toReturn.add(SubTipoDespesa.RPPS);
            } else if (tipo.isDividaPublica() || tipo.isPrecatorio()) {
                toReturn.add(SubTipoDespesa.JUROS);
                toReturn.add(SubTipoDespesa.OUTROS_ENCARGOS);
                toReturn.add(SubTipoDespesa.VALOR_PRINCIPAL);
            } else {
                toReturn.add(SubTipoDespesa.NAO_APLICAVEL);
            }
        }
        return toReturn;
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

    public boolean isVigenciaEncerrada() {
        return selecionado.getFimVigencia() != null && facade.isVigenciaEncerrada(selecionado.getFimVigencia());
    }
}
