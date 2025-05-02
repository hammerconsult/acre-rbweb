package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 26/12/13
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Deprecated
public class RelatorioRREOAnexo04Calculator extends ItemDemonstrativoCalculator {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private ItemDemonstrativo itemDemonstrativo;
    private Exercicio exercicioCorrente;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;

    public RelatorioRREOAnexo04Calculator() {
    }

    public BigDecimal calcularPrevisaoInicialAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal previsaoInicial = calcularPrevisaoInicialAlteradoImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return previsaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularPrevisaoInicialAlteradoImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = itemDemonstrativoFacade.recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularPrevisaoInicialAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                total = total.add(calcularPrevisaoInicialAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                total = total.add(calcularPrevisaoInicialAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularPrevisaoInicialAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                total = total.subtract(calcularPrevisaoInicialAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                total = total.subtract(calcularPrevisaoInicialAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularPrevisaoInicialAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql =
                "   SELECT " +
                        "     COALESCE(SUM(A.VALOR),0) AS VALOR " +
                        "   FROM RECEITALOA A ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON A.ID = RLF.RECEITALOA_ID ";
            sql += " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID ";
            sql += " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON A.ENTIDADE_ID = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA B ON A.CONTADERECEITA_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA C ON A.LOA_ID = C.ID "
                + " INNER JOIN LDO D ON C.LDO_ID = D.ID "
                + " WHERE D.EXERCICIO_ID = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }


    public BigDecimal calcularPrevisaoAtualizadaAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal previsaoAtualizada = calcularPrevisaoAtualizadaAlteradoImpl(itemDemonstrativo, dataInicial, dataFinal, relatoriosItemDemonst);
        return previsaoAtualizada;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularPrevisaoAtualizadaAlteradoImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = itemDemonstrativoFacade.recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularPrevisaoAtualizadaAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                total = total.add(calcularPrevisaoAtualizadaAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                total = total.add(calcularPrevisaoAtualizadaAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularPrevisaoAtualizadaAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                total = total.subtract(calcularPrevisaoAtualizadaAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                total = total.subtract(calcularPrevisaoAtualizadaAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularPrevisaoAtualizadaAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "SELECT "
                + "  COALESCE(SUM( "
                + "  COALESCE( "
                + "  case when TIPOALT = 'ANULACAO' then VALOR * (-1) else VALOR end "
                + "  , 0) "
                + "  ), 0) AS VALOR "
                + "  FROM( "
                + "    SELECT "
                + "      RE.TIPOALTERACAOORC AS TIPOALT, "
                + "      RE.VALOR AS VALOR "
                + "    FROM ALTERACAOORC ALT "
                + "      INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID "
                + "      INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN RECEITALOAFONTE RLF ON RLOA.ID = RLF.RECEITALOA_ID "
                    + " INNER JOIN CONTADEDESTINACAO CD ON CD.ID = RLF.DESTINACAODERECURSOS_ID "
                    + " INNER JOIN FONTEDERECURSOS FR ON CD.FONTEDERECURSOS_ID = FR.ID " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON RLOA.ENTIDADE_ID = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA B ON RLOA.CONTADERECEITA_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA L ON RLOA.LOA_ID = L.ID "
                + " INNER JOIN LDO LD ON L.LDO_ID = LD.ID "
                + "WHERE LD.EXERCICIO_ID = :exercicio "
                + " AND trunc(ALT.DATAALTERACAO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') "
                + "UNION ALL "
                + "  SELECT "
                + "    'normal' AS TIPOALT,"
                + "    RLOA.VALOR AS VALOR "
                + "  FROM RECEITALOA RLOA ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN RECEITALOAFONTE RLF ON RLOA.ID = RLF.RECEITALOA_ID "
                    + " INNER JOIN CONTADEDESTINACAO CD ON CD.ID = RLF.DESTINACAODERECURSOS_ID "
                    + " INNER JOIN FONTEDERECURSOS FR ON CD.FONTEDERECURSOS_ID = FR.ID " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON RLOA.ENTIDADE_ID = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA B ON RLOA.CONTADERECEITA_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA L ON RLOA.LOA_ID = L.ID "
                + " INNER JOIN LDO LD ON L.LDO_ID = LD.ID "
                + "WHERE LD.EXERCICIO_ID = :exercicio "
                + " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("exercicio", this.exercicioCorrente.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularSaldo(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal despesaDotacaoInicial = calcularSaldoImpl(itDemonstrativo, dataInicial, dataFinal, relatoriosItemDemonst);
        return despesaDotacaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularSaldoImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularSaldoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularSaldoQuery(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularSaldoQuery(formula, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularSaldoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularSaldoQuery(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularSaldoQuery(formula, dataInicial, dataFinal, relatoriosItemDemonst));
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
    public BigDecimal calcularSaldoQuery(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " select coalesce(sum(saldo.totaldebito - saldo.totalcredito), 0) as valor from saldocontacontabil saldo " +
                " inner join conta c on saldo.contacontabil_id = c.id ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "c", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON saldo.unidadeorganizacional_id  = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " inner join (SELECT trunc(MAX(sld.datasaldo)) as maiorData,  sld.unidadeorganizacional_id as unidade, sld.contacontabil_id as conta " +
                "  FROM saldocontacontabil sld " +
                " inner join conta c on sld.contacontabil_id = c.id ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "c", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON sld.unidadeorganizacional_id = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += "  WHERE trunc(sld.datasaldo) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') " +
                "  group by sld.unidadeorganizacional_id ,sld.contacontabil_id " +
                "  ) datas on saldo.unidadeorganizacional_id = datas.unidade and saldo.contacontabil_id = datas.conta and saldo.datasaldo = datas.maiordata " +
                " where saldo.unidadeorganizacional_id = datas.unidade and saldo.contacontabil_id = datas.conta and trunc(saldo.datasaldo) = trunc(datas.maiordata) ";
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


    public BigDecimal calcularReserva(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal despesaDotacaoInicial = calcularReservaImpl(itDemonstrativo, dataInicial, dataFinal, relatoriosItemDemonst);
        return despesaDotacaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularReservaImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularReservaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularReservaQuery(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularReservaQuery(formula, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularReservaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularReservaQuery(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularReservaQuery(formula, dataInicial, dataFinal, relatoriosItemDemonst));
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
    public BigDecimal calcularReservaQuery(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " select (coalesce(sum(saldo.dotacao), 0) + coalesce(sum(saldo.alteracao), 0) - coalesce(sum(saldo.empenhado), 0) - coalesce(sum(saldo.reservado), 0) - coalesce(sum(saldo.reservadoporlicitacao), 0)) from saldofontedespesaorc saldo " +
                " inner join fontedespesaorc fonte on saldo.fontedespesaorc_id = fonte.id " +
                " inner join despesaorc desp on fonte.despesaorc_id = desp.id " +
                " inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id " +
                " inner join conta c on provdesp.contadedespesa_id = c.id ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "c", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON saldo.unidadeorganizacional_id = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += "  inner join (select trunc(max(sld.datasaldo)) as data, sld.fontedespesaorc_id as fonte, sld.unidadeorganizacional_id as unidade  " +
                " from saldofontedespesaorc sld " +
                " inner join fontedespesaorc fonte on sld.fontedespesaorc_id = fonte.id " +
                " inner join despesaorc desp on fonte.despesaorc_id = desp.id " +
                " inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id " +
                " inner join conta c on provdesp.contadedespesa_id = c.id ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "c", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON sld.unidadeorganizacional_id = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " where trunc(sld.datasaldo) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  " +
                " group by sld.fontedespesaorc_id, sld.unidadeorganizacional_id) maiorData on trunc(maiordata.data) = trunc(saldo.DATASALDO) and maiordata.unidade = saldo.unidadeorganizacional_id and maiordata.fonte = saldo.fontedespesaorc_id " +
                " where trunc(saldo.datasaldo) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') ";
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

    public ItemDemonstrativo getItemDemonstrativo() {
        return itemDemonstrativo;
    }

    public void setItemDemonstrativo(ItemDemonstrativo itemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
    }

    public Exercicio getExercicioCorrente() {
        return exercicioCorrente;
    }

    public void setExercicioCorrente(Exercicio exercicioCorrente) {
        this.exercicioCorrente = exercicioCorrente;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
