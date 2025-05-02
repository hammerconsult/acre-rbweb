package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigObrigacaoAPagar;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaDespesa;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoObrigacaoAPagarFacade;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mga on 22/06/2017.
 */
@ManagedBean(name = "configuracaoObrigacaoAPagarControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-obrigacaopagar", pattern = "/configuracao-obrigacao-a-pagar/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/configobrigacaopagar/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-obrigacaopagar", pattern = "/configuracao-obrigacao-a-pagar/editar/#{configuracaoObrigacaoAPagarControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configobrigacaopagar/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-obrigacaopagar", pattern = "/configuracao-obrigacao-a-pagar/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/configobrigacaopagar/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-obrigacaopagar", pattern = "/configuracao-obrigacao-a-pagar/ver/#{configuracaoObrigacaoAPagarControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/configobrigacaopagar/visualizar.xhtml")
})
public class ConfiguracaoObrigacaoAPagarControlador extends ConfigEventoSuperControlador<ConfigObrigacaoAPagar> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoObrigacaoAPagarFacade facade;
    private ConfigObrigacaoAPagar configuracaoNaoAlterada;

    public ConfiguracaoObrigacaoAPagarControlador() {
        super(ConfigObrigacaoAPagar.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-obrigacao-a-pagar/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-configuracao-obrigacaopagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setSubTipoDespesa(SubTipoDespesa.NAO_APLICAVEL);
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "edita-configuracao-obrigacaopagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        configuracaoNaoAlterada = (ConfigObrigacaoAPagar) getFacede().recuperar(super.getId());
    }

    @URLAction(mappingId = "ver-configuracao-obrigacaopagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<SelectItem> getTiposReconhecimentos() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem("", null));
        for (TipoReconhecimentoObrigacaoPagar tipo : TipoReconhecimentoObrigacaoPagar.retornaTipoReconhecimentosObrigacaoPagar()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<TipoContaDespesa> getTipoContas() {
        List<TipoContaDespesa> toReturn = new ArrayList<>();
        if (selecionado != null && selecionado.getConta() != null) {
            TipoContaDespesa tipo = ((ContaDespesa) selecionado.getConta()).getTipoContaDespesa();
            if (!TipoContaDespesa.NAO_APLICAVEL.equals(tipo) && tipo != null) {
                selecionado.setTipoContaDespesa(tipo);
                toReturn.add(tipo);
            } else {
                List<TipoContaDespesa> busca = facade.getContaFacade().buscarTiposContasDespesaNosFilhosDaConta(((ContaDespesa) selecionado.getConta()));
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

    public List<SelectItem> getCategoriasOrcamentarias() {
        return Util.getListSelectItem(CategoriaOrcamentaria.values(), false);
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

    public List<Conta> completarContasDespesa(String parte) {
        return facade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), facade.getSistemaFacade().getExercicioCorrente());
    }

    public List<EventoContabil> completarEventoContabil(String parte) {
        return facade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.OBRIGACAO_APAGAR, selecionado.getTipoLancamento());
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
