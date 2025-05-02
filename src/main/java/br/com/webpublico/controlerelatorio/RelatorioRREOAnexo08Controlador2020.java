package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo08Facade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 25/10/13
 * Time: 11:19
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo8-2020", pattern = "/relatorio/rreo/anexo8/2020", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo08-2020.xhtml")
})
@Deprecated
public class RelatorioRREOAnexo08Controlador2020 extends AbstractReport implements Serializable {

    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private RelatorioRREOAnexo08Facade relatorioRREOAnexo08Facade;
    private String mesInicial;
    private String mesFinal;
    private List<ItemDemonstrativoComponente> itens;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private BigDecimal item3AteBimestre;
    private BigDecimal item11AteBimestre;
    private BigDecimal item12AteBimestre;
    private BigDecimal item13AteBimestre;
    private BigDecimal item131AteBimestre;
    private BigDecimal item132AteBimestre;
    private BigDecimal item14AteBimestre;
    private BigDecimal item17_1;
    private BigDecimal item17_2;
    private BigDecimal item22AteBimestre;
    private BigDecimal item23AteBimestre;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;

    public RelatorioRREOAnexo08Controlador2020() {
        itens = Lists.newArrayList();
    }

    private List<RREOAnexo08Item01> buscarDadosGrupo1() {
        List<RREOAnexo08Item01> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item01 it = new RREOAnexo08Item01();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setPrevisaoInicial(buscarPrevisaoInicial(itemDemonstrativo));
                it.setPrevisaoAtualizada(buscarPrevisaoAtualizada(itemDemonstrativo));
                it.setPrevisaoAtualizada(it.getPrevisaoInicial().add(it.getPrevisaoAtualizada()));
                it.setReceitaRealizadaAteBimestre(buscarReceitaRealizadaAteOBimestre(itemDemonstrativo));
                if (it.getDescricao().startsWith("3-")) {
                    item3AteBimestre = it.getReceitaRealizadaAteBimestre();
                }
                if (it.getPrevisaoAtualizada().compareTo(BigDecimal.ZERO) > 0) {
                    it.setReceitaRealizadaPercentual(it.getReceitaRealizadaAteBimestre().divide(it.getPrevisaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    it.setReceitaRealizadaPercentual(BigDecimal.ZERO);
                }
                if (itemDemonstrativo.getExibirNoRelatorio()) {
                    toReturn.add(it);
                }
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item03> buscarDadosGrupo3() {
        BigDecimal item111AteBimestre = BigDecimal.ZERO;
        BigDecimal item10AteBimestre = BigDecimal.ZERO;
        List<RREOAnexo08Item03> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item03 it = new RREOAnexo08Item03();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setPrevisaoInicial(buscarPrevisaoInicial(itemDemonstrativo));
                it.setPrevisaoAtualizada(buscarPrevisaoAtualizada(itemDemonstrativo));
                it.setPrevisaoAtualizada(it.getPrevisaoInicial().add(it.getPrevisaoAtualizada()));
                if (it.getDescricao().startsWith("12-")) {
                    it.setReceitaRealizadaAteBimestre(item111AteBimestre.subtract(item10AteBimestre));
                    item12AteBimestre = it.getReceitaRealizadaAteBimestre();
                } else {
                    it.setReceitaRealizadaAteBimestre(buscarReceitaRealizadaAteOBimestre(itemDemonstrativo));
                }
                if (it.getReceitaRealizadaAteBimestre().compareTo(BigDecimal.ZERO) < 0) {
                    it.setReceitaRealizadaAteBimestre(it.getReceitaRealizadaAteBimestre().multiply(new BigDecimal("-1")));
                }
                if (it.getPrevisaoAtualizada().compareTo(BigDecimal.ZERO) != 0) {
                    it.setReceitaRealizadaPercentual(it.getReceitaRealizadaAteBimestre().divide(it.getPrevisaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    it.setReceitaRealizadaPercentual(BigDecimal.ZERO);
                }
                if (it.getDescricao().startsWith("11-")) {
                    item11AteBimestre = it.getReceitaRealizadaAteBimestre();
                } else if (it.getDescricao().trim().startsWith("11.1-")) {
                    item111AteBimestre = it.getReceitaRealizadaAteBimestre();
                } else if (it.getDescricao().startsWith("10-")) {
                    item10AteBimestre = it.getReceitaRealizadaAteBimestre();
                }
                if (itemDemonstrativo.getExibirNoRelatorio()) {
                    toReturn.add(it);
                }
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item04> buscarDadosGrupo4() {
        item131AteBimestre = BigDecimal.ZERO;
        item132AteBimestre = BigDecimal.ZERO;
        List<RREOAnexo08Item04> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item04 it = new RREOAnexo08Item04();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setDotacaoInicial(buscarDotacaoInicial(itemDemonstrativo));
                it.setDotacaoAtualizada(buscarCreditosAdicionais(itemDemonstrativo));
                it.setDotacaoAtualizada(it.getDotacaoAtualizada().add(it.getDotacaoInicial()));
                it.setDespesaEmpenhadaAteBimestre(buscarDespesaEmpenhadaAteBimestre(itemDemonstrativo));
                it.setDespesaLiquidadaAteBimestre(buscarDespesasLiquidadasAteOBimestre(itemDemonstrativo));
                if (it.getDotacaoAtualizada().compareTo(BigDecimal.ZERO) != 0) {
                    it.setDespesaEmpenhadaPercentual(it.getDespesaEmpenhadaAteBimestre().divide(it.getDotacaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    it.setDespesaLiquidadaPercentual(it.getDespesaLiquidadaAteBimestre().divide(it.getDotacaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    it.setDespesaEmpenhadaPercentual(BigDecimal.ZERO);
                    it.setDespesaLiquidadaPercentual(BigDecimal.ZERO);
                }
                if (isUltimoBimestre()) {
                    it.setRestoAPagar(buscarRestoAPagarNaoProcessados(itemDemonstrativo));
                } else {
                    it.setRestoAPagar(BigDecimal.ZERO);
                }
                if (it.getDescricao().startsWith("13-")) {
                    item13AteBimestre = it.getDespesaLiquidadaAteBimestre().add(it.getRestoAPagar());
                } else if (it.getDescricao().trim().startsWith("13.1-")) {
                    item131AteBimestre = it.getDespesaLiquidadaAteBimestre().add(it.getRestoAPagar());
                } else if (it.getDescricao().trim().startsWith("13.2-")) {
                    item132AteBimestre = it.getDespesaLiquidadaAteBimestre().add(it.getRestoAPagar());
                } else if (it.getDescricao().startsWith("14-")) {
                    item14AteBimestre = it.getDespesaLiquidadaAteBimestre().add(it.getRestoAPagar());
                }
                if (itemDemonstrativo.getExibirNoRelatorio()) {
                    toReturn.add(it);
                }
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item05> buscarDadosGrupo5() {
        List<RREOAnexo08Item05> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 5);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item05 it = new RREOAnexo08Item05();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setOrdem(item.getOrdem());
                if (itemDemonstrativo.getDescricao().startsWith("16")) {
                    it.setValor(buscarEmpenhoRestoPorContaDestinacao(itemDemonstrativo).add(buscarCreditosAdicionais(itemDemonstrativo)));
                } else if (itemDemonstrativo.getDescricao().startsWith("17")) {
                    it.setValor(buscarCreditosAdicionaisSuperavit(itemDemonstrativo));
                    if (itemDemonstrativo.getDescricao().startsWith("17.1-")) {
                        item17_1 = it.getValor();
                    } else if (itemDemonstrativo.getDescricao().startsWith("17.2-")) {
                        item17_2 = it.getValor();
                    }
                } else {
                    it.setValor(buscarCreditosAdicionaisSuperavit(itemDemonstrativo));
                }
                if (itemDemonstrativo.getExibirNoRelatorio()) {
                    toReturn.add(it);
                }
            }
        }
        return toReturn;
    }

    private HashMap<String, BigDecimal> buscarDadosGrupo6ParaAnexo14() {
        HashMap<String, BigDecimal> retorno = new HashMap<>();
        if (item11AteBimestre != null && item11AteBimestre.compareTo(BigDecimal.ZERO) != 0) {
            retorno.put("INFANTIL", ((item13AteBimestre.subtract(item17_1)).multiply(BigDecimal.valueOf(100))).divide(item11AteBimestre, 6, BigDecimal.ROUND_HALF_UP));
        }
        return retorno;
    }

    private List<RREOAnexo08Item09> buscarDadosGrupo8() {
        List<RREOAnexo08Item09> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 8);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item09 it = new RREOAnexo08Item09();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setDotacaoInicial(buscarDotacaoInicial(itemDemonstrativo));
                it.setDotacaoAtualizada(buscarCreditosAdicionais(itemDemonstrativo));
                it.setDotacaoAtualizada(it.getDotacaoAtualizada().add(it.getDotacaoInicial()));
                it.setDespesaEmpenhadaAteBimestre(buscarDespesaEmpenhadaAteBimestre(itemDemonstrativo));
                it.setDespesaLiquidadaAteBimestre(buscarDespesasLiquidadasAteOBimestre(itemDemonstrativo));
                if (it.getDotacaoAtualizada().compareTo(BigDecimal.ZERO) != 0) {
                    it.setDespesaEmpenhadaPercentual(it.getDespesaEmpenhadaAteBimestre().divide(it.getDotacaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    it.setDespesaLiquidadaPercentual(it.getDespesaLiquidadaAteBimestre().divide(it.getDotacaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    it.setDespesaEmpenhadaPercentual(BigDecimal.ZERO);
                    it.setDespesaLiquidadaPercentual(BigDecimal.ZERO);
                }
                if (isUltimoBimestre()) {
                    it.setRestoAPagar(buscarRestoAPagarNaoProcessados(itemDemonstrativo));
                } else {
                    it.setRestoAPagar(BigDecimal.ZERO);
                }
                if (it.getDescricao().startsWith("22")) {
                    item22AteBimestre = it.getDespesaLiquidadaAteBimestre().add(it.getRestoAPagar());
                } else if (it.getDescricao().startsWith("23")) {
                    item23AteBimestre = it.getDespesaLiquidadaAteBimestre().add(it.getRestoAPagar());
                }
                if (itemDemonstrativo.getExibirNoRelatorio()) {
                    toReturn.add(it);
                }
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item10> buscarDadosGrupo9() {
        List<RREOAnexo08Item10> toReturn = new ArrayList<>();
        BigDecimal valor = BigDecimal.ZERO;
        BigDecimal valorItem38 = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 9);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item10 it = new RREOAnexo08Item10();
                it.setDescricao(item.getDescricaoComEspacos());
                if (itemDemonstrativo.getDescricao().startsWith("30") || itemDemonstrativo.getDescricao().startsWith("31") || itemDemonstrativo.getDescricao().startsWith("32")) {
                    it.setValor(buscarEmpenhoNormalPorContaDestinacao(itemDemonstrativo));
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("33")) {
                    corrigirNumeroNoFinalDaString(item, it);
                    it.setValor(buscarEmpenhoRestoPorContaDestinacao(itemDemonstrativo));
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("29")) {
                    it.setValor(item12AteBimestre);
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("36")) {
                    toReturn.add(new RREOAnexo08Item10());
                    corrigirNumeroNoFinalDaString(item, it);
                    it.setValor(item22AteBimestre.add(item23AteBimestre).subtract(valor));
                    valorItem38 = it.getValor();
                } else if (itemDemonstrativo.getDescricao().startsWith("37") && item3AteBimestre.compareTo(BigDecimal.ZERO) != 0) {
                    toReturn.add(new RREOAnexo08Item10());
                    corrigirNumeroNoFinalDaString(item, it);
                    it.setValor(valorItem38.divide(item3AteBimestre, 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else if (itemDemonstrativo.getDescricao().startsWith("35")) {
                    corrigirNumeroNoFinalDaString(item, it);
                    it.setValor(valor);
                } else {
                    it.setValor(buscarValorContabilAtual(itemDemonstrativo));
                    valor = valor.add(it.getValor());
                }
                if (itemDemonstrativo.getExibirNoRelatorio()) {
                    toReturn.add(it);
                }
            }
        }
        return toReturn;
    }

    private void corrigirNumeroNoFinalDaString(ItemDemonstrativoComponente item, RREOAnexo08Item10 it) {
        String descricao = item.getDescricaoComEspacos();
        descricao = descricao.substring(0, descricao.length() - 1) + "<sup><font fontName=\"Arial\" size=\"6\">" + item.getDescricaoComEspacos().substring(item.getDescricaoComEspacos().length() - 1, item.getDescricaoComEspacos().length()) + "</font></sup>";
        it.setDescricao(descricao);
    }

    public List<RREOAnexo08Item10> buscaRegistros() {
        List<RREOAnexo08Item10> toReturn = new ArrayList<>();
        List<ItemDemonstrativo> itensDemonstrativos = recuperarRelatorioEConfiguracoes();
        popularComponentes(itensDemonstrativos);
        instanciarItemDemonstrativoFiltros();
        buscarDadosGrupo1();
        buscarDadosGrupo3();
        buscarDadosGrupo8();
        toReturn.addAll(buscarDadosGrupo9());
        return toReturn;
    }

    private List<ItemDemonstrativo> recuperarRelatorioEConfiguracoes() {
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        return itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(getSistemaFacade().getExercicioCorrente(), "", "Anexo 8", TipoRelatorioItemDemonstrativo.RREO);
    }

    private void popularComponentes(List<ItemDemonstrativo> itensDemonstrativos) {
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
    }

    public List<RREOAnexo08Item04> buscarRegistrosIndice13() {
        List<RREOAnexo08Item04> toReturn = new ArrayList<>();
        List<ItemDemonstrativo> itensDemonstrativos = recuperarRelatorioEConfiguracoes();
        popularComponentes(itensDemonstrativos);
        instanciarItemDemonstrativoFiltros();
        toReturn.addAll(buscarDadosGrupo4());
        return toReturn;
    }

    public List<RREOAnexo08Item05> buscarRegistrosIndice17() {
        List<RREOAnexo08Item05> toReturn = new ArrayList<>();
        List<ItemDemonstrativo> itensDemonstrativos = recuperarRelatorioEConfiguracoes();
        popularComponentes(itensDemonstrativos);
        instanciarItemDemonstrativoFiltros();
        toReturn.addAll(buscarDadosGrupo5());
        return toReturn;
    }

    public HashMap<String, BigDecimal> buscarRegistrosIndice19() {
        List<ItemDemonstrativo> itensDemonstrativos = recuperarRelatorioEConfiguracoes();
        popularComponentes(itensDemonstrativos);
        instanciarItemDemonstrativoFiltros();
        buscarDadosGrupo3();
        buscarDadosGrupo4();
        buscarDadosGrupo5();
        return buscarDadosGrupo6ParaAnexo14();
    }

    public void gerarRelatorioRREOAnexo08() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("contabil/rreo-anexo8-2020/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("contabil/rreo-anexo8-2020/salvar/");
            ConfiguracaoDeRelatorio configuracao = getSistemaFacade().buscarConfiguracaoDeRelatorioPorChave();
            String urlWebreport = configuracao.getUrl() + dto.getApi();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ResponseEntity<byte[]> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, byte[].class);
            byte[] array = exchange.getBody();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(getSistemaFacade().getExercicioCorrente());
            anexo.setNome("Anexo 8 - Demonstrativo Das Receitas e Despesas com Manutenção e Desenvolvimento do Ensino - MDE");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(dto, "RelatorioRREOAnexo08", anexo, array);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void montarDtoSemApi(RelatorioDTO dto) {
        RelatoriosItemDemonst relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("DATAINICIAL", Mes.getMesToInt(Integer.valueOf(mesInicial)).getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", Mes.getMesToInt(Integer.valueOf(mesFinal)).getDescricao().toUpperCase());
        dto.adicionarParametro("BIMESTRE_FINAL", isUltimoBimestre());
        dto.adicionarParametro("ID_EXERCICIO", getSistemaFacade().getExercicioCorrente().getId());
        dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
        dto.adicionarParametro("mesInicial", mesInicial);
        dto.adicionarParametro("mesFinal", mesFinal);
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.setNomeRelatorio("RelatorioRREOAnexo08");

    }

    private boolean isUltimoBimestre() {
        return "12".equals(mesFinal);
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(new Integer(mesFinal), getSistemaFacade().getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataReferencia("01/" + mesInicial + "/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(getSistemaFacade().getExercicioCorrente());
    }

    @URLAction(mappingId = "relatorio-rreo-anexo8-2020", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mesFinal = null;
        mesInicial = null;
        item3AteBimestre = BigDecimal.ZERO;
        item11AteBimestre = BigDecimal.ZERO;
        item12AteBimestre = BigDecimal.ZERO;
        item13AteBimestre = BigDecimal.ZERO;
        item131AteBimestre = BigDecimal.ZERO;
        item132AteBimestre = BigDecimal.ZERO;
        item14AteBimestre = BigDecimal.ZERO;
        item17_1 = BigDecimal.ZERO;
        item17_2 = BigDecimal.ZERO;
        item22AteBimestre = BigDecimal.ZERO;
        item23AteBimestre = BigDecimal.ZERO;
    }

    private BigDecimal buscarValorContabilAtual(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_ATUAL);
    }

    private BigDecimal buscarReceitaRealizadaAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE);
    }

    private BigDecimal buscarPrevisaoInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_INICIAL);
    }

    private BigDecimal buscarPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_ATUALIZADA);
    }

    private BigDecimal buscarDotacaoInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DOTACAO_INICIAL);
    }

    private BigDecimal buscarCreditosAdicionais(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.CREDITOS_ADICIONAIS);
    }

    private BigDecimal buscarCreditosAdicionaisSuperavit(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.CREDITOS_ADICIONAIS_SUPERAVIT);
    }

    private BigDecimal buscarDespesaEmpenhadaAteBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_EMPENHADAS_ATE_BIMESTRE);
    }

    private BigDecimal buscarEmpenhoNormalPorContaDestinacao(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.EMPENHO_NORMAL_POR_CONTA_DESTINACAO);
    }

    private BigDecimal buscarEmpenhoRestoPorContaDestinacao(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.EMPENHO_RESTO_POR_CONTA_DESTINACAO);
    }

    private BigDecimal buscarDespesasLiquidadasAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_LIQUIDADAS_ATE_BIMESTRE);
    }

    private BigDecimal buscarRestoAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_NAO_PROCESSADOS);
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
        Integer mes = Integer.parseInt(this.mesFinal);
        if (mes == 12) {
            this.mesInicial = "11";
        } else {
            this.mesInicial = "0" + (mes - 1);
        }
    }

    public BigDecimal getItem11AteBimestre() {
        return item11AteBimestre;
    }

    public void setItem11AteBimestre(BigDecimal item11AteBimestre) {
        this.item11AteBimestre = item11AteBimestre;
    }

    public BigDecimal getItem12AteBimestre() {
        return item12AteBimestre;
    }

    public void setItem12AteBimestre(BigDecimal item12AteBimestre) {
        this.item12AteBimestre = item12AteBimestre;
    }

    public BigDecimal getItem13AteBimestre() {
        return item13AteBimestre;
    }

    public void setItem13AteBimestre(BigDecimal item13AteBimestre) {
        this.item13AteBimestre = item13AteBimestre;
    }

    public BigDecimal getItem14AteBimestre() {
        return item14AteBimestre;
    }

    public void setItem14AteBimestre(BigDecimal item14AteBimestre) {
        this.item14AteBimestre = item14AteBimestre;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public void setItemDemonstrativoFacade(ItemDemonstrativoFacade itemDemonstrativoFacade) {
        this.itemDemonstrativoFacade = itemDemonstrativoFacade;
    }

    public RelatorioRREOAnexo08Facade getRelatorioRREOAnexo08Facade() {
        return relatorioRREOAnexo08Facade;
    }

    public void setRelatorioRREOAnexo08Facade(RelatorioRREOAnexo08Facade relatorioRREOAnexo08Facade) {
        this.relatorioRREOAnexo08Facade = relatorioRREOAnexo08Facade;
    }

    public ItemDemonstrativoFiltros getItemDemonstrativoFiltros() {
        return itemDemonstrativoFiltros;
    }

    public void setItemDemonstrativoFiltros(ItemDemonstrativoFiltros itemDemonstrativoFiltros) {
        this.itemDemonstrativoFiltros = itemDemonstrativoFiltros;
    }
}
