/*
 * Codigo gerado automaticamente em Wed Jun 15 12:16:05 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@ManagedBean(name = "producaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProducao", pattern = "/producao/novo/", viewId = "/faces/administrativo/materiais/producaopropria/edita.xhtml"),
    @URLMapping(id = "editarProducao", pattern = "/producao/editar/#{producaoControlador.id}/", viewId = "/faces/administrativo/materiais/producaopropria/edita.xhtml"),
    @URLMapping(id = "listarProducao", pattern = "/producao/listar/", viewId = "/faces/administrativo/materiais/producaopropria/lista.xhtml"),
    @URLMapping(id = "verProducao", pattern = "/producao/ver/#{producaoControlador.id}/", viewId = "/faces/administrativo/materiais/producaopropria/visualizar.xhtml")
})
public class ProducaoControlador extends PrettyControlador<Producao> implements Serializable, CRUD {

    @EJB
    private ProducaoFacade producaoFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private LoteMaterialFacade loteMaterialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private EstoqueLoteMaterialFacade estoqueLoteMaterialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    private MoneyConverter moneyConverter;
    private ItemProduzido itemProduzidoSelecionado;
    private ItemProduzido itemProduzidoEditado;
    private List<InsumoAplicado> listaInsumoEdicao;
    private InsumoProducao insumoProducaoSelecionado;
    private InsumoProducao insumoProducaoEdicao;
    private InsumoAplicado insumoAplicadoSelecionado;
    private HierarquiaOrganizacional hierarquia;
    private boolean editandoInsumo;
    private boolean editandoItem;
    private boolean editandoProducao;
    private BigDecimal quantidadeEmEstoque;
    private LocalEstoqueOrcamentario estoqueOrcamentario;

    public ProducaoControlador() {
        super(Producao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return producaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/producao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoProducao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        parametrosIniciais();
    }

    @Override
    @URLAction(mappingId = "verProducao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarProducao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaRegrasDoNegocio()) {
            try {
                producaoFacade.salvarNovoAlternativo(selecionado);
                FacesUtil.addOperacaoRealizada("A produção foi salva com sucesso.");
            } catch (ValidacaoException vex) {
                FacesUtil.printAllFacesMessages(vex.getMensagens());
            } catch (ParseException ex) {
                logger.debug("Erro de Parse:", ex);
            } catch (Exception ex) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
                logger.error("Erro:", ex);
            } finally {
                redireciona();
            }
        }
    }

    public void manipularInsumos(ItemProduzido itemProduzido) {
        if (isOperacaoVer()) {
            itemProduzido.setInsumosAplicados(producaoFacade.recuperarInsumos(itemProduzido));
        }
        itemProduzidoSelecionado = itemProduzido;
        novoInsumoProducao();
    }

    public boolean validarInicioDoCadastroDeItemProduzido() {
        boolean validou = true;

        if (hierarquia == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe uma unidade organizacional!");
            validou = false;
        }

        if (selecionado.getDataProducao() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe a data da produção!");
            validou = false;
        }

        return validou;
    }

    public void novoItemProduzidoComProducao() {
        itemProduzidoSelecionado = new ItemProduzido(selecionado);
    }

    public ItemProduzido getItemProduzidoSelecionado() {
        return itemProduzidoSelecionado;
    }

    public void setItemProduzidoSelecionado(ItemProduzido itemProduzidoSelecionado) {
        this.itemProduzidoSelecionado = itemProduzidoSelecionado;
    }

    public InsumoProducao getInsumoProducaoSelecionado() {
        return insumoProducaoSelecionado;
    }

    public void setInsumoProducaoSelecionado(InsumoProducao insumoProducaoSelecionado) {
        this.insumoProducaoSelecionado = insumoProducaoSelecionado;
    }

    public InsumoAplicado getInsumoAplicadoSelecionado() {
        return insumoAplicadoSelecionado;
    }

    public void setInsumoAplicadoSelecionado(InsumoAplicado insumoAplicadoSelecionado) {
        this.insumoAplicadoSelecionado = insumoAplicadoSelecionado;
    }

    public HierarquiaOrganizacional getHierarquia() {
        return hierarquia;
    }

    public void setHierarquia(HierarquiaOrganizacional hierarquia) {
        if (this.hierarquia != null && !this.hierarquia.equals(hierarquia)) {
            novoItemProduzidoComProducao();
            selecionado.getItemsProduzidos().clear();
            FacesUtil.atualizarComponente("Formulario:tabViewPrincipal:panelMaterial");
        }
        this.hierarquia = hierarquia;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void setMoneyConverter(MoneyConverter moneyConverter) {
        this.moneyConverter = moneyConverter;
    }

    public boolean isEditandoInsumo() {
        return editandoInsumo;
    }

    public void setEditandoInsumo(boolean editandoInsumo) {
        this.editandoInsumo = editandoInsumo;
    }

    public boolean isEditandoItem() {
        return editandoItem;
    }

    public void setEditandoItem(boolean editandoItem) {
        this.editandoItem = editandoItem;
    }

    public boolean isEditandoProducao() {
        return editandoProducao;
    }

    public void setEditandoProducao(boolean editandoProducao) {
        this.editandoProducao = editandoProducao;
    }

    private void parametrosIniciais() {
        itemProduzidoSelecionado = new ItemProduzido();
        insumoProducaoSelecionado = new InsumoProducao();
        editandoProducao = false;
        hierarquia = null;
    }

    public List<Material> completaMaterialInsumo(String parte) {
        if (insumoProducaoSelecionado != null && insumoProducaoSelecionado.getLocalEstoque() != null) {
            return materialFacade.buscarMateriaisPorLocalDeEstoqueECodigoOuDescricao(parte.trim(), insumoProducaoSelecionado.getLocalEstoque(), StatusMaterial.getSituacoesDeferidoInativo());
        }
        return null;
    }

    public List<LoteMaterial> completaLoteItemProduzido(String parte) {
        UnidadeOrganizacional uo = hierarquia.getSubordinada();
        Material material = itemProduzidoSelecionado.getMaterial();

        return loteMaterialFacade.recuperaLotesPorMaterialEUnidadeMesmoQueNaoExistaEstoque(parte.trim().toLowerCase(), material, uo);
    }

    public List<LoteMaterial> completaLoteInsumoProducao(String parte) {
        UnidadeOrganizacional uo = hierarquia.getSubordinada();
        Material material = insumoProducaoSelecionado.getMaterial();

        return completaLoteMaterial(parte, material, uo);
    }

    public List<LoteMaterial> completaLoteMaterial(String parte, Material material, UnidadeOrganizacional uo) {
        return loteMaterialFacade.listaFiltrandoPorMaterialEUnidade(parte.trim().toLowerCase(), material, uo);
    }

    public List<LocalEstoque> completaLocalEstoque(String parte) {
        if (hierarquia != null) {
            selecionado.setUnidadeOrganizacional(hierarquia.getSubordinada());
            List<LocalEstoque> localEstoques = localEstoqueFacade.completarLocalEstoqueAbertos(parte.trim().toLowerCase(), selecionado.getUnidadeOrganizacional());
            if (localEstoques != null && localEstoques.isEmpty()) {
                FacesUtil.addAtencao("Nenhum local de estoque encontrado para a unidade organizacional " + hierarquia.getSubordinada());
                return new ArrayList<>();
            }
            return localEstoques;
        } else {
            validarInicioDoCadastroDeItemProduzido();
        }
        return new ArrayList<>();
    }

    public boolean exibirInputLoteMaterialItemProduzido() {
        if (itemProduzidoSelecionado != null) {
            if (itemProduzidoSelecionado.getMaterial() != null) {
                return itemProduzidoSelecionado.getMaterial().getControleDeLote();
            }
        }

        return false;
    }

    public void adicionarItemProduzido() {
        if (!validarItemProduzido()) {
            return;
        }
        try {
            if (editandoItem) {
                if (listaInsumoEdicao != null) {
                    for (InsumoAplicado insumoAplicado : listaInsumoEdicao) {
                        insumoAplicado.setItemProduzido(itemProduzidoSelecionado);
                    }
                }
                listaInsumoEdicao = null;
                itemProduzidoEditado = null;
                editandoItem = false;
            }
            itemProduzidoSelecionado.setProducao(selecionado);
            itemProduzidoSelecionado.setTipoEstoque(TipoEstoque.PRODUTOS_ACABADOS_PRINCIPAL);
            itemProduzidoSelecionado.setLocalEstoqueOrcamentario(localEstoqueFacade.recuperarOuCriarNovoLocalEstoqueOrcamentario(itemProduzidoSelecionado.getLocalEstoque(), itemProduzidoSelecionado.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria(), sistemaFacade.getDataOperacao()));
            selecionado.setItemsProduzidos(Util.adicionarObjetoEmLista(selecionado.getItemsProduzidos(), itemProduzidoSelecionado));
            novoItemProduzidoComProducao();
        } catch (ExcecaoNegocioGenerica eng) {
            FacesUtil.addOperacaoNaoRealizada(eng.getMessage());
        }
    }

    public void removerItemProduzido(ItemProduzido ip) {
        selecionado.getItemsProduzidos().remove(ip);
        novoItemProduzidoComProducao();
    }

    public void editarItemProduzido(ItemProduzido ip) {
        itemProduzidoEditado = ip;
        listaInsumoEdicao = ip.getInsumosAplicados();
        itemProduzidoSelecionado = (ItemProduzido) Util.clonarObjeto(ip);
        selecionado.getItemsProduzidos().remove(ip);
        editandoItem = true;
    }

    public void cancelarEdicao() {
        selecionado.getItemsProduzidos().add(itemProduzidoEditado);
        novoItemProduzidoComProducao();
        itemProduzidoEditado = null;
        editandoItem = false;
        FacesUtil.executaJavaScript("aguarde.hide()");
        return;
    }

    public void cancelarEdicaoInsumo() {
        itemProduzidoSelecionado.getInsumosAplicados().add(insumoAplicadoSelecionado);
        editandoInsumo = false;
        novoInsumoProducao();
    }

    private boolean validarItemProduzido() {
        boolean validou = true;

        if (itemProduzidoSelecionado.getMaterial() == null) {
            exibirMensagemMaterialNaoInformado("item produzido");
            validou = false;
        } else {
            if (itemProduzidoSelecionado.getMaterial().getControleDeLote()) {
                if (itemProduzidoSelecionado.getLoteMaterial() == null) {
                    exibirMensagemLoteNãoInformado();
                    validou = false;
                }
            }

            if (itemProduzidoRepetido(itemProduzidoSelecionado) && !editandoItem) {
                exibirMensagemItemProduzidoRepetido(itemProduzidoSelecionado);
                validou = false;
            }

            if (itemProduzidoSelecionado.getLocalEstoque() == null) {
                exibirMensagemLocalEstoqueNãoInformado();
                validou = false;
            } else if (!localEstoqueFacade.recuperar(itemProduzidoSelecionado.getLocalEstoque().getId()).isDoTipo(TipoEstoque.PRODUTOS_ACABADOS_PRINCIPAL)) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O local de estoque " + itemProduzidoSelecionado.getLocalEstoque().getDescricao() + " não é do tipo Produto Acabado!");
                validou = false;
            }
        }

        if (itemProduzidoSelecionado.getQuantidade() == null) {
            exibirMensagemQuantidadeNaoInformada();
            validou = false;
        } else {
            if (itemProduzidoSelecionado.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                exibirMensagemQuantidadeMenorOuIgualAZero();
                validou = false;
            }
        }

        if (itemProduzidoSelecionado.getValor() == null) {
            exibirMensagemValorNaoInformado();
            validou = false;
        } else {
            if (itemProduzidoSelecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                exibirMensagemValorMenorOuIgualAZero();
                validou = false;
            }
        }

        return validou;
    }

    private void exibirMensagemLocalEstoqueNãoInformado() {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe o local de estoque!");
    }

    private boolean itemProduzidoRepetido(ItemProduzido item) {
        for (ItemProduzido itemProduzido : selecionado.getItemsProduzidos()) {
            if (itemProduzido.getMaterial().equals(item.getMaterial())) {
                return true;
            }
        }

        return false;
    }

    public void novoInsumoProducao() {
        insumoProducaoSelecionado = new InsumoProducao();
        quantidadeEmEstoque = BigDecimal.ZERO;
    }

    public boolean exibirInputLoteMaterialInsumoProducao() {
        if (insumoProducaoSelecionado != null) {
            if (insumoProducaoSelecionado.getMaterial() != null) {
                return insumoProducaoSelecionado.getMaterial().getControleDeLote();
            }
        }

        return false;
    }

    private void novoInsumoAplicadoComItemProduzidoEInsumoProducao() {
        insumoAplicadoSelecionado = new InsumoAplicado(itemProduzidoSelecionado, insumoProducaoSelecionado);
    }

    public void adicionarInsumoProducao() {
        if (!editandoInsumo) {
            novoInsumoAplicadoComItemProduzidoEInsumoProducao();
        }

        if (validaAdicaoDoInsumoProducao()) {
            insumoProducaoSelecionado.setLocalEstoqueOrcamentario(localEstoqueFacade.recuperarLocalEstoqueOrcamentario(insumoProducaoSelecionado.getLocalEstoque(), insumoProducaoSelecionado.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria(), sistemaFacade.getDataOperacao()));
            insumoAplicadoSelecionado.setItemProduzido(itemProduzidoSelecionado);
            insumoAplicadoSelecionado.setInsumoProducao(insumoProducaoSelecionado);
            itemProduzidoSelecionado.setInsumosAplicados(Util.adicionarObjetoEmLista(itemProduzidoSelecionado.getInsumosAplicados(), insumoAplicadoSelecionado));
            novoInsumoProducao();
            editandoInsumo = false;
        }
    }

    public void removerInsumoProducao(InsumoAplicado ia) {
        itemProduzidoSelecionado.getInsumosAplicados().remove(ia);
    }

    public void editarInsumoProducao(InsumoAplicado ia) {
        insumoAplicadoSelecionado = ia;
        insumoProducaoEdicao = ia.getInsumoProducao();
        insumoProducaoSelecionado = (InsumoProducao) Util.clonarObjeto(insumoProducaoEdicao);
        itemProduzidoSelecionado.getInsumosAplicados().remove(ia);
        editandoInsumo = true;
        recuperarEstoque();
    }

    private boolean validaAdicaoDoInsumoProducao() {
        boolean validou = true;

        if (quantidadeEmEstoque.equals(BigDecimal.ZERO)) {
            FacesUtil.addOperacaoNaoPermitida("Quantidade em estoque igual a zero(0).");
            validou = false;
        }

        if (insumoProducaoSelecionado.getMaterial() == null) {
            exibirMensagemMaterialNaoInformado("insumo");
            validou = false;
        } else {
            if (insumoProducaoSelecionado.getMaterial().getControleDeLote()) {
                if (insumoProducaoSelecionado.getLoteMaterial() == null) {
                    exibirMensagemLoteNãoInformado();
                    validou = false;
                }
            }

            if (insumoRepetido(insumoProducaoSelecionado.getMaterial(), itemProduzidoSelecionado) && !editandoInsumo) {
                exibirMensagemInsumoRepetido(insumoProducaoSelecionado.getMaterial());
                validou = false;
            }

            if (insumoIgualAoItemProducao()) {
                exibirMensagemInsumoIgualItemProducao();
                validou = false;
            }

            if (insumoProducaoSelecionado.getLocalEstoque() == null) {
                exibirMensagemLocalEstoqueNãoInformado();
                validou = false;
            }
        }

        if (insumoProducaoSelecionado.getQuantidade() == null) {
            exibirMensagemQuantidadeNaoInformada();
            validou = false;
        } else {
            if (insumoProducaoSelecionado.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                exibirMensagemQuantidadeMenorOuIgualAZero();
                validou = false;
            }
        }

        return validou;
    }

    private boolean insumoIgualAoItemProducao() {
        return insumoProducaoSelecionado.getMaterial().equals(itemProduzidoSelecionado.getMaterial());
    }

    public List<SelectItem> getHierarquiasOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        try {
            for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(itemProduzidoSelecionado.getLocalEstoque().getUnidadeOrganizacional(), sistemaFacade.getDataOperacao())) {
                toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
            }
        } catch (NullPointerException ex) {
        }

        return toReturn;
    }

    private boolean insumoRepetido(Material material, ItemProduzido ip) {
        if (ip.getInsumosAplicados() != null) {
            for (InsumoAplicado ia : ip.getInsumosAplicados()) {
                if (ia.getInsumoProducao().getMaterial().equals(material)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Boolean validaCampos() {
        return Util.validaCampos(selecionado);
    }

    private boolean validaRegrasDoNegocio() {
        boolean validou = true;

        if (!validaCampos()) {
            validou = false;
        }

        if (validou && nenhumItemProducaoInformado()) {
            validou = false;
        }

        if (validou && !validaQuantidadeDosInsumosUtilizados()) {
            validou = false;
        }

        if (validou && !validaSeMaterialPodeEstarEmDeterminadoLocalDeEstoque()) {
            validou = false;
        }

        if (validou && !validaMaterialLocalEstoqueEstaBloqueado()) {
            return false;
        }

        return validou;
    }

    private boolean nenhumItemProducaoInformado() {
        if (selecionado.getItemsProduzidos() == null) {
            exibirMensagemNenhumItemProducaoInformado();
            return true;
        }

        if (selecionado.getItemsProduzidos().isEmpty()) {
            exibirMensagemNenhumItemProducaoInformado();
            return true;
        }

        return false;
    }

    private String formatarDateDDMMYYYY(Date dataSaida) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(dataSaida);
    }

    private boolean validaQuantidadeDosInsumosUtilizados() {
        Estoque estoque;
        boolean validou = true;

        for (ItemProduzido itemProduzido : selecionado.getItemsProduzidos()) {
            if (itemProduzido.getInsumosAplicados() == null || itemProduzido.getInsumosAplicados().isEmpty()) {
                validou = false;
                exibirMensagemListaDeInsumosNaoPreenchida(itemProduzido.getMaterial());
            } else {
                for (InsumoAplicado insumoAplicado : itemProduzido.getInsumosAplicados()) {
                    InsumoProducao insumoProducao = insumoAplicado.getInsumoProducao();

                    try {
                        HashSet<Material> materiais = new HashSet<>(Lists.newArrayList(insumoProducao.getMaterial()));
                        ConsolidadorDeEstoque novoConsolidador = localEstoqueFacade.getNovoConsolidadorComReservaEstoque(insumoProducao.getLocalEstoqueOrcamentario().getLocalEstoque(), materiais, sistemaFacade.getDataOperacao());
                        if (!validaEstoque(novoConsolidador, insumoProducao, itemProduzido, validou)) {
                            validou = false;
                        } else {
                            if (!validaEstoqueLote(insumoProducao, itemProduzido, novoConsolidador.getEstoque(insumoProducao.getMaterial(), insumoProducao.getLocalEstoqueOrcamentario()), validou, selecionado.getDataProducao())) {
                                validou = false;
                            }
                        }
                    } catch (ExcecaoNegocioGenerica ex) {
                        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
                        validou = false;
                    }
                }
            }
        }

        return validou;
    }

    private boolean validaEstoque(ConsolidadorDeEstoque consolidadorDeEstoque, InsumoProducao insumo, ItemProduzido itemProduzido, boolean validou) {
        if (consolidadorDeEstoque.getEstoque(insumo.getMaterial(), insumo.getLocalEstoqueOrcamentario()) == null) {
            exibirMensagemDeNenhumaEntradaFoiFeitaParaEsteEstoque(insumo, itemProduzido);
            validou = false;
        } else {
            if (consolidadorDeEstoque.getFisicoConsolidado(insumo.getMaterial()).compareTo(insumo.getQuantidade()) < 0) {
                exibirMensagemDeEstoqueInsuficiente(insumo, itemProduzido, consolidadorDeEstoque.getEstoque(insumo.getMaterial(), insumo.getLocalEstoqueOrcamentario()));
                validou = false;
            }
        }
        return validou;
    }

    private boolean validaEstoqueLote(InsumoProducao insumo, ItemProduzido itemProduzido, Estoque estoque, boolean validou, Date dataSaida) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");

        EstoqueLoteMaterial elm;
        if (insumo.getMaterial().getControleDeLote()) {
            elm = estoqueLoteMaterialFacade.recuperarEstoqueLoteMaterial(estoque, insumo.getLoteMaterial());

            if (elm == null) {
                elm = estoqueLoteMaterialFacade.recuperarEstoqueLoteMaterial(insumo.getLoteMaterial(), insumo.getLocalEstoqueOrcamentario());
            }

            if (elm == null) {
                exibirMensagemDeNenhumaEntradaFoiFeitaParaEsteEstoqueLote(insumo, itemProduzido);
                validou = false;
            } else {
                if (elm.getQuantidade().compareTo(insumo.getQuantidade()) < 0) {
                    exibirMensagemDeEstoqueLoteInsuficiente(insumo, elm);
                    validou = false;
                }
            }
        }
        return validou;
    }

    public boolean validaExclusaoProducao() {
        boolean validou = true;

        for (ItemProduzido ip : selecionado.getItemsProduzidos()) {
            try {
                producaoFacade.validaQuantidadesFuturasDoEstoque(selecionado.getDataProducao(), ip.getQuantidade(), ip.getMaterial(), ip.getLocalEstoque());

                if (ip.getMaterial().getControleDeLote()) {
                    producaoFacade.validaQuantidadesFuturasDoEstoqueLote(selecionado.getDataProducao(), ip.getQuantidade(), ip.getMaterial(), ip.getLocalEstoque(), ip.getLoteMaterial());
                }

                BigDecimal financeiro = ip.getValor().multiply(ip.getQuantidade());
                producaoFacade.validaValoresFinanceirosFuturos(selecionado.getDataProducao(), financeiro, ip.getMaterial(), ip.getLocalEstoque());
            } catch (OperacaoEstoqueException ex) {
                validou = false;
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), ex.getMessage());
            }
        }

        return validou;
    }

    private boolean validaSeMaterialPodeEstarEmDeterminadoLocalDeEstoque() {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");

        boolean validou = true;

        for (ItemProduzido itemProduzido : selecionado.getItemsProduzidos()) {
            boolean retorno = localEstoqueFacade.materialPossuiVinculoComHierarquiaDoLocalEstoque(itemProduzido.getMaterial(), itemProduzido.getLocalEstoque());

            if (!retorno) {
                exibiMensagemDeErroVinculoDeMaterialELocalEstoque(itemProduzido.getMaterial().getDescricao(), itemProduzido.getLocalEstoque().getDescricao());
                validou = false;
                continue;
            } else {
                if (itemProduzido.getInsumosAplicados() == null || itemProduzido.getInsumosAplicados().isEmpty()) {
                    validou = false;
                    exibirMensagemListaDeInsumosNaoPreenchida(itemProduzido.getMaterial());
                } else {
                    for (InsumoAplicado insumoAplicado : itemProduzido.getInsumosAplicados()) {
                        retorno = localEstoqueFacade.materialPossuiVinculoComHierarquiaDoLocalEstoque(insumoAplicado.getInsumoProducao().getMaterial(), insumoAplicado.getInsumoProducao().getLocalEstoque());

                        if (!retorno) {
                            exibiMensagemDeErroVinculoDeMaterialELocalEstoque(insumoAplicado.getInsumoProducao().getMaterial().getDescricao(), insumoAplicado.getInsumoProducao().getLocalEstoque().getDescricao());
                            validou = false;
                        }
                    }
                }
            }
        }

        return validou;
    }

    private boolean validaMaterialLocalEstoqueEstaBloqueado() {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");

        boolean deuCerto = true;
        for (ItemProduzido itemProduzido : selecionado.getItemsProduzidos()) {
            if (estoqueFacade.estoqueBloqueado(itemProduzido.getMaterial(), itemProduzido.getLocalEstoqueOrcamentario(), itemProduzido.getTipoEstoque(), selecionado.getDataProducao())) {
                exibiMensagemDeEstoqueDoMaterialBloqueado(itemProduzido.getMaterial().toStringAutoComplete(), itemProduzido.getLocalEstoque().toStringAutoComplete());
                deuCerto = false;
                for (InsumoAplicado insumoAplicado : itemProduzido.getInsumosAplicados()) {
                    if (estoqueFacade.estoqueBloqueado(insumoAplicado.getInsumoProducao().getMaterial(), insumoAplicado.getInsumoProducao().getLocalEstoqueOrcamentario(), itemProduzido.getTipoEstoque(), selecionado.getDataProducao())) {
                        exibiMensagemDeEstoqueDoMaterialBloqueado(insumoAplicado.getInsumoProducao().getMaterial().toStringAutoComplete(), insumoAplicado.getInsumoProducao().getLocalEstoque().toStringAutoComplete());
                        deuCerto = false;
                    }
                }
            }
        }
        return deuCerto;
    }

    //mensagens begin
    private void exibirMensagemListaDeInsumosNaoPreenchida(Material material) {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe os insumos utilizados na produção do item " + material.getDescricao().toUpperCase() + "!");
    }

    private void exibirMensagemMaterialNaoInformado(String tipo) {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe o " + tipo.toUpperCase() + "!");
    }

    private void exibirMensagemLoteNãoInformado() {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe o lote!");
    }

    private void exibirMensagemQuantidadeNaoInformada() {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe a quantidade!");
    }

    private void exibirMensagemQuantidadeMenorOuIgualAZero() {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não é permitido informar quantidades negativas ou iguais a zero!");
    }

    private void exibirMensagemValorNaoInformado() {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe o valor!");
    }

    private void exibirMensagemValorMenorOuIgualAZero() {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não é permitido informar valores negativos ou iguais a zero!");
    }

    private void exibirMensagemInsumoRepetido(Material material) {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O insumo " + material.getDescricao() + " já foi informado!");
    }

    private void exibirMensagemInsumoIgualItemProducao() {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O insumo " + insumoProducaoSelecionado.getMaterial().getDescricao().toUpperCase() + ", não pode ser igual ao item produzido!");
    }

    private void exibirMensagemNenhumItemProducaoInformado() {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe um ou mais itens produzidos!");
    }

    private void exibirMensagemDeEstoqueInsuficiente(InsumoProducao insumo, ItemProduzido itemProduzido, Estoque est) {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(),
            "O insumo " + insumo.getMaterial().getDescricao().toUpperCase()
                + ", utilizado no item " + itemProduzido.getMaterial().getDescricao().toUpperCase()
                + ", não possui quantidade em estoque suficiente! Quantidade em estoque = " + est.getFisico());
    }

    private void exibirMensagemDeEstoqueLoteInsuficiente(InsumoProducao insumo, EstoqueLoteMaterial elm) {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(),
            "O item " + insumo.getMaterial().getDescricao().toUpperCase()
                + " não possui quantidade em lote suficiente! Quantidade em lote = " + elm.getQuantidade());
    }

    private void exibirMensagemDeNenhumaEntradaFoiFeitaParaEsteEstoqueLote(InsumoProducao insumo, ItemProduzido itemProduzido) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");

        FacesUtil.addError(
            "Quantidade insuficiente na data " + this.formatarDateDDMMYYYY(selecionado.getDataProducao()) + "!",
            "Nenhuma entrada foi realizada para o lote "
                + insumo.getLoteMaterial().getIdentificacao().toUpperCase() + ", do insumo " + insumo.getMaterial().getDescricao().toUpperCase()
                + ", no local de estoque " + insumo.getLocalEstoque().getDescricao().toUpperCase()
                + ", utilizado no item " + itemProduzido.getMaterial().getDescricao().toUpperCase()
                + ", ou todos os itens deste lote foram consumidos, portanto seu saldo é zero!");
    }

    private void exibirMensagemDeNenhumaEntradaFoiFeitaParaEsteEstoque(InsumoProducao insumo, ItemProduzido itemProduzido) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");

        FacesUtil.addError(
            "Quantidade insuficiente na data " + this.formatarDateDDMMYYYY(selecionado.getDataProducao()) + "!",
            "Nenhuma entrada foi realizada para o insumo "
                + insumo.getMaterial().getDescricao().toUpperCase() + ", no local de estoque " + insumo.getLocalEstoque().getDescricao().toUpperCase()
                + ", utilizado no item " + itemProduzido.getMaterial().getDescricao().toUpperCase() + ", portanto seu saldo é zero!");
    }

    private void exibirMensagemItemProduzidoRepetido(ItemProduzido item) {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O item " + item.getMaterial().getDescricao().toUpperCase() + " já foi informado!");
    }

    private void exibiMensagemDeErroVinculoDeMaterialELocalEstoque(String descricaoMaterial, String descricaoLocalEstoque) {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não é permitido realizar movimentações do material " + descricaoMaterial.toUpperCase() + " no local de estoque " + descricaoLocalEstoque.toUpperCase());
    }

    private void exibiMensagemDeEstoqueDoMaterialBloqueado(String descMaterial, String descLocalEstoque) {
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não é permitido realizar movimentações do material " + descMaterial.toUpperCase() + " no local de estoque " + descLocalEstoque.toUpperCase() + ", pois está sendo realizado o inventário.");
    }

    public void recuperarEstoque() {
        try{
            if (insumoProducaoSelecionado != null && insumoProducaoSelecionado.getMaterial() != null) {
                estoqueOrcamentario = localEstoqueFacade.recuperarLocalEstoqueOrcamentario(insumoProducaoSelecionado.getLocalEstoque(), insumoProducaoSelecionado.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria(), sistemaFacade.getDataOperacao());
                if (estoqueOrcamentario != null) {
                    HashSet<Material> materiais = new HashSet<>(Lists.newArrayList(insumoProducaoSelecionado.getMaterial()));
                    ConsolidadorDeEstoque novoConsolidador = localEstoqueFacade.getNovoConsolidadorComReservaEstoque(estoqueOrcamentario.getLocalEstoque(), materiais, sistemaFacade.getDataOperacao());
                    quantidadeEmEstoque = novoConsolidador.getFisicoConsolidado(insumoProducaoSelecionado.getMaterial());
                }
            }
        }catch (ExcecaoNegocioGenerica ex){
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public BigDecimal getQuantidadeEmEstoque() {
        return quantidadeEmEstoque;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        if (itemProduzidoSelecionado != null && itemProduzidoSelecionado.getLocalEstoqueOrcamentario().getLocalEstoque() != null) {
            return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                itemProduzidoSelecionado.getLocalEstoqueOrcamentario().getLocalEstoque().getUnidadeOrganizacional(),
                sistemaFacade.getDataOperacao());
        }
        return new HierarquiaOrganizacional();
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativaInsumo() {
        if (insumoProducaoSelecionado.getLocalEstoqueOrcamentario().getLocalEstoque() != null) {
            return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                insumoProducaoSelecionado.getLocalEstoqueOrcamentario().getLocalEstoque().getUnidadeOrganizacional(),
                sistemaFacade.getDataOperacao());
        }
        return new HierarquiaOrganizacional();
    }

    public HierarquiaOrganizacional hierarquiaOrcamentaria(UnidadeOrganizacional unidadeOrganizacional) {
        if (unidadeOrganizacional != null) {
            return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), unidadeOrganizacional, sistemaFacade.getDataOperacao());
        }
        return new HierarquiaOrganizacional();
    }
}
