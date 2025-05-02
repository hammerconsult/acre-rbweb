package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LevantamentoEstoqueFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLevantamentoEstoque", pattern = "/levantamento-estoque/novo/", viewId = "/faces/administrativo/materiais/levantamento-estoque/edita.xhtml"),
    @URLMapping(id = "editarLevantamentoEstoque", pattern = "/levantamento-estoque/editar/#{levantamentoEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/levantamento-estoque/edita.xhtml"),
    @URLMapping(id = "listarLevantamentoEstoque", pattern = "/levantamento-estoque/listar/", viewId = "/faces/administrativo/materiais/levantamento-estoque/lista.xhtml"),
    @URLMapping(id = "verLevantamentoEstoque", pattern = "/levantamento-estoque/ver/#{levantamentoEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/levantamento-estoque/visualizar.xhtml")
})
public class LevantamentoEstoqueControlador extends PrettyControlador<LevantamentoEstoque> implements Serializable, CRUD {

    @EJB
    private LevantamentoEstoqueFacade facade;
    private Material materialFiltro;
    private GrupoMaterial grupofiltro;
    private Set<Material> filtrosMaterial;
    private Boolean selecionarTodos;
    private ItemLevantamentoEstoque itemLevantamentoEstoqueNavegacao;
    private List<HierarquiaOrganizacional> hierarquiasOrcamentarias;

    public LevantamentoEstoqueControlador() {
        super(LevantamentoEstoque.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoLevantamentoEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributos();
    }

    @URLAction(mappingId = "verLevantamentoEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    @URLAction(mappingId = "editarLevantamentoEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        buscarUnidadeOrcamentaria();
        if (selecionado.isEfetivado()) {
            FacesUtil.addOperacaoNaoPermitida("Não é permitido editar um levantamento de estoque já efetivado.");
            redirecionarParaVer();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/levantamento-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarCamposObrigatorios();
            validarValoresInformadoParaItens();
            desconsiderarItensZerados();
            selecionado = facade.salvarLevantamento(selecionado);
            FacesUtil.addOperacaoRealizada("Levantamento salvo com sucesso");
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void finalizar() {
        try {
            validarCamposObrigatorios();
            validarValoresInformadoParaItens();
            desconsiderarItensZerados();
            selecionado = facade.efetivarLevantamento(selecionado);
            FacesUtil.addOperacaoRealizada("Levantamento finalizado com sucesso");
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void inicializarAtributos() {
        this.selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        this.selecionado.setDataLevantamento(facade.getSistemaFacade().getDataOperacao());
        this.filtrosMaterial = new HashSet<>();
        this.selecionarTodos = Boolean.FALSE;
        LevantamentoEstoqueControlador recuperado = (LevantamentoEstoqueControlador) Web.pegaDaSessao(LevantamentoEstoqueControlador.class);
        if (recuperado != null) {
            this.setSelecionado(recuperado.getSelecionado());
            this.setFiltrosMaterial(recuperado.getFiltrosMaterial());
            if (recuperado.itemLevantamentoEstoqueNavegacao != null) {
                LoteMaterial lote = (LoteMaterial) Web.pegaDaSessao(LoteMaterial.class);
                if (lote != null) {
                    this.itemLevantamentoEstoqueNavegacao = recuperado.itemLevantamentoEstoqueNavegacao;
                    this.itemLevantamentoEstoqueNavegacao.setLoteMaterial(lote);
                }
            }
            Web.limpaNavegacao();
        }
    }

    public Material getMaterialFiltro() {
        return materialFiltro;
    }

    public void setMaterialFiltro(Material materialFiltro) {
        this.materialFiltro = materialFiltro;
    }

    public GrupoMaterial getGrupofiltro() {
        return grupofiltro;
    }

    public void setGrupofiltro(GrupoMaterial grupofiltro) {
        this.grupofiltro = grupofiltro;
    }

    public Set<Material> getFiltrosMaterial() {
        return filtrosMaterial;
    }

    public void setFiltrosMaterial(Set<Material> filtrosMaterial) {
        this.filtrosMaterial = filtrosMaterial;
    }

    public List<Material> getListaMateriais() {
        List<Material> retorno = new ArrayList<>();
        if (filtrosMaterial != null) {
            retorno.addAll(filtrosMaterial);
        }
        return retorno;
    }

    public void buscarUnidadeOrcamentaria() {
        hierarquiasOrcamentarias = Lists.newArrayList();
        if (selecionado.getHierarquiaAdministrativa() != null && selecionado.getHierarquiaAdministrativa().getSubordinada() != null) {
            hierarquiasOrcamentarias = facade.getHierarquiaFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(selecionado.getHierarquiaAdministrativa().getSubordinada(), facade.getSistemaFacade().getDataOperacao());
        }
    }

    public List<SelectItem> getUnidadesOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (hierarquiasOrcamentarias != null && !hierarquiasOrcamentarias.isEmpty()) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : hierarquiasOrcamentarias) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }

    public List<LocalEstoque> buscarLocaisDeEstoquePorUsuarioGestorAndUnidadeAdministrativa(String s) {
        if (selecionado.getHierarquiaAdministrativa() != null) {
            return facade.getLocalEstoqueFacade().completaLocaisDeEstoquePorUsuarioGestorAndUnidadeAdministrativa(s, facade.getSistemaFacade().getUsuarioCorrente(), facade.getSistemaFacade().getDataOperacao(), selecionado.getHierarquiaAdministrativa().getSubordinada());
        }
        return null;
    }

    public void limparMateriais() {
        filtrosMaterial = new HashSet<>();
        materialFiltro = null;
        grupofiltro = null;
    }

    public void buscarMateriais() {
        filtrosMaterial = new HashSet<>();
        if (grupofiltro != null) {
            filtrosMaterial.addAll(facade.getMaterialFacade().buscarMaterialPorGrupoMaterial(grupofiltro, "", StatusMaterial.getSituacoesDeferidoInativo()));
        }
        if (materialFiltro != null) {
            filtrosMaterial.add(facade.getMaterialFacade().buscaMaterialPorCodigo(materialFiltro.getCodigo()));
        }
        if (grupofiltro == null && materialFiltro == null) {
            filtrosMaterial.addAll(facade.getMaterialFacade().lista());
        }
        if (filtrosMaterial.isEmpty()) {
            FacesUtil.addOperacaoNaoRealizada("Nenhum material deferido encontrado.");
        }
        materialFiltro = null;
        grupofiltro = null;
    }

    public void abrirCatalogoMateriais() {
        try {
            validarCamposObrigatorios();
            FacesUtil.executaJavaScript("dlgCatalogoMateriais.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarMaterial() {
        for (Material material : filtrosMaterial) {
            if (material.getSelecionadoNaLista()) {
                if (selecionado.hasMaterialNosItensDoLevantamento(material)) continue;
                ItemLevantamentoEstoque item = new ItemLevantamentoEstoque(material, selecionado);
                Util.adicionarObjetoEmLista(this.selecionado.getItensLevantamentoEstoque(), item);
                material.setSelecionadoNaLista(false);
            }
        }
        limparMateriais();
    }

    public boolean hasItensAdicionados() {
        return selecionado.getItensLevantamentoEstoque() != null && !selecionado.getItensLevantamentoEstoque().isEmpty();
    }

    public Boolean getSelecionarTodos() {
        return selecionarTodos;
    }

    public void setSelecionarTodos(Boolean selecionarTodos) {
        this.selecionarTodos = selecionarTodos;
        for (Material material : getListaMateriais()) {
            material.setSelecionadoNaLista(selecionarTodos);
        }
    }

    public Boolean isTodosSelecionados() {
        for (Material material : getListaMateriais()) {
            if (!material.getSelecionadoNaLista()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getHierarquiaAdministrativa() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo unidade administrativa deve ser informado.");
        }
        if (selecionado.getHierarquiaOrcamentaria() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo unidade orçamentária deve ser informado.");
        }
        if (selecionado.getLocalEstoque() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo local de estoque deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarValoresInformadoParaItens() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItensLevantamentoEstoque().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione ao menos um material na lista para continuar.");
        }
        ve.lancarException();
        for (ItemLevantamentoEstoque itemLevantamentoEstoque : selecionado.getItensLevantamentoEstoque()) {
            if (itemLevantamentoEstoque.getQuantidade() == null || itemLevantamentoEstoque.getQuantidade().compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a <b> QUANTIDADE </b> para o material <b>" + itemLevantamentoEstoque.getMaterial().toString().toUpperCase() + "</b>");
            }
            if (itemLevantamentoEstoque.getValorUnitario() == null || itemLevantamentoEstoque.getValorUnitario().compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o <b> VALOR UNITÁRIO </b> para o material <b>" + itemLevantamentoEstoque.getMaterial().toString().toUpperCase() + "</b>");
            }
            if (itemLevantamentoEstoque.getMaterial().getControleDeLote() && itemLevantamentoEstoque.getLoteMaterial() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o <b> LOTE </b> para o material <b>" + itemLevantamentoEstoque.getMaterial().toString().toUpperCase() + "</b>");
            }
        }
        ve.lancarException();
    }

    private void desconsiderarItensZerados() {
        List<ItemLevantamentoEstoque> remover = new ArrayList<>();
        for (ItemLevantamentoEstoque item : selecionado.getItensLevantamentoEstoque()) {
            if (item.getValorUnitario().equals(BigDecimal.ZERO) && item.getQuantidade().equals(BigDecimal.ZERO)) {
                remover.add(item);
            }
        }
        selecionado.getItensLevantamentoEstoque().removeAll(remover);
    }

    public void navegarParaLoteMaterial(ItemLevantamentoEstoque itemLev) {
        this.itemLevantamentoEstoqueNavegacao = itemLev;
        Web.navegacao(this.getUrlAtual(), "/lote-de-material/novo/", itemLev.getMaterial(), this);
    }

    public void calcularValorItem(ItemLevantamentoEstoque item) {
        try {
            validarCalculoValorItem(item);
            item.calcularValorTotal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCalculoValorItem(ItemLevantamentoEstoque item) {
        ValidacaoException ve = new ValidacaoException();
        if (item.getQuantidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Quantidade deve ser informado.");
        }
        if (item.getValorUnitario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Unitário deve ser informado.");
        }
        ve.lancarException();
        if (item.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Quantidade deve ser maior que zero (0).");
            item.setQuantidade(BigDecimal.ZERO);
            FacesUtil.atualizarComponente("Formulario:panelTabelaMat");
        }
        ve.lancarException();
        if (item.getValorUnitario().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor Unitário deve ser maior que zero (0).");
            item.setValorUnitario(BigDecimal.ZERO);
            FacesUtil.atualizarComponente("Formulario:panelTabelaMat");
        }
        ve.lancarException();
    }

    public void setSituacaoEmElaboracao() {
        facade.setSituacaoEmElaboracao(selecionado);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

}
