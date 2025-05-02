package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoFormula;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Mateus on 04/09/2014.
 */
@Stateless
public class RelatorioRGFAnexo05Calculator2017 extends ItemDemonstrativoCalculator implements Serializable {
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
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" AND E.DATAEMPENHO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT  sum(coalesce(est.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join EMPENHOESTORNO est on EST.EMPENHO_ID = e.id and est.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" union all    ")
            .append(" SELECT  sum(coalesce(l.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID and l.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" AND E.DATAEMPENHO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(concatenaParametros(parametros, 2, " and "))
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
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" ) ");
        Query q = getEm().createNativeQuery(sql.toString());
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }

        }
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
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" AND E.DATAEMPENHO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS' ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT  sum(coalesce(est.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join EMPENHOESTORNO est on EST.EMPENHO_ID = e.id and est.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS' ")
            .append(concatenaParametros(parametros, 2, " and "))
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
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS' and p.status <> 'ABERTO' ")
            .append(concatenaParametros(parametros, 2, " and "))
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
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS' and p.status <> 'ABERTO' ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" ) ");
        Query q = getEm().createNativeQuery(sql.toString());
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getSingleResult();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularRestoLiquidadosNaoPagosProcessados(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, List<ParametrosRelatorios> parametros) {
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
                        total = total.add(calcularRestoLiquidadosNaoPagosProcessados(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, parametros));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(saldoRestoLiquidadosNaoPagosProcessados(formula, relatoriosItemDemonst, parametros).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(saldoRestoLiquidadosNaoPagosProcessados(formula, relatoriosItemDemonst, parametros));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestoLiquidadosNaoPagosProcessados(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, parametros));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(saldoRestoLiquidadosNaoPagosProcessados(formula, relatoriosItemDemonst, parametros).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(saldoRestoLiquidadosNaoPagosProcessados(formula, relatoriosItemDemonst, parametros));
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
    public BigDecimal saldoRestoLiquidadosNaoPagosProcessados(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(valor), 0) as saldo from (  ")
            .append(" SELECT  sum(coalesce(l.valor,0)) as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" AND E.DATAEMPENHO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" and l.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT  sum(coalesce(le.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID  ")
            .append(" inner join liquidacaoestorno le on le.liquidacao_id = l.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" and l.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" and le.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT sum(coalesce(p.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID  ")
            .append(" inner join pagamento p on p.liquidacao_id = l.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO) ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" and l.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" and p.datapagamento between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') and p.status <> 'ABERTO'  ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT sum(coalesce(pe.valor,0)) as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join LIQUIDACAO l on e.id = L.EMPENHO_ID  ")
            .append(" inner join pagamento p on p.liquidacao_id = l.id ")
            .append(" inner join pagamentoestorno pe on pe.pagamento_id = p.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'NORMAL'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio  ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" and l.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" and p.datapagamento between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" and pe.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') and p.status <> 'ABERTO'  ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" ) ");
        Query q = getEm().createNativeQuery(sql.toString());
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
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
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" AND E.DATAEMPENHO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'PROCESSADOS' ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" union all  ")
            .append(" SELECT  sum(coalesce(est.valor,0)) * -1 as valor   ")
            .append(" FROM EMPENHO E  ")
            .append(" inner join EMPENHOESTORNO est on EST.EMPENHO_ID = e.id and est.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(" inner join fontedespesaorc fdo on e.fontedespesaorc_id = fdo.id ")
            .append(" inner join provisaoppafonte ppf on fdo.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" inner join fontederecursos font on cd.fontederecursos_id = font.id ")
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'PROCESSADOS' ")
            .append(concatenaParametros(parametros, 2, " and "))
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
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'PROCESSADOS' and p.status <> 'ABERTO' ")
            .append(concatenaParametros(parametros, 2, " and "))
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
            .append(relatoriosItemDemonst.getUsaFonteRecurso() ? recuperaIds(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class) : "")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id ")
            .append(" INNER JOIN unidadeorganizacional und ON e.unidadeorganizacional_id = und.id ")
            .append(relatoriosItemDemonst.getUsaUnidadeOrganizacional() ? recuperaIds(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class) : "")
            .append(" WHERE E.CATEGORIAORCAMENTARIA = 'RESTO'  ")
            .append(" AND E.EXERCICIO_ID = :exercicio ")
            .append(" and e.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')     ")
            .append(" AND E.DATAEMPENHO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, E.DATAEMPENHO)  ")
            .append(" and E.TIPORESTOSPROCESSADOS = 'PROCESSADOS' and p.status <> 'ABERTO' ")
            .append(concatenaParametros(parametros, 2, " and "))
            .append(" ) ");
        Query q = getEm().createNativeQuery(sql.toString());
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getSingleResult();
        }
    }
}
