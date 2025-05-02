/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.Util;
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
 * @author juggernaut
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ItemDemonstrativoCalculator implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Exercicio exercicioCorrente;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ContaFacade contaFacade;
    private ItemDemonstrativo itemDemonstrativo;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ItemDemonstrativoCalculator() {
    }

    @Deprecated
    //Trocar utilização pelo UtilRelatorioContabil
    public String concatenaParametros(List<ParametrosRelatorios> parametros, Integer local, String clausula) {
        StringBuilder sql = new StringBuilder();
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == local) {
                sql.append(clausula + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        return sql.toString();
    }

    @Deprecated
    //Trocar utilização pelo UtilRelatorioContabil
    public Query adicionaParametros(List<ParametrosRelatorios> parametros, Query q) {
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
        return q;
    }

    public ItemDemonstrativo recuperarItemDemonstrativoPeloNome(String nomeDoItem, Exercicio exercicio) {
        Query q = this.getEm().createQuery("select it from ItemDemonstrativo it "
            + "where it.descricao like :descricao and it.exercicio = :exercicio");
        q.setParameter("descricao", nomeDoItem);
        q.setParameter("exercicio", exercicio);
        if (q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("O Item Demonstrativo " + nomeDoItem + " não está cadastrado, ou não foi cadastrado corretamente!");
        } else {
            return (ItemDemonstrativo) q.getSingleResult();
        }
    }

    public ItemDemonstrativo recuperarItemDemonstrativoPeloNomeERelatorio(String nomeDoItem, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        String sql = "select it.* from itemdemonstrativo it " +
            " inner join relatoriositemdemonst rel on it.relatoriositemdemonst_id = rel.id  " +
            " where it.descricao like :descricao and rel.exercicio_id = :exercicio and rel.id = :relatorio ";
        Query q = this.getEm().createNativeQuery(sql, ItemDemonstrativo.class);
        q.setParameter("descricao", nomeDoItem);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("relatorio", relatoriosItemDemonst.getId());
        if (q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("O Item Demonstrativo " + nomeDoItem + " não está cadastrado, ou não foi cadastrado corretamente!");
        } else {
            return (ItemDemonstrativo) q.getSingleResult();
        }
    }

    @Deprecated
    //Utilizar o método buscarPrevisaoInicial
    public BigDecimal calcularPrevisaoInicialAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal previsaoInicial = calcularPrevisaoInicialAlteradoImpl(itemDemonstrativo, relatoriosItemDemonst);
        return previsaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularPrevisaoInicialAlteradoImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularPrevisaoInicialAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularPrevisaoInicialAlteradoNaoRecursivo(formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularPrevisaoInicialAlteradoNaoRecursivo(formula, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularPrevisaoInicialAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularPrevisaoInicialAlteradoNaoRecursivo(formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularPrevisaoInicialAlteradoNaoRecursivo(formula, relatoriosItemDemonst));
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
    public BigDecimal calcularPrevisaoInicialAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql = "SELECT COALESCE(SUM(rlf.VALOR),0) ";
        } else {
            sql = "SELECT COALESCE(SUM(A.VALOR),0) ";
        }
        sql += " FROM RECEITALOA A ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON A.ID = RLF.RECEITALOA_ID ";
            sql += " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID ";
            sql += " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on A.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA B ON A.CONTADERECEITA_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA C ON A.LOA_ID = C.ID "
            + " INNER JOIN LDO D ON C.LDO_ID = D.ID "
            + " INNER JOIN EXERCICIO E ON D.EXERCICIO_ID = E.ID "
            + " WHERE e.id = :exercicio ";
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

    public String recuperaIds(FormulaItemDemonstrativo formulaItemDemonstrativo, String alias, Class classe) {
        if (formulaItemDemonstrativo.getComponenteFormula().isEmpty()) {
            return "";
        }
        String retorno = "";
        for (ComponenteFormula cf : formulaItemDemonstrativo.getComponenteFormula()) {
            if (cf.getClass().equals(classe)) {
                if (retorno.trim().equals("")) {
                    if (classe.equals(ComponenteFormulaFonteRecurso.class)) {
                        retorno = " and (" + alias + ".codigo like '";
                    } else if (classe.equals(ComponenteFormulaConta.class)) {
                        retorno = " and (" + alias + ".codigo like '";
                    } else if (classe.equals(ComponenteFormulaTipoDesp.class)) {
                        retorno = " and (" + alias + ".TIPODESPESAORC like '";
                    } else {
                        retorno = " and " + alias + ".id in (";
                    }
                }
                if (classe.equals(ComponenteFormulaFonteRecurso.class)) {
                    if (((ComponenteFormulaFonteRecurso) cf).getFonteDeRecursos() != null) {
                        retorno += ((ComponenteFormulaFonteRecurso) cf).getFonteDeRecursos().getCodigo() + "' or " + alias + ".codigo like '";
                    } else {
                        retorno += "xx' or " + alias + ".codigo like '";
                    }

                } else if (classe.equals(ComponenteFormulaConta.class)) {
                    if (((ComponenteFormulaConta) cf).getConta() != null) {
                        retorno += ((ComponenteFormulaConta) cf).getConta().getCodigoSemZerosAoFinal() + "%' or " + alias + ".codigo like '";
                    } else {
                        retorno += "x' or " + alias + ".codigo like '";
                    }
                } else if (classe.equals(ComponenteFormulaUnidadeOrganizacional.class)) {
                    retorno += ((ComponenteFormulaUnidadeOrganizacional) cf).getUnidadeOrganizacional().getId() + ",";
                } else if (classe.equals(ComponenteFormulaSubFuncao.class)) {
                    retorno += ((ComponenteFormulaSubFuncao) cf).getSubFuncao().getId() + ",";
                } else if (classe.equals(ComponenteFormulaFuncao.class)) {
                    retorno += ((ComponenteFormulaFuncao) cf).getFuncao().getId() + ",";
                } else if (classe.equals(ComponenteFormulaPrograma.class)) {
                    retorno += ((ComponenteFormulaPrograma) cf).getProgramaPPA().getId() + ",";
                } else if (classe.equals(ComponenteFormulaAcao.class)) {
                    retorno += ((ComponenteFormulaAcao) cf).getAcaoPPA().getId() + ",";
                } else if (classe.equals(ComponenteFormulaTipoDesp.class)) {
                    retorno += ((ComponenteFormulaTipoDesp) cf).getTipoDespesaORC().name() + "' or " + alias + ".TIPODESPESAORC like '";
                }
            }
        }

        if (retorno.endsWith(" or " + alias + ".codigo like '")) {
            retorno = retorno.substring(0, retorno.length() - (17 + alias.length())) + ")";
        } else if (retorno.endsWith(" or " + alias + ".TIPODESPESAORC like '")) {
            retorno = retorno.substring(0, retorno.length() - (25 + alias.length())) + ")";
        } else if (!retorno.trim().equals("")) {
            retorno = retorno.substring(0, retorno.length() - 1) + ")";
        }

        return retorno;
    }

    //Utilizar buscarSaldoContabilPorTipoBalancete
    @Deprecated
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
        Util.imprimeSQL(sql.toString(), q);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    @Deprecated
    //Utilizar o método buscarPrevisaoAtualizada
    public BigDecimal calcularPrevisaoAtualizadaAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                                totalAdicao = totalAdicao.add(calcularPrevisaoAtualizadaAlteradoNaoRecursivo(formula, relatoriosItemDemonst, dataInicial, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularPrevisaoAtualizadaAlteradoNaoRecursivo(formula, relatoriosItemDemonst, dataInicial, dataFinal));
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
                                totalSubtracao = totalSubtracao.subtract(calcularPrevisaoAtualizadaAlteradoNaoRecursivo(formula, relatoriosItemDemonst, dataInicial, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularPrevisaoAtualizadaAlteradoNaoRecursivo(formula, relatoriosItemDemonst, dataInicial, dataFinal));
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
    public BigDecimal calcularPrevisaoAtualizadaAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, String dataInicial, String dataFinal) {
        BigDecimal total;
        String sql = " SELECT SUM(VALOR) AS VALOR FROM ( "
            + " SELECT COALESCE(SUM(RE.VALOR), 0) AS VALOR "
            + " FROM ALTERACAOORC ALT  "
            + " INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID  "
            + " INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN FONTEDERECURSOS FR ON RE.FONTEDERECURSO_ID = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RLOA.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA B ON RLOA.CONTADERECEITA_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA L ON RLOA.LOA_ID = L.ID  "
            + "  INNER JOIN LDO LD ON L.LDO_ID = LD.ID  "
            + "  INNER JOIN EXERCICIO E ON LD.EXERCICIO_ID = E.ID  "
            + "  WHERE e.id = :exercicio  "
            + "  AND RE.TIPOALTERACAOORC = 'PREVISAO' "
            + "  and trunc(ALT.DATAEFETIVACAO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + "  "
            + "  union all "
            + "  SELECT COALESCE(SUM(RE.VALOR), 0) * -1 AS valor "
            + "  FROM ALTERACAOORC ALT  "
            + "  INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID  "
            + "  INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN FONTEDERECURSOS FR ON RE.FONTEDERECURSO_ID = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RLOA.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA B ON RLOA.CONTADERECEITA_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA L ON RLOA.LOA_ID = L.ID  "
            + "  INNER JOIN LDO LD ON L.LDO_ID = LD.ID  "
            + "  INNER JOIN EXERCICIO E ON LD.EXERCICIO_ID = E.ID  "
            + "  WHERE e.id = :exercicio  "
            + "  AND RE.TIPOALTERACAOORC = 'ANULACAO' "
            + "  and trunc(ALT.DATAEFETIVACAO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') "
            + "  "
//            ESTORNO RECEITA ORÇAMENTÁRIA
            + " union all ";
        sql += " SELECT COALESCE(SUM(re.VALOR), 0) * -1 AS VALOR "
            + "  FROM EstornoAlteracaoOrc est "
            + "  INNER JOIN ALTERACAOORC ALT ON EST.alteracaoORC_ID = ALT.ID "
            + "  INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID  "
            + "  INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN FONTEDERECURSOS FR ON RE.FONTEDERECURSO_ID = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RLOA.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA B ON RLOA.CONTADERECEITA_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA L ON RLOA.LOA_ID = L.ID  "
            + "  INNER JOIN LDO LD ON L.LDO_ID = LD.ID  "
            + "  INNER JOIN EXERCICIO E ON LD.EXERCICIO_ID = E.ID  "
            + "  WHERE e.id = :exercicio  "
            + "  AND RE.TIPOALTERACAOORC = 'PREVISAO' "
            + "  and trunc(ALT.DATAEFETIVACAO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + " "
            + " union all ";
        sql += " SELECT COALESCE(SUM(re.VALOR), 0) AS VALOR "
            + "  FROM EstornoAlteracaoOrc est "
            + "  INNER JOIN ALTERACAOORC ALT ON EST.alteracaoORC_ID = ALT.ID "
            + "  INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID  "
            + "  INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN FONTEDERECURSOS FR ON RE.FONTEDERECURSO_ID = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RLOA.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA B ON RLOA.CONTADERECEITA_ID = B.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "B", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN LOA L ON RLOA.LOA_ID = L.ID  "
            + "  INNER JOIN LDO LD ON L.LDO_ID = LD.ID  "
            + "  INNER JOIN EXERCICIO E ON LD.EXERCICIO_ID = E.ID  "
            + "  WHERE e.id = :exercicio  "
            + "  AND RE.TIPOALTERACAOORC = 'ANULACAO' "
            + "  and trunc(ALT.DATAEFETIVACAO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("exercicio", this.exercicioCorrente.getId());
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

    public BigDecimal calcularReceitaRealizadaNoBimestreAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal receitaRealizadaNoBimestre = calcularReceitaRealizadaNoBimestreAlteradoImpl(itemDemonstrativo, dataInicial, dataFinal, relatoriosItemDemonst);
        return receitaRealizadaNoBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularReceitaRealizadaNoBimestreAlteradoImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularReceitaRealizadaNoBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularReceitaRealizadaNoBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst));
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
    public BigDecimal calcularReceitaRealizadaNoBimestreAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "SELECT sum(VALOR) FROM ( ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += "SELECT COALESCE(SUM(lrf.VALOR),0) as valor ";
        } else {
            sql += "SELECT COALESCE(SUM(lr.VALOR),0) as valor ";
        }
        sql += " FROM LANCAMENTORECEITAORC LR "
            + "INNER JOIN RECEITALOA RE ON LR.RECEITALOA_ID = RE.ID ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " inner join lancreceitafonte lrf on lrf.lancreceitaorc_id = lr.id "
                + " INNER JOIN ReceitaLOAFonte rlf ON lrf.receitaloafonte_id = RLF.id "
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
        sql += "WHERE trunc(LR.DATALANCAMENTO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + " union all ";
        if (!relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += "SELECT COALESCE(SUM(lre.VALOR), 0) * -1 AS VALOR ";
        } else {
            sql += "SELECT COALESCE(SUM(recorcfonte.VALOR),0) *-1 as valor ";
        }
        sql += " FROM RECEITAORCESTORNO LRE "
            + "INNER JOIN ReceitaORCFonteEstorno recorcfonte ON recorcfonte.receitaorcestorno_id = LRE.id "
            + " INNER JOIN ReceitaLOAFonte lr ON recorcfonte.receitaloafonte_id = lr.ID "
            + "INNER JOIN RECEITALOA RE ON LR.RECEITALOA_ID = RE.ID ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ContaDeDestinacao cd ON cd.ID = lr.DESTINACAODERECURSOS_ID "
                + " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RE.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA CONT ON  RE.CONTADERECEITA_ID = CONT.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "CONT", ComponenteFormulaConta.class);
        }
        sql += "WHERE trunc(lre.DATAESTORNO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + ") ";
        Query q = this.getEm().createNativeQuery(sql);
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

    @Deprecated
    public BigDecimal calcularReceitaRealizadaAteOBimestreAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal receitaRealizadaAteOBimestre = calcularReceitaRealizadaAteOBimestreAlteradoImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return receitaRealizadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularReceitaRealizadaAteOBimestreAlteradoImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularReceitaRealizadaAteOBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularReceitaRealizadaAteOBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, this.exercicioCorrente.getAno(), relatoriosItemDemonst));
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
    private BigDecimal calcularReceitaRealizadaAteOBimestreAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, Integer ex, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "SELECT sum(VALOR) FROM ( ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " SELECT COALESCE(SUM(lrf.VALOR),0) as valor ";
        } else {
            sql += " SELECT COALESCE(SUM(lr.VALOR),0) as valor ";
        }
        sql += " FROM LANCAMENTORECEITAORC LR "
            + "INNER JOIN RECEITALOA RE ON LR.RECEITALOA_ID = RE.ID "
            + "INNER JOIN LOA L ON RE.LOA_ID = L.ID "
            + "INNER JOIN LDO LD ON L.LDO_ID = LD.ID "
            + "INNER JOIN EXERCICIO e ON LD.EXERCICIO_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " inner join lancreceitafonte lrf on lrf.lancreceitaorc_id = lr.id "
                + " INNER JOIN ReceitaLOAFonte rlf ON lrf.receitaloafonte_id = RLF.id "
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
        sql += "WHERE trunc(LR.DATALANCAMENTO) BETWEEN to_date('01/01/' || e.ano, 'dd/mm/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  AND e.ano = :exercicio "
            + " union all ";
        if (!relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += "SELECT COALESCE(SUM(lre.VALOR), 0) * -1 AS VALOR ";
        } else {
            sql += "SELECT COALESCE(SUM(recorcfonte.VALOR),0) *-1 as valor ";
        }
        sql += "FROM RECEITAORCESTORNO LRE "
            + " INNER JOIN RECEITALOA RE ON LRE.RECEITALOA_ID = RE.ID "
            + " INNER JOIN LOA L ON RE.LOA_ID = L.ID "
            + " INNER JOIN LDO LD ON L.LDO_ID = LD.ID "
            + " INNER JOIN EXERCICIO e ON LD.EXERCICIO_ID = E.ID ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaORCFonteEstorno recorcfonte ON recorcfonte.receitaorcestorno_id = LRE.id "
                + " INNER JOIN ReceitaLOAFonte lr ON recorcfonte.receitaloafonte_id = lr.ID "
                + " INNER JOIN ContaDeDestinacao cd ON cd.ID = lr.DESTINACAODERECURSOS_ID "
                + " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on RE.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA CONT ON  RE.CONTADERECEITA_ID = CONT.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "CONT", ComponenteFormulaConta.class);
        }
        sql += "WHERE trunc(LRE.DATAESTORNO) BETWEEN to_date('01/01/' || e.ano, 'dd/mm/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') "
            + "AND e.ano = :exercicio )  ";
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

    public BigDecimal calcularPrevisaoAtualizadaSaldoExAnteriores(ItemDemonstrativo itemDemonstrativo, String dataInicial, String dataFinal) {
        BigDecimal despesaDotacaoInicial = calcularPrevisaoAtualizadaSaldoExAnterioresImpl(itemDemonstrativo, dataInicial, dataFinal);
        return despesaDotacaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularPrevisaoAtualizadaSaldoExAnterioresImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal) {
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
                        total = total.add(calcularPrevisaoAtualizadaSaldoExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularPrevisaoAtualizadaSaldoExAnterioresQuery(dataInicial, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularPrevisaoAtualizadaSaldoExAnterioresQuery(dataInicial, dataFinal));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularPrevisaoAtualizadaSaldoExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularPrevisaoAtualizadaSaldoExAnterioresQuery(dataInicial, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularPrevisaoAtualizadaSaldoExAnterioresQuery(dataInicial, dataFinal));
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
    public BigDecimal calcularPrevisaoAtualizadaSaldoExAnterioresQuery(String dataInicial, String dataFinal) {
        BigDecimal total;
        String sql = " select coalesce(sum(a.superavit), 0) as valor from alteracaoorc a " +
            " inner join suplementacaoorc s on a.id = s.alteracaoorc_id " +
            " where s.origemsuplemtacao = 'SUPERAVIT' and a.DATAEFETIVACAO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  ";
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

    public BigDecimal calcularReceitaRealizadaAteBimestreSaldoExAnteriores(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio, String dataFinal) {
        this.exercicioCorrente = exercicio;
        BigDecimal despesaDotacaoInicial = calcularReceitaRealizadaAteBimestreSaldoExAnterioresImpl(itemDemonstrativo, dataFinal);
        return despesaDotacaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularReceitaRealizadaAteBimestreSaldoExAnterioresImpl(ItemDemonstrativo itDemonstrativo, String dataFinal) {
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
                        total = total.add(calcularReceitaRealizadaAteBimestreSaldoExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaAteBimestreSaldoExAnterioresQuery(dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaAteBimestreSaldoExAnterioresQuery(dataFinal));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularReceitaRealizadaAteBimestreSaldoExAnterioresImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaAteBimestreSaldoExAnterioresQuery(dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaAteBimestreSaldoExAnterioresQuery(dataFinal));
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
    public BigDecimal calcularReceitaRealizadaAteBimestreSaldoExAnterioresQuery(String dataFinal) {
        BigDecimal total;
        String sql = " select coalesce(sum(a.superavit), 0) as valor from alteracaoorc a " +
            " inner join suplementacaoorc s on a.id = s.alteracaoorc_id " +
            " where s.origemsuplemtacao = 'SUPERAVIT' and a.DATAEFETIVACAO BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", "01/01" + this.exercicioCorrente.getAno());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    @Deprecated
    //Utilizar o método buscarDotacaoInicial
    public BigDecimal calcularDotacaoInicialAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaDotacaoInicial = calcularDotacaoInicialAlteradoImpl(itemDemonstrativo, relatoriosItemDemonst);
        return despesaDotacaoInicial;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDotacaoInicialAlteradoImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        totalAdicao = totalAdicao.add(calcularDotacaoInicialAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDotacaoInicialAlteradoNaoRecursivo(formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDotacaoInicialAlteradoNaoRecursivo(formula, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        totalSubtracao = totalSubtracao.subtract(calcularDotacaoInicialAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDotacaoInicialAlteradoNaoRecursivo(formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDotacaoInicialAlteradoNaoRecursivo(formula, relatoriosItemDemonst));
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
    public BigDecimal calcularDotacaoInicialAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "SELECT COALESCE(sum(A.DOTACAO), 0) FROM SALDOFONTEDESPESAORC A  ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " inner join PROVISAOPPAFONTE ppf on b.PROVISAOPPAFONTE_ID = ppf.id "
                + " inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id "
                + " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "fr", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  ";
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
        sql += " INNER JOIN "
            + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, trunc(MAX(A.DATASALDO)) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND trunc(A.DATASALDO) = trunc(FONTES.MAXDATE) "
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

    @Deprecated
    //Utilizar o método buscarCreditosAdicionais
    public BigDecimal calcularCreditosAdicionaisAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaCreditosAdicionais = calcularCreditosAdicionaisAlteradoImpl(itemDemonstrativo, relatoriosItemDemonst, dataInicial, dataFinal);
        return despesaCreditosAdicionais;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularCreditosAdicionaisAlteradoImpl(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, String dataInicial, String dataFinal) {
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
                        total = total.add(calcularCreditosAdicionaisAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, dataInicial, dataFinal));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularCreditosAdicionaisAlteradoNaoRecursivo(formula, relatoriosItemDemonst, dataInicial, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularCreditosAdicionaisAlteradoNaoRecursivo(formula, relatoriosItemDemonst, dataInicial, dataFinal));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularCreditosAdicionaisAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst, dataInicial, dataFinal));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularCreditosAdicionaisAlteradoNaoRecursivo(formula, relatoriosItemDemonst, dataInicial, dataFinal).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularCreditosAdicionaisAlteradoNaoRecursivo(formula, relatoriosItemDemonst, dataInicial, dataFinal));
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
    public BigDecimal calcularCreditosAdicionaisAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst, String dataInicial, String dataFinal) {
        BigDecimal total;
        String sql = "SELECT COALESCE(sum(A.ALTERACAO), 0) as valor  FROM SALDOFONTEDESPESAORC A  ";
        sql += " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on d.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
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
        sql += " INNER JOIN "
            + " (SELECT sld.FONTEDESPESAORC_ID AS FONTE, trunc(MAX(sld.DATASALDO)) AS MAXDATE, sld.UNIDADEORGANIZACIONAL_ID FROM SALDOFONTEDESPESAORC sld  "
            + " where trunc(sld.DATASALDO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + "  GROUP BY sld.FONTEDESPESAORC_ID, sld.UNIDADEORGANIZACIONAL_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND trunc(A.DATASALDO) = trunc(FONTES.MAXDATE) and a.UNIDADEORGANIZACIONAL_ID = fontes.UNIDADEORGANIZACIONAL_ID "
            + "  WHERE C.EXERCICIO_ID = :EXERCICIO and trunc(a.DATASALDO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  ";
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

    public BigDecimal calcularDespesasEmpenhadasNoBimestreAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaNoBimestre = calcularDespesasEmpenhadasNoBimestreAlteradoImpl(itemDemonstrativo, dataInicial, dataFinal, relatoriosItemDemonst);
        return despesaEmpenhadaNoBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasEmpenhadasNoBimestreAlteradoImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = itemDemonstrativoFacade.recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula().equals(OperacaoFormula.ADICAO)) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDespesasEmpenhadasNoBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasEmpenhadasNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasEmpenhadasNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasEmpenhadasNoBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasEmpenhadasNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasEmpenhadasNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst));
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
    private BigDecimal calcularDespesasEmpenhadasNoBimestreAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = "SELECT sum(valor) as valor FROM( "
            + " SELECT COALESCE(sum(A.valor), 0) AS valor FROM empenho A  ";
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
        sql += " WHERE C.EXERCICIO_ID = :EXERCICIO and cast(a.dataempenho as Date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') "
            + " and a.categoriaorcamentaria = 'NORMAL'"
            + " union all "
            + " SELECT COALESCE(sum(emp.valor), 0) * - 1 AS valor FROM empenhoestorno emp "
            + " inner join empenho a on a.id = emp.empenho_id ";
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
        sql += " WHERE C.EXERCICIO_ID = :EXERCICIO and cast(emp.dataestorno as Date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') "
            + " and a.categoriaorcamentaria = 'NORMAL'"
            + " ) ";
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

    public BigDecimal calcularDespesasEmpenhadasAteOBimestreAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasEmpenhadasAteOBimestreAlteradoImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasEmpenhadasAteOBimestreAlteradoImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularDespesasEmpenhadasAteOBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasEmpenhadasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasEmpenhadasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasEmpenhadasAteOBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasEmpenhadasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasEmpenhadasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst));
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
    private BigDecimal calcularDespesasEmpenhadasAteOBimestreAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;

        String sql = " select sum(valor) from ( "
            + " SELECT COALESCE(sum(A.valor), 0) AS valor FROM empenho A ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " inner join PROVISAOPPAFONTE ppf on b.PROVISAOPPAFONTE_ID = ppf.id "
                + " inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id "
                + " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "fr", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  ";
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
        sql += " WHERE c.exercicio_id = :EXERCICIO and trunc(a.dataempenho) <= to_date(:DATAFINAL, 'dd/mm/yyyy') "
            + " and a.categoriaorcamentaria = 'NORMAL'"
            + " union all "
            + "SELECT COALESCE(sum(emp.valor), 0) * - 1 AS valor FROM empenhoestorno emp "
            + " inner join empenho a on emp.empenho_id = a.id ";
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += " inner join UNIDADEORGANIZACIONAL uo on a.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " inner join PROVISAOPPAFONTE ppf on b.PROVISAOPPAFONTE_ID = ppf.id "
                + " inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id "
                + " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "fr", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaTipoDespesa()) {
            sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaTipoDesp.class);
        }
        sql += " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  ";
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
        sql += " WHERE c.exercicio_id = :EXERCICIO and trunc(emp.dataestorno) <= to_date(:DATAFINAL, 'dd/mm/yyyy') "
            + " and a.categoriaorcamentaria = 'NORMAL' "
            + " ) ";
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

    @Deprecated
    public BigDecimal calcularDespesasLiquidadasNoBimestreAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasNoBimestre = calcularDespesasLiquidadasNoBimestreAlteradoImpl(itemDemonstrativo, dataInicial, dataFinal, relatoriosItemDemonst);
        return despesasLiquidadasNoBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasNoBimestreAlteradoImpl(ItemDemonstrativo itDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularDespesasLiquidadasNoBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasNoBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataInicial, dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasNoBimestreAlteradoNaoRecursivo(formula, dataInicial, dataFinal, relatoriosItemDemonst));
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
    public BigDecimal calcularDespesasLiquidadasNoBimestreAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataInicial, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT sum(valor) as valor FROM( "
            + " SELECT COALESCE(sum(liq.valor), 0) AS valor FROM liquidacao liq"
            + "  inner join empenho a on liq.empenho_id = a.id  ";
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
        sql += " WHERE C.EXERCICIO_ID = :EXERCICIO and cast(liq.DATALIQUIDACAO as date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy')"
            + " and a.categoriaorcamentaria = 'NORMAL' and liq.categoriaorcamentaria = 'NORMAL' "
            + " union all "
            + " SELECT COALESCE(sum(liqest.valor), 0) * - 1 AS valor FROM liquidacaoestorno liqest "
            + " inner join liquidacao liq on liqest.liquidacao_id = liq.id"
            + " inner join empenho a on liq.empenho_id = a.id ";
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
        sql += " WHERE C.EXERCICIO_ID = :EXERCICIO and cast(liqest.dataestorno as date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy')"
            + " and a.categoriaorcamentaria = 'NORMAL' and liq.categoriaorcamentaria = 'NORMAL' "
            + " ) ";
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

    public BigDecimal calcularRestoAPagarNaoProcessadosExAnterior(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularRestoAPagarNaoProcessadosExAnteriorImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularRestoAPagarNaoProcessadosExAnteriorImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = itemDemonstrativoFacade.recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            Long ano = Long.parseLong(dataFinal.substring(6, 10));
            String periodo = dataFinal.substring(0, 6) + (ano - 1);
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularRestoAPagarNaoProcessadosImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularRestoAPagarNaoProcessadosSql(formula, periodo, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularRestoAPagarNaoProcessadosSql(formula, periodo, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestoAPagarNaoProcessadosImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularRestoAPagarNaoProcessadosSql(formula, periodo, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularRestoAPagarNaoProcessadosSql(formula, periodo, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    @Deprecated
    //Utilizar o método buscarRestosAPagarNaoProcessados
    public BigDecimal calcularRestoAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasNoBimestre = calcularRestoAPagarNaoProcessadosImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return despesasLiquidadasNoBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularRestoAPagarNaoProcessadosImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularRestoAPagarNaoProcessadosImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularRestoAPagarNaoProcessadosSql(formula, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularRestoAPagarNaoProcessadosSql(formula, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestoAPagarNaoProcessadosImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularRestoAPagarNaoProcessadosSql(formula, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularRestoAPagarNaoProcessadosSql(formula, dataFinal, relatoriosItemDemonst));
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
    public BigDecimal calcularRestoAPagarNaoProcessadosSql(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(valor), 0) as valor from ( ")
            .append(" select sum(emp.valor) as valor from empenho emp ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID ")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ");
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql.append(" inner join UNIDADEORGANIZACIONAL uo on emp.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class));
        }
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql.append(" inner join PROVISAOPPAFONTE ppf on FONTDESP.PROVISAOPPAFONTE_ID = ppf.id ")
                .append(" inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id ")
                .append(" inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "fr", ComponenteFormulaFonteRecurso.class));
        }
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " + recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class));
        }
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao()) {
                sql.append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class));
            }
        }
        if (relatoriosItemDemonst.getUsaFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao()) {
                sql.append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class));
            }
        }
        if (relatoriosItemDemonst.getUsaPrograma()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao() || relatoriosItemDemonst.getUsaFuncao()) {
                sql.append(" INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class));
            }
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql.append(recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class));
        }
        sql.append(" where ")
            .append(" emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and emp.exercicio_id = ").append(":EXERCICIO_ID ")
            .append(" and trunc(emp.dataempenho) between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(e.valor) * -1 as valor from empenhoestorno e ")
            .append(" inner join empenho emp on emp.id = e.empenho_id ");
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql.append(" inner join UNIDADEORGANIZACIONAL uo on emp.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class));
        }
        sql.append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ");
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql.append(" inner join PROVISAOPPAFONTE ppf on FONTDESP.PROVISAOPPAFONTE_ID = ppf.id ")
                .append(" inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id ")
                .append(" inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "fr", ComponenteFormulaFonteRecurso.class));
        }
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " + recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class));
        }
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao()) {
                sql.append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class));
            }
        }
        if (relatoriosItemDemonst.getUsaFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao()) {
                sql.append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class));
            }
        }
        if (relatoriosItemDemonst.getUsaPrograma()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao() || relatoriosItemDemonst.getUsaFuncao()) {
                sql.append(" INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class));
            }
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql.append(recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class));
        }
        sql.append(" where ")
            .append(" emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and trunc(e.dataestorno) between to_date('01/01/' || ").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(l.valor) * -1 as valor from liquidacao l ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ");
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql.append(" inner join UNIDADEORGANIZACIONAL uo on emp.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class));
        }
        sql.append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ");

        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql.append(" inner join PROVISAOPPAFONTE ppf on FONTDESP.PROVISAOPPAFONTE_ID = ppf.id ")
                .append(" inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id ")
                .append(" inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "fr", ComponenteFormulaFonteRecurso.class));
        }
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " + recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class));
        }
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao()) {
                sql.append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class));
            }
        }
        if (relatoriosItemDemonst.getUsaFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao()) {
                sql.append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class));
            }
        }
        if (relatoriosItemDemonst.getUsaPrograma()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao() || relatoriosItemDemonst.getUsaFuncao()) {
                sql.append(" INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class));
            }
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql.append(recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class));
        }
        sql.append(" where ")
            .append(" l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and l.exercicio_id = ").append(":EXERCICIO_ID ")
            .append(" and trunc(l.dataliquidacao) between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" union all ")
            .append(" select sum(el.valor) as valor from liquidacaoestorno el ")
            .append(" inner join liquidacao l on l.id = el.liquidacao_id ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ");
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql.append(" inner join UNIDADEORGANIZACIONAL uo on emp.UNIDADEORGANIZACIONAL_ID = uo.id " + recuperaIds(formulaItemDemonstrativo, "uo", ComponenteFormulaUnidadeOrganizacional.class));
        }
        sql.append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ");
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql.append(" inner join PROVISAOPPAFONTE ppf on FONTDESP.PROVISAOPPAFONTE_ID = ppf.id ")
                .append(" inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id ")
                .append(" inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "fr", ComponenteFormulaFonteRecurso.class));
        }
        if (relatoriosItemDemonst.getUsaAcao()) {
            sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID " + recuperaIds(formulaItemDemonstrativo, "AC", ComponenteFormulaAcao.class));
        }
        if (relatoriosItemDemonst.getUsaSubFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao()) {
                sql.append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID " + recuperaIds(formulaItemDemonstrativo, "SF", ComponenteFormulaSubFuncao.class));
            }
        }
        if (relatoriosItemDemonst.getUsaFuncao()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao()) {
                sql.append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID " + recuperaIds(formulaItemDemonstrativo, "FU", ComponenteFormulaFuncao.class));
            }
        }
        if (relatoriosItemDemonst.getUsaPrograma()) {
            if (relatoriosItemDemonst.getUsaAcao() || relatoriosItemDemonst.getUsaSubFuncao() || relatoriosItemDemonst.getUsaFuncao()) {
                sql.append(" INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class));
            } else {
                sql.append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
                    .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
                    .append(" INNER JOIN PROGRAMAPPA PROG ON AC.PROGRAMA_ID = PROG.ID " + recuperaIds(formulaItemDemonstrativo, "PROG", ComponenteFormulaPrograma.class));
            }
        }
        if (relatoriosItemDemonst.getUsaConta()) {
            sql.append(recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class));
        }
        sql.append(" where ")
            .append(" l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and trunc(el.dataestorno) between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" ) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ANO_EXERCICIO", this.exercicioCorrente.getAno());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("EXERCICIO_ID", this.exercicioCorrente.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getResultList().get(0);
        }
    }

    @Deprecated
    //Utilizar o método buscarDespesasLiquidadasAteOBimestrePorCategoria
    public BigDecimal calcularDespesasLiquidadasAteOBimestreAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasAteOBimestre = calcularDespesasLiquidadasAteOBimestreAlteradoImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return despesasLiquidadasAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreAlteradoImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularDespesasLiquidadasAteOBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasAteOBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst));
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
    public BigDecimal calcularDespesasLiquidadasAteOBimestreAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT sum(valor) from ( "
            + " select COALESCE(sum(liq.valor), 0) AS valor FROM liquidacao liq"
            + "  inner join empenho a on liq.empenho_id = a.id  ";
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
        sql += " WHERE C.EXERCICIO_ID = :EXERCICIO and trunc(liq.DATALIQUIDACAO) <= TO_DATE(:DATAFINAL, 'dd/mm/yyyy')"
            + " and a.categoriaorcamentaria = 'NORMAL' and liq.categoriaorcamentaria = 'NORMAL' "
            + " union all "
            + "  SELECT COALESCE(sum(liqest.valor), 0) * - 1 AS valor FROM liquidacaoestorno liqest "
            + " inner join liquidacao liq on liqest.liquidacao_id = liq.id"
            + " inner join empenho a on liq.empenho_id = a.id ";
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
        sql += " WHERE C.EXERCICIO_ID = :EXERCICIO and trunc(liqest.dataestorno) <= TO_DATE(:DATAFINAL, 'dd/mm/yyyy')"
            + " and a.categoriaorcamentaria = 'NORMAL' and liq.categoriaorcamentaria = 'NORMAL' )";
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

    public BigDecimal calcularDespesasPagasAteOBimestreAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasAteOBimestre = calcularDespesasPagasAteOBimestreAlteradoImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return despesasLiquidadasAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasPagasAteOBimestreAlteradoImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularDespesasPagasAteOBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasPagasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasPagasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasPagasAteOBimestreAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasPagasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasPagasAteOBimestreAlteradoNaoRecursivo(formula, dataFinal, relatoriosItemDemonst));
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
    public BigDecimal calcularDespesasPagasAteOBimestreAlteradoNaoRecursivo(FormulaItemDemonstrativo formulaItemDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " SELECT sum(valor) from ( "
            + " select COALESCE(sum(pag.valorfinal), 0) AS valor FROM pagamento pag "
            + " inner join  liquidacao liq on pag.liquidacao_id = liq.id "
            + "  inner join empenho a on liq.empenho_id = a.id  ";
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
        sql += " WHERE C.EXERCICIO_ID = :EXERCICIO and cast(pag.datapagamento as date) <= TO_DATE(:DATAFINAL, 'dd/mm/yyyy')"
            + " and a.categoriaorcamentaria = 'NORMAL' and liq.categoriaorcamentaria = 'NORMAL' and pag.categoriaorcamentaria = 'NORMAL'  and pag.status <> 'ABERTO' "
            + " union all "
            + "  SELECT COALESCE(sum(pagest.valorfinal), 0) * - 1 AS valor FROM pagamentoestorno pagest "
            + " inner join pagamento pag on pagest.pagamento_id = pag.id"
            + " inner join liquidacao liq on pag.liquidacao_id = liq.id"
            + " inner join empenho a on liq.empenho_id = a.id ";
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
        sql += " WHERE C.EXERCICIO_ID = :EXERCICIO and cast(pagest.dataestorno as date) <= TO_DATE(:DATAFINAL, 'dd/mm/yyyy')"
            + " and a.categoriaorcamentaria = 'NORMAL' and liq.categoriaorcamentaria = 'NORMAL' and pag.categoriaorcamentaria = 'NORMAL'  and pag.status <> 'ABERTO' "
            + " union all "
            + " select COALESCE(sum(rec.valor), 0) AS valor FROM receitaextra rec "
            + " inner join RETENCAOPGTO ret on rec.RETENCAOPGTO_ID = ret.id "
            + " inner join pagamento pag on ret.PAGAMENTO_ID = pag.id  "
            + " inner join  liquidacao liq on pag.liquidacao_id = liq.id "
            + " inner join empenho a on liq.empenho_id = a.id  ";
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
        sql += " WHERE C.EXERCICIO_ID = :EXERCICIO and cast(rec.DATARECEITA as date) <= TO_DATE(:DATAFINAL, 'dd/mm/yyyy')"
            + " and a.categoriaorcamentaria = 'NORMAL' and liq.categoriaorcamentaria = 'NORMAL' and pag.categoriaorcamentaria = 'NORMAL'  and pag.status <> 'ABERTO' "

            + " union all "
            + " select COALESCE(sum(rec_est.valor), 0) * - 1 AS valor FROM receitaextraestorno rec_est "
            + " inner join receitaextra rec on rec_est.RECEITAEXTRA_ID = rec.id "
            + " inner join RETENCAOPGTO ret on rec.RETENCAOPGTO_ID = ret.id "
            + " inner join pagamento pag on ret.PAGAMENTO_ID = pag.id  "
            + " inner join  liquidacao liq on pag.liquidacao_id = liq.id "
            + " inner join empenho a on liq.empenho_id = a.id  ";
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
        sql += " WHERE C.EXERCICIO_ID = :EXERCICIO and cast(rec_est.DATAESTORNO as date) <= TO_DATE(:DATAFINAL, 'dd/mm/yyyy')"
            + " and a.categoriaorcamentaria = 'NORMAL' and liq.categoriaorcamentaria = 'NORMAL' and pag.categoriaorcamentaria = 'NORMAL'  and pag.status <> 'ABERTO' "
            + " )";
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

    public BigDecimal calcularDespesasLiquidadasAteOBimestreExAnteriorAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasLiquidadasAteOBimestreExAnteriorAlteradoImpl(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreExAnteriorAlteradoImpl(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = itemDemonstrativoFacade.recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            Long ano = Long.parseLong(dataFinal.substring(6, 10));
            String periodo = dataFinal.substring(0, 6) + (ano - 1);
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularDespesasLiquidadasAteOBimestreExAnteriorAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreAlteradoNaoRecursivo(formula, periodo, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularDespesasLiquidadasAteOBimestreAlteradoNaoRecursivo(formula, periodo, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularDespesasLiquidadasAteOBimestreExAnteriorAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreAlteradoNaoRecursivo(formula, periodo, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularDespesasLiquidadasAteOBimestreAlteradoNaoRecursivo(formula, periodo, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    public BigDecimal calcularReceitaRealizadaAteOBimestreExAnteriorAlterado(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal receitaRealizadaAteOBimestre = calcularReceitaRealizadaAteOBimestreImplExAnteriorAlterado(itemDemonstrativo, dataFinal, relatoriosItemDemonst);
        return receitaRealizadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularReceitaRealizadaAteOBimestreImplExAnteriorAlterado(ItemDemonstrativo itDemonstrativo, String dataFinal, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularReceitaRealizadaAteOBimestreImplExAnteriorAlterado(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaAteOBimestreAlteradoNaoRecursivo(formula, periodo, (this.exercicioCorrente.getAno() - 1), relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularReceitaRealizadaAteOBimestreAlteradoNaoRecursivo(formula, periodo, (this.exercicioCorrente.getAno() - 1), relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularReceitaRealizadaAteOBimestreImplExAnteriorAlterado(((ComponenteFormulaItem) componente).getItemDemonstrativo(), dataFinal, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaAteOBimestreAlteradoNaoRecursivo(formula, periodo, (this.exercicioCorrente.getAno() - 1), relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularReceitaRealizadaAteOBimestreAlteradoNaoRecursivo(formula, periodo, (this.exercicioCorrente.getAno() - 1), relatoriosItemDemonst));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    public String buscarComplementosQuery(FormulaItemDemonstrativo formulaItemDemonstrativo, String alias, Class classe, Class classeConta) {
        if (formulaItemDemonstrativo.getComponenteFormula().isEmpty()) {
            return "";
        }
        StringBuilder retorno = new StringBuilder();
        for (ComponenteFormula cf : formulaItemDemonstrativo.getComponenteFormula()) {
            if (cf.getClass().equals(classe)) {
                if (retorno.toString().isEmpty()) {
                    if (classe.equals(ComponenteFormulaFonteRecurso.class) || classe.equals(ComponenteFormulaConta.class)) {
                        retorno.append(" and (").append(alias).append(".codigo like '");
                    } else if (classe.equals(ComponenteFormulaTipoDesp.class)) {
                        retorno.append(" and (").append(alias).append(".TIPODESPESAORC like '");
                    } else {
                        retorno.append(" and ").append(alias).append(".id in (");
                    }
                }
                if (classe.equals(ComponenteFormulaFonteRecurso.class)) {
                    if (((ComponenteFormulaFonteRecurso) cf).getFonteDeRecursos() != null) {
                        retorno.append("%").append(((ComponenteFormulaFonteRecurso) cf).getFonteDeRecursos().getCodigo().length() == 3 ?
                            ((ComponenteFormulaFonteRecurso) cf).getFonteDeRecursos().getCodigo().substring(1, 3) :
                            ((ComponenteFormulaFonteRecurso) cf).getFonteDeRecursos().getCodigo()
                        ).append("' or ").append(alias).append(".codigo like '");
                    } else {
                        retorno.append("00' or ").append(alias).append(".codigo like '");
                    }

                } else if (classe.equals(ComponenteFormulaConta.class)) {
                    Conta contaConfiguracao = ((ComponenteFormulaConta) cf).getConta();
                    if (contaConfiguracao != null && classeConta.equals(contaConfiguracao.getClass())) {
                        retorno.append(((ComponenteFormulaConta) cf).getConta().getCodigoSemZerosAoFinal()).append("%' or ").append(alias).append(".codigo like '");
                    } else {
                        retorno.append("0' or ").append(alias).append(".codigo like '");
                    }
                } else if (classe.equals(ComponenteFormulaUnidadeOrganizacional.class)) {
                    retorno.append(((ComponenteFormulaUnidadeOrganizacional) cf).getUnidadeOrganizacional().getId()).append(",");
                } else if (classe.equals(ComponenteFormulaSubFuncao.class)) {
                    retorno.append(((ComponenteFormulaSubFuncao) cf).getSubFuncao().getId()).append(",");
                } else if (classe.equals(ComponenteFormulaSubConta.class)) {
                    retorno.append(((ComponenteFormulaSubConta) cf).getSubConta().getId()).append(",");
                } else if (classe.equals(ComponenteFormulaFuncao.class)) {
                    retorno.append(((ComponenteFormulaFuncao) cf).getFuncao().getId()).append(",");
                } else if (classe.equals(ComponenteFormulaPrograma.class)) {
                    retorno.append(((ComponenteFormulaPrograma) cf).getProgramaPPA().getId()).append(",");
                } else if (classe.equals(ComponenteFormulaAcao.class)) {
                    retorno.append(((ComponenteFormulaAcao) cf).getAcaoPPA().getId()).append(",");
                } else if (classe.equals(ComponenteFormulaSubacao.class)) {
                    retorno.append(((ComponenteFormulaSubacao) cf).getSubAcaoPPA().getId()).append(",");
                } else if (classe.equals(ComponenteFormulaTipoDesp.class)) {
                    retorno.append(((ComponenteFormulaTipoDesp) cf).getTipoDespesaORC().name()).append("' or ").append(alias).append(".TIPODESPESAORC like '");
                }
            }
        }

        if (retorno.toString().endsWith(" or " + alias + ".codigo like '")) {
            return retorno.substring(0, retorno.length() - (17 + alias.length())) + ")";
        } else if (retorno.toString().endsWith(" or " + alias + (".TIPODESPESAORC like '"))) {
            return retorno.substring(0, retorno.length() - (25 + alias.length())) + ")";
        } else if (!retorno.toString().isEmpty()) {
            return retorno.substring(0, retorno.length() - 1) + ")";
        }
        return "";
    }

    public String getAndVigenciaVwUnidade(String campoData) {
        return " and " + campoData + " between VW.iniciovigencia and coalesce(VW.fimvigencia, " + campoData + ") ";
    }

    public String adicionarParametros(ItemDemonstrativoFiltros filtros) {
        return UtilRelatorioContabil.concatenarParametros(filtros.getParametros(), 1, " and ");
    }

    public String adicionarFontesPelaFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ItemDemonstrativoFiltros filtros) {
        return filtros.getRelatorio().getUsaFonteRecurso() ? buscarComplementosQuery(formulaItemDemonstrativo, "font", ComponenteFormulaFonteRecurso.class, null) : "";
    }

    public String adicionarTiposDeDespesaPelaFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ItemDemonstrativoFiltros filtros) {
        return filtros.getRelatorio().getUsaTipoDespesa() ? buscarComplementosQuery(formulaItemDemonstrativo, "desp", ComponenteFormulaTipoDesp.class, null) : "";
    }

    public String adicionarUnidadesPelaFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ItemDemonstrativoFiltros filtros) {
        return filtros.getRelatorio().getUsaUnidadeOrganizacional() ? buscarComplementosQuery(formulaItemDemonstrativo, "und", ComponenteFormulaUnidadeOrganizacional.class, null) : "";
    }

    public String adicionarAcoesPelaFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ItemDemonstrativoFiltros filtros) {
        return filtros.getRelatorio().getUsaAcao() ? buscarComplementosQuery(formulaItemDemonstrativo, "ac", ComponenteFormulaAcao.class, null) : "";
    }

    public String adicionarSubacoesPelaFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ItemDemonstrativoFiltros filtros) {
        return filtros.getRelatorio().getUsaSubAcao() ? buscarComplementosQuery(formulaItemDemonstrativo, "sa", ComponenteFormulaSubacao.class, null) : "";
    }

    public String adicionarSubFuncoesPelaFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ItemDemonstrativoFiltros filtros) {
        return filtros.getRelatorio().getUsaSubFuncao() ? buscarComplementosQuery(formulaItemDemonstrativo, "sf", ComponenteFormulaSubFuncao.class, null) : "";
    }

    public String adicionarFuncoesPelaFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ItemDemonstrativoFiltros filtros) {
        return filtros.getRelatorio().getUsaFuncao() ? buscarComplementosQuery(formulaItemDemonstrativo, "fu", ComponenteFormulaFuncao.class, null) : "";
    }

    public String adicionarProgramasPelaFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ItemDemonstrativoFiltros filtros) {
        return filtros.getRelatorio().getUsaPrograma() ? buscarComplementosQuery(formulaItemDemonstrativo, "prog", ComponenteFormulaPrograma.class, null) : "";
    }

    public String adicionarSubContaPelaFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ItemDemonstrativoFiltros filtros) {
        return filtros.getRelatorio().getUsaContaFinanceira() ? buscarComplementosQuery(formulaItemDemonstrativo, "sub", ComponenteFormulaSubConta.class, null) : "";
    }

    public String adicionarContaPelaFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ItemDemonstrativoFiltros filtros, Class classeConta) {
        return filtros.getRelatorio().getUsaConta() ? buscarComplementosQuery(formulaItemDemonstrativo, "c", ComponenteFormulaConta.class, classeConta) : "";
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void setExercicio(Exercicio exercicioCorrente) {
        this.exercicioCorrente = exercicioCorrente;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public Exercicio getExercicioCorrente() {
        return exercicioCorrente;
    }

    public void setExercicioCorrente(Exercicio exercicioCorrente) {
        this.exercicioCorrente = exercicioCorrente;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
