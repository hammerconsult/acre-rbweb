/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Funcao;
import br.com.webpublico.entidades.SubFuncao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author juggernaut
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioRREOAnexo02Calculator2018 extends ItemDemonstrativoCalculator {

    private Exercicio exercicioCorrente;

    public BigDecimal calcularDotacaoInicialAnexo02(Exercicio exercicioCorrente, Funcao funcao, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDotacaoInicialAnexo02Sql(funcao, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDotacaoInicialAnexo02Sql(Funcao funcao, String clausula) {
        BigDecimal total;
        String sql = " SELECT COALESCE(sum(A.DOTACAO), 0) FROM SALDOFONTEDESPESAORC A  "
            + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID "
            + " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID "
            + " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID "
            + " INNER JOIN (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND A.DATASALDO = FONTES.MAXDATE "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO "
            + "   And " + clausula + " (substr(e.CODIGO,5,2) = '91')  ";
        if (funcao != null) {
            sql += "   AND FU.id = :id ";
        }
        Query q = this.getEm().createNativeQuery(sql);
        if (funcao != null) {
            q.setParameter("id", funcao.getId());
        }
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularDotacaoAtualizadaAnexo02(Exercicio exercicioCorrente, String codigo, String dataInicial, String dataFinal, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDotacaoAtualizadaAnexo02Sql(codigo, dataInicial, dataFinal, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDotacaoAtualizadaAnexo02Sql(String codigo, String dataInicial, String dataFinal, String clausula) {
        BigDecimal total;
        String sql = "SELECT COALESCE(sum(A.ALTERACAO), 0) FROM SALDOFONTEDESPESAORC A  "
            + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID "
            + " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID "
            + " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID "
            + " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  "
            + " INNER JOIN "
            + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " where a.DATASALDO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + "  GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND A.DATASALDO = FONTES.MAXDATE "
            + "  WHERE C.EXERCICIO_ID = :EXERCICIO and a.DATASALDO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') ";
        if (codigo != null) {
            sql += " AND FU.CODIGO = :CODIGO ";
        }
//        String sql = " SELECT SUM(DOTACAO_ATUALIZADA) AS DOTACAO_ATUALIZADA FROM ("
//                + "SELECT "
//                + " coalesce(SUM(pd.valor), 0) AS dotacao_atualizada "
//                + " from despesaorc d "
//                + " inner join PROVISAOPPADESPESA PD on pd.id = d.provisaoppadespesa_id "
//                + " inner join unidadeorganizacional uni on pd.unidadeorganizacional_id = uni.id "
//                + " INNER JOIN CONTA E ON pd.CONTADEDESPESA_ID = E.ID "
//                + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = pd.subacaoppa_id "
//                + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
//                + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
//                + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
//                + " where d.EXERCICIO_ID = :EXERCICIO ";
//        if (codigo != null) {
//            sql += " AND F.CODIGO = :CODIGO ";
//        }
//        sql += " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') "
//                + " UNION ALL "
//                + " SELECT "
//                + " COALESCE(SUM(SU.VALOR), 0) AS dotacao_atualizada "
//                + " from SUPLEMENTACAOORC SU "
//                + " INNER JOIN ALTERACAOORC ALT ON ALT.ID = SU.ALTERACAOORC_ID "
//                + " INNER JOIN FONTEDESPESAORC FONTE ON FONTE.ID = SU.FONTEDESPESAORC_ID "
//                + " inner join despesaorc d on d.id = fonte.despesaorc_id "
//                + " inner join PROVISAOPPADESPESA PD on pd.id = d.provisaoppadespesa_id "
//                + " INNER JOIN CONTA E ON pd.CONTADEDESPESA_ID = E.ID "
//                + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = pd.subacaoppa_id "
//                + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
//                + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
//                + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
//                + " WHERE ALT.DATAALTERACAO between TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') "
//                + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') ";
//        if (codigo != null) {
//            sql += " AND F.CODIGO = :CODIGO ";
//        }
//        sql += " AND d.EXERCICIO_ID = :EXERCICIO "
//                + " UNION ALL "
//                + " SELECT "
//                + " COALESCE(SUM(ANUL.VALOR), 0) * - 1 AS dotacao_atualizada "
//                + " FROM ANULACAOORC ANUL "
//                + " INNER JOIN ALTERACAOORC ALT ON ALT.ID = ANUL.ALTERACAOORC_ID "
//                + " INNER JOIN FONTEDESPESAORC FONTE ON FONTE.ID = ANUL.FONTEDESPESAORC_ID "
//                + " inner join despesaorc d on d.id = fonte.despesaorc_id "
//                + " inner join PROVISAOPPADESPESA PD on pd.id = d.provisaoppadespesa_id "
//                + " INNER JOIN CONTA E ON pd.CONTADEDESPESA_ID = E.ID "
//                + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = pd.subacaoppa_id "
//                + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
//                + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
//                + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
//                + " WHERE ALT.DATAALTERACAO between TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') "
//                + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') ";
//        if (codigo != null) {
//            sql += " AND F.CODIGO = :CODIGO ";
//        }
//        sql += " AND d.EXERCICIO_ID = :EXERCICIO "
//                + ")";

        Query q = this.getEm().createNativeQuery(sql);
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
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

    public BigDecimal calcularDespesasEmpenhadasNoBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String dataInicial, String dataFinal, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasEmpenhadasNoBimestreAnexo02Sql(codigo, dataInicial, dataFinal, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDespesasEmpenhadasNoBimestreAnexo02Sql(String codigo, String dataInicial, String dataFinal, String clausula) {
        BigDecimal total;
        String sql = " select coalesce(sum(valor),0 ) as valor from ( "
            + " select coalesce(sum(emp.valor), 0) as valor from empenho emp "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and emp.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')"
            + " and emp.categoriaorcamentaria = 'NORMAL' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " union all "
            + " select coalesce(sum(est.valor), 0) * -1 as valor from empenhoestorno est "
            + " inner join empenho emp on emp.id = est.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and est.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')"
            + " and emp.categoriaorcamentaria = 'NORMAL' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " ) ";

        Query q = this.getEm().createNativeQuery(sql);
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
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

    public BigDecimal calcularDespesasEmpenhadasAteOBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String dataFinal, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasEmpenhadasAteOBimestreAnexo02Sql(codigo, dataFinal, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDespesasEmpenhadasAteOBimestreAnexo02Sql(String codigo, String dataFinal, String clausula) {
        BigDecimal total;
        String sql = " select sum(valor) from ( "
            + " select coalesce(sum(emp.valor), 0) as valor "
            + " from empenho emp "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and emp.dataempenho between to_date('01/01/" + exercicioCorrente.getAno() + "' , 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'NORMAL' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " union all "
            + " select coalesce(sum(est.valor), 0) * -1 as valor from empenhoestorno est "
            + " inner join empenho emp on emp.id = est.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and est.dataestorno between to_date('01/01/" + exercicioCorrente.getAno() + "' , 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'NORMAL' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
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

    public BigDecimal calcularDespesasEmpenhadasAteOBimestreAnexo02Resto(Exercicio exercicioCorrente, String codigo, String dataFinal, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasEmpenhadasAteOBimestreAnexo02RestoSql(codigo, dataFinal, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDespesasEmpenhadasAteOBimestreAnexo02RestoSql(String codigo, String dataFinal, String clausula) {
        BigDecimal total;
        String sql = " select sum(valor) from ( "
            + " select coalesce(sum(emp.valor), 0) as valor "
            + " from empenho emp "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and emp.dataempenho between to_date('01/01/" + exercicioCorrente.getAno() + "' , 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'RESTO' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " union all "
            + " select  coalesce(sum(est.valor), 0) * -1 as valor from empenhoestorno est "
            + " inner join empenho emp on emp.id = est.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and est.dataestorno between to_date('01/01/" + exercicioCorrente.getAno() + "' , 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'RESTO' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
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

    public BigDecimal calcularDespesasLiquidadasNoBimestreAnexo02(Exercicio exercicioCorrente, Long id, String dataInicial, String dataFinal, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasLiquidadasNoBimestreAnexo02Sql(id, dataInicial, dataFinal, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDespesasLiquidadasNoBimestreAnexo02Sql(Long id, String dataInicial, String dataFinal, String clausula) {
        BigDecimal total;
        String sql = " select coalesce(sum(valor), 0) as valor from ( "
            + " select coalesce(sum(liq.valor), 0) as valor from liquidacao liq "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and liq.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' ";
        if (id != null) {
            sql += " AND F.id = :id ";
        }
        sql += " union all "
            + " select coalesce(sum(est.valor), 0) * -1 as valor from liquidacaoestorno est "
            + " inner join liquidacao liq on liq.id = est.liquidacao_id "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and est.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' ";
        if (id != null) {
            sql += " AND F.id = :id ";
        }
        sql += " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        if (id != null) {
            q.setParameter("id", id);
        }
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularDespesasLiquidadasAteOBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String dataFinal, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasLiquidadasAteOBimestreAnexo02Sql(codigo, dataFinal, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDespesasLiquidadasAteOBimestreAnexo02Sql(String codigo, String dataFinal, String clausula) {
        BigDecimal total;
        String sql = " select coalesce(sum(valor), 0) from ( "
            + " select coalesce(sum(liq.valor), 0) as valor from liquidacao liq "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and liq.dataliquidacao between to_date('01/01/" + exercicioCorrente.getAno() + "', 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " union all "
            + " select coalesce(sum(est.valor), 0) * -1 as valor from liquidacaoestorno est "
            + " inner join liquidacao liq on liq.id = est.liquidacao_id "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and est.dataestorno between to_date('01/01/" + exercicioCorrente.getAno() + "', 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " ) ";

        Query q = this.getEm().createNativeQuery(sql);
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
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

    public BigDecimal calcularDespesasLiquidadasAteOBimestreAnexo02Resto(Exercicio exercicioCorrente, String codigo, String dataFinal, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasLiquidadasAteOBimestreAnexo02RestoSql(codigo, dataFinal, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDespesasLiquidadasAteOBimestreAnexo02RestoSql(String codigo, String dataFinal, String clausula) {
        BigDecimal total;
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(valor), 0) as valor from ( ")
            .append(" select sum(emp.valor) as valor from empenho emp ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID ")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where  emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and emp.exercicio_id = ").append(":EXERCICIO ")
            .append(codigo != null ? " AND FU.CODIGO = :CODIGO " : "")
            .append(" and " + clausula + " (substr(c.CODIGO,5,2) = '91') ")
            .append(" and emp.dataempenho between to_date('01/01/' ||").append(exercicioCorrente.getAno()).append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(e.valor) * -1 as valor from empenhoestorno e ")
            .append(" inner join empenho emp on emp.id = e.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where  emp.categoriaorcamentaria = 'NORMAL' ")
            .append(codigo != null ? " AND FU.CODIGO = :CODIGO " : "")
            .append(" and " + clausula + " (substr(c.CODIGO,5,2) = '91') ")
            .append(" and e.dataestorno between to_date('01/01/' || ").append(exercicioCorrente.getAno()).append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(l.valor) * -1 as valor from liquidacao l ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and l.exercicio_id = ").append(":EXERCICIO ")
            .append(codigo != null ? " AND FU.CODIGO = :CODIGO " : "")
            .append(" and " + clausula + " (substr(c.CODIGO,5,2) = '91') ")
            .append(" and l.dataliquidacao between to_date('01/01/' ||").append(exercicioCorrente.getAno()).append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" union all ")
            .append(" select sum(el.valor) as valor from liquidacaoestorno el ")
            .append(" inner join liquidacao l on l.id = el.liquidacao_id ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(codigo != null ? " AND FU.CODIGO = :CODIGO " : "")
            .append(" and " + clausula + " (substr(c.CODIGO,5,2) = '91') ")
            .append(" and el.dataestorno between to_date('01/01/' ||").append(exercicioCorrente.getAno()).append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" ) ");
        Query q = this.getEm().createNativeQuery(sql.toString());
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
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

    public BigDecimal calcularDotacaoInicialSubFuncaoAnexo02(Exercicio exercicioCorrente, Funcao funcao, SubFuncao sf, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDotacaoInicialSubFuncaoAnexo02Sql(funcao, sf, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDotacaoInicialSubFuncaoAnexo02Sql(Funcao funcao, SubFuncao sf, String clausula) {
        BigDecimal total;
        String sql = " SELECT COALESCE(sum(A.DOTACAO), 0) FROM SALDOFONTEDESPESAORC A  "
            + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID "
            + " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID "
            + " INNER JOIN SUBFUNCAO SF ON ac.subfuncao_id = sf.id "
            + " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID "
            + " INNER JOIN (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND A.DATASALDO = FONTES.MAXDATE "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO "
            + "   and " + clausula + " (substr(e.CODIGO,5,2) = '91')  "
            + "   AND SF.id = :sf_id "
            + "   and fu.id = :fu_id ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("sf_id", sf.getId());
        q.setParameter("fu_id", funcao.getId());
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularDotacaoAtualizadaSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, String dataInicial, String dataFinal, Funcao funcao, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDotacaoAtualizadaSubFuncaoAnexo02Sql(codigo, dataInicial, dataFinal, funcao, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDotacaoAtualizadaSubFuncaoAnexo02Sql(String codigo, String dataInicial, String dataFinal, Funcao funcao, String clausula) {
        BigDecimal total;
        String sql = "SELECT COALESCE(sum(A.ALTERACAO), 0) FROM SALDOFONTEDESPESAORC A  "
            + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID "
            + " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID "
            + " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID "
            + " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  "
            + " INNER JOIN "
            + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " where a.DATASALDO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + "  GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND A.DATASALDO = FONTES.MAXDATE "
            + "  WHERE C.EXERCICIO_ID = :EXERCICIO and a.DATASALDO BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') "
            + " AND SF.CODIGO = :CODIGO "
            + "   and fu.id = :fu_id ";
//        String sql = " SELECT SUM(DOTACAO_ATUALIZADA) AS DOTACAO_ATUALIZADA FROM ( "
//                + " SELECT "
//                + " coalesce(SUM(pd.valor), 0) AS dotacao_atualizada "
//                + " from despesaorc d "
//                + " inner join PROVISAOPPADESPESA PD on pd.id = d.provisaoppadespesa_id "
//                + " inner join unidadeorganizacional uni on pd.unidadeorganizacional_id = uni.id "
//                + " INNER JOIN CONTA E ON pd.CONTADEDESPESA_ID = E.ID "
//                + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = pd.subacaoppa_id "
//                + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
//                + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
//                + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
//                + " where d.EXERCICIO_ID = :EXERCICIO "
//                + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') "
//                + " AND SF.CODIGO = :CODIGO "
//                + "   and f.id = :fu_id "
//                + " UNION ALL "
//                + " SELECT "
//                + " COALESCE(SUM(SU.VALOR), 0) AS dotacao_atualizada "
//                + " from SUPLEMENTACAOORC SU "
//                + " INNER JOIN ALTERACAOORC ALT ON ALT.ID = SU.ALTERACAOORC_ID "
//                + " INNER JOIN FONTEDESPESAORC FONTE ON FONTE.ID = SU.FONTEDESPESAORC_ID "
//                + " inner join despesaorc d on d.id = fonte.despesaorc_id "
//                + " inner join PROVISAOPPADESPESA PD on pd.id = d.provisaoppadespesa_id "
//                + " INNER JOIN CONTA E ON pd.CONTADEDESPESA_ID = E.ID "
//                + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = pd.subacaoppa_id "
//                + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
//                + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
//                + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
//                + " WHERE ALT.DATAALTERACAO between TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') "
//                + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') "
//                + " AND SF.CODIGO = :CODIGO "
//                + "   and f.id = :fu_id "
//                + " AND d.EXERCICIO_ID = :EXERCICIO "
//                + " UNION ALL "
//                + " SELECT "
//                + " COALESCE(SUM(ANUL.VALOR), 0) * - 1 AS dotacao_atualizada "
//                + " FROM ANULACAOORC ANUL "
//                + " INNER JOIN ALTERACAOORC ALT ON ALT.ID = ANUL.ALTERACAOORC_ID "
//                + " INNER JOIN FONTEDESPESAORC FONTE ON FONTE.ID = ANUL.FONTEDESPESAORC_ID "
//                + " inner join despesaorc d on d.id = fonte.despesaorc_id "
//                + " inner join PROVISAOPPADESPESA PD on pd.id = d.provisaoppadespesa_id "
//                + " INNER JOIN CONTA E ON pd.CONTADEDESPESA_ID = E.ID "
//                + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = pd.subacaoppa_id "
//                + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
//                + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
//                + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
//                + " WHERE ALT.DATAALTERACAO between TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') "
//                + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') "
//                + " AND SF.CODIGO = :CODIGO "
//                + "   and f.id = :fu_id "
//                + "  AND d.EXERCICIO_ID = :EXERCICIO "
//                + " )";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CODIGO", codigo);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("fu_id", funcao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularDespesasEmpenhadasNoBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, String dataInicial, String dataFinal, Funcao funcao, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasEmpenhadasNoBimestreSubFuncaoAnexo02Sql(codigo, dataInicial, dataFinal, funcao, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDespesasEmpenhadasNoBimestreSubFuncaoAnexo02Sql(String codigo, String dataInicial, String dataFinal, Funcao funcao, String clausula) {
        BigDecimal total;
        String sql = " select sum(valor) from ( "
            + " select coalesce(sum(emp.valor), 0) as valor "
            + " from empenho emp "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and f.id = :fu_id "
            + " and emp.dataempenho between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO "
            + " union all "
            + " select coalesce(sum(est.valor), 0) * -1 as valor from empenhoestorno est "
            + " inner join empenho emp on emp.id = est.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + "   and f.id = :fu_id "
            + " and d.exercicio_id = :EXERCICIO "
            + " and est.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO ) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CODIGO", codigo);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("fu_id", funcao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularDespesasEmpenhadasAteOBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, String dataFinal, Funcao funcao, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasEmpenhadasAteOBimestreSubFuncaoAnexo02Sql(codigo, dataFinal, funcao, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDespesasEmpenhadasAteOBimestreSubFuncaoAnexo02Sql(String codigo, String dataFinal, Funcao funcao, String clausula) {
        BigDecimal total;
        String sql = " select sum(valor) from ( "
            + " select coalesce(sum(emp.valor), 0) as valor "
            + " from empenho emp "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO, 5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + "   and f.id = :fu_id "
            + " and emp.dataempenho between to_date('01/01/" + exercicioCorrente.getAno() + "' , 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO "
            + " union all "
            + " select coalesce(sum(est.valor), 0) * -1 as valor from empenhoestorno est "
            + " inner join empenho emp on emp.id = est.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + "   and f.id = :fu_id "
            + " and d.exercicio_id = :EXERCICIO "
            + " and est.dataestorno between to_date('01/01/" + exercicioCorrente.getAno() + "', 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO ) ";

        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CODIGO", codigo);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("fu_id", funcao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularDespesasLiquidadasNoBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, String dataInicial, String dataFinal, Funcao funcao, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasLiquidadasNoBimestreSubFuncaoAnexo02Sql(codigo, dataInicial, dataFinal, funcao, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDespesasLiquidadasNoBimestreSubFuncaoAnexo02Sql(String codigo, String dataInicial, String dataFinal, Funcao funcao, String clausula) {
        BigDecimal total;
        String sql = "select sum(valor) from ( "
            + " select coalesce(sum(liq.valor), 0) as valor from liquidacao liq "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + "   and f.id = :fu_id "
            + " and d.exercicio_id = :EXERCICIO "
            + " and liq.dataliquidacao between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO "
            + " union all "
            + " select coalesce(sum(est.valor), 0) * -1 as valor from liquidacaoestorno est "
            + " inner join liquidacao liq on liq.id = est.liquidacao_id "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + "   and f.id = :fu_id "
            + " and est.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO )";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CODIGO", codigo);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("fu_id", funcao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public BigDecimal calcularDespesasLiquidadasAteOBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, String dataFinal, Funcao funcao, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesaEmpenhadaAteOBimestre = calcularDespesasLiquidadasAteOBimestreSubFuncaoAnexo02Sql(codigo, dataFinal, funcao, clausula);
        return despesaEmpenhadaAteOBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularDespesasLiquidadasAteOBimestreSubFuncaoAnexo02Sql(String codigo, String dataFinal, Funcao funcao, String clausula) {
        BigDecimal total;
        String sql = "select sum(valor) from ( "
            + " select coalesce(sum(liq.valor), 0) as valor from liquidacao liq "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + "   and f.id = :fu_id "
            + " and d.exercicio_id = :EXERCICIO "
            + " and liq.dataliquidacao between to_date('01/01/" + exercicioCorrente.getAno() + "', 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO "
            + " union all "
            + " select coalesce(sum(est.valor), 0) * -1 as valor from liquidacaoestorno est "
            + " inner join liquidacao liq on liq.id = est.liquidacao_id "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + "   and f.id = :fu_id "
            + " and d.exercicio_id = :EXERCICIO "
            + " and est.dataestorno between to_date('01/01/" + exercicioCorrente.getAno() + "', 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO ) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CODIGO", codigo);
        q.setParameter("EXERCICIO", this.exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("fu_id", funcao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularRestoPagarTotal(Exercicio exercicio, String dataFinal, Funcao funcao) {
        BigDecimal total;
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(valor), 0) as valor from ( ")
            .append(" select sum(emp.valor) as valor from empenho emp ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID ")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and emp.exercicio_id = ").append(":EXERCICIO_ID ");
        if (funcao != null) {
            sql.append("  and fu.id = :fu_id ");
        }
        sql.append(" and emp.dataempenho between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(e.valor) * -1 as valor from empenhoestorno e ")
            .append(" inner join empenho emp on emp.id = e.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where emp.categoriaorcamentaria = 'NORMAL' ");
        if (funcao != null) {
            sql.append("  and fu.id = :fu_id ");
        }
        sql.append(" and e.dataestorno between to_date('01/01/' || ").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(l.valor) * -1 as valor from liquidacao l ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ");
        if (funcao != null) {
            sql.append("  and fu.id = :fu_id ");
        }
        sql.append(" and l.exercicio_id = ").append(":EXERCICIO_ID ")
            .append(" and l.dataliquidacao between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" union all ")
            .append(" select sum(el.valor) as valor from liquidacaoestorno el ")
            .append(" inner join liquidacao l on l.id = el.liquidacao_id ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ");
        if (funcao != null) {
            sql.append("  and fu.id = :fu_id ");
        }
        sql.append(" and el.dataestorno between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" ) ");
        Query q = getEm().createNativeQuery(sql.toString());
        q.setParameter("ANO_EXERCICIO", exercicio.getAno());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("EXERCICIO_ID", exercicio.getId());
        if (funcao != null) {
            q.setParameter("fu_id", funcao.getId());
        }
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public List<Funcao> buscaCodigoFuncao(Exercicio exercicio, String clausula) {
        String sql = " SELECT fu.* FROM SALDOFONTEDESPESAORC A  "
            + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID "
            + " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID "
            + " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID "
            + " INNER JOIN (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND A.DATASALDO = FONTES.MAXDATE "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO "
            + "   and " + clausula + " (substr(e.CODIGO,5,2) = '91')  "
            + " group by fu.id, fu.descricao, fu.codigo"
            + " order by fu.codigo ";
        Query q = this.getEm().createNativeQuery(sql, Funcao.class);
        q.setParameter("EXERCICIO", exercicio.getId());
        return (List<Funcao>) q.getResultList();
    }

    public List<SubFuncao> buscaSubFuncao(Long id, String clausula) {
        List<SubFuncao> retorna = new ArrayList<>();
        String sql = " select sf.* "
            + " from despesaorc d "
            + " inner join PROVISAOPPADESPESA PD on pd.id = d.provisaoppadespesa_id "
            + " INNER JOIN CONTA E ON PD.CONTADEDESPESA_ID = E.ID "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = pd.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON a.subfuncao_id = sF.id "
            + " where f.id = :id "
            + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') "
            + " group by sf.id, sf.descricao, sf.funcao_id, sf.codigo"
            + " order by sf.codigo ";
        Query q = this.getEm().createNativeQuery(sql, SubFuncao.class);
        q.setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            retorna = (List<SubFuncao>) q.getResultList();
        }
        return retorna;
    }

    public BigDecimal calcularRestoAPagarNaoProcessadosFuncao(Exercicio exercicioCorrente, String dataFinal, Funcao funcao, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasNoBimestre = calcularRestoAPagarNaoProcessadosFuncaoSql(dataFinal, funcao, clausula);
        return despesasLiquidadasNoBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularRestoAPagarNaoProcessadosFuncaoSql(String dataFinal, Funcao funcao, String clausula) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(valor), 0) as valor from ( ")
            .append(" select sum(emp.valor) as valor from empenho emp ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID ")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and emp.exercicio_id = ").append(":EXERCICIO_ID ");
        if (funcao != null) {
            sql.append(" and fu.id = ").append(" :funcao_id ");
        }
        sql.append(" and emp.dataempenho between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(e.valor) * -1 as valor from empenhoestorno e ")
            .append(" inner join empenho emp on emp.id = e.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and e.dataestorno between to_date('01/01/' || ").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ");
        if (funcao != null) {
            sql.append(" and fu.id = ").append(" :funcao_id ");
        }
        sql.append(" union all ")
            .append(" select sum(l.valor) * -1 as valor from liquidacao l ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and l.exercicio_id = ").append(":EXERCICIO_ID ")
            .append(" and l.dataliquidacao between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ");
        if (funcao != null) {
            sql.append(" and fu.id = ").append(" :funcao_id ");
        }
        sql.append(" union all ")
            .append(" select sum(el.valor) as valor from liquidacaoestorno el ")
            .append(" inner join liquidacao l on l.id = el.liquidacao_id ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ");
        if (funcao != null) {
            sql.append(" and fu.id = ").append(" :funcao_id ");
        }
        sql.append(" and el.dataestorno between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" ) ");
        Query q = getEm().createNativeQuery(sql.toString());
        q.setParameter("ANO_EXERCICIO", this.exercicioCorrente.getAno());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("EXERCICIO_ID", this.exercicioCorrente.getId());
        if (funcao != null) {
            q.setParameter("funcao_id", funcao.getId());
        }
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getResultList().get(0);
        }
    }

    public BigDecimal calcularRestoAPagarNaoProcessadosSubFuncao(Exercicio exercicioCorrente, String dataFinal, Funcao funcao, SubFuncao sf, String clausula) {
        this.exercicioCorrente = exercicioCorrente;
        BigDecimal despesasLiquidadasNoBimestre = calcularRestoAPagarNaoProcessadosSubFuncaoSql(dataFinal, funcao, sf, clausula);
        return despesasLiquidadasNoBimestre;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularRestoAPagarNaoProcessadosSubFuncaoSql(String dataFinal, Funcao funcao, SubFuncao sf, String clausula) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(valor), 0) as valor from ( ")
            .append(" select sum(emp.valor) as valor from empenho emp ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID ")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID ")
            .append(" where emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and emp.exercicio_id = ").append(":EXERCICIO_ID ")
            .append(" and fu.id = ").append(" :funcao_id ")
            .append(" and Sf.id = ").append(" :subfuncao_id ")
            .append(" and emp.dataempenho between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(e.valor) * -1 as valor from empenhoestorno e ")
            .append(" inner join empenho emp on emp.id = e.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID  ")
            .append(" where emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and fu.id = ").append(" :funcao_id ")
            .append(" and Sf.id = ").append(" :subfuncao_id ")
            .append(" and e.dataestorno between to_date('01/01/' || ").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(l.valor) * -1 as valor from liquidacao l ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID  ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and l.exercicio_id = ").append(":EXERCICIO_ID ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and fu.id = ").append(" :funcao_id ")
            .append(" and Sf.id = ").append(" :subfuncao_id ")
            .append(" and l.dataliquidacao between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" union all ")
            .append(" select sum(el.valor) as valor from liquidacaoestorno el ")
            .append(" inner join liquidacao l on l.id = el.liquidacao_id ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID  ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and fu.id = ").append(" :funcao_id ")
            .append(" and Sf.id = ").append(" :subfuncao_id ")
            .append(" and el.dataestorno between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" ) ");
        Query q = getEm().createNativeQuery(sql.toString());
        q.setParameter("ANO_EXERCICIO", this.exercicioCorrente.getAno());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("EXERCICIO_ID", this.exercicioCorrente.getId());
        q.setParameter("funcao_id", funcao.getId());
        q.setParameter("subfuncao_id", sf.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getResultList().get(0);
        }
    }

    public Exercicio getExercicioCorrente() {
        return exercicioCorrente;
    }

    public void setExercicioCorrente(Exercicio exercicioCorrente) {
        this.exercicioCorrente = exercicioCorrente;
    }
}
