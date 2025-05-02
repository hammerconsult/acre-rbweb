/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.negocios.ReferenciaAnualFacade;

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
public class RelatorioRREOAnexo06Calculator extends ItemDemonstrativoCalculator {

    @EJB
    private ReferenciaAnualFacade referenciaAnualFacade;
    private ItemDemonstrativo itemDemonstrativo;
    private Exercicio exercicioCorrente;

    public RelatorioRREOAnexo06Calculator() {
    }

    public BigDecimal calcularDotacaoInicialRPPS(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaDotacaoInicial = calcularDotacaoInicialRPPSImpl(this.itemDemonstrativo, clausula, relatoriosItemDemonst, formulaItemDemonstrativo);
        return despesaDotacaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDotacaoInicialRPPSImpl(ItemDemonstrativo itDemonstrativo, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
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
                        total = total.add(calcularDotacaoInicialRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDotacaoInicialRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDotacaoInicialRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDotacaoInicialRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDotacaoInicialRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDotacaoInicialRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
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
    private BigDecimal calcularDotacaoInicialRPPSNaoRecursivo(Conta conta, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        BigDecimal total;
        String sql = " SELECT COALESCE(sum(A.DOTACAO), 0) FROM SALDOFONTEDESPESAORC A  "
                + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  "
                + " INNER JOIN UNIDADEORGANIZACIONAL UO ON D.UNIDADEORGANIZACIONAL_ID = UO.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN "
                + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
                + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
                + " WHERE E.CODIGO LIKE :CONTA AND C.EXERCICIO_ID = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CONTA", conta.getCodigoSemZerosAoFinal() + "%");
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularCreditosAdicionaisRPPS(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaCreditosAdicionais = calcularCreditosAdicionaisRPPSImpl(this.itemDemonstrativo, clausula, relatoriosItemDemonst, formulaItemDemonstrativo);
        return despesaCreditosAdicionais;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularCreditosAdicionaisRPPSImpl(ItemDemonstrativo itDemonstrativo, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
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
                        total = total.add(calcularCreditosAdicionaisRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularCreditosAdicionaisRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularCreditosAdicionaisRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularCreditosAdicionaisRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularCreditosAdicionaisRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularCreditosAdicionaisRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
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
    private BigDecimal calcularCreditosAdicionaisRPPSNaoRecursivo(Conta conta, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        BigDecimal total;
        String sql = " SELECT COALESCE(sum(A.ALTERACAO), 0) FROM SALDOFONTEDESPESAORC A  "
                + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  "
                + " INNER JOIN UNIDADEORGANIZACIONAL UO ON D.UNIDADEORGANIZACIONAL_ID = UO.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN "
                + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
                + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
                + " WHERE E.CODIGO LIKE :CONTA AND C.EXERCICIO_ID = :EXERCICIO  "
                + " GROUP BY E.CODIGO";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CONTA", conta.getCodigoSemZerosAoFinal() + "%");
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularDespesasLiquidadasNoBimestreRPPS(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasNoBimestre = calcularDespesasLiquidadasNoBimestreRPPSImpl(this.itemDemonstrativo, dataInicial, dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo);
        return despesasLiquidadasNoBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasNoBimestreRPPSImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
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
                        total = total.add(calcularDespesasLiquidadasNoBimestreRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasNoBimestreRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), dataInicial, dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasNoBimestreRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), dataInicial, dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasNoBimestreRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasNoBimestreRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), dataInicial, dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasNoBimestreRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), dataInicial, dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
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
    private BigDecimal calcularDespesasLiquidadasNoBimestreRPPSNaoRecursivo(Conta conta, String dataInicial, String dataFinal, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        BigDecimal total;
        String sql = " SELECT COALESCE(valorfinal - valorinicial, 0) FROM( "
                + " SELECT COALESCE(sum(A.LIQUIDADO), 0) AS valorfinal, 1 AS grupo FROM SALDOFONTEDESPESAORC A "
                + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
                + " INNER JOIN UNIDADEORGANIZACIONAL UO ON D.UNIDADEORGANIZACIONAL_ID = UO.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN "
                + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A "
                + " WHERE TO_CHAR(a.DATASALDO, 'dd/MM/yyyy') <= :DATAFINAL "
                + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
                + " WHERE E.CODIGO LIKE :CONTA AND C.EXERCICIO_ID = :EXERCICIO "
                + " ) tabelaultimovalor "
                + " INNER JOIN( "
                + " SELECT COALESCE(sum(A.LIQUIDADO), 0) AS valorinicial, 1 AS grupo FROM SALDOFONTEDESPESAORC A "
                + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
                + " INNER JOIN UNIDADEORGANIZACIONAL UO ON D.UNIDADEORGANIZACIONAL_ID = UO.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN "
                + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A "
                + " WHERE TO_CHAR(a.DATASALDO, 'dd/MM/yyyy') <= :DATAINICIAL "
                + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
                + " WHERE E.CODIGO LIKE :CONTA AND C.EXERCICIO_ID = :EXERCICIO  "
                + " ) tabelaprimeirovalor ON tabelaultimovalor.grupo = tabelaprimeirovalor.grupo";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CONTA", conta.getCodigoSemZerosAoFinal() + "%");
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
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

    public BigDecimal calcularDespesasLiquidadasAteOBimestreRPPS(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasAteOBimestre = calcularDespesasLiquidadasAteOBimestreRPPSImpl(this.itemDemonstrativo, dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo);
        return despesasLiquidadasAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreRPPSImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
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
                        total = total.add(calcularDespesasLiquidadasAteOBimestreRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasAteOBimestreRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
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
    private BigDecimal calcularDespesasLiquidadasAteOBimestreRPPSNaoRecursivo(Conta conta, String dataFinal, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        BigDecimal total;
        String sql = " SELECT COALESCE(sum(A.LIQUIDADO), 0) AS valor FROM SALDOFONTEDESPESAORC A "
                + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
                + " INNER JOIN UNIDADEORGANIZACIONAL UO ON D.UNIDADEORGANIZACIONAL_ID = UO.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN "
                + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A "
                + " WHERE TO_CHAR(a.DATASALDO, 'dd/MM/yyyy') <= :DATAFINAL "
                + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
                + " WHERE E.CODIGO LIKE :CONTA AND C.EXERCICIO_ID = :EXERCICIO ";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("CONTA", conta.getCodigoSemZerosAoFinal() + "%");
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

    public BigDecimal calcularDespesasEmpenhadasAteOBimestreExAnteriorRPPS(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasEmpenhadasAteOBimestreExAnteriorRPPSImpl(this.itemDemonstrativo, dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasEmpenhadasAteOBimestreExAnteriorRPPSImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = getItemDemonstrativoFacade().recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            Long ano = Long.parseLong(dataFinal.substring(6, 10));
            String periodo = dataFinal.substring(0, 6) + (ano - 1);
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDespesasEmpenhadasAteOBimestreExAnteriorRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasEmpenhadasAteOBimestreRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), periodo, (this.exercicioCorrente.getAno() - 1), clausula, relatoriosItemDemonst, formulaItemDemonstrativo).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasEmpenhadasAteOBimestreRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), periodo, (this.exercicioCorrente.getAno() - 1), clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasEmpenhadasAteOBimestreExAnteriorRPPSImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasEmpenhadasAteOBimestreRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), periodo, (this.exercicioCorrente.getAno() - 1), clausula, relatoriosItemDemonst, formulaItemDemonstrativo).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasEmpenhadasAteOBimestreRPPSNaoRecursivo(((ComponenteFormulaConta) componente).getConta(), periodo, (this.exercicioCorrente.getAno() - 1), clausula, relatoriosItemDemonst, formulaItemDemonstrativo));
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
    private BigDecimal calcularDespesasEmpenhadasAteOBimestreRPPSNaoRecursivo(Conta conta, String dataFinal, Integer ex, String clausula, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        BigDecimal total;
        String sql = "SELECT COALESCE(sum(A.EMPENHADO), 0) AS valor FROM SALDOFONTEDESPESAORC A ";
        sql += " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID ";
        sql += " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID ";
        sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON D.UNIDADEORGANIZACIONAL_ID = UO.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN exercicio f ON C.exercicio_id = f.id";
        sql += " INNER JOIN ";
        sql += " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A ";
        sql += " WHERE TO_CHAR(A.DATASALDO, 'dd/MM/yyyy') <= :DATAFINAL ";
        sql += " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID ";
        sql += " WHERE E.CODIGO LIKE :CONTA AND f.ano = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CONTA", conta.getCodigoSemZerosAoFinal() + "%");
        q.setParameter("EXERCICIO", ex);
        q.setParameter("DATAFINAL", dataFinal);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularDotacaoInicialExAnteriores(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst, String dataInicial, String dataFinal) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal dotacaoInicialExAnteriores = calcularDotacaoInicialExAnterioresImpl(itemDemonstrativo, relatoriosItemDemonst, dataInicial, dataFinal);
        return dotacaoInicialExAnteriores;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDotacaoInicialExAnterioresImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, String dataInicial, String dataFinal) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = getItemDemonstrativoFacade().recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            Long ano = Long.parseLong(dataFinal.substring(6, 10));
            String periodoInicial = dataInicial.substring(0, 6) + (ano - 1);
            String periodoFinal = dataFinal.substring(0, 6) + (ano - 1);
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDotacaoInicialExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, dataInicial, dataFinal));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDotacaoInicialExAnterioresSql(relatoriosItemDemonst, (exercicioCorrente.getAno() - 1), formula, periodoInicial, periodoFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDotacaoInicialExAnterioresSql(relatoriosItemDemonst, (exercicioCorrente.getAno() - 1), formula, periodoInicial, periodoFinal));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDotacaoInicialExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, dataInicial, dataFinal));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDotacaoInicialExAnterioresSql(relatoriosItemDemonst, (exercicioCorrente.getAno() - 1), formula, periodoInicial, periodoFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDotacaoInicialExAnterioresSql(relatoriosItemDemonst, (exercicioCorrente.getAno() - 1), formula, periodoInicial, periodoFinal));
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
    public BigDecimal calcularDotacaoInicialExAnterioresSql(RelatoriosItemDemonst relatoriosItemDemonst, Integer ex, FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal) {
        BigDecimal total;
        String sql = " SELECT COALESCE(SUM(VALOR), 0) AS DOTACAOATUAL ";
        sql += " FROM ( ";
        sql += " SELECT ";
        sql += " COALESCE(PROV.VALOR, 0) AS VALOR ";
        sql += " FROM PROVISAOPPADESPESA PROV ";
        sql += " INNER JOIN CONTA C ON C.ID = PROV.CONTADEDESPESA_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN SUBACAOPPA SA ON SA.ID = PROV.SUBACAOPPA_ID AND SA.EXERCICIO_ID = :EXERCICIO ";
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID =  PROV.UNIDADEORGANIZACIONAL_ID AND TO_DATE(:DATAFINAL, 'DD/MM/YYYY') BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, TO_DATE(:DATAFINAL, 'DD/MM/YYYY')) ";
        sql += " WHERE (SUBSTR(C.CODIGO,3,1) LIKE '3' OR SUBSTR(C.CODIGO,3,1) LIKE '6') ";
        sql += " UNION ALL ";
        sql += " SELECT ";
        sql += " COALESCE(SU.VALOR, 0) AS VALOR ";
        sql += " FROM SUPLEMENTACAOORC SU ";
        sql += " INNER JOIN ALTERACAOORC ALT ON ALT.ID = SU.ALTERACAOORC_ID ";
        sql += " INNER JOIN FONTEDESPESAORC FONTE ON FONTE.ID = SU.FONTEDESPESAORC_ID ";
        sql += " INNER JOIN DESPESAORC D ON D.ID = FONTE.DESPESAORC_ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "D", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA PROV ON PROV.ID = D.PROVISAOPPADESPESA_ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = PROV.UNIDADEORGANIZACIONAL_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA C ON C.ID = PROV.CONTADEDESPESA_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = PROV.UNIDADEORGANIZACIONAL_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = PROV.UNIDADEORGANIZACIONAL_ID AND ALT.DATAALTERACAO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, ALT.DATAALTERACAO) ";
        sql += " WHERE ALT.DATAALTERACAO >= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') AND ALT.DATAALTERACAO < TO_DATE(:DATAFINAL, 'DD/MM/YYYY') ";
        sql += " AND (SUBSTR(C.CODIGO,3,1) LIKE '3' OR SUBSTR(C.CODIGO,3,1) LIKE '6') ";
        sql += " AND D.EXERCICIO_ID = :EXERCICIO ";
        sql += " AND SU.TIPODESPESAORC = 'ORCAMENTARIA' ";
        sql += " UNION ALL ";
        sql += " SELECT ";
        sql += " COALESCE(ANUL.VALOR, 0) * -1 AS VALOR ";
        sql += " FROM ANULACAOORC ANUL ";
        sql += " INNER JOIN ALTERACAOORC ALT ON ALT.ID = ANUL.ALTERACAOORC_ID ";
        sql += " INNER JOIN FONTEDESPESAORC FONTE ON FONTE.ID = ANUL.FONTEDESPESAORC_ID ";
        sql += " INNER JOIN DESPESAORC D ON D.ID = FONTE.DESPESAORC_ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "D", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA PROV ON PROV.ID = D.PROVISAOPPADESPESA_ID ";
        sql += " INNER JOIN CONTA C ON C.ID = PROV.CONTADEDESPESA_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = PROV.UNIDADEORGANIZACIONAL_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = PROV.UNIDADEORGANIZACIONAL_ID AND ALT.DATAALTERACAO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, ALT.DATAALTERACAO) ";
        sql += " WHERE ALT.DATAALTERACAO >= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') AND ALT.DATAALTERACAO < TO_DATE(:DATAFINAL, 'DD/MM/YYYY') ";
        sql += " AND (SUBSTR(C.CODIGO,3,1) LIKE '3' OR SUBSTR(C.CODIGO,3,1) LIKE '6') ";
        sql += " AND D.EXERCICIO_ID = :EXERCICIO )";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", ex);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setMaxResults(1);
        //System.out.println("12......................................................>>>>");
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularCreditosAdicionaisExAnteriores(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal creditosAdicionaisExAnteriores = calcularCreditosAdicionaisExAnterioresImpl(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst);
        return creditosAdicionaisExAnteriores;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularCreditosAdicionaisExAnterioresImpl(ItemDemonstrativo itDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularCreditosAdicionaisExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicioCorrente, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularCreditosAdicionaisExAnterioresSql(relatoriosItemDemonst, exercicioCorrente, formula).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularCreditosAdicionaisExAnterioresSql(relatoriosItemDemonst, exercicioCorrente, formula));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularCreditosAdicionaisExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicioCorrente, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularCreditosAdicionaisExAnterioresSql(relatoriosItemDemonst, exercicioCorrente, formula).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularCreditosAdicionaisExAnterioresSql(relatoriosItemDemonst, exercicioCorrente, formula));
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
    public BigDecimal calcularCreditosAdicionaisExAnterioresSql(RelatoriosItemDemonst relatoriosItemDemonst, Exercicio exercicioCorrente, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal total;
        String sql = "SELECT COALESCE(sum(A.ALTERACAO), 0) FROM SALDOFONTEDESPESAORC A  "
                + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  "
                + " INNER JOIN UNIDADEORGANIZACIONAL UNID ON D.UNIDADEORGANIZACIONAL_ID = UNID.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UNID", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN "
                + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
                + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
                + " WHERE (SUBSTR(E.CODIGO,3,1) LIKE '3' OR SUBSTR(E.CODIGO,3,1) LIKE '6') "
                + " AND C.EXERCICIO_ID = :EXERCICIO "
                + " GROUP BY E.CODIGO ";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }


    public BigDecimal calcularDespesasLiquidadasNoBimestreExAnteriores(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, String dataInicial, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasNoBimestreExAnteriores = calcularDespesasLiquidadasNoBimestreExAnterioresImpl(itemDemonstrativo, dataFinal, dataInicial, relatoriosItemDemonst);
        return despesasLiquidadasNoBimestreExAnteriores;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasNoBimestreExAnterioresImpl(ItemDemonstrativo itemDemonstrativo, String dataFinal, String dataInicial, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itemDemonstrativo = getItemDemonstrativoFacade().recuperar(itemDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itemDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            Long ano = Long.parseLong(dataFinal.substring(6, 10));
            String periodo = dataFinal.substring(0, 6) + (ano - 1);
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDespesasLiquidadasNoBimestreExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, dataInicial, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            if (itemDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasNoBimestreExAnterioresSql(formula, periodo, dataFinal, this.exercicioCorrente, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasNoBimestreExAnterioresSql(formula, periodo, dataFinal, this.exercicioCorrente, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasNoBimestreExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, dataInicial, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            if (itemDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasNoBimestreExAnterioresSql(formula, periodo, dataFinal, this.exercicioCorrente, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasNoBimestreExAnterioresSql(formula, periodo, dataFinal, this.exercicioCorrente, relatoriosItemDemonst));
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
    public BigDecimal calcularDespesasLiquidadasNoBimestreExAnterioresSql(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, String dataInicial, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal total;
        String sql = " SELECT valorfinal - valorinicial FROM( "
                + " SELECT COALESCE(sum(A.LIQUIDADO), 0) AS valorfinal, 1 AS grupo FROM SALDOFONTEDESPESAORC A "
                + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
                + " INNER JOIN UNIDADEORGANIZACIONAL UNID ON D.UNIDADEORGANIZACIONAL_ID = UNID.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UNID", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN "
                + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A "
                + " WHERE TO_CHAR(a.DATASALDO, 'dd/MM/yyyy') <= :DATAFINAL "
                + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
                + " WHERE (SUBSTR(E.CODIGO,3,1) LIKE '3' OR SUBSTR(E.CODIGO,3,1) LIKE '6') AND E.DTYPE = 'ContaDespesa' AND C.EXERCICIO_ID = :EXERCICIO  "
                + " ) tabelaultimovalor "
                + " INNER JOIN( "
                + " SELECT COALESCE(sum(A.LIQUIDADO), 0) AS valorinicial, 1 AS grupo FROM SALDOFONTEDESPESAORC A "
                + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
                + " INNER JOIN UNIDADEORGANIZACIONAL UNID ON D.UNIDADEORGANIZACIONAL_ID = UNID.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UNID", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN "
                + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A "
                + " WHERE TO_CHAR(a.DATASALDO, 'dd/MM/yyyy') <= :DATAINICIAL "
                + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
                + " WHERE (SUBSTR(E.CODIGO,3,1) LIKE '3' OR SUBSTR(E.CODIGO,3,1) LIKE '6') "
                + "AND C.EXERCICIO_ID = :EXERCICIO  "
                + " ) tabelaprimeirovalor ON tabelaultimovalor.grupo = tabelaprimeirovalor.grupo";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
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

    public BigDecimal calcularDespesasLiquidadasAteOBimestreExAnteriores(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasAteOBimestreExAnteriores = calcularDespesasLiquidadasAteOBimestreExAnterioresImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return despesasLiquidadasAteOBimestreExAnteriores;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreExAnterioresImpl(ItemDemonstrativo itemDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itemDemonstrativo = getItemDemonstrativoFacade().recuperar(itemDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itemDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            Long ano = Long.parseLong(dataFinal.substring(6, 10));
            String periodo = dataFinal.substring(0, 6) + (ano - 1);
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDespesasLiquidadasAteOBimestreExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itemDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreExAnterioresSql(exercicioCorrente, relatoriosItemDemonst, formula, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreExAnterioresSql(exercicioCorrente, relatoriosItemDemonst, formula, dataFinal));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasAteOBimestreExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itemDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreExAnterioresSql(exercicioCorrente, relatoriosItemDemonst, formula, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreExAnterioresSql(exercicioCorrente, relatoriosItemDemonst, formula, dataFinal));
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
    public BigDecimal calcularDespesasLiquidadasAteOBimestreExAnterioresSql(Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal total;
        String sql = " SELECT COALESCE(sum(A.LIQUIDADO), 0) AS valor FROM SALDOFONTEDESPESAORC A "
                + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
                + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
                + " INNER JOIN UNIDADEORGANIZACIONAL UNID ON D.UNIDADEORGANIZACIONAL_ID = UNID.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UNID", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN "
                + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A "
                + " WHERE TO_CHAR(a.DATASALDO, 'dd/MM/yyyy') <= :DATAFINAL "
                + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID "
                + " WHERE (SUBSTR(E.CODIGO,3,1) LIKE '3' OR SUBSTR(E.CODIGO,3,1) LIKE '6') "
                + " AND C.EXERCICIO_ID = :EXERCICIO  ";
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
    public BigDecimal calcularDespesasEmpenhadasAteOBimestreExAnterior(Integer ex, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal) {
        BigDecimal total;
        String sql = " SELECT COALESCE(sum(A.EMPENHADO), 0) AS valor FROM SALDOFONTEDESPESAORC A ";
        sql += " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID ";
        sql += " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID ";
        sql += " INNER JOIN UNIDADEORGANIZACIONAL UNID ON D.UNIDADEORGANIZACIONAL_ID = UNID.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += recuperaIds(formulaItemDemonstrativo, "UNID", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "E", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN exercicio f ON C.exercicio_id = f.id";
        sql += " INNER JOIN ";
        sql += " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A ";
        sql += " WHERE TO_CHAR(A.DATASALDO, 'dd/MM/yyyy') <= :DATAFINAL ";
        sql += " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID ";
        sql += " WHERE (SUBSTR(E.CODIGO,3,1) LIKE '3' OR SUBSTR(E.CODIGO,3,1) LIKE '6') AND f.ano = :EXERCICIO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", ex);
        q.setParameter("DATAFINAL", dataFinal);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }


    //    RECUPERA VALORES DA RECEITA ATUALIZADA
    public BigDecimal calcularPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst, String dataInicial, String dataFinal) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal previsaoAtualizada = calcularPrevisaoAtualizadaAlteradoImpl(itemDemonstrativo, relatoriosItemDemonst, dataInicial, dataFinal);
        return previsaoAtualizada;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularPrevisaoAtualizadaAlteradoImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, String dataInicial, String dataFinal) {
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
                        total = total.add(calcularPrevisaoAtualizadaAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, dataInicial, dataFinal));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularPrevisaoAtualizadaSql(formula, relatoriosItemDemonst, exercicioCorrente, dataInicial, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularPrevisaoAtualizadaSql(formula, relatoriosItemDemonst, exercicioCorrente, dataInicial, dataFinal));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularPrevisaoAtualizadaAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, dataInicial, dataFinal));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularPrevisaoAtualizadaSql(formula, relatoriosItemDemonst, exercicioCorrente, dataInicial, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularPrevisaoAtualizadaSql(formula, relatoriosItemDemonst, exercicioCorrente, dataInicial, dataFinal));
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
    public BigDecimal calcularPrevisaoAtualizadaSql(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, Exercicio exercicioCorrente, String dataInicial, String dataFinal) {
        BigDecimal total;
        String sql = " SELECT ";
        sql += " COALESCE(SUM(VALOR), 0) AS VALOR ";
        sql += " FROM( ";
        sql += " SELECT ";
        sql += " (COALESCE(DECODE(RE.TIPOALTERACAOORC, 'PREVISAO', RE.VALOR, RE.VALOR * (-1)), 0)) AS VALOR ";
        sql += " FROM ALTERACAOORC ALT ";
        sql += " INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID ";
        sql += " INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID ";
        sql += " INNER JOIN CONTA B ON RLOA.CONTADERECEITA_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = RLOA.ENTIDADE_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.SUBORDINADA_ID = RLOA.ENTIDADE_ID AND ALT.DATAALTERACAO BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, ALT.DATAALTERACAO) ";
        sql += " INNER JOIN LOA L ON RLOA.LOA_ID = L.ID ";
        sql += " INNER JOIN LDO LD ON L.LDO_ID = LD.ID ";
        sql += " WHERE LD.EXERCICIO_ID = :EXERCICIO ";
        sql += " AND ALT.DATAALTERACAO >= TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND  ALT.DATAALTERACAO < TO_DATE(:DATAFINAL, 'dd/MM/yyyy')";
        sql += " UNION ALL ";
        sql += " SELECT ";
        sql += " RLOA.VALOR AS VALOR ";
        sql += " FROM RECEITALOA RLOA ";
        sql += " INNER JOIN CONTA B ON RLOA.CONTADERECEITA_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = RLOA.ENTIDADE_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.SUBORDINADA_ID = RLOA.ENTIDADE_ID AND TO_DATE(:DATAFINAL, 'dd/mm/yyyy') BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, TO_DATE(:DATAFINAL, 'dd/mm/yyyy')) ";
        sql += " INNER JOIN LOA L ON RLOA.LOA_ID = L.ID ";
        sql += " INNER JOIN LDO LD ON L.LDO_ID = LD.ID ";
        sql += " WHERE LD.EXERCICIO_ID = :EXERCICIO ) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
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


    //    RECUPERA VALORES DA RECEITA REALIZADA NO BIMESTRE
    public BigDecimal calcularReceitaRealizadaNoBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal receitaRealizadaNoBimestre = calcularReceitaRealizadaNoBimestreImpl(itemDemonstrativo, dataInicial, dataFinal, relatoriosItemDemonst);
        return receitaRealizadaNoBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularReceitaRealizadaNoBimestreImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularReceitaRealizadaNoBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaNoBimestreSql(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaNoBimestreSql(formula, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularReceitaRealizadaNoBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaNoBimestreSql(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaNoBimestreSql(formula, dataInicial, dataFinal, relatoriosItemDemonst));
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
    public BigDecimal calcularReceitaRealizadaNoBimestreSql(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT COALESCE(SUM(VALOR), 0) AS VALOR ";
        sql += " FROM ( ";
        sql += " SELECT COALESCE(LANC.VALOR, 0) AS VALOR ";
        sql += " FROM RECEITALOA RECLOA ";
        sql += " INNER JOIN CONTA C ON RECLOA.CONTADERECEITA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LANCAMENTORECEITAORC LANC ON LANC.RECEITALOA_ID = RECLOA.ID ";
        sql += " INNER JOIN LOA ON LOA.ID = RECLOA.LOA_ID";
        sql += " INNER JOIN LDO ON LDO.ID = LOA.LDO_ID AND LDO.EXERCICIO_ID = :EXERCICIO";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = RECLOA.ENTIDADE_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = RECLOA.ENTIDADE_ID AND LANC.DATALANCAMENTO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, LANC.DATALANCAMENTO)";
        sql += " WHERE LANC.DATALANCAMENTO >= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') AND LANC.DATALANCAMENTO < TO_DATE(:DATAFINAL, 'DD/MM/YYYY') ";
        sql += " UNION ALL ";
        sql += " SELECT COALESCE(EST.VALOR ,0) * -1 AS VALOR ";
        sql += " FROM RECEITALOA RECLOA ";
        sql += " INNER JOIN CONTA C ON RECLOA.CONTADERECEITA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN RECEITAORCESTORNO EST ON EST.RECEITALOA_ID = RECLOA.ID ";
        sql += " INNER JOIN LOA ON LOA.ID = RECLOA.LOA_ID";
        sql += " INNER JOIN LDO ON LDO.ID = LOA.LDO_ID AND LDO.EXERCICIO_ID = :EXERCICIO ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = RECLOA.ENTIDADE_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = RECLOA.ENTIDADE_ID AND EST.DATAESTORNO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, EST.DATAESTORNO)";
        sql += " WHERE EST.DATAESTORNO >= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') AND EST.DATAESTORNO < TO_DATE(:DATAFINAL, 'DD/MM/YYYY'))";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }


    //    RECUPERA VALORES DA RECEITA REALIZADA ATE O BIMESTRE
    public BigDecimal calcularReceitaRealizadaAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) throws ParseException {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal receitaRealizadaAteOBimestre = calcularReceitaRealizadaAteOBimestreImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return receitaRealizadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularReceitaRealizadaAteOBimestreImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) throws ParseException {
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
                        total = total.add(calcularReceitaRealizadaAteOBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaAteOBimestre(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaAteOBimestre(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularReceitaRealizadaAteOBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaAteOBimestre(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaAteOBimestre(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst));
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
    private BigDecimal calcularReceitaRealizadaAteOBimestre(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, Integer ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT COALESCE(SUM(VALOR), 0) AS VALOR ";
        sql += " FROM ( ";
        sql += " SELECT COALESCE(LANC.VALOR, 0) AS VALOR ";
        sql += " FROM RECEITALOA RECLOA ";
        sql += " INNER JOIN CONTA C ON RECLOA.CONTADERECEITA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA ON LOA.ID = RECLOA.LOA_ID";
        sql += " INNER JOIN LDO ON LDO.ID = LOA.LDO_ID";
        sql += " INNER JOIN EXERCICIO E ON E.ID = LDO.EXERCICIO_ID ";
        sql += " INNER JOIN LANCAMENTORECEITAORC LANC ON LANC.RECEITALOA_ID = RECLOA.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = RECLOA.ENTIDADE_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = RECLOA.ENTIDADE_ID AND LANC.DATALANCAMENTO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, LANC.DATALANCAMENTO)";
        sql += " WHERE LANC.DATALANCAMENTO >= '01/01/' || :EXERCICIO AND LANC.DATALANCAMENTO < TO_DATE(:DATAFINAL, 'DD/MM/YYYY')";
        sql += " AND E.ANO = :EXERCICIO ";
        sql += " UNION ALL ";
        sql += " SELECT COALESCE(EST.VALOR ,0) * -1 AS VALOR ";
        sql += " FROM RECEITALOA RECLOA ";
        sql += " INNER JOIN CONTA C ON RECLOA.CONTADERECEITA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA ON LOA.ID = RECLOA.LOA_ID";
        sql += " INNER JOIN LDO ON LDO.ID = LOA.LDO_ID ";
        sql += " INNER JOIN EXERCICIO E ON E.ID = LDO.EXERCICIO_ID ";
        sql += " INNER JOIN RECEITAORCESTORNO EST ON EST.RECEITALOA_ID = RECLOA.ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = RECLOA.ENTIDADE_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = RECLOA.ENTIDADE_ID AND EST.DATAESTORNO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, EST.DATAESTORNO)";
        sql += " WHERE EST.DATAESTORNO  >= '01/01/' || :EXERCICIO AND EST.DATAESTORNO < TO_DATE(:DATAFINAL, 'DD/MM/YYYY')";
        sql += " AND E.ANO = :EXERCICIO )";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("EXERCICIO", ex);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    //    RECUPERA VALORES DA RECEITA REALIZADA ATE O BIMESTRE DO EXERCICIO ANTERIOR
    public BigDecimal calcularReceitaRealizadaAteOBimestreExAnterior(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal receitaRealizadaAteOBimestre = calcularReceitaRealizadaAteOBimestreImplExAnterior(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return receitaRealizadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularReceitaRealizadaAteOBimestreImplExAnterior(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = getItemDemonstrativoFacade().recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            Long ano = Long.parseLong(dataFinal.substring(6, 10));
            String periodo = dataFinal.substring(0, 6) + (ano - 1);
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularReceitaRealizadaAteOBimestreImplExAnterior(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaAteOBimestre(formula, periodo, this.exercicioCorrente.getAno() - 1, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaAteOBimestre(formula, periodo, this.exercicioCorrente.getAno() - 1, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularReceitaRealizadaAteOBimestreImplExAnterior(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (total.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaAteOBimestre(formula, periodo, this.exercicioCorrente.getAno() - 1, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaAteOBimestre(formula, periodo, this.exercicioCorrente.getAno() - 1, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }


    //    RECUPERA VALORES DESPESA ATUALIZADA
    public BigDecimal calcularDotacaoInicial(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst, String dataInicial, String dataFinal) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaDotacaoInicial = calcularDotacaoInicialImpl(itemDemonstrativo, relatoriosItemDemonst, dataInicial, dataFinal);
        return despesaDotacaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDotacaoInicialImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, String dataInicial, String dataFinal) {
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
                        total = total.add(calcularDotacaoInicialImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, dataInicial, dataFinal));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDotacaoInicial(formula, relatoriosItemDemonst, dataInicial, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDotacaoInicial(formula, relatoriosItemDemonst, dataInicial, dataFinal));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDotacaoInicialImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, dataInicial, dataFinal));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDotacaoInicial(formula, relatoriosItemDemonst, dataInicial, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDotacaoInicial(formula, relatoriosItemDemonst, dataInicial, dataFinal));
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
    public BigDecimal calcularDotacaoInicial(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, String dataInicial, String dataFinal) {
        BigDecimal total;
        String sql = " SELECT COALESCE(SUM(VALOR), 0) AS DOTACAOATUAL ";
        sql += " FROM ( ";
        sql += " SELECT ";
        sql += " COALESCE(PROV.VALOR, 0) AS VALOR ";
        sql += " FROM PROVISAOPPADESPESA PROV ";
        sql += " INNER JOIN CONTA C ON C.ID = PROV.CONTADEDESPESA_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN SUBACAOPPA SA ON SA.ID = PROV.SUBACAOPPA_ID AND SA.EXERCICIO_ID = :EXERCICIO ";
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = PROV.UNIDADEORGANIZACIONAL_ID AND TO_DATE(:DATAFINAL, 'DD/MM/YYYY') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:DATAFINAL, 'DD/MM/YYYY')) ";
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = VW.SUPERIOR_ID AND TO_DATE(:DATAFINAL, 'DD/MM/YYYY') BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, TO_DATE(:DATAFINAL, 'DD/MM/YYYY')) ";
        sql += " UNION ALL ";
        sql += " SELECT ";
        sql += " COALESCE(SU.VALOR, 0) AS VALOR ";
        sql += " FROM SUPLEMENTACAOORC SU ";
        sql += " INNER JOIN ALTERACAOORC ALT ON ALT.ID = SU.ALTERACAOORC_ID ";
        sql += " INNER JOIN FONTEDESPESAORC FONTE ON FONTE.ID = SU.FONTEDESPESAORC_ID ";
        sql += " INNER JOIN DESPESAORC D ON D.ID = FONTE.DESPESAORC_ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "D", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA PROV ON PROV.ID = D.PROVISAOPPADESPESA_ID ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = PROV.UNIDADEORGANIZACIONAL_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA C ON C.ID = PROV.CONTADEDESPESA_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = PROV.UNIDADEORGANIZACIONAL_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = PROV.UNIDADEORGANIZACIONAL_ID AND ALT.DATAALTERACAO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, ALT.DATAALTERACAO) ";
        sql += " WHERE ALT.DATAALTERACAO BETWEEN TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') AND TO_DATE(:DATAFINAL, 'DD/MM/YYYY') ";
        sql += " AND SU.TIPODESPESAORC = 'ORCAMENTARIA' ";
        sql += " UNION ALL ";
        sql += " SELECT ";
        sql += " COALESCE(ANUL.VALOR, 0) * -1 AS VALOR ";
        sql += " FROM ANULACAOORC ANUL ";
        sql += " INNER JOIN ALTERACAOORC ALT ON ALT.ID = ANUL.ALTERACAOORC_ID ";
        sql += " INNER JOIN FONTEDESPESAORC FONTE ON FONTE.ID = ANUL.FONTEDESPESAORC_ID ";
        sql += " INNER JOIN DESPESAORC D ON D.ID = FONTE.DESPESAORC_ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "D", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA PROV ON PROV.ID = D.PROVISAOPPADESPESA_ID ";
        sql += " INNER JOIN CONTA C ON C.ID = PROV.CONTADEDESPESA_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = PROV.UNIDADEORGANIZACIONAL_ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = PROV.UNIDADEORGANIZACIONAL_ID AND ALT.DATAALTERACAO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, ALT.DATAALTERACAO) ";
        sql += " WHERE ALT.DATAALTERACAO BETWEEN TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') AND TO_DATE(:DATAFINAL, 'DD/MM/YYYY')) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
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

    //    RECUPERA DESPESA LIQUIDADAS NO BIMESTRE
    public BigDecimal calcularDespesasLiquidadasNoBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasNoBimestre = calcularDespesasLiquidadasNoBimestreImpl(itemDemonstrativo, dataInicial, dataFinal, relatoriosItemDemonst);
        return despesasLiquidadasNoBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasNoBimestreImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularDespesasLiquidadasNoBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasNoBimestre(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasNoBimestre(formula, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasNoBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasNoBimestre(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasNoBimestre(formula, dataInicial, dataFinal, relatoriosItemDemonst));
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
    public BigDecimal calcularDespesasLiquidadasNoBimestre(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT COALESCE(SUM(VALOR), 0) AS VALOR ";
        sql += " FROM( ";
        sql += " SELECT COALESCE(LIQ.VALOR, 0) AS VALOR ";
        sql += " FROM LIQUIDACAO LIQ ";
        sql += " INNER JOIN EMPENHO EMP ON LIQ.EMPENHO_ID = EMP.ID ";
        sql += " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "DESP", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID ";
        sql += " INNER JOIN CONTA C ON C.ID = PROV.CONTADEDESPESA_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON LIQ.UNIDADEORGANIZACIONAL_ID = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.SUBORDINADA_ID = LIQ.UNIDADEORGANIZACIONAL_ID AND LIQ.DATALIQUIDACAO BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, LIQ.DATALIQUIDACAO) ";
        sql += " WHERE LIQ.DATALIQUIDACAO >= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') AND LIQ.DATALIQUIDACAO < TO_DATE(:DATAFINAL, 'DD/MM/YYYY') ";
        sql += " AND DESP.EXERCICIO_ID = :EXERCICIO ";
        sql += " UNION ALL ";
        sql += " SELECT COALESCE(EST.VALOR, 0) *-1 AS VALOR ";
        sql += " FROM LIQUIDACAO LIQ ";
        sql += " INNER JOIN LIQUIDACAOESTORNO EST ON EST.LIQUIDACAO_ID = LIQ.ID ";
        sql += " INNER JOIN EMPENHO EMP ON LIQ.EMPENHO_ID = EMP.ID ";
        sql += " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "DESP", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID ";
        sql += " INNER JOIN CONTA C ON C.ID = PROV.CONTADEDESPESA_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON EST.UNIDADEORGANIZACIONAL_ID = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.SUBORDINADA_ID = LIQ.UNIDADEORGANIZACIONAL_ID AND EST.DATAESTORNO BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, EST.DATAESTORNO) ";
        sql += " WHERE EST.DATAESTORNO >= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') AND EST.DATAESTORNO < TO_DATE(:DATAFINAL, 'DD/MM/YYYY') ";
        sql += " AND DESP.EXERCICIO_ID = :EXERCICIO) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
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

    public BigDecimal calcularDespesasLiquidadasAteOBimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasAteOBimestre = calcularDespesasLiquidadasAteOBimestreImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return despesasLiquidadasAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularDespesasLiquidadasAteOBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreSql(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreSql(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasAteOBimestreImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreSql(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreSql(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst));
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
    public BigDecimal calcularDespesasLiquidadasAteOBimestreSql(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, Integer ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT COALESCE(SUM(VALOR), 0) AS VALOR ";
        sql += " FROM( ";
        sql += " SELECT COALESCE(LIQ.VALOR, 0) AS VALOR ";
        sql += " FROM LIQUIDACAO LIQ ";
        sql += " INNER JOIN EMPENHO EMP ON LIQ.EMPENHO_ID = EMP.ID ";
        sql += " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "DESP", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID ";
        sql += " INNER JOIN CONTA C ON C.ID = PROV.CONTADEDESPESA_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON LIQ.UNIDADEORGANIZACIONAL_ID = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.SUBORDINADA_ID = LIQ.UNIDADEORGANIZACIONAL_ID AND LIQ.DATALIQUIDACAO BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, LIQ.DATALIQUIDACAO) ";
        sql += " INNER JOIN EXERCICIO EX ON EX.ID = DESP.EXERCICIO_ID ";
        sql += " WHERE CAST(LIQ.DATALIQUIDACAO AS DATE) >= '01/01/' || :EXERCICIO AND CAST(LIQ.DATALIQUIDACAO AS DATE) < TO_DATE(:DATAFINAL, 'DD/MM/YYYY') ";
        sql += " AND DESP.EXERCICIO_ID = :EXERCICIO ";
        sql += " UNION ALL ";
        sql += " SELECT COALESCE(EST.VALOR, 0) * -1 AS VALOR ";
        sql += " FROM LIQUIDACAO LIQ ";
        sql += " INNER JOIN LIQUIDACAOESTORNO EST ON EST.LIQUIDACAO_ID = LIQ.ID ";
        sql += " INNER JOIN EMPENHO EMP ON LIQ.EMPENHO_ID = EMP.ID ";
        sql += " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID ";
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "DESP", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID ";
        sql += " INNER JOIN CONTA C ON C.ID = PROV.CONTADEDESPESA_ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " INNER JOIN UNIDADEORGANIZACIONAL UO ON EST.UNIDADEORGANIZACIONAL_ID = UO.ID " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.SUBORDINADA_ID = LIQ.UNIDADEORGANIZACIONAL_ID AND EST.DATAESTORNO BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, EST.DATAESTORNO) ";
        sql += " INNER JOIN EXERCICIO EX ON EX.ID = DESP.EXERCICIO_ID ";
        sql += " WHERE EST.DATAESTORNO >= '01/01/' || :EXERCICIO AND EST.DATAESTORNO < TO_DATE(:DATAFINAL, 'DD/MM/YYYY')";
        sql += " AND DESP.EXERCICIO_ID = :EXERCICIO) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("EXERCICIO", ex);
        q.setParameter("DATAFINAL", dataFinal);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }


    //    RECUPERA DESPESAS LIQUIDADAS ATE O BIMESTRE DO EXERCICIO ANTERIOR
    public BigDecimal calcularDespesasLiquidadasAteOBimestreExAnterior(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasLiquidadasAteOBimestreExAnteriorImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreExAnteriorImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = getItemDemonstrativoFacade().recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            Long ano = Long.parseLong(dataFinal.substring(6, 10));
            String periodo = dataFinal.substring(0, 6) + (ano - 1);
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDespesasLiquidadasAteOBimestreExAnteriorImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreSql(formula, periodo, this.exercicioCorrente.getAno() - 1, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreSql(formula, periodo, this.exercicioCorrente.getAno() - 1, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasAteOBimestreExAnteriorImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreSql(formula, periodo, this.exercicioCorrente.getAno() - 1, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreSql(formula, periodo, this.exercicioCorrente.getAno() - 1, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    public ReferenciaAnualFacade getReferenciaAnualFacade() {
        return referenciaAnualFacade;
    }
}
