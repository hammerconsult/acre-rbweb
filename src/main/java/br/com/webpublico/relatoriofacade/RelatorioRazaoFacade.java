package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RazaoCabecalho;
import br.com.webpublico.entidadesauxiliares.RazaoMovimentos;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.negocios.ConfiguracaoCabecalhoFacade;
import br.com.webpublico.util.UtilRelatorioContabil;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 29/05/14
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RelatorioRazaoFacade extends RelatorioContabilSuperFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoCabecalhoFacade configuracaoCabecalhoFacade;

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<RazaoCabecalho> montarConsulta(ApresentacaoRelatorio apresentacao, Boolean pesquisouUg, List<ParametrosRelatorios> parametros, String dataInicial, TipoBalancete tipoInicial, TipoBalancete tipoFinal) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT S.CONTACONTABIL_ID AS idConta, ")
            .append("       c.codigo || ' - ' || c.descricao as codigoConta, ")
            .append("       descricaoOrgao, ")
            .append("       idOrgao, ")
            .append("       descricaoUnidade, ")
            .append("       idUnidade, ")
            .append("       descricaoUnidadeGestora, ")
            .append("       idUnidadeGestora ")
            .append("   FROM (SELECT trunc(MAX(s.DATASALDO))           AS MAXDATASALDO, ")
            .append("                s.UNIDADEORGANIZACIONAL_ID AS UO, ")
            .append("                s.CONTACONTABIL_ID         AS CONTA, ")
            .append("                s.tipobalancete as tipobalancete ")
            .append(apresentacao.isApresentacaoOrgao() || apresentacao.isApresentacaoUnidade() ? " , vworg.codigo || ' - ' || vworg.descricao as descricaoOrgao, vworg.subordinada_id as idOrgao " : " , '' as descricaoOrgao, 0 as idOrgao ")
            .append(apresentacao.isApresentacaoUnidade() ? " , vw.codigo || ' - ' || vw.descricao as descricaoUnidade, vw.subordinada_id as idUnidade " : " , '' as descricaoUnidade, 0 as idUnidade ")
            .append(apresentacao.isApresentacaoUnidadeGestora() ? " , ug.codigo || ' - ' || ug.descricao as descricaoUnidadeGestora, ug.id as idUnidadeGestora " : " , '' as descricaoUnidadeGestora, 0 as idUnidadeGestora ")
            .append("           FROM SALDOCONTACONTABIL s ")
            .append("          inner join conta c on S.CONTACONTABIL_ID = c.id ")
            .append("          inner join contacontabil cc on c.id = cc.id ")
            .append("          INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID ")
            .append("          INNER JOIN VWHIERARQUIAORCAMENTARIA VWorg ON vw.superior_id = VWorg.SUBORDINADA_ID ")
            .append(apresentacao.isApresentacaoUnidadeGestora() || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append("          where to_date(:DataFinal,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DataFinal,'dd/mm/yyyy')) ")
            .append("            and to_date(:DataFinal,'dd/mm/yyyy') BETWEEN VWorg.INICIOVIGENCIA AND COALESCE(VWorg.FIMVIGENCIA, to_date(:DataFinal,'dd/mm/yyyy')) ")
            .append("            and c.exercicio_id = :exercicio ")
            .append("            and s.tipobalancete IN :tipoBalanceteInicial ")
            .append("            AND trunc(s.DATASALDO)   <= to_date(:DataFinal,'dd/mm/yyyy') ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append("          GROUP BY s.UNIDADEORGANIZACIONAL_ID, ")
            .append("                   s.CONTACONTABIL_ID, ")
            .append("                   s.tipobalancete ")
            .append(apresentacao.isApresentacaoOrgao() || apresentacao.isApresentacaoUnidade() ? " , vworg.codigo, vworg.descricao, vworg.subordinada_id " : "")
            .append(apresentacao.isApresentacaoUnidade() ? " , vw.codigo, vw.descricao, vw.subordinada_id " : "")
            .append(apresentacao.isApresentacaoUnidadeGestora() ? " , ug.codigo, ug.descricao, ug.id " : "")
            .append("          union all ")
            .append("         SELECT trunc(MAX(s.DATASALDO)) AS MAXDATASALDO, ")
            .append("                s.UNIDADEORGANIZACIONAL_ID AS UO, ")
            .append("                s.CONTACONTABIL_ID         AS CONTA, ")
            .append("                s.tipobalancete as tipobalancete ")
            .append(apresentacao.isApresentacaoOrgao() || apresentacao.isApresentacaoUnidade() ? " , vworg.codigo || ' - ' || vworg.descricao as descricaoOrgao, vworg.subordinada_id as idOrgao " : " , '' as descricaoOrgao, 0 as idOrgao ")
            .append(apresentacao.isApresentacaoUnidade() ? " , vw.codigo || ' - ' || vw.descricao as descricaoUnidade, vw.subordinada_id as idUnidade " : " , '' as descricaoUnidade, 0 as idUnidade ")
            .append(apresentacao.isApresentacaoUnidadeGestora() ? " , ug.codigo || ' - ' || ug.descricao as descricaoUnidadeGestora, ug.id as idUnidadeGestora " : " , '' as descricaoUnidadeGestora, 0 as idUnidadeGestora ")
            .append("           FROM SALDOCONTACONTABIL s ")
            .append("          inner join conta c on S.CONTACONTABIL_ID = c.id ")
            .append("          inner join contacontabil cc on c.id = cc.id ")
            .append("          INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID ")
            .append("          INNER JOIN VWHIERARQUIAORCAMENTARIA VWorg ON vw.superior_id = VWorg.SUBORDINADA_ID ")
            .append(apresentacao.isApresentacaoUnidadeGestora() || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append("          where to_date(:DataFinal,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DataFinal,'dd/mm/yyyy')) ")
            .append("            and to_date(:DataFinal,'dd/mm/yyyy') BETWEEN VWorg.INICIOVIGENCIA AND COALESCE(VWorg.FIMVIGENCIA, to_date(:DataFinal,'dd/mm/yyyy')) ")
            .append("            and c.exercicio_id = :exercicio ")
            .append("            and s.tipobalancete IN :tipoBalanceteFinal ")
            .append("            AND trunc(s.DATASALDO)    <= to_date(:DataFinal,'dd/mm/yyyy') ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append("          GROUP BY s.UNIDADEORGANIZACIONAL_ID, ")
            .append("                   s.CONTACONTABIL_ID, ")
            .append("                   s.tipobalancete ")
            .append(apresentacao.isApresentacaoOrgao() || apresentacao.isApresentacaoUnidade() ? " , vworg.codigo, vworg.descricao, vworg.subordinada_id " : "")
            .append(apresentacao.isApresentacaoUnidade() ? " , vw.codigo, vw.descricao, vw.subordinada_id " : "")
            .append(apresentacao.isApresentacaoUnidadeGestora() ? " , ug.codigo, ug.descricao, ug.id " : "")
            .append("      ) MAX_SALDO ")
            .append("  INNER JOIN SALDOCONTACONTABIL S ON S.UNIDADEORGANIZACIONAL_ID = MAX_SALDO.UO ")
            .append("                                 AND S.CONTACONTABIL_ID         = MAX_SALDO.CONTA ")
            .append("                                 AND trunc(S.DATASALDO)         = trunc(MAX_SALDO.MAXDATASALDO) ")
            .append("                                 and s.tipobalancete            = max_saldo.tipobalancete ")
            .append("  inner join conta c on S.CONTACONTABIL_ID = c.id ")
            .append("  where (s.totalcredito > 0 or s.totaldebito > 0) ")
            .append("  group by S.CONTACONTABIL_ID, c.codigo, c.descricao, ")
            .append("       descricaoOrgao, ")
            .append("       idOrgao, ")
            .append("       descricaoUnidade, ")
            .append("       idUnidade, ")
            .append("       descricaoUnidadeGestora, ")
            .append("       idUnidadeGestora ");
        if (apresentacao.isApresentacaoOrgao()) {
            sql.append("  order by descricaoOrgao, c.codigo ");
        } else if (apresentacao.isApresentacaoUnidade()) {
            sql.append("  order by descricaoUnidade, c.codigo ");
        } else if (apresentacao.isApresentacaoUnidadeGestora()) {
            sql.append("  order by descricaoUnidadeGestora, c.codigo ");
        } else {
            sql.append("  order by c.codigo ");
        }
        Query q = getEm().createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametrosComparandoLocais(parametros, q, 0, 1, 4, 5);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<RazaoCabecalho> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                HashMap parametrosAuxiliares = new HashMap();
                RazaoCabecalho item = new RazaoCabecalho();
                item.setDescricaoConta((String) obj[1]);
                item.setOrgao((String) obj[2]);
                item.setUnidade((String) obj[4]);
                item.setUnidadeGestora((String) obj[6]);
                parametrosAuxiliares.put("DataInicial", dataInicial);
                parametrosAuxiliares.put("tipoInicial", tipoInicial.name());
                parametrosAuxiliares.put("tipoFinal", tipoFinal.name());
                parametrosAuxiliares.put("idConta", ((BigDecimal) obj[0]).longValue());
                parametrosAuxiliares.put("idOrgao", ((BigDecimal) obj[3]).longValue());
                parametrosAuxiliares.put("idUnidade", ((BigDecimal) obj[5]).longValue());
                parametrosAuxiliares.put("idUnidadeGestora", ((BigDecimal) obj[7]).longValue());
                item.setSaldoAnterior(buscarSaldoAnterior(apresentacao, pesquisouUg, parametros, parametrosAuxiliares));
                item.setMovimentos(buscarMovimentos(apresentacao, pesquisouUg, parametros, parametrosAuxiliares));
                retorno.add(item);
            }
            return retorno;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public BigDecimal buscarSaldoAnterior(ApresentacaoRelatorio apresentacao, Boolean pesquisouUg, List<ParametrosRelatorios> parametros, HashMap parametrosAuxiliares) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT coalesce(sum(S.totalcredito), 0) - coalesce(sum(s.totaldebito), 0) AS saldo ")
            .append("   FROM (SELECT trunc(MAX(TEMP.DATASALDO)) AS MAXDATASALDO, ")
            .append("                TEMP.UNIDADEORGANIZACIONAL_ID AS UO, ")
            .append("                TEMP.CONTACONTABIL_ID         AS CONTA, ")
            .append("                temp.tipobalancete as tipobalancete ")
            .append("           FROM SALDOCONTACONTABIL TEMP ")
            .append("          WHERE temp.tipobalancete IN :tipoBalanceteInicial ")
            .append("            AND trunc(TEMP.DATASALDO)  ")
            .append(((String) parametrosAuxiliares.get("DataInicial")).substring(0, 5).equals("01/01") ? " <= TO_DATE(:DataInicial, 'DD/MM/YYYY') " :
                parametrosAuxiliares.get("tipoInicial").equals("APURACAO") || parametrosAuxiliares.get("tipoInicial").equals("ENCERRAMENTO") ? " <= TO_DATE(:DataFinal, 'DD/MM/YYYY')" :
                    " < TO_DATE(:DataInicial, 'DD/MM/YYYY') ")
            .append("          GROUP BY TEMP.UNIDADEORGANIZACIONAL_ID, ")
            .append("                   TEMP.CONTACONTABIL_ID, ")
            .append("                   temp.tipobalancete ")
            .append("      ) MAX_SALDO ")
            .append("  INNER JOIN SALDOCONTACONTABIL S ON S.UNIDADEORGANIZACIONAL_ID = MAX_SALDO.UO ")
            .append("                                 AND S.CONTACONTABIL_ID         = MAX_SALDO.CONTA ")
            .append("                                 AND trunc(S.DATASALDO)         = trunc(MAX_SALDO.MAXDATASALDO) ")
            .append("                                 and s.tipobalancete            = max_saldo.tipobalancete ")
            .append("  inner join conta c on S.CONTACONTABIL_ID = c.id ")
            .append("  inner join contacontabil cc on c.id = cc.id ")
            .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON MAX_SALDO.uo = VW.SUBORDINADA_ID ")
            .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VWorg ON vw.superior_id = VWorg.SUBORDINADA_ID ")
            .append(apresentacao.isApresentacaoUnidadeGestora() || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append("  where to_date(:DataFinal,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DataFinal,'dd/mm/yyyy')) ")
            .append("    and to_date(:DataFinal,'dd/mm/yyyy') BETWEEN VWorg.INICIOVIGENCIA AND COALESCE(VWorg.FIMVIGENCIA, to_date(:DataFinal,'dd/mm/yyyy')) ")
            .append("    and (s.totalcredito > 0 or s.totaldebito > 0) ")
            .append("    and c.exercicio_id = :exercicio ")
            .append("    and c.id = :idConta ")
            .append(((Long) parametrosAuxiliares.get("idOrgao")).compareTo(0l) != 0 ? "    and vworg.subordinada_id = :idOrgao " : "")
            .append(((Long) parametrosAuxiliares.get("idUnidade")).compareTo(0l) != 0 ? "    and vw.subordinada_id = :idUnidade and vworg.subordinada_id = :idOrgao  " : "")
            .append(((Long) parametrosAuxiliares.get("idUnidadeGestora")).compareTo(0l) != 0 ? "    and ug.id = :idUnidadeGestora " : "")
            .append(concatenaParametros(parametros, 1, " and "));
        Query q = getEm().createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametrosComparandoLocais(parametros, q, 1, 3, 4, 5, 6);

        q.setParameter("idConta", parametrosAuxiliares.get("idConta"));
        if (((Long) parametrosAuxiliares.get("idOrgao")).compareTo(0l) != 0) {
            q.setParameter("idOrgao", parametrosAuxiliares.get("idOrgao"));
        }
        if (((Long) parametrosAuxiliares.get("idUnidade")).compareTo(0l) != 0) {
            q.setParameter("idOrgao", parametrosAuxiliares.get("idOrgao"));
            q.setParameter("idUnidade", parametrosAuxiliares.get("idUnidade"));
        }
        if (((Long) parametrosAuxiliares.get("idUnidadeGestora")).compareTo(0l) != 0) {
            q.setParameter("idUnidadeGestora", parametrosAuxiliares.get("idUnidadeGestora"));
        }
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getSingleResult();
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<RazaoMovimentos> buscarMovimentos(ApresentacaoRelatorio apresentacao, Boolean pesquisouUg, List<ParametrosRelatorios> parametros, HashMap parametrosAuxiliares) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT codigoCLP, ")
            .append("        itemLCP, ")
            .append("        codigoLCP, ")
            .append("        codigoEvento, ")
            .append("        descricaoEvento, ")
            .append("        codigoUnidade, ")
            .append("        codigoOrgao, ")
            .append("        codigoContaContra, ")
            .append("        descricaoContaContra, ")
            .append("        dataLancamento, ")
            .append("        operacao, ")
            .append("        historico, ")
            .append("        valor ")
            .append("   from ( ")     // Crédito
            .append(" SELECT CONF.CODIGO AS codigoCLP, ")
            .append("        ITEM.ITEM AS itemLCP, ")
            .append("        ITEM.CODIGO AS codigoLCP, ")
            .append("        EVENTO.CODIGO AS codigoEvento, ")
            .append("        EVENTO.DESCRICAO AS descricaoEvento, ")
            .append("        SUBSTR(VW.CODIGO, 8, 3) AS codigoUnidade, ")
            .append("        SUBSTR(VW.CODIGO, 4, 3) AS codigoOrgao, ")
            .append("        CONTACONTRADEB.CODIGO AS codigoContaContra, ")
            .append("        CONTACONTRADEB.DESCRICAO AS descricaoContaContra, ")
            .append("        cast(LANC.DATALANCAMENTO as Date) AS dataLancamento, ")
            .append("        'CREDITO' AS operacao, ")
            .append("        LANC.COMPLEMENTOHISTORICO as historico, ")
            .append("        COALESCE(LANC.VALOR, 0) AS valor ")
            .append("   FROM CONTACONTABIL CONTA ")
            .append("  INNER JOIN CONTA c ON CONTA.ID = c.ID ")
            .append("  INNER JOIN LANCAMENTOCONTABIL LANC ON LANC.CONTACREDITO_ID = c.ID ")
            .append("  INNER JOIN LCP ITEM ON LANC.LCP_ID = ITEM.ID ")
            .append("  INNER JOIN CLP CONF ON ITEM.CLP_ID = CONF.ID ")
            .append("  INNER JOIN ITEMPARAMETROEVENTO ITEMPARAM ON LANC.ITEMPARAMETROEVENTO_ID = ITEMPARAM.ID ")
            .append("  INNER JOIN PARAMETROEVENTO PARAM ON ITEMPARAM.PARAMETROEVENTO_ID = PARAM.ID ")
            .append("  INNER JOIN EVENTOCONTABIL EVENTO ON PARAM.EVENTOCONTABIL_ID = EVENTO.ID ")
            .append("  INNER JOIN CONTA CONTACONTRADEB ON LANC.CONTADEBITO_ID = CONTACONTRADEB.ID ")
            .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON LANC.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID ")
            .append(apresentacao.isApresentacaoUnidadeGestora() || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append("  where trunc(lanc.DATALANCAMENTO) BETWEEN to_date(:DataInicial, 'dd/mm/yyyy') AND to_date(:DataFinal, 'dd/mm/yyyy') ")
            .append("    and to_date(:DataFinal, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DataFinal, 'dd/mm/yyyy')) ")
            .append("    and to_date(:DataFinal, 'dd/mm/yyyy') BETWEEN trunc(CONF.INICIOVIGENCIA) AND COALESCE(trunc(CONF.FIMVIGENCIA), to_date(:DataFinal, 'dd/mm/yyyy')) ")
            .append("    and c.EXERCICIO_ID = :exercicio ")
            .append("    and c.id = :idConta ")
            .append("    and param.TIPOBALANCETE in :tipoBalanceteFinal ")
            .append(((Long) parametrosAuxiliares.get("idOrgao")).compareTo(0l) != 0 ? "    and vw.superior_id = :idOrgao " : "")
            .append(((Long) parametrosAuxiliares.get("idUnidade")).compareTo(0l) != 0 ? "    and vw.subordinada_id = :idUnidade and vw.superior_id = :idOrgao  " : "")
            .append(((Long) parametrosAuxiliares.get("idUnidadeGestora")).compareTo(0l) != 0 ? "    and ug.id = :idUnidadeGestora " : "")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append("  union all ")  // Débito
            .append(" SELECT CONF.CODIGO AS codigoCLP, ")
            .append("        ITEM.ITEM AS itemLCP, ")
            .append("        ITEM.CODIGO AS codigoLCP, ")
            .append("        EVENTO.CODIGO AS codigoEvento, ")
            .append("        EVENTO.DESCRICAO AS descricaoEvento, ")
            .append("        SUBSTR(VW.CODIGO, 8, 3) AS codigoUnidade, ")
            .append("        SUBSTR(VW.CODIGO, 4, 3) AS codigoOrgao, ")
            .append("        CONTACONTRACRED.CODIGO AS codigoContaContra, ")
            .append("        CONTACONTRACRED.DESCRICAO AS descricaoContaContra, ")
            .append("        cast(LANC.DATALANCAMENTO as Date) AS dataLancamento, ")
            .append("        'DEBITO' AS operacao, ")
            .append("        LANC.COMPLEMENTOHISTORICO as historico, ")
            .append("        COALESCE(LANC.VALOR, 0) AS valor ")
            .append("   FROM CONTACONTABIL CONTA ")
            .append("  INNER JOIN CONTA c ON CONTA.ID = c.ID ")
            .append("  INNER JOIN LANCAMENTOCONTABIL LANC ON LANC.CONTADEBITO_ID = c.ID ")
            .append("  INNER JOIN LCP ITEM ON LANC.LCP_ID = ITEM.ID ")
            .append("  INNER JOIN CLP CONF ON ITEM.CLP_ID = CONF.ID ")
            .append("  INNER JOIN ITEMPARAMETROEVENTO ITEMPARAM ON LANC.ITEMPARAMETROEVENTO_ID = ITEMPARAM.ID ")
            .append("  INNER JOIN PARAMETROEVENTO PARAM ON ITEMPARAM.PARAMETROEVENTO_ID = PARAM.ID ")
            .append("  INNER JOIN EVENTOCONTABIL EVENTO ON PARAM.EVENTOCONTABIL_ID = EVENTO.ID ")
            .append("  INNER JOIN CONTA CONTACONTRACRED ON LANC.CONTACREDITO_ID = CONTACONTRACRED.ID ")
            .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON LANC.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID ")
            .append(apresentacao.isApresentacaoUnidadeGestora() || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append("  where trunc(lanc.DATALANCAMENTO) BETWEEN to_date(:DataInicial, 'dd/mm/yyyy') AND to_date(:DataFinal, 'dd/mm/yyyy') ")
            .append("    and to_date(:DataFinal, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DataFinal, 'dd/mm/yyyy')) ")
            .append("    and to_date(:DataFinal, 'dd/mm/yyyy') BETWEEN trunc(CONF.INICIOVIGENCIA) AND COALESCE(trunc(CONF.FIMVIGENCIA), to_date(:DataFinal, 'dd/mm/yyyy')) ")
            .append("    and c.EXERCICIO_ID = :exercicio ")
            .append("    and c.id = :idConta ")
            .append("    and param.TIPOBALANCETE in :tipoBalanceteFinal ")
            .append(((Long) parametrosAuxiliares.get("idOrgao")).compareTo(0l) != 0 ? "    and vw.superior_id = :idOrgao " : "")
            .append(((Long) parametrosAuxiliares.get("idUnidade")).compareTo(0l) != 0 ? "    and vw.subordinada_id = :idUnidade and vw.superior_id = :idOrgao  " : "")
            .append(((Long) parametrosAuxiliares.get("idUnidadeGestora")).compareTo(0l) != 0 ? "    and ug.id = :idUnidadeGestora " : "")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append(" ) ")
            .append(" order by dataLancamento ");
        Query q = getEm().createNativeQuery(sql.toString());
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1 || parametrosRelatorios.getLocal() == 2 || parametrosRelatorios.getLocal() == 5) {
                q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
                if (parametrosRelatorios.getCampo2() != null) {
                    q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
                }
            }
        }
        q.setParameter("idConta", parametrosAuxiliares.get("idConta"));
        if (((Long) parametrosAuxiliares.get("idOrgao")).compareTo(0l) != 0) {
            q.setParameter("idOrgao", parametrosAuxiliares.get("idOrgao"));
        }
        if (((Long) parametrosAuxiliares.get("idUnidade")).compareTo(0l) != 0) {
            q.setParameter("idOrgao", parametrosAuxiliares.get("idOrgao"));
            q.setParameter("idUnidade", parametrosAuxiliares.get("idUnidade"));
        }
        if (((Long) parametrosAuxiliares.get("idUnidadeGestora")).compareTo(0l) != 0) {
            q.setParameter("idUnidadeGestora", parametrosAuxiliares.get("idUnidadeGestora"));
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<RazaoMovimentos> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RazaoMovimentos item = new RazaoMovimentos();
                item.setCodigoCLP((String) obj[0]);
                item.setItemLCP(((BigDecimal) obj[1]).toString());
                item.setCodigoLCP((String) obj[2]);
                item.setCodigoEvento((String) obj[3]);
                item.setDescricaoEvento((String) obj[4]);
                item.setCodigoUnidade((String) obj[5]);
                item.setCodigoOrgao((String) obj[6]);
                item.setCodigoContaContra((String) obj[7]);
                item.setDescricaoContaContra((String) obj[8]);
                item.setData((Date) obj[9]);
                item.setOperacao((String) obj[10]);
                item.setHistorico((String) obj[11]);
                item.setValorMovimento((BigDecimal) obj[12]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public EntityManager getEm() {
        return em;
    }

    public ConfiguracaoCabecalhoFacade getConfiguracaoCabecalhoFacade() {
        return configuracaoCabecalhoFacade;
    }
}
