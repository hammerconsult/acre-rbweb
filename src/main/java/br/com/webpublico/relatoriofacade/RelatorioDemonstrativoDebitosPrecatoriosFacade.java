package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.DemonstrativoDebitosPrecatorios;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.util.UtilRelatorioContabil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 17/05/2017.
 */
@Stateless
public class RelatorioDemonstrativoDebitosPrecatoriosFacade extends RelatorioContabilSuperFacade {

    @EJB
    private PessoaFacade pessoaFacade;

    public List<DemonstrativoDebitosPrecatorios> buscarDados(List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT nomePessoa, ");
        sql.append("        processoTribunal, ");
        sql.append("        max(dataSaldo) as dataSaldo, ");
        sql.append("        naturezaPrecatorio, ");
        sql.append("        coalesce(sum(saldoanterior), 0) as saldoanterior,  ");
        sql.append("        coalesce(sum(inclusao), 0) as inclusao, ");
        sql.append("        coalesce(sum(atualizacao), 0) as atualizacao,  ");
        sql.append("        coalesce(sum(pagamento), 0) as pagamento,  ");
        sql.append("        coalesce(sum(cancelamento), 0) as cancelamento, ");
        sql.append("        coalesce(sum(saldoAtual), 0) as saldoAtual ");
        sql.append("   from ( ");
        sql.append(" SELECT nomePessoa,  ");
        sql.append("        processoTribunal, ");
        sql.append("        dataSaldo, ");
        sql.append("        naturezaPrecatorio, ");
        sql.append("        SUM(saldoanterior) AS saldoanterior,  ");
        sql.append("        SUM(inclusao) AS inclusao, ");
        sql.append("        SUM(ATUALIZACAO) AS atualizacao,  ");
        sql.append("        SUM(PAGAMENTO) AS pagamento,  ");
        sql.append("        SUM(CANCELAMENTO) AS cancelamento, ");
        sql.append("        SUM(saldoanterior) + SUM(inclusao) +  SUM(ATUALIZACAO) - SUM(PAGAMENTO) - SUM(CANCELAMENTO) as saldoAtual ");
        sql.append(" from ( ");
        sql.append(" SELECT coalesce(pf.nome, pj.razaosocial) as nomePessoa, ");
        sql.append("        divida.numeroDocContProc as processoTribunal, ");
        sql.append("        saldo.data AS dataSaldo, ");
        sql.append("        divida.naturezaDebito as naturezaPrecatorio, ");
        sql.append("        0 AS saldoanterior, ");
        sql.append("        (saldo.INSCRICAO) AS inclusao, ");
        sql.append("        (saldo.ATUALIZACAO) - (saldo.APROPRIACAO) AS atualizacao, ");
        sql.append("        (saldo.PAGAMENTO) AS pagamento, ");
        sql.append("        (saldo.CANCELAMENTO) AS cancelamento ");
        sql.append("   FROM saldodividapublica saldo ");
        sql.append("  INNER JOIN dividapublica divida ON saldo.DIVIDAPUBLICA_ID = divida.id ");
        sql.append("  inner join pessoa p on divida.pessoa_id = p.id ");
        sql.append("   left join pessoafisica pf on p.id = pf.id ");
        sql.append("   left join pessoajuridica pj on p.id = pj.id ");
        sql.append("  INNER JOIN CATEGORIADIVIDAPUBLICA cat ON divida.CATEGORIADIVIDAPUBLICA_ID = cat.id ");
        sql.append("  INNER JOIN UNIDADEDIVIDAPUBLICA unidade ON unidade.DIVIDAPUBLICA_ID = divida.id and unidade.unidadeorganizacional_id = saldo.unidadeorganizacional_id ");
        sql.append("  INNER JOIN VWHIERARQUIAORCAMENTARIA vw ON vw.SUBORDINADA_ID = saldo.UNIDADEORGANIZACIONAL_ID ");
        sql.append("  INNER JOIN VWHIERARQUIAORCAMENTARIA vworg ON vworg.SUBORDINADA_ID = vw.SUPERIOR_ID         ");
        sql.append("  WHERE saldo.data = (SELECT MAX(sa.data)  ");
        sql.append("                        FROM saldodividapublica sa  ");
        sql.append("                       WHERE sa.DIVIDAPUBLICA_ID = saldo.DIVIDAPUBLICA_ID  ");
        sql.append("                        and sa.unidadeorganizacional_ID = saldo.unidadeorganizacional_ID  ");
        sql.append("                        AND sa.DATA <= to_date(:dataFinal, 'dd/MM/yyyy'))    ");
        sql.append("    AND saldo.data BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, saldo.data)  ");
        sql.append("    AND saldo.data BETWEEN vworg.iniciovigencia AND COALESCE(vworg.fimvigencia, saldo.data)  ");
        sql.append("    and cat.naturezadividapublica = :precatorio ");
        sql.append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " AND "));
        sql.append("  UNION ALL  ");
        sql.append(" SELECT coalesce(pf.nome, pj.razaosocial) as nomePessoa, ");
        sql.append("        divida.numeroDocContProc as processoTribunal, ");
        sql.append("        saldoanterior.data as dataSaldo, ");
        sql.append("        divida.naturezaDebito as naturezaPrecatorio, ");
        sql.append("        (((saldoanterior.INSCRICAO + saldoanterior.ATUALIZACAO) - (saldoanterior.APROPRIACAO + saldoanterior.PAGAMENTO + saldoanterior.CANCELAMENTO))) AS saldoanterior,  ");
        sql.append("        (saldoanterior.INSCRICAO) * - 1 AS inclusao,  ");
        sql.append("        (saldoanterior.ATUALIZACAO - saldoanterior.APROPRIACAO) * - 1    AS atualizacao,  ");
        sql.append("        (saldoanterior.PAGAMENTO) * - 1 AS pagamento,  ");
        sql.append("        (saldoanterior.CANCELAMENTO) * - 1 AS cancelamento ");
        sql.append("   FROM SALDODIVIDAPUBLICA saldoanterior  ");
        sql.append("  INNER JOIN DIVIDAPUBLICA divida ON divida.id = saldoanterior.DIVIDAPUBLICA_ID  ");
        sql.append("  inner join pessoa p on divida.pessoa_id = p.id ");
        sql.append("   left join pessoafisica pf on p.id = pf.id ");
        sql.append("   left join pessoajuridica pj on p.id = pj.id ");
        sql.append("  INNER JOIN CATEGORIADIVIDAPUBLICA cat on cat.id = divida.CATEGORIADIVIDAPUBLICA_ID  ");
        sql.append("  INNER JOIN UNIDADEDIVIDAPUBLICA unidade ON unidade.DIVIDAPUBLICA_ID = divida.id and unidade.unidadeorganizacional_id = saldoanterior.unidadeorganizacional_id  ");
        sql.append("  INNER JOIN VWHIERARQUIAORCAMENTARIA vw ON vw.SUBORDINADA_ID = saldoanterior.UNIDADEORGANIZACIONAL_ID  ");
        sql.append("  INNER JOIN VWHIERARQUIAORCAMENTARIA vworg ON vworg.SUBORDINADA_ID = vw.SUPERIOR_ID ");
        sql.append("  WHERE saldoanterior.DATA = (SELECT MAX(s.data)  ");
        sql.append("                                FROM saldodividapublica s  ");
        sql.append("                               WHERE s.DIVIDAPUBLICA_ID = saldoanterior.DIVIDAPUBLICA_ID  ");
        sql.append("                                 and s.unidadeorganizacional_ID = saldoanterior.unidadeorganizacional_ID  ");
        sql.append("                                 AND s.DATA < to_date(:dataInicial, 'dd/MM/yyyy'))  ");
        sql.append("    AND saldoanterior.data BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, saldoanterior.data)  ");
        sql.append("    AND saldoanterior.data BETWEEN vworg.iniciovigencia AND COALESCE(vworg.fimvigencia, saldoanterior.data) ");
        sql.append("    and cat.naturezadividapublica = :precatorio  ");
        sql.append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " AND "));
        sql.append(" ) GROUP BY nomePessoa, processoTribunal, dataSaldo, naturezaPrecatorio ");
        sql.append(" ) ");
        sql.append("  group by nomePessoa,  ");
        sql.append("        processoTribunal, ");
        sql.append("        naturezaPrecatorio  ");
        Query q = getEm().createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        q.setParameter("precatorio", NaturezaDividaPublica.PRECATORIO.name());
        List<DemonstrativoDebitosPrecatorios> retorno = new ArrayList<>();
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                DemonstrativoDebitosPrecatorios debitos = new DemonstrativoDebitosPrecatorios();
                debitos.setCredor((String) obj[0]);
                debitos.setNumeroProcesso((String) obj[1]);
                debitos.setData((Date) obj[2]);
                debitos.setNatureza((String) obj[3]);
                debitos.setSaldoAnterior((BigDecimal) obj[4]);
                debitos.setInclusao((BigDecimal) obj[5]);
                debitos.setAtualizacao((BigDecimal) obj[6]);
                debitos.setPagamentos((BigDecimal) obj[7]);
                debitos.setCancelamento((BigDecimal) obj[8]);
                debitos.setDesconto(BigDecimal.ZERO);
                debitos.setSaldoAtual((BigDecimal) obj[9]);
                if (hasValores(debitos)) {
                    retorno.add(debitos);
                }
            }

        }
        return retorno;
    }

    private boolean hasValores(DemonstrativoDebitosPrecatorios debitos) {
        return debitos.getSaldoAnterior().compareTo(BigDecimal.ZERO) != 0
            || debitos.getDesconto().compareTo(BigDecimal.ZERO) != 0
            || debitos.getAtualizacao().compareTo(BigDecimal.ZERO) != 0
            || debitos.getInclusao().compareTo(BigDecimal.ZERO) != 0
            || debitos.getPagamentos().compareTo(BigDecimal.ZERO) != 0
            || debitos.getSaldoAtual().compareTo(BigDecimal.ZERO) != 0
            || debitos.getCancelamento().compareTo(BigDecimal.ZERO) != 0;
    }
}
