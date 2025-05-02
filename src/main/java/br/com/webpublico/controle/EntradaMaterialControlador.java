/*
 * Codigo gerado automaticamente em Fri Feb 24 10:25:37 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoIngressoBaixa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EntradaMaterialFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean(name = "entradaMaterialControlador")
public class EntradaMaterialControlador extends PrettyControlador<EntradaMaterial> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(EntradaMaterialControlador.class);

    @EJB
    protected EntradaMaterialFacade facade;
    protected ItemEntradaMaterial itemEntradaMaterial;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private List<LocalEstoque> locaisEstoque;
    private List<HierarquiaOrganizacional> hierarquiasOrcamentaria;
    private LocalEstoque localEstoque;

    public EntradaMaterialControlador() {
        metadata = new EntidadeMetaData(EntradaMaterial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica());
        selecionado.setDataEntrada(facade.getSistemaFacade().getDataOperacao());
        novoItemMaterial();
    }

    @Override
    public void editar() {
        super.editar();
        novoItemMaterial();
    }

    public void novoItemMaterial() {
        itemEntradaMaterial = new ItemEntradaMaterial(selecionado);
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            validarRegrasDoNegocio();
            selecionado = facade.salvarEntradaMaterial(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionaParaVer();
        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void redirecionaParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void validarRegrasDoNegocio() throws ValidacaoException {
        ValidacaoException vex = new ValidacaoException();
        if (selecionado.getItens().isEmpty()) {
            vex.adicionarMensagemDeOperacaoNaoPermitida("Deve haver pelo menos um item na lista da entrada.");
        }
        validarLocalELoteSelecionado(vex);
        validarVinculoMaterialComLocalEstoque(vex);
        vex.lancarException();
    }

    protected void validarLocalELoteSelecionado(ValidacaoException vex) {
        for (ItemEntradaMaterial iem : selecionado.getItens()) {
            if (iem.getLocalEstoque() == null) {
                vex.adicionarMensagemDeCampoObrigatorio("Informe o local de estoque do item " + iem.getDescricao() + ".");
            } else if (iem.localEstoqueFechadoNaData(selecionado.getDataEntrada())) {
                vex.adicionarMensagemDeOperacaoNaoPermitida("O local de estoque do item " + iem.getDescricao() + " está fechado.");
            }
            if (iem.getLoteMaterial() == null && iem.requerLote()) {
                vex.adicionarMensagemDeCampoObrigatorio("O campo lote deve ser informado para o item " + iem.getDescricao() + ".");
            }
            if (iem.getLoteMaterial() != null && iem.loteVencidoNaData(selecionado.getDataEntrada())) {
                vex.adicionarMensagemDeOperacaoNaoPermitida("O lote do item " + iem.getDescricao() + " está vencido.");
            }
            if (iem.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria() == null) {
                vex.adicionarMensagemDeCampoObrigatorio("A unidade orçamentária do item " + iem.getDescricao() + " deve ser preenchida.");
            }
        }
    }

    protected void validarVinculoMaterialComLocalEstoque(ValidacaoException vex) {
        if (!getSelecionado().ehEntradaCompra()) {
            for (ItemEntradaMaterial iem : selecionado.getItens()) {
                if (iem.getMaterial() != null && iem.getLocalEstoque() != null) {
                    boolean possuiVinculo = facade.getLocalEstoqueFacade().materialPossuiVinculoComLocalEstoque(iem.getMaterial(), iem.getLocalEstoque());

                    if (!possuiVinculo) {
                        String url = FacesUtil.getRequestContextPath() + "/associativa-material-local-de-estoque/";

                        String mensagem = "Não é permitido realizar movimentações do material <b>" + iem.getMaterial().getCodigoDescricaoCurta() + "</b> no local de estoque <b>" + iem.getLocalEstoque().getCodigoDescricao() + "</b>. ";
                        mensagem += "<b><a href='" + url + "' target='_blank' style='color: blue;'>Clique aqui para Editar o Local de Estoque</a></b>";

                        Web.poeNaSessao("LOCAL_ESTOQUE", iem.getLocalEstoque());
                        vex.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
                    }
                }
            }
        }
    }

    protected void validarEstoqueBloqueado(ValidacaoException vex) {
        for (ItemEntradaMaterial item : selecionado.getItens()) {
            if (item.getLocalEstoque() != null && facade.getEstoqueFacade().estoqueBloqueado(item.getMaterial(), item.getLocalEstoque(), item.getUnidadeOrcamentaria(), selecionado.getDataEntrada(), item.getTipoEstoque())) {
                vex.adicionarMensagemDeOperacaoNaoPermitida("O estoque do item " + item.getDescricao() + " está bloqueado no momento.");
            }
        }
    }


    public List<LoteMaterial> completaLoteMaterial(String parte) {
        return facade.getLoteMaterialFacade().buscarFiltrandoPorMaterial(parte, itemEntradaMaterial.getMaterial());
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalAdministrativaOndeUsuarioEhGestorMaterial(parte.trim(), null,
            facade.getSistemaFacade().getDataOperacao(), facade.getSistemaFacade().getUsuarioCorrente());
    }

    public ItemEntradaMaterial getItemEntradaMaterial() {
        return itemEntradaMaterial;
    }

    public void setItemEntradaMaterial(ItemEntradaMaterial itemEntradaMaterial) {
        this.itemEntradaMaterial = itemEntradaMaterial;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return facade.getPessoaFacade().listaTodasPessoas(parte);
    }

    public List<SelectItem> getHierarquiasOrcamentariasDaAdministrativa() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        try {
            if (itemEntradaMaterial != null && itemEntradaMaterial.getLocalEstoquePai() != null && itemEntradaMaterial.getLocalEstoquePai().getUnidadeOrganizacional() != null) {
                for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(
                    itemEntradaMaterial.getLocalEstoquePai().getUnidadeOrganizacional(),
                    facade.getSistemaFacade().getDataOperacao())) {
                    toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
                }
            }
        } catch (NullPointerException ex) {
            logger.error("getHierarquiasOrcamentarias {}", ex);
        }
        return toReturn;
    }

    public List<SelectItem> getHierarquiasOrcamentariasDaAdministrativaItemEntrada(ItemEntradaMaterial item) {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        try {
            if (item.getLocalEstoqueOrcamentario() != null && item.getLocalEstoqueOrcamentario().getLocalEstoque() != null) {
                for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(
                    item.getLocalEstoqueOrcamentario().getLocalEstoque().getUnidadeOrganizacional(),
                    facade.getSistemaFacade().getDataOperacao())) {
                    toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
                }
            }
        } catch (NullPointerException ex) {
            logger.error("getHierarquiasOrcamentarias {}", ex);
        }
        return toReturn;
    }

    public List<SelectItem> getSelectItensHierarquiasOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (HierarquiaOrganizacional obj : getHierarquiasOrcamentaria()) {
            toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
        }
        return toReturn;
    }

    public boolean isVisualizar() {
        return Operacoes.VER.equals(operacao);
    }

    public boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }

        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        if (hierarquiaAdministrativa != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaAdministrativa.getSubordinada());
        }
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public void adicionarUnidadeDaHierarquia() {
        if (getHierarquiaAdministrativa() != null) {
            getSelecionado().setUnidadeOrganizacional(getHierarquiaAdministrativa().getSubordinada());
        }
    }


    public HierarquiaOrganizacional hierarquiaOrcamentaria(UnidadeOrganizacional unidadeOrganizacional) {
        if (unidadeOrganizacional != null) {
            return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), unidadeOrganizacional, facade.getSistemaFacade().getDataOperacao());
        }
        return new HierarquiaOrganizacional();
    }

    public List<TipoIngresso> completarTipoIngressoIncorporacao(String filtro) {
        return facade.getTipoIngressoFacade().buscarFiltrandoPorTipoIngressoAndTipoBem(filtro, TipoIngressoBaixa.INCORPORACAO, TipoBem.ESTOQUE);
    }

    public List<TipoIngresso> completarTipoIngressoCompra(String filtro) {
        return facade.getTipoIngressoFacade().buscarFiltrandoPorTipoIngressoAndTipoBem(filtro, TipoIngressoBaixa.COMPRA, TipoBem.ESTOQUE);
    }

    public List<LocalEstoque> getLocaisEstoque() {
        return locaisEstoque;
    }

    public void setLocaisEstoque(List<LocalEstoque> locaisEstoque) {
        this.locaisEstoque = locaisEstoque;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrcamentaria() {
        return hierarquiasOrcamentaria;
    }

    public void setHierarquiasOrcamentaria(List<HierarquiaOrganizacional> hierarquiasOrcamentaria) {
        this.hierarquiasOrcamentaria = hierarquiasOrcamentaria;
    }
}
