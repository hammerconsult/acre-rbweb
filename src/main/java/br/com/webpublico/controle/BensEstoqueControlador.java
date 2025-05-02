/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BensEstoqueFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.DateSelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */
@ManagedBean(name = "bensEstoqueControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-bens-estoque", pattern = "/contabil/bens-estoque/novo/", viewId = "/faces/financeiro/orcamentario/bens/bensestoque/edita.xhtml"),
    @URLMapping(id = "editar-bens-estoque", pattern = "/contabil/bens-estoque/editar/#{bensEstoqueControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/bensestoque/edita.xhtml"),
    @URLMapping(id = "ver-bens-estoque", pattern = "/contabil/bens-estoque/ver/#{bensEstoqueControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/bensestoque/visualizar.xhtml"),
    @URLMapping(id = "listar-bens-estoque", pattern = "/contabil/bens-estoque/listar/", viewId = "/faces/financeiro/orcamentario/bens/bensestoque/lista.xhtml"),})
public class BensEstoqueControlador extends PrettyControlador<BensEstoque> implements Serializable, CRUD {

    @EJB
    private BensEstoqueFacade bensEstoqueFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterGrupoMaterial;
    private ConverterAutoComplete converterTipoIngresso, converterTipoBaixa;
    private boolean habilitaTipoBaixa;
    private boolean habilitaTipoIngresso;

    public BensEstoqueControlador() {
        super(BensEstoque.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return bensEstoqueFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/bens-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new BensEstoque();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setDataBem(sistemaControlador.getDataOperacao());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        habilitaTipoBaixa = false;
        habilitaTipoIngresso = false;
    }

    @URLAction(mappingId = "ver-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        selecionado = bensEstoqueFacade.recuperar(selecionado.getId());
        recuperaTipoBaixaTipoIngresso();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                if (operacao.equals(Operacoes.NOVO)) {
                    bensEstoqueFacade.salvarNovo(selecionado);
                    FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                } else {
                    bensEstoqueFacade.salvar(selecionado);
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

    public HierarquiaOrganizacional getUnidadeOrcamentaria() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            return bensEstoqueFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(sistemaControlador.getDataOperacao(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
        return null;
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            return bensEstoqueFacade.getHierarquiaOrganizacionalFacade().getHierarquiaAdministrativaDaOrcamentaria(selecionado.getUnidadeOrganizacional(), selecionado.getDataBem());
        }
        return null;
    }

    public boolean renderizaTipoIngresso() {
        if (selecionado.getOperacoesBensEstoque() != null
                && habilitaTipoIngresso) {
            return true;
        }
        return false;
    }

    public boolean renderizaTipoBaixa() {
        if (selecionado.getOperacoesBensEstoque() != null
                && habilitaTipoBaixa) {
            return true;
        }
        return false;
    }

    public void listener() {
        setaEvento();
        recuperaTipoBaixaTipoIngresso();
    }

    public void setaEvento() {
        try {

            selecionado.setEventoContabil(null);
            if (selecionado.getOperacoesBensEstoque() != null) {
                ConfigBensEstoque configBensEstoque = bensEstoqueFacade.getConfigBensEstoqueFacade().recuperaEventoPorTipoOperacao(selecionado.getOperacoesBensEstoque(), selecionado.getTipoLancamento(), selecionado.getDataBem());
                Preconditions.checkNotNull(configBensEstoque, "Não foi encontrada uma Configuração para os Parametros informados ");
                selecionado.setEventoContabil(configBensEstoque.getEventoContabil());

            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Evento não encontrado! ", ex.getMessage()));
        }
    }

    public Boolean validaCampos() {
        boolean retorno = true;
        retorno = Util.validaCampos(selecionado);
        if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            FacesUtil.addOperacaoNaoPermitida(" O valor deve ser maior que zero (0).");
            retorno = false;
        }
        if (selecionado.getTipoIngresso() == null && habilitaTipoIngresso) {
            FacesUtil.addCampoObrigatorio(" O campo Tipo de Ingresso deve ser informado.");
            retorno = false;
        }
        if (selecionado.getTipoBaixaBens() == null && habilitaTipoBaixa) {
            FacesUtil.addCampoObrigatorio(" O campo Tipo de Baixa deve ser informado.");
            retorno = false;
        }
        return retorno;
    }

    public Boolean getVerificaEdicao() {
        if (selecionado == null) {
            return false;
        }
        if (selecionado.getId() != null) {
            return true;
        } else {
            return false;
        }
    }

    public List<BensEstoque> getLista() {
        return bensEstoqueFacade.listaDecrescente();
    }

    public List<GrupoMaterial> completaGrupoMaterial(String parte) {
        return bensEstoqueFacade.getGrupoMaterialFacade().listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public List<TipoIngresso> completaTipoIngresso(String parte) {
        return bensEstoqueFacade.getTipoIngressoFacade().listaFiltrandoPorTipoBem(parte.trim(), TipoBem.ESTOQUE);
    }

    public List<TipoBaixaBens> completaTipoBaixaBens(String parte) {
        return bensEstoqueFacade.getTipoBaixaBensFacade().listaFiltrandoPorTipoBem(parte.trim(), TipoBem.ESTOQUE);
    }

    public List<TipoLancamento> getTiposLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        for (TipoLancamento tipo : TipoLancamento.values()) {
            lista.add(tipo);
        }
        return lista;
    }

    private List<TipoOperacaoBensEstoque> excecoesDeItensListados() {
        List<TipoOperacaoBensEstoque> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE);
        lista.add(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA);
        lista.add(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA);
        return lista;
    }

    public List<SelectItem> getOperacoesBensEstoque() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoOperacaoBensEstoque tipo : TipoOperacaoBensEstoque.values()) {
            if (!excecoesDeItensListados().contains(tipo)) {
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return lista;
    }

    public List<SelectItem> getTiposEstoque() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoEstoque tipo : TipoEstoque.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

//    public List<SelectItem> getTiposIngressos() {
//        List<SelectItem> lista = new ArrayList<>();
//        lista.add(new SelectItem(null, ""));
//        for (TipoIngresso tipo : bensEstoqueFacade.getTipoIngressoFacade().listaPorTipoBem(TipoBem.ESTOQUE)) {
//            lista.add(new SelectItem(tipo, tipo.getDescricao()));
//        }
//        return lista;
//    }
//
//    public List<SelectItem> getTiposBaixaBens() {
//        List<SelectItem> lista = new ArrayList<>();
//        lista.add(new SelectItem(null, ""));
//        lista.add(new SelectItem(null, ""));
//        for (TipoBaixaBens tipo : bensEstoqueFacade.getTipoBaixaBensFacade().listaPorTipoBem(TipoBem.ESTOQUE)) {
//            lista.add(new SelectItem(tipo, tipo.getDescricao()));
//        }
//        return lista;
//    }

    public ConverterAutoComplete getConverterGrupoMaterial() {
        if (converterGrupoMaterial == null) {
            converterGrupoMaterial = new ConverterAutoComplete(GrupoMaterial.class, bensEstoqueFacade.getGrupoMaterialFacade());
        }
        return converterGrupoMaterial;
    }

    public ConverterAutoComplete getConverterTipoIngresso() {
        if (converterTipoIngresso == null) {
            converterTipoIngresso = new ConverterAutoComplete(TipoIngresso.class, bensEstoqueFacade.getTipoIngressoFacade());
        }
        return converterTipoIngresso;
    }

    public ConverterAutoComplete getConverterTipoBaixa() {
        if (converterTipoBaixa == null) {
            converterTipoBaixa = new ConverterAutoComplete(TipoBaixaBens.class, bensEstoqueFacade.getTipoBaixaBensFacade());
        }
        return converterTipoBaixa;
    }

    public void recuperaTipoBaixaTipoIngresso() {
        if (selecionado.getOperacoesBensEstoque() == null) {
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
            habilitaTipoIngresso = false;
            habilitaTipoBaixa = false;
        }
    }

    public boolean isTipoOperacaoParaBaixa() {
        if (selecionado.getOperacoesBensEstoque().equals(TipoOperacaoBensEstoque.BAIXA_BENS_ESTOQUE_POR_CONSUMO)) {
            return true;
        }

        return false;
    }

    public boolean isTipoOperacaoParaIngresso() {
        if (selecionado.getOperacoesBensEstoque().equals(TipoOperacaoBensEstoque.INCORPORACAO_BENS_ESTOQUE)) {
            return true;
        }

        return false;
    }

    public void setaData(DateSelectEvent event) {
        selecionado.setDataBem((Date) sistemaControlador.getDataOperacao());
    }

    public BensEstoque getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(BensEstoque selecionado) {
        this.selecionado = selecionado;
    }

    public boolean isHabilitaTipoBaixa() {
        return habilitaTipoBaixa;
    }

    public boolean isHabilitaTipoIngresso() {
        return habilitaTipoIngresso;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
