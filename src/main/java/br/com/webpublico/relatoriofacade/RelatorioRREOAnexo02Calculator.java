/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Funcao;
import br.com.webpublico.entidades.SubFuncao;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author juggernaut
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioRREOAnexo02Calculator extends ItemDemonstrativoCalculator {

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDotacaoInicialAnexo02(Exercicio exercicioCorrente, Funcao funcao, String clausula) {
        String sql = " SELECT COALESCE(sum(A.DOTACAO), 0) FROM SALDOFONTEDESPESAORC A  "
            + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID "
            + " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = AC.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID "
            + " INNER JOIN (SELECT A.FONTEDESPESAORC_ID AS FONTE, trunc(MAX(A.DATASALDO) )AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND trunc(A.DATASALDO) = trunc(FONTES.MAXDATE) "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO "
            + "   And " + clausula + " (substr(e.CODIGO,5,2) = '91')  ";
        if (funcao != null) {
            sql += "   AND FU.id = :id ";
        }
        Query q = this.getEm().createNativeQuery(sql);
        if (funcao != null) {
            q.setParameter("id", funcao.getId());
        }
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDotacaoAtualizadaAnexo02(Exercicio exercicioCorrente, String codigo, String dataInicial, String dataFinal, String clausula) {
        String sql = "SELECT COALESCE(sum(A.ALTERACAO), 0) FROM SALDOFONTEDESPESAORC A  "
            + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = AC.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID "
            + " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  "
            + " INNER JOIN "
            + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, trunc(MAX(A.DATASALDO)) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " where trunc(a.DATASALDO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + "  GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND trunc(A.DATASALDO) = trunc(FONTES.MAXDATE) "
            + "  WHERE C.EXERCICIO_ID = :EXERCICIO and trunc(a.DATASALDO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') ";
        if (codigo != null) {
            sql += " AND FU.CODIGO = :CODIGO ";
        }

        Query q = this.getEm().createNativeQuery(sql);
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasEmpenhadasNoBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String dataInicial, String dataFinal, String clausula) {
        String sql = " select coalesce(sum(valor),0 ) as valor from ( "
            + " select coalesce(sum(emp.valor), 0) as valor from empenho emp "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(emp.dataempenho) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')"
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
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(est.dataestorno) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy')"
            + " and emp.categoriaorcamentaria = 'NORMAL' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " ) ";

        Query q = this.getEm().createNativeQuery(sql);
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasEmpenhadasAteOBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String dataFinal, String clausula) {
        String sql = " select sum(valor) from ( "
            + " select coalesce(sum(emp.valor), 0) as valor "
            + " from empenho emp "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(emp.dataempenho) between to_date('01/01/" + exercicioCorrente.getAno() + "' , 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
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
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(est.dataestorno) between to_date('01/01/" + exercicioCorrente.getAno() + "' , 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'NORMAL' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasEmpenhadasAteOBimestreAnexo02Resto(Exercicio exercicioCorrente, String codigo, String dataFinal, String clausula) {
        String sql = " select sum(valor) from ( "
            + " select coalesce(sum(emp.valor), 0) as valor "
            + " from empenho emp "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(emp.dataempenho) between to_date('01/01/" + exercicioCorrente.getAno() + "' , 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
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
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(est.dataestorno) between to_date('01/01/" + exercicioCorrente.getAno() + "' , 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'RESTO' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasNoBimestreAnexo02(Exercicio exercicioCorrente, Long id, String dataInicial, String dataFinal, String clausula) {
        String sql = " select coalesce(sum(valor), 0) as valor from ( "
            + " select coalesce(sum(liq.valor), 0) as valor from liquidacao liq "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(liq.dataliquidacao) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
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
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(est.dataestorno) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' ";
        if (id != null) {
            sql += " AND F.id = :id ";
        }
        sql += " ) ";
        Query q = this.getEm().createNativeQuery(sql);
        if (id != null) {
            q.setParameter("id", id);
        }
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreAnexo02(Exercicio exercicioCorrente, String codigo, String dataFinal, String clausula) {
        String sql = " select coalesce(sum(valor), 0) from ( "
            + " select coalesce(sum(liq.valor), 0) as valor from liquidacao liq "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(liq.dataliquidacao) between to_date('01/01/" + exercicioCorrente.getAno() + "', 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
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
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(est.dataestorno) between to_date('01/01/" + exercicioCorrente.getAno() + "', 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' ";
        if (codigo != null) {
            sql += " AND F.CODIGO = :CODIGO ";
        }
        sql += " ) ";

        Query q = this.getEm().createNativeQuery(sql);
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreAnexo02Resto(Exercicio exercicioCorrente, String codigo, String dataFinal, String clausula) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(valor), 0) as valor from ( ")
            .append(" select sum(emp.valor) as valor from empenho emp ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID ")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where  emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and emp.exercicio_id = ").append(":EXERCICIO ")
            .append(codigo != null ? " AND FU.CODIGO = :CODIGO " : "")
            .append(" and " + clausula + " (substr(c.CODIGO,5,2) = '91') ")
            .append(" and trunc(emp.dataempenho) between to_date('01/01/' ||").append(exercicioCorrente.getAno()).append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(e.valor) * -1 as valor from empenhoestorno e ")
            .append(" inner join empenho emp on emp.id = e.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where  emp.categoriaorcamentaria = 'NORMAL' ")
            .append(codigo != null ? " AND FU.CODIGO = :CODIGO " : "")
            .append(" and " + clausula + " (substr(c.CODIGO,5,2) = '91') ")
            .append(" and trunc(e.dataestorno) between to_date('01/01/' || ").append(exercicioCorrente.getAno()).append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
            .append(" union all ")
            .append(" select sum(l.valor) * -1 as valor from liquidacao l ")
            .append(" inner join empenho emp on emp.id = l.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONTDESP ON EMP.FONTEDESPESAORC_ID = FONTDESP.ID ")
            .append(" INNER JOIN DESPESAORC desporc ON FONTDESP.DESPESAORC_ID = desporc.ID  ")
            .append(" INNER JOIN PROVISAOPPADESPESA provdesp ON desporc.PROVISAOPPADESPESA_ID = provdesp.ID")
            .append(" INNER JOIN CONTA c ON provdesp.CONTADEDESPESA_ID = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SA ON provdesp.SUBACAOPPA_ID = SA.ID  ")
            .append(" INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID ")
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and l.exercicio_id = ").append(":EXERCICIO ")
            .append(codigo != null ? " AND FU.CODIGO = :CODIGO " : "")
            .append(" and " + clausula + " (substr(c.CODIGO,5,2) = '91') ")
            .append(" and trunc(l.dataliquidacao) between to_date('01/01/' ||").append(exercicioCorrente.getAno()).append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
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
            .append(" INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(codigo != null ? " AND FU.CODIGO = :CODIGO " : "")
            .append(" and " + clausula + " (substr(c.CODIGO,5,2) = '91') ")
            .append(" and trunc(el.dataestorno) between to_date('01/01/' ||").append(exercicioCorrente.getAno()).append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" ) ");
        Query q = this.getEm().createNativeQuery(sql.toString());
        if (codigo != null) {
            q.setParameter("CODIGO", codigo);
        }
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDotacaoInicialSubFuncaoAnexo02(Exercicio exercicioCorrente, Funcao funcao, SubFuncao sf, String clausula) {
        String sql = " SELECT COALESCE(sum(A.DOTACAO), 0) FROM SALDOFONTEDESPESAORC A  "
            + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID "
            + " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID "
            + " INNER JOIN SUBFUNCAO SF ON ac.subfuncao_id = sf.id  and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID "
            + " INNER JOIN (SELECT A.FONTEDESPESAORC_ID AS FONTE, trunc(MAX(A.DATASALDO)) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND trunc(A.DATASALDO) = trunc(FONTES.MAXDATE) "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO "
            + "   and " + clausula + " (substr(e.CODIGO,5,2) = '91')  "
            + "   AND SF.id = :sf_id "
            + "   and fu.id = :fu_id ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("sf_id", sf.getId());
        q.setParameter("fu_id", funcao.getId());
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDotacaoAtualizadaSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, String dataInicial, String dataFinal, Funcao funcao, String clausula) {
        String sql = "SELECT COALESCE(sum(A.ALTERACAO), 0) FROM SALDOFONTEDESPESAORC A  "
            + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID  "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID  "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID "
            + " INNER JOIN SUBFUNCAO SF ON AC.SUBFUNCAO_ID = SF.ID  and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID "
            + " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID  "
            + " INNER JOIN "
            + " (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " where trunc(a.DATASALDO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + "  GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND trunc(A.DATASALDO) = trunc(FONTES.MAXDATE) "
            + "  WHERE C.EXERCICIO_ID = :EXERCICIO and trunc(a.DATASALDO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  "
            + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') "
            + " AND SF.CODIGO = :CODIGO "
            + "   and fu.id = :fu_id ";

        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CODIGO", codigo);
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("fu_id", funcao.getId());
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasEmpenhadasNoBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, String dataInicial, String dataFinal, Funcao funcao, String clausula) {
        String sql = " select sum(valor) from ( "
            + " select coalesce(sum(emp.valor), 0) as valor "
            + " from empenho emp "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id  and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + " and f.id = :fu_id "
            + " and trunc(emp.dataempenho) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
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
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id  and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + "   and f.id = :fu_id "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(est.dataestorno) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO ) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CODIGO", codigo);
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("fu_id", funcao.getId());
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasEmpenhadasAteOBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, String dataFinal, Funcao funcao, String clausula) {
        String sql = " select sum(valor) from ( "
            + " select coalesce(sum(emp.valor), 0) as valor "
            + " from empenho emp "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id  and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO, 5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + "   and f.id = :fu_id "
            + " and trunc(emp.dataempenho) between to_date('01/01/" + exercicioCorrente.getAno() + "' , 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
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
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id  and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + "   and f.id = :fu_id "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(est.dataestorno) between to_date('01/01/" + exercicioCorrente.getAno() + "', 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and emp.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO ) ";

        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CODIGO", codigo);
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("fu_id", funcao.getId());
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasNoBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, String dataInicial, String dataFinal, Funcao funcao, String clausula) {
        String sql = "select sum(valor) from ( "
            + " select coalesce(sum(liq.valor), 0) as valor from liquidacao liq "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id  and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + "   and f.id = :fu_id "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(liq.dataliquidacao) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
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
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id  and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + " and d.exercicio_id = :EXERCICIO "
            + "   and f.id = :fu_id "
            + " and trunc(est.dataestorno) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO )";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CODIGO", codigo);
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("fu_id", funcao.getId());
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularDespesasLiquidadasAteOBimestreSubFuncaoAnexo02(Exercicio exercicioCorrente, String codigo, String dataFinal, Funcao funcao, String clausula) {
        String sql = "select sum(valor) from ( "
            + " select coalesce(sum(liq.valor), 0) as valor from liquidacao liq "
            + " inner join empenho emp on emp.id = liq.empenho_id "
            + " inner join despesaorc d on d.id = emp.despesaorc_id "
            + " inner join provisaoppadespesa ppa on ppa.id = d.provisaoppadespesa_id "
            + " inner join conta c on c.id = ppa.contadedespesa_id "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = ppa.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id  and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + "   and f.id = :fu_id "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(liq.dataliquidacao) between to_date('01/01/" + exercicioCorrente.getAno() + "', 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
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
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id  and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " WHERE " + clausula + " (substr(c.CODIGO,5,2) = '91') "
            + "   and f.id = :fu_id "
            + " and d.exercicio_id = :EXERCICIO "
            + " and trunc(est.dataestorno) between to_date('01/01/" + exercicioCorrente.getAno() + "', 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and liq.categoriaorcamentaria = 'NORMAL' "
            + " AND sF.CODIGO = :CODIGO ) ";
        Query q = this.getEm().createNativeQuery(sql);
        q.setParameter("CODIGO", codigo);
        q.setParameter("EXERCICIO", exercicioCorrente.getId());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("fu_id", funcao.getId());
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public List<Funcao> buscarFuncoes(Exercicio exercicio, String clausula) {
        String sql = " SELECT fu.* FROM SALDOFONTEDESPESAORC A  "
            + " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID "
            + " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID "
            + " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID "
            + " INNER JOIN SUBACAOPPA SA ON D.SUBACAOPPA_ID = SA.ID "
            + " INNER JOIN ACAOPPA AC ON SA.ACAOPPA_ID = AC.ID "
            + " INNER JOIN FUNCAO FU ON AC.FUNCAO_ID = FU.ID "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = AC.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID "
            + " INNER JOIN (SELECT A.FONTEDESPESAORC_ID AS FONTE, trunc(MAX(A.DATASALDO)) AS MAXDATE FROM SALDOFONTEDESPESAORC A  "
            + " GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND trunc(A.DATASALDO) = trunc(FONTES.MAXDATE) "
            + " WHERE C.EXERCICIO_ID = :EXERCICIO "
            + "   and " + clausula + " (substr(e.CODIGO,5,2) = '91')  "
            + " group by fu.id, fu.descricao, fu.codigo"
            + " order by fu.codigo ";
        Query q = this.getEm().createNativeQuery(sql, Funcao.class);
        q.setParameter("EXERCICIO", exercicio.getId());
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        return (List<Funcao>) q.getResultList();
    }

    public List<SubFuncao> buscarSubFuncoes(Long id, String clausula) {
        String sql = " select sf.* "
            + " from despesaorc d "
            + " inner join PROVISAOPPADESPESA PD on pd.id = d.provisaoppadespesa_id "
            + " INNER JOIN CONTA E ON PD.CONTADEDESPESA_ID = E.ID "
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = pd.subacaoppa_id "
            + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id "
            + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id "
            + " INNER JOIN SUBFUNCAO SF ON SF.ID = a.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 "
            + " where f.id = :id "
            + " AND " + clausula + " (substr(E.CODIGO,5,2) = '91') "
            + " group by sf.id, sf.descricao, sf.funcao_id, sf.codigo, sf.exibirnoanexo2 "
            + " order by sf.codigo ";
        Query q = this.getEm().createNativeQuery(sql, SubFuncao.class);
        q.setParameter("id", id);
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        if (!q.getResultList().isEmpty()) {
            return (List<SubFuncao>) q.getResultList();
        }
        return Lists.newArrayList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularRestoAPagarNaoProcessadosFuncao(Exercicio exercicioCorrente, String dataFinal, Funcao funcao, String clausula) {
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
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = ac.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" where emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and emp.exercicio_id = ").append(":EXERCICIO_ID ");
        if (funcao != null) {
            sql.append(" and fu.id = ").append(" :funcao_id ");
        }
        sql.append(" and trunc(emp.dataempenho) between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
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
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = ac.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" where emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and trunc(e.dataestorno) between to_date('01/01/' || ").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ");
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
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = ac.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and l.exercicio_id = ").append(":EXERCICIO_ID ")
            .append(" and trunc(l.dataliquidacao) between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ");
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
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = ac.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ");
        if (funcao != null) {
            sql.append(" and fu.id = ").append(" :funcao_id ");
        }
        sql.append(" and trunc(el.dataestorno) between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" ) ");
        Query q = getEm().createNativeQuery(sql.toString());
        q.setParameter("ANO_EXERCICIO", exercicioCorrente.getAno());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("EXERCICIO_ID", exercicioCorrente.getId());
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        if (funcao != null) {
            q.setParameter("funcao_id", funcao.getId());
        }
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getResultList().get(0);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calcularRestoAPagarNaoProcessadosSubFuncao(Exercicio exercicioCorrente, String dataFinal, Funcao funcao, SubFuncao sf, String clausula) {
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
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = aC.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" where emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and emp.exercicio_id = ").append(":EXERCICIO_ID ")
            .append(" and fu.id = ").append(" :funcao_id ")
            .append(" and Sf.id = ").append(" :subfuncao_id ")
            .append(" and trunc(emp.dataempenho) between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
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
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = aC.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" where emp.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and fu.id = ").append(" :funcao_id ")
            .append(" and Sf.id = ").append(" :subfuncao_id ")
            .append(" and trunc(e.dataestorno) between to_date('01/01/' || ").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy') ")
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
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = aC.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and l.exercicio_id = ").append(":EXERCICIO_ID ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and fu.id = ").append(" :funcao_id ")
            .append(" and Sf.id = ").append(" :subfuncao_id ")
            .append(" and trunc(l.dataliquidacao) between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
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
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = aC.subfuncao_id and SF.exibirnoanexo2 = :exibirnoanexo2 ")
            .append(" where l.categoriaorcamentaria = 'NORMAL' ")
            .append(" and ").append(clausula).append(" substr(c.CODIGO,5,2) = '91' ")
            .append(" and fu.id = ").append(" :funcao_id ")
            .append(" and Sf.id = ").append(" :subfuncao_id ")
            .append(" and trunc(el.dataestorno) between to_date('01/01/' ||").append(":ANO_EXERCICIO").append(", 'dd/mm/yyyy') AND to_date(").append(":DATAFINAL").append(", 'dd/MM/yyyy')  ")
            .append(" ) ");
        Query q = getEm().createNativeQuery(sql.toString());
        q.setParameter("ANO_EXERCICIO", exercicioCorrente.getAno());
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("EXERCICIO_ID", exercicioCorrente.getId());
        q.setParameter("funcao_id", funcao.getId());
        q.setParameter("subfuncao_id", sf.getId());
        q.setParameter("exibirnoanexo2", Boolean.TRUE);
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getResultList().get(0);
        }
    }
}
