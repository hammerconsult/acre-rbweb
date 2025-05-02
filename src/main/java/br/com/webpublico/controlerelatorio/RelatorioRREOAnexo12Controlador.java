/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.RREOAnexo12Item;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo12Facade;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo12", pattern = "/relatorio/rreo/anexo12/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo12.xhtml")
})
public class RelatorioRREOAnexo12Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    @EJB
    private RelatorioRREOAnexo12Facade relatorioRREOAnexo12Facade;
    private BigDecimal totalGrupo1Coluna3;

    public RelatorioRREOAnexo12Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo12", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        totalGrupo1Coluna3 = BigDecimal.ZERO;
        portalTipoAnexo = PortalTipoAnexo.ANEXO12_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        super.limparCampos();
    }

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), exercicio.getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setRelatorio(getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 12", exercicio, TipoRelatorioItemDemonstrativo.RREO));
        itemDemonstrativoFiltros.setExercicio(exercicio);
    }

    private List<RREOAnexo12Item> prepararDadosGrupo1() {
        List<RREOAnexo12Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo12Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                rel.setDescricao(item.getDescricaoComEspacos());
                rel.setValorColuna1(calcularPrevisaoInicial(itemDemonstrativo));
                rel.setValorColuna2(calcularPrevisaoAtualizada(itemDemonstrativo).add(rel.getValorColuna1()));
                rel.setValorColuna3(calcularReceitaRealizada(itemDemonstrativo));
                if (rel.getValorColuna2().compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna4(rel.getValorColuna3().multiply(BigDecimal.valueOf(100)).divide(rel.getValorColuna2(), 2, RoundingMode.HALF_UP));
                }
                if (item.getOrdem() == 21) {
                    totalGrupo1Coluna3 = rel.getValorColuna3();
                }
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo12Item> prepararDadosGrupo3() {
        List<RREOAnexo12Item> toReturn = Lists.newArrayList();
        BigDecimal totalOrdem5Coluna1 = BigDecimal.ZERO;
        BigDecimal totalOrdem5Coluna2 = BigDecimal.ZERO;
        BigDecimal totalOrdem5Coluna3 = BigDecimal.ZERO;
        BigDecimal despesaMinima = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo12Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                rel.setDescricao(item.getDescricaoComEspacos());
                switch (item.getOrdem()) {
                    case 6:
                        if (totalGrupo1Coluna3.compareTo(BigDecimal.ZERO) != 0) {
                            rel.setValorColuna1(totalGrupo1Coluna3.multiply(BigDecimal.valueOf(15)).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
                            despesaMinima = rel.getValorColuna1();
                        }
                        break;
                    case 8:
                        rel.setValorColuna1(totalOrdem5Coluna1.subtract(despesaMinima));
                        rel.setValorColuna2(totalOrdem5Coluna2.subtract(despesaMinima));
                        rel.setValorColuna3(totalOrdem5Coluna3.subtract(despesaMinima));
                        break;
                    case 9:
                        if ((totalOrdem5Coluna1.subtract(despesaMinima)).compareTo(BigDecimal.ZERO) < 0) {
                            rel.setValorColuna1(totalOrdem5Coluna1.subtract(despesaMinima));
                        }
                        rel.setValorColuna2(null);
                        rel.setValorColuna3(null);
                        break;
                    case 10:
                        if (totalGrupo1Coluna3.compareTo(BigDecimal.ZERO) != 0) {
                            rel.setValorColuna1(totalOrdem5Coluna1.divide(totalGrupo1Coluna3, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
                            rel.setValorColuna2(totalOrdem5Coluna2.divide(totalGrupo1Coluna3, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
                            rel.setValorColuna3(null);
                        }
                        break;
                    default:
                        rel.setValorColuna1(calcularDespesasEmpenhadasAteBimestre(itemDemonstrativo));
                        rel.setValorColuna2(calcularDespesasLiquidadasAteBimestre(itemDemonstrativo));
                        rel.setValorColuna3(calcularDespesasPagasAteBimestre(itemDemonstrativo));
                        if (item.getOrdem() == 5) {
                            totalOrdem5Coluna1 = rel.getValorColuna1();
                            totalOrdem5Coluna2 = rel.getValorColuna2();
                            totalOrdem5Coluna3 = rel.getValorColuna3();
                        }
                        break;
                }
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo12Item> buscarRegistros() {
        List<RREOAnexo12Item> toReturn = Lists.newArrayList();
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRREOAnexo12Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 12", TipoRelatorioItemDemonstrativo.RREO);
        itens = Lists.newArrayList();
        totalGrupo1Coluna3 = BigDecimal.ZERO;
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        prepararDadosGrupo1();
        prepararDadosGrupo10();
        toReturn.addAll(prepararDadosGrupo3());
        return toReturn;
    }

    private void calcularDespesasPorSubfuncao(RREOAnexo12Item rel, ItemDemonstrativoComponente item, ItemDemonstrativo itemDemonstrativo) {
        rel.setDescricao(item.getDescricaoComEspacos());
        rel.setValorColuna1(calcularDotacaoInicial(itemDemonstrativo));
        rel.setValorColuna2(calcularDotacaoAtualizada(itemDemonstrativo).add(rel.getValorColuna1()));
        rel.setValorColuna3(calcularDespesasEmpenhadasAteBimestre(itemDemonstrativo));
        rel.setValorColuna5(calcularDespesasLiquidadasAteBimestre(itemDemonstrativo));
        rel.setValorColuna7(calcularDespesasPagasAteBimestre(itemDemonstrativo));
        if (rel.getValorColuna2().compareTo(BigDecimal.ZERO) != 0) {
            rel.setValorColuna4(rel.getValorColuna3().multiply(BigDecimal.valueOf(100)).divide(rel.getValorColuna2(), 2, RoundingMode.HALF_UP));
            rel.setValorColuna6(rel.getValorColuna5().multiply(BigDecimal.valueOf(100)).divide(rel.getValorColuna2(), 2, RoundingMode.HALF_UP));
            rel.setValorColuna8(rel.getValorColuna7().multiply(BigDecimal.valueOf(100)).divide(rel.getValorColuna2(), 2, RoundingMode.HALF_UP));
        }
        if (bimestre.isUltimoBimestre()) {
            rel.setValorColuna9(calcularRestosAPagarNaoProcessados(itemDemonstrativo));
        }
    }

    private List<RREOAnexo12Item> prepararDadosGrupo10() {
        List<RREOAnexo12Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo12Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 10);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                calcularDespesasPorSubfuncao(rel, item, itemDemonstrativo);
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
        dto.adicionarParametro("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("BIMESTRE_FINAL", bimestre.isUltimoBimestre());
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
        dto.adicionarParametro("ARTIGO", getArtigo());
        dto.adicionarParametro("mesInicial", bimestre.getMesInicial().getNumeroMesString());
        dto.adicionarParametro("mesFinal", bimestre.getMesFinal().getNumeroMesString());
        dto.adicionarParametro("relatoriosItemDemonst", getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 12", exercicio, TipoRelatorioItemDemonstrativo.RREO).toDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.setNomeRelatorio("RelatorioRREOAnexo12");
    }

    private String getArtigo() {
        if (exercicio.getAno() > 2022) {
            return "RREO – ANEXO XII  (LC n° 141/2012 art.35)";
        }
        return "RREO – Anexo 12 (ADCT, art. 77)";
    }

    private BigDecimal calcularPrevisaoInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo12Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_INICIAL);
    }

    private BigDecimal calcularPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo12Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_ATUALIZADA);
    }

    private BigDecimal calcularReceitaRealizada(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo12Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE);
    }

    private BigDecimal calcularDotacaoInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo12Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DOTACAO_INICIAL);
    }

    private BigDecimal calcularDotacaoAtualizada(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo12Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.CREDITOS_ADICIONAIS);
    }

    private BigDecimal calcularDespesasPagasAteBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo12Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_PAGAS_ATE_BIMESTRE);
    }

    private BigDecimal calcularDespesasLiquidadasAteBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo12Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_LIQUIDADAS_ATE_BIMESTRE);
    }

    private BigDecimal calcularDespesasEmpenhadasAteBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo12Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_EMPENHADAS_ATE_BIMESTRE);
    }

    private BigDecimal calcularRestosAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo12Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_NAO_PROCESSADOS);
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo12";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo12/";
    }
}
