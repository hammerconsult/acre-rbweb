package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FormularioLocalEstoque;
import br.com.webpublico.entidadesauxiliares.FormularioLoteMaterial;
import br.com.webpublico.entidadesauxiliares.ItemSaidaMaterialRequisicaoVO;
import br.com.webpublico.entidadesauxiliares.ItemSaidaMaterialVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OpcaoOrdenacaoRelatorioSaidaConsumoMaterial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 18/11/13
 * Time: 09:29
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "saidaConsumoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSaidaConsumo", pattern = "/saida-por-consumo/novo/", viewId = "/faces/administrativo/materiais/saidaMaterial/consumo/edita.xhtml"),
    @URLMapping(id = "editarSaidaConsumo", pattern = "/saida-por-consumo/editar/#{saidaConsumoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/saidaMaterial/consumo/edita.xhtml"),
    @URLMapping(id = "listarSaidaConsumo", pattern = "/saida-por-consumo/listar/", viewId = "/faces/administrativo/materiais/saidaMaterial/consumo/lista.xhtml"),
    @URLMapping(id = "verSaidaConsumo", pattern = "/saida-por-consumo/ver/#{saidaConsumoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/saidaMaterial/consumo/visualizar.xhtml")
})
public class SaidaConsumoMaterialControlador extends SaidaMaterialControlador {

    private List<ItemSaidaMaterialRequisicaoVO> itensSaidaRequisicao;
    private OpcaoOrdenacaoRelatorioSaidaConsumoMaterial opcaoOrdenacaoRelatorio;

    public SaidaConsumoMaterialControlador() {
        super();
        metadata = new EntidadeMetaData(SaidaRequisicaoMaterial.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/saida-por-consumo/";
    }

    @Override
    @URLAction(mappingId = "novaSaidaConsumo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoMovimentoMaterial.EM_ELABORACAO);
        itensRequisicao = new ArrayList<>();
        selecionado.setTipoBaixaBens(facade.getTipoBaixaBensFacade().recuperarTipoIngressoBaixa(TipoBem.ESTOQUE, TipoIngressoBaixa.CONSUMO));
        selecionado.setRetiradoPor(facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome());
    }

    @Override
    @URLAction(mappingId = "editarSaidaConsumo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        if (selecionado.getSituacao().isConcluida()) {
            FacesUtil.addOperacaoNaoPermitida("Não é permitido realizar alterações em uma saída já concluída.");
            redirecionarParaVer();
        }
        buscarLocaisEstoqueArmazenamento();
    }

    @URLAction(mappingId = "verSaidaConsumo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        recuperarObjeto();
        operacao = Operacoes.VER;
        preencherItemSaidaVoParaVisualizacao();
        setOpcaoOrdenacaoRelatorio(OpcaoOrdenacaoRelatorioSaidaConsumoMaterial.ITEM_SAIDA);
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/saida-por-consumo/listar/");
    }

    @Override
    public void salvarSemMovimentar() {
        try {
            Util.validarCampos(selecionado);
            validarRegrasEspecificas();
            criarItemSaidaMaterial();
            atribuirNumeroItemSaida();
            validarMaterialDuplicado();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    @Override
    public void preConcluir() {
        facade.validarRequisicaoMaterialBloqueadaSingleton(selecionado);
        super.preConcluir();
    }

    public void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (itensSaidaRequisicao.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum item encontrado para a saída material.");
        } else if (!hasItemSelecionado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A saída deve possuir ao menos um item com quantidade maior que zero(0).");
        }
        for (ItemSaidaMaterialRequisicaoVO itemVO : itensSaidaRequisicao) {
            itemVO.getItensSaida().forEach(item -> {
                if (item.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Saída não Permtida!", "O material " + item.getMaterial().getCodigo() + " está com o valor total negativo.");
                }
            });
        }
        ve.lancarException();
    }

    public Boolean hasItemSelecionado() {
        boolean controle = false;
        for (ItemSaidaMaterialRequisicaoVO itemVO : itensSaidaRequisicao) {
            controle = itemVO.getItensSaida().stream().anyMatch(ItemSaidaMaterialVO::hasQuantidade);
            if (controle) break;
        }
        return controle;
    }

    public List<RequisicaoMaterial> completarRequisicaoMaterial(String parte) {
        return facade.getRequisicaoMaterialFacade().buscarRequisicaoConsumoMaterialComQtdeAutorizadaDisponivel(parte.trim(), facade.getSistemaFacade().getUsuarioCorrente());
    }

    public void limparDadosRequisicao() {
        itensSaidaRequisicao = Lists.newArrayList();
        getSaidaRequisicaoMaterial().setRequisicaoMaterial(null);
    }

    private void validarSaidaNaoConcluidaParaRequisicao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecionado está nulo para realizar a saída de material.");

        } else if (!getSaidaRequisicaoMaterial().getRequisicaoMaterial().podeSerUtilizadaEmSaidas()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido realizar saídas para a requisição informada (" + getSaidaRequisicaoMaterial().getRequisicaoMaterial().getTipoSituacaoRequisicao().getDescricao() + ").");
        }
        ve.lancarException();
        List<SaidaRequisicaoMaterial> saidas = facade.recuperaSaidasDaRequisicao(getSaidaRequisicaoMaterial().getRequisicaoMaterial());
        for (SaidaRequisicaoMaterial saida : saidas) {
            if (isOperacaoNovo() && saida.getSituacao().isEmElaboracao()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A requisição não pode ser utilizada, pois está em uma saída de material em elaboração.");
            }
        }
        if (ve.temMensagens()) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Conclua a(s) saída(s) para voltar a utilizar o saldo da requisição.");
        }
        ve.lancarException();
    }

    public void buscarLocaisEstoqueArmazenamento() {
        try {
            validarSaidaNaoConcluidaParaRequisicao();
            setHierarquiaAdministrativa(getHierarquiaDaUnidade(getSaidaRequisicaoMaterial().getRequisicaoMaterial().getUnidadeRequerente(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
            itensSaidaRequisicao = Lists.newArrayList();
            List<ItemAprovacaoMaterial> itensAprovacao = facade.getAprovacaoMaterialFacade().buscarItensAprovados(getSaidaRequisicaoMaterial().getRequisicaoMaterial());

            Map<ItemAprovacaoMaterial, List<FormularioLocalEstoque>> map = new HashMap<>();
            List<HierarquiaOrganizacional> unidadesOrcamentarias = getOrcamentariasDaAdministrativa(getSaidaRequisicaoMaterial().getRequisicaoMaterial().getLocalEstoqueOrigem().getUnidadeOrganizacional());
            for (ItemAprovacaoMaterial itemAprovMat : itensAprovacao) {
                if (!itemAprovMat.getItemRequisicaoMaterial().isAtendidoTotalmente()) {
                    grupoMaterial = itemAprovMat.getMaterial().getGrupo();
                    localEstoque = getSaidaRequisicaoMaterial().getRequisicaoMaterial().getLocalEstoqueOrigem();
                    List<FormularioLocalEstoque> locaisEstoqueMov = getLocaisEstoquePermitidoMovimentacoes(itemAprovMat.getMaterial(), unidadesOrcamentarias);
                    preencherMapMaterialLocalEstoque(map, itemAprovMat, locaisEstoqueMov);
                }
            }
            criarItemSaidaMaterialVO(map);
            Collections.sort(itensSaidaRequisicao);
            FacesUtil.executaJavaScript("organizarColunaTabelaItens()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            getSaidaRequisicaoMaterial().setRequisicaoMaterial(null);
        }
    }

    private void criarItemSaidaMaterialVO(Map<ItemAprovacaoMaterial, List<FormularioLocalEstoque>> map) {
        for (Map.Entry<ItemAprovacaoMaterial, List<FormularioLocalEstoque>> entry : map.entrySet()) {
            ItemSaidaMaterialRequisicaoVO novoItemSaidaAprov = new ItemSaidaMaterialRequisicaoVO();
            ItemAprovacaoMaterial itemAprov = entry.getKey();
            novoItemSaidaAprov.setItemAvaliacaoMaterial(itemAprov);

            for (FormularioLocalEstoque le : entry.getValue()) {
                List<FormularioLoteMaterial> lotes = facade.getLoteMaterialFacade().buscarLoteMaterialPorMaterialAndLocalEstoqueAndOrcamentariaAndFiltro(le.getIdLocalEstoque(), itemAprov.getMaterial().getId(), le.getIdUnidadeOrcamentaria(), "");
                if (!Util.isListNullOrEmpty(lotes)) {
                    for (FormularioLoteMaterial lote : lotes) {
                        if (lote.getQuantidadeLote().compareTo(BigDecimal.ZERO) > 0) {
                            ItemSaidaMaterialVO novoItemVO = new ItemSaidaMaterialVO();
                            novoItemVO.setItemSaidaMaterialAvaliacao(novoItemSaidaAprov);
                            novoItemVO.setMaterial(itemAprov.getMaterial());
                            novoItemVO.setLoteMaterial(facade.getLoteMaterialFacade().recuperar(lote.getIdLote()));
                            novoItemVO.setLocalEstoque(facade.getLocalEstoqueFacade().recuperarSemDependencia(le.getIdLocalEstoque()));
                            novoItemVO.setHierarquiaOrc(facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaPorTipoIdUnidadeEData(TipoHierarquiaOrganizacional.ORCAMENTARIA, le.getIdUnidadeOrcamentaria(), selecionado.getDataSaida()));
                            novoItemVO.setLocalEstoqueOrcamentario(facade.getLocalEstoqueFacade().recuperarLocalEstoqueOrcamentario(novoItemVO.getLocalEstoque(), novoItemVO.getHierarquiaOrc().getSubordinada(), selecionado.getDataSaida()));
                            novoItemVO.setQuantidadeEstoqueAtual(lote.getQuantidadeLote());
                            novoItemVO.setValorUnitario(le.getValorUnitario());
                            novoItemVO.setQuantidade(BigDecimal.ZERO);

                            if (isOperacaoEditar()) {
                                novoItemVO.setItemSaidaMaterial(facade.recuperarItemSaidaMaterial(itemAprov.getItemRequisicaoMaterial(), novoItemVO.getLocalEstoqueOrcamentario(), novoItemVO.getLoteMaterial(), selecionado));
                                if (novoItemVO.getItemSaidaMaterial() != null) {
                                    novoItemVO.setQuantidade(novoItemVO.getItemSaidaMaterial().getQuantidade());
                                }
                            }
                            novoItemVO.calcularValorTotal();
                            novoItemSaidaAprov.getItensSaida().add(novoItemVO);
                        }
                    }
                } else {
                    ItemSaidaMaterialVO novoItemVO = new ItemSaidaMaterialVO();
                    novoItemVO.setItemSaidaMaterialAvaliacao(novoItemSaidaAprov);
                    novoItemVO.setMaterial(itemAprov.getMaterial());
                    novoItemVO.setLocalEstoque(facade.getLocalEstoqueFacade().recuperarSemDependencia(le.getIdLocalEstoque()));
                    novoItemVO.setHierarquiaOrc(facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaPorTipoIdUnidadeEData(TipoHierarquiaOrganizacional.ORCAMENTARIA, le.getIdUnidadeOrcamentaria(), selecionado.getDataSaida()));
                    novoItemVO.setLocalEstoqueOrcamentario(facade.getLocalEstoqueFacade().recuperarLocalEstoqueOrcamentario(novoItemVO.getLocalEstoque(), novoItemVO.getHierarquiaOrc().getSubordinada(), selecionado.getDataSaida()));
                    novoItemVO.setQuantidadeEstoqueAtual(le.getFisico());
                    novoItemVO.setValorEstoqueAtual(le.getFinanceiro());
                    novoItemVO.setValorUnitario(le.getValorUnitario());
                    novoItemVO.setQuantidade(entry.getValue().size() == 1 ? itemAprov.getItemRequisicaoMaterial().getQuantidadeAAtender() : BigDecimal.ZERO);

                    if (isOperacaoEditar()) {
                        novoItemVO.setItemSaidaMaterial(facade.recuperarItemSaidaMaterial(itemAprov.getItemRequisicaoMaterial(), novoItemVO.getLocalEstoqueOrcamentario(), null, selecionado));
                        if (novoItemVO.getItemSaidaMaterial() != null) {
                            novoItemVO.setQuantidade(novoItemVO.getItemSaidaMaterial().getQuantidade());
                        }
                    }
                    novoItemVO.calcularValorTotal();
                    novoItemSaidaAprov.getItensSaida().add(novoItemVO);
                }
            }
            if (!Util.isListNullOrEmpty(novoItemSaidaAprov.getItensSaida())) {
                itensSaidaRequisicao.add(novoItemSaidaAprov);
            }
        }
    }

    private void preencherMapMaterialLocalEstoque(Map<ItemAprovacaoMaterial, List<FormularioLocalEstoque>> map, ItemAprovacaoMaterial itemAprovMat,
                                                  List<FormularioLocalEstoque> locaisEstoqueMov) {
        for (FormularioLocalEstoque le : locaisEstoqueMov) {
            if (!map.containsKey(itemAprovMat)) {
                map.put(itemAprovMat, Lists.newArrayList(le));
            } else {
                List<FormularioLocalEstoque> itensMap = map.get(itemAprovMat);
                itensMap.add(le);
                map.put(itemAprovMat, itensMap);
            }
        }
    }


    public void processarItemSaidaMaterial(ItemSaidaMaterialVO itemVO, ItemSaidaMaterialRequisicaoVO itemReqVO) {
        try {
            validarFormulariosLocalEstoque(itemVO, itemReqVO);
            itemVO.calcularValorTotal();
            FacesUtil.executaJavaScript("organizarColunaTabelaItens()");
        } catch (ValidacaoException ve) {
            itemVO.setQuantidade(BigDecimal.ZERO);
            itemVO.setValorTotal(BigDecimal.ZERO);
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (!Util.isListNullOrEmpty(itensSaidaRequisicao)) {
            for (ItemSaidaMaterialRequisicaoVO itemReq : itensSaidaRequisicao) {
                for (ItemSaidaMaterialVO item : itemReq.getItensSaida()) {
                    if (item.hasQuantidade()) {
                        total = total.add(item.getValorTotal());
                    }
                }
            }
        }
        return total;
    }

    private void criarItemSaidaMaterial() {
        novaListaDeItemSaida();
        itensSaidaRequisicao.forEach(itemAprov -> itemAprov.getItensSaida().forEach(itemVO -> {
            if (itemVO.hasQuantidade()) {
                ItemSaidaMaterial itemSaida = new ItemSaidaMaterial(selecionado, itemAprov.getItemAvaliacaoMaterial().getItemRequisicaoMaterial());
                itemSaida.setQuantidade(itemVO.getQuantidade());
                itemSaida.setValorUnitario(itemVO.getValorUnitario());
                itemSaida.setValorTotal(itemVO.getValorTotal());
                itemSaida.setLocalEstoqueOrcamentario(itemVO.getLocalEstoqueOrcamentario());
                itemSaida.setLoteMaterial(itemVO.getLoteMaterial());
                Util.adicionarObjetoEmLista(selecionado.getListaDeItemSaidaMaterial(), itemSaida);
            }
        }));
    }

    public void validarFormulariosLocalEstoque(ItemSaidaMaterialVO itemVO, ItemSaidaMaterialRequisicaoVO itemReqVO) {
        ValidacaoException ve = new ValidacaoException();
        String descricaoEstoque = itemVO.getControleLote() ? "estoque lote" : "estoque";

        BigDecimal qtdeTotalSaida = itemReqVO.getQuantidadeTotalItemSaidaMaterial();
        BigDecimal qtdeAtender = itemVO.getItemSaidaMaterialAvaliacao().getItemAvaliacaoMaterial().getItemRequisicaoMaterial().getQuantidadeAAtender();

        if (qtdeTotalSaida.compareTo(qtdeAtender) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade de saída <b>" + qtdeTotalSaida + "</b> é maior do que a quantidade a atender <b>"
                + qtdeAtender + "</b> para o material:  " + itemVO.getMaterial() + ".");
            ve.lancarException();
        }
        if (itemVO.getQuantidade() != null && itemVO.getQuantidade().compareTo(itemVO.getQuantidadeEstoqueAtual()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade do " + descricaoEstoque + " é menor que a quantidade de saída no local de estoque " + itemVO.getLocalEstoque().getCodigoDescricao() + ".");
        }
        ve.lancarException();
    }

    public List<TipoBaixaBens> completarTipoBaixaPorConsumo(String parte) {
        return facade.getTipoBaixaBensFacade().buscarFiltrandoPorTipoBemAndTipoIngresso(parte.trim(), TipoBem.ESTOQUE, TipoIngressoBaixa.CONSUMO);
    }

    public void gerarRelatorioSaidaPorConsumo() {
        try {
            FacesUtil.executaJavaScript("dialogRelatorio.hide()");
            RelatorioDTO dto = new RelatorioDTO();
            String nomeRelatorio = "RELATÓRIO DE SAÍDA DE MATERIAIS POR CONSUMO";
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_RELATORIO", nomeRelatorio);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("idSaidaMaterial", selecionado.getId());
            dto.adicionarParametro("OPCAOORDENACAO", opcaoOrdenacaoRelatorio.name());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/saida-consumo-material/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<ItemSaidaMaterialRequisicaoVO> getItensSaidaRequisicao() {
        return itensSaidaRequisicao;
    }

    public void setItensSaidaRequisicao(List<ItemSaidaMaterialRequisicaoVO> itensSaidaRequisicao) {
        this.itensSaidaRequisicao = itensSaidaRequisicao;
    }

    public OpcaoOrdenacaoRelatorioSaidaConsumoMaterial getOpcaoOrdenacaoRelatorio() {
        return opcaoOrdenacaoRelatorio;
    }

    public void setOpcaoOrdenacaoRelatorio(OpcaoOrdenacaoRelatorioSaidaConsumoMaterial opcaoOrdenacaoRelatorio) {
        this.opcaoOrdenacaoRelatorio = opcaoOrdenacaoRelatorio;
    }

    public List<SelectItem> getOpcoes() {
        return Util.getListSelectItemSemCampoVazio(OpcaoOrdenacaoRelatorioSaidaConsumoMaterial.values(), false);
    }
}
