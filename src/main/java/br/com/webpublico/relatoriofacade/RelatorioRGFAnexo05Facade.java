package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.negocios.contabil.RelatorioItemDemonstrativoCalculador;
import br.com.webpublico.util.UtilRelatorioContabil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mateus on 26/04/18.
 */
@Stateless
public class RelatorioRGFAnexo05Facade extends RelatorioItemDemonstrativoCalculador implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ItemDemonstrativoCalculator itemDemonstrativoCalculator;

    @EJB
    private RelatorioDemonstrativoDisponibilidadeRecursoFacade relatorioDDRFacade;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularRestoEmpenhoNaoProcessados(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, List<ParametrosRelatorios> parametros) {
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
                        total = total.add(calcularRestoEmpenhoNaoProcessados(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, parametros));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(saldoRestoEmpenhoNaoProcessados(formula, relatoriosItemDemonst, parametros).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(saldoRestoEmpenhoNaoProcessados(formula, relatoriosItemDemonst, parametros));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestoEmpenhoNaoProcessados(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, parametros));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(saldoRestoEmpenhoNaoProcessados(formula, relatoriosItemDemonst, parametros).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(saldoRestoEmpenhoNaoProcessados(formula, relatoriosItemDemonst, parametros));
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
    public BigDecimal saldoRestoEmpenhoNaoProcessados(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(valor), 0) as saldo from (  ")
            .append(" SELECT  sum(coalesce(e.valor,0)) as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" AND E.DATAEMPENHO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT  sum(coalesce(est.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join EMPENHOESTORNO est on EST.EMPENHO_ID = e.id and est.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" union all    ")
            .append(" SELECT  sum(coalesce(l.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and l.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" AND E.DATAEMPENHO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT  sum(coalesce(le.valor,0)) as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and l.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join liquidacaoestorno le on le.liquidacao_id = l.id and le.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO) ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" ) ");
        Query q = em.createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getSingleResult();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularRestoEmpenhoNaoProcessadosExerciciosAnteriores(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, List<ParametrosRelatorios> parametros) {
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
                        total = total.add(calcularRestoEmpenhoNaoProcessadosExerciciosAnteriores(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, parametros));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(saldoRestoEmpenhoNaoProcessadosExerciciosAnteriores(formula, relatoriosItemDemonst, parametros).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(saldoRestoEmpenhoNaoProcessadosExerciciosAnteriores(formula, relatoriosItemDemonst, parametros));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestoEmpenhoNaoProcessadosExerciciosAnteriores(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, parametros));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(saldoRestoEmpenhoNaoProcessadosExerciciosAnteriores(formula, relatoriosItemDemonst, parametros).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(saldoRestoEmpenhoNaoProcessadosExerciciosAnteriores(formula, relatoriosItemDemonst, parametros));
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
    public BigDecimal saldoRestoEmpenhoNaoProcessadosExerciciosAnteriores(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(valor), 0) as saldo from (  ")
            .append(" SELECT  sum(coalesce(e.valor,0)) as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = :categoria  ")
            .append(" and E.TIPORESTOSPROCESSADOS = :tiporesto  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" AND trunc(E.DATAEMPENHO) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(E.DATAEMPENHO))  ")
            .append(" AND trunc(E.DATAEMPENHO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT  sum(coalesce(est.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join EMPENHOESTORNO est on EST.EMPENHO_ID = e.id   ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = :categoria  ")
            .append(" and E.TIPORESTOSPROCESSADOS = :tiporesto  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" AND trunc(est.dataestorno) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(est.dataestorno))  ")
            .append(" and trunc(est.dataestorno) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" union all    ")
            .append(" SELECT  sum(coalesce(l.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = :categoria  ")
            .append(" and E.TIPORESTOSPROCESSADOS = :tiporesto  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" and trunc(l.dataliquidacao) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" AND trunc(l.dataliquidacao) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(l.dataliquidacao))  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT  sum(coalesce(le.valor,0)) as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID ")
            .append(" inner join liquidacaoestorno le on le.liquidacao_id = l.id ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = :categoria  ")
            .append(" and E.TIPORESTOSPROCESSADOS = :tiporesto  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" and trunc(le.dataestorno) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" AND trunc(le.dataestorno) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(le.dataestorno))  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" ) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("categoria", CategoriaOrcamentaria.RESTO.name());
        q.setParameter("tiporesto", TipoRestosProcessado.NAO_PROCESSADOS.name());

        UtilRelatorioContabil.adicionarParametros(parametros, q);
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getSingleResult();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, List<ParametrosRelatorios> parametros) {
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
                        total = total.add(calcularRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, parametros));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(saldoRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(formula, relatoriosItemDemonst, parametros).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(saldoRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(formula, relatoriosItemDemonst, parametros));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, parametros));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(saldoRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(formula, relatoriosItemDemonst, parametros).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(saldoRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(formula, relatoriosItemDemonst, parametros));
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
    public BigDecimal saldoRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(valor), 0) as saldo from (  ")
            .append(" SELECT  sum(coalesce(e.valor,0)) as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" AND E.DATAEMPENHO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'PROCESSADOS' ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT  sum(coalesce(est.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join EMPENHOESTORNO est on EST.EMPENHO_ID = e.id and est.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'PROCESSADOS' ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT sum(coalesce(p.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and l.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join pagamento p on p.liquidacao_id = l.id and p.datapagamento between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'PROCESSADOS' and p.status <> 'ABERTO' ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT sum(coalesce(pe.valor,0)) as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and l.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join pagamento p on p.liquidacao_id = l.id and p.datapagamento between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join pagamentoestorno pe on pe.pagamento_id = p.id and pe.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'PROCESSADOS' and p.status <> 'ABERTO' ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" ) ");
        Query q = em.createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getSingleResult();
        }
    }

    public RelatorioDemonstrativoDisponibilidadeRecursoFacade getRelatorioDDRFacade() {
        return relatorioDDRFacade;
    }
}

