/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.RGFAnexo01Pessoal2017;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author major
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioRGFAnexo01Calculator2017 extends ItemDemonstrativoCalculator implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Exercicio exercicioCorrente;
    private ItemDemonstrativo itemDemonstrativo;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<RGFAnexo01Pessoal2017> geraDadosGrupoUm(List<ItemDemonstrativoComponente> itens, RelatoriosItemDemonst relatoriosItemDemonst, String mesInicial, String mesFinal, Exercicio exercicioCorrente, BigDecimal despesaTotalComPessoal, String tipoAdministracao, String esferas) {
        List<RGFAnexo01Pessoal2017> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo01Pessoal2017 it = new RGFAnexo01Pessoal2017();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setLiquidacao(calcularLiquidacao(itemDemonstrativo, exercicioCorrente, mesInicial.equals("01") ? "01/" + mesInicial + "/" + exercicioCorrente.getAno() : "01/" + mesInicial + "/" + (exercicioCorrente.getAno() - 1), Util.getDiasMes(new Integer(mesFinal), exercicioCorrente.getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), tipoAdministracao == null ? "" : " AND VW.TIPOADMINISTRACAO = '" + tipoAdministracao + "'", esferas.equals("CONSOLIDADO") ? "" : " AND VW.esferadopoder = '" + esferas + "'", relatoriosItemDemonst));
                it.setInscritasRestos(calcularRestos(itemDemonstrativo, exercicioCorrente, mesInicial.equals("01") ? "01/01/" + exercicioCorrente.getAno() : "01/01/" + exercicioCorrente.getAno(), Util.getDiasMes(new Integer(mesFinal), exercicioCorrente.getAno()) + "/" + mesFinal + "/" + exercicioCorrente.getAno(), tipoAdministracao == null ? "" : " AND VW.TIPOADMINISTRACAO = '" + tipoAdministracao + "'", esferas.equals("CONSOLIDADO") ? "" : " AND VW.esferadopoder = '" + esferas + "'", relatoriosItemDemonst));
                if (item.getNome().equals("DESPESA LÍQUIDA COM PESSOAL (III) = (I - II)")) {
                    despesaTotalComPessoal = it.getLiquidacao().add(it.getInscritasRestos());
                }
                toReturn.add(it);
                if (item.getNome().equals("DESPESA LÍQUIDA COM PESSOAL (III) = (I - II)")) {
                    it = new RGFAnexo01Pessoal2017();
                    it.setDescricao("DESPESA TOTAL COM PESSOAL - DTP (IV) = (III a + III b)");
                    it.setLiquidacao(despesaTotalComPessoal);
                    toReturn.add(it);
                }
            }
        }
        return toReturn;
    }

    public BigDecimal calcularLiquidacao(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, String tipoAdministracao, String poder, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal liquidacao = calcularLiquidacaoImpl(itemDemonstrativo, exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst);
        return liquidacao;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularLiquidacaoImpl(ItemDemonstrativo itDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, String tipoAdministracao, String poder, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = getItemDemonstrativoFacade().recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularLiquidacaoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calculaLiquidacaoSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula)).multiply(new BigDecimal(-1));
                            } else {
                                totalAdicao = totalAdicao.add(calculaLiquidacaoSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularLiquidacaoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calculaLiquidacaoSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula)).multiply(new BigDecimal(-1));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calculaLiquidacaoSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaLiquidacaoSql(Exercicio ex, String dataInicial, String dataFinal, String tipoAdministracao, String poder, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        BigDecimal total;
        String sql = " SELECT sum(valor) as valor FROM( "
                + " SELECT COALESCE(sum(liq.valor), 0) AS valor FROM liquidacao liq"
                + "  inner join empenho a on liq.empenho_id = a.id  "
                + " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON a.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID and cast(LIQ.DATALIQUIDACAO as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, cast(LIQ.DATALIQUIDACAO as Date)) " + tipoAdministracao + " " + poder;
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  "
                + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " inner join PROVISAOPPAFONTE ppf on b.PROVISAOPPAFONTE_ID = ppf.id "
                    + " inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id "
                    + " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "fr", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
                    " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " + recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao()) {
                sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
            } else {
                sql += " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
                        " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " +
                        " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
            }
        }
        if (relatoriosItemDemonst.getUsaFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao()) {
                sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
            } else {
                sql += " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
                        " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " +
                        " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
            }
        }
        if (relatoriosItemDemonst.getUsaPrograma()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao() || relatoriosItemDemonst.getUsaFuncao()) {
                sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
            } else {
                sql += " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
                        " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " +
                        " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
            }
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " WHERE cast(liq.DATALIQUIDACAO as date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy')"
                + " and a.categoriaorcamentaria = 'NORMAL' and liq.categoriaorcamentaria = 'NORMAL' "
                + " union all "
                + " SELECT COALESCE(sum(liqest.valor), 0) * - 1 AS valor FROM liquidacaoestorno liqest "
                + " inner join liquidacao liq on liqest.liquidacao_id = liq.id"
                + " inner join empenho a on liq.empenho_id = a.id "
                + " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON a.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID and cast(liqest.dataestorno as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, cast(liqest.dataestorno as Date)) " + tipoAdministracao + " " + poder;
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  "
                + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " inner join PROVISAOPPAFONTE ppf on b.PROVISAOPPAFONTE_ID = ppf.id "
                    + " inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id "
                    + " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "fr", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
                    " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " + recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao()) {
                sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
            } else {
                sql += " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
                        " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " +
                        " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
            }
        }
        if (relatoriosItemDemonst.getUsaFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao()) {
                sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
            } else {
                sql += " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
                        " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " +
                        " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
            }
        }
        if (relatoriosItemDemonst.getUsaPrograma()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao() || relatoriosItemDemonst.getUsaFuncao()) {
                sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
            } else {
                sql += " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
                        " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " +
                        " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
            }
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " WHERE cast(liqest.dataestorno as date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy')"
                + " and a.categoriaorcamentaria = 'NORMAL' and liq.categoriaorcamentaria = 'NORMAL' "
                + " ) ";
        Query q = this.getEm().createNativeQuery(sql);
//        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularRestos(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, String tipoAdministracao, String poder, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal liquidacao = calcularRestosImpl(itemDemonstrativo, exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst);
        return liquidacao;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularRestosImpl(ItemDemonstrativo itDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, String tipoAdministracao, String poder, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = getItemDemonstrativoFacade().recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularRestosImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calculaRestosSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula)).multiply(new BigDecimal(-1));
                            } else {
                                totalAdicao = totalAdicao.add(calculaRestosSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestosImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calculaRestosSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula)).multiply(new BigDecimal(-1));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calculaRestosSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaRestosSql(Exercicio ex, String dataInicial, String dataFinal, String tipoAdministracao, String poder, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        BigDecimal total;
        String sql = " select sum(valor) from ( "
                + " SELECT COALESCE(SUM(EMP.valor),0) AS VALOR "
                + " FROM EMPENHO EMP "
                + " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID "
                + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID "
                + " INNER JOIN CONTA C ON PROV.CONTADEDESPESA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON EMP.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID and cast(EMP.DATAEMPENHO as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, cast(EMP.DATAEMPENHO as Date)) "
                + " WHERE UPPER(EMP.CATEGORIAORCAMENTARIA) LIKE 'RESTO'  " + tipoAdministracao + " " + poder
                + " and emp.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS' "
                + " AND cast(EMP.DATAEMPENHO as Date)  between  to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') "
                + " union all "
                + " SELECT COALESCE(SUM(est.valor),0) * - 1 AS VALOR  "
                + " from empenhoestorno est "
                + " inner join EMPENHO EMP on est.empenho_id = emp.id "
                + " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID  "
                + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID  "
                + " INNER JOIN CONTA C ON PROV.CONTADEDESPESA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON EMP.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID and cast(est.dataestorno as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, cast(est.dataestorno as Date))  "
                + " WHERE UPPER(est.CATEGORIAORCAMENTARIA) LIKE 'RESTO'  " + tipoAdministracao + " " + poder
                + "  AND cast(est.dataestorno as Date)  between  to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') "
                + " and emp.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS' "
                + " union all "
                + " SELECT COALESCE(SUM(liq.valor),0) * - 1 AS VALOR "
                + " FROM liquidacao liq "
                + " inner join EMPENHO EMP on liq.empenho_id = emp.id"
                + " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID "
                + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID "
                + " INNER JOIN CONTA C ON PROV.CONTADEDESPESA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON EMP.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID and cast(EMP.DATAEMPENHO as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, cast(EMP.DATAEMPENHO as Date)) "
                + " WHERE  UPPER(liq.CATEGORIAORCAMENTARIA) LIKE 'RESTO'  " + tipoAdministracao + " " + poder
                + " AND cast(liq.dataliquidacao as Date)  between  to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') "
                + " and emp.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS' "
                + " union all "
                + " SELECT COALESCE(SUM(est.valor),0) AS VALOR "
                + " FROM liquidacaoestorno est "
                + " inner join liquidacao liq on est.liquidacao_id = liq.id "
                + " inner join EMPENHO EMP on liq.empenho_id = emp.id"
                + " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID "
                + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID "
                + " INNER JOIN CONTA C ON PROV.CONTADEDESPESA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON EMP.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID and cast(EMP.DATAEMPENHO as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, cast(EMP.DATAEMPENHO as Date)) "
                + " WHERE UPPER(est.CATEGORIAORCAMENTARIA) LIKE 'RESTO'  " + tipoAdministracao + " " + poder
                + " and emp.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS' "
                + "  AND cast(est.dataestorno as Date) between  to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') "
                + " )";
        Query q = em.createNativeQuery(sql);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("DATAFINAL", dataFinal);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public Exercicio getExercicioCorrente() {
        return exercicioCorrente;
    }

    public void setExercicioCorrente(Exercicio exercicioCorrente) {
        this.exercicioCorrente = exercicioCorrente;
    }

    public ItemDemonstrativo getItemDemonstrativo() {
        return itemDemonstrativo;
    }

    public void setItemDemonstrativo(ItemDemonstrativo itemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public RelatorioRREOAnexo03Calculator getRelatorioRREOAnexo03Calculator() {
        return relatorioRREOAnexo03Calculator;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }
}
