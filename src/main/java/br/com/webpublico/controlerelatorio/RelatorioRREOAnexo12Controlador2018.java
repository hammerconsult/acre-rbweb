/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.RREOAnexo12Item;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.negocios.RelatorioRREOAnexo12Calculator2018;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo12-2018", pattern = "/relatorio/rreo/anexo12/2018/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo12-2018.xhtml")
})
@Deprecated
public class RelatorioRREOAnexo12Controlador2018 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRREOAnexo12Calculator2018 relatorioAnexo12RREOCalculator;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    private List<ItemDemonstrativoComponente> itens;
    private String dataInicial;
    private String dataFinal;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private BigDecimal totalGrupo1Coluna3;
    private BigDecimal totalGrupo3Coluna3;
    private BigDecimal totalGrupo3Coluna5;
    private BigDecimal totalGrupo5Coluna5;

    public RelatorioRREOAnexo12Controlador2018() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
        totalGrupo5Coluna5 = BigDecimal.ZERO;
        totalGrupo1Coluna3 = BigDecimal.ZERO;
        totalGrupo3Coluna3 = BigDecimal.ZERO;
        totalGrupo3Coluna5 = BigDecimal.ZERO;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo1() throws ParseException {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                rel.setValorColuna1(calculaPrevisaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                rel.setValorColuna2(calculaPrevisaoAtualizada(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst).add(rel.getValorColuna1()));
                rel.setValorColuna3(calculaReceitaRealizada(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                if (rel.getValorColuna2().compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna4(rel.getValorColuna3().multiply(BigDecimal.valueOf(100)).divide(rel.getValorColuna2(), 2));
                } else {
                    rel.setValorColuna4(BigDecimal.ZERO);
                }
                if (rel.getDescricao().equals("TOTAL DAS RECEITAS PARA APURAÇÃO DA APLICAÇÃO EM AÇÕES E SERVIÇOS PÚBLICOS DE SAÚDE (III) = I + II")) {
                    totalGrupo1Coluna3 = rel.getValorColuna3();
                }
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo2() throws ParseException {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                rel.setValorColuna1(calculaPrevisaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                rel.setValorColuna2(calculaPrevisaoAtualizada(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst).add(rel.getValorColuna1()));
                rel.setValorColuna3(calculaReceitaRealizada(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                if (rel.getValorColuna2().compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna4(rel.getValorColuna3().multiply(BigDecimal.valueOf(100)).divide(rel.getValorColuna2(), 2));
                } else {
                    rel.setValorColuna4(BigDecimal.ZERO);
                }
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo3() {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                rel.setValorColuna1(calculaDotacaoInicial(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                rel.setValorColuna2(rel.getValorColuna1().add(calculaDotacaoAtualizada(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst)));
                rel.setValorColuna3(calculaDespesaEmpenhadaAteBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                if (rel.getValorColuna2().compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna4(rel.getValorColuna3().divide(rel.getValorColuna2(), 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    rel.setValorColuna4(BigDecimal.ZERO);
                }
                rel.setValorColuna5(calculaDespesaLiquidadaAteBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                if (rel.getValorColuna2().compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna6(rel.getValorColuna5().divide(rel.getValorColuna2(), 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    rel.setValorColuna6(BigDecimal.ZERO);
                }
                if (dataFinal.equals("12")) {
                    rel.setValorColuna7(recuperarRestoAPagarNaoProcessados(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                } else {
                    rel.setValorColuna7(BigDecimal.ZERO);
                }
                if (rel.getDescricao().equals("TOTAL DAS DESPESAS COM SAÚDE (IV)")) {
                    totalGrupo3Coluna3 = rel.getValorColuna3();
                    totalGrupo3Coluna5 = rel.getValorColuna5();
                }
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo4() {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                rel.setValorColuna1(calculaDotacaoInicial(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                if (rel.getDescricao().trim().startsWith("DESPESAS CUSTEADAS COM DISPONIBILIDADE ")) {
                    //solicitação Sr Tonismar para o ajuste de valor no relatório.
                    if (dataFinal.equals("12") && sistemaControlador.getExercicioCorrente().getAno() == 2015) {
                        rel.setValorColuna2(BigDecimal.valueOf(13312.65));
                        rel.setValorColuna3(BigDecimal.valueOf(13312.65));
                        rel.setValorColuna5(BigDecimal.valueOf(13312.65));
                    } else {
                        rel.setValorColuna2(BigDecimal.ZERO);
                        rel.setValorColuna3(BigDecimal.ZERO);
                        rel.setValorColuna5(BigDecimal.ZERO);
                    }
                } else {
                    rel.setValorColuna2(rel.getValorColuna1().add(calculaDotacaoAtualizada(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst)));
                    rel.setValorColuna3(calculaDespesaEmpenhadaAteBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    rel.setValorColuna5(calculaDespesaLiquidadaAteBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                }
                if (rel.getDescricao().trim().startsWith("TOTAL DAS DESPESAS COM NÃO COMPUTADAS (V)")) {
                    if (dataFinal.equals("12") && sistemaControlador.getExercicioCorrente().getAno() == 2015) {
                        rel.setValorColuna2(rel.getValorColuna2().add(BigDecimal.valueOf(13312.65)));
                        rel.setValorColuna3(rel.getValorColuna3().add(BigDecimal.valueOf(13312.65)));
                        rel.setValorColuna5(rel.getValorColuna5().add(BigDecimal.valueOf(13312.65)));
                    }
                }
                if (totalGrupo3Coluna3.compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna4(rel.getValorColuna3().divide(totalGrupo3Coluna3, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    rel.setValorColuna4(BigDecimal.ZERO);
                }

                if (totalGrupo3Coluna5.compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna6(rel.getValorColuna5().divide(totalGrupo3Coluna5, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    rel.setValorColuna6(BigDecimal.ZERO);
                }
                if (dataFinal.equals("12")) {
                    rel.setValorColuna7(recuperarRestoAPagarNaoProcessados(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                } else {
                    rel.setValorColuna7(BigDecimal.ZERO);
                }

                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo5() {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 5);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                rel.setValorColuna1(calculaDotacaoInicial(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                if (dataFinal.equals("12")) {
                    rel.setValorColuna7(recuperarRestoAPagarNaoProcessados(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                } else {
                    rel.setValorColuna7(BigDecimal.ZERO);
                }
                if (dataFinal.equals("12") && sistemaControlador.getExercicioCorrente().getAno() == 2015) {
                    rel.setValorColuna2(rel.getValorColuna1().add(calculaDotacaoAtualizada(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst)).subtract(BigDecimal.valueOf(13312.65)));
                    rel.setValorColuna3(calculaDespesaEmpenhadaAteBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst).subtract(BigDecimal.valueOf(13312.65)));
                    rel.setValorColuna5(calculaDespesaLiquidadaAteBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst).subtract(BigDecimal.valueOf(13312.65)));
                } else {
                    rel.setValorColuna2(rel.getValorColuna1().add(calculaDotacaoAtualizada(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst)));
                    rel.setValorColuna3(calculaDespesaEmpenhadaAteBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    rel.setValorColuna5(calculaDespesaLiquidadaAteBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                }
                if (totalGrupo3Coluna3.compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna4(rel.getValorColuna3().divide(totalGrupo3Coluna3, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    rel.setValorColuna4(BigDecimal.ZERO);
                }

                if (totalGrupo3Coluna5.compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna6(rel.getValorColuna5().divide(totalGrupo3Coluna5, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    rel.setValorColuna6(BigDecimal.ZERO);
                }

                totalGrupo5Coluna5 = rel.getValorColuna5().add(rel.getValorColuna7());
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo6() {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 6);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                if (totalGrupo1Coluna3.compareTo(BigDecimal.ZERO) != 0) {
                    rel.setValorColuna1(totalGrupo5Coluna5.divide(totalGrupo1Coluna3, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    rel.setValorColuna1(BigDecimal.ZERO);
                }
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo12Item> buscaRegistros() throws ParseException {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 12", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 12", TipoRelatorioItemDemonstrativo.RREO);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            ItemDemonstrativoComponente itemDemonstrativoComponente = new ItemDemonstrativoComponente();
            itemDemonstrativoComponente.setNome(itemDemonstrativo.getNome());
            itemDemonstrativoComponente.setDescricao(itemDemonstrativo.getDescricao());
            itemDemonstrativoComponente.setOrdem(itemDemonstrativo.getOrdem());
            itemDemonstrativoComponente.setColuna(itemDemonstrativo.getColuna());
            itemDemonstrativoComponente.setGrupo(itemDemonstrativo.getGrupo());
            itemDemonstrativoComponente.setEspaco(itemDemonstrativo.getEspaco());
            itemDemonstrativoComponente.setItemDemonstrativo(itemDemonstrativo);
            itens.add(itemDemonstrativoComponente);
        }
        preparaDadosGrupo1();
        preparaDadosGrupo5();
        toReturn.addAll(preparaDadosGrupo6());
        return toReturn;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo7() {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 7);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                if (totalGrupo1Coluna3.compareTo(BigDecimal.ZERO) != 0) {
                    rel.setValorColuna1(totalGrupo5Coluna5.subtract((totalGrupo1Coluna3.multiply(BigDecimal.valueOf(15))).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                } else {
                    rel.setValorColuna1(BigDecimal.ZERO);
                }
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo8() {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        BigDecimal inscritosAnoAnterior = BigDecimal.ZERO;
        BigDecimal inscritosAnoAnteriorAoAnterior = BigDecimal.ZERO;
        BigDecimal canceladoAnoAnterior = BigDecimal.ZERO;
        BigDecimal canceladoAnoAnteriorAoAnterior = BigDecimal.ZERO;
        BigDecimal pagoAnoAnterior = BigDecimal.ZERO;
        BigDecimal pagoAnoAnteriorAoAnterior = BigDecimal.ZERO;
        BigDecimal aPagarAnoAnterior = BigDecimal.ZERO;
        BigDecimal aPagarAnoAnteriorAoAnterior = BigDecimal.ZERO;
        BigDecimal parcelaAnoAnterior = BigDecimal.ZERO;
        BigDecimal parcelaAnoAnteriorAoAnterior = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 8);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                if (item.getOrdem() == 1) {
                    rel.setValorColuna1(calculaRestosAPagarNaoProcessadosExAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    rel.setValorColuna2(calculaRestosAPagarCancelados(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    rel.setValorColuna3(calculaRestosAPagarNaoProcessadosPagosExAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    rel.setValorColuna4(calculaRestosAPagarNaoProcessadosAPagarExAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    rel.setValorColuna5(calculaRestosAPagarNaoProcessadosParcelaExAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    inscritosAnoAnterior = rel.getValorColuna1();
                    canceladoAnoAnterior = rel.getValorColuna2();
                    pagoAnoAnterior = rel.getValorColuna3();
                    aPagarAnoAnterior = rel.getValorColuna4();
                    parcelaAnoAnterior = rel.getValorColuna5();
                } else if (item.getOrdem() == 2) {
                    rel.setValorColuna1(calculaRestosAPagarNaoProcessadosExAnteriorAoAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    rel.setValorColuna2(calculaRestosAPagarCanceladosExAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    rel.setValorColuna3(calculaRestosAPagarNaoProcessadosPagosExAnteriorAoAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    rel.setValorColuna4(calculaRestosAPagarNaoProcessadosAPagarExAnteriorAoAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    rel.setValorColuna5(calculaRestosAPagarNaoProcessadosParcelaExAnteriorAoAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    inscritosAnoAnteriorAoAnterior = inscritosAnoAnteriorAoAnterior.add(rel.getValorColuna1());
                    canceladoAnoAnteriorAoAnterior = canceladoAnoAnteriorAoAnterior.add(rel.getValorColuna2());
                    pagoAnoAnteriorAoAnterior = pagoAnoAnteriorAoAnterior.add(rel.getValorColuna3());
                    aPagarAnoAnteriorAoAnterior = aPagarAnoAnteriorAoAnterior.add(rel.getValorColuna4());
                    parcelaAnoAnteriorAoAnterior = parcelaAnoAnteriorAoAnterior.add(rel.getValorColuna5());
                } else if (item.getOrdem() == 3) {
                    Exercicio ex = relatorioAnexo12RREOCalculator.getExercicioFacade().getExercicioPorAno(sistemaControlador.getExercicioCorrente().getAno() - 1);
                    rel.setValorColuna1(calculaRestosAPagarNaoProcessadosExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna2(calculaRestosAPagarCanceladosExAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna3(calculaRestosAPagarNaoProcessadosPagosExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna4(calculaRestosAPagarNaoProcessadosAPagarExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna5(calculaRestosAPagarNaoProcessadosParcelaExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    inscritosAnoAnteriorAoAnterior = inscritosAnoAnteriorAoAnterior.add(rel.getValorColuna1());
                    canceladoAnoAnteriorAoAnterior = canceladoAnoAnteriorAoAnterior.add(rel.getValorColuna2());
                    pagoAnoAnteriorAoAnterior = pagoAnoAnteriorAoAnterior.add(rel.getValorColuna3());
                    aPagarAnoAnteriorAoAnterior = aPagarAnoAnteriorAoAnterior.add(rel.getValorColuna4());
                    parcelaAnoAnteriorAoAnterior = parcelaAnoAnteriorAoAnterior.add(rel.getValorColuna5());
                } else if (item.getOrdem() == 4) {
                    Exercicio ex = relatorioAnexo12RREOCalculator.getExercicioFacade().getExercicioPorAno(sistemaControlador.getExercicioCorrente().getAno() - 2);
                    rel.setValorColuna1(calculaRestosAPagarNaoProcessadosExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna2(calculaRestosAPagarCanceladosExAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna3(calculaRestosAPagarNaoProcessadosPagosExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna4(calculaRestosAPagarNaoProcessadosAPagarExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna5(calculaRestosAPagarNaoProcessadosParcelaExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    inscritosAnoAnteriorAoAnterior = inscritosAnoAnteriorAoAnterior.add(rel.getValorColuna1());
                    canceladoAnoAnteriorAoAnterior = canceladoAnoAnteriorAoAnterior.add(rel.getValorColuna2());
                    pagoAnoAnteriorAoAnterior = pagoAnoAnteriorAoAnterior.add(rel.getValorColuna3());
                    aPagarAnoAnteriorAoAnterior = aPagarAnoAnteriorAoAnterior.add(rel.getValorColuna4());
                    parcelaAnoAnteriorAoAnterior = parcelaAnoAnteriorAoAnterior.add(rel.getValorColuna5());
                } else if (item.getOrdem() == 5) {
                    Exercicio ex = relatorioAnexo12RREOCalculator.getExercicioFacade().getExercicioPorAno(sistemaControlador.getExercicioCorrente().getAno() - 3);
                    rel.setValorColuna1(calculaRestosAPagarNaoProcessadosExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna2(calculaRestosAPagarCanceladosExAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna3(calculaRestosAPagarNaoProcessadosPagosExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna4(calculaRestosAPagarNaoProcessadosAPagarExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    rel.setValorColuna5(calculaRestosAPagarNaoProcessadosParcelaExAnteriorAoAnterior(itemDemonstrativo, ex, relatoriosItemDemonst));
                    inscritosAnoAnteriorAoAnterior = inscritosAnoAnteriorAoAnterior.add(rel.getValorColuna1());
                    canceladoAnoAnteriorAoAnterior = canceladoAnoAnteriorAoAnterior.add(rel.getValorColuna2());
                    pagoAnoAnteriorAoAnterior = pagoAnoAnteriorAoAnterior.add(rel.getValorColuna3());
                    aPagarAnoAnteriorAoAnterior = aPagarAnoAnteriorAoAnterior.add(rel.getValorColuna4());
                    parcelaAnoAnteriorAoAnterior = parcelaAnoAnteriorAoAnterior.add(rel.getValorColuna5());
                } else {
                    rel.setValorColuna1(inscritosAnoAnterior.add(inscritosAnoAnteriorAoAnterior));
                    rel.setValorColuna2(canceladoAnoAnterior.add(canceladoAnoAnteriorAoAnterior));
                    rel.setValorColuna3(pagoAnoAnterior.add(pagoAnoAnteriorAoAnterior));
                    rel.setValorColuna4(aPagarAnoAnterior.add(aPagarAnoAnteriorAoAnterior));
                    rel.setValorColuna5(parcelaAnoAnterior.add(parcelaAnoAnteriorAoAnterior));
                }
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo9() {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        BigDecimal restosColuna1 = BigDecimal.ZERO;
        BigDecimal restosColuna2 = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 9);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                if (rel.getDescricao().equals("Restos a Pagar Cancelados ou Prescritos em 2013")) {
                    Exercicio exercicio = relatorioAnexo12RREOCalculator.getExercicioFacade().getExercicioPorAno(sistemaControlador.getExercicioCorrente().getAno() - 2);
                    rel.setValorColuna1(calculaRestosAPagarCanceladosExAnterior(itemDemonstrativo, exercicio, relatoriosItemDemonst));
                    restosColuna1 = restosColuna1.add(rel.getValorColuna1());
                    rel.setValorColuna2(rel.getValorColuna1());
                    restosColuna2 = restosColuna2.add(rel.getValorColuna2());
                    rel.setValorColuna3(BigDecimal.ZERO);
                } else if (rel.getDescricao().equals("Restos a Pagar Cancelados ou Prescritos em 2014")) {
                    Exercicio exercicio = relatorioAnexo12RREOCalculator.getExercicioFacade().getExercicioPorAno(sistemaControlador.getExercicioCorrente().getAno() - 1);
                    rel.setValorColuna1(calculaRestosAPagarCanceladosExAnterior(itemDemonstrativo, exercicio, relatoriosItemDemonst));
                    restosColuna1 = restosColuna1.add(rel.getValorColuna1());
                    rel.setValorColuna2(rel.getValorColuna1());
                    restosColuna2 = restosColuna2.add(rel.getValorColuna2());
                    rel.setValorColuna3(BigDecimal.ZERO);
                } else if (rel.getDescricao().equals("Total (VIII)")) {
                    rel.setValorColuna1(restosColuna1);
                    rel.setValorColuna2(restosColuna2);
                    rel.setValorColuna3(rel.getValorColuna1().subtract(rel.getValorColuna2()));
                } else if (rel.getDescricao().equals("Restos a Pagar Cancelados ou Prescritos em 2015")) {
                    rel.setValorColuna1(calculaRestosAPagarCanceladosExAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    restosColuna1 = restosColuna1.add(rel.getValorColuna1());
                    rel.setValorColuna2(rel.getValorColuna1());
                    restosColuna2 = restosColuna2.add(rel.getValorColuna2());
                    rel.setValorColuna3(BigDecimal.ZERO);
                } else {
                    rel.setValorColuna1(calculaRestosAPagarCanceladosExAnterior(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    restosColuna1 = restosColuna1.add(rel.getValorColuna1());
                    rel.setValorColuna2(BigDecimal.ZERO);
                    rel.setValorColuna3(rel.getValorColuna1());
                }

                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo10() {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 10);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                rel.setValorColuna1(BigDecimal.ZERO);
                rel.setValorColuna2(BigDecimal.ZERO);
                rel.setValorColuna3(BigDecimal.ZERO);
                toReturn.add(rel);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo12Item> preparaDadosGrupo11() {
        BigDecimal totalColuna3 = BigDecimal.ZERO;
        BigDecimal totalColuna5 = BigDecimal.ZERO;
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 11);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo12Item rel = new RREOAnexo12Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                rel.setDescricao(descricao);
                rel.setValorColuna1(calculaDotacaoInicial(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                rel.setValorColuna2(rel.getValorColuna1().add(calculaDotacaoAtualizada(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst)));
                rel.setValorColuna3(calculaDespesaEmpenhadaAteBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                if (rel.getValorColuna2().compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna4(rel.getValorColuna3().divide(rel.getValorColuna2(), 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    rel.setValorColuna4(BigDecimal.ZERO);
                }
                rel.setValorColuna5(calculaDespesaLiquidadaAteBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                if (rel.getValorColuna2().compareTo(BigDecimal.ZERO) > 0) {
                    rel.setValorColuna6(rel.getValorColuna5().divide(rel.getValorColuna2(), 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    rel.setValorColuna6(BigDecimal.ZERO);
                }
                if (dataFinal.equals("12")) {
                    rel.setValorColuna7(recuperarRestoAPagarNaoProcessados(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                } else {
                    rel.setValorColuna7(BigDecimal.ZERO);
                }
                if (descricao.equals("TOTAL")) {
                    totalColuna3 = rel.getValorColuna3();
                    totalColuna5 = rel.getValorColuna5();
                }
                toReturn.add(rel);
            }
        }
        toReturn = atualizaPercentuaisTotal(toReturn, totalColuna3, totalColuna5);
        return toReturn;
    }

    private List<RREOAnexo12Item> atualizaPercentuaisTotal(List<RREOAnexo12Item> itens, BigDecimal totalColuna3, BigDecimal totalColuna5) {
        for (RREOAnexo12Item item : itens) {
            if (totalColuna3.compareTo(BigDecimal.ZERO) != 0) {
                item.setValorColuna4(item.getValorColuna3().divide(totalColuna3, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
            }
            if (totalColuna5.compareTo(BigDecimal.ZERO) != 0) {
                item.setValorColuna6(item.getValorColuna5().divide(totalColuna5, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
            }
        }
        return itens;
    }

    private List<RREOAnexo12Item> preparaDados() {
        List<RREOAnexo12Item> toReturn = new ArrayList<>();
        RREOAnexo12Item rel = new RREOAnexo12Item();
        rel.setDescricao("");
        toReturn.add(rel);
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 12", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            HashMap parameters = montarParametrosRelatorio();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDados());
            gerarRelatorioComDadosEmCollection(getNomeJasper(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 12", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            HashMap parameters = montarParametrosRelatorio();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDados());
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 12 - Demonstrativo da Receita de Impostos Líquida e das Despesas Próprias com Ações e Serviços Públicos de Saúde");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(dataFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(getNomeJasper(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeJasper() {
        return "RelatorioRREOAnexo122018.jasper";
    }

    private HashMap montarParametrosRelatorio() throws ParseException {
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("ANO", sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("DATAINICIAL", retornaDescricaoMes(dataInicial));
        parameters.put("DATAFINAL", retornaDescricaoMes(dataFinal));
        parameters.put("GRUPO1", preparaDadosGrupo1());
        parameters.put("GRUPO2", preparaDadosGrupo2());
        parameters.put("GRUPO3", preparaDadosGrupo3());
        parameters.put("GRUPO4", preparaDadosGrupo4());
        parameters.put("GRUPO5", preparaDadosGrupo5());
        parameters.put("GRUPO6", preparaDadosGrupo6());
        parameters.put("GRUPO7", preparaDadosGrupo7());
        parameters.put("GRUPO8", preparaDadosGrupo8());
        parameters.put("GRUPO9", preparaDadosGrupo9());
        parameters.put("GRUPO10", preparaDadosGrupo10());
        parameters.put("GRUPO11", preparaDadosGrupo11());
        parameters.put("BIMESTRE_FINAL", dataFinal.equals("12"));
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno());
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getUsername());
        }
        return parameters;
    }

    @URLAction(mappingId = "relatorio-rreo-anexo12-2018", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.dataFinal = null;
        this.dataInicial = null;
    }

    private String retornaDescricaoMes(String mes) {
        String toReturn = "";
        switch (mes) {
            case "01":
                toReturn = "JANEIRO";
                break;
            case "02":
                toReturn = "FEVEREIRO";
                break;
            case "03":
                toReturn = "MARÇO";
                break;
            case "04":
                toReturn = "ABRIL";
                break;
            case "05":
                toReturn = "MAIO";
                break;
            case "06":
                toReturn = "JUNHO";
                break;
            case "07":
                toReturn = "JULHO";
                break;
            case "08":
                toReturn = "AGOSTO";
                break;
            case "09":
                toReturn = "SETEMBRO";
                break;
            case "10":
                toReturn = "OUTUBRO";
                break;
            case "11":
                toReturn = "NOVEMBRO";
                break;
            case "12":
                toReturn = "DEZEMBRO";
                break;
        }
        return toReturn;
    }

    private BigDecimal calculaPrevisaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularPrevisaoInicialAlterado(itemDemonstrativo, exercicio, relatoriosItemDemonst);
    }

    private BigDecimal calculaPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularPrevisaoAtualizadaAlterado(itemDemonstrativo, exercicio, "01/01/" + exercicio.getAno(), Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + exercicio.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal calculaReceitaRealizada(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) throws ParseException {
        return relatorioAnexo12RREOCalculator.calcularReceitaRealizadaAteOBimestreAlterado(itemDemonstrativo, exercicio, Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + exercicio.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal calculaDotacaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularDotacaoInicialAlterado(itemDemonstrativo, exercicio, relatoriosItemDemonst);
    }

    private BigDecimal calculaDotacaoAtualizada(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularCreditosAdicionaisAlterado(itemDemonstrativo, exercicio, "01/01/" + exercicio.getAno(), Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + exercicio.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal calculaDespesaLiquidadaAteBimestre(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularDespesasLiquidadasAteOBimestreAlterado(itDemonstrativo, ex, Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + ex.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal calculaDespesaEmpenhadaAteBimestre(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularDespesasEmpenhadasAteOBimestreAlterado(itDemonstrativo, ex, Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + ex.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarNaoProcessadosExAnterior(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularRestosAPagarNaoProcessadosDespesaPropriaImpl(itDemonstrativo, "01/01/" + ex.getAno(), Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + ex.getAno(), ex, relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarNaoProcessadosExAnteriorAoAnterior(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio exercicio = relatorioAnexo12RREOCalculator.getExercicioFacade().getExercicioPorAno(ex.getAno() - 1);
        return relatorioAnexo12RREOCalculator.calcularRestosAPagarNaoProcessadosDespesaPropriaImpl(itDemonstrativo, "01/01/" + exercicio.getAno(), Util.getDiasMes(new Integer(dataFinal), ex.getAno()) + "/" + dataFinal + "/" + ex.getAno(), exercicio, relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarNaoProcessadosPagosExAnterior(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularRestosAPagarNaoProcessadosPagosDespesaPropriaImpl(itDemonstrativo, "01/01/" + ex.getAno(), Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + ex.getAno(), ex, relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarNaoProcessadosPagosExAnteriorAoAnterior(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio exercicio = relatorioAnexo12RREOCalculator.getExercicioFacade().getExercicioPorAno(ex.getAno() - 1);
        return relatorioAnexo12RREOCalculator.calcularRestosAPagarNaoProcessadosPagosDespesaPropriaImpl(itDemonstrativo, "01/01/" + exercicio.getAno(), Util.getDiasMes(new Integer(dataFinal), ex.getAno()) + "/" + dataFinal + "/" + ex.getAno(), exercicio, relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarNaoProcessadosAPagarExAnterior(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularRestosAPagarNaoProcessadosAPagarDespesaPropriaImpl(itDemonstrativo, "01/01/" + ex.getAno(), Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + ex.getAno(), ex, relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarNaoProcessadosAPagarExAnteriorAoAnterior(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio exercicio = relatorioAnexo12RREOCalculator.getExercicioFacade().getExercicioPorAno(ex.getAno() - 1);
        return relatorioAnexo12RREOCalculator.calcularRestosAPagarNaoProcessadosAPagarDespesaPropriaImpl(itDemonstrativo, "01/01/" + exercicio.getAno(), Util.getDiasMes(new Integer(dataFinal), ex.getAno()) + "/" + dataFinal + "/" + ex.getAno(), exercicio, relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarCancelados(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularRestosAPagarCanceladosDespesaPropriaImpl(itDemonstrativo, "01/01/" + ex.getAno(), Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + ex.getAno(), ex, relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarCanceladosExAnterior(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio exercicio = relatorioAnexo12RREOCalculator.getExercicioFacade().getExercicioPorAno(ex.getAno() - 1);
        return relatorioAnexo12RREOCalculator.calcularRestosAPagarCanceladosDespesaPropriaImpl(itDemonstrativo, "01/01/" + exercicio.getAno(), Util.getDiasMes(new Integer(dataFinal), ex.getAno()) + "/" + dataFinal + "/" + ex.getAno(), exercicio, relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarNaoProcessadosParcelaExAnterior(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularRestosAPagarNaoProcessadosParcelaImpl(itDemonstrativo, "01/01/" + ex.getAno(), Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + ex.getAno(), ex, relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarNaoProcessadosParcelaExAnteriorAoAnterior(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio exercicio = relatorioAnexo12RREOCalculator.getExercicioFacade().getExercicioPorAno(ex.getAno() - 1);
        return relatorioAnexo12RREOCalculator.calcularRestosAPagarNaoProcessadosParcelaImpl(itDemonstrativo, "01/01/" + exercicio.getAno(), Util.getDiasMes(new Integer(dataFinal), ex.getAno()) + "/" + dataFinal + "/" + ex.getAno(), exercicio, relatoriosItemDemonst);
    }

    private BigDecimal recuperarRestoAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioAnexo12RREOCalculator.calcularRestoAPagarNaoProcessados(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(dataFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + dataFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
        Integer mes = Integer.parseInt(this.dataFinal);
        if (mes == 12) {
            this.dataInicial = "11";
        } else {
            this.dataInicial = "0" + (mes - 1);
        }
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
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

    public BigDecimal getTotalGrupo1Coluna3() {
        return totalGrupo1Coluna3;
    }

    public void setTotalGrupo1Coluna3(BigDecimal totalGrupo1Coluna3) {
        this.totalGrupo1Coluna3 = totalGrupo1Coluna3;
    }

    public BigDecimal getTotalGrupo3Coluna3() {
        return totalGrupo3Coluna3;
    }

    public void setTotalGrupo3Coluna3(BigDecimal totalGrupo3Coluna3) {
        this.totalGrupo3Coluna3 = totalGrupo3Coluna3;
    }

    public BigDecimal getTotalGrupo5Coluna5() {
        return totalGrupo5Coluna5;
    }

    public void setTotalGrupo5Coluna5(BigDecimal totalGrupo5Coluna5) {
        this.totalGrupo5Coluna5 = totalGrupo5Coluna5;
    }

    public void setItemDemonstrativoFacade(ItemDemonstrativoFacade itemDemonstrativoFacade) {
        this.itemDemonstrativoFacade = itemDemonstrativoFacade;
    }

    public RelatorioRREOAnexo12Calculator2018 getRelatorioAnexo12RREOCalculator() {
        return relatorioAnexo12RREOCalculator;
    }

    public void setRelatorioAnexo12RREOCalculator(RelatorioRREOAnexo12Calculator2018 relatorioAnexo12RREOCalculator) {
        this.relatorioAnexo12RREOCalculator = relatorioAnexo12RREOCalculator;
    }
}
