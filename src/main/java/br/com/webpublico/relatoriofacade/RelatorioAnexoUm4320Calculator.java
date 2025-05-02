/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.enums.ClasseDaConta;
import br.com.webpublico.entidades.ComponenteFormula;
import br.com.webpublico.entidades.ComponenteFormulaAcao;
import br.com.webpublico.entidades.ComponenteFormulaConta;
import br.com.webpublico.entidades.ComponenteFormulaFonteRecurso;
import br.com.webpublico.entidades.ComponenteFormulaFuncao;
import br.com.webpublico.entidades.ComponenteFormulaItem;
import br.com.webpublico.entidades.ComponenteFormulaPrograma;
import br.com.webpublico.entidades.ComponenteFormulaSubFuncao;
import br.com.webpublico.entidades.ComponenteFormulaUnidadeOrganizacional;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FormulaItemDemonstrativo;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author reidocrime
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioAnexoUm4320Calculator {

    private ItemDemonstrativo itemDemonstrativo;
    private Exercicio exercicioCorrente;
    @EJB
    private ContaFacade contaFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;

    public BigDecimal calculaValor(ClasseDaConta classe, String dataInicial, String dataFinal, String filtro, ItemDemonstrativo itemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {

        BigDecimal valor = calculaValorImpl(classe, dataInicial, dataFinal, filtro, itemDemonstrativo, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaValorImpl(ClasseDaConta classeDaConta, String dataInicial, String dataFinal, String filtro, ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calculaValorImpl(classeDaConta, dataInicial, dataFinal, filtro, ((ComponenteFormulaItem) componente).getItemDemonstrativo(), relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                if (classeDaConta.equals(ClasseDaConta.DESPESA)) {
                                    totalAdicao = totalAdicao.add(despesa(dataInicial, dataFinal, filtro, formula, relatoriosItemDemonst));
                                } else {
                                    totalAdicao = totalAdicao.add(receita(dataInicial, dataFinal, filtro, formula, relatoriosItemDemonst));
                                }
                            } else {
                                if (classeDaConta.equals(ClasseDaConta.DESPESA)) {
                                    totalAdicao = totalAdicao.add(despesa(dataInicial, dataFinal, filtro, formula, relatoriosItemDemonst));
                                } else {
                                    totalAdicao = totalAdicao.add(receita(dataInicial, dataFinal, filtro, formula, relatoriosItemDemonst));
                                }
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        ItemDemonstrativo it = ((ComponenteFormulaItem) componente).getItemDemonstrativo();
                        total = total.subtract(calculaValorImpl(classeDaConta, dataInicial, dataFinal, filtro, it, relatoriosItemDemonst));
                        total = total.subtract(calculaValorImpl(classeDaConta, dataInicial, dataFinal, filtro, it, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                if (classeDaConta.equals(ClasseDaConta.DESPESA)) {
                                    totalSubtracao = totalSubtracao.subtract(despesa(dataInicial, dataFinal, filtro, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                                } else {
                                    totalSubtracao = totalSubtracao.subtract(receita(dataInicial, dataFinal, filtro, formula, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                                }
                            } else {
                                if (classeDaConta.equals(ClasseDaConta.DESPESA)) {
                                    totalSubtracao = totalSubtracao.subtract(despesa(dataInicial, dataFinal, filtro, formula, relatoriosItemDemonst));
                                } else {
                                    totalSubtracao = totalSubtracao.subtract(receita(dataInicial, dataFinal, filtro, formula, relatoriosItemDemonst));
                                }
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    public String recuperaIds(FormulaItemDemonstrativo formulaItemDemonstrativo, String alias, Class classe) {
        String retorno = "";
        for (ComponenteFormula cf : formulaItemDemonstrativo.getComponenteFormula()) {
            if (cf.getClass().equals(classe)) {
                if (retorno.trim().equals("")) {
                    if (classe.equals(ComponenteFormulaConta.class)) {
                        retorno = "and (" + alias + ".codigo like '";
                    } else {
                        retorno = "and " + alias + ".id in (";
                    }
                }
                if (classe.equals(ComponenteFormulaFonteRecurso.class)) {
                    retorno += ((ComponenteFormulaFonteRecurso) cf).getFonteDeRecursos().getId() + ",";
                } else if (classe.equals(ComponenteFormulaConta.class)) {
                    retorno += ((ComponenteFormulaConta) cf).getConta().getCodigoSemZerosAoFinal() + "%' or " + alias + ".codigo like '";
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
                }
            }
        }

        if (formulaItemDemonstrativo.getComponenteFormula().isEmpty()) {
            return "";
        }
        if (retorno.endsWith(" or " + alias + ".codigo like '")) {
            retorno = retorno.substring(0, retorno.length() - (17 + alias.length())) + ")";
        } else if (!retorno.trim().equals("")) {
            retorno = retorno.substring(0, retorno.length() - 1) + ")";
        }

        return retorno;
    }

    private BigDecimal receita(String dataInicial, String dataFinal, String filtro, FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        try {
            String sql = "SELECT ";
            sql += "      coalesce(sum(lanc.valor),0) ";
            sql += " FROM CONTA C ";
            sql += " INNER JOIN RECEITALOA REC ON rec.contadereceita_id=C.ID  ";
            sql += " INNER JOIN LANCAMENTORECEITAORC LANC   ON LANC.RECEITALOA_ID = REC.ID  ";
            sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = LANC.UNIDADEORGANIZACIONAL_ID  ";
            sql += " WHERE LANC.DATALANCAMENTO BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') ";
            sql += " AND LANC.DATALANCAMENTO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:DATAFINAL, 'DD/MM/YYYY'))  ";
            if (relatoriosItemDemonst.getUsaConta()) {
                sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
            }
            sql += filtro;

            Query q = this.em.createNativeQuery(sql);
            q.setParameter("DATAINICIAL", dataInicial);
            q.setParameter("DATAFINAL", dataFinal);
//            Util u = new Util();
//            u.imprimeSQL(sql, q);
            return (BigDecimal) q.getSingleResult();
        } catch (NonUniqueResultException ne) {
            throw new ExcecaoNegocioGenerica("O Item Demonstrativo não retornou nenhum resultado");
        } catch (NoResultException nr) {
            throw new ExcecaoNegocioGenerica("A receita retornou mais de um resultado para o Item Demonstrativo");
        }
    }

    private BigDecimal despesa(String dataInicial, String dataFinal, String filtro, FormulaItemDemonstrativo formulaItemDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        try {
            String sql = "   select  coalesce(sum(valor),0) from( ";
            sql += "SELECT ";
            sql += "     c.codigo as codigo, ";
            sql += "     COALESCE(emp.VALOR, 0) AS VALOR      ";
            sql += "FROM ";
            sql += "   EMPENHO EMP ";
            sql += "INNER JOIN fontedespesaorc DESP ";
            sql += "        ON desp.ID = emp.fontedespesaorc_id ";
            sql += "inner join provisaoppafonte ppf ";
            sql += "        on ppf.id= desp.provisaoppafonte_id ";
            sql += "INNER JOIN PROVISAOPPADESPESA PROVDESP ";
            sql += "        ON PROVDESP.ID =ppf.provisaoppadespesa_id ";
            sql += "INNER JOIN CONTA C ";
            sql += "        ON PROVDESP.CONTADEDESPESA_ID = C.ID ";
            if (relatoriosItemDemonst.getUsaConta()) {
                sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
            }
            sql += "INNER JOIN VWHIERARQUIAORCAMENTARIA VW ";
            sql += "        ON VW.SUBORDINADA_ID = EMP.UNIDADEORGANIZACIONAL_ID ";
            sql += "WHERE ";
            sql += "    EMP.DATAEMPENHO BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') ";
            sql += "AND cast(EMP.DATAEMPENHO as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:DATAFINAL, 'DD/MM/YYYY')) ";
            sql += "AND emp.categoriaorcamentaria ='NORMAL' ";
            sql += filtro;
            sql += "                 union all ";
            sql += "SELECT ";
            sql += "     c.codigo as codigo, ";
            sql += "     COALESCE(EST.VALOR, 0) * -1 AS VALOR ";
            sql += "FROM ";
            sql += "   EMPENHOESTORNO EST ";
            sql += "INNER JOIN EMPENHO EMP ";
            sql += "        ON EMP.ID =EST.EMPENHO_ID ";
            sql += "INNER JOIN fontedespesaorc DESP ";
            sql += "        ON desp.ID = emp.fontedespesaorc_id ";
            sql += "inner join provisaoppafonte ppf ";
            sql += "        on ppf.id= desp.provisaoppafonte_id ";
            sql += "INNER JOIN PROVISAOPPADESPESA PROVDESP ";
            sql += "        ON PROVDESP.ID =ppf.provisaoppadespesa_id ";
            sql += "INNER JOIN CONTA C ";
            sql += "        ON PROVDESP.CONTADEDESPESA_ID = C.ID ";
            if (relatoriosItemDemonst.getUsaConta()) {
                sql += recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
            }
            sql += "INNER JOIN VWHIERARQUIAORCAMENTARIA VW ";
            sql += "        ON VW.SUBORDINADA_ID = EMP.UNIDADEORGANIZACIONAL_ID ";
            sql += "WHERE ";
            sql += "    CAST(EST.DATAESTORNO AS DATE) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') ";
            sql += "AND cast(EST.DATAESTORNO  as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:DATAFINAL, 'DD/MM/YYYY')) ";
            sql += "AND emp.categoriaorcamentaria ='NORMAL'" + filtro + " )dados ";

            Query q = this.em.createNativeQuery(sql);
            q.setParameter("DATAINICIAL", dataInicial);
            q.setParameter("DATAFINAL", dataFinal);

//
//            Util u = new Util();
//            u.imprimeSQL(sql, q);
            return (BigDecimal) q.getSingleResult();
        } catch (NonUniqueResultException ne) {
            throw new ExcecaoNegocioGenerica("O Item Demonstrativo não retornou nenhum resultado");
        } catch (NoResultException nr) {
            throw new ExcecaoNegocioGenerica("A receita retornou mais de um resultado para o Item Demonstrativo");
        }

    }
}
