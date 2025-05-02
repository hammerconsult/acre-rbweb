/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoFormula;
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
 * @author juggernaut
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioRGFAnexo04Calculator2017 extends ItemDemonstrativoCalculator implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Exercicio exercicioCorrente;
    private ItemDemonstrativo itemDemonstrativo;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;

    public RelatorioRGFAnexo04Calculator2017() {
    }

//    public CategoriaDividaPublica recuperarCategoriaDividaPublicaPeloCodigo(String codigo) {
//        Query q = this.getEm().createQuery(" select cdp from CategoriaDividaPublica cdp "
//                + " where cdp.codigo like :codigo");
//        q.setParameter("codigo", codigo);
//        //System.out.println("Codigo: " + codigo);
//        CategoriaDividaPublica cdp = (CategoriaDividaPublica) q.getSingleResult();
//        return cdp;
//    }
//
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
//    public BigDecimal calcularReceitaRealizadaNoQuadrimestre(String codigo, String dataInicial, String dataFinal) {
//        BigDecimal total;
//        Query q = this.getEm().createNativeQuery("SELECT COALESCE(sum(SDP.INSCRICAO + SDP.ATUALIZACAO + SDP.RECEITA) - SUM(SDP.APROPRIACAO + SDP.PAGAMENTO + SDP.CANCELAMENTO), 0) AS VALOR "
//                + " FROM SALDODIVIDAPUBLICA SDP "
//                + " INNER JOIN DIVIDAPUBLICA DP ON SDP.DIVIDAPUBLICA_ID = DP.ID "
//                + " INNER JOIN CATEGORIADIVIDAPUBLICA CDP ON DP.CATEGORIADIVIDAPUBLICA_ID = CDP.ID "
//                + " WHERE SDP.DATA IN (SELECT MAX(SDP.DATA) FROM SALDODIVIDAPUBLICA SDP WHERE SDP.DATA BETWEEN :DATAINICIAL AND :DATAFINAL GROUP BY SDP.UNIDADEORGANIZACIONAL_ID)"
//                + " AND CDP.CODIGO LIKE :CODIGO");
//        q.setParameter("DATAFINAL", dataFinal);
//        q.setParameter("DATAINICIAL", dataInicial);
//        q.setParameter("CODIGO", codigo + "%");
//        q.setMaxResults(1);
//        if (!q.getResultList().isEmpty()) {
//            total = (BigDecimal) q.getResultList().get(0);
//        } else {
//            total = BigDecimal.ZERO;
//        }
//        return total;
//    }
//
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
//    public BigDecimal calcularReceitaRealizadaAteOQuadrimestre(String codigo, String dataInicial, String dataFinal) {
//        BigDecimal total;
//        Query q = this.getEm().createNativeQuery("SELECT COALESCE(sum(SDP.INSCRICAO + SDP.ATUALIZACAO + SDP.RECEITA) - SUM(SDP.APROPRIACAO + SDP.PAGAMENTO + SDP.CANCELAMENTO), 0) AS VALOR "
//                + " FROM SALDODIVIDAPUBLICA SDP "
//                + " INNER JOIN DIVIDAPUBLICA DP ON SDP.DIVIDAPUBLICA_ID = DP.ID "
//                + " INNER JOIN CATEGORIADIVIDAPUBLICA CDP ON DP.CATEGORIADIVIDAPUBLICA_ID = CDP.ID "
//                + " WHERE SDP.DATA IN (SELECT MAX(SDP.DATA) FROM SALDODIVIDAPUBLICA SDP WHERE SDP.DATA BETWEEN :DATAINICIAL AND :DATAFINAL GROUP BY SDP.UNIDADEORGANIZACIONAL_ID)"
//                + " AND CDP.CODIGO LIKE :CODIGO");
//        q.setParameter("DATAFINAL", dataFinal);
//        q.setParameter("DATAINICIAL", dataInicial);
//        q.setParameter("CODIGO", codigo + "%");
//        q.setMaxResults(1);
//        if (!q.getResultList().isEmpty()) {
//            total = (BigDecimal) q.getResultList().get(0);
//        } else {
//            total = BigDecimal.ZERO;
//        }
//        return total;
//    }
//
//    public BigDecimal calcularReceitaLiquida(ItemDemonstrativo itemDemonstrativo, Exercicio exercicio) {
//        this.itemDemonstrativo = itemDemonstrativo;
//        this.exercicioCorrente = exercicio;
//        BigDecimal valor = calcularReceitaLiquidaImpl(this.itemDemonstrativo);
//        return valor;
//    }
//
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
//    private BigDecimal calcularReceitaLiquidaImpl(ItemDemonstrativo itDemonstrativo) {
//        BigDecimal total = BigDecimal.ZERO;
//        itDemonstrativo = this.getEm().merge(itDemonstrativo);
//        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
//        for (FormulaItemDemonstrativo formula : formulas) {
//            List<ComponenteFormula> componentes = formula.getComponenteFormula();
//            for (ComponenteFormula componente : componentes) {
//                if (componente instanceof ComponenteFormulaItem) {
//                    if (componente.getOperacaoFormula() == OperacaoFormula.ADICAO) {
//                        total = total.add(calcularReceitaLiquidaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo()));
//                    } else {
//                        total = total.subtract(calcularReceitaLiquidaImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo()));
//                    }
//                } else {
//                    if (((ComponenteFormulaConta) componente).getConta() != null) {
//                        if (componente.getOperacaoFormula() == OperacaoFormula.ADICAO) {
////                            if (itDemonstrativo.getDescricao().equals("Dedução de Receita para Formação do FUNDEB")) {
////                                total = total.add(calcularQuadrimestreReceitaLiquidaSqlDeducao(((ComponenteFormulaConta) componente).getConta(), dataInicial, dataFinal));
////                            } else {
////                                total = total.add(calcularQuadrimestreReceitaLiquidaSql(((ComponenteFormulaConta) componente).getConta(), dataInicial, dataFinal));
//                            total = total.add(calcularReceitaLiquidaSql(((ComponenteFormulaConta) componente).getConta()));
////                            }
//                        } else {
////                            if (itDemonstrativo.getDescricao().equals("Dedução de Receita para Formação do FUNDEB")) {
////                                total = total.subtract(calcularQuadrimestreReceitaLiquidaSql(((ComponenteFormulaConta) componente).getConta(), dataInicial, dataFinal));
////                            } else {
////                                total = total.subtract(calcularQuadrimestreReceitaLiquidaSql(((ComponenteFormulaConta) componente).getConta(), dataInicial, dataFinal));
//                            total = total.subtract(calcularReceitaLiquidaSql(((ComponenteFormulaConta) componente).getConta()));
////                            }
//                        }
//                    }
//                }
//            }
//        }
//        return total;
//    }
//
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
//    private BigDecimal calcularReceitaLiquidaSql(Conta conta) {
//        BigDecimal total;
//        String sql = " SELECT COALESCE(SUM(A.VALOR),0) - COALESCE(SUM(RECEST.VALOR), 0) "
//                + " FROM LANCAMENTORECEITAORC A "
//                + " INNER JOIN RECEITALOA B ON A.RECEITALOA_ID = B.ID "
//                + "  LEFT JOIN RECEITAORCESTORNO recest ON RECEST.LANCAMENTORECEITAORC_ID = A.ID "
//                + " INNER JOIN CONTA C ON B.CONTADERECEITA_ID = C.ID "
//                + " INNER JOIN LOA D ON B.LOA_ID = D.ID "
//                + " INNER JOIN LDO ldo ON D.LDO_ID = ldo.ID "
//                + " INNER JOIN EXERCICIO E ON LDO.EXERCICIO_ID = E.ID "
////                + " WHERE C.CODIGO LIKE :CONTA AND TO_CHAR(A.DATALANCAMENTO, 'MM/yyyy') between :DATAINICIAL AND :DATAFINAL "
//                + " WHERE C.CODIGO LIKE :CONTA "
//                + " AND E.id = :exercicio  ";
//        Query q = this.getEm().createNativeQuery(sql);
//        q.setParameter("CONTA", conta.getCodigoSemZerosAoFinal() + "%");
//        q.setParameter("exercicio", this.exercicioCorrente.getId());
////        q.setParameter("DATAINICIAL", dataInicial);
////        q.setParameter("DATAFINAL", dataFinal);
//        q.setMaxResults(1);
//        if (!q.getResultList().isEmpty()) {
//            total = (BigDecimal) q.getResultList().get(0);
//        } else {
//            total = BigDecimal.ZERO;
//        }
//        return total;
//    }

//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
//    private BigDecimal calcularQuadrimestreReceitaLiquidaSqlDeducao(Conta conta, String dataInicial, String dataFinal) {
//        BigDecimal total;
//        String sql = " SELECT COALESCE(SUM(A.VALOR),0) - COALESCE(SUM(RECEST.VALOR), 0) "
//                + " FROM LANCAMENTORECEITAORC A "
//                + "  LEFT JOIN RECEITAORCESTORNO recest on RECEST.LANCAMENTORECEITAORC_ID = A.ID "
//                + " INNER JOIN RECEITALOA B ON A.RECEITALOA_ID = B.ID "
//                + " INNER JOIN CONTA C ON B.CONTADERECEITA_ID = C.ID "
//                + " INNER JOIN LOA D ON B.LOA_ID = D.ID "
//                + " INNER JOIN LDO ldo ON D.LDO_ID = ldo.ID "
//                + " INNER JOIN EXERCICIO E ON LDO.EXERCICIO_ID = E.ID "
//                + " WHERE C.CODIGO LIKE :CONTA AND TO_CHAR(A.DATALANCAMENTO, 'MM/yyyy') between :DATAINICIAL AND :DATAFINAL "
//                + " AND E.id = :exercicio  ";
//        Query q = this.getEm().createNativeQuery(sql);
//        q.setParameter("CONTA", conta.getCodigoSemZerosAoFinal() + "%");
//        q.setParameter("exercicio", this.exercicioCorrente.getId());
//        q.setParameter("DATAINICIAL", dataInicial);
//        q.setParameter("DATAFINAL", dataFinal);
//        q.setMaxResults(1);
//        if (!q.getResultList().isEmpty()) {
//            total = (BigDecimal) q.getResultList().get(0);
//        } else {
//            total = BigDecimal.ZERO;
//        }
//        return total;
//    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
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

    public RelatorioRREOAnexo03Calculator getRelatorioRREOAnexo03Calculator() {
        return relatorioRREOAnexo03Calculator;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }
}
