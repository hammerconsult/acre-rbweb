package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo14Facade;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mateus on 12/09/2014.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo14-2018", pattern = "/relatorio/rreo/anexo14/2018/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo14-2018.xhtml")
})
public class RelatorioRREOAnexo14Controlador2018 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private RelatorioRREOAnexo08Controlador2018 relatorioRREOAnexo08Controlador;
    private RelatorioRREOAnexo12Controlador2018 relatorioRREOAnexo12Controlador;
    @EJB
    private RelatorioRREOAnexo14Facade relatorioRREOAnexo14Facade;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    private String mesInicial;
    private String mesFinal;
    private List<ItemDemonstrativoComponente> itens;
    private RelatoriosItemDemonst relatoriosItemDemonst;

    public RelatorioRREOAnexo14Controlador2018() {
        itens = Lists.newArrayList();
    }

    private List<RREOAnexo14Item> preparaDados() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        RREOAnexo14Item it = new RREOAnexo14Item();
        it.setDescricao("");
        toReturn.add(it);
        return toReturn;
    }

    private void atribuirNullNosValores(RREOAnexo14Item it) {
        it.setValorColuna1(null);
        it.setValorColuna2(null);
        it.setValorColuna3(null);
        it.setValorColuna4(null);
    }

    private BigDecimal getSubTotalRefinanciamentoReceita() throws ParseException {
        RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Subtotal com Refinanciamento", sistemaControlador.getExercicioCorrente(), relatorio);
        return recuperarReceitaRealizadaAteOBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst);
    }

    private BigDecimal getSuperavitFinanceiro() {
        RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Superávit Financeiro Utilizado para Créditos Adicionais", sistemaControlador.getExercicioCorrente(), relatorio);
        return recuperaValorAteBimestreAtual(itemDemonstrativo, relatoriosItemDemonst);
    }

    private BigDecimal getSubTotalRefinanciamentoDespesa() {
        RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Subtotal c/ Refinanciamento (Despesa)", sistemaControlador.getExercicioCorrente(), relatorio);
        BigDecimal valorResto = BigDecimal.ZERO;
        if (isDezembro()) {
            valorResto = recuperarRestoAPagarNaoProcessados(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatorio);
        }
        return recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst).add(valorResto);
    }

    private BigDecimal getValorRefinancimentoReceita() throws ParseException {
        BigDecimal valorReceita = getSubTotalRefinanciamentoReceita();
        BigDecimal valorDespesa = getSubTotalRefinanciamentoDespesa();
        if (valorDespesa.subtract(valorReceita).compareTo(BigDecimal.ZERO) > 0) {
            return valorDespesa.subtract(valorReceita);
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getValorRefinancimentoDespesa() throws ParseException {
        BigDecimal valorReceita = getSubTotalRefinanciamentoReceita();
        BigDecimal valorDespesa = getSubTotalRefinanciamentoDespesa();
        if (valorReceita.subtract(valorDespesa).compareTo(BigDecimal.ZERO) > 0) {
            return valorReceita.subtract(valorDespesa);
        } else {
            return BigDecimal.ZERO;
        }
    }


    private List<RREOAnexo14Item> buscarDadosAnexo1() throws ParseException {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        ItemDemonstrativo itemReceita = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Subtotal com Refinanciamento", sistemaControlador.getExercicioCorrente(), relatorio);
        ItemDemonstrativo itemDespesa = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Subtotal c/ Refinanciamento (Despesa)", sistemaControlador.getExercicioCorrente(), relatorio);
        ItemDemonstrativo itemReserva = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Reserva do RPPS", sistemaControlador.getExercicioCorrente(), relatorio);
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                switch (item.getOrdem()) {
                    case 1:
                    case 7:
                        atribuirNullNosValores(it);
                        break;
                    case 2:
                        it.setValorColuna2(recuperarPrevisaoInicial(itemReceita, sistemaControlador.getExercicioCorrente(), relatorio));
                        break;
                    case 3:
                        it.setValorColuna2(recuperarPrevisaoInicial(itemReceita, sistemaControlador.getExercicioCorrente(), relatorio).add(recuperarPrevisaoAtualizada(itemReceita, sistemaControlador.getExercicioCorrente(), relatorio)));
                        break;
                    case 4:
                        it.setValorColuna1(recuperarReceitaRealizadaNoBimestre(itemReceita, sistemaControlador.getExercicioCorrente(), relatorio));
                        it.setValorColuna2(recuperarReceitaRealizadaAteOBimestre(itemReceita, sistemaControlador.getExercicioCorrente(), relatorio));
                        break;
                    case 5:
                        it.setValorColuna2(getValorRefinancimentoReceita());
                        break;
                    case 6:
                        it.setValorColuna2(getSuperavitFinanceiro());
                        break;
                    case 8:
                        it.setValorColuna2(recuperarDotacaoInicial(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio)
                            .add(recuperarDotacaoInicial(itemReserva, sistemaControlador.getExercicioCorrente(), relatorio)));
                        break;
                    case 9:
                        it.setValorColuna2(recuperarCreditosAdicionais(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio));
                        break;
                    case 10:
                        it.setValorColuna2(recuperarDotacaoInicial(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio)
                            .add(recuperarDotacaoInicial(itemReserva, sistemaControlador.getExercicioCorrente(), relatorio))
                            .add(recuperarCreditosAdicionais(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio)));
                        break;
                    case 11:
                        it.setValorColuna2(recuperarDespesasEmpenhadasAteOBimestre(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio));
                        break;
                    case 12:
                        it.setValorColuna2(recuperarDespesasLiquidadasAteOBimestre(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio));
                        break;
                    case 13:
                        it.setValorColuna2(recuperarDespesasPagasAteOBimestre(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio));
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

    private List<RREOAnexo14Item> preparaDadosAnexo2() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("Despesas Empenhadas")) {
                    it = recuperaValoresAnexo2Empenhadas(it);
                } else if (it.getDescricao().equals("Despesas Liquidadas")) {
                    it = recuperaValoresAnexo2Liquidadas(it);
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> preparaDadosAnexo3() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(calculaRcl());
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> preparaDadosAnexo4() throws ParseException {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        ItemDemonstrativo itemReceita = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("Total das Receitas Previdenciárias RPPS (III) = (I + II)", sistemaControlador.getExercicioCorrente(), relatorio);
        ItemDemonstrativo itemDespesa = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("TOTAL DAS DESPESAS PREVIDENCIÁRIAS - RPPS (VI) = (IV + V)", sistemaControlador.getExercicioCorrente(), relatorio);
        BigDecimal valorReceitaAteBimestre = BigDecimal.ZERO;
        BigDecimal valorDespesaAteBimestre = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(item.getDescricaoComEspacos());
                if (item.getOrdem() == 1 || item.getOrdem() == 5) {
                    atribuirNullNosValores(it);
                } else if (item.getOrdem() == 2) {
                    it.setValorColuna2(recuperarReceitaRealizadaAteOBimestre(itemReceita, sistemaControlador.getExercicioCorrente(), relatorio));
                    valorReceitaAteBimestre = it.getValorColuna2();
                } else if (item.getOrdem() == 3) {
                    it.setValorColuna2(recuperarDespesasLiquidadasAteOBimestre(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio));
                    valorDespesaAteBimestre = it.getValorColuna2();
                } else if (item.getOrdem() == 4) {
                    it.setValorColuna2(valorReceitaAteBimestre.subtract(valorDespesaAteBimestre));
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private BigDecimal recuperaValorAnexo5() {
        BigDecimal resultado = BigDecimal.ZERO;
        RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 5", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        ItemDemonstrativo item = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("DÍVIDA FISCAL LÍQUIDA (VI) = (III + IV - V)", sistemaControlador.getExercicioCorrente(), relatorio);
        resultado = recuperaValorBimestreAtualAnexo5(item, relatorio);
        resultado = resultado.subtract(recuperaValorExercicioAnteriorAnexo5(item, relatorio));
        return resultado;
    }

    private HashMap<String, BigDecimal> buscarValoresAnexo5e6() {
        RelatorioRREOAnexo06NovoControlador relatorioRREOAnexo06NovoControlador = (RelatorioRREOAnexo06NovoControlador) Util.getControladorPeloNome("relatorioRREOAnexo06NovoControlador");
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRREOAnexo06NovoControlador.recuperarRelatorioEConfiguracoes();
        relatorioRREOAnexo06NovoControlador.popularComponentes(itensDemonstrativos);
        relatorioRREOAnexo06NovoControlador.limparCampos();
        relatorioRREOAnexo06NovoControlador.setMes(Mes.getMesToInt(new Integer(mesFinal)));
        relatorioRREOAnexo06NovoControlador.setModeloDe2018(true);
        relatorioRREOAnexo06NovoControlador.instanciarItemDemonstrativoFiltros();
        relatorioRREOAnexo06NovoControlador.buscarDadosGrupo5();
        relatorioRREOAnexo06NovoControlador.buscarDadosGrupo6();
        relatorioRREOAnexo06NovoControlador.buscarDadosGrupo8();
        HashMap mapAnexo5e6 = new HashMap();
        for (RREOAnexo06Item rreoAnexo06Item : relatorioRREOAnexo06NovoControlador.buscarDadosGrupo9()) {
            if (rreoAnexo06Item.getOrdem() == 5) {
                mapAnexo5e6.put("ANEXO5", rreoAnexo06Item.getValorColuna1());
            }
        }
        mapAnexo5e6.put("ANEXO6", relatorioRREOAnexo06NovoControlador.buscarDadosGrupo10().get(0).getValorColuna1());
        return mapAnexo5e6;
    }

    private List<RREOAnexo14Item> buscarDadosAnexo5e6() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        HashMap<String, BigDecimal> valoresAnexo5e6 = buscarValoresAnexo5e6();
        for (ItemDemonstrativoComponente idc : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(idc.getItemDemonstrativo(), 5);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                it.setDescricao(idc.getDescricaoComEspacos());
                if (idc.getOrdem() == 1) {
                    ReferenciaAnual referenciaAnual = relatorioRREOAnexo14Facade.getRelatorioRREOAnexo05Calculator().getReferenciaAnualFacade().recuperaReferenciaPorExercicio(sistemaControlador.getExercicioCorrente());
                    if (referenciaAnual != null) {
                        it.setValorColuna1(referenciaAnual.getMetaResultadoNominal());
                    }
                    it.setValorColuna2(valoresAnexo5e6.get("ANEXO5"));
                    if (it.getValorColuna1() != null && it.getValorColuna1().compareTo(BigDecimal.ZERO) != 0) {
                        it.setValorColuna3(it.getValorColuna2().divide(it.getValorColuna1(), BigDecimal.ROUND_HALF_UP, 6).multiply(BigDecimal.valueOf(100)));
                    }
                } else if (idc.getOrdem() == 2) {
                    ReferenciaAnual referenciaAnual = relatorioRREOAnexo14Facade.getReferenciaAnualFacade().recuperaReferenciaPorExercicio(sistemaControlador.getExercicioCorrente());
                    if (referenciaAnual != null) {
                        it.setValorColuna1(referenciaAnual.getMetaResultadoPrimario());
                    }
                    it.setValorColuna2(valoresAnexo5e6.get("ANEXO6"));
                    if (it.getValorColuna1() != null && it.getValorColuna1().compareTo(BigDecimal.ZERO) != 0) {
                        it.setValorColuna3(it.getValorColuna2().divide(it.getValorColuna1(), BigDecimal.ROUND_HALF_UP, 6).multiply(BigDecimal.valueOf(100)));
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> preparaDadosAnexo7() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        BigDecimal valorColuna1 = BigDecimal.ZERO;
        BigDecimal valorColuna2 = BigDecimal.ZERO;
        BigDecimal valorColuna3 = BigDecimal.ZERO;
        BigDecimal valorColuna4 = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 6);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("RESTOS A PAGAR PROCESSADOS") || it.getDescricao().equals("RESTOS A PAGAR NÃO-PROCESSADOS")) {
                    atribuirNullNosValores(it);
                } else if (item.getDescricao().equals("Poder Executivo (Processados)")) {
                    it.setValorColuna1(retornaRestosAPagarProcessadosEmDezembroExAnterior(sistemaControlador.getExercicioCorrente(), "EXECUTIVO", "EXECUTIVO", ""));
                    it.setValorColuna2(retornaRestosAPagarProcessadosCancelados(sistemaControlador.getExercicioCorrente(), "EXECUTIVO", "EXECUTIVO", ""));
                    it.setValorColuna3(retornaRestosAPagarProcessadosPagos(sistemaControlador.getExercicioCorrente(), "EXECUTIVO", "EXECUTIVO", ""));
                    it.setValorColuna4(it.getValorColuna1().subtract(it.getValorColuna3().add(it.getValorColuna2())));
                    valorColuna1 = valorColuna1.add(it.getValorColuna1());
                    valorColuna2 = valorColuna2.add(it.getValorColuna2());
                    valorColuna3 = valorColuna3.add(it.getValorColuna3());
                    valorColuna4 = valorColuna4.add(it.getValorColuna4());
                } else if (item.getDescricao().equals("Poder Legislativo (Processados)")) {
                    it.setValorColuna1(retornaRestosAPagarProcessadosEmDezembroExAnterior(sistemaControlador.getExercicioCorrente(), "LEGISLATIVO", "LEGISLATIVO", ""));
                    it.setValorColuna2(retornaRestosAPagarProcessadosCancelados(sistemaControlador.getExercicioCorrente(), "LEGISLATIVO", "LEGISLATIVO", ""));
                    it.setValorColuna3(retornaRestosAPagarProcessadosPagos(sistemaControlador.getExercicioCorrente(), "LEGISLATIVO", "LEGISLATIVO", ""));
                    it.setValorColuna4(it.getValorColuna1().subtract(it.getValorColuna3().add(it.getValorColuna2())));
                    valorColuna1 = valorColuna1.add(it.getValorColuna1());
                    valorColuna2 = valorColuna2.add(it.getValorColuna2());
                    valorColuna3 = valorColuna3.add(it.getValorColuna3());
                    valorColuna4 = valorColuna4.add(it.getValorColuna4());
                } else if (item.getDescricao().equals("Poder Executivo (Não Processados)")) {
                    it.setValorColuna1(retornaRestosAPagarNaoProcessadosEmDezembroExAnterior(sistemaControlador.getExercicioCorrente(), "EXECUTIVO", "EXECUTIVO", ""));
                    it.setValorColuna2(retornaRestosAPagarNaoProcessadosCancelados(sistemaControlador.getExercicioCorrente(), "EXECUTIVO", "EXECUTIVO", ""));
                    it.setValorColuna3(retornaRestosAPagarNaoProcessadosPagos(sistemaControlador.getExercicioCorrente(), "EXECUTIVO", "EXECUTIVO", ""));
                    it.setValorColuna4(it.getValorColuna1().subtract(it.getValorColuna3().add(it.getValorColuna2())));
                    valorColuna1 = valorColuna1.add(it.getValorColuna1());
                    valorColuna2 = valorColuna2.add(it.getValorColuna2());
                    valorColuna3 = valorColuna3.add(it.getValorColuna3());
                    valorColuna4 = valorColuna4.add(it.getValorColuna4());
                } else if (item.getDescricao().equals("Poder Legislativo (Não Processados)")) {
                    it.setValorColuna1(retornaRestosAPagarNaoProcessadosEmDezembroExAnterior(sistemaControlador.getExercicioCorrente(), "LEGISLATIVO", "LEGISLATIVO", ""));
                    it.setValorColuna2(retornaRestosAPagarNaoProcessadosCancelados(sistemaControlador.getExercicioCorrente(), "LEGISLATIVO", "LEGISLATIVO", ""));
                    it.setValorColuna3(retornaRestosAPagarNaoProcessadosPagos(sistemaControlador.getExercicioCorrente(), "LEGISLATIVO", "LEGISLATIVO", ""));
                    it.setValorColuna4(it.getValorColuna1().subtract(it.getValorColuna3().add(it.getValorColuna2())));
                    valorColuna1 = valorColuna1.add(it.getValorColuna1());
                    valorColuna2 = valorColuna2.add(it.getValorColuna2());
                    valorColuna3 = valorColuna3.add(it.getValorColuna3());
                    valorColuna4 = valorColuna4.add(it.getValorColuna4());
                } else if (item.getDescricao().equals("TOTAL")) {
                    it.setValorColuna1(valorColuna1);
                    it.setValorColuna2(valorColuna2);
                    it.setValorColuna3(valorColuna3);
                    it.setValorColuna4(valorColuna4);
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private RREOAnexo14Item recuperaValorAnexo08Linha1(RREOAnexo14Item rreoAnexo14Item) throws ParseException {
        List<RREOAnexo08Item10> lista = Lists.newArrayList();
        relatorioRREOAnexo08Controlador = new RelatorioRREOAnexo08Controlador2018();
        relatorioRREOAnexo08Controlador.setMesFinal(mesFinal);
        relatorioRREOAnexo08Controlador.setMesInicial(mesInicial);
        relatorioRREOAnexo08Controlador.setSistemaControlador(sistemaControlador);
        relatorioRREOAnexo08Controlador.setItemDemonstrativoFacade(itemDemonstrativoFacade);
        relatorioRREOAnexo08Controlador.setRelatorioRREOAnexo08Facade(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo08Facade2018());
        lista = relatorioRREOAnexo08Controlador.buscaRegistros();
        for (RREOAnexo08Item10 item : lista) {
            if (item.getDescricao().startsWith("37")) {
                rreoAnexo14Item.setValorColuna1(item.getValor());
            }
            if (item.getDescricao().startsWith("38")) {
                rreoAnexo14Item.setValorColuna3(item.getValor());
            }
        }
        return rreoAnexo14Item;
    }

    private BigDecimal recuperaValorAnexo08Linha2() throws ParseException {
        List<RREOAnexo08Item04> lista = Lists.newArrayList();
        relatorioRREOAnexo08Controlador = new RelatorioRREOAnexo08Controlador2018();
        relatorioRREOAnexo08Controlador.setMesFinal(mesFinal);
        relatorioRREOAnexo08Controlador.setMesInicial(mesInicial);
        relatorioRREOAnexo08Controlador.setSistemaControlador(sistemaControlador);
        relatorioRREOAnexo08Controlador.setItemDemonstrativoFacade(itemDemonstrativoFacade);
        relatorioRREOAnexo08Controlador.setRelatorioRREOAnexo08Facade(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo08Facade2018());
        lista = relatorioRREOAnexo08Controlador.buscarRegistrosIndice13();
        BigDecimal toReturn = BigDecimal.ZERO;
        for (RREOAnexo08Item04 item : lista) {
            if (item.getDescricao().startsWith("13- ")) {
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

    private BigDecimal recuperaValorAnexo08Linha2Percentual() throws ParseException {
        List<RREOAnexo08Item06> lista = Lists.newArrayList();
        relatorioRREOAnexo08Controlador = new RelatorioRREOAnexo08Controlador2018();
        relatorioRREOAnexo08Controlador.limparCampos();
        relatorioRREOAnexo08Controlador.setMesFinal(mesFinal);
        relatorioRREOAnexo08Controlador.setMesInicial(mesInicial);
        relatorioRREOAnexo08Controlador.setSistemaControlador(sistemaControlador);
        relatorioRREOAnexo08Controlador.setItemDemonstrativoFacade(itemDemonstrativoFacade);
        relatorioRREOAnexo08Controlador.setRelatorioRREOAnexo08Facade(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo08Facade2018());
        lista = relatorioRREOAnexo08Controlador.buscarRegistrosIndice19();
        BigDecimal toReturn = BigDecimal.ZERO;
        for (RREOAnexo08Item06 item : lista) {
            if (item.getDescricao().startsWith("   19.1-")) {
                toReturn = item.getValor();
                break;
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> preparaDadosAnexo8() throws ParseException {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 7);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().startsWith("Mínimo Anual de 25%")) {
                    it = recuperaValorAnexo08Linha1(it);
                    it.setValorColuna2(BigDecimal.valueOf(25));
                } else if (it.getDescricao().startsWith("Mínimo Anual de 60%")) {
                    it.setValorColuna1(recuperaValorAnexo08Linha2());
                    it.setValorColuna2(BigDecimal.valueOf(60));
                    it.setValorColuna3(recuperaValorAnexo08Linha2Percentual());
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> preparaDadosAnexo9() throws ParseException {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 9", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        ItemDemonstrativo itemReceita = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITAS DE OPERAÇÕES DE CRÉDITO (I)", sistemaControlador.getExercicioCorrente(), relatorio);
        ItemDemonstrativo itemDespesa = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("DESPESAS DE CAPITAL LÍQUIDA (II)", sistemaControlador.getExercicioCorrente(), relatorio);
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 8);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("Receitas de Operação de Crédito")) {
                    it.setValorColuna1(recuperarReceitaRealizadaAteOBimestre(itemReceita, sistemaControlador.getExercicioCorrente(), relatorio));
                    it.setValorColuna2(recuperarPrevisaoInicial(itemReceita, sistemaControlador.getExercicioCorrente(), relatorio).add(recuperarPrevisaoAtualizada(itemReceita, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst)).subtract(it.getValorColuna1()));
                } else if (it.getDescricao().equals("Despesa de Capital Líquida")) {
                    it.setValorColuna1(recuperarDespesasLiquidadasAteOBimestre(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio).add(recuperarRestoAPagarNaoProcessados(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio)));
                    it.setValorColuna2(recuperarDotacaoInicial(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio).add(recuperarCreditosAdicionais(itemDespesa, sistemaControlador.getExercicioCorrente(), relatorio)).subtract(it.getValorColuna1()));
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> preparaDadosAnexo10() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 9);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("Regime Geral de Previdência Social") || it.getDescricao().equals("Regime Próprio de Previdência Social dos Servidores")) {
                    atribuirNullNosValores(it);
                } else {
                    if (isDezembro()) {
                        it = relatorioRREOAnexo14Facade.buscaValorProjecao(item.getOrdem(), it,
                            relatorioRREOAnexo14Facade.getProjecaoAtuarialFacade().retornaMenorData(sistemaControlador.getExercicioCorrente()),
                            relatorioRREOAnexo14Facade.getProjecaoAtuarialFacade().retornaMaiorData(sistemaControlador.getExercicioCorrente()),
                            sistemaControlador.getExercicioCorrente());
                    } else {
                        it.setValorColuna1(BigDecimal.ZERO);
                        it.setValorColuna2(BigDecimal.ZERO);
                        it.setValorColuna3(BigDecimal.ZERO);
                        it.setValorColuna4(BigDecimal.ZERO);
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private boolean isDezembro() {
        return Mes.DEZEMBRO.equals(Mes.getMesToInt(Integer.valueOf(mesFinal)));
    }

    private List<RREOAnexo14Item> preparaDadosAnexo11() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 10);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(BigDecimal.ZERO);
                it.setValorColuna2(BigDecimal.ZERO);
                it.setValorColuna3(BigDecimal.ZERO);
                it.setValorColuna4(BigDecimal.ZERO);
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private BigDecimal recuperaValorColuna1Anexo12() {
        BigDecimal toReturn = BigDecimal.ZERO;
        RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 12", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        ItemDemonstrativo item = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("TOTAL DAS DESPESAS COM AÇÕES E SERVIÇOS PÚBLICOS DE SAÚDE (VI) = (IV - V)", sistemaControlador.getExercicioCorrente(), relatorio);
        toReturn = recuperarDespesasLiquidadasAteOBimestre(item, sistemaControlador.getExercicioCorrente(), relatorio);
        BigDecimal valorResto = BigDecimal.ZERO;
        if (isDezembro()) {
            valorResto = recuperarRestoAPagarNaoProcessados(item, sistemaControlador.getExercicioCorrente(), relatorio).subtract(BigDecimal.valueOf(13312.65));
        }
        toReturn = toReturn.add(valorResto);
        return toReturn;
    }

    private BigDecimal recuperaValorColuna3Anexo12() throws ParseException {
        List<RREOAnexo12Item> lista = Lists.newArrayList();
        relatorioRREOAnexo12Controlador = new RelatorioRREOAnexo12Controlador2018();
        relatorioRREOAnexo12Controlador.setDataFinal(mesFinal);
        relatorioRREOAnexo12Controlador.setDataInicial(mesInicial);
        relatorioRREOAnexo12Controlador.setSistemaControlador(sistemaControlador);
        relatorioRREOAnexo12Controlador.setItemDemonstrativoFacade(itemDemonstrativoFacade);
        relatorioRREOAnexo12Controlador.setRelatorioAnexo12RREOCalculator(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo12Calculator2018());
        lista = relatorioRREOAnexo12Controlador.buscaRegistros();
        BigDecimal toReturn = BigDecimal.ZERO;
        for (RREOAnexo12Item item : lista) {
            if (item.getDescricao().startsWith("PERCENTUAL DE APLICAÇÃO")) {
                toReturn = item.getValorColuna1();
                break;
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> preparaDadosAnexo12() throws ParseException {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 11);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(recuperaValorColuna1Anexo12());
                it.setValorColuna2(BigDecimal.valueOf(15));
                it.setValorColuna3(recuperaValorColuna3Anexo12());
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo14Item> preparaDadosAnexo13() {
        List<RREOAnexo14Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 12);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo14Item it = new RREOAnexo14Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(BigDecimal.ZERO);
                it.setValorColuna2(BigDecimal.ZERO);
                it.setValorColuna3(BigDecimal.ZERO);
                it.setValorColuna4(BigDecimal.ZERO);
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public RREOAnexo14Item recuperaValoresAnexo2Liquidadas(RREOAnexo14Item item) {
        List<Funcao> resultado = relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().buscarFuncoes(sistemaControlador.getExercicioCorrente(), "  ");
        for (Funcao funcao : relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().buscarFuncoes(sistemaControlador.getExercicioCorrente(), " NOT ")) {
            resultado = Util.adicionarObjetoEmLista(resultado, funcao);
        }
        BigDecimal totalColuna2 = BigDecimal.ZERO;
        for (Funcao funcao : resultado) {
            totalColuna2 = totalColuna2.add(recuperaDespesasLiquidadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), "  "));
            totalColuna2 = totalColuna2.add(recuperaDespesasLiquidadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " NOT "));
//            if (mesFinal.equals("12")) {
//                totalColuna2 = totalColuna2.add(recuperaDespesasLiquidadasAteOBimestreAnexo02Resto(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), "  "));
//                totalColuna2 = totalColuna2.add(recuperaDespesasLiquidadasAteOBimestreAnexo02Resto(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " NOT "));
//            }
        }
        item.setValorColuna2(totalColuna2);
        return item;
    }

    public RREOAnexo14Item recuperaValoresAnexo2Empenhadas(RREOAnexo14Item item) {
        List<Funcao> resultado = relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().buscarFuncoes(sistemaControlador.getExercicioCorrente(), "  ");
        for (Funcao funcao : relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().buscarFuncoes(sistemaControlador.getExercicioCorrente(), " NOT ")) {
            resultado = Util.adicionarObjetoEmLista(resultado, funcao);
        }
        BigDecimal totalColuna2 = BigDecimal.ZERO;
        for (Funcao funcao : resultado) {
            totalColuna2 = totalColuna2.add(recuperaDespesasEmpenhadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), "  "));
            totalColuna2 = totalColuna2.add(recuperaDespesasEmpenhadasAteOBimestreAnexo02(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " NOT "));
            if (isDezembro()) {
                totalColuna2 = totalColuna2.add(recuperaDespesasEmpenhadasAteOBimestreAnexo02Resto(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), "  "));
                totalColuna2 = totalColuna2.add(recuperaDespesasEmpenhadasAteOBimestreAnexo02Resto(sistemaControlador.getExercicioCorrente(), funcao.getCodigo(), " NOT "));
            }
        }
        item.setValorColuna2(totalColuna2);
        return item;
    }

    public BigDecimal calculaRcl() {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo14Facade.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", sistemaControlador.getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(sistemaControlador.getExercicioCorrente().getAno(), new Integer(mesFinal) - 1, new Integer(1));
            String data = formataDataMesAno(dataCalendar.getTime());
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo03Calculator().recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, sistemaControlador.getExercicioCorrente()));
                data = alteraMes(data);
            }
            return toReturn;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private String alteraMes(String data) {
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

    private String formataDataMesAno(Date data) {
        return new SimpleDateFormat("MM/yyyy").format(data);
    }

    public void gerarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 14", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDados());
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 14", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDados());
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 14 - Demonstrativo Simplificado do Relatório Resumido da Execução Orçamentária");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioRREOAnexo142018.jasper";
    }

    private HashMap montarParametros() throws ParseException {
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("ANEXO1", buscarDadosAnexo1());
        parameters.put("ANEXO2", preparaDadosAnexo2());
        parameters.put("ANEXO3", preparaDadosAnexo3());
        parameters.put("ANEXO4", preparaDadosAnexo4());
        parameters.put("ANEXO5E6", buscarDadosAnexo5e6());
        parameters.put("ANEXO7", preparaDadosAnexo7());
        parameters.put("ANEXO8", preparaDadosAnexo8());
        parameters.put("ANEXO9", preparaDadosAnexo9());
        parameters.put("ANEXO10", preparaDadosAnexo10());
        parameters.put("ANEXO11", preparaDadosAnexo11());
        parameters.put("ANEXO12", preparaDadosAnexo12());
        parameters.put("ANEXO13", preparaDadosAnexo13());
        parameters.put("DATAINICIAL", retornaDescricaoMes(mesInicial));
        parameters.put("DATAFINAL", retornaDescricaoMes(mesFinal));
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno().toString());
        parameters.put("USER", sistemaControlador.getUsuarioCorrente().getNome());
        return parameters;
    }

    @URLAction(mappingId = "relatorio-rreo-anexo14-2018", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.mesFinal = null;
        this.mesInicial = null;
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

    private BigDecimal recuperarPrevisaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcularPrevisaoInicialAlterado(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst);
    }

    private BigDecimal recuperarPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcularPrevisaoAtualizadaAlterado(itemDemonstrativo, exercicioCorrente, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperaValorBimestreAtualAnexo5(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getRelatorioRREOAnexo05Calculator().calcular(itDemonstrativo, "01/" + mesInicial + "/" + sistemaControlador.getExercicioCorrente().getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + sistemaControlador.getExercicioCorrente().getAno(), sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst);
    }

    private BigDecimal recuperaValorAteBimestreAtual(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcular(itDemonstrativo, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + sistemaControlador.getExercicioCorrente().getAno(), sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarReceitaRealizadaNoBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcularReceitaRealizadaNoBimestreAlterado(itemDemonstrativo, exercicioCorrente, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarReceitaRealizadaAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) throws ParseException {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcularReceitaRealizadaAteOBimestreAlterado(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarDotacaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcularDotacaoInicialAlterado(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst);
    }

    private BigDecimal recuperarCreditosAdicionais(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcularCreditosAdicionaisAlterado(itemDemonstrativo, exercicioCorrente, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarDespesasEmpenhadasAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcularDespesasEmpenhadasAteOBimestreAlterado(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarDespesasLiquidadasAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcularDespesasLiquidadasAteOBimestreAlterado(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarDespesasPagasAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcularDespesasPagasAteOBimestreAlterado(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperaDespesasEmpenhadasAteOBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String clausula) {
        return relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().calcularDespesasEmpenhadasAteOBimestreAnexo02(exercicioCorrente, codigo, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), clausula);
    }

    private BigDecimal recuperaDespesasEmpenhadasAteOBimestreAnexo02Resto(Exercicio exercicioCorrente, String codigo, String clausula) {
        Exercicio ex = relatorioRREOAnexo14Facade.getExercicioFacade().getExercicioPorAno(exercicioCorrente.getAno() - 1);
        return relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().calcularDespesasEmpenhadasAteOBimestreAnexo02Resto(ex, codigo, Util.getDiasMes(new Integer(mesFinal), ex.getAno()) + "/" + mesFinal + "/" + ex.getAno(), clausula);
    }

    private BigDecimal recuperaDespesasLiquidadasAteOBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String clausula) {
        return relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().calcularDespesasLiquidadasAteOBimestreAnexo02(exercicioCorrente, codigo, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), clausula);
    }

    private BigDecimal recuperaDespesasLiquidadasAteOBimestreAnexo02Resto(Exercicio exercicioCorrente, String codigo, String clausula) {
        return relatorioRREOAnexo14Facade.getRelatorioRREOAnexo02Calculator().calcularDespesasLiquidadasAteOBimestreAnexo02Resto(exercicioCorrente, codigo, Util.getDiasMes(new Integer(mesFinal), exercicioCorrente.getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), clausula);
    }

    private BigDecimal recuperaValorExercicioAnteriorAnexo5(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcular(itDemonstrativo, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst);
    }

    private BigDecimal retornaRestosAPagarProcessadosEmDezembroExAnterior(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2, String desdobrado) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarExAnteriores(exercicioCorrente, esferaPoder1, esferaPoder2, "=", desdobrado, "PROCESSADOS", " NOT "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarExAnteriores(exercicioCorrente, esferaPoder1, esferaPoder2, "=", desdobrado, "PROCESSADOS", " "));
        return valor;
    }

    private BigDecimal retornaRestosAPagarProcessadosCancelados(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2, String desdobrado) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosCancelados(exercicioCorrente, esferaPoder1, esferaPoder2, desdobrado, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), "PROCESSADOS", " "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosCancelados(exercicioCorrente, esferaPoder1, esferaPoder2, desdobrado, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), "PROCESSADOS", " NOT "));
        return valor;
    }

    private BigDecimal retornaRestosAPagarProcessadosPagos(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2, String desdobrado) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosPagos(exercicioCorrente, esferaPoder1, esferaPoder2, "=", desdobrado, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), "PROCESSADOS", " "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosPagos(exercicioCorrente, esferaPoder1, esferaPoder2, "=", desdobrado, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), "PROCESSADOS", " NOT "));
        return valor;
    }

    private BigDecimal retornaRestosAPagarNaoProcessadosEmDezembroExAnterior(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2, String desdobrado) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarExAnteriores(exercicioCorrente, esferaPoder1, esferaPoder2, "=", desdobrado, "NAO_PROCESSADOS", " NOT "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarExAnteriores(exercicioCorrente, esferaPoder1, esferaPoder2, "=", desdobrado, "NAO_PROCESSADOS", " "));
        return valor;
    }

    private BigDecimal retornaRestosAPagarNaoProcessadosCancelados(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2, String desdobrado) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosCancelados(exercicioCorrente, esferaPoder1, esferaPoder2, desdobrado, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), "NAO_PROCESSADOS", " NOT "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosCancelados(exercicioCorrente, esferaPoder1, esferaPoder2, desdobrado, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), "NAO_PROCESSADOS", " "));
        return valor;
    }

    private BigDecimal retornaRestosAPagarNaoProcessadosPagos(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2, String desdobrado) {
        BigDecimal valor = BigDecimal.ZERO;
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosPagos(exercicioCorrente, esferaPoder1, esferaPoder2, "=", desdobrado, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), "NAO_PROCESSADOS", " NOT "));
        valor = valor.add(relatorioRREOAnexo14Facade.getRelatorioRREOAnexo07Calculos().calculaRestosAPagarProcessadosPagos(exercicioCorrente, esferaPoder1, esferaPoder2, "=", desdobrado, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), "NAO_PROCESSADOS", " "));
        return valor;
    }

    private BigDecimal recuperarRestoAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo14Facade.getItemDemonstrativoCalculator().calcularRestoAPagarNaoProcessados(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
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

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
