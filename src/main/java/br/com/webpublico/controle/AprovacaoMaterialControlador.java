/*
 * Codigo gerado automaticamente em Mon Feb 27 15:06:12 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AprovacaoMaterialFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean(name = "aprovacaoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAvaliacaoRequisicao", pattern = "/avaliacao-de-requisicao-de-material/novo/", viewId = "/faces/administrativo/materiais/aprovacaomaterial/edita.xhtml"),
    @URLMapping(id = "editarAvaliacaoRequisicao", pattern = "/avaliacao-de-requisicao-de-material/editar/#{aprovacaoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/aprovacaomaterial/edita.xhtml"),
    @URLMapping(id = "listarAvaliacaoRequisicao", pattern = "/avaliacao-de-requisicao-de-material/listar/", viewId = "/faces/administrativo/materiais/aprovacaomaterial/lista.xhtml"),
    @URLMapping(id = "verAvaliacaoRequisicao", pattern = "/avaliacao-de-requisicao-de-material/ver/#{aprovacaoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/aprovacaomaterial/visualizar.xhtml")
})
public class AprovacaoMaterialControlador extends PrettyControlador<AprovacaoMaterial> implements Serializable, CRUD {

    @EJB
    private AprovacaoMaterialFacade facade;
    private SaidaRequisicaoMaterial saidaRecuperada;
    private ItemAprovacaoMaterial itemSelecionado;
    private Material novoMaterial;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<ItemAprovacaoMaterial> itensSelecionados;
    private ConsolidadorDeEstoque consolidadorDeEstoque;

    public AprovacaoMaterialControlador() {
        metadata = new EntidadeMetaData(AprovacaoMaterial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void salvar() {
        try {
            validarRegrasNegocio();
            facade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVer();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void validarRegrasNegocio() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getItensAprovados() == null || selecionado.getItensAprovados().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Cada item deve ter uma quantidade aprovada ou deve ser rejeitado.");
        }
        ve.lancarException();
        validarEstoqueEPoliticaDeEstoqueDeCadaItem(ve);
        ve.lancarException();
    }

    @Override
    @URLAction(mappingId = "novaAvaliacaoRequisicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataDaAprovacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setAprovador(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    public void excluir() {
        if (saidaRecuperada != null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Esta aprovação está associada a " + selecionado.getRequisicaoMaterial().toString().toUpperCase() + " e também está associada a saída de requisição de Material " + saidaRecuperada.toString().toUpperCase() + ".");
            return;
        }
        super.excluir();
    }

    @Override
    @URLAction(mappingId = "editarAvaliacaoRequisicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        selecionado.setItensAprovados(facade.buscarItensAvaliacaoRequisicao(selecionado));
        saidaRecuperada = facade.getRequisicaoMaterialFacade().validaAssociacaoDaRequisicaoComSaidaMaterial(selecionado.getRequisicaoMaterial());
        Collections.sort(selecionado.getItensAprovados());
        consolidadorDeEstoque = facade.getLocalEstoqueFacade().getNovoConsolidadorDesconsiderandoReservasDaAprovacao(selecionado, selecionado.getItensAprovados());
        recuperarQuantidadeSaidaItens();
    }

    @Override
    @URLAction(mappingId = "verAvaliacaoRequisicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        Collections.sort(selecionado.getItensAprovados());
        saidaRecuperada = facade.getRequisicaoMaterialFacade().validaAssociacaoDaRequisicaoComSaidaMaterial(selecionado.getRequisicaoMaterial());
        itensSelecionados = new ArrayList<>();
        consolidadorDeEstoque = facade.getLocalEstoqueFacade().getNovoConsolidadorDesconsiderandoReservasDaAprovacao(selecionado, selecionado.getItensAprovados());
        recuperarQuantidadeSaidaItens();
    }

    private HierarquiaOrganizacional getHierarquiaDaUnidade() {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getRequisicaoMaterial().getUnidadeRequerente(),
            facade.getSistemaFacade().getDataOperacao());
    }

    public void adicionarUnidade() {
        if (hierarquiaOrganizacional != null) {
            selecionado.getRequisicaoMaterial().setUnidadeRequerente(hierarquiaOrganizacional.getSubordinada());
        }
    }

    @Override
    public void cancelar() {
        super.cancelar();
        selecionado.setItensAprovados(facade.buscarItensAvaliacaoRequisicao(selecionado));
    }

    @Override
    public String getCaminhoPadrao() {
        return "/avaliacao-de-requisicao-de-material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void recuperaItensDaRequisicao() {
        if (facade.getRequisicaoMaterialFacade().requisicaoJaFoiAvaliada(selecionado.getRequisicaoMaterial())) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), "A requisição selecionada já foi avaliada.");
        } else {
            selecionado.setRequisicaoMaterial(facade.getRequisicaoMaterialFacade().recuperar(selecionado.getRequisicaoMaterial().getId()));
            associarItensRequisicaoComItensAprovacao();
        }
    }

    private void associarItensRequisicaoComItensAprovacao() {
        selecionado.setItensAprovados(new ArrayList<ItemAprovacaoMaterial>());
        HashSet<Material> materiais = new HashSet<>();
        for (ItemRequisicaoMaterial itemRequisicaoMaterial : selecionado.getRequisicaoMaterial().getItensRequisitados()) {
            selecionado.getItensAprovados().add(new ItemAprovacaoMaterial(selecionado, itemRequisicaoMaterial));
            itemRequisicaoMaterial.setMaterialAprovado(itemRequisicaoMaterial.getMaterialRequisitado());
            itemRequisicaoMaterial.setQuantidadeSaida(facade.getSaidaMaterialFacade().getQuantidadeSaida(itemRequisicaoMaterial));
            materiais.add(itemRequisicaoMaterial.getMaterialRequisitado());
        }
        consolidadorDeEstoque = facade.getLocalEstoqueFacade().getNovoConsolidadorComReservaEstoque(selecionado.getRequisicaoMaterial().getLocalEstoqueOrigem(), materiais, facade.getSistemaFacade().getDataOperacao());
        Collections.sort(selecionado.getItensAprovados());
    }

    public void atualizarSituacao(ItemAprovacaoMaterial item) {
        if (item.getQuantidadeAprovada() == null) {
            item.setTipoAprovacao(null);
            return;
        }

        if (item.getQuantidadeAprovada().compareTo(BigDecimal.ZERO) == 0) {
            item.setTipoAprovacao(TipoAprovacaoMaterial.REJEITADO);
            return;
        }

        if (item.getQuantidadeAprovada().compareTo(item.getItemRequisicaoMaterial().getQuantidade()) == 0) {
            item.setTipoAprovacao(TipoAprovacaoMaterial.APROVADO_TOTAL);
            return;
        }

        if (item.getQuantidadeAprovada().compareTo(item.getItemRequisicaoMaterial().getQuantidade()) < 0) {
            item.setTipoAprovacao(TipoAprovacaoMaterial.APROVADO_PARCIAL);
            return;
        }

        if (item.getQuantidadeAprovada().compareTo(item.getItemRequisicaoMaterial().getQuantidade()) > 0) {
            item.setTipoAprovacao(null);
            return;
        }
    }

    public Boolean renderizaCampoDaInputQuantidade(ItemAprovacaoMaterial item) {
        if (item.getItemRequisicaoMaterial().getQuantidadeSaida() != null && item.getQuantidadeAprovada() != null) {
            if (item.getQuantidadeAprovada().compareTo(BigDecimal.ZERO) > 0) {
                if (item.getItemRequisicaoMaterial().getQuantidadeSaida().compareTo(item.getQuantidadeAprovada()) == 0 && item.getItemRequisicaoMaterial().getQuantidade().compareTo(item.getQuantidadeAprovada()) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public SelectItem[] tiposDeAprovacoesParaFiltroView() {
        SelectItem[] opcoes = new SelectItem[4];

        opcoes[0] = new SelectItem("", "Situação");
        opcoes[1] = new SelectItem("Aprovado Total", "Aprovado Total");
        opcoes[2] = new SelectItem("Aprovado Parcial", "Aprovado Parcial");
        opcoes[3] = new SelectItem("Rejeitado", "Rejeitado");

        return opcoes;
    }

    public void aprovarTodosOsItens() {
        for (ItemAprovacaoMaterial itemAprovacaoMaterial : selecionado.getItensAprovados()) {
            aprovarItemTotalmente(itemAprovacaoMaterial);
        }
    }

    public void aprovarItemTotalmente(ItemAprovacaoMaterial itemAprovacaoMaterial) {
        itemAprovacaoMaterial.setQuantidadeAprovada(itemAprovacaoMaterial.getItemRequisicaoMaterial().getQuantidade());
        itemAprovacaoMaterial.setTipoAprovacao(TipoAprovacaoMaterial.APROVADO_TOTAL);
    }

    public void rejeitarItem(ItemAprovacaoMaterial item) {
        item.setQuantidadeAprovada(BigDecimal.ZERO);
        item.setTipoAprovacao(TipoAprovacaoMaterial.REJEITADO);
    }

    public void selecionarItem(ItemAprovacaoMaterial item) {
        itemSelecionado = item;
    }

    private void validarTrocaMaterial() {
        ValidacaoException ve = new ValidacaoException();
        if (novoMaterial == null || novoMaterial.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo novo material deve ser informado.");
        }
        ve.lancarException();
        if (novoMaterial.equals(itemSelecionado.getItemRequisicaoMaterial().getMaterialRequisitado())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe um material diferente do requisitado.");
        }
        ve.lancarException();
    }

    public void efetivarTroca() {
        try {
            validarTrocaMaterial();
            HashSet<Material> materiais = new HashSet<>();
            itemSelecionado.getItemRequisicaoMaterial().setMaterialAprovado(novoMaterial);
            materiais.add(novoMaterial);
            for (ItemAprovacaoMaterial mat : selecionado.getItensAprovados()) {
                materiais.add(mat.getMaterial());
            }
            consolidadorDeEstoque = facade.getLocalEstoqueFacade().getNovoConsolidadorComReservaEstoque(selecionado.getRequisicaoMaterial().getLocalEstoqueOrigem(), materiais, facade.getSistemaFacade().getDataOperacao());

            cancelarTroca();
            FacesUtil.executaJavaScript("trocarMaterial.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarTroca() {
        itemSelecionado = null;
        novoMaterial = null;
    }

    public void voltarMaterialRequisitado() {
        HashSet<Material> materiais = new HashSet<>();
        itemSelecionado.getItemRequisicaoMaterial().setMaterialAprovado(itemSelecionado.getItemRequisicaoMaterial().getMaterialRequisitado());
        materiais.add(itemSelecionado.getItemRequisicaoMaterial().getMaterialAprovado());
        consolidadorDeEstoque = facade.getLocalEstoqueFacade().getNovoConsolidadorDesconsiderandoReservasDaAprovacao(selecionado, selecionado.getItensAprovados());
        cancelarTroca();
    }

    public Boolean mostrarBotaoSelecionar(ItemAprovacaoMaterial item) {
        return itensSelecionados != null &&
            item != null &&
            !itensSelecionados.contains(item);
    }

    public Boolean readOnlyRequisicaoMaterial() {
        return selecionado.getId() != null;
    }

    public Boolean disableBotaoAprovarTodosOsItens() {
        return saidaRecuperada != null;
    }

    public Boolean disableBotaoAprovarItemTotalmente(ItemAprovacaoMaterial item) {
        return item.getQuantidadeAprovada() != null && item.getQuantidadeAprovada().compareTo(item.getItemRequisicaoMaterial().getQuantidade()) == 0;
    }

    public Boolean disableBotaoRejeitarItem(ItemAprovacaoMaterial item) {
        return (item.getQuantidadeAprovada() != null && item.getQuantidadeAprovada().compareTo(BigDecimal.ZERO) == 0)
                || (item.getItemRequisicaoMaterial().getQuantidadeSaida() != null && item.getItemRequisicaoMaterial().getQuantidadeSaida().compareTo(BigDecimal.ZERO) > 0);
    }

    private void validarEstoqueEPoliticaDeEstoqueDeCadaItem(ValidacaoException ve) {
        for (ItemAprovacaoMaterial iam : selecionado.getItensAprovados()) {
            if (iam.getTipoAprovacao() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O item " + iam.getMaterial().toString().toUpperCase() + " não foi avaliado.");
            }

            if (iam.getQuantidadeAprovada() != null) {
                if (iam.getQuantidadeAprovada().compareTo(BigDecimal.ZERO) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade digitada referente ao item " + iam.getMaterial().toString().toUpperCase() + " deve ser maior que 0 (ZERO).");
                }

                if (iam.getItemRequisicaoMaterial().getQuantidadeSaida() != null) {
                    if (iam.getQuantidadeAprovada().compareTo(iam.getItemRequisicaoMaterial().getQuantidadeSaida()) < 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida( "Quantidade digitada referente ao item " + iam.getMaterial().toString().toUpperCase() + " não pode ser menor que a Quantidade Saída.");
                    }
                }

                if (iam.getQuantidadeAprovada().compareTo(iam.getItemRequisicaoMaterial().getQuantidade()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade digitada referente ao item " + iam.getMaterial().toString().toUpperCase() + " é superior a quantidade permitida.");
                }
            }
        }
        ve.lancarException();

        consolidadorDeEstoque.validarItensDaAprovacao(selecionado.getMapaDeItensAprovadosConsolidandoQuantidade());
        validarPoliticaDeEstoque(consolidadorDeEstoque);
    }

    private void validarPoliticaDeEstoque(ConsolidadorDeEstoque consolidador) {
        facade.getPoliticaDeEstoqueFacade().validarPoliticaDeEstoqueAprovacaoMaterial(consolidador,
            selecionado.getMapaDeItensAprovadosConsolidandoQuantidade(),
            selecionado.getRequisicaoMaterial().getLocalEstoqueOrigem().getUnidadeOrganizacional());
    }

    public boolean hasSaidaMaterial() {
        return saidaRecuperada != null;
    }

    public boolean isRequisicaoConsumo() {
        return selecionado.getRequisicaoMaterial() != null && selecionado.getRequisicaoMaterial().isRequisicaoConsumo();
    }

    public String recuperarStyleDoItem(ItemAprovacaoMaterial iam) {
        return iam.isMaterialRequisitadoFoiTrocado() ? "color: #009900; font-weight: bold;" : "";
    }

    public ItemAprovacaoMaterial getItemSelecionado() {
        return itemSelecionado;
    }

    public void setItemSelecionado(ItemAprovacaoMaterial itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public Material getNovoMaterial() {
        return novoMaterial;
    }

    public void setNovoMaterial(Material novoMaterial) {
        this.novoMaterial = novoMaterial;
    }

    public List<Material> completarMaterialPermitidoNoLocalEstoqueOrigem(String parte) {
        return facade.getMaterialFacade().completarMaterialPermitidoNaHierarquiaDoLocalEstoque(
            null, selecionado.getRequisicaoMaterial().getLocalEstoqueOrigem(), parte, StatusMaterial.getSituacoesDeferidoInativo());
    }

    public ConsolidadorDeEstoque getConsolidador() {
        if (selecionado.getId() == null) {
            HashSet<Material> materiais = new HashSet<>();
            for (ItemAprovacaoMaterial item : selecionado.getMateriaisAprovados()) {
                materiais.add(item.getMaterial());
            }
            return facade.getLocalEstoqueFacade().getNovoConsolidadorComReservaEstoque(selecionado.getRequisicaoMaterial().getLocalEstoqueOrigem(), materiais, selecionado.getDataDaAprovacao());
        }
        return facade.getLocalEstoqueFacade().getNovoConsolidadorDesconsiderandoReservasDaAprovacao(selecionado, selecionado.getMateriaisAprovados());
    }

    private String getSecretaria() {
        return getHierarquiaDaUnidade().getDescricao();
    }

    public void setDetalhesMaterial(ItemAprovacaoMaterial item) {
        selecionarItem(item);
        FacesUtil.atualizarComponente("form-material");
        FacesUtil.executaJavaScript("dlgMaterial.show()");
    }

    public BigDecimal getQuantidadeAAvaliar(ItemAprovacaoMaterial item) {
        return item.getQuantidadeAprovada() != null && item.getItemRequisicaoMaterial().getQuantidadeSaida() != null
                ? item.getQuantidadeAprovada().subtract(item.getItemRequisicaoMaterial().getQuantidadeSaida()) : BigDecimal.ZERO;
    }

    public void recuperarQuantidadeSaidaItens() {
        for (ItemAprovacaoMaterial itemAprov : selecionado.getItensAprovados()) {
            itemAprov.getItemRequisicaoMaterial().setQuantidadeSaida(facade.getSaidaMaterialFacade().getQuantidadeSaida(itemAprov.getItemRequisicaoMaterial()));
        }
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", "Avaliação de Requisição de Materiais");
            dto.adicionarParametro("SECRETARIA", getSecretaria());
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("TIPOREQUISICAO", selecionado.getRequisicaoMaterial().getTipoRequisicao().name());
            dto.adicionarParametro("idAprovacaoMaterial", selecionado.getId());
            dto.setNomeRelatorio("Avaliação de Requisição de Materiais");
            dto.setApi("administrativo/aprovacao-material/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<ItemAprovacaoMaterial> getItensSelecionados() {
        return itensSelecionados;
    }

    public void setItensSelecionados(List<ItemAprovacaoMaterial> itensSelecionados) {
        this.itensSelecionados = itensSelecionados;
    }

    public ConsolidadorDeEstoque getConsolidadorDeEstoque() {
        return consolidadorDeEstoque;
    }

    public void setConsolidadorDeEstoque(ConsolidadorDeEstoque consolidadorDeEstoque) {
        this.consolidadorDeEstoque = consolidadorDeEstoque;
    }
}
