/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.ItemDemonstrativoCalculator;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo03Calculator;

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
 * @author major
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioRGFAnexo03Calculator2017 extends ItemDemonstrativoCalculator implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private Exercicio exercicioCorrente;
    private ItemDemonstrativo itemDemonstrativo;

//    public BigDecimal calcularExercicioAnterior(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente) {
//        this.itemDemonstrativo = itemDemonstrativo;
//        this.exercicioCorrente = exercicioCorrente;
//        BigDecimal liquidacao = calcularExercicioAnteriorImpl(this.itemDemonstrativo, exercicioCorrente);
//        return liquidacao;
//    }
//
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
//    private BigDecimal calcularExercicioAnteriorImpl(ItemDemonstrativo itDemonstrativo, Exercicio exercicioCorrente) {
//        BigDecimal total = BigDecimal.ZERO;
//        itDemonstrativo = this.getEm().merge(itDemonstrativo);
//        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
//        for (FormulaItemDemonstrativo formula : formulas) {
//            List<ComponenteFormula> componentes = formula.getComponenteFormula();
//            for (ComponenteFormula componente : componentes) {
//                if (componente instanceof ComponenteFormulaItem) {
//                    if (componente.getOperacaoFormula() == OperacaoFormula.ADICAO) {
//                        total = total.add(calcularExercicioAnteriorImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicioCorrente));
//                    } else {
//                        total = total.subtract(calcularExercicioAnteriorImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicioCorrente));
//                    }
//                } else {
//                    if (((ComponenteFormulaConta) componente).getConta() != null) {
//                        if (componente.getOperacaoFormula() == OperacaoFormula.ADICAO) {
//                            total = total.add(calculaExercicioAnteriorSql(((ComponenteFormulaConta) componente).getConta(), exercicioCorrente));
//                        } else {
//                            total = total.subtract(calculaExercicioAnteriorSql(((ComponenteFormulaConta) componente).getConta(), exercicioCorrente));
//                        }
//                    }
//                }
//            }
//        }
//        return total;
//    }
//
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
//    public BigDecimal calculaExercicioAnteriorSql(Conta conta, Exercicio ex) {
//        BigDecimal total;
//        String sql = " SELECT SUM(CREDITO_MAIOR - DEBITO_MAIOR) AS valor FROM ( "
//                + " SELECT COALESCE(SUM(SALDO.TOTALDEBITO),0) AS DEBITO_MAIOR,  "
//                + " COALESCE(SUM(SALDO.TOTALCREDITO),0) AS CREDITO_MAIOR "
//                + " FROM SALDOCONTACONTABIL SALDO "
//                + " INNER JOIN CONTA C ON SALDO.CONTACONTABIL_ID = C.ID "
//                + " WHERE TO_CHAR(SALDO.DATASALDO,'DD/MM/YYYY') IN ( SELECT MAX(TO_CHAR(SALDO.DATASALDO,'DD/MM/YYYY')) FROM SALDOCONTACONTABIL SALDO WHERE SALDO.DATASALDO <= to_date('31/12/" + (ex.getAno() - 1) + "', 'dd/mm/yyyy') GROUP BY SALDO.UNIDADEORGANIZACIONAL_ID) "
//                + " AND C.CODIGO LIKE :CONTA "
//                + " ) ITENS ";
//        Query q = em.createNativeQuery(sql);
//        q.setParameter("CONTA", conta.getCodigoSemZerosAoFinal() + "%");
//        q.setMaxResults(1);
//        if (!q.getResultList().isEmpty()) {
//            total = (BigDecimal) q.getResultList().get(0);
//        } else {
//            total = BigDecimal.ZERO;
//        }
//        return total;
//    }

    public BigDecimal calcularQuadrimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal valor = calcularQuadrimestreImpl(itemDemonstrativo, dataInicial, dataFinal, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularQuadrimestreImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = itemDemonstrativoFacade.recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularQuadrimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularQuadrimestreSql(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularQuadrimestreSql(formula, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularQuadrimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularQuadrimestreSql(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularQuadrimestreSql(formula, dataInicial, dataFinal, relatoriosItemDemonst));
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
    private BigDecimal calcularQuadrimestreSql(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT sum(VALOR) FROM (     " +
                " SELECT COALESCE(SUM(lr.VALOR), 0) AS VALOR FROM LANCAMENTORECEITAORC LR " +
                " INNER JOIN RECEITALOA RE ON LR.RECEITALOA_ID = RE.ID " +
                " INNER JOIN LOA L ON RE.LOA_ID = L.ID " +
                " INNER JOIN LDO LD ON L.LDO_ID = LD.ID " +
                " INNER JOIN EXERCICIO e ON LD.EXERCICIO_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON RE.ID = RLF.RECEITALOA_ID "
                    + " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID "
                    + " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RE.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA CONT ON  RE.CONTADERECEITA_ID = CONT.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "CONT", ComponenteFormulaConta.class);
        }
        sql += " WHERE LR.DATALANCAMENTO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') AND e.id = :exercicio " +
                " union all " +
                " SELECT COALESCE(SUM(lre.VALOR), 0) * - 1 AS VALOR FROM RECEITAORCESTORNO LRE " +
                " INNER JOIN LANCAMENTORECEITAORC LR ON LR.ID = LRE.LANCAMENTORECEITAORC_ID " +
                " INNER JOIN RECEITALOA RE ON LR.RECEITALOA_ID = RE.ID " +
                " INNER JOIN LOA L ON RE.LOA_ID = L.ID " +
                " INNER JOIN LDO LD ON L.LDO_ID = LD.ID " +
                " INNER JOIN EXERCICIO e ON LD.EXERCICIO_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON RE.ID = RLF.RECEITALOA_ID "
                    + " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID "
                    + " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RE.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA CONT ON  RE.CONTADERECEITA_ID = CONT.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "CONT", ComponenteFormulaConta.class);
        }
        sql += " WHERE LR.DATALANCAMENTO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') AND e.id = :exercicio " +
                " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("exercicio", this.exercicioCorrente.getId());
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

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }
}
