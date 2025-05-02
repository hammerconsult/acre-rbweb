package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BensIntangiveisFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Claudio
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-bens-intangiveis", pattern = "/contabil/bens-intangiveis/novo/", viewId = "/faces/financeiro/orcamentario/bens/bensintangiveis/edita.xhtml"),
    @URLMapping(id = "editar-bens-intangiveis", pattern = "/contabil/bens-intangiveis/editar/#{bensIntangiveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/bensintangiveis/edita.xhtml"),
    @URLMapping(id = "ver-bens-intangiveis", pattern = "/contabil/bens-intangiveis/ver/#{bensIntangiveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/bensintangiveis/visualizar.xhtml"),
    @URLMapping(id = "listar-bens-intangiveis", pattern = "/contabil/bens-intangiveis/listar/", viewId = "/faces/financeiro/orcamentario/bens/bensintangiveis/lista.xhtml")
})
public class BensIntangiveisControlador extends PrettyControlador<BensIntangiveis> implements Serializable, CRUD {

    @EJB
    private BensIntangiveisFacade bensIntangiveisFacade;
    private ConverterAutoComplete converterGrupoBem;
    private ConverterAutoComplete converterTipoIngresso;
    private ConverterAutoComplete converterTipoBaixa;
    private boolean habilitaTipoBaixa;
    private boolean habilitaTipoIngresso;

    public BensIntangiveisControlador() {
        super(BensIntangiveis.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return bensIntangiveisFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/bens-intangiveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setDataBem(bensIntangiveisFacade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(bensIntangiveisFacade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(bensIntangiveisFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        habilitaTipoBaixa = false;
        habilitaTipoIngresso = false;
    }

    @URLAction(mappingId = "editar-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "ver-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        selecionado = bensIntangiveisFacade.recuperar(selecionado.getId());
        recuperaTipoBaixaTipoIngresso();
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            if (validaCampos()) {
                try {
                    if (operacao.equals(Operacoes.NOVO)) {
                        bensIntangiveisFacade.salvarNovo(selecionado);
                        FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                    } else {
                        bensIntangiveisFacade.salvar(selecionado);
                        FacesUtil.addOperacaoRealizada("Registro alterado com sucesso.");
                    }
                    redireciona();
                } catch (ExcecaoNegocioGenerica ex) {
                    FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
                } catch (Exception e) {
                    FacesUtil.addOperacaoNaoRealizada("Não foi possível salvar. Detalhes do erro: " + e.getMessage());
                }
            }
        }
    }

    public String getCodigoUnidadeOrcamentaria() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            HierarquiaOrganizacional hierarquiaOrganizacional = bensIntangiveisFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
            return hierarquiaOrganizacional.getCodigo();
        }
        return "";
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            HierarquiaOrganizacional hierarquiaOrganizacional = bensIntangiveisFacade.getHierarquiaOrganizacionalFacade().getHierarquiaAdministrativaDaOrcamentaria(selecionado.getUnidadeOrganizacional(), selecionado.getDataBem());
            return hierarquiaOrganizacional;
        }
        return null;
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getTipoIngresso() == null && habilitaTipoIngresso) {
            FacesUtil.addCampoObrigatorio(" O campo Tipo de Ingresso deve ser informado.");
            retorno = false;
        }
        if (selecionado.getTipoBaixaBens() == null && habilitaTipoBaixa) {
            FacesUtil.addCampoObrigatorio("O campo Tipo de Baixa deve ser informado.");
            retorno = false;
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addOperacaoNaoPermitida("O campo valor deve ser maior que zero (0).");
            retorno = false;
        }
        return retorno;
    }

    public boolean renderizaTipoIngresso() {
        if (selecionado.getTipoOperacaoBemEstoque() != null
            && habilitaTipoIngresso) {
            return true;
        }
        return false;
    }

    public boolean renderizaTipoBaixa() {
        if (selecionado.getTipoOperacaoBemEstoque() != null
            && habilitaTipoBaixa) {
            return true;
        }
        return false;
    }

    public List<TipoLancamento> getTiposLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        for (TipoLancamento tipo : TipoLancamento.values()) {
            lista.add(tipo);
        }
        return lista;
    }

    public List<SelectItem> getListaTipoGrupo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoGrupo tg : TipoGrupo.values()) {
            if (tg.getClasseDeUtilizacao() == selecionado.getClass()) {
                toReturn.add(new SelectItem(tg, tg.getDescricao()));
            }
        }
        return toReturn;
    }

    private List<TipoOperacaoBensIntangiveis> excecoesDeItensListados() {
        List<TipoOperacaoBensIntangiveis> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensIntangiveis.AQUISICAO_BENS_INTANGIVEIS);
        lista.add(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA);
        lista.add(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA);
        return lista;
    }

    public List<SelectItem> getOperacoesBemIntangiveis() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoOperacaoBensIntangiveis tipo : TipoOperacaoBensIntangiveis.values()) {
            if (!excecoesDeItensListados().contains(tipo)) {
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return lista;
    }

    public List<TipoIngresso> completaTipoIngresso(String parte) {
        return bensIntangiveisFacade.getTipoIngressoFacade().listaFiltrandoPorTipoBem(parte.trim(), TipoBem.INTANGIVEIS);
    }

    public List<TipoBaixaBens> completaTipoBaixaBens(String parte) {
        return bensIntangiveisFacade.getTipoBaixaBensFacade().listaFiltrandoPorTipoBem(parte.trim(), TipoBem.INTANGIVEIS);
    }

    public List<GrupoBem> completaGrupoBem(String parte) {
        return bensIntangiveisFacade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte, TipoBem.INTANGIVEIS);
    }

    public ConverterAutoComplete getConverterGrupoBem() {
        if (converterGrupoBem == null) {
            converterGrupoBem = new ConverterAutoComplete(GrupoBem.class, bensIntangiveisFacade.getGrupoBemFacade());
        }
        return converterGrupoBem;
    }

    public void setaPessoa(SelectEvent evt) {
        Pessoa p = (Pessoa) evt.getObject();
    }

    public ConverterAutoComplete getConverterTipoIngresso() {
        if (converterTipoIngresso == null) {
            converterTipoIngresso = new ConverterAutoComplete(TipoIngresso.class, bensIntangiveisFacade.getTipoIngressoFacade());
        }
        return converterTipoIngresso;
    }

    public ConverterAutoComplete getConverterTipoBaixa() {
        if (converterTipoBaixa == null) {
            converterTipoBaixa = new ConverterAutoComplete(TipoBaixaBens.class, bensIntangiveisFacade.getTipoBaixaBensFacade());
        }
        return converterTipoBaixa;
    }

    public void listener() {
        setaEvento();
        recuperaTipoBaixaTipoIngresso();
    }

    public void setaEvento() {
        try {

            selecionado.setEventoContabil(null);
            if (selecionado.getTipoOperacaoBemEstoque() != null) {
                ConfigBensIntangiveis configBensIntangiveis = bensIntangiveisFacade.getConfigBensIntangiveisFacade().recuperaEventoPorTipoOperacao(selecionado.getTipoOperacaoBemEstoque(), selecionado.getTipoLancamento(), selecionado.getDataBem());
                Preconditions.checkNotNull(configBensIntangiveis, "Não foi encontrada uma Configuração para os Parametros informados ");
                selecionado.setEventoContabil(configBensIntangiveis.getEventoContabil());
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Evento não encontrado! ", ex.getMessage()));
        }
    }

    public void recuperaTipoBaixaTipoIngresso() {
        if (selecionado.getTipoOperacaoBemEstoque() == null) {
            selecionado.setTipoIngresso(null);
            selecionado.setTipoBaixaBens(null);
            habilitaTipoBaixa = false;
            habilitaTipoIngresso = false;
        } else if (isTipoOperacaoParaBaixa()) {
            selecionado.setTipoIngresso(null);
            habilitaTipoIngresso = false;
            habilitaTipoBaixa = true;
        } else if (isTipoOperacaoParaIngresso()) {
            selecionado.setTipoBaixaBens(null);
            habilitaTipoBaixa = false;
            habilitaTipoIngresso = true;
        } else {
            habilitaTipoBaixa = false;
            habilitaTipoIngresso = false;
        }
    }

    private boolean isTipoOperacaoParaBaixa() {
        return TipoOperacaoBensIntangiveis.DESINCORPORACAO_BENS_INTANGIVEIS.equals(selecionado.getTipoOperacaoBemEstoque());
    }

    private boolean isTipoOperacaoParaIngresso() {
        return TipoOperacaoBensIntangiveis.INCORPORACAO_BENS_INTANGIVEIS.equals(selecionado.getTipoOperacaoBemEstoque());
    }

    public boolean isHabilitaTipoBaixa() {
        return habilitaTipoBaixa;
    }

    public void setHabilitaTipoBaixa(boolean habilitaTipoBaixa) {
        this.habilitaTipoBaixa = habilitaTipoBaixa;
    }

    public boolean isHabilitaTipoIngresso() {
        return habilitaTipoIngresso;
    }

    public void setHabilitaTipoIngresso(boolean habilitaTipoIngresso) {
        this.habilitaTipoIngresso = habilitaTipoIngresso;
    }
}
