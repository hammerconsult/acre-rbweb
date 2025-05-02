package br.com.webpublico.controlerelatorio;

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
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo08Calculator;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
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
    @URLMapping(id = "relatorio-rreo-anexo8-2016", pattern = "/relatorio/rreo/anexo8/2016", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo082016.xhtml")
})
@Deprecated
public class RelatorioRREOAnexo082016Controlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private RelatorioRREOAnexo08Calculator relatorioRREOAnexo08Calculator;
    private String mesInicial;
    private String mesFinal;
    private List<ItemDemonstrativoComponente> itens;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private BigDecimal item3AteBimestre;
    private BigDecimal item11AteBimestre;
    private BigDecimal item12AteBimestre;
    private BigDecimal item13AteBimestre;
    private BigDecimal item14AteBimestre;
    private BigDecimal item17;
    private BigDecimal item23AteBimestre;
    private BigDecimal item24AteBimestre;


    public RelatorioRREOAnexo082016Controlador() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
        item3AteBimestre = BigDecimal.ZERO;
        item11AteBimestre = BigDecimal.ZERO;
        item12AteBimestre = BigDecimal.ZERO;
        item13AteBimestre = BigDecimal.ZERO;
        item14AteBimestre = BigDecimal.ZERO;
        item17 = BigDecimal.ZERO;
        item23AteBimestre = BigDecimal.ZERO;
        item24AteBimestre = BigDecimal.ZERO;
    }

    private List<RREOAnexo08Item01> preparaDados() {
        List<RREOAnexo08Item01> lista = new ArrayList<>();
        RREOAnexo08Item01 item = new RREOAnexo08Item01();
        item.setDescricao("");
        lista.add(item);
        return lista;
    }

    private List<RREOAnexo08Item01> preparaDadosItem01() throws ParseException {
        List<RREOAnexo08Item01> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item01 it = new RREOAnexo08Item01();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setPrevisaoInicial(recuperarPrevisaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setPrevisaoAtualizada(recuperarPrevisaoAtualizada(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setPrevisaoAtualizada(it.getPrevisaoInicial().add(it.getPrevisaoAtualizada()));
                it.setReceitaRealizadaNoBimestre(recuperarReceitaRealizadaNoBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setReceitaRealizadaAteBimestre(recuperarReceitaRealizadaAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
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

    private List<RREOAnexo08Item02> preparaDadosItem02() throws ParseException {
        List<RREOAnexo08Item02> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item02 it = new RREOAnexo08Item02();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setPrevisaoInicial(recuperarPrevisaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setPrevisaoAtualizada(it.getPrevisaoInicial().add(recuperarPrevisaoAtualizada(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst)));
                it.setReceitaRealizadaNoBimestre(recuperarReceitaRealizadaNoBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setReceitaRealizadaAteBimestre(recuperarReceitaRealizadaAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
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

    private List<RREOAnexo08Item03> preparaDadosItem03() throws ParseException {
        List<RREOAnexo08Item03> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item03 it = new RREOAnexo08Item03();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setPrevisaoInicial(recuperarPrevisaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setPrevisaoAtualizada(recuperarPrevisaoAtualizada(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setPrevisaoAtualizada(it.getPrevisaoInicial().add(it.getPrevisaoAtualizada()));
                it.setReceitaRealizadaNoBimestre(recuperarReceitaRealizadaNoBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setReceitaRealizadaAteBimestre(recuperarReceitaRealizadaAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
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

    private List<RREOAnexo08Item04> preparaDadosItem04() {
        List<RREOAnexo08Item04> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item04 it = new RREOAnexo08Item04();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setDotacaoInicial(recuperarDotacaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setDotacaoAtualizada(recuperarCreditosAdicionais(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setDotacaoAtualizada(it.getDotacaoAtualizada().add(it.getDotacaoInicial()));
                it.setDespesaEmpenhadaAteBimestre(calculaDespesaEmpenhadaAteBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setDespesaLiquidadaAteBimestre(recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                if (it.getDotacaoAtualizada().compareTo(BigDecimal.ZERO) != 0) {
                    it.setDespesaEmpenhadaPercentual(it.getDespesaEmpenhadaAteBimestre().divide(it.getDotacaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    it.setDespesaLiquidadaPercentual(it.getDespesaLiquidadaAteBimestre().divide(it.getDotacaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    it.setDespesaEmpenhadaPercentual(BigDecimal.ZERO);
                    it.setDespesaLiquidadaPercentual(BigDecimal.ZERO);
                }
                if (mesFinal.equals("12")) {
                    it.setRestoAPagar(recuperarRestoAPagarNaoProcessados(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
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

    private List<RREOAnexo08Item05> preparaDadosItem05() {
        List<RREOAnexo08Item05> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 5);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item05 it = new RREOAnexo08Item05();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (itemDemonstrativo.getDescricao().startsWith("16")) {
                    it.setValor(recuperaValorResto(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                } else {
                    //solicitação Sr Tonismar para o ajuste de valor no relatório.
                    //segundo o próprio Sr Tonismar, não há configuração para este item.
                    //Jaime aceitou a proposta feita pelo Sr Tonismar de deixar o valor fixo para esse caso no último bimestre de 2015
                    if (mesFinal.equals("12") && sistemaControlador.getExercicioCorrente().getAno() == 2015) {
                        if (itemDemonstrativo.getDescricao().startsWith("17-") || itemDemonstrativo.getDescricao().startsWith("17.2-") ||
                            itemDemonstrativo.getDescricao().startsWith("18-")) {
                            item17 = BigDecimal.valueOf(119845.66);
                            it.setValor(item17);
                        } else {
                            it.setValor(recuperaValorDestinacao(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                        }
                    } else {
                        it.setValor(recuperaValorDestinacao(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item06> preparaDadosItem06() {
        List<RREOAnexo08Item06> toReturn = new ArrayList<>();
        BigDecimal valor191 = BigDecimal.ZERO;
        BigDecimal valor192 = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 6);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item06 it = new RREOAnexo08Item06();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().startsWith("19 -")) {
                    it.setValor(recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst).subtract(item17));
                    it.setValor(it.getValor().add(recuperarRestoAPagarNaoProcessados(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst)));
                } else if (it.getDescricao().startsWith("   19.1-")) {
                    if (item11AteBimestre.compareTo(BigDecimal.ZERO) != 0) {
                        it.setValor(item13AteBimestre.divide(item11AteBimestre, 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                        valor191 = it.getValor().setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    } else {
                        it.setValor(BigDecimal.ZERO);
                    }
                } else if (it.getDescricao().startsWith("   19.2-")) {
                    if (item11AteBimestre.compareTo(BigDecimal.ZERO) != 0) {
                        it.setValor(item14AteBimestre.subtract(item17).divide(item11AteBimestre, 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                        valor192 = it.getValor().setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    } else {
                        it.setValor(BigDecimal.ZERO);
                    }
                } else if (it.getDescricao().startsWith("   19.3-")) {
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

    private List<RREOAnexo08Item07> preparaDadosItem07() {
        List<RREOAnexo08Item07> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 7);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item07 it = new RREOAnexo08Item07();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (itemDemonstrativo.getDescricao().startsWith("21")) {
                    it.setValor(recuperaValorDestinacao(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                } else {
                    it.setValor(recuperaSaldoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item08> preparaDadosItem08() throws ParseException {
        List<RREOAnexo08Item08> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 8);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item08 it = new RREOAnexo08Item08();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setPrevisaoInicial(recuperarPrevisaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst).multiply(BigDecimal.valueOf(0.25)));
                it.setPrevisaoAtualizada(recuperarPrevisaoAtualizada(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst).multiply(BigDecimal.valueOf(0.25)));
                it.setPrevisaoAtualizada(it.getPrevisaoInicial().add(it.getPrevisaoAtualizada()));
                it.setReceitaRealizadaNoBimestre(recuperarReceitaRealizadaNoBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst).multiply(BigDecimal.valueOf(0.25)));
                it.setReceitaRealizadaAteBimestre(recuperarReceitaRealizadaAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst).multiply(BigDecimal.valueOf(0.25)));
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

    private List<RREOAnexo08Item09> preparaDadosItem09() {
        List<RREOAnexo08Item09> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 9);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item09 it = new RREOAnexo08Item09();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setDotacaoInicial(recuperarDotacaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setDotacaoAtualizada(recuperarCreditosAdicionais(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setDotacaoAtualizada(it.getDotacaoAtualizada().add(it.getDotacaoInicial()));
                it.setDespesaEmpenhadaAteBimestre(calculaDespesaEmpenhadaAteBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setDespesaLiquidadaAteBimestre(recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                if (it.getDotacaoAtualizada().compareTo(BigDecimal.ZERO) != 0) {
                    it.setDespesaEmpenhadaPercentual(it.getDespesaEmpenhadaAteBimestre().divide(it.getDotacaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    it.setDespesaLiquidadaPercentual(it.getDespesaLiquidadaAteBimestre().divide(it.getDotacaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    it.setDespesaEmpenhadaPercentual(BigDecimal.ZERO);
                    it.setDespesaLiquidadaPercentual(BigDecimal.ZERO);
                }
                if (mesFinal.equals("12")) {
                    it.setRestoAPagar(recuperarRestoAPagarNaoProcessados(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                } else {
                    it.setRestoAPagar(BigDecimal.ZERO);
                }
                if (it.getDescricao().startsWith("23")) {
                    item23AteBimestre = it.getDespesaLiquidadaAteBimestre().add(it.getRestoAPagar());
                } else if (it.getDescricao().startsWith("24")) {
                    item24AteBimestre = it.getDespesaLiquidadaAteBimestre().add(it.getRestoAPagar());
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item10> preparaDadosItem10() {
        List<RREOAnexo08Item10> toReturn = new ArrayList<>();
        BigDecimal valor = BigDecimal.ZERO;
        BigDecimal valorItem38 = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 10);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item10 it = new RREOAnexo08Item10();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (itemDemonstrativo.getDescricao().startsWith("31") || itemDemonstrativo.getDescricao().startsWith("33") || itemDemonstrativo.getDescricao().startsWith("34")) {
                    it.setValor(recuperaValorDestinacao(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("32")) {
                    it.setValor(recuperaValor(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("35")) {
                    it.setValor(recuperaValorResto(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("30")) {
                    it.setValor(item12AteBimestre);
                    valor = valor.add(it.getValor());
                } else if (itemDemonstrativo.getDescricao().startsWith("38")) {
                    it.setValor(item23AteBimestre.add(item24AteBimestre).subtract(valor));
                    valorItem38 = it.getValor();
                } else if (itemDemonstrativo.getDescricao().startsWith("39") && item3AteBimestre.compareTo(BigDecimal.ZERO) != 0) {
                    it.setValor(valorItem38.divide(item3AteBimestre, 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else if (itemDemonstrativo.getDescricao().startsWith("37")) {
                    it.setValor(valor);
                } else {
                    it.setValor(recuperaValor(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                    valor = valor.add(it.getValor());
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo08Item10> buscaRegistros() throws ParseException {
        List<RREOAnexo08Item10> toReturn = new ArrayList<>();
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 8", TipoRelatorioItemDemonstrativo.RREO);
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
        preparaDadosItem01();
        preparaDadosItem03();
        preparaDadosItem09();
        toReturn.addAll(preparaDadosItem10());
        return toReturn;
    }

    public List<RREOAnexo08Item04> buscaRegistrosIndice13() throws ParseException {
        List<RREOAnexo08Item04> toReturn = new ArrayList<>();
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 8", TipoRelatorioItemDemonstrativo.RREO);
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
        toReturn.addAll(preparaDadosItem04());
        return toReturn;
    }

    public List<RREOAnexo08Item06> buscaRegistrosIndice19() throws ParseException {
        List<RREOAnexo08Item06> toReturn = new ArrayList<>();
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 8", TipoRelatorioItemDemonstrativo.RREO);
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
        preparaDadosItem03();
        preparaDadosItem04();
        toReturn.addAll(preparaDadosItem06());
        return toReturn;
    }

    private List<RREOAnexo08Item11> preparaDadosItem11() {
        List<RREOAnexo08Item11> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 11);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item11 it = new RREOAnexo08Item11();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setDotacaoInicial(recuperarDotacaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setDotacaoAtualizada(recuperarCreditosAdicionais(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setDotacaoAtualizada(it.getDotacaoAtualizada().add(it.getDotacaoInicial()));
                it.setDespesaEmpenhadaAteBimestre(calculaDespesaEmpenhadaAteBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setDespesaLiquidadaAteBimestre(recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                if (it.getDotacaoAtualizada().compareTo(BigDecimal.ZERO) != 0) {
                    it.setDespesaEmpenhadaPercentual(it.getDespesaEmpenhadaAteBimestre().divide(it.getDotacaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    it.setDespesaLiquidadaPercentual(it.getDespesaLiquidadaAteBimestre().divide(it.getDotacaoAtualizada(), 6, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                } else {
                    it.setDespesaEmpenhadaPercentual(BigDecimal.ZERO);
                    it.setDespesaLiquidadaPercentual(BigDecimal.ZERO);
                }
                if (mesFinal.equals("12")) {
                    it.setRestoAPagar(recuperarRestoAPagarNaoProcessados(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                } else {
                    it.setRestoAPagar(BigDecimal.ZERO);
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item12> preparaDadosItem12() {
        List<RREOAnexo08Item12> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 12);
            if (itemDemonstrativo.getId() != null) {
                Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo08Item12 it = new RREOAnexo08Item12();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setSaldoAteBimestre(BigDecimal.ZERO);
                it.setCanceladosEm(calculaRestosAPagarCancelados(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo08Item13> preparaDadosItem13() {
        List<RREOAnexo08Item13> toReturn = new ArrayList<>();
        BigDecimal valor = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 13);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo08Item13 it = new RREOAnexo08Item13();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().startsWith("47-")) {
                    it.setFundeb(recuperaSaldoInicial(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    valor = valor.add(it.getFundeb());
                } else if (it.getDescricao().startsWith("48-")) {
                    it.setFundeb(recuperaValorItem48(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    valor = valor.add(it.getFundeb());
                } else if (it.getDescricao().startsWith("49-")) {
                    it.setFundeb(recuperaValor(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    if (it.getFundeb().compareTo(BigDecimal.ZERO) < 0) {
                        it.setFundeb(it.getSalarioEducacao().multiply(BigDecimal.valueOf(-1)));
                    }
                    valor = valor.subtract(it.getFundeb());
                } else if (it.getDescricao().startsWith("   49.")) {
                    it.setFundeb(recuperaValor(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    if (it.getFundeb().compareTo(BigDecimal.ZERO) < 0) {
                        it.setFundeb(it.getSalarioEducacao().multiply(BigDecimal.valueOf(-1)));
                    }
                } else if (it.getDescricao().startsWith("51-")) {
                    it.setFundeb(valor);
                } else {
                    it.setFundeb(recuperaValor(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                    valor = valor.add(it.getFundeb());
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public void gerarRelatorioRREOAnexo08() throws ParseException, IOException, JRException {
        try {
            String arquivoJasper = getNomeJasper();
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 8", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDados());
            gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
    public void salvarRelatorio() throws ParseException, IOException, JRException {
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
        return "RelatorioRREOAnexo08_2016.jasper";
    }

    private HashMap montarParametros() throws ParseException {
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("ITEM01", preparaDadosItem01());
        parameters.put("ITEM02", preparaDadosItem02());
        parameters.put("ITEM03", preparaDadosItem03());
        parameters.put("ITEM04", preparaDadosItem04());
        parameters.put("ITEM05", preparaDadosItem05());
        parameters.put("ITEM06", preparaDadosItem06());
        parameters.put("ITEM07", preparaDadosItem07());
        parameters.put("ITEM08", preparaDadosItem08());
        parameters.put("ITEM09", preparaDadosItem09());
        parameters.put("ITEM10", preparaDadosItem10());
        parameters.put("ITEM11", preparaDadosItem11());
        parameters.put("ITEM12", preparaDadosItem12());
        parameters.put("ITEM13", preparaDadosItem13());
        parameters.put("DATAINICIAL", retornaDescricaoMes(mesInicial));
        parameters.put("DATAFINAL", retornaDescricaoMes(mesFinal));
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno().toString());
        parameters.put("BIMESTRE_FINAL", mesFinal.equals("12"));
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        return parameters;
    }

    @URLAction(mappingId = "relatorio-rreo-anexo8-2016", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.mesFinal = null;
        this.mesInicial = null;
    }

    private BigDecimal recuperaValorResto(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calculaValorRestoDestinacao(itemDemonstrativo, exercicio, relatoriosItemDemonst);
    }

    private BigDecimal recuperaValor(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcular(itemDemonstrativo, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + sistemaControlador.getExercicioCorrente().getAno(), exercicio, relatoriosItemDemonst);
    }

    private BigDecimal recuperaValorItem48(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularItem48(itemDemonstrativo, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + sistemaControlador.getExercicioCorrente().getAno(), exercicio, relatoriosItemDemonst);
    }

    private BigDecimal recuperaSaldoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularSaldoInicial(itemDemonstrativo, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), exercicio, relatoriosItemDemonst);
    }

    private BigDecimal recuperaValorDestinacao(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calculaValorDestinacao(itemDemonstrativo, exercicio, relatoriosItemDemonst);
    }

    private BigDecimal recuperarPrevisaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularPrevisaoInicialAlterado(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst);
    }

    private BigDecimal recuperarPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularPrevisaoAtualizadaAlterado(itemDemonstrativo, exercicioCorrente, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarReceitaRealizadaNoBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularReceitaRealizadaNoBimestreAlterado(itemDemonstrativo, exercicioCorrente, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarReceitaRealizadaAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) throws ParseException {
        return relatorioRREOAnexo08Calculator.calcularReceitaRealizadaAteOBimestreAlterado(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarDotacaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularDotacaoInicialAlterado(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst);
    }

    private BigDecimal recuperarCreditosAdicionais(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularCreditosAdicionaisAlterado(itemDemonstrativo, exercicioCorrente, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarDespesasLiquidadasAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularDespesasLiquidadasAteOBimestreAlterado(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal calculaRestosAPagarCancelados(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularRestosAPagarCanceladosDespesaPropriaImpl(itDemonstrativo, "01/" + mesInicial + "/" + ex.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + ex.getAno(), ex, relatoriosItemDemonst);
    }

    private BigDecimal calculaDespesaEmpenhadaAteBimestre(ItemDemonstrativo itDemonstrativo, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularDespesasEmpenhadasAteOBimestreAlterado(itDemonstrativo, ex, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + ex.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarRestoAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo08Calculator.calcularRestoAPagarNaoProcessados(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
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

    public RelatorioRREOAnexo08Calculator getRelatorioRREOAnexo08Calculator() {
        return relatorioRREOAnexo08Calculator;
    }

    public void setRelatorioRREOAnexo08Calculator(RelatorioRREOAnexo08Calculator relatorioRREOAnexo08Calculator) {
        this.relatorioRREOAnexo08Calculator = relatorioRREOAnexo08Calculator;
    }
}
