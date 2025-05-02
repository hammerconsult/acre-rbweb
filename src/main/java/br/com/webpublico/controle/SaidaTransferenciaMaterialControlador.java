package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FormularioLocalEstoque;
import br.com.webpublico.entidadesauxiliares.FormularioLoteMaterial;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoIngressoBaixa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 19/11/13
 * Time: 09:20
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "saidaTransferenciaMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSaidaTransferencia", pattern = "/saida-por-transferencia/novo/", viewId = "/faces/administrativo/materiais/saidaMaterial/transferencia/edita.xhtml"),
    @URLMapping(id = "editarSaidaTransferencia", pattern = "/saida-por-transferencia/editar/#{saidaTransferenciaMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/saidaMaterial/transferencia/edita.xhtml"),
    @URLMapping(id = "listarSaidaTransferencia", pattern = "/saida-por-transferencia/listar/", viewId = "/faces/administrativo/materiais/saidaMaterial/transferencia/lista.xhtml"),
    @URLMapping(id = "verSaidaTransferencia", pattern = "/saida-por-transferencia/ver/#{saidaTransferenciaMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/saidaMaterial/transferencia/visualizar.xhtml")
})
public class SaidaTransferenciaMaterialControlador extends SaidaMaterialControlador {

    private BigDecimal quantidadeDisponivel;
    private BigDecimal quantidadeAtender;

    public SaidaTransferenciaMaterialControlador() {
        metadata = new EntidadeMetaData(SaidaRequisicaoMaterial.class);
        itensRequisicao = Lists.newArrayList();
        quantidadeAtender = BigDecimal.ZERO;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/saida-por-transferencia/";
    }

    @Override
    @URLAction(mappingId = "novaSaidaTransferencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarSaidaTransferencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verSaidaTransferencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        setHierarquiaAdministrativa(getHierarquiaDaUnidade(getSaidaRequisicaoMaterial().getRequisicaoMaterial().getUnidadeRequerente(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
    }

    public List<RequisicaoMaterial> completarRequisicaoMaterial(String parte) {
        return facade.getRequisicaoMaterialFacade().buscarRequisicaoTransferenciaMaterialComQtdeAutorizadaDisponivel(parte.trim());
    }

    public List<SaidaRequisicaoMaterial> completarSaidaTransferenciaDeRequisicoesNaoAtendidasTotalmente(String parte) {
        return facade.completaSaidaTransferenciaDeRequisicoesNaoAtendidasTotalmente(parte.trim());
    }

    public void recuperarMateriaisDoItemRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial) {
        itensRequisicao.clear();
        if (requisicaoMaterial != null) {
            RequisicaoMaterial requisicao = facade.getRequisicaoMaterialFacade().recuperar(requisicaoMaterial.getId());
            setLocalEstoque(requisicao.getLocalEstoqueOrigem());
            if (requisicao.podeSerUtilizadaEmSaidas()) {
                getSaidaRequisicaoMaterial().setRequisicaoMaterial(requisicao);
                novaListaDeItemSaida();


                List<ItemAprovacaoMaterial> itens = facade.getAprovacaoMaterialFacade().buscarItensAprovados(requisicao);
                for (ItemAprovacaoMaterial itemAprov : itens) {
                    if (itemAprov.isAprovado() && !itemAprov.getItemRequisicaoMaterial().isAtendidoTotalmente()) {
                        itensRequisicao.add(itemAprov.getItemRequisicaoMaterial());
                    }
                }
            } else {
                getSaidaRequisicaoMaterial().setRequisicaoMaterial(null);
                FacesUtil.addAtencao("Não é permitido realizar saídas para a requisição informada (" + requisicao.getTipoSituacaoRequisicao().getDescricao() + ").");
            }
        }
    }

    public void atribuirMaterial(ItemRequisicaoMaterial itemRequisicaoMaterial) {
        try {
            hierarquiaOrcamentaria = null;
            material = itemRequisicaoMaterial.getMaterialDisponibilizado();
            quantidadeAtender = itemRequisicaoMaterial.getQuantidadeAAtender();
            validarSeMaterialJaFoiAdicionado();
            FacesUtil.executaJavaScript("dlgInfoLocalEstoque.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public List<TipoBaixaBens> completarTipoBaixaPorConsumo(String parte) {
        return facade.getTipoBaixaBensFacade().buscarFiltrandoPorTipoBemAndTipoIngresso(parte.trim(), TipoBem.ESTOQUE, TipoIngressoBaixa.CONSUMO);
    }

    public void recuperarLocaisDeEstoque() {
        try {
            validarFiltrosIniciaisParaConsultaDeLocaisDeEstoque();
            setFormularioLocalEstoquesRecuperados(facade.getLocalEstoqueFacade().recuperarLocaisEstoquesPermitidosMovimentacoes(localEstoque, Lists.newArrayList(material.getId()), null, facade.getSistemaFacade().getUsuarioCorrente(), hierarquiaOrcamentaria.getSubordinada(), facade.getSistemaFacade().getDataOperacao()));
            AprovacaoMaterial aprovacaoMaterial = facade.getAprovacaoMaterialFacade().recuperarAprovacaoRequisicaoAndItens(getSaidaRequisicaoMaterial().getRequisicaoMaterial());
            quantidadeDisponivel = facade.getLocalEstoqueFacade().getNovoConsolidadorDesconsiderandoReservasDaAprovacao(aprovacaoMaterial, aprovacaoMaterial.getMateriaisAprovados()).getFisicoConsolidado(material);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarFiltrosIniciaisParaConsultaDeLocaisDeEstoque() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrcamentaria == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
            if (formularioLocalEstoquesRecuperados != null) {
                formularioLocalEstoquesRecuperados.clear();
            }
        }
        ve.lancarException();
    }

    public void cancelarItens() {
        limparCampos();
    }

    @Override
    public void limparCampos() {
        formularioLocalEstoquesSelecionados = Lists.newArrayList();
        formularioLoteMaterialSelecionados = Lists.newArrayList();
    }

    public void adicionarItemTransferencia() {
        try {
            validarItemTransferenciaAoAdicionar();
            for (FormularioLocalEstoque localSelecionado : formularioLocalEstoquesSelecionados) {
                ItemRequisicaoMaterial itemRequisicao = getItemRequisicao(material);
                if (localSelecionado.getControleLote()) {
                    for (FormularioLoteMaterial lote : localSelecionado.getListaLotes()) {
                        BigDecimal quantidadeDesejada = lote.getQuantidadeSaida();
                        LocalEstoque local = facade.getLocalEstoqueFacade().recuperar(lote.getIdLocalEstoque());
                        ItemRequisicaoSaidaMaterial itemRequisicaoSaidaMaterial = new ItemRequisicaoSaidaMaterial(itemRequisicao, itemSaidaMaterialSelecionado);
                        itemSaidaMaterialSelecionado.setItemRequisicaoSaidaMaterial(itemRequisicaoSaidaMaterial);
                        itemSaidaMaterialSelecionado.setQuantidade(quantidadeDesejada);
                        itemSaidaMaterialSelecionado.setValorUnitario(localSelecionado.getValorUnitario());
                        itemSaidaMaterialSelecionado.setLoteMaterial(facade.getLoteMaterialFacade().recuperar(lote.getIdLote()));
                        itemSaidaMaterialSelecionado.setLocalEstoqueOrcamentario(facade.getLocalEstoqueFacade().recuperarLocalEstoqueOrcamentario(local, hierarquiaOrcamentaria.getSubordinada(), facade.getSistemaFacade().getDataOperacao()));
                        adicionarItem();
                    }
                } else {
                    BigDecimal quantidadeDesejada = localSelecionado.getQtdeDesejada();
                    ItemRequisicaoSaidaMaterial itemRequisicaoSaidaMaterial = new ItemRequisicaoSaidaMaterial(itemRequisicao, itemSaidaMaterialSelecionado);
                    itemSaidaMaterialSelecionado.setItemRequisicaoSaidaMaterial(itemRequisicaoSaidaMaterial);
                    itemSaidaMaterialSelecionado.setQuantidade(quantidadeDesejada);
                    itemSaidaMaterialSelecionado.setValorUnitario(localSelecionado.getValorUnitario());
                    itemSaidaMaterialSelecionado.setLoteMaterial(null);
                    itemSaidaMaterialSelecionado.setLocalEstoqueOrcamentario(facade.getLocalEstoqueFacade().recuperarLocalEstoqueOrcamentario(facade.getLocalEstoqueFacade().recuperar(localSelecionado.getIdLocalEstoque().longValue()), hierarquiaOrcamentaria.getSubordinada(), facade.getSistemaFacade().getDataOperacao()));
                    adicionarItem();
                }
            }
            limparCampos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarItemTransferenciaAoAdicionar() {
        ValidacaoException ve = new ValidacaoException();
        if (formularioLocalEstoquesSelecionados == null || formularioLocalEstoquesSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um local de estoque de armazenamento.");
        }
        ve.lancarException();
        validarFormulariosLocalEstoque(ve);
        validarLoteMaterialDoFormulariosLocalEstoque(ve);
    }

    public void processarItemSaidaMaterialPorLoteSelecionado() {
        try {
            validarItemSaidaPorLoteMaterial();
            localEstoqueSelecionado.getListaLotes().addAll(formularioLoteMaterialSelecionados);
            formularioLoteMaterialSelecionados.clear();
            FacesUtil.executaJavaScript("dialogEstoqueLotes.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarItemSaidaPorLoteMaterial() {
        ValidacaoException ve = new ValidacaoException();
        if (formularioLoteMaterialSelecionados == null || formularioLoteMaterialSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um lote do material de armazenamento.");
        }
        ve.lancarException();
        validarFormulariosLoteMaterial(ve);
    }

    private void validarFormulariosLoteMaterial(ValidacaoException ve) {
        for (FormularioLoteMaterial formularioLote : formularioLoteMaterialSelecionados) {
            BigDecimal quantidadeEmEstoque = formularioLote.getQuantidadeLote();
            BigDecimal quantidadeDesejada = formularioLote.getQuantidadeSaida();

            if (quantidadeDesejada == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a quantidade desejada do lote " + formularioLote.getIdentificacao() + ".");
            }
            ve.lancarException();
            if (quantidadeDesejada.compareTo(quantidadeEmEstoque) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade do lote " + formularioLote.getIdentificacao() + " é menor do que a desejada.");

            } else if (quantidadeDesejada.compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade desejada do lote " + formularioLote.getIdentificacao() + " deve ser maior que zero.");

            } else if (quantidadeDesejada.compareTo(quantidadeDisponivel) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote " + formularioLote.getIdentificacao() + " não tem a quantidade desejada disponível.");
            }
        }
        ve.lancarException();
    }

    public BigDecimal quantidadeGeralNoLocalDeEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        for (FormularioLocalEstoque formulario : formularioLocalEstoquesSelecionados) {
            if (formulario.getQtdeDesejada() != null) {
                total = total.add(formulario.getQtdeDesejada());
            }
        }
        return total;
    }

    private void validarFormulariosLocalEstoque(ValidacaoException ve) {
        for (FormularioLocalEstoque localSelecionado : formularioLocalEstoquesSelecionados) {
            if (!localSelecionado.getControleLote()) {
                BigDecimal quantidadeDesejada = localSelecionado.getQtdeDesejada();
                BigDecimal quantidadeGeralRequisitada = quantidadeGeralNoLocalDeEstoque();
                if (quantidadeGeralRequisitada.compareTo(quantidadeAtender) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade desejada <b>" + quantidadeGeralRequisitada + "</b> é maior do que a quantidade a atender <b>" + quantidadeAtender + "</b> na requisição do material ");
                    ve.lancarException();
                }
                if (quantidadeDesejada == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a quantidade desejada para o local de estoque <b>" + localSelecionado.getDescricaLocalEstoque() + "</b>");
                }
                ve.lancarException();
                if (quantidadeDesejada.compareTo(quantidadeAtender) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade do local de estoque " + facade.getLocalEstoqueFacade().recuperar(localSelecionado.getIdLocalEstoque().longValue()).toString() + " ultrapassa a quantidade atender (" + quantidadeAtender + ") da requisição");
                }
                if (quantidadeDesejada.compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade desejada no local de estoque " + localSelecionado.getDescricaLocalEstoque() + " deve ser maior que zero.");
                }
                if (quantidadeDesejada.compareTo(quantidadeDisponivel) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade disponível é menor do que a desejada.");
                }
            }
        }
        ve.lancarException();
    }

    private void validarLoteMaterialDoFormulariosLocalEstoque(ValidacaoException ve) {

        if (material.getControleDeLote()) {
            BigDecimal quantidadeDesejada = BigDecimal.ZERO;

            for (FormularioLocalEstoque local : formularioLocalEstoquesSelecionados) {
                if (local.getListaLotes() == null || local.getListaLotes().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe os lote(s) de Material(is) para o local de estoque " + local.getDescricaLocalEstoque());
                }
                ve.lancarException();
                for (FormularioLoteMaterial formularioLote : local.getListaLotes()) {
                    quantidadeDesejada = quantidadeDesejada.add(formularioLote.getQuantidadeSaida());

                    if (quantidadeDesejada == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("Informe a quantidade desejada do lote " + formularioLote.getIdentificacao());

                    } else if (quantidadeDesejada.compareTo(quantidadeAtender) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade do lote " + formularioLote.getIdentificacao() + " ultrapassa a quantidade atender (" + quantidadeAtender + ") da requisição");

                    } else if (quantidadeDesejada.compareTo(BigDecimal.ZERO) == 0) {
                        ve.adicionarMensagemDeCampoObrigatorio("Informe a quantidade desejada do lote " + formularioLote.getIdentificacao());

                    } else if (quantidadeDesejada.compareTo(quantidadeDisponivel) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O lote " + formularioLote.getIdentificacao() + " não tem a quantidade desejada disponível.");
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarItem() {
        try {
            validarItemSaidaMaterial();
            itemSaidaMaterialSelecionado.setSaidaMaterial(selecionado);
            itemSaidaMaterialSelecionado.calcularValorTotal();
            selecionado.getListaDeItemSaidaMaterial().add(itemSaidaMaterialSelecionado);
            formularioLocalEstoquesRecuperados.clear();
            itemSaidaMaterialSelecionado = new ItemSaidaMaterial(selecionado, new ItemRequisicaoMaterial());
            FacesUtil.executaJavaScript("dlgInfoLocalEstoque.hide()");
            FacesUtil.atualizarComponente("Formulario:tabViewPrincipal:tableItemSaidaTransferencia");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarItemSaidaMaterial() {
        ValidacaoException ve = new ValidacaoException();
        if (itemSaidaMaterialSelecionado.getQuantidade() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo quantidade deve ser informado.");

        } else if (itemSaidaMaterialSelecionado.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade de saída deve ser maior que zero (0).");
        }
        ve.lancarException();
    }

    public void selecionar(FormularioLocalEstoque localEstoque) {
        formularioLocalEstoquesSelecionados.add(localEstoque);
        definirFormularioLocalEstoqueSelecionado(localEstoque);
        if (localEstoque.getControleLote()) {
            FacesUtil.executaJavaScript("dialogEstoqueLotes.show()");
        }
        FacesUtil.atualizarComponente(":formDlgLoteEstoque");
    }

    public void definirFormularioLocalEstoqueSelecionado(FormularioLocalEstoque formularioLocalEstoque) {
        formularioLoteMaterialRecuperados = null;
        localEstoqueSelecionado = formularioLocalEstoque;
        if (formularioLocalEstoque != null) {
            formularioLoteMaterialRecuperados = facade.getLoteMaterialFacade().buscarLoteMaterialPorMaterialAndLocalEstoqueAndOrcamentariaAndFiltro(formularioLocalEstoque.getIdLocalEstoque(), material.getId(), hierarquiaOrcamentaria.getSubordinada().getId(), "");
        }
    }

    public ItemRequisicaoMaterial getItemRequisicao(Material material) {
        RequisicaoMaterial requisicao = facade.getRequisicaoMaterialFacade().recuperar(getSaidaRequisicaoMaterial().getRequisicaoMaterial().getId());
        for (ItemRequisicaoMaterial item : requisicao.getItensRequisitados()) {
            if (item.getMaterialDisponibilizado().equals(material)) {
                return item;
            }
        }
        return null;
    }

    public void validarSeMaterialJaFoiAdicionado() {
        ValidacaoException ve = new ValidacaoException();
        for (ItemSaidaMaterial itens : selecionado.getListaDeItemSaidaMaterial()) {
            if (itens.getItemRequisicaoSaidaMaterial().getItemSaidaMaterial().getMaterial().equals(material)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O material " + material.getDescricao() + " já foi adicionado na lista, para prosseguir remova" +
                    " todos os itens desse material e adicione novamente.");
            }
        }
        ve.lancarException();
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/saida-por-transferencia/listar/");
    }

    public void gerarRelatorioSaidaPorTransferencia() {
        try {
            String nomeRelatorio = "RELATÓRIO DE SAÍDA DE MATERIAIS POR TRANSFERÊNCIA";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_RELATORIO", nomeRelatorio);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "MATERIAIS");
            dto.adicionarParametro("idSaidaMaterial", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/saida-material-transferencia/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error(ex.getMessage());
        }
    }


    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
}
