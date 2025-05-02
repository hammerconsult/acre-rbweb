/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.negocios.ReferenciaAnualFacade;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author juggernaut
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioRREOAnexo05Calculator extends ItemDemonstrativoCalculator {

    @EJB
    private ReferenciaAnualFacade referenciaAnualFacade;

    public RelatorioRREOAnexo05Calculator() {
    }

    public BigDecimal calcular(ItemDemonstrativo itemDemonstrativo, String dataInicial, String dataFinal, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal valor = calcularImpl(itemDemonstrativo, dataInicial, dataFinal, exercicio, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, exercicio, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularSql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularSql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, exercicio, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularSql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularSql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst));
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
    private BigDecimal calcularSql(String dataInicial, String dataFinal, Exercicio exercicio, FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        StringBuilder sql = new StringBuilder();
        sql.append(" select ")
                .append(" coalesce(sum(coalesce(saldoanterior, 0) + coalesce(totalcredito, 0) - coalesce(totaldebito, 0) ),0) as saldofinal ")
                .append(" from ( ")
                .append(" select  ")
                .append(" sum(s.totalcredito - s.totaldebito) as saldoanterior, ")
                .append(" 0 as totalcredito, ")
                .append(" 0 as totaldebito ")
                .append(" from saldocontacontabil s ")
                .append(" inner join conta c on s.contacontabil_id = c.id ")
                .append(" inner join contacontabil cc on c.id = cc.id ");
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql.append("inner join unidadeorganizacional UO on s.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class));
        }
        sql.append(" inner join ( ")
                .append(" select trunc(max(datasaldo)) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
                .append(" where trunc(datasaldo) <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY')  ")
                .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
                .append(" ) ms on ")
                .append(" trunc(s.datasaldo) = trunc(ms.datasaldo) ")
                .append(" and s.contacontabil_id = ms.contacontabil_id ")
                .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
                .append(" and s.tipobalancete = ms.tipobalancete ")
                .append(" where s.tipobalancete in ('ABERTURA', 'TRANSPORTE','MENSAL') ")
                .append(" and c.exercicio_id = :exercicio ");
        if (relatoriosItemDemonst.getUsaConta()) {
            sql.append(recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class));
        }
        sql.append(" union all ")
                .append(" select  ")
                .append(" 0 as saldoanterior, ")
                .append(" sum(creditofinal - creditoanterior) as credito, ")
                .append(" sum(debitofinal - debitoanterior) as debito ")
                .append(" from ( ")
                .append(" select ")
                .append(" sum(s.totalcredito) as creditoanterior, ")
                .append(" sum(s.totaldebito) as debitoanterior, ")
                .append(" 0 as creditofinal, ")
                .append(" 0 as debitofinal ")
                .append(" from saldocontacontabil s ")
                .append(" inner join conta c on s.contacontabil_id = c.id ")
                .append(" inner join contacontabil cc on c.id = cc.id ");
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql.append("inner join unidadeorganizacional UO on s.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class));
        }
        sql.append(" inner join ( ")
                .append(" select trunc(max(datasaldo)) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
                .append(" where trunc(datasaldo) <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') ")
                .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
                .append(" ) ms on ")
                .append(" trunc(s.datasaldo) = trunc(ms.datasaldo) ")
                .append(" and s.contacontabil_id = ms.contacontabil_id ")
                .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
                .append(" and s.tipobalancete = ms.tipobalancete ")
                .append(!dataFinal.substring(0, 4).equals("31/12") ? " where s.tipobalancete in ('MENSAL') " : "where s.tipobalancete in ('MENSAL', 'APURACAO')")
                .append(" and c.exercicio_id = :exercicio ");
        if (relatoriosItemDemonst.getUsaConta()) {
            sql.append(recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class));
        }
        sql.append(" union all ")
                .append(" select ")
                .append(" 0 as creditoanterior, ")
                .append(" 0 as debitoanterior, ")
                .append(" sum(s.totalcredito) as creditofinal, ")
                .append(" sum(s.totaldebito) as debitofinal ")
                .append(" from saldocontacontabil s ")
                .append(" inner join conta c on s.contacontabil_id = c.id ")
                .append(" inner join contacontabil cc on c.id = cc.id ");
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql.append("inner join unidadeorganizacional UO on s.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class));
        }
        sql.append(" inner join ( ")
                .append(" select trunc(max(datasaldo)) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
                .append(" where trunc(datasaldo) <= to_date(:DATAFINAL,'dd/MM/yyyy') ")
                .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
                .append(" ) ms on ")
                .append(" trunc(s.datasaldo) = trunc(ms.datasaldo) ")
                .append(" and s.contacontabil_id = ms.contacontabil_id ")
                .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
                .append(" and s.tipobalancete = ms.tipobalancete ")
                .append(!dataFinal.substring(0, 5).equals("31/12") ? " where s.tipobalancete in ('MENSAL') " : "where s.tipobalancete in ('MENSAL', 'APURACAO')")
                .append(" and c.exercicio_id = :exercicio ");
        if (relatoriosItemDemonst.getUsaConta()) {
            sql.append(recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class));
        }
        sql.append(" )) ");
        Query q = this.getEm().createNativeQuery(sql.toString());
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularExAnterior(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal valor = calcularExAnteriorImpl(itemDemonstrativo, exercicio, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularExAnteriorImpl(ItemDemonstrativo itDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularExAnteriorImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicio, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularExercicioAnteriorSql(exercicio, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularExercicioAnteriorSql(exercicio, formula, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularExAnteriorImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicio, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularExercicioAnteriorSql(exercicio, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularExercicioAnteriorSql(exercicio, formula, relatoriosItemDemonst));
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
    private BigDecimal calcularExercicioAnteriorSql(Exercicio exercicio, FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        StringBuilder sql = new StringBuilder();
        sql.append(" select  ")
                .append(" coalesce(sum(s.totalcredito - s.totaldebito), 0) as saldoanterior ")
                .append(" from saldocontacontabil s ")
                .append(" inner join conta c on s.contacontabil_id = c.id ")
                .append(" inner join contacontabil cc on c.id = cc.id ");
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql.append("inner join unidadeorganizacional UO on s.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class));
        }
        sql.append(" inner join ( ")
                .append(" select max(datasaldo) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
                .append(" where datasaldo <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY')  ")
                .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
                .append(" ) ms on ")
                .append(" s.datasaldo = ms.datasaldo ")
                .append(" and s.contacontabil_id = ms.contacontabil_id ")
                .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
                .append(" and s.tipobalancete = ms.tipobalancete ")
                .append(" where s.tipobalancete in ('ABERTURA', 'TRANSPORTE','MENSAL') ")
                .append(" and c.exercicio_id = :exercicio ");
        if (relatoriosItemDemonst.getUsaConta()) {
            sql.append(recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class));
        }
        Query q = this.getEm().createNativeQuery(sql.toString());
        q.setParameter("DATAINICIAL", "01/01/" + exercicio.getAno());
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularExAnteriorAnexo2(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal valor = calcularExAnteriorAnexo2Impl(itemDemonstrativo, exercicio, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularExAnteriorAnexo2Impl(ItemDemonstrativo itDemonstrativo, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularExAnteriorAnexo2Impl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicio, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularExercicioAnteriorAnexo2Sql(exercicio, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularExercicioAnteriorAnexo2Sql(exercicio, formula, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularExAnteriorAnexo2Impl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicio, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularExercicioAnteriorAnexo2Sql(exercicio, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularExercicioAnteriorAnexo2Sql(exercicio, formula, relatoriosItemDemonst));
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
    private BigDecimal calcularExercicioAnteriorAnexo2Sql(Exercicio exercicio, FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        StringBuilder sql = new StringBuilder();
        sql.append(" select  ")
                .append(" coalesce(sum(s.totalcredito - s.totaldebito), 0) as saldoanterior ")
                .append(" from saldocontacontabil s ")
                .append(" inner join conta c on s.contacontabil_id = c.id ")
                .append(" inner join contacontabil cc on c.id = cc.id ");
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql.append("inner join unidadeorganizacional UO on s.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class));
        }
        sql.append(" inner join ( ")
                .append(" select max(datasaldo) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
                .append(" where datasaldo <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY')  ")
                .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
                .append(" ) ms on ")
                .append(" s.datasaldo = ms.datasaldo ")
                .append(" and s.contacontabil_id = ms.contacontabil_id ")
                .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
                .append(" and s.tipobalancete = ms.tipobalancete ")
                .append(" where s.tipobalancete in ('TRANSPORTE') ")
                .append(" and c.exercicio_id = :exercicio ");
        if (relatoriosItemDemonst.getUsaConta()) {
            sql.append(recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class));
        }
        Query q = this.getEm().createNativeQuery(sql.toString());
        q.setParameter("DATAINICIAL", "01/01/" + exercicio.getAno());
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularDivida(ItemDemonstrativo itemDemonstrativo, String dataInicial, String dataFinal, Integer ano, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal valor = calcularDividaImpl(itemDemonstrativo, dataInicial, dataFinal, ano, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDividaImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, Integer ano, RelatoriosItemDemonst relatoriosItemDemonst) {

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
                        total = total.add(calcularDividaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ano, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDividaSql(dataInicial, dataFinal, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularExercicioAnteriorDividaSql(ano, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDividaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ano, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDividaSql(dataInicial, dataFinal, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularExercicioAnteriorDividaSql(ano, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
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
    private BigDecimal calcularDividaSql(String dataInicial, String dataFinal, FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT (VALOR_FINAL - VALOR_INICIAL) AS VALOR_NO_BIMESTRE FROM "
                + " (SELECT 1 AS GRUPO, (COALESCE(SUM(A.TOTALCREDITO), 0) - COALESCE(SUM(A.TOTALDEBITO), 0)) AS VALOR_INICIAL "
                + " FROM SALDOCONTACONTABIL A "
                + " INNER JOIN CONTA B ON A.CONTACONTABIL_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN UNIDADEORGANIZACIONAL E ON A.UNIDADEORGANIZACIONAL_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN ENTIDADE ENT ON E.ENTIDADE_ID = ENT.ID "
                + " INNER JOIN (SELECT C.ID AS MAXDATA, "
                + " MIN(C.DATASALDO) AS MAXDATE "
                + " FROM SALDOCONTACONTABIL C "
                + " GROUP BY C.ID) "
                + " DATA ON DATA.MAXDATA = A.ID "
                + " WHERE TO_CHAR(A.DATASALDO, 'dd/MM/yyyy') >= TO_CHAR(TO_DATE(:DATAINICIAL, 'dd/MM/yyyy'),'dd/MM/yyyy') "
                + " AND ENT.CLASSIFICACAOUO = 'RPPS') INICIAL "
                + " INNER JOIN ( "
                + " SELECT 1 AS GRUPO, (COALESCE(SUM(A.TOTALCREDITO), 0) - COALESCE(SUM(A.TOTALDEBITO), 0)) AS VALOR_FINAL "
                + " FROM SALDOCONTACONTABIL A "
                + " INNER JOIN CONTA B ON A.CONTACONTABIL_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN UNIDADEORGANIZACIONAL E ON A.UNIDADEORGANIZACIONAL_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN ENTIDADE ENT ON E.ENTIDADE_ID = ENT.ID "
                + " INNER JOIN (SELECT C.ID AS MAXDATA, "
                + " MAX(C.DATASALDO) AS MAXDATE "
                + " FROM SALDOCONTACONTABIL C "
                + " GROUP BY C.ID) "
                + " DATA ON DATA.MAXDATA = A.ID "
                + " WHERE TO_CHAR(A.DATASALDO, 'dd/MM/yyyy') < TO_CHAR(TO_DATE(:DATAFINAL, 'dd/MM/yyyy'),'dd/MM/yyyy') "
                + " AND ENT.CLASSIFICACAOUO = 'RPPS') FINAL ON INICIAL.GRUPO = FINAL.GRUPO ";
        Query q = this.getEm().createNativeQuery(sql);
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularExercicioAnteriorDividaSql(Integer ano, FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT (COALESCE(SUM(A.TOTALCREDITO), 0) - COALESCE(SUM(A.TOTALDEBITO), 0)) AS VALOR "
                + " FROM SALDOCONTACONTABIL A "
                + " INNER JOIN CONTA B ON A.CONTACONTABIL_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN UNIDADEORGANIZACIONAL E ON A.UNIDADEORGANIZACIONAL_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN ENTIDADE ENT ON E.ENTIDADE_ID = ENT.ID "
                + " INNER JOIN (SELECT C.ID AS MAXDATA, "
                + " MAX(C.DATASALDO) AS MAXDATE "
                + " FROM SALDOCONTACONTABIL C "
                + " GROUP BY C.ID) "
                + " DATA ON A.ID = DATA.MAXDATA "
                + " WHERE TO_CHAR(A.DATASALDO, 'yyyy') = TO_CHAR(TO_DATE(:ANO, 'yyyy'),'yyyy') "
                + " AND TO_CHAR(A.DATASALDO, 'dd/MM/yyyy') <= TO_CHAR(TO_DATE(:DATAFINAL, 'dd/MM/yyyy'),'dd/MM/yyyy') "
                + " AND ENT.CLASSIFICACAOUO = 'RPPS' ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("DATAFINAL", "31/12/" + (ano - 1));
        q.setParameter("ANO", ano);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public ReferenciaAnualFacade getReferenciaAnualFacade() {
        return referenciaAnualFacade;
    }
}
