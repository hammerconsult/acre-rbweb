/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.Exercicio;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.math.BigDecimal;

/**
 * @author juggernaut
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioRREOAnexo07Calculos extends ItemDemonstrativoCalculator {

    public RelatorioRREOAnexo07Calculos() {
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaRestosAPagarExAnteriores(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2, String clausula, String filtro, String tipoResto, String intra) {
        String sql = " select coalesce(sum(e.valor),0) as valor from empenho e  "
            + " inner join vwhierarquiaorcamentaria uni on e.unidadeorganizacional_id = uni.subordinada_id " + filtro
            + " INNER JOIN exercicio ex ON e.exercicio_id = ex.id "
            + " INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID "
            + " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID "
            + " INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID "
            + " where trunc(e.dataempenho) between uni.iniciovigencia and coalesce(uni.fimvigencia, trunc(e.dataempenho)) "
            + " and e.categoriaorcamentaria = 'RESTO' "
            + " and e.tiporestosprocessados = '" + tipoResto + "'"
            + " AND " + intra + " SUBSTR(c.codigo, 5,2) = '91' "
            + " AND UPPER(uni.ESFERADOPODER) IN (:ESFERA1, :ESFERA2)  "
            + " AND ex.ano " + clausula + " :ANOANTERIOR  ";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("ESFERA1", esferaPoder1);
        q.setParameter("ESFERA2", esferaPoder2);
        q.setParameter("ANOANTERIOR", exercicioCorrente.getAno());
        q.setMaxResults(1);
        return (BigDecimal) q.getSingleResult();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaRestosAPagarProcessadosCancelados(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2, String filtro, String dataInicial, String dataFinal, String tipoResto, String intra) {
        String sql = " select coalesce(sum(es.valor),0) as valor from empenhoestorno es "
            + " inner join empenho e on es.empenho_id = e.id "
            + " inner join vwhierarquiaorcamentaria uni on e.unidadeorganizacional_id = uni.subordinada_id " + filtro
            + " INNER JOIN exercicio ex ON e.exercicio_id = ex.id "
            + " INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID "
            + " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID "
            + " INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID "
            + " where trunc(es.DATAESTORNO) between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy')  "
            + " and to_date(:DATAFINAL, 'dd/MM/yyyy') between uni.iniciovigencia and coalesce(uni.fimvigencia, to_date(:DATAFINAL, 'dd/MM/yyyy')) "
            + " and es.categoriaorcamentaria = 'RESTO' "
            + " and e.tiporestosprocessados = '" + tipoResto + "'"
            + " AND " + intra + " SUBSTR(c.codigo, 5,2) = '91' "
            + " AND UPPER(uni.ESFERADOPODER) IN (:ESFERA1, :ESFERA2)  "
            + " AND ex.ano = :ANO ";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("ESFERA1", esferaPoder1);
        q.setParameter("ESFERA2", esferaPoder2);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("ANO", exercicioCorrente.getAno());
        q.setMaxResults(1);
        return (BigDecimal) q.getSingleResult();
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaRestosAPagarProcessadosPagos(Exercicio exercicioCorrente, String esferaPoder1, String esferaPoder2, String clausula, String filtro, String dataInicial, String dataFinal, String tipoResto, String intra) {
        String sql = " select coalesce(sum(valor), 0) from( "
            + " select sum(coalesce(p.valor,0)) as valor from pagamento p "
            + " inner join liquidacao l on p.liquidacao_id = l.id "
            + " inner join empenho e on l.empenho_id = e.id "
            + " INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID "
            + " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID "
            + " INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID "
            + " INNER JOIN exercicio ex ON e.exercicio_id = ex.id "
            + " inner join vwhierarquiaorcamentaria uni on p.unidadeorganizacional_id = uni.subordinada_id " + filtro
            + " where trunc(p.datapagamento) between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and to_date(:DATAFINAL, 'dd/MM/yyyy') between uni.iniciovigencia and coalesce(uni.fimvigencia, to_date(:DATAFINAL, 'dd/MM/yyyy')) "
            + " and p.categoriaorcamentaria = 'RESTO' "
            + " and e.tiporestosprocessados = '" + tipoResto + "'"
            + " AND " + intra + " SUBSTR(c.codigo, 5,2) = '91' "
            + " AND UPPER(uni.ESFERADOPODER) IN (:ESFERA1, :ESFERA2)  "
            + " AND ex.ano " + clausula + " :ANO  and p.status <> 'ABERTO' "
            + " union all "
            + " select sum(coalesce(es.valor,0)) * -1 as valor from pagamentoestorno es "
            + " inner join pagamento p on es.pagamento_id= p.id "
            + " inner join liquidacao l on p.liquidacao_id = l.id "
            + " inner join empenho e on l.empenho_id = e.id "
            + " INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID "
            + " INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID "
            + " INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID "
            + " INNER JOIN exercicio ex ON e.exercicio_id = ex.id "
            + " inner join vwhierarquiaorcamentaria uni on es.unidadeorganizacional_id = uni.subordinada_id " + filtro
            + " where trunc(es.dataestorno) between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') "
            + " and to_date(:DATAFINAL, 'dd/MM/yyyy') between uni.iniciovigencia and coalesce(uni.fimvigencia, to_date(:DATAFINAL, 'dd/MM/yyyy')) "
            + " and es.categoriaorcamentaria = 'RESTO' "
            + " and e.tiporestosprocessados = '" + tipoResto + "'"
            + " AND " + intra + " SUBSTR(c.codigo, 5,2) = '91' "
            + " AND UPPER(uni.ESFERADOPODER) IN (:ESFERA1, :ESFERA2)  "
            + " AND ex.ano " + clausula + " :ANO  and p.status <> 'ABERTO' "
            + " ) ";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("ESFERA1", esferaPoder1);
        q.setParameter("ESFERA2", esferaPoder2);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("DATAFINAL", dataFinal);
        q.setParameter("ANO", exercicioCorrente.getAno());
        q.setMaxResults(1);
        return (BigDecimal) q.getSingleResult();
    }
}
