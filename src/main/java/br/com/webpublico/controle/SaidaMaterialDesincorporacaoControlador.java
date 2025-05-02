package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FormularioLocalEstoque;
import br.com.webpublico.entidadesauxiliares.FormularioLoteMaterial;
import br.com.webpublico.entidadesauxiliares.ItemSaidaMaterialVO;
import br.com.webpublico.enums.*;
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
import org.jetbrains.annotations.NotNull;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 19/11/13
 * Time: 08:41
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSaidaDesincorporacao", pattern = "/saida-material-por-desincorporacao/novo/", viewId = "/faces/administrativo/materiais/saidaMaterial/desincorporacao/edita.xhtml"),
    @URLMapping(id = "editarSaidaDesincorporacao", pattern = "/saida-material-por-desincorporacao/editar/#{saidaMaterialDesincorporacaoControlador.id}/", viewId = "/faces/administrativo/materiais/saidaMaterial/desincorporacao/edita.xhtml"),
    @URLMapping(id = "litarSaidaDesincorporacao", pattern = "/saida-material-por-desincorporacao/listar/", viewId = "/faces/administrativo/materiais/saidaMaterial/desincorporacao/lista.xhtml"),
    @URLMapping(id = "verSaidaDesincorporacao", pattern = "/saida-material-por-desincorporacao/ver/#{saidaMaterialDesincorporacaoControlador.id}/", viewId = "/faces/administrativo/materiais/saidaMaterial/desincorporacao/visualizar.xhtml")
})
public class SaidaMaterialDesincorporacaoControlador extends SaidaMaterialControlador {

    public SaidaMaterialDesincorporacaoControlador() {
        super();
        metadata = new EntidadeMetaData(SaidaMaterialDesincorporacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/saida-material-por-desincorporacao/";
    }

    @Override
    @URLAction(mappingId = "novaSaidaDesincorporacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoMovimentoMaterial.EM_ELABORACAO);
        selecionado.setTipoBaixaBens(facade.getTipoBaixaBensFacade().recuperarTipoIngressoBaixa(TipoBem.ESTOQUE, TipoIngressoBaixa.DESINCORPORACAO));
        selecionado.setRetiradoPor(facade.getSistemaFacade().getUsuarioCorrente().getNome());
    }

    @Override
    @URLAction(mappingId = "editarSaidaDesincorporacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        if (selecionado.getSituacao().isConcluida()) {
            FacesUtil.addOperacaoNaoPermitida("Não é permitido realizar alterações em uma saída já concluída.");
            redirecionarParaVer();
        }
        itensSaidaMaterialVO = Lists.newArrayList();
        localEstoque = selecionado.getListaDeItemSaidaMaterial().get(0).getLocalEstoque();
        hierarquiaAdministrativa  = getHierarquiaDaUnidade(localEstoque.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        materiais = Lists.newArrayList();
        selecionado.getListaDeItemSaidaMaterial().forEach(item -> materiais.add(item.getMaterial()));
        preencherLocalEstoqueArmazenamento();
    }

    @URLAction(mappingId = "verSaidaDesincorporacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        recuperarObjeto();
        operacao = Operacoes.VER;
        preencherItemSaidaVoParaVisualizacao();
    }

    @Override
    public void salvarSemMovimentar() {
        try {
            Util.validarCampos(selecionado);
            validarRegrasEspecificas();
            criarItemSaidaMaterial();
            atribuirNumeroItemSaida();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public void processarItemSaidaMaterial(ItemSaidaMaterialVO itemVO) {
        try {
            validarQuantidadeEstoqueItemSaida(itemVO);
            itemVO.calcularValorTotal();
        } catch (ValidacaoException ve) {
            itemVO.setQuantidade(BigDecimal.ZERO);
            itemVO.setValorTotal(BigDecimal.ZERO);
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (itensSaidaMaterialVO.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum local estoque de armazenamento encontrado para a saída material.");
        }
        ve.lancarException();

        if (!hasItemSelecionado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A saída deve possuir ao menos um item com quantidade maior que zero(0).");
        }
        itensSaidaMaterialVO.forEach(item -> {
            if (item.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Saída não Permtida!", "O material " + item.getMaterial().getCodigo() + " está com o valor total negativo.");
            }
        });
        ve.lancarException();
    }

    private void criarItemSaidaMaterial() {
        novaListaDeItemSaida();
        itensSaidaMaterialVO.forEach(itemVO -> {
            if (itemVO.hasQuantidade()) {
                ItemSaidaDesincorporacao isd = new ItemSaidaDesincorporacao();
                isd.setMaterial(itemVO.getMaterial());
                isd.setObservacao(itemVO.getObservacao());

                ItemSaidaMaterial itemSaida = new ItemSaidaMaterial(selecionado, isd);
                itemSaida.setQuantidade(itemVO.getQuantidade());
                itemSaida.setValorUnitario(itemVO.getValorUnitario());
                itemSaida.setValorTotal(itemVO.getValorTotal());
                itemSaida.setLocalEstoqueOrcamentario(itemVO.getLocalEstoqueOrcamentario());
                itemSaida.setLoteMaterial(itemVO.getLoteMaterial());
                selecionado.getListaDeItemSaidaMaterial().add(itemSaida);
            }
        });
    }

    public Boolean hasItemSelecionado() {
        return itensSaidaMaterialVO.stream().anyMatch(ItemSaidaMaterialVO::hasQuantidade);
    }

    public void validarQuantidadeEstoqueItemSaida(ItemSaidaMaterialVO itemVO) {
        ValidacaoException ve = new ValidacaoException();
        String descricaoEstoque = itemVO.getControleLote() ? "estoque lote" : "estoque";

        if (itemVO.getQuantidade() != null && itemVO.getQuantidade().compareTo(itemVO.getQuantidadeEstoqueAtual()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade do " + descricaoEstoque + " deve ser menor que a quantidade de saída no local de estoque " + itemVO.getLocalEstoque().getCodigoDescricao() + ".");
        }
        ve.lancarException();
    }

    public void validarSaidaNaoConcluida() {
        ValidacaoException ve = new ValidacaoException();
        for (Material mat : materiais) {
            SaidaMaterial saidaMaterial = facade.recuperarSaidaDesincorporacaoPorMaterialNaoConcluida(mat);
            if (saidaMaterial != null && !saidaMaterial.getId().equals(selecionado.getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O material "+mat.getCodigo()+  " encontra-se em uma saída em elaboração. Conclua a saída anterior para realizar uma nova saída.");
            }
        }
        ve.lancarException();
    }


    public void buscarLocaisEstoqueArmazenamento() {
        try {
            validarCampaLocalEstoque();
            itensSaidaMaterialVO = Lists.newArrayList();
            materiais = getMateriaisDoLocalEstoque();
            validarSaidaNaoConcluida();
            preencherLocalEstoqueArmazenamento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void preencherLocalEstoqueArmazenamento() {
        List<HierarquiaOrganizacional> unidadesOrcamentarias = getUnidadesOrcamentarias();
        for (Material mat : materiais) {
            List<FormularioLocalEstoque> locais = getLocaisEstoquePermitidoMovimentacoes(mat, unidadesOrcamentarias);

            for (FormularioLocalEstoque le : locais) {
                List<FormularioLoteMaterial> lotes = facade.getLoteMaterialFacade().buscarLoteMaterialPorMaterialAndLocalEstoqueAndOrcamentariaAndFiltro(le.getIdLocalEstoque(), mat.getId(), le.getIdUnidadeOrcamentaria(), "");
                if (!Util.isListNullOrEmpty(lotes)) {
                    for (FormularioLoteMaterial lote : lotes) {
                        if (lote.getQuantidadeLote().compareTo(BigDecimal.ZERO) > 0) {
                            ItemSaidaMaterialVO novoItemVO = novoItemSaidaMaterialVO(mat, le);
                            novoItemVO.setLoteMaterial(facade.getLoteMaterialFacade().recuperar(lote.getIdLote()));
                            novoItemVO.setQuantidadeEstoqueAtual(lote.getQuantidadeLote());

                            atribuirQuantidadeItemSaidaEdicao(novoItemVO, mat, novoItemVO.getLoteMaterial());
                            novoItemVO.calcularValorTotal();
                            itensSaidaMaterialVO.add(novoItemVO);
                        }
                    }
                } else {
                    ItemSaidaMaterialVO novoItemVO = novoItemSaidaMaterialVO(mat, le);
                    novoItemVO.setQuantidadeEstoqueAtual(le.getFisico());
                    novoItemVO.setValorEstoqueAtual(le.getFinanceiro());
                    atribuirQuantidadeItemSaidaEdicao(novoItemVO, mat, null);
                    novoItemVO.calcularValorTotal();
                    itensSaidaMaterialVO.add(novoItemVO);
                }
            }
        }
        Collections.sort(itensSaidaMaterialVO);
    }

    private void atribuirQuantidadeItemSaidaEdicao(ItemSaidaMaterialVO novoItemVO, Material mat, LoteMaterial loteMaterial) {
        if (isOperacaoEditar()) {
            novoItemVO.setItemSaidaMaterial(facade.recuperarItemSaidaMaterial(mat, novoItemVO.getLocalEstoqueOrcamentario(), loteMaterial, selecionado));
            if (novoItemVO.getItemSaidaMaterial() != null) {
                novoItemVO.setQuantidade(novoItemVO.getItemSaidaMaterial().getQuantidade());
                novoItemVO.setObservacao(novoItemVO.getItemSaidaMaterial().getItemSaidaDesincorporacao().getObservacao());
            }
        }
    }

    private ItemSaidaMaterialVO novoItemSaidaMaterialVO(Material mat, FormularioLocalEstoque le) {
        ItemSaidaMaterialVO novoItemVO = new ItemSaidaMaterialVO();
        novoItemVO.setMaterial(mat);
        novoItemVO.setLocalEstoque(facade.getLocalEstoqueFacade().recuperarSemDependencia(le.getIdLocalEstoque()));
        novoItemVO.setHierarquiaOrc(facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaPorTipoIdUnidadeEData(TipoHierarquiaOrganizacional.ORCAMENTARIA, le.getIdUnidadeOrcamentaria(), selecionado.getDataSaida()));
        novoItemVO.setLocalEstoqueOrcamentario(facade.getLocalEstoqueFacade().recuperarLocalEstoqueOrcamentario(novoItemVO.getLocalEstoque(), novoItemVO.getHierarquiaOrc().getSubordinada(), selecionado.getDataSaida()));
        novoItemVO.setQuantidadeEstoqueAtual(BigDecimal.ZERO);
        novoItemVO.setValorUnitario(le.getValorUnitario());
        novoItemVO.setQuantidade(BigDecimal.ZERO);
        return novoItemVO;
    }

    private List<HierarquiaOrganizacional> getUnidadesOrcamentarias() {
        if (hierarquiaOrcamentaria != null) {
            return Lists.newArrayList(hierarquiaOrcamentaria);
        }
        return getOrcamentariasDaAdministrativa(localEstoque.getUnidadeOrganizacional());
    }

    private List<Material> getMateriaisDoLocalEstoque() {
        if (material != null) {
            return Lists.newArrayList(material);
        }
        return facade.getMaterialFacade().completarMaterialNaHierarquiaDoLocalEstoquePorDataAndComEstoque(localEstoque, grupoMaterial, "", StatusMaterial.getSituacoesDeferidoInativo());
    }

    public List<TipoBaixaBens> completarTipoBaixa(String parte) {
        return facade.getTipoBaixaBensFacade().buscarFiltrandoPorTipoBemAndTipoIngresso(parte.trim(), TipoBem.ESTOQUE, TipoIngressoBaixa.DESINCORPORACAO);
    }

    public void gerarRelatorioSaidaDesincorporacao() {
        try {
            String nomeRelatorio = "RELATÓRIO DE SAÍDA DE MATERIAIS POR DESINCORPORAÇÃO";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_RELATORIO", nomeRelatorio);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("idSaidaMaterial", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/saida-material-desincorporacao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error(ex.getMessage());
        }
    }
}
