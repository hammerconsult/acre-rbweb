/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author juggernaut
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioRREOAnexo03Calculator extends ItemDemonstrativoCalculator {

    private ItemDemonstrativo itemDemonstrativo;

    public RelatorioRREOAnexo03Calculator() {
    }

    public BigDecimal calcular(ItemDemonstrativo itemDemonstrativo, String data) {
        this.itemDemonstrativo = itemDemonstrativo;
        Integer anoParametro = Integer.parseInt(data.substring(3, 7));
        BigDecimal valor = calcularImpl(this.itemDemonstrativo, data, anoParametro);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularImpl(ItemDemonstrativo itDemonstrativo, String data, Integer ano) {
        BigDecimal total = BigDecimal.ZERO;
        itDemonstrativo = this.getEm().merge(itDemonstrativo);
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            for (ComponenteFormula componente : componentes) {
                if (componente instanceof ComponenteFormulaItem) {
                    if (componente.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                        total = total.add(calcularImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), data, ano));
                    } else {
                        total = total.subtract(calcularImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), data, ano));
                    }
                } else {
                    if (((ComponenteFormulaConta) componente).getConta() != null) {
                        if (componente.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                            total = total.add(calcularSql(((ComponenteFormulaConta) componente).getConta(), data, ano));
                        } else {
                            total = total.subtract(calcularSql(((ComponenteFormulaConta) componente).getConta(), data, ano));
                        }
                    }
                }
            }
        }
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularSql(Conta conta, String data, Integer ano) {

        BigDecimal total;
        String sql = " SELECT COALESCE(SUM(A.VALOR),0)"
            + " FROM LANCAMENTORECEITAORC A "
            + " INNER JOIN RECEITALOA B ON A.RECEITALOA_ID = B.ID "
            + " INNER JOIN CONTA C ON B.CONTADERECEITA_ID = C.ID "
            + " INNER JOIN LOA D ON B.LOA_ID = D.ID "
            + " INNER JOIN LDO ldo ON D.LDO_ID = ldo.ID "
            + " INNER JOIN EXERCICIO E ON LDO.EXERCICIO_ID = E.ID "
            + " WHERE C.CODIGO LIKE :CONTA AND TO_CHAR(A.DATALANCAMENTO, 'MM/yyyy') = TO_CHAR(TO_DATE(:DATA, 'MM/yyyy'),'MM/yyyy') "
            + " AND E.ANO = :ANO ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CONTA", conta.getCodigoSemZerosAoFinal() + "%");
        q.setParameter("DATA", data);
        q.setParameter("ANO", ano);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal recuperarValorPeloMesAnterior(ItemDemonstrativo itemDemonstrativo, String data, RelatoriosItemDemonst relatoriosItemDemonst, Exercicio exercicioLogado) {
        Integer ano = Integer.parseInt(data.substring(3, 7));
        if (ano.compareTo(exercicioLogado.getAno()) < 0) {
            return calcularAlterado(getItemExercicioAnterior(itemDemonstrativo), data, relatoriosItemDemonst);
        } else {
            return calcularAlterado(itemDemonstrativo, data, relatoriosItemDemonst);
        }
    }

    private ItemDemonstrativo getItemExercicioAnterior(ItemDemonstrativo itemDemonstrativo) {
        return itemDemonstrativo.getItemExercicioAnterior() != null ? itemDemonstrativo.getItemExercicioAnterior() : itemDemonstrativo;
    }

    public BigDecimal calcularAlterado(ItemDemonstrativo itemDemonstrativo, String data, RelatoriosItemDemonst relatoriosItemDemonst) {
        Integer anoParametro = Integer.parseInt(data.substring(3, 7));
        BigDecimal valor = calcularAlteradoImpl(itemDemonstrativo, data, anoParametro, relatoriosItemDemonst);
        return valor;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularAlteradoImpl(ItemDemonstrativo itDemonstrativo, String data, Integer ano, RelatoriosItemDemonst relatoriosItemDemonst) {
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
                        total = total.add(calcularAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), data, ano, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calcularAlteradoSql(formula, data, ano, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalAdicao = totalAdicao.add(calcularAlteradoSql(formula, data, ano, relatoriosItemDemonst));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularAlteradoImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), data, ano, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calcularAlteradoSql(formula, data, ano, relatoriosItemDemonst).multiply(new BigDecimal(-1)));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calcularAlteradoSql(formula, data, ano, relatoriosItemDemonst));
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
    private BigDecimal calcularAlteradoSql(FormulaItemDemonstrativo formulaItemDemonstrativo, String data, Integer ano, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total;
        String sql = " select coalesce(SUM(VALOR), 0) as valor from ( " +
            " select sum(a.valor) as valor " +
            " FROM LANCAMENTORECEITAORC A  " +
            " inner join RECEITALOA B on a.RECEITALOA_ID = B.id ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON B.ID = RLF.RECEITALOA_ID ";
            sql += " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID ";
            sql += " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on B.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " INNER JOIN CONTA C ON B.CONTADERECEITA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "c", ComponenteFormulaConta.class);
        }
        sql += " WHERE TO_CHAR(A.DATALANCAMENTO, 'MM/yyyy') = TO_CHAR(TO_DATE(:DATA, 'MM/yyyy'),'MM/yyyy') " +
            " union all " +
            " select coalesce(sum(recest.valor) * -1, 0) as valor from " +
            " RECEITAORCESTORNO RECEST " +
            " inner join RECEITALOA B on RECEST.RECEITALOA_ID = B.id ";
        if (relatoriosItemDemonst.getUsaFonteRecurso()) {
            sql += " INNER JOIN ReceitaLOAFonte rlf ON B.ID = RLF.RECEITALOA_ID ";
            sql += " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID ";
            sql += " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " + recuperaIds(formulaItemDemonstrativo, "FR", ComponenteFormulaFonteRecurso.class);
        }
        if (relatoriosItemDemonst.getUsaUnidadeOrganizacional()) {
            sql += "inner join unidadeorganizacional UO on B.entidade_id = uo.id " + recuperaIds(formulaItemDemonstrativo, "UO", ComponenteFormulaUnidadeOrganizacional.class);
        }
        sql += " inner join CONTA C on B.CONTADERECEITA_ID = C.id ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += recuperaIds(formulaItemDemonstrativo, "c", ComponenteFormulaConta.class);
        }
        sql += " WHERE TO_CHAR(RECEST.DATAESTORNO, 'MM/yyyy') = TO_CHAR(TO_DATE(:DATA, 'MM/yyyy'),'MM/yyyy')) ";

        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("DATA", data);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularRcl(Mes mesFinal, Exercicio exercicio) {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", exercicio, relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(exercicio.getAno(), (mesFinal.getNumeroMes() - 1), new Integer(1));
            String data = Util.formatterMesAno.format(dataCalendar.getTime());
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, exercicio));
                data = alterarMes(data);
            }
            return toReturn;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal calcularRclExercicioAnterior(Mes mesFinal) {
        Exercicio exercicioAnterior = getExercicioFacade().getExercicioPorAno(getSistemaFacade().getExercicioCorrente().getAno() - 1);
        return calcularRcl(mesFinal, exercicioAnterior);
    }

    private String alterarMes(String data) {
        Integer mes = Integer.parseInt(data.substring(0, 2));
        Integer ano = Integer.parseInt(data.substring(3, 7));
        if (mes != 1) {
            mes -= 1;
        } else {
            mes = 12;
            ano -= 1;
        }
        String toReturn;
        if (mes < 10) {
            toReturn = "0" + mes + "/" + ano;
        } else {
            toReturn = mes + "/" + ano;
        }
        return toReturn;
    }
}
