package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.relatoriofacade.ItemDemonstrativoCalculator;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

/**
 * @author juggernaut
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Deprecated
public class RelatorioRREOAnexo12Calculator2018 extends ItemDemonstrativoCalculator {

    private ItemDemonstrativo itemDemonstrativo;
    private Exercicio exercicioCorrente;
    private String dataInicial;
    private String dataFinal;
    @EJB
    private ContaFacade contaFacade;

    public RelatorioRREOAnexo12Calculator2018() {
    }

    public BigDecimal calcularPrevisaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal previsaoInicial = calcularPrevisaoInicialImpl(itemDemonstrativo, relatoriosItemDemonst);
        return previsaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularPrevisaoInicialImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = this.getEm().merge(itDemonstrativo);
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularPrevisaoInicialImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.add(calculaValoresPrevisaoInicial(formula, relatoriosItemDemonst));
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularPrevisaoInicialImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.subtract(calculaValoresPrevisaoInicial(formula, relatoriosItemDemonst));
                        }
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal calcularPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal previsaoInicial = calcularPrevisaoAtualizadaImpl(itemDemonstrativo, relatoriosItemDemonst);
        return previsaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularPrevisaoAtualizadaImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = this.getEm().merge(itDemonstrativo);
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularPrevisaoAtualizadaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.add(calcularPrevisaoAtualizadaContaNaoRecursivo(formula, relatoriosItemDemonst));
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularPrevisaoAtualizadaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.subtract(calcularPrevisaoAtualizadaContaNaoRecursivo(formula, relatoriosItemDemonst));
                        }
                    }
                }
            }
        }
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularPrevisaoAtualizadaContaNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "SELECT (PREVISAO - ANULACAO) AS VALOR FROM ( "
            + " SELECT 1 AS GRUPO, COALESCE(SUM(RE.VALOR), 0) AS PREVISAO "
            + " FROM ALTERACAOORC ALT  "
            + " INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID  "
            + " INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON RLOA.ID = RLF.RECEITALOA_ID ";
            sql += " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID ";
            sql += " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RLOA.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += " INNER JOIN CONTA B ON RLOA.CONTADERECEITA_ID = B.ID " + recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA L ON RLOA.LOA_ID = L.ID  "
            + " INNER JOIN LDO LD ON L.LDO_ID = LD.ID  "
            + " INNER JOIN EXERCICIO E ON LD.EXERCICIO_ID = E.ID  "
            + " WHERE e.id = :exercicio  "
            + " AND RE.TIPOALTERACAOORC = 'PREVISAO') PREVISAO "
            + " LEFT JOIN( "
            + " SELECT 1 AS GRUPO, COALESCE(SUM(RE.VALOR), 0) AS ANULACAO "
            + " FROM ALTERACAOORC ALT  "
            + " INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID  "
            + " INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON RLOA.ID = RLF.RECEITALOA_ID ";
            sql += " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID ";
            sql += " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RLOA.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += " INNER JOIN CONTA B ON RLOA.CONTADERECEITA_ID = B.ID " + recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA L ON RLOA.LOA_ID = L.ID  "
            + " INNER JOIN LDO LD ON L.LDO_ID = LD.ID  "
            + " INNER JOIN EXERCICIO E ON LD.EXERCICIO_ID = E.ID  "
            + " WHERE e.id = :exercicio  "
            + " AND RE.TIPOALTERACAOORC = 'ANULACAO' "
            + " ) ANULACAO ON PREVISAO.GRUPO = ANULACAO.GRUPO";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("exercicio", this.exercicioCorrente.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaValoresPrevisaoInicial(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal retorno = BigDecimal.ZERO;
        String sql = "SELECT COALESCE(SUM(A.VALOR),0)"
            + " FROM RECEITALOA A ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON A.ID = RLF.RECEITALOA_ID ";
            sql += " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID ";
            sql += " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on A.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += " INNER JOIN CONTA B ON A.CONTADERECEITA_ID = B.ID " + recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);

        }
        sql += " INNER JOIN LOA C ON A.LOA_ID = C.ID "
            + " INNER JOIN LDO D ON C.LDO_ID = D.ID "
            + " INNER JOIN EXERCICIO E ON D.EXERCICIO_ID = E.ID "
            + " WHERE e.id = :exercicio ";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("exercicio", this.exercicioCorrente.getId());
        if (!q.getResultList().isEmpty()) {
            retorno = (BigDecimal) q.getResultList().get(0);
        } else {
            retorno = BigDecimal.ZERO;
        }
        return retorno;
    }

    public BigDecimal calcularReceitaRealizadaAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) throws ParseException {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal receitaRealizadaAteOBimestre = calcularReceitaRealizadaAteOBimestreImpl(this.itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return receitaRealizadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularReceitaRealizadaAteOBimestreImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) throws ParseException {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = this.getEm().merge(itDemonstrativo);
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            Long ano = Long.parseLong(dataFinal.substring(6, 10));
            String periodo = dataFinal.substring(0, 6) + (ano - 1);
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularReceitaRealizadaAteOBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.add(calcularReceitaRealizadaAteOBimestreContaNaoRecursivo(formula, periodo, (this.exercicioCorrente.getAno() - 1), relatoriosItemDemonst));
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularReceitaRealizadaAteOBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.subtract(calcularReceitaRealizadaAteOBimestreContaNaoRecursivo(formula, periodo, (this.exercicioCorrente.getAno() - 1), relatoriosItemDemonst));
                        }
                    }
                }
            }
        }
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularReceitaRealizadaAteOBimestreContaNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, Integer ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "SELECT (VALOR_ATE_BIMESTRE - VALOR_ATE_BIMESTRE_EST) FROM ( "
            + "SELECT 1 AS grupo, COALESCE(SUM(lr.VALOR), 0) AS VALOR_ATE_BIMESTRE FROM LANCAMENTORECEITAORC LR "
            + "INNER JOIN RECEITALOA RE ON LR.RECEITALOA_ID = RE.ID "
            + "INNER JOIN LOA L ON RE.LOA_ID = L.ID "
            + "INNER JOIN LDO LD ON L.LDO_ID = LD.ID "
            + "INNER JOIN EXERCICIO e ON LD.EXERCICIO_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON RE.ID = RLF.RECEITALOA_ID ";
            sql += " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID ";
            sql += " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RE.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += " INNER JOIN CONTA CONT ON RE.CONTADERECEITA_ID = CONT.ID " + recuperaIds(formulaItemDemonstrativo, "CONT", ComponenteFormulaConta.class);
        }
        sql += "WHERE LR.DATALANCAMENTO BETWEEN '01/01/' || e.ano AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  AND e.ano = :exercicio "
            + ") lanc INNER JOIN ( "
            + "SELECT 1 AS grupo, COALESCE(SUM(lre.VALOR), 0) AS VALOR_ATE_BIMESTRE_EST FROM RECEITAORCESTORNO LRE "
            + "INNER JOIN LANCAMENTORECEITAORC LR ON LR.ID = LRE.LANCAMENTORECEITAORC_ID "
            + "INNER JOIN RECEITALOA RE ON LR.RECEITALOA_ID = RE.ID "
            + "INNER JOIN LOA L ON RE.LOA_ID = L.ID "
            + "INNER JOIN LDO LD ON L.LDO_ID = LD.ID "
            + "INNER JOIN EXERCICIO e ON LD.EXERCICIO_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON RE.ID = RLF.RECEITALOA_ID ";
            sql += " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID ";
            sql += " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RE.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += " INNER JOIN CONTA CONT ON RE.CONTADERECEITA_ID = CONT.ID " + recuperaIds(formulaItemDemonstrativo, "CONT", ComponenteFormulaConta.class);
        }
        sql += "WHERE LR.DATALANCAMENTO BETWEEN '01/01/' || e.ano AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') "
            + "AND e.ano = :exercicio ) est ON lanc.grupo = est.grupo ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("exercicio", ex);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }


    public BigDecimal calcularDotacaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal retorno = calcularDotacaoInicialImpl(itemDemonstrativo, relatoriosItemDemonst);
        return retorno;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDotacaoInicialImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = this.getEm().merge(itDemonstrativo);
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDotacaoInicialImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.add(calcularDotacaoInicialNaoRecursivo(formula, relatoriosItemDemonst));
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDotacaoInicialImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.subtract(calcularDotacaoInicialNaoRecursivo(formula, relatoriosItemDemonst));
                        }
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal calcularDotacaoInicialNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "SELECT COALESCE(SUM(A.DOTACAO), 0) FROM SALDOFONTEDESPESAORC A   ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC FONTE ON A.FONTEDESPESAORC_ID = FONTE.ID " +
            " INNER JOIN DESPESAORC C ON FONTE.DESPESAORC_ID = C.ID  " +
            " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  " +
            " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
            " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  " + recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID " +
                " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID " +
                " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += " INNER JOIN " +
            " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  " +
            " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID  " +
            " WHERE C.EXERCICIO_ID = :EXERCICIO ";
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

    public BigDecimal calcularCreditosAdicionais(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal retorno = calcularCreditosAdicionaisImpl(itemDemonstrativo, relatoriosItemDemonst);
        return retorno;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularCreditosAdicionaisImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = this.getEm().merge(itDemonstrativo);
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularCreditosAdicionaisImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.add(calcularCreditosAdicionaisNaoRecursivo(formula, relatoriosItemDemonst));
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularCreditosAdicionaisImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.subtract(calcularCreditosAdicionaisNaoRecursivo(formula, relatoriosItemDemonst));
                        }
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal calcularCreditosAdicionaisNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "SELECT COALESCE(sum(A.ALTERACAO), 0) FROM SALDOFONTEDESPESAORC A  ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC FONTE ON A.FONTEDESPESAORC_ID = FONTE.ID " +
            " INNER JOIN DESPESAORC C ON FONTE.DESPESAORC_ID = C.ID  " +
            " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  " +
            " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
            " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  " + recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }

        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID " +
                " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID " +
                " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += " INNER JOIN "
            + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO ";
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

    public BigDecimal calcularDespesasLiquidadasAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst, String dataFinal) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal retorno = calcularDespesasLiquidadasAteOBimestreImpl(itemDemonstrativo, relatoriosItemDemonst, dataFinal);
        return retorno;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, String dataFinal) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = this.getEm().merge(itDemonstrativo);
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDespesasLiquidadasAteOBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, dataFinal));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.add(calcularDespesasLiquidadasAteOBimestreNaoRecursivo(formula, relatoriosItemDemonst, dataFinal));
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasAteOBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, dataFinal));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.subtract(calcularDespesasLiquidadasAteOBimestreNaoRecursivo(formula, relatoriosItemDemonst, dataFinal));
                        }
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal calcularDespesasLiquidadasAteOBimestreNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, String dataFinal) {
        BigDecimal total;
        String sql = " SELECT COALESCE(sum(A.LIQUIDADO), 0) AS valor FROM SALDOFONTEDESPESAORC A ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC FONTE ON A.FONTEDESPESAORC_ID = FONTE.ID " +
            " INNER JOIN DESPESAORC C ON FONTE.DESPESAORC_ID = C.ID  " +
            " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  " +
            " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  " +
            " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  " + recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }

        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID " +
                " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID " +
                " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += " INNER JOIN "
            + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A "
            + " WHERE TO_CHAR(a.DATASALDO, 'dd/MM/yyyy') <= :DATAFINAL "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
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
    public BigDecimal calcularDotacaoInicialDespesaPropriaImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, Exercicio exercicio) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = this.getEm().merge(itDemonstrativo);
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDotacaoInicialDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, exercicio));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.add(calcularDotacaoInicialDespesaPropria(formula, relatoriosItemDemonst, exercicio));
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDotacaoInicialDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, exercicio));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.subtract(calcularDotacaoInicialDespesaPropria(formula, relatoriosItemDemonst, exercicio));
                        }
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal calcularDotacaoInicialDespesaPropria(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, Exercicio exercicio) {
        BigDecimal toReturn = BigDecimal.ZERO;
        String sql = "SELECT COALESCE(sum(A.DOTACAO), 0) FROM SALDOFONTEDESPESAORC A  ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC FONTE ON A.FONTEDESPESAORC_ID = FONTE.ID "
            + " INNER JOIN DESPESAORC C ON FONTE.DESPESAORC_ID = C.ID  "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += " INNER JOIN "
            + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", exercicio.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            toReturn = (BigDecimal) q.getResultList().get(0);
        }
        return toReturn;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularCreditosAdicionaisDespesaPropriaImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, Exercicio exercicio) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = this.getEm().merge(itDemonstrativo);
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularCreditosAdicionaisDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, exercicio));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.add(calcularCreditosAdicionaisDespesaPropria(formula, relatoriosItemDemonst, exercicio));
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularCreditosAdicionaisDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, exercicio));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.subtract(calcularCreditosAdicionaisDespesaPropria(formula, relatoriosItemDemonst, exercicio));
                        }
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal calcularCreditosAdicionaisDespesaPropria(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, Exercicio exercicio) {
        BigDecimal total;
        String sql = "SELECT COALESCE(sum(A.ALTERACAO), 0) FROM SALDOFONTEDESPESAORC A  ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC FONTE ON A.FONTEDESPESAORC_ID = FONTE.ID "
            + " INNER JOIN DESPESAORC C ON FONTE.DESPESAORC_ID = C.ID  "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += " INNER JOIN "
            + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", exercicio.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreDespesaPropriaImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = this.getEm().merge(itDemonstrativo);
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDespesasLiquidadasAteOBimestreDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.add(calcularDespesasLiquidadasAteOBimestreDespesaPropria(formula, dataFinal, ex, relatoriosItemDemonst));
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasAteOBimestreDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            total = total.subtract(calcularDespesasLiquidadasAteOBimestreDespesaPropria(formula, dataFinal, ex, relatoriosItemDemonst));
                        }
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal calcularDespesasLiquidadasAteOBimestreDespesaPropria(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT COALESCE(sum(A.LIQUIDADO), 0) AS valor FROM SALDOFONTEDESPESAORC A ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC FONTE ON A.FONTEDESPESAORC_ID = FONTE.ID "
            + " INNER JOIN DESPESAORC C ON FONTE.DESPESAORC_ID = C.ID  "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  " + recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }

        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += " INNER JOIN "
            + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A "
            + " WHERE TO_CHAR(A.DATASALDO, 'dd/MM/yyyy') <= :DATAFINAL "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", ex.getId());
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
    public BigDecimal calcularRestosAPagarNaoProcessadosDespesaPropriaImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularRestosAPagarNaoProcessadosDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularRestosAPagarNaoProcessadosDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularRestosAPagarNaoProcessadosDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestosAPagarNaoProcessadosDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularRestosAPagarNaoProcessadosDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularRestosAPagarNaoProcessadosDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    public BigDecimal calcularRestosAPagarNaoProcessadosDespesaPropria(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT COALESCE(SUM(EMP.VALOR), 0) AS VALOR  "
            + " FROM EMPENHO EMP "
            + " inner join UNIDADEORGANIZACIONAL uo on emp.UNIDADEORGANIZACIONAL_ID = uo.id ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN DESPESAORC DESP ON DESP.ID = EMP.DESPESAORC_ID "
            + " INNER JOIN FONTEDESPESAORC FONTE ON EMP.FONTEDESPESAORC_ID = FONTE.id "
            + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID "
            + " INNER JOIN SUBACAOPPA SA ON PROV.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        sql += " INNER JOIN CONTA E ON PROV.CONTADEDESPESA_ID = E.ID  ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += " WHERE UPPER(EMP.CATEGORIAORCAMENTARIA) = 'RESTO' "
            + " AND cast(EMP.DATAEMPENHO as Date) between to_date(:DATAINICIAL,'dd/mm/yyyy') and to_date(:DATAFINAL,'dd/mm/yyyy') "
            + " AND EMP.EXERCICIO_ID = :EXERCICIO "
            + " and emp.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS' ";

        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", ex.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
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
    public BigDecimal calcularRestosAPagarNaoProcessadosPagosDespesaPropriaImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularRestosAPagarNaoProcessadosPagosDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularRestosAPagarNaoProcessadosPagosDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularRestosAPagarNaoProcessadosPagosDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestosAPagarNaoProcessadosPagosDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularRestosAPagarNaoProcessadosPagosDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularRestosAPagarNaoProcessadosPagosDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    public BigDecimal calcularRestosAPagarNaoProcessadosPagosDespesaPropria(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " select coalesce(sum(valor), 0) from(  "
            + "  select sum(coalesce(p.valor,0)) as valor from pagamento p  "
            + "  inner join liquidacao l on p.liquidacao_id = l.id  "
            + "  inner join empenho e on l.empenho_id = e.id  "
            + "  INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  "
            + "  INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  "
            + "  INNER JOIN FONTEDESPESAORC FONTE ON e.FONTEDESPESAORC_ID = FONTE.id ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += "  INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  "
            + "  inner join UNIDADEORGANIZACIONAL uo on e.UNIDADEORGANIZACIONAL_ID = uo.id ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += "  where  "
            + "  cast(p.datapagamento as Date) between to_date(:DATAINICIAL,'dd/mm/yyyy') and to_date(:DATAFINAL,'dd/mm/yyyy') "
            + "  and p.categoriaorcamentaria = 'RESTO'  "
            + "  and e.tiporestosprocessados = 'NAO_PROCESSADOS' "
            + " AND e.EXERCICIO_ID = :EXERCICIO "
            + "  union all  "
            + "  select sum(coalesce(es.valor,0)) * -1 as valor from pagamentoestorno es  "
            + "  inner join pagamento p on es.pagamento_id= p.id  "
            + "  inner join liquidacao l on p.liquidacao_id = l.id  "
            + "  inner join empenho e on l.empenho_id = e.id  "
            + "  INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  "
            + "  INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  "
            + "  INNER JOIN FONTEDESPESAORC FONTE ON e.FONTEDESPESAORC_ID = FONTE.id ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += "  INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  "
            + "  inner join UNIDADEORGANIZACIONAL uo on e.UNIDADEORGANIZACIONAL_ID = uo.id ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += "  where  "
            + "  cast(es.dataestorno as Date) between to_date(:DATAINICIAL,'dd/mm/yyyy') and to_date(:DATAFINAL,'dd/mm/yyyy') "
            + "  and es.categoriaorcamentaria = 'RESTO'  "
            + "  and e.tiporestosprocessados = 'NAO_PROCESSADOS' "
            + " AND e.EXERCICIO_ID = :EXERCICIO "
            + " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", ex.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
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
    public BigDecimal calcularRestosAPagarNaoProcessadosAPagarDespesaPropriaImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularRestosAPagarNaoProcessadosAPagarDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularRestosAPagarNaoProcessadosAPagarDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularRestosAPagarNaoProcessadosAPagarDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestosAPagarNaoProcessadosAPagarDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularRestosAPagarNaoProcessadosAPagarDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularRestosAPagarNaoProcessadosAPagarDespesaPropria(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    public BigDecimal calcularRestosAPagarNaoProcessadosAPagarDespesaPropria(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " select coalesce(sum(valor), 0) from(  "
            + " SELECT SUM(COALESCE(e.valor,0)) as valor  "
            + " FROM empenho e "
            + " INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID "
            + " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID "
            + " INNER JOIN FONTEDESPESAORC FONTE ON e.FONTEDESPESAORC_ID = FONTE.id ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += "  INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  "
            + "  inner join UNIDADEORGANIZACIONAL uo on e.UNIDADEORGANIZACIONAL_ID = uo.id ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " WHERE cast(e.dataempenho as Date) between to_date(:DATAINICIAL,'dd/mm/yyyy') and to_date(:DATAFINAL,'dd/mm/yyyy') "
            + "  and e.categoriaorcamentaria = 'RESTO'  "
            + "  and e.tiporestosprocessados = 'NAO_PROCESSADOS' "
            + " AND e.EXERCICIO_ID = :EXERCICIO "
            + " UNION ALL   "
            + "   SELECT SUM(COALESCE(es.valor,0)) * - 1  AS valor "
            + " FROM empenhoestorno es "
            + " INNER JOIN empenho e ON es.empenho_id = e.id "
            + " INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID "
            + " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID "
            + " INNER JOIN FONTEDESPESAORC FONTE ON e.FONTEDESPESAORC_ID = FONTE.id ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += "  INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  "
            + "  inner join UNIDADEORGANIZACIONAL uo on e.UNIDADEORGANIZACIONAL_ID = uo.id ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += "  and cast(es.dataestorno as Date) between to_date(:DATAINICIAL,'dd/mm/yyyy') and to_date(:DATAFINAL,'dd/mm/yyyy') "
            + "  and es.categoriaorcamentaria = 'RESTO'  "
            + "  and e.tiporestosprocessados = 'NAO_PROCESSADOS' "
            + " AND e.EXERCICIO_ID = :EXERCICIO "
            + " union all "
            + "  select sum(coalesce(p.valor,0)) * - 1 as valor from pagamento p  "
            + "  inner join liquidacao l on p.liquidacao_id = l.id  "
            + "  inner join empenho e on l.empenho_id = e.id  "
            + "  INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  "
            + "  INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  "
            + "  INNER JOIN FONTEDESPESAORC FONTE ON e.FONTEDESPESAORC_ID = FONTE.id ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += "  INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  "
            + "  inner join UNIDADEORGANIZACIONAL uo on e.UNIDADEORGANIZACIONAL_ID = uo.id ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += "  where  "
            + "  cast(p.datapagamento as Date) between to_date(:DATAINICIAL,'dd/mm/yyyy') and to_date(:DATAFINAL,'dd/mm/yyyy') "
            + "  and p.categoriaorcamentaria = 'RESTO'  "
            + "  and e.tiporestosprocessados = 'NAO_PROCESSADOS' "
            + " AND e.EXERCICIO_ID = :EXERCICIO "
            + "  union all  "
            + "  select sum(coalesce(es.valor,0)) as valor from pagamentoestorno es  "
            + "  inner join pagamento p on es.pagamento_id= p.id  "
            + "  inner join liquidacao l on p.liquidacao_id = l.id  "
            + "  inner join empenho e on l.empenho_id = e.id  "
            + "  INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  "
            + "  INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  "
            + "  INNER JOIN FONTEDESPESAORC FONTE ON e.FONTEDESPESAORC_ID = FONTE.id ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += "  INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  "
            + "  inner join UNIDADEORGANIZACIONAL uo on e.UNIDADEORGANIZACIONAL_ID = uo.id ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += "  where  "
            + "  cast(es.dataestorno as Date) between to_date(:DATAINICIAL,'dd/mm/yyyy') and to_date(:DATAFINAL,'dd/mm/yyyy') "
            + "  and es.categoriaorcamentaria = 'RESTO'  "
            + "  and e.tiporestosprocessados = 'NAO_PROCESSADOS' "
            + " AND e.EXERCICIO_ID = :EXERCICIO "
            + " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", ex.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
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
    public BigDecimal calcularRestosAPagarCanceladosDespesaPropriaImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularRestosAPagarCanceladosDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularRestosAPagarCanceladosDespesaPropria(formula, ex, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularRestosAPagarCanceladosDespesaPropria(formula, ex, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestosAPagarCanceladosDespesaPropriaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularRestosAPagarCanceladosDespesaPropria(formula, ex, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularRestosAPagarCanceladosDespesaPropria(formula, ex, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    public BigDecimal calcularRestosAPagarCanceladosDespesaPropria(FormulaItemDemonstrativo formulaItemDemonstrativo, Exercicio exercicio, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "SELECT COALESCE(SUM(A.VALOR), 0) AS VALOR "
            + " FROM empenhoestorno A "
            + " INNER JOIN EMPENHO c ON a.EMPENHO_ID = c.ID "
            + " INNER JOIN DESPESAORC DESP ON DESP.ID = c.DESPESAORC_ID "
            + " INNER JOIN FONTEDESPESAORC FONTE ON c.FONTEDESPESAORC_ID = FONTE.id "
            + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID "
            + " INNER JOIN SUBACAOPPA SA ON PROV.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += " INNER JOIN CONTA E ON PROV.CONTADEDESPESA_ID = E.ID  " + recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID " + recuperaIds(formulaItemDemonstrativo, "FONTEREC", ComponenteFormulaFonteRecurso.class);
        }
        sql += " INNER JOIN UNIDADEORGANIZACIONAL uni ON c.UNIDADEORGANIZACIONAL_ID = uni.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uni", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " WHERE UPPER(a.CATEGORIAORCAMENTARIA) = 'RESTO' "
            + " AND cast(A.DATAESTORNO as Date) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') "
            + " AND c.exercicio_id = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", exercicio.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
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
    public BigDecimal calcularRestosAPagarNaoProcessadosParcelaImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularRestosAPagarNaoProcessadosParcelaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularRestosAPagarNaoProcessadosParcela(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularRestosAPagarNaoProcessadosParcela(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestosAPagarNaoProcessadosParcelaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, ex, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularRestosAPagarNaoProcessadosParcela(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularRestosAPagarNaoProcessadosParcela(formula, dataInicial, dataFinal, ex, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    public BigDecimal calcularRestosAPagarNaoProcessadosParcela(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, Exercicio ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT COALESCE(SUM(EMP.VALOR), 0) AS VALOR  "
            + " FROM EMPENHO EMP "
            + " inner join UNIDADEORGANIZACIONAL uo on emp.UNIDADEORGANIZACIONAL_ID = uo.id ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN DESPESAORC DESP ON DESP.ID = EMP.DESPESAORC_ID "
            + " INNER JOIN FONTEDESPESAORC FONTE ON EMP.FONTEDESPESAORC_ID = FONTE.id "
            + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID "
            + " INNER JOIN SUBACAOPPA SA ON PROV.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ";
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class);
        }
        sql += " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ";
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class);
        }
        sql += " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ";
        if (relatoriosItemDemonst.getUsaFuncao()) {
            sql += recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class);
        }
        sql += " INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID ";
        if (relatoriosItemDemonst.getUsaPrograma()) {
            sql += recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class);
        }
        sql += " INNER JOIN CONTA E ON PROV.CONTADEDESPESA_ID = E.ID  ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN PROVISAOPPAFONTE PROVFONTE ON FONTE.PROVISAOPPAFONTE_ID = PROVFONTE.ID "
                + " INNER JOIN CONTADEDESTINACAO CONTADEST ON PROVFONTE.DESTINACAODERECURSOS_ID = CONTADEST.ID "
                + " INNER JOIN FONTEDERECURSOS FONTEREC ON CONTADEST.FONTEDERECURSOS_ID = FONTEREC.ID and FONTEREC.CODIGO LIKE '01' ";
        }
        sql += " WHERE UPPER(EMP.CATEGORIAORCAMENTARIA) = 'RESTO' "
            + " AND cast(EMP.DATAEMPENHO as Date) between to_date(:DATAINICIAL,'dd/mm/yyyy') and to_date(:DATAFINAL,'dd/mm/yyyy') "
            + " AND EMP.EXERCICIO_ID = :EXERCICIO "
            + " and emp.TIPORESTOSPROCESSADOS = 'NAO_PROCESSADOS' ";

        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", ex.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setMaxResults(1);
//        new Util().imprimeSQL(sql, q);
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

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }
}

