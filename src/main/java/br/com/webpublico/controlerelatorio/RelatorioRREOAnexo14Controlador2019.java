package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo14Facade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mateus on 12/09/2014.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo14-2019", pattern = "/relatorio/rreo/anexo14/2019/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo14-2019.xhtml")
})
@Deprecated
public class RelatorioRREOAnexo14Controlador2019 extends AbstractReport implements Serializable {

    @EJB
    private RelatorioRREOAnexo14Facade relatorioRREOAnexo14Facade;
    private List<ItemDemonstrativoComponente> itens;
    private BimestreAnexosLei bimestre;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;

    public RelatorioRREOAnexo14Controlador2019() {
        itens = Lists.newArrayList();
    }

    public List<SelectItem> getBimestres() {
        return Util.getListSelectItemSemCampoVazio(BimestreAnexosLei.values(), false);
    }

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getSistemaFacade().getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + getSistemaFacade().getExercicioCorrente().getAno());
        atualizarRelatorioNoFiltro("Anexo 14");
        itemDemonstrativoFiltros.setExercicio(getSistemaFacade().getExercicioCorrente());
    }

    private void atualizarRelatorioNoFiltro(String relatorio) {
        itemDemonstrativoFiltros.setRelatorio(relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao(relatorio, getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO));
    }

    private List<RREOAnexo14Item> prepararDados() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        toReturn.add(new RREOAnexo14Item());
        return toReturn;
    }

    private void atribuirNullNosValores(RREOAnexo14Item it) {
        it.setValorColuna1(null);
        it.setValorColuna2(null);
        it.setValorColuna3(null);
        it.setValorColuna4(null);
    }

    private BigDecimal getSubTotalRefinanciamentoReceita() {
        RelatoriosItemDemonst relatorio = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        atualizarRelatorioNoFiltro("Anexo 1");
        ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Subtotal com Refinanciamento", getExercicioCorrente(), relatorio);
        return calcularReceitaRealizadaAteOBimestre(itemDemonstrativo);
    }

    private BigDecimal getSuperavitFinanceiro() {
        RelatoriosItemDemonst relatorio = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Superávit Financeiro Utilizado para Créditos Adicionais", getExercicioCorrente(), relatorio);
        atualizarRelatorioNoFiltro("Anexo 1");
        return calcularValorContabilAteBimestreAtual(itemDemonstrativo);
    }

    private BigDecimal getSubTotalRefinanciamentoDespesa() {
        RelatoriosItemDemonst relatorio = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        atualizarRelatorioNoFiltro("Anexo 1");
        ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Subtotal c/ Refinanciamento (Despesa)", getExercicioCorrente(), relatorio);
        BigDecimal valorResto = BigDecimal.ZERO;
        if (bimestre.isUltimoBimestre()) {
            valorResto = calcularRestoAPagarNaoProcessados(itemDemonstrativo);
        }
        return recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo).add(valorResto);
    }

    private BigDecimal getValorRefinancimentoReceita() {
        BigDecimal valorReceita = getSubTotalRefinanciamentoReceita();
        BigDecimal valorDespesa = getSubTotalRefinanciamentoDespesa();
        if (valorDespesa.subtract(valorReceita).compareTo(BigDecimal.ZERO) > 0) {
            return valorDespesa.subtract(valorReceita);
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getValorRefinancimentoDespesa() {
        BigDecimal valorReceita = getSubTotalRefinanciamentoReceita();
        BigDecimal valorDespesa = getSubTotalRefinanciamentoDespesa();
        if (valorReceita.subtract(valorDespesa).compareTo(BigDecimal.ZERO) > 0) {
            return valorReceita.subtract(valorDespesa);
        } else {
            return BigDecimal.ZERO;
        }
    }


    private List<RREOAnexo14Item> buscarDadosAnexo1() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        RelatoriosItemDemonst relatorio = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        atualizarRelatorioNoFiltro("Anexo 1");
        ItemDemonstrativo itemReceita = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Subtotal com Refinanciamento", getExercicioCorrente(), relatorio);
        ItemDemonstrativo itemDespesa = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Subtotal c/ Refinanciamento (Despesa)", getExercicioCorrente(), relatorio);
        ItemDemonstrativo itemReserva = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Reserva do RPPS", getExercicioCorrente(), relatorio);
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                switch (item.getOrdem()) {
                    case 1:
                    case 7:
                        atribuirNullNosValores(it);
                        break;
                    case 2:
                        it.setValorColuna2(calcularPrevisaoInicial(itemReceita));
                        break;
                    case 3:
                        it.setValorColuna2(calcularPrevisaoInicial(itemReceita).add(calcularPrevisaoAtualizada(itemReceita)));
                        break;
                    case 4:
                        it.setValorColuna1(calcularReceitaRealizadaNoBimestre(itemReceita));
                        it.setValorColuna2(calcularReceitaRealizadaAteOBimestre(itemReceita));
                        break;
                    case 5:
                        it.setValorColuna2(getValorRefinancimentoReceita());
                        break;
                    case 6:
                        it.setValorColuna2(getSuperavitFinanceiro());
                        break;
                    case 8:
                        it.setValorColuna2(calcularDotacaoInicial(itemDespesa)
                            .add(calcularDotacaoInicial(itemReserva)));
                        break;
                    case 9:
                        it.setValorColuna2(calcularCreditosAdicionais(itemDespesa));
                        break;
                    case 10:
                        it.setValorColuna2(calcularDotacaoInicial(itemDespesa)
                            .add(calcularDotacaoInicial(itemReserva))
                            .add(calcularCreditosAdicionais(itemDespesa)));
                        break;
                    case 11:
                        it.setValorColuna2(calcularDespesasEmpenhadasAteOBimestre(itemDespesa));
                        break;
                    case 12:
                        it.setValorColuna2(recuperarDespesasLiquidadasAteOBimestre(itemDespesa));
                        break;
                    case 13:
                        it.setValorColuna2(recuperarDespesasPagasAteOBimestre(itemDespesa));
                        break;
                    case 14:
                        it.setValorColuna2(getValorRefinancimentoDespesa());
                        break;
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo2() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        atualizarRelatorioNoFiltro("Anexo 2");
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                if (item.getOrdem() == 1) {
                    it = buscarValoresAnexo2Empenhadas(it);
                } else if (item.getOrdem() == 2) {
                    it = buscarValoresAnexo2Liquidadas(it);
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo3() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        atualizarRelatorioNoFiltro("Anexo 3");
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(calcularRcl());
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo4() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        RelatoriosItemDemonst relatorio = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        atualizarRelatorioNoFiltro("Anexo 4");
        ItemDemonstrativo itemReceitaPrevidenciario = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("TOTAL DAS RECEITAS PREVIDENCIÁRIAS RPPS - (IV) = (I + III - II)", getExercicioCorrente(), relatorio);
        ItemDemonstrativo itemDespesaPrevidenciario = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("TOTAL DAS DESPESAS PREVIDENCIÁRIAS RPPS (VII) = (V + VI)", getExercicioCorrente(), relatorio);
        ItemDemonstrativo itemReceitaFinanceiro = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("TOTAL DAS RECEITAS PREVIDENCIÁRIAS RPPS - (XI) = (IX + X)", getExercicioCorrente(), relatorio);
        ItemDemonstrativo itemDespesaFinanceiro = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("TOTAL DAS DESPESAS PREVIDENCIÁRIAS RPPS (XIV) = (XII + XIII)", getExercicioCorrente(), relatorio);
        BigDecimal valorReceitaPrevidenciarioAteBimestre = BigDecimal.ZERO;
        BigDecimal valorDespesaPrevidenciarioAteBimestre = BigDecimal.ZERO;
        BigDecimal valorReceitaFinanceiroAteBimestre = BigDecimal.ZERO;
        BigDecimal valorDespesaFinanceiroAteBimestre = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                switch (item.getOrdem()) {
                    case 1:
                    case 5:
                        atribuirNullNosValores(it);
                        break;
                    case 2:
                        it.setValorColuna2(calcularReceitaRealizadaAteOBimestre(itemReceitaPrevidenciario));
                        valorReceitaPrevidenciarioAteBimestre = it.getValorColuna2();
                        break;
                    case 3:
                        it.setValorColuna2(recuperarDespesasLiquidadasAteOBimestre(itemDespesaPrevidenciario));
                        valorDespesaPrevidenciarioAteBimestre = it.getValorColuna2();
                        break;
                    case 4:
                        it.setValorColuna2(valorReceitaPrevidenciarioAteBimestre.subtract(valorDespesaPrevidenciarioAteBimestre));
                        break;
                    case 6:
                        it.setValorColuna2(calcularReceitaRealizadaAteOBimestre(itemReceitaFinanceiro));
                        valorReceitaFinanceiroAteBimestre = it.getValorColuna2();
                        break;
                    case 7:
                        it.setValorColuna2(recuperarDespesasLiquidadasAteOBimestre(itemDespesaFinanceiro));
                        valorDespesaFinanceiroAteBimestre = it.getValorColuna2();
                        break;
                    case 8:
                        it.setValorColuna2(valorReceitaFinanceiroAteBimestre.subtract(valorDespesaFinanceiroAteBimestre));
                        break;
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private HashMap<String, BigDecimal> buscarValoresAnexo5e6() {
        RelatorioRREOAnexo06NovoControlador relatorioRREOAnexo06NovoControlador = (RelatorioRREOAnexo06NovoControlador) Util.getControladorPeloNome("relatorioRREOAnexo06NovoControlador");
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRREOAnexo06NovoControlador.recuperarRelatorioEConfiguracoes();
        relatorioRREOAnexo06NovoControlador.popularComponentes(itensDemonstrativos);
        relatorioRREOAnexo06NovoControlador.limparCampos();
        relatorioRREOAnexo06NovoControlador.setMes(bimestre.getMesFinal());
        relatorioRREOAnexo06NovoControlador.instanciarItemDemonstrativoFiltros();
        relatorioRREOAnexo06NovoControlador.buscarDadosGrupo1();
        relatorioRREOAnexo06NovoControlador.buscarDadosGrupo2();
        relatorioRREOAnexo06NovoControlador.buscarDadosGrupo5();
        HashMap mapAnexo5e6 = new HashMap();
        mapAnexo5e6.put("ANEXO5", relatorioRREOAnexo06NovoControlador.buscarDadosGrupo6().get(0).getValorColuna1());
        mapAnexo5e6.put("ANEXO6", relatorioRREOAnexo06NovoControlador.buscarDadosGrupo3().get(0).getValorColuna1());
        return mapAnexo5e6;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo5e6() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        HashMap<String, BigDecimal> valoresAnexo5e6 = buscarValoresAnexo5e6();
        ReferenciaAnual referenciaAnual = relatorioRREOAnexo14Facade.getReferenciaAnualFacade().recuperaReferenciaPorExercicio(getExercicioCorrente());
        for (ItemDemonstrativoComponente idc : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(idc.getItemDemonstrativo(), 5);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(idc.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                if (referenciaAnual != null) {
                    if (idc.getOrdem() == 1) {
                        it.setValorColuna1(referenciaAnual.getMetaResultadoPrimario());
                        it.setValorColuna2(valoresAnexo5e6.get("ANEXO6"));
                    } else if (idc.getOrdem() == 2) {
                        it.setValorColuna1(referenciaAnual.getMetaResultadoNominal());
                        it.setValorColuna2(valoresAnexo5e6.get("ANEXO5"));
                    }
                }
                if (it.getValorColuna1() != null && it.getValorColuna1().compareTo(BigDecimal.ZERO) != 0) {
                    it.setValorColuna3(it.getValorColuna2().divide(it.getValorColuna1(), BigDecimal.ROUND_HALF_UP, 6).multiply(BigDecimal.valueOf(100)));
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo7() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        BigDecimal valorColuna1 = BigDecimal.ZERO;
        BigDecimal valorColuna2 = BigDecimal.ZERO;
        BigDecimal valorColuna3 = BigDecimal.ZERO;
        BigDecimal valorColuna4 = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 6);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                switch (item.getOrdem()) {
                    case 1:
                        it.setValorColuna1(calcularRestosAPagarProcessadosEmDezembroExAnterior(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna2(calcularRestosAPagarProcessadosCancelados(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna3(calcularRestosAPagarProcessadosPagos(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna4(it.getValorColuna1().subtract(it.getValorColuna3().add(it.getValorColuna2())));
                        break;
                    case 6:
                        it.setValorColuna1(calcularRestosAPagarNaoProcessadosEmDezembroExAnterior(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna2(calcularRestosAPagarNaoProcessadosCancelados(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna3(calcularRestosAPagarNaoProcessadosPagos(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna4(it.getValorColuna1().subtract(it.getValorColuna3().add(it.getValorColuna2())));
                        break;
                    case 2:
                        it.setValorColuna1(calcularRestosAPagarProcessadosEmDezembroExAnterior(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.EXECUTIVO.name()));
                        it.setValorColuna2(calcularRestosAPagarProcessadosCancelados(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.EXECUTIVO.name()));
                        it.setValorColuna3(calcularRestosAPagarProcessadosPagos(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.EXECUTIVO.name()));
                        it.setValorColuna4(it.getValorColuna1().subtract(it.getValorColuna3().add(it.getValorColuna2())));
                        valorColuna1 = valorColuna1.add(it.getValorColuna1());
                        valorColuna2 = valorColuna2.add(it.getValorColuna2());
                        valorColuna3 = valorColuna3.add(it.getValorColuna3());
                        valorColuna4 = valorColuna4.add(it.getValorColuna4());
                        break;
                    case 3:
                        it.setValorColuna1(calcularRestosAPagarProcessadosEmDezembroExAnterior(getExercicioCorrente(), EsferaDoPoder.LEGISLATIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna2(calcularRestosAPagarProcessadosCancelados(getExercicioCorrente(), EsferaDoPoder.LEGISLATIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna3(calcularRestosAPagarProcessadosPagos(getExercicioCorrente(), EsferaDoPoder.LEGISLATIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna4(it.getValorColuna1().subtract(it.getValorColuna3().add(it.getValorColuna2())));
                        valorColuna1 = valorColuna1.add(it.getValorColuna1());
                        valorColuna2 = valorColuna2.add(it.getValorColuna2());
                        valorColuna3 = valorColuna3.add(it.getValorColuna3());
                        valorColuna4 = valorColuna4.add(it.getValorColuna4());
                        break;
                    case 7:
                        it.setValorColuna1(calcularRestosAPagarNaoProcessadosEmDezembroExAnterior(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.EXECUTIVO.name()));
                        it.setValorColuna2(calcularRestosAPagarNaoProcessadosCancelados(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.EXECUTIVO.name()));
                        it.setValorColuna3(calcularRestosAPagarNaoProcessadosPagos(getExercicioCorrente(), EsferaDoPoder.EXECUTIVO.name(), EsferaDoPoder.EXECUTIVO.name()));
                        it.setValorColuna4(it.getValorColuna1().subtract(it.getValorColuna3().add(it.getValorColuna2())));
                        valorColuna1 = valorColuna1.add(it.getValorColuna1());
                        valorColuna2 = valorColuna2.add(it.getValorColuna2());
                        valorColuna3 = valorColuna3.add(it.getValorColuna3());
                        valorColuna4 = valorColuna4.add(it.getValorColuna4());
                        break;
                    case 8:
                        it.setValorColuna1(calcularRestosAPagarNaoProcessadosEmDezembroExAnterior(getExercicioCorrente(), EsferaDoPoder.LEGISLATIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna2(calcularRestosAPagarNaoProcessadosCancelados(getExercicioCorrente(), EsferaDoPoder.LEGISLATIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna3(calcularRestosAPagarNaoProcessadosPagos(getExercicioCorrente(), EsferaDoPoder.LEGISLATIVO.name(), EsferaDoPoder.LEGISLATIVO.name()));
                        it.setValorColuna4(it.getValorColuna1().subtract(it.getValorColuna3().add(it.getValorColuna2())));
                        valorColuna1 = valorColuna1.add(it.getValorColuna1());
                        valorColuna2 = valorColuna2.add(it.getValorColuna2());
                        valorColuna3 = valorColuna3.add(it.getValorColuna3());
                        valorColuna4 = valorColuna4.add(it.getValorColuna4());
                        break;
                    case 11:
                        it.setValorColuna1(valorColuna1);
                        it.setValorColuna2(valorColuna2);
                        it.setValorColuna3(valorColuna3);
                        it.setValorColuna4(valorColuna4);
                        break;
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private RREOAnexo14Item buscarValorAnexo08Linha1(RREOAnexo14Item rreoAnexo14Item) {
        RelatorioRREOAnexo08Controlador2020 relatorioRREOAnexo08Controlador = (RelatorioRREOAnexo08Controlador2020) Util.getControladorPeloNome("relatorioRREOAnexo08Controlador2020");
        relatorioRREOAnexo08Controlador.setMesFinal(bimestre.getMesFinal().getNumeroMesString());
        relatorioRREOAnexo08Controlador.setMesInicial(bimestre.getMesInicial().getNumeroMesString());
        for (RREOAnexo08Item10 item : relatorioRREOAnexo08Controlador.buscaRegistros()) {
            if (item.getDescricao() != null && item.getDescricao().startsWith("36")) {
                rreoAnexo14Item.setValorColuna1(item.getValor());
            }
            if (item.getDescricao() != null && item.getDescricao().startsWith("37")) {
                rreoAnexo14Item.setValorColuna3(item.getValor());
            }
        }
        return rreoAnexo14Item;
    }

    private BigDecimal buscarValorAnexo08Linha2() {
        RelatorioRREOAnexo08Controlador2020 relatorioRREOAnexo08Controlador = (RelatorioRREOAnexo08Controlador2020) Util.getControladorPeloNome("relatorioRREOAnexo08Controlador2020");
        relatorioRREOAnexo08Controlador.setMesFinal(bimestre.getMesFinal().getNumeroMesString());
        relatorioRREOAnexo08Controlador.setMesInicial(bimestre.getMesInicial().getNumeroMesString());
        BigDecimal toReturn = BigDecimal.ZERO;
        for (RREOAnexo08Item04 item : relatorioRREOAnexo08Controlador.buscarRegistrosIndice13()) {
            if (item.getDescricao().trim().startsWith("13.1-")) {
                toReturn = item.getDespesaLiquidadaAteBimestre();
                break;
            }
        }
        List<RREOAnexo08Item05> itensIndice17 = relatorioRREOAnexo08Controlador.buscarRegistrosIndice17();
        for (RREOAnexo08Item05 item : itensIndice17) {
            if (item.getOrdem() == 5) {
                return toReturn.subtract(item.getValor());
            }
        }
        return toReturn;
    }

    private BigDecimal buscarValorAnexo08Linha3() {
        RelatorioRREOAnexo08Controlador2020 relatorioRREOAnexo08Controlador = (RelatorioRREOAnexo08Controlador2020) Util.getControladorPeloNome("relatorioRREOAnexo08Controlador2020");
        relatorioRREOAnexo08Controlador.setMesFinal(bimestre.getMesFinal().getNumeroMesString());
        relatorioRREOAnexo08Controlador.setMesInicial(bimestre.getMesInicial().getNumeroMesString());
        BigDecimal toReturn = BigDecimal.ZERO;
        for (RREOAnexo08Item04 item : relatorioRREOAnexo08Controlador.buscarRegistrosIndice13()) {
            if (item.getDescricao().trim().startsWith("13.2-")) {
                toReturn = item.getDespesaLiquidadaAteBimestre();
                break;
            }
        }
        List<RREOAnexo08Item05> itensIndice17 = relatorioRREOAnexo08Controlador.buscarRegistrosIndice17();
        for (RREOAnexo08Item05 item : itensIndice17) {
            if (item.getOrdem() == 5) {
                return toReturn.subtract(item.getValor());
            }
        }
        return toReturn;
    }

    private HashMap<String, BigDecimal> buscarValorAnexo08Percentuais() {
        RelatorioRREOAnexo08Controlador2020 relatorioRREOAnexo08Controlador = (RelatorioRREOAnexo08Controlador2020) Util.getControladorPeloNome("relatorioRREOAnexo08Controlador2020");
        relatorioRREOAnexo08Controlador.limparCampos();
        relatorioRREOAnexo08Controlador.setMesFinal(bimestre.getMesFinal().getNumeroMesString());
        relatorioRREOAnexo08Controlador.setMesInicial(bimestre.getMesInicial().getNumeroMesString());
        return relatorioRREOAnexo08Controlador.buscarRegistrosIndice19();
    }

    private List<RREOAnexo14Item> buscarDadosAnexo8() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        HashMap<String, BigDecimal> percentuais = buscarValorAnexo08Percentuais();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 7);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                switch (item.getOrdem()) {
                    case 1:
                        it = buscarValorAnexo08Linha1(it);
                        it.setValorColuna2(BigDecimal.valueOf(25));
                        break;
                    case 3:
                        it.setValorColuna1(buscarValorAnexo08Linha2().add(buscarValorAnexo08Linha3()));
                        it.setValorColuna2(BigDecimal.valueOf(60));
                        it.setValorColuna3(percentuais.get("INFANTIL"));
                        break;
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo9() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        RelatoriosItemDemonst relatorio = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 9", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        atualizarRelatorioNoFiltro("Anexo 9");
        ItemDemonstrativo itemReceita = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITAS DE OPERAÇÕES DE CRÉDITO (I)", getExercicioCorrente(), relatorio);
        ItemDemonstrativo itemDespesa = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("DESPESAS DE CAPITAL LÍQUIDA (II)", getExercicioCorrente(), relatorio);
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 8);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                if (item.getOrdem() == 1) {
                    it.setValorColuna1(calcularReceitaRealizadaAteOBimestre(itemReceita));
                    it.setValorColuna2(calcularPrevisaoInicial(itemReceita).add(calcularPrevisaoAtualizada(itemReceita)).subtract(it.getValorColuna1()));
                } else if (item.getOrdem() == 2) {
                    it.setValorColuna1(recuperarDespesasLiquidadasAteOBimestre(itemDespesa).add(calcularRestoAPagarNaoProcessados(itemDespesa)));
                    it.setValorColuna2(calcularDotacaoInicial(itemDespesa).add(calcularCreditosAdicionais(itemDespesa)).subtract(it.getValorColuna1()));
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo10() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 9);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                if (item.getOrdem() == 1 || item.getOrdem() == 5) {
                    atribuirNullNosValores(it);
                } else {
                    if (bimestre.isUltimoBimestre()) {
                        it = relatorioRREOAnexo14Facade.buscaValorProjecao(item.getOrdem(), it,
                            relatorioRREOAnexo14Facade.getProjecaoAtuarialFacade().retornaMenorData(getExercicioCorrente()),
                            relatorioRREOAnexo14Facade.getProjecaoAtuarialFacade().retornaMaiorData(getExercicioCorrente()),
                            getExercicioCorrente());
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo11() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 10);
            RelatoriosItemDemonst relatorio = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 11", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            atualizarRelatorioNoFiltro("Anexo 11");
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                if (item.getOrdem() == 1) {
                    ItemDemonstrativo itemAnexo11 = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITAS DE CAPITAL - ALIENAÇÃO DE ATIVOS (I)", getExercicioCorrente(), relatorio);
                    it.setValorColuna1(calcularReceitaRealizadaAteOBimestre(itemAnexo11));
                    it.setValorColuna2(calcularPrevisaoInicial(itemAnexo11).add(calcularPrevisaoAtualizada(itemAnexo11)).subtract(it.getValorColuna1()));
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private BigDecimal buscarValorColuna1Anexo12() {
        RelatoriosItemDemonst relatorio = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 12", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        atualizarRelatorioNoFiltro("Anexo 12");
        ItemDemonstrativo item = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("(=) VALOR APLICADO EM ASPS (XVI) = (XII - XIII - XIV - XV)", getExercicioCorrente(), relatorio);
        BigDecimal toReturn = bimestre.isUltimoBimestre() ? calcularDespesasEmpenhadasAteOBimestre(item) : recuperarDespesasLiquidadasAteOBimestre(item);
        BigDecimal valorResto = bimestre.isUltimoBimestre() ? calcularRestoAPagarNaoProcessados(item).subtract(BigDecimal.valueOf(13312.65)) : BigDecimal.ZERO;
        toReturn = toReturn.add(valorResto);
        return toReturn;
    }

    private BigDecimal buscarValorColuna3Anexo12() {
        RelatorioRREOAnexo12Controlador relatorioRREOAnexo12Controlador = (RelatorioRREOAnexo12Controlador) Util.getControladorPeloNome("relatorioRREOAnexo12Controlador");
        relatorioRREOAnexo12Controlador.setBimestre(bimestre);
        relatorioRREOAnexo12Controlador.instanciarItemDemonstrativoFiltros();
        List<RREOAnexo12Item> itensAnexo12 = relatorioRREOAnexo12Controlador.buscarRegistros();
        BigDecimal toReturn = BigDecimal.ZERO;
        for (RREOAnexo12Item item : itensAnexo12) {
            if (item.getDescricao().trim().startsWith("PERCENTUAL DA RECEITA DE IMPOSTOS E TRANSFERÊNCIAS")) {
                toReturn = bimestre.isUltimoBimestre() ? item.getValorColuna1() : item.getValorColuna2();
                break;
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo12() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 11);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(buscarValorColuna1Anexo12());
                it.setValorColuna2(BigDecimal.valueOf(15));
                it.setValorColuna3(buscarValorColuna3Anexo12());
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo13() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 12);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private RREOAnexo14Item buscarValoresAnexo2Liquidadas(RREOAnexo14Item item) {
        List<Funcao> resultado = relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().buscarFuncoes(getExercicioCorrente(), "  ");
        for (Funcao funcao : relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().buscarFuncoes(getExercicioCorrente(), " NOT ")) {
            resultado = Util.adicionarObjetoEmLista(resultado, funcao);
        }
        BigDecimal totalColuna2 = BigDecimal.ZERO;
        for (Funcao funcao : resultado) {
            totalColuna2 = totalColuna2.add(calcularDespesasLiquidadasAteOBimestreAnexo02(getExercicioCorrente(), funcao.getCodigo(), "  "));
            totalColuna2 = totalColuna2.add(calcularDespesasLiquidadasAteOBimestreAnexo02(getExercicioCorrente(), funcao.getCodigo(), " NOT "));
        }
        item.setValorColuna2(totalColuna2);
        return item;
    }

    private RREOAnexo14Item buscarValoresAnexo2Empenhadas(RREOAnexo14Item item) {
        List<Funcao> resultado = relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().buscarFuncoes(getExercicioCorrente(), "  ");
        for (Funcao funcao : relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().buscarFuncoes(getExercicioCorrente(), " NOT ")) {
            resultado = Util.adicionarObjetoEmLista(resultado, funcao);
        }
        BigDecimal totalColuna2 = BigDecimal.ZERO;
        for (Funcao funcao : resultado) {
            totalColuna2 = totalColuna2.add(calcularDespesasEmpenhadasAteOBimestreAnexo02(getExercicioCorrente(), funcao.getCodigo(), "  "));
            totalColuna2 = totalColuna2.add(calcularDespesasEmpenhadasAteOBimestreAnexo02(getExercicioCorrente(), funcao.getCodigo(), " NOT "));
            if (bimestre.isUltimoBimestre()) {
                totalColuna2 = totalColuna2.add(calcularDespesasEmpenhadasAteOBimestreAnexo02Resto(getExercicioCorrente(), funcao.getCodigo(), "  "));
                totalColuna2 = totalColuna2.add(calcularDespesasEmpenhadasAteOBimestreAnexo02Resto(getExercicioCorrente(), funcao.getCodigo(), " NOT "));
            }
        }
        item.setValorColuna2(totalColuna2);
        return item;
    }

    private BigDecimal calcularRcl() {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = relatorioRREOAnexo14Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            atualizarRelatorioNoFiltro("Anexo 3");
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(getExercicioCorrente().getAno(), bimestre.getMesFinal().getNumeroMesIniciandoEmZero(), new Integer(1));
            String data = DataUtil.getDataFormatada(dataCalendar.getTime(), "MM/yyyy");
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo03Calculator().recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, getExercicioCorrente()));
                data = alterarMes(data);
            }
            return toReturn;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private String alterarMes(String data) {
        Integer mes = Integer.parseInt(data.substring(0, 2));
        Integer ano = Integer.parseInt(data.substring(3, 7));
        if (mes != 1) {
            mes += -1;
        } else {
            mes = 12;
            ano += -1;
        }
        String toReturn;
        if (mes < 10) {
            toReturn = "0" + mes + "/" + ano;
        } else {
            toReturn = mes + "/" + ano;
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            relatorioRREOAnexo14Facade.limparHash();
            relatorioRREOAnexo14Facade.limparHashItensRecuperados();
            instanciarItemDemonstrativoFiltros();
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(prepararDados());
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            instanciarItemDemonstrativoFiltros();
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(prepararDados());
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(getExercicioCorrente());
            anexo.setNome("Anexo 14 - Demonstrativo Simplificado do Relatório Resumido da Execução Orçamentária");
            anexo.setMes(bimestre.getMesFinal());
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioRREOAnexo14.jasper";
    }

    public HashMap montarParametros() {
        HashMap parameters = new HashMap();
        relatorioRREOAnexo14Facade.limparHash();
        relatorioRREOAnexo14Facade.limparHashItensRecuperados();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("ANEXO1", buscarDadosAnexo1());
        parameters.put("ANEXO2", buscarDadosAnexo2());
        parameters.put("ANEXO3", buscarDadosAnexo3());
        parameters.put("ANEXO4", buscarDadosAnexo4());
        parameters.put("ANEXO5E6", buscarDadosAnexo5e6());
        parameters.put("ANEXO7", buscarDadosAnexo7());
        parameters.put("ANEXO8", buscarDadosAnexo8());
        parameters.put("ANEXO9", buscarDadosAnexo9());
        parameters.put("ANEXO10", buscarDadosAnexo10());
        parameters.put("ANEXO11", buscarDadosAnexo11());
        parameters.put("ANEXO12", buscarDadosAnexo12());
        parameters.put("ANEXO13", buscarDadosAnexo13());
        parameters.put("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        parameters.put("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        parameters.put("ANO_EXERCICIO", getExercicioCorrente().getAno().toString());
        parameters.put("USER", getSistemaFacade().getUsuarioCorrente().getNome());
        return parameters;
    }

    @URLAction(mappingId = "relatorio-rreo-anexo14-2019", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
    }

    private BigDecimal calcularPrevisaoInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_INICIAL);
    }

    private BigDecimal calcularPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_ATUALIZADA);
    }

    private BigDecimal calcularValorContabilAteBimestreAtual(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_ATUAL_POR_TIPO_BALANCETE);
    }

    private BigDecimal calcularReceitaRealizadaNoBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_NO_BIMESTRE);
    }

    private BigDecimal calcularReceitaRealizadaAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE);
    }

    private BigDecimal calcularDotacaoInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DOTACAO_INICIAL);
    }

    private BigDecimal calcularCreditosAdicionais(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.CREDITOS_ADICIONAIS);
    }

    private BigDecimal calcularDespesasEmpenhadasAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_EMPENHADAS_ATE_BIMESTRE);
    }

    private BigDecimal recuperarDespesasLiquidadasAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_LIQUIDADAS_ATE_BIMESTRE);
    }

    private BigDecimal recuperarDespesasPagasAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_PAGAS_ATE_BIMESTRE);
    }

    private BigDecimal calcularDespesasEmpenhadasAteOBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String clausula) {
        return relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().calcularDespesasEmpenhadasAteOBimestreAnexo02(exercicioCorrente, codigo, Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicioCorrente.getAno(), clausula);
    }

    private BigDecimal calcularDespesasEmpenhadasAteOBimestreAnexo02Resto(Exercicio exercicioCorrente, String codigo, String clausula) {
        Exercicio ex = relatorioRREOAnexo14Facade.getExercicioFacade().getExercicioPorAno(exercicioCorrente.getAno() - 1);
        return relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().calcularDespesasEmpenhadasAteOBimestreAnexo02Resto(ex, codigo, Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), ex.getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + ex.getAno(), clausula);
    }

    private BigDecimal calcularDespesasLiquidadasAteOBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String clausula) {
        return relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().calcularDespesasLiquidadasAteOBimestreAnexo02(exercicioCorrente, codigo, Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicioCorrente.getAno(), clausula);
    }

    private BigDecimal calcularRestosAPagarProcessadosEmDezembroExAnterior(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarExAnteriores(exercicioCorrente, esferaPoder1, esferaPoder2, "=", "", "PROCESSADOS", " NOT "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarExAnteriores(exercicioCorrente, esferaPoder1, esferaPoder2, "=", "", "PROCESSADOS", " "));
        return valor;
    }

    private BigDecimal calcularRestosAPagarProcessadosCancelados(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosCancelados(exercicioCorrente, esferaPoder1, esferaPoder2, "", "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicioCorrente.getAno(), "PROCESSADOS", " "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosCancelados(exercicioCorrente, esferaPoder1, esferaPoder2, "", "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicioCorrente.getAno(), "PROCESSADOS", " NOT "));
        return valor;
    }

    private BigDecimal calcularRestosAPagarProcessadosPagos(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosPagos(exercicioCorrente, esferaPoder1, esferaPoder2, "=", "", "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicioCorrente.getAno(), "PROCESSADOS", " "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosPagos(exercicioCorrente, esferaPoder1, esferaPoder2, "=", "", "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicioCorrente.getAno(), "PROCESSADOS", " NOT "));
        return valor;
    }

    private BigDecimal calcularRestosAPagarNaoProcessadosEmDezembroExAnterior(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarExAnteriores(exercicioCorrente, esferaPoder1, esferaPoder2, "=", "", "NAO_PROCESSADOS", " NOT "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarExAnteriores(exercicioCorrente, esferaPoder1, esferaPoder2, "=", "", "NAO_PROCESSADOS", " "));
        return valor;
    }

    private BigDecimal calcularRestosAPagarNaoProcessadosCancelados(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosCancelados(exercicioCorrente, esferaPoder1, esferaPoder2, "", "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicioCorrente.getAno(), "NAO_PROCESSADOS", " NOT "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosCancelados(exercicioCorrente, esferaPoder1, esferaPoder2, "", "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicioCorrente.getAno(), "NAO_PROCESSADOS", " "));
        return valor;
    }

    private BigDecimal calcularRestosAPagarNaoProcessadosPagos(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosPagos(exercicioCorrente, esferaPoder1, esferaPoder2, "=", "", "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicioCorrente.getAno(), "NAO_PROCESSADOS", " NOT "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosPagos(exercicioCorrente, esferaPoder1, esferaPoder2, "=", "", "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicioCorrente.getAno(), "NAO_PROCESSADOS", " "));
        return valor;
    }

    private BigDecimal calcularRestoAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo14Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_NAO_PROCESSADOS);
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public BimestreAnexosLei getBimestre() {
        return bimestre;
    }

    public void setBimestre(BimestreAnexosLei bimestre) {
        this.bimestre = bimestre;
    }

    public ItemDemonstrativoFiltros getItemDemonstrativoFiltros() {
        return itemDemonstrativoFiltros;
    }

    public void setItemDemonstrativoFiltros(ItemDemonstrativoFiltros itemDemonstrativoFiltros) {
        this.itemDemonstrativoFiltros = itemDemonstrativoFiltros;
    }
}
