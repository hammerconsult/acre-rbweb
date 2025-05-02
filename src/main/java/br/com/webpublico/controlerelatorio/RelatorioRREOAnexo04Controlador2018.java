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
import br.com.webpublico.entidadesauxiliares.RREOAnexo04Item;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo04Calculator;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
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
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo4-2018", pattern = "/relatorio/rreo/anexo4/2018/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo04-2018.xhtml")
})
@Deprecated
public class RelatorioRREOAnexo04Controlador2018 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRREOAnexo04Calculator relatorioRREOAnexo04Calculator;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private String mesInicial;
    private String mesFinal;
    private List<ItemDemonstrativoComponente> itens;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private Exercicio exercicioCorrente;
    private BigDecimal totalPrevisaoInicialGrupo1;
    private BigDecimal totalPrevisaoAtualizadaGrupo1;
    private BigDecimal totalReceitasRealizadasNoBimestreGrupo1;
    private BigDecimal totalReceitasRealizadasAteOBimestreGrupo1;
    private BigDecimal totalReceitasRealizadasAteOBimestreExAnteriorGrupo1;

    public RelatorioRREOAnexo04Controlador2018() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo4-2018", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.mesInicial = "";
        this.mesFinal = "";
    }

    public List<RREOAnexo04Item> prepararDados() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        RREOAnexo04Item item = new RREOAnexo04Item();
        item.setDescricao("");
        toReturn.add(item);
        return toReturn;
    }

    public List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo1() throws ParseException {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo04Item it = new RREOAnexo04Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(recuperarPrevisaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna2(recuperarPrevisaoAtualizada(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna3(recuperarReceitasRealizadasNoBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna4(recuperarReceitasRealizadasAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna5(recuperarReceitasRealizadasAteOBimestreExAnterior(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                if (it.getDescricao().equals("TOTAL DAS RECEITAS PREVIDENCIÁRIAS RPPS (III) = (I + II)")) {
                    totalPrevisaoInicialGrupo1 = it.getValorColuna1();
                    totalPrevisaoAtualizadaGrupo1 = it.getValorColuna2();
                    totalReceitasRealizadasNoBimestreGrupo1 = it.getValorColuna3();
                    totalReceitasRealizadasAteOBimestreGrupo1 = it.getValorColuna4();
                    totalReceitasRealizadasAteOBimestreExAnteriorGrupo1 = it.getValorColuna5();
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo2() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo04Item it = new RREOAnexo04Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(recuperarDotacaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna2(recuperarCreditosAdicionais(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst).add(it.getValorColuna1()));
                it.setValorColuna3(recuperarDespesasEmpenhadasAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna4(recuperarDespesasEmpenhadasAteOBimestreExAnterior(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna5(recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna6(recuperarDespesasLiquidadasAteOBimestreExAnterior(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                if (it.getDescricao().equals("RESULTADO PREVIDENCIÁRIO - RPPS (VII) = (III - VI)")) {
                    it.setValorColuna1(it.getValorColuna1().add(totalPrevisaoInicialGrupo1));
                    it.setValorColuna2(it.getValorColuna2().add(totalPrevisaoAtualizadaGrupo1));
                    it.setValorColuna5(it.getValorColuna5().add(totalReceitasRealizadasAteOBimestreGrupo1));
                    it.setValorColuna6(it.getValorColuna6().add(totalReceitasRealizadasAteOBimestreExAnteriorGrupo1));
                }
                if (mesFinal.equals("12")) {
                    it.setValorColuna7(recuperarRestoAPagarNaoProcessados(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                    it.setValorColuna8(recuperarRestoAPagarNaoProcessadosExAnterior(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                } else {
                    it.setValorColuna7(BigDecimal.ZERO);
                    it.setValorColuna8(BigDecimal.ZERO);
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo3() throws ParseException {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo04Item it = new RREOAnexo04Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(BigDecimal.ZERO);
                it.setValorColuna2(BigDecimal.ZERO);
                it.setValorColuna3(recuperarSaldoPeriodo(itemDemonstrativo, relatoriosItemDemonst));
                it.setValorColuna4(recuperarSaldoAtePeriodo(itemDemonstrativo, relatoriosItemDemonst));
                it.setValorColuna5(recuperarSaldoAtePeriodoExAnterior(itemDemonstrativo, relatoriosItemDemonst));
                toReturn.add(it);
            }
        }
        return toReturn;
    }


    public List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo4() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(recuperarReserva(itemDemonstrativo, relatoriosItemDemonst));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo5() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 5);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(recuperarSaldoMesInicial(itemDemonstrativo, relatoriosItemDemonst));
                it.setValorColuna2(recuperarSaldoPeriodo2(itemDemonstrativo, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                it.setValorColuna3(recuperarSaldoPeriodoExAnterior(itemDemonstrativo, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo6() throws ParseException {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 6);
            if (itemDemonstrativo.getId() != null) {
                exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo04Item it = new RREOAnexo04Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(recuperarPrevisaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna2(recuperarPrevisaoAtualizada(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna4(recuperarReceitasRealizadasAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna5(recuperarReceitasRealizadasAteOBimestreExAnterior(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo7() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 7);
            if (itemDemonstrativo.getId() != null) {
                exercicioCorrente = sistemaControlador.getExercicioCorrente();
                RREOAnexo04Item it = new RREOAnexo04Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(recuperarDotacaoInicial(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna2(recuperarCreditosAdicionais(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst).add(it.getValorColuna1()));
                it.setValorColuna3(recuperarDespesasEmpenhadasAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna4(recuperarDespesasEmpenhadasAteOBimestreExAnterior(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna5(recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                it.setValorColuna6(recuperarDespesasLiquidadasAteOBimestreExAnterior(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                if (mesFinal.equals("12")) {
                    it.setValorColuna7(recuperarRestoAPagarNaoProcessados(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                    it.setValorColuna8(recuperarRestoAPagarNaoProcessadosExAnterior(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst));
                } else {
                    it.setValorColuna7(BigDecimal.ZERO);
                    it.setValorColuna8(BigDecimal.ZERO);
                }
                toReturn.add(it);
            }
        }
        return toReturn;
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

    public void gerarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(prepararDados());
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }


    public void salvarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(prepararDados());
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 4 - Demonstrativo das Receitas e Despesas Previdenciárias do Regime Próprio de Previdência dos Servidores");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioRREOAnexo042018.jasper";
    }

    private HashMap montarParametros() throws ParseException {
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("GRUPO1", prepararDadosParaEmissaoGrupo1());
        parameters.put("GRUPO2", prepararDadosParaEmissaoGrupo2());
        parameters.put("GRUPO3", prepararDadosParaEmissaoGrupo3());
        parameters.put("GRUPO4", prepararDadosParaEmissaoGrupo4());
        parameters.put("GRUPO5", prepararDadosParaEmissaoGrupo5());
        parameters.put("GRUPO6", prepararDadosParaEmissaoGrupo6());
        parameters.put("GRUPO7", prepararDadosParaEmissaoGrupo7());
        parameters.put("BIMESTRE_FINAL", mesFinal.equals("12"));
        parameters.put("DATAINICIAL", retornaDescricaoMes(mesInicial));
        parameters.put("DATAFINAL", retornaDescricaoMes(mesFinal));
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno());
        return parameters;
    }

    private BigDecimal recuperarPrevisaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularPrevisaoInicialAlterado(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularPrevisaoAtualizadaAlterado(itemDemonstrativo, exercicioCorrente, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarSaldoMesInicial(ItemDemonstrativo itemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularSaldo(itemDemonstrativo, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesInicial), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesInicial + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarSaldoPeriodo(ItemDemonstrativo itemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcular(itemDemonstrativo, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), exercicioCorrente, relatoriosItemDemonst);
    }

    private BigDecimal recuperarSaldoPeriodo2(ItemDemonstrativo itemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcular(itemDemonstrativo, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), exercicioCorrente, relatoriosItemDemonst);
    }

    private BigDecimal recuperarSaldoPeriodoExAnterior(ItemDemonstrativo itemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio ex = exercicioFacade.getExercicioPorAno(exercicioCorrente.getAno() - 1);
        return relatorioRREOAnexo04Calculator.calcular(itemDemonstrativo, "01/" + mesInicial + "/" + ex.getAno(), Util.getDiasMes(new Integer(mesFinal), ex.getAno()) + "/" + mesFinal + "/" + ex.getAno(), ex, relatoriosItemDemonst);
    }

    private BigDecimal recuperarSaldoAtePeriodo(ItemDemonstrativo itemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcular(itemDemonstrativo, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), exercicioCorrente, relatoriosItemDemonst);
    }

    private BigDecimal recuperarSaldoAtePeriodoExAnterior(ItemDemonstrativo itemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio ex = exercicioFacade.getExercicioPorAno(exercicioCorrente.getAno() - 1);
        return relatorioRREOAnexo04Calculator.calcularSaldo(itemDemonstrativo, "01/01/" + ex.getAno(), Util.getDiasMes(new Integer(mesFinal), ex.getAno()) + "/" + mesFinal + "/" + ex.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarSaldoPeriodoExAnterior2(ItemDemonstrativo itemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio ex = exercicioFacade.getExercicioPorAno(exercicioCorrente.getAno() - 1);
        return relatorioRREOAnexo04Calculator.calcularSaldo(itemDemonstrativo, "01/01/" + ex.getAno(), Util.getDiasMes(new Integer(mesFinal), ex.getAno()) + "/" + mesFinal + "/" + ex.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarReserva(ItemDemonstrativo itemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularReserva(itemDemonstrativo, "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarReceitasRealizadasNoBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularReceitaRealizadaNoBimestreAlterado(itemDemonstrativo, exercicioCorrente, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarReceitasRealizadasAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) throws ParseException {
        return relatorioRREOAnexo04Calculator.calcularReceitaRealizadaAteOBimestreAlterado(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarReceitasRealizadasAteOBimestreExAnterior(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) throws ParseException {
        Exercicio ex = exercicioFacade.getExercicioPorAno(exercicioCorrente.getAno() - 1);
        return relatorioRREOAnexo04Calculator.calcularReceitaRealizadaAteOBimestreAlterado(getItemExercicioAnterior(itemDemonstrativo), ex, Util.getDiasMes(new Integer(mesFinal), ex.getAno()) + "/" + mesFinal + "/" + ex.getAno(), relatoriosItemDemonst);
    }

    private ItemDemonstrativo getItemExercicioAnterior(ItemDemonstrativo itemDemonstrativo) {
        return itemDemonstrativo.getItemExercicioAnterior() != null ? itemDemonstrativo.getItemExercicioAnterior() : itemDemonstrativo;
    }

    private BigDecimal recuperarDotacaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularDotacaoInicialAlterado(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst);
    }

    private BigDecimal recuperarCreditosAdicionais(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularCreditosAdicionaisAlterado(itemDemonstrativo, exercicioCorrente, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarDespesasLiquidadasNoBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularDespesasLiquidadasNoBimestreAlterado(itemDemonstrativo, exercicioCorrente, "01/" + mesInicial + "/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }


    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
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

    private BigDecimal recuperarDespesasEmpenhadasAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularDespesasEmpenhadasAteOBimestreAlterado(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarDespesasEmpenhadasAteOBimestreExAnterior(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio exercicio = buscarExercicioAnterior(exercicioCorrente);
        return relatorioRREOAnexo04Calculator.calcularDespesasEmpenhadasAteOBimestreAlterado(getItemExercicioAnterior(itemDemonstrativo), exercicio, Util.getDiasMes(new Integer(mesFinal), exercicio.getAno()) + "/" + mesFinal + "/" + exercicio.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarDespesasLiquidadasAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularDespesasLiquidadasAteOBimestreAlterado(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarDespesasLiquidadasAteOBimestreExAnterior(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio exercicio = buscarExercicioAnterior(exercicioCorrente);
        return relatorioRREOAnexo04Calculator.calcularDespesasLiquidadasAteOBimestreAlterado(getItemExercicioAnterior(itemDemonstrativo), exercicio, Util.getDiasMes(new Integer(mesFinal), exercicio.getAno()) + "/" + mesFinal + "/" + exercicio.getAno(), relatoriosItemDemonst);
    }

    private Exercicio buscarExercicioAnterior(Exercicio exercicioCorrente) {
        return relatorioRREOAnexo04Calculator.getExercicioFacade().getExercicioPorAno(exercicioCorrente.getAno() - 1);
    }

    private BigDecimal recuperarRestoAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo04Calculator.calcularRestoAPagarNaoProcessados(itemDemonstrativo, exercicioCorrente, Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), relatoriosItemDemonst);
    }

    private BigDecimal recuperarRestoAPagarNaoProcessadosExAnterior(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        Exercicio exercicio = buscarExercicioAnterior(exercicioCorrente);
        return relatorioRREOAnexo04Calculator.calcularRestoAPagarNaoProcessados(getItemExercicioAnterior(itemDemonstrativo), exercicio, Util.getDiasMes(new Integer(mesFinal), exercicio.getAno()) + "/" + mesFinal + "/" + exercicio.getAno(), relatoriosItemDemonst);
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
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

    public Exercicio getExercicioCorrente() {
        return exercicioCorrente;
    }

    public void setExercicioCorrente(Exercicio exercicioCorrente) {
        this.exercicioCorrente = exercicioCorrente;
    }

    public RelatorioRREOAnexo04Calculator getRelatorioRREOAnexo04Calculator() {
        return relatorioRREOAnexo04Calculator;
    }

    public void setRelatorioRREOAnexo04Calculator(RelatorioRREOAnexo04Calculator relatorioRREOAnexo04Calculator) {
        this.relatorioRREOAnexo04Calculator = relatorioRREOAnexo04Calculator;
    }
}
