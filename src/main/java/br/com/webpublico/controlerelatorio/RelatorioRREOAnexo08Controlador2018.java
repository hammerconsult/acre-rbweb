package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo08Facade2018;
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
    @URLMapping(id = "relatorio-rreo-anexo8-2018", pattern = "/relatorio/rreo/anexo8/2018/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo08-2018.xhtml")
})
public class RelatorioRREOAnexo08Controlador2018 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private RelatorioRREOAnexo08Facade2018 relatorioRREOAnexo08Facade;
    private String mesInicial;
    private String mesFinal;
    private List<ItemDemonstrativoComponente> itens;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private BigDecimal item3AteBimestre;
    private BigDecimal item11AteBimestre;
    private BigDecimal item12AteBimestre;
    private BigDecimal item13AteBimestre;
    private BigDecimal item14AteBimestre;
    private BigDecimal item17_1;
    private BigDecimal item17_2;
    private BigDecimal item22AteBimestre;
    private BigDecimal item23AteBimestre;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;

    public RelatorioRREOAnexo08Controlador2018() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
    }

    private List<RREOAnexo08Item01> preparaDados() {
        List<RREOAnexo08Item01> lista = new ArrayList<>();
        RREOAnexo08Item01 item = new RREOAnexo08Item01();
        item.setDescricao("");
        lista.add(item);
        return lista;
    }

    private List<RREOAnexo08Item01> buscarDadosGrupo1() throws ParseException {
        List<RREOAnexo08Item01> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item01 it = new RREOAnexo08Item01();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
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
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item02> buscarDadosGrupo2() throws ParseException {
        List<RREOAnexo08Item02> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item02 it = new RREOAnexo08Item02();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setPrevisaoInicial(buscarPrevisaoInicial(itemDemonstrativo));
                it.setPrevisaoAtualizada(it.getPrevisaoInicial().add(buscarPrevisaoAtualizada(itemDemonstrativo)));
                it.setReceitaRealizadaAteBimestre(buscarReceitaRealizadaAteOBimestre(itemDemonstrativo));
                if (it.getPrevisaoAtualizada().compareTo(BigDecimal.ZERO) > 0) {
                    it.setReceitaRealizadaPercentual(it.getReceitaRealizadaAteBimestre().divide(it.getPrevisaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    it.setReceitaRealizadaPercentual(BigDecimal.ZERO);
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item03> buscarDadosGrupo3() {
        List<RREOAnexo08Item03> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item03 it = new RREOAnexo08Item03();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setPrevisaoInicial(buscarPrevisaoInicial(itemDemonstrativo));
                it.setPrevisaoAtualizada(buscarPrevisaoAtualizada(itemDemonstrativo));
                it.setPrevisaoAtualizada(it.getPrevisaoInicial().add(it.getPrevisaoAtualizada()));
                it.setReceitaRealizadaAteBimestre(buscarReceitaRealizadaAteOBimestre(itemDemonstrativo));
                if (it.getPrevisaoAtualizada().compareTo(BigDecimal.ZERO) != 0) {
                    it.setReceitaRealizadaPercentual(it.getReceitaRealizadaAteBimestre().divide(it.getPrevisaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    it.setReceitaRealizadaPercentual(BigDecimal.ZERO);
                }
                if (it.getDescricao().startsWith("11-")) {
                    item11AteBimestre = it.getReceitaRealizadaAteBimestre();
                } else if (it.getDescricao().startsWith("12-")) {
                    item12AteBimestre = it.getReceitaRealizadaAteBimestre();
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item04> buscarDadosGrupo4() {
        List<RREOAnexo08Item04> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item04 it = new RREOAnexo08Item04();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
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
                } else if (it.getDescricao().startsWith("14-")) {
                    item14AteBimestre = it.getDespesaLiquidadaAteBimestre().add(it.getRestoAPagar());
                }

                toReturn.add(it);
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
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setOrdem(item.getOrdem());
                if (itemDemonstrativo.getDescricao().startsWith("16")) {
                    it.setValor(buscarEmpenhoRestoPorContaDestinacao(itemDemonstrativo).add(buscarCreditosAdicionais(itemDemonstrativo)));
                } else if (itemDemonstrativo.getDescricao().startsWith("17")) {
                    it.setValor(buscarEmpenhoNormalPorContaDestinacao(itemDemonstrativo).add(buscarCreditosAdicionaisSuperavit(itemDemonstrativo)));
                    if (itemDemonstrativo.getDescricao().startsWith("17.1-")) {
                        item17_1 = it.getValor();
                    } else if (itemDemonstrativo.getDescricao().startsWith("17.2-")) {
                        item17_2 = it.getValor();
                    }
                } else {
                    it.setValor(buscarEmpenhoNormalPorContaDestinacao(itemDemonstrativo).add(buscarCreditosAdicionaisSuperavit(itemDemonstrativo)));
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item06> buscarDadosGrupo6() {
        List<RREOAnexo08Item06> toReturn = new ArrayList<>();
        BigDecimal valor191 = BigDecimal.ZERO;
        BigDecimal valor192 = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 6);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item06 it = new RREOAnexo08Item06();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().startsWith("19 -")) {
                    BigDecimal valor = item13AteBimestre.add(item14AteBimestre);
                    it.setValor(valor.subtract(item17_1).subtract(item17_2));
                } else if (it.getDescricao().trim().startsWith("19.1-")) {
                    if (item11AteBimestre.compareTo(BigDecimal.ZERO) != 0) {
                        it.setValor((item13AteBimestre.subtract(item17_1)).divide(item11AteBimestre, 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                        valor191 = it.getValor().setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    } else {
                        it.setValor(BigDecimal.ZERO);
                    }
                } else if (it.getDescricao().trim().startsWith("19.2-")) {
                    if (item11AteBimestre.compareTo(BigDecimal.ZERO) != 0) {
                        it.setValor(item14AteBimestre.subtract(item17_2).divide(item11AteBimestre, 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                        valor192 = it.getValor().setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    } else {
                        it.setValor(BigDecimal.ZERO);
                    }
                } else if (it.getDescricao().trim().startsWith("19.3-")) {
                    if (item11AteBimestre.compareTo(BigDecimal.ZERO) != 0) {
                        it.setValor(BigDecimal.valueOf(100).subtract(valor191.add(valor192)));
                    } else {
                        it.setValor(BigDecimal.ZERO);
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item07> buscarDadosGrupo7() {
        List<RREOAnexo08Item07> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 7);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item07 it = new RREOAnexo08Item07();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (itemDemonstrativo.getDescricao().startsWith("21")) {
                    it.setValor(buscarEmpenhoNormalPorContaDestinacao(itemDemonstrativo).add(buscarCreditosAdicionaisSuperavit(itemDemonstrativo)));
                } else {
                    it.setValor(buscarSaldoContabilInicial(itemDemonstrativo));
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item09> buscarDadosGrupo8() {
        List<RREOAnexo08Item09> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 8);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item09 it = new RREOAnexo08Item09();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
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
                toReturn.add(it);
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
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (itemDemonstrativo.getDescricao().startsWith("30") || itemDemonstrativo.getDescricao().startsWith("32") || itemDemonstrativo.getDescricao().startsWith("33")) {
                    it.setValor(buscarEmpenhoNormalPorContaDestinacao(itemDemonstrativo));
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("31")) {
                    it.setValor(buscarValorContabilAtual(itemDemonstrativo));
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("34")) {
                    it.setValor(buscarEmpenhoRestoPorContaDestinacao(itemDemonstrativo));
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("29")) {
                    it.setValor(item12AteBimestre);
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("37")) {
                    it.setValor(item22AteBimestre.add(item23AteBimestre).subtract(valor));
                    valorItem38 = it.getValor();
                } else if (itemDemonstrativo.getDescricao().startsWith("38") && item3AteBimestre.compareTo(BigDecimal.ZERO) != 0) {
                    it.setValor(valorItem38.divide(item3AteBimestre, 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else if (itemDemonstrativo.getDescricao().startsWith("36")) {
                    it.setValor(valor);
                } else {
                    it.setValor(buscarValorContabilAtual(itemDemonstrativo));
                    valor = valor.add(it.getValor());
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo08Item10> buscaRegistros() throws ParseException {
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
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        return itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 8", TipoRelatorioItemDemonstrativo.RREO);
    }

    private void popularComponentes(List<ItemDemonstrativo> itensDemonstrativos) {
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
    }

    public List<RREOAnexo08Item04> buscarRegistrosIndice13() {
        List<RREOAnexo08Item04> toReturn = Lists.newArrayList();
        List<ItemDemonstrativo> itensDemonstrativos = recuperarRelatorioEConfiguracoes();
        popularComponentes(itensDemonstrativos);
        instanciarItemDemonstrativoFiltros();
        toReturn.addAll(buscarDadosGrupo4());
        return toReturn;
    }

    public List<RREOAnexo08Item05> buscarRegistrosIndice17() {
        List<RREOAnexo08Item05> toReturn = Lists.newArrayList();
        List<ItemDemonstrativo> itensDemonstrativos = recuperarRelatorioEConfiguracoes();
        popularComponentes(itensDemonstrativos);
        instanciarItemDemonstrativoFiltros();
        toReturn.addAll(buscarDadosGrupo5());
        return toReturn;
    }

    public List<RREOAnexo08Item06> buscarRegistrosIndice19() {
        List<RREOAnexo08Item06> toReturn = Lists.newArrayList();
        List<ItemDemonstrativo> itensDemonstrativos = recuperarRelatorioEConfiguracoes();
        popularComponentes(itensDemonstrativos);
        instanciarItemDemonstrativoFiltros();
        buscarDadosGrupo3();
        buscarDadosGrupo4();
        buscarDadosGrupo5();
        toReturn.addAll(buscarDadosGrupo6());
        return toReturn;
    }

    private List<RREOAnexo08Item11> buscarDadosGrupo10() {
        List<RREOAnexo08Item11> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 10);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item11 it = new RREOAnexo08Item11();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
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
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item12> buscarDadosGrupo11() {
        List<RREOAnexo08Item12> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 11);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item12 it = new RREOAnexo08Item12();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setSaldoAteBimestre(BigDecimal.ZERO);
                it.setCanceladosEm(buscarRestosAPagarCancelados(itemDemonstrativo));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item13> buscarDadosGrupo12() {
        List<RREOAnexo08Item13> toReturn = new ArrayList<>();
        BigDecimal valorFundeb = BigDecimal.ZERO;
        BigDecimal valorSalarioEducacao = BigDecimal.ZERO;
        //Variaveis auxiliares para calculo do item 48, é necessário ser feito desta forma pois as linhas buscam valores de locais diferentes, impossibilitando a busca de forma correta por recursão
        BigDecimal valorFundebItem48 = BigDecimal.ZERO;
        BigDecimal valorSalarioEducacaoItem48 = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 12);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item13 it = new RREOAnexo08Item13();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().startsWith("46-")) {
                    it.setFundeb(buscarSaldoSubContaInicialPorColuna(itemDemonstrativo, 1));
                    it.setSalarioEducacao(buscarSaldoSubContaInicialPorColuna(itemDemonstrativo, 2));

                    valorFundeb = valorFundeb.add(it.getFundeb());
                    valorSalarioEducacao = valorSalarioEducacao.add(it.getSalarioEducacao());
                } else if (it.getDescricao().startsWith("47-")
                    || it.getDescricao().startsWith("49-")) {
                    it.setFundeb(buscarReceitaRealizadaAteOBimestrePorColuna(itemDemonstrativo, 1));
                    it.setSalarioEducacao(buscarReceitaRealizadaAteOBimestrePorColuna(itemDemonstrativo, 2));
                    valorFundeb = valorFundeb.add(it.getFundeb());
                    valorSalarioEducacao = valorSalarioEducacao.add(it.getSalarioEducacao());
                } else if (it.getDescricao().trim().startsWith("48.")) {
                    if (it.getDescricao().trim().startsWith("48.1")) {
                        it.setFundeb(buscarDespesasPagasAteOBimestrePorColuna(itemDemonstrativo, 1));
                        it.setSalarioEducacao(buscarDespesasPagasAteOBimestrePorColuna(itemDemonstrativo, 2));
                    } else if (it.getDescricao().trim().startsWith("48.2")) {
                        it.setFundeb(buscarRestosPagosAteOBimestrePorColuna(itemDemonstrativo, 1));
                        it.setSalarioEducacao(buscarRestosPagosAteOBimestrePorColuna(itemDemonstrativo, 2));
                    }
                    valorFundeb = valorFundeb.subtract(it.getFundeb());
                    valorSalarioEducacao = valorSalarioEducacao.subtract(it.getSalarioEducacao());
                    valorFundebItem48 = valorFundebItem48.add(it.getFundeb());
                    valorSalarioEducacaoItem48 = valorSalarioEducacaoItem48.add(it.getSalarioEducacao());
                } else if (it.getDescricao().startsWith("50-")) {
                    it.setFundeb(valorFundeb);
                    it.setSalarioEducacao(valorSalarioEducacao);
                } else if (it.getDescricao().trim().startsWith("51")) {
                    it.setFundeb(buscarLancamentosConciliacaoPorColuna(itemDemonstrativo, 1));
                    it.setSalarioEducacao(buscarLancamentosConciliacaoPorColuna(itemDemonstrativo, 2));
                    if (it.getDescricao().trim().startsWith("51.2")) {
                        valorFundeb = valorFundeb.add(it.getFundeb());
                        valorSalarioEducacao = valorSalarioEducacao.add(it.getSalarioEducacao());
                    }
                } else if (it.getDescricao().trim().startsWith("52-")) {
                    it.setFundeb(valorFundeb);
                    it.setSalarioEducacao(valorSalarioEducacao);
                }
                toReturn.add(it);
            }
        }

        for (RREOAnexo08Item13 item : toReturn) {
            if (item.getDescricao().trim().startsWith("48")) {
                item.setFundeb(valorFundebItem48);
                item.setSalarioEducacao(valorSalarioEducacaoItem48);
                break;
            }
        }
        return toReturn;
    }

    public void gerarRelatorioRREOAnexo08() {
        try {
            String arquivoJasper = getNomeJasper();
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            relatorioRREOAnexo08Facade.limparHash();
            relatorioRREOAnexo08Facade.limparHashItensRecuperados();
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDados());
            gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDados());
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 8 - Demonstrativo Das Receitas e Despesas com Manutenção e Desenvolvimento do Ensino - MDE");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(getNomeJasper(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeJasper() {
        return "RelatorioRREOAnexo082018.jasper";
    }

    private HashMap montarParametros() throws ParseException {
        HashMap parameters = new HashMap();
        instanciarItemDemonstrativoFiltros();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("ITEM01", buscarDadosGrupo1());
        parameters.put("ITEM02", buscarDadosGrupo2());
        parameters.put("ITEM03", buscarDadosGrupo3());
        parameters.put("ITEM04", buscarDadosGrupo4());
        parameters.put("ITEM05", buscarDadosGrupo5());
        parameters.put("ITEM06", buscarDadosGrupo6());
        parameters.put("ITEM07", buscarDadosGrupo7());
        parameters.put("ITEM08", buscarDadosGrupo8());
        parameters.put("ITEM09", buscarDadosGrupo9());
        parameters.put("ITEM10", buscarDadosGrupo10());
        parameters.put("ITEM11", buscarDadosGrupo11());
        parameters.put("ITEM12", buscarDadosGrupo12());
        parameters.put("DATAINICIAL", Mes.getMesToInt(Integer.valueOf(mesInicial)).getDescricao().toUpperCase());
        parameters.put("DATAFINAL", Mes.getMesToInt(Integer.valueOf(mesFinal)).getDescricao().toUpperCase());
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno().toString());
        parameters.put("BIMESTRE_FINAL", isUltimoBimestre());
        parameters.put("USER", sistemaControlador.getUsuarioCorrente().getNome());
        return parameters;
    }

    private boolean isUltimoBimestre() {
        return "12".equals(mesFinal);
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + sistemaControlador.getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + sistemaControlador.getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataReferencia("01/" + mesInicial + "/" + sistemaControlador.getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(sistemaControlador.getExercicioCorrente());
    }

    @URLAction(mappingId = "relatorio-rreo-anexo8-2018", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mesFinal = null;
        mesInicial = null;
        item3AteBimestre = BigDecimal.ZERO;
        item11AteBimestre = BigDecimal.ZERO;
        item12AteBimestre = BigDecimal.ZERO;
        item13AteBimestre = BigDecimal.ZERO;
        item14AteBimestre = BigDecimal.ZERO;
        item17_1 = BigDecimal.ZERO;
        item17_2 = BigDecimal.ZERO;
        item22AteBimestre = BigDecimal.ZERO;
        item23AteBimestre = BigDecimal.ZERO;
    }

    private BigDecimal buscarDespesasPagasAteOBimestrePorColuna(ItemDemonstrativo itemDemonstrativo, Integer coluna) {
        return relatorioRREOAnexo08Facade.calcularValorPelaColuna(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_PAGAS_ATE_BIMESTRE_COM_DESPESA_EXTRA, coluna);
    }

    private BigDecimal buscarRestosPagosAteOBimestrePorColuna(ItemDemonstrativo itemDemonstrativo, Integer coluna) {
        return relatorioRREOAnexo08Facade.calcularValorPelaColuna(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_PAGOS_ATE_BIMESTRE, coluna);
    }

    private BigDecimal buscarLancamentosConciliacaoPorColuna(ItemDemonstrativo itemDemonstrativo, Integer coluna) {
        return relatorioRREOAnexo08Facade.calcularValorPelaColuna(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.LANCAMENTOS_CONCILIACAO_BANCARIA, coluna);
    }

    private BigDecimal buscarSaldoSubContaInicialPorColuna(ItemDemonstrativo itemDemonstrativo, Integer coluna) {
        return relatorioRREOAnexo08Facade.calcularValorPelaColuna(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_SUBCONTA_INICIAL, coluna);
    }

    private BigDecimal buscarValorContabilAtual(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_ATUAL);
    }

    private BigDecimal buscarSaldoContabilInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_INICIAL);
    }

    private BigDecimal buscarReceitaRealizadaAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE);
    }

    private BigDecimal buscarReceitaRealizadaAteOBimestrePorColuna(ItemDemonstrativo itemDemonstrativo, Integer coluna) {
        return relatorioRREOAnexo08Facade.calcularValorPelaColuna(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE, coluna);
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

    private BigDecimal buscarRestosAPagarCancelados(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_CANCELADOS);
    }

    private BigDecimal buscarRestoAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo08Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_NAO_PROCESSADOS);
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
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

    public RelatorioRREOAnexo08Facade2018 getRelatorioRREOAnexo08Facade() {
        return relatorioRREOAnexo08Facade;
    }

    public void setRelatorioRREOAnexo08Facade(RelatorioRREOAnexo08Facade2018 relatorioRREOAnexo08Facade) {
        this.relatorioRREOAnexo08Facade = relatorioRREOAnexo08Facade;
    }

    public ItemDemonstrativoFiltros getItemDemonstrativoFiltros() {
        return itemDemonstrativoFiltros;
    }

    public void setItemDemonstrativoFiltros(ItemDemonstrativoFiltros itemDemonstrativoFiltros) {
        this.itemDemonstrativoFiltros = itemDemonstrativoFiltros;
    }
}
