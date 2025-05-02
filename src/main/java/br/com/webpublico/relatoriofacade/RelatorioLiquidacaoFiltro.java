/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import javax.ejb.Stateless;

/**
 * @author reidocrime
 */
@Stateless
public class RelatorioLiquidacaoFiltro {

    public String sqlBaseRelatorioComFIltro(String parteSQL) {

        StringBuilder sql = new StringBuilder();
        String toReturn = "";
        sql.append(" select ");
        sql.append(" l.numero ");
        sql.append(" l.dataliquidacao, ");
        sql.append(" coalesce(d.codigoreduzido,' ') as codigo, ");
        sql.append(" l.valor, ");
        sql.append(" emp.numero empenho, ");
        sql.append(" pf.nome as fornecedor, ");
        sql.append(" substr(emp.tipoempenho,0,1) as tipo_empenho, ");
        sql.append(" con.codigo as conta, ");
        sql.append(" con.descricao, ");
        sql.append(" uni.descricao, ");
        sql.append(" F.CODIGO ||'.' ||SF.CODIGO ||'.' ||P.CODIGO ||'.' ||TPA.CODIGO ||'' ||A.CODIGO ||'.' ||SUB.CODIGO as projetoatividade, ");
        sql.append(" cd.CODIGO as fonte, ");
        sql.append(" orgao.DESCRICAO as org_descricao, ");
        sql.append(" unidade.DESCRICAO as und_descricao, ");
        sql.append(" ho_orgao.CODIGONO||'.'||ho_unidade.codigono as cd_unidade ");
        sql.append(" from liquidacao l ");
        sql.append(" inner join conta con on l.contadespesadesdobrada_id = con.id ");
        sql.append(" inner join empenho emp on l.empenho_id = emp.id and emp.CATEGORIAORCAMENTARIA = 'NORMAL' ");
        sql.append(" inner join pessoafisica pf on emp.fornecedor_id = pf.id ");
        sql.append(" inner join unidadeorganizacional uni on emp.unidadeorganizacional_id = uni.id ");
        sql.append(" inner join hierarquiaorganizacional ho_unidade on emp.unidadeorganizacional_id = ho_unidade.subordinada_id AND ho_unidade.EXERCICIO_ID = L.EXERCICIO_ID and ho_unidade.TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA' ");
        sql.append(" inner join hierarquiaorganizacional ho_orgao on ho_unidade.superior_id = ho_orgao.subordinada_id AND ho_ORGAO.EXERCICIO_ID = L.EXERCICIO_ID and ho_ORGAO.TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA' ");
        sql.append(" inner join unidadeorganizacional unidade on ho_unidade.subordinada_id = unidade.id ");
        sql.append(" inner join unidadeorganizacional orgao on ho_orgao.subordinada_id = orgao.id ");
        sql.append(" INNER JOIN DESPESAORC D on emp.DESPESAORC_ID = D.id ");
        sql.append(" INNER JOIN PROVISAOPPADESPESA PD ON pd.ID = d.provisaoppadespesa_id ");
        sql.append(" INNER JOIN PROVISAOPPA PROV ON PROV.ID = pd.provisaoppa_id ");
        sql.append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacao_id ");
        sql.append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ");
        sql.append(" INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID ");
        sql.append(" INNER JOIN programappa P ON P.ID = A.programa_id ");
        sql.append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ");
        sql.append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ");
        sql.append(" inner join historicocontabil hist on l.historicocontabil_id = hist.id ");
        sql.append(" inner join exercicio ex on l.exercicio_id = ex.id ");
        sql.append(" INNER JOIN FONTEDESPESAORC ftd on emp.FONTEDESPESAORC_ID =ftd.ID ");
        sql.append(" INNER JOIN PROVISAOPPAFONTE ppf on ftd.PROVISAOPPAFONTE_ID=ppf.ID ");
        sql.append(" INNER JOIN conta cd on ppf.DESTINACAODERECURSOS_ID = cd.id ");
        sql.append(" WHERE L.ID NOT IN(SELECT  LIQ.ID ");
        sql.append("       FROM ");
        sql.append("    LIQUIDACAO LIQ ");
        sql.append(" INNER JOIN LIQUIDACAOESTORNO LIQ_ESTOR ON LIQ_ESTOR.LIQUIDACAO_ID = LIQ.ID AND LIQ.VALOR = LIQ_ESTOR.VALOR) ");
        sql.append(" ");
        sql.append(parteSQL);

        toReturn = sql.toString();

        return toReturn;

    }
}
