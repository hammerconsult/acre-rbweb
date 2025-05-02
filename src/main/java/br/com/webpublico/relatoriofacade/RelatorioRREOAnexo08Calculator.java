package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.negocios.RelatorioRREOAnexo12Calculator2018;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 30/10/13
 * Time: 11:01
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Deprecated
public class RelatorioRREOAnexo08Calculator extends RelatorioRREOAnexo12Calculator2018 {

    public BigDecimal calculaValor(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        setExercicioCorrente(exercicioCorrente);
        BigDecimal valor = calculaValorImpl(itemDemonstrativo, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaValorImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calculaValorImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calculaValorNaoRecursivo(formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calculaValorNaoRecursivo(formula, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calculaValorImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calculaValorNaoRecursivo(formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calculaValorNaoRecursivo(formula, relatoriosItemDemonst));
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
    public BigDecimal calculaValorNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT coalesce(sum(emp.valor), 0) as valor  FROM EMPENHO EMP " +
                " INNER JOIN UNIDADEORGANIZACIONAL UO ON EMP.UNIDADEORGANIZACIONAL_ID = UO.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID " +
                " INNER JOIN PROVISAOPPADESPESA PROVDESP ON DESP.PROVISAOPPADESPESA_ID = PROVDESP.ID " +
                " inner join provisaoppafonte ppf on PROVDESP.id = ppf.provisaoppadespesa_id " +
                " inner JOIN CONTADEDESTINACAO CD ON CD.ID = PPF.DESTINACAODERECURSOS_ID " +
                " inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id " +
                " INNER JOIN SUBACAOPPA SUB ON PROVDESP.SUBACAOPPA_ID = SUB.ID " +
                " INNER JOIN ACAOPPA AC ON SUB.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN CONTA C ON PROVDESP.CONTADEDESPESA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " where DESP.EXERCICIO_ID = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", getExercicioCorrente().getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calculaValorDestinacao(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        setExercicioCorrente(exercicioCorrente);
        BigDecimal valor = calculaValorDestinacaoImpl(itemDemonstrativo, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaValorDestinacaoImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calculaValorDestinacaoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calculaValorDestinacaoNaoRecursivo(formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calculaValorDestinacaoNaoRecursivo(formula, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calculaValorDestinacaoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calculaValorDestinacaoNaoRecursivo(formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calculaValorDestinacaoNaoRecursivo(formula, relatoriosItemDemonst));
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
    public BigDecimal calculaValorDestinacaoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT coalesce(sum(emp.valor), 0) as valor  FROM EMPENHO EMP " +
                " INNER JOIN UNIDADEORGANIZACIONAL UO ON EMP.UNIDADEORGANIZACIONAL_ID = UO.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID " +
                " INNER JOIN PROVISAOPPADESPESA PROVDESP ON DESP.PROVISAOPPADESPESA_ID = PROVDESP.ID " +
                " inner join provisaoppafonte ppf on PROVDESP.id = ppf.provisaoppadespesa_id " +
                " inner JOIN CONTADEDESTINACAO CD ON CD.ID = PPF.DESTINACAODERECURSOS_ID " +
                " inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id " +
                " INNER JOIN SUBACAOPPA SUB ON PROVDESP.SUBACAOPPA_ID = SUB.ID " +
                " INNER JOIN ACAOPPA AC ON SUB.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN CONTA C ON CD.ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " where DESP.EXERCICIO_ID = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", getExercicioCorrente().getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calculaValorRestoDestinacao(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        setExercicioCorrente(exercicioCorrente);
        BigDecimal valor = calculaValorRestoDestinacaoImpl(itemDemonstrativo, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaValorRestoDestinacaoImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calculaValorRestoDestinacaoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calculaValorRestoDestinacaoNaoRecursivo(formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calculaValorRestoDestinacaoNaoRecursivo(formula, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calculaValorRestoDestinacaoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calculaValorRestoDestinacaoNaoRecursivo(formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calculaValorRestoDestinacaoNaoRecursivo(formula, relatoriosItemDemonst));
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
    public BigDecimal calculaValorRestoDestinacaoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT coalesce(sum(emp.valor), 0) as valor  FROM EMPENHO EMP " +
                " INNER JOIN UNIDADEORGANIZACIONAL UO ON EMP.UNIDADEORGANIZACIONAL_ID = UO.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID " +
                " INNER JOIN PROVISAOPPADESPESA PROVDESP ON DESP.PROVISAOPPADESPESA_ID = PROVDESP.ID " +
                " inner join provisaoppafonte ppf on PROVDESP.id = ppf.provisaoppadespesa_id " +
                " inner JOIN CONTADEDESTINACAO CD ON CD.ID = PPF.DESTINACAODERECURSOS_ID " +
                " inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id " +
                " INNER JOIN SUBACAOPPA SUB ON PROVDESP.SUBACAOPPA_ID = SUB.ID " +
                " INNER JOIN ACAOPPA AC ON SUB.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN CONTA C ON CD.ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " where DESP.EXERCICIO_ID = :EXERCICIO AND EMP.CATEGORIAORCAMENTARIA = 'RESTO' ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", getExercicioCorrente().getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
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
                                totalAdicao = calcularSql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1));
                            } else {
                                totalAdicao = calcularSql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst);
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
                                totalSubtracao = calcularSql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1));
                            } else {
                                totalSubtracao = calcularSql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst);
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.subtract(totalSubtracao));
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
            .append(" select max(datasaldo) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
            .append(" where datasaldo <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') ")
            .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
            .append(" ) ms on ")
            .append(" s.datasaldo = ms.datasaldo ")
            .append(" and s.contacontabil_id = ms.contacontabil_id ")
            .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
            .append(" and s.tipobalancete = ms.tipobalancete ")
            .append(!dataFinal.substring(0, 5).equals("31/12") ? " where s.tipobalancete in ('MENSAL') " : "where s.tipobalancete in ('MENSAL', 'APURACAO')")
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
            .append(" select max(datasaldo) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
            .append(" where datasaldo <= to_date(:DATAFINAL,'dd/MM/yyyy') ")
            .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
            .append(" ) ms on ")
            .append(" s.datasaldo = ms.datasaldo ")
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

    public BigDecimal calcularItem48(ItemDemonstrativo itemDemonstrativo, String dataInicial, String dataFinal, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal valor = calcularItem48Impl(itemDemonstrativo, dataInicial, dataFinal, exercicio, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularItem48Impl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularItem48Impl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, exercicio, relatoriosItemDemonst));
                    } else {
                        if (componente instanceof ComponenteFormulaConta) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularItem48Sql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst, ((ComponenteFormulaConta) componente).getConta()).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularItem48Sql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst, ((ComponenteFormulaConta) componente).getConta()));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularItem48Impl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, exercicio, relatoriosItemDemonst));
                    } else {

                        if (componente instanceof ComponenteFormulaConta) {
                            if (itDemonstrativo.getInverteSinal()) {
                                if (((ComponenteFormulaConta) componente).getConta().getCodigo().startsWith("7")) {
                                    totalSubtracao = totalSubtracao.add(calcularSaldoInicialItem48Sql(dataInicial, exercicio, formula, relatoriosItemDemonst, ((ComponenteFormulaConta) componente).getConta()).multiply(new BigDecimal(-1)));
                                } else {
                                    totalSubtracao = totalSubtracao.subtract(calcularItem48Sql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst, ((ComponenteFormulaConta) componente).getConta()).multiply(new BigDecimal(-1)));
                                }
                            } else {
                                if (((ComponenteFormulaConta) componente).getConta().getCodigo().startsWith("7")) {
                                    totalSubtracao = totalSubtracao.add(calcularSaldoInicialItem48Sql(dataInicial, exercicio, formula, relatoriosItemDemonst, ((ComponenteFormulaConta) componente).getConta()));
                                } else {
                                    totalSubtracao = totalSubtracao.subtract(calcularItem48Sql(dataInicial, dataFinal, exercicio, formula, relatoriosItemDemonst, ((ComponenteFormulaConta) componente).getConta()));
                                }
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.subtract(totalSubtracao));
        return total;
    }

    public BigDecimal calcularSaldoInicial(ItemDemonstrativo itemDemonstrativo, String dataInicial, String dataFinal, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal valor = calcularSaldoInicialImpl(itemDemonstrativo, dataInicial, dataFinal, exercicio, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularSaldoInicialImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularSaldoInicialImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, exercicio, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularSaldoInicialSql(dataInicial, exercicio, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularSaldoInicialSql(dataInicial, exercicio, formula, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularSaldoInicialImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, exercicio, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularSaldoInicialSql(dataInicial, exercicio, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularSaldoInicialSql(dataInicial, exercicio, formula, relatoriosItemDemonst));
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
    private BigDecimal calcularSaldoInicialSql(String dataInicial, Exercicio exercicio, FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " select COALESCE(sum(SALDOINICIAL.TOTALCREDITO), 0) - COALESCE(sum(SALDOINICIAL.TOTALDEBITO), 0) AS SALDOfinal from ( "
                + " SELECT COALESCE(SUM(scc.totalcredito), 0) as totalcredito, COALESCE(SUM(scc.totaldebito), 0) AS totaldebito, c.id as conta "
                + " FROM SALDOCONTACONTABIL SCC   "
                + " INNER JOIN CONTA C ON SCC.CONTACONTABIL_ID = C.ID "
                + " inner join planodecontasexercicio pce on c.PLANODECONTAS_ID = pce.PLANOCONTABIL_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on scc.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " where pce.EXERCICIO_ID = :exercicio  "
                + " and scc.datasaldo = (SELECT MAX(sld.DATASALDO) AS maxdata "
                + "    FROM SALDOCONTACONTABIL sld "
                + "    WHERE sld.DATASALDO <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') "
                + "    and sld.unidadeorganizacional_id = scc.unidadeorganizacional_id and "
                + "      sld.contacontabil_id = scc.contacontabil_id) "
                + " ) saldoinicial ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(1);
//        new Util().imprimeSQL(sql, q);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularSaldoInicialItem48Sql(String dataInicial, Exercicio exercicio, FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, Conta c) {
        BigDecimal total;
        String sql = " select COALESCE(sum(SALDOINICIAL.TOTALCREDITO), 0) - COALESCE(sum(SALDOINICIAL.TOTALDEBITO), 0) AS SALDOfinal from ( "
                + " SELECT COALESCE(SUM(scc.totalcredito), 0) as totalcredito, COALESCE(SUM(scc.totaldebito), 0) AS totaldebito, c.id as conta "
                + " FROM SALDOCONTACONTABIL SCC   "
                + " INNER JOIN CONTA C ON SCC.CONTACONTABIL_ID = C.ID "
                + " inner join planodecontasexercicio pce on c.PLANODECONTAS_ID = pce.PLANOCONTABIL_ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on scc.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " where pce.EXERCICIO_ID = :exercicio  and c.codigo like :conta  "
                + " and scc.datasaldo = (SELECT MAX(sld.DATASALDO) AS maxdata "
                + "    FROM SALDOCONTACONTABIL sld "
                + "    WHERE sld.DATASALDO <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') "
                + "    and sld.unidadeorganizacional_id = scc.unidadeorganizacional_id and "
                + "      sld.contacontabil_id = scc.contacontabil_id) "
                + " ) saldoinicial ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("conta", c.getCodigoSemZerosAoFinal() + "%");
        q.setMaxResults(1);
//        new Util().imprimeSQL(sql, q);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularItem48Sql(String dataInicial, String dataFinal, Exercicio exercicio, FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, Conta conta) {
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
                .append(" select max(datasaldo) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
                .append(" where datasaldo <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY')  ")
                .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
                .append(" ) ms on ")
                .append(" s.datasaldo = ms.datasaldo ")
                .append(" and s.contacontabil_id = ms.contacontabil_id ")
                .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
                .append(" and s.tipobalancete = ms.tipobalancete ")
                .append(" where s.tipobalancete in ('ABERTURA', 'TRANSPORTE','MENSAL') ")
                .append("  and c.codigo like :conta ")
                .append("  and c.exercicio_id = :exercicio ")
                .append(" union all ")
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
                .append(" select max(datasaldo) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
                .append(" where datasaldo <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') ")
                .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
                .append(" ) ms on ")
                .append(" s.datasaldo = ms.datasaldo ")
                .append(" and s.contacontabil_id = ms.contacontabil_id ")
                .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
                .append(" and s.tipobalancete = ms.tipobalancete ")
                .append(!dataFinal.substring(0, 5).equals("31/12") ? " where s.tipobalancete in ('MENSAL') " : "where s.tipobalancete in ('MENSAL', 'APURACAO')")
                .append("  and c.codigo like :conta ")
                .append("  and c.exercicio_id = :exercicio ")
                .append(" union all ")
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
                .append(" select max(datasaldo) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
                .append(" where datasaldo <= to_date(:DATAFINAL,'dd/MM/yyyy') ")
                .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
                .append(" ) ms on ")
                .append(" s.datasaldo = ms.datasaldo ")
                .append(" and s.contacontabil_id = ms.contacontabil_id ")
                .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
                .append(" and s.tipobalancete = ms.tipobalancete ")
                .append(!dataFinal.substring(0, 5).equals("31/12") ? " where s.tipobalancete in ('MENSAL') " : "where s.tipobalancete in ('MENSAL', 'APURACAO')")
                .append("  and c.codigo like :conta ")
                .append("  and c.exercicio_id = :exercicio ")
                .append(" )) ");
//        if (relatoriosItemDemonst.getUsaConta()) {
//            sql.append(recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class));
//        }

//        String sql = " select coalesce(sum((COALESCE(SALDOINICIAL.TOTALCREDITO, 0) - COALESCE(SALDOINICIAL.TOTALDEBITO, 0)) - (COALESCE(SALDOFINAL.TOTALDEBITO,0) - COALESCE(saldoinicial.totaldebito,0)) + (COALESCE(saldofinal.TOTALCREDITO,0) - COALESCE(saldoinicial.totalcredito,0))), 0) AS SALDOfinal from ( "
//                + " SELECT COALESCE(SUM(scc.totalcredito), 0) as totalcredito, COALESCE(SUM(scc.totaldebito), 0) AS totaldebito, c.id as conta "
//                + " FROM SALDOCONTACONTABIL SCC "
//                + " INNER JOIN CONTA C ON SCC.CONTACONTABIL_ID = C.ID "
//                + " inner join planodecontasexercicio pce on c.PLANODECONTAS_ID = pce.PLANOCONTABIL_ID ";
//        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
//            sql += "inner join unidadeorganizacional UO on scc.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
//        }
//        sql += " where pce.EXERCICIO_ID = :exercicio and c.codigo like :conta "
//                + " and scc.datasaldo = (SELECT MAX(sld.DATASALDO) AS maxdata "
//                + "    FROM SALDOCONTACONTABIL sld "
//                + "    WHERE sld.DATASALDO <= TO_DATE(:DATAFINAL, 'DD/MM/YYYY') "
//                + "    and sld.unidadeorganizacional_id = scc.unidadeorganizacional_id and "
//                + "      sld.contacontabil_id = scc.contacontabil_id) "
//                + "  group by c.id"
//                + "  ) saldofinal left join ( "
//                + " SELECT COALESCE(SUM(scc.totalcredito), 0) as totalcredito, COALESCE(SUM(scc.totaldebito), 0) AS totaldebito, c.id as conta "
//                + " FROM SALDOCONTACONTABIL SCC   "
//                + " INNER JOIN CONTA C ON SCC.CONTACONTABIL_ID = C.ID "
//                + " inner join planodecontasexercicio pce on c.PLANODECONTAS_ID = pce.PLANOCONTABIL_ID ";
//        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
//            sql += "inner join unidadeorganizacional UO on scc.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
//        }
//        sql += " where pce.EXERCICIO_ID = :exercicio  and c.codigo like :conta  "
//                + " and scc.datasaldo = (SELECT MAX(sld.DATASALDO) AS maxdata "
//                + "    FROM SALDOCONTACONTABIL sld "
//                + "    WHERE sld.DATASALDO <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') "
//                + "    and sld.unidadeorganizacional_id = scc.unidadeorganizacional_id and "
//                + "      sld.contacontabil_id = scc.contacontabil_id) "
//                + "  group by c.id"
//                + " ) saldoinicial on saldofinal.conta = saldoinicial.conta ";
        Query q = this.getEm().createNativeQuery(sql.toString());
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("conta", conta.getCodigoSemZerosAoFinal() + "%");
        q.setMaxResults(1);
//        new Util().imprimeSQL(sql, q);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

}
