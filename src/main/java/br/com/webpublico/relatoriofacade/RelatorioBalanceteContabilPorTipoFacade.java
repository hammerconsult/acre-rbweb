package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidadesauxiliares.BalanceteContabilFiltros;
import br.com.webpublico.entidadesauxiliares.BalanceteContabilItem;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.UtilRelatorioContabil;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 03/04/14
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RelatorioBalanceteContabilPorTipoFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<BalanceteContabilItem> buscarDados(BalanceteContabilFiltros filtros) {
        StringBuilder queryRelatorio = new StringBuilder();
        queryRelatorio.append(" with balancete (id, descricao, codigo, superior_id, saldoanterior, totalcredito, totaldebito, saldofinal, unidade_gestora, orgao, unidade, unidade_gestora_id, orgao_id, unidade_id, categoria) as ( ")
            .append(" ")
            .append(" select id, ")
            .append("        descricao, ")
            .append("        codigo, ")
            .append("        superior_id, ")
            .append("        saldoanterior, ")
            .append("        totalcredito, ")
            .append("        totaldebito, ")
            .append("        (saldoanterior + totalcredito - totaldebito) as saldofinal, ")
            .append("        unidade_gestora, ")
            .append("        orgao, ")
            .append("        unidade, ")
            .append("        unidade_gestora_id, ")
            .append("        orgao_id, ")
            .append("        unidade_id, ")
            .append("        categoria ")
            .append(" from ( ")
//              Início Saldo Anterior
            .append(" select c.id, ")
            .append("        c.descricao, ")
            .append("        c.codigo, ")
            .append("        c.superior_id, ")
            .append("        sum(s.totalcredito - s.totaldebito) as saldoanterior, ")
            .append("        0 as totalcredito, ")
            .append("        0 as totaldebito, ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? " ug.codigo || ' - ' || ug.descricao as unidade_gestora, ug.id as unidade_gestora_id, " : " '' as unidade_gestora, 0 as unidade_gestora_id, ")
            .append("        vworg.codigo || ' - ' || vworg.descricao as orgao, ")
            .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
            .append("        vworg.subordinada_id as orgao_id, ")
            .append("        vw.subordinada_id as unidade_id, ")
            .append("        c.categoria ")
            .append("  from saldocontacontabil s ")
            .append(" inner join conta c on s.contacontabil_id = c.id ")
            .append(" inner join contacontabil cc on c.id = cc.id ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID ")
            .append(" inner join VWHIERARQUIAORCAMENTARIA vworg on vworg.subordinada_id = vw.superior_id ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" inner join ( ")
            .append(" select trunc(max(datasaldo)) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
            .append(" where trunc(datasaldo) ")
            .append("01/01".equals(filtros.getDataInicial().substring(0, 5)) ? " <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') " :
                TipoBalancete.APURACAO.equals(filtros.getTipoBalancete()) || TipoBalancete.ENCERRAMENTO.equals(filtros.getTipoBalancete()) ? " <= TO_DATE(:DATAFINAL, 'DD/MM/YYYY')" :
                    " < TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') ")
            .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
            .append(" ) ms on ")
            .append(" trunc(s.datasaldo) = trunc(ms.datasaldo) ")
            .append(" and s.contacontabil_id = ms.contacontabil_id ")
            .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
            .append(" and s.tipobalancete = ms.tipobalancete ")
            .append(" where s.tipobalancete in :tipoBalanceteInicial ")
            .append(" and to_date(:DATAFINAL,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DATAFINAL,'dd/MM/yyyy')) ")
            .append(" and to_date(:DATAFINAL,'dd/MM/yyyy') BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, to_date(:DATAFINAL,'dd/MM/yyyy')) ")
            .append(UtilRelatorioContabil.concatenarParametros(filtros.getParametros(), 1, "and"))
            .append(UtilRelatorioContabil.concatenarParametros(filtros.getParametros(), 2, "and"))
            .append("  group by c.id, c.descricao, c.codigo, c.superior_id, vw.codigo, vw.descricao, vworg.codigo, vworg.descricao, vworg.subordinada_id, vw.subordinada_id, c.categoria ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? ", ug.codigo, ug.descricao, ug.id " : "")
//              Fim Saldo Anterior
            .append("  union all ")
//              Início Saldo Final
            .append(" select id, ")
            .append("        descricao, ")
            .append("        codigo, ")
            .append("        superior_id, ")
            .append("        0 as saldoanterior, ")
            .append("        sum(creditofinal - creditoanterior) as credito, ")
            .append("        sum(debitofinal - debitoanterior) as debito, ")
            .append("        unidade_gestora, ")
            .append("        unidade_gestora_id, ")
            .append("        orgao, ")
            .append("        unidade, ")
            .append("        orgao_id, ")
            .append("        unidade_id, ")
            .append("        categoria ")
            .append("  from ( ")
//              Início Saldo Anterior Mensal do Saldo Final
            .append(" select c.id, ")
            .append("        c.descricao, ")
            .append("        c.codigo, ")
            .append("        c.superior_id, ")
            .append("        sum(s.totalcredito) as creditoanterior, ")
            .append("        sum(s.totaldebito) as debitoanterior, ")
            .append("        0 as creditofinal, ")
            .append("        0 as debitofinal, ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? "ug.codigo || ' - ' || ug.descricao as unidade_gestora, ug.id as unidade_gestora_id, " : "'' as unidade_gestora, 0 as unidade_gestora_id, ")
            .append("        vworg.codigo || ' - ' || vworg.descricao as orgao, ")
            .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
            .append("        vworg.subordinada_id as orgao_id, ")
            .append("        vw.subordinada_id as unidade_id, ")
            .append("        c.categoria ")
            .append("  from saldocontacontabil s ")
            .append(" inner join conta c on s.contacontabil_id = c.id ")
            .append(" inner join contacontabil cc on c.id = cc.id ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID ")
            .append(" inner join VWHIERARQUIAORCAMENTARIA vworg on vworg.subordinada_id = vw.superior_id ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" inner join ( ")
            .append(" select trunc(max(datasaldo)) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
            .append(" where trunc(datasaldo) ")
            .append("01/01".equals(filtros.getDataInicial().substring(0, 5)) && !TipoBalancete.ABERTURA.equals(filtros.getTipoBalanceteFinal()) ? " <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') " : " < TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') ")
            .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
            .append(" ) ms on ")
            .append(" trunc(s.datasaldo) = trunc(ms.datasaldo) ")
            .append(" and s.contacontabil_id = ms.contacontabil_id ")
            .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
            .append(" and s.tipobalancete = ms.tipobalancete ")
            .append(" where s.tipobalancete in :tipoBalanceteFinal ")
            .append(" and to_date(:DATAFINAL,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DATAFINAL,'dd/MM/yyyy')) ")
            .append(" and to_date(:DATAFINAL,'dd/MM/yyyy') BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, to_date(:DATAFINAL,'dd/MM/yyyy')) ")
            .append(UtilRelatorioContabil.concatenarParametros(filtros.getParametros(), 1, "and"))
            .append(UtilRelatorioContabil.concatenarParametros(filtros.getParametros(), 2, "and"))
            .append(" group by c.id, c.descricao, c.codigo, c.superior_id, vw.codigo, vw.descricao, vworg.codigo, vworg.descricao, vworg.subordinada_id, vw.subordinada_id, c.categoria ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? ", ug.codigo, ug.descricao, ug.id " : "")
//              Fim Saldo Anterior Mensal do Saldo Final
            .append(" union all ")
//              Início Saldo Mensal do Saldo Final
            .append(" select c.id, ")
            .append("        c.descricao, ")
            .append("        c.codigo, ")
            .append("        c.superior_id, ")
            .append("        0 as creditoanterior, ")
            .append("        0 as debitoanterior, ")
            .append("        sum(s.totalcredito) as creditofinal, ")
            .append("        sum(s.totaldebito) as debitofinal, ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? "ug.codigo || ' - ' || ug.descricao as unidade_gestora, ug.id as unidade_gestora_id, " : "'' as unidade_gestora, 0 as unidade_gestora_id, ")
            .append("        vworg.codigo || ' - ' || vworg.descricao as orgao, ")
            .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
            .append("        vworg.subordinada_id as orgao_id, ")
            .append("        vw.subordinada_id as unidade_id, ")
            .append("        c.categoria ")
            .append("  from saldocontacontabil s ")
            .append(" inner join conta c on s.contacontabil_id = c.id ")
            .append(" inner join contacontabil cc on c.id = cc.id ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID ")
            .append(" inner join VWHIERARQUIAORCAMENTARIA vworg on vworg.subordinada_id = vw.superior_id ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" inner join ( ")
            .append(" select trunc(max(datasaldo)) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
            .append(" where trunc(datasaldo) <= to_date(:DATAFINAL,'dd/MM/yyyy') ")
            .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
            .append(" ) ms on ")
            .append(" trunc(s.datasaldo) = trunc(ms.datasaldo) ")
            .append(" and s.contacontabil_id = ms.contacontabil_id ")
            .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
            .append(" and s.tipobalancete = ms.tipobalancete ")
            .append(" where s.tipobalancete in :tipoBalanceteSaldoAtual ")
            .append(" and to_date(:DATAFINAL,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DATAFINAL,'dd/MM/yyyy')) ")
            .append(" and to_date(:DATAFINAL,'dd/MM/yyyy') BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, to_date(:DATAFINAL,'dd/MM/yyyy')) ")
            .append(UtilRelatorioContabil.concatenarParametros(filtros.getParametros(), 1, "and"))
            .append(UtilRelatorioContabil.concatenarParametros(filtros.getParametros(), 2, "and"))
            .append(" group by c.id, c.descricao, c.codigo, c.superior_id, vw.codigo, vw.descricao, vworg.codigo, vworg.descricao, vworg.subordinada_id, vw.subordinada_id, c.categoria ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? ", ug.codigo, ug.descricao, ug.id " : "")
//              Fim Saldo Mensal do Saldo Final
            .append(" ) ")
            .append(" group by id, descricao, codigo, superior_id, unidade_gestora, unidade_gestora_id, orgao, unidade, orgao_id, unidade_id, categoria ")
            .append(" ) ")
//              Fim Saldo Final
            .append("  union all ")
            .append("       select sup.id, ")
            .append("              sup.descricao, ")
            .append("              sup.codigo, ")
            .append("              sup.superior_id, ")
            .append("              it.saldoanterior, ")
            .append("              it.totalcredito, ")
            .append("              it.totaldebito, ")
            .append("              it.saldofinal, ")
            .append("              it.unidade_gestora, ")
            .append("              it.orgao, ")
            .append("              it.unidade, ")
            .append("              it.unidade_gestora_id, ")
            .append("              it.orgao_id, ")
            .append("              it.unidade_id, ")
            .append("              sup.categoria ")
            .append("        from conta sup ")
            .append("        inner join balancete it on sup.id = it.superior_id ")
            .append(" ) select id, ")
            .append("          descricao, ")
            .append("          codigo, ")
            .append("          coalesce(sum(saldoanterior), 0) as saldoanterior, ")
            .append("          coalesce(sum(totalcredito), 0) as totalcredito, ")
            .append("          coalesce(sum(totaldebito), 0) as totaldebito, ")
            .append("          coalesce(sum(saldofinal), 0) as saldofinal, ")
            .append("          nivelestrutura(codigo, '.') as nivel, ");
        if (filtros.getApresentacao().isApresentacaoConsolidado()) {
            queryRelatorio.append("          '' as unidade_gestora, ")
                .append("          '' as orgao, ")
                .append("          '' as unidade, ")
                .append("          0 as unidade_gestora_id, ")
                .append("          0 as orgao_id, ")
                .append("          0 as unidade_id, ")
                .append("          categoria ")
                .append("  from balancete ")
                .append("  where (coalesce(saldoanterior, 0) <> 0 or coalesce(totalcredito, 0) <> 0 or coalesce(totaldebito, 0) <> 0 or coalesce(saldofinal, 0) <> 0) ")
                .append(" group by id, codigo, descricao, categoria ")
                .append(" order by codigo ");
        } else if (filtros.getApresentacao().isApresentacaoUnidadeGestora()) {
            queryRelatorio.append("          unidade_gestora, ")
                .append("          '' as orgao, ")
                .append("          '' as unidade, ")
                .append("          unidade_gestora_id, ")
                .append("          0 as orgao_id, ")
                .append("          0 as unidade_id, ")
                .append("          categoria ")
                .append("  from balancete ")
                .append("  where (coalesce(saldoanterior, 0) <> 0 or coalesce(totalcredito, 0) <> 0 or coalesce(totaldebito, 0) <> 0 or coalesce(saldofinal, 0) <> 0) ")
                .append(" group by id, codigo, descricao,  unidade_gestora_id, unidade_gestora, categoria ")
                .append(" order by unidade_gestora, codigo ");
        } else if (filtros.getApresentacao().isApresentacaoOrgao()) {
            queryRelatorio.append("          '' as unidade_gestora, ")
                .append("          orgao, ")
                .append("          '' as unidade, ")
                .append("          0 as unidade_gestora_id, ")
                .append("          orgao_id, ")
                .append("          0 as unidade_id, ")
                .append("          categoria ")
                .append("  from balancete ")
                .append("  where (coalesce(saldoanterior, 0) <> 0 or coalesce(totalcredito, 0) <> 0 or coalesce(totaldebito, 0) <> 0 or coalesce(saldofinal, 0) <> 0) ")
                .append(" group by id, codigo, descricao, orgao_id, orgao, categoria ")
                .append(" order by orgao, codigo ");
        } else {
            queryRelatorio.append("          '' as unidade_gestora, ")
                .append("          orgao, ")
                .append("          unidade, ")
                .append("          0 as unidade_gestora_id, ")
                .append("          orgao_id, ")
                .append("          unidade_id, ")
                .append("          categoria ")
                .append("  from balancete ")
                .append("  where (coalesce(saldoanterior, 0) <> 0 or coalesce(totalcredito, 0) <> 0 or coalesce(totaldebito, 0) <> 0 or coalesce(saldofinal, 0) <> 0) ")
                .append(" group by id, codigo, descricao, orgao_id, orgao, unidade_id, unidade, categoria  ")
                .append(" order by orgao, unidade, codigo ");
        }
        Query q = em.createNativeQuery(queryRelatorio.toString());
        UtilRelatorioContabil.adicionarParametros(filtros.getParametros(), q);
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        List<BalanceteContabilItem> retorno = new ArrayList<>();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                BalanceteContabilItem balanceteContabilItem = new BalanceteContabilItem();
                balanceteContabilItem.setId(((BigDecimal) obj[0]).longValue());
                balanceteContabilItem.setDescricao((String) obj[1]);
                balanceteContabilItem.setCodigo((String) obj[2]);
                balanceteContabilItem.setSaldoAnterior((BigDecimal) obj[3]);
                balanceteContabilItem.setMovimentoCredito((BigDecimal) obj[4]);
                balanceteContabilItem.setMovimentoDebito((BigDecimal) obj[5]);
                balanceteContabilItem.setSaldoFinal((BigDecimal) obj[6]);
                balanceteContabilItem.setNivel((BigDecimal) obj[7]);
                balanceteContabilItem.setUnidadeGestora((String) obj[8]);
                balanceteContabilItem.setOrgao((String) obj[9]);
                balanceteContabilItem.setUnidade((String) obj[10]);
                balanceteContabilItem.setUnidadeGestoraId(((BigDecimal) obj[11]).longValue());
                balanceteContabilItem.setOrgaoId(((BigDecimal) obj[12]).longValue());
                balanceteContabilItem.setUnidadeId(((BigDecimal) obj[13]).longValue());
                if (filtros.getClassificacaoContaAuxiliar() != null && obj[14] != null) {
                    CategoriaConta categoriaConta = CategoriaConta.valueOf((String) obj[14]);
                    if (CategoriaConta.ANALITICA.equals(categoriaConta)) {
                        balanceteContabilItem.setContasAuxiliares(buscarDadosContasAuxiliares(filtros, balanceteContabilItem));
                    }
                }
                if (balanceteContabilItem.getSaldoAnterior().compareTo(BigDecimal.ZERO) != 0 ||
                    balanceteContabilItem.getMovimentoCredito().compareTo(BigDecimal.ZERO) != 0 ||
                    balanceteContabilItem.getMovimentoDebito().compareTo(BigDecimal.ZERO) != 0 ||
                    balanceteContabilItem.getSaldoFinal().compareTo(BigDecimal.ZERO) != 0 ||
                    !balanceteContabilItem.getContasAuxiliares().isEmpty()) {
                    retorno.add(balanceteContabilItem);

                }

            }
        }
        return retorno;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    private List<BalanceteContabilItem> buscarDadosContasAuxiliares(BalanceteContabilFiltros filtros, BalanceteContabilItem item) {
        StringBuilder queryRelatorio = new StringBuilder();
        queryRelatorio.append(" WITH balancete (descricao, codigo, superior_id, saldoanterior, totalcredito, totaldebito, saldofinal) AS ( ")
            .append(" SELECT descricao, ")
            .append("  codigo, ")
            .append("  superior_id, ")
            .append("  COALESCE(saldoanterior, 0) AS saldoanterior, ")
            .append("  COALESCE(totalcredito, 0) AS totalcredito, ")
            .append("  COALESCE(totaldebito, 0) AS totaldebito, ")
            .append("  COALESCE(saldoanterior, 0) + COALESCE(totalcredito, 0) - COALESCE(totaldebito, 0) AS saldofinal ")
            .append(" FROM ( ")
            .append(" select c.descricao, ")
            .append("        c.codigo, ")
            .append("        c.superior_id, ")
            .append("        (s.totalcredito - s.totaldebito) as saldoanterior, ")
            .append("        0 as totalcredito, ")
            .append("        0 as totaldebito ")
            .append("  from saldocontacontabil s ")
            .append(" inner join conta c on s.contacontabil_id = c.id ")
            .append(" inner join contaauxiliar cc on c.id = cc.id ")
            .append(" inner join TipoContaAuxiliar tca on cc.tipocontaauxiliar_id = tca.id ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" inner join ( ")
            .append(" select trunc(max(datasaldo)) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
            .append(" where trunc(datasaldo) ")
            .append("01/01".equals(filtros.getDataInicial().substring(0, 5)) ? " <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') " :
                TipoBalancete.APURACAO.equals(filtros.getTipoBalancete()) || TipoBalancete.ENCERRAMENTO.equals(filtros.getTipoBalancete()) ? " <= TO_DATE(:DATAFINAL, 'DD/MM/YYYY')" :
                    " < TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') ")
            .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
            .append(" ) ms on ")
            .append(" trunc(s.datasaldo) = trunc(ms.datasaldo) ")
            .append(" and s.contacontabil_id = ms.contacontabil_id ")
            .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
            .append(" and s.tipobalancete = ms.tipobalancete ")
            .append(" where s.tipobalancete in :tipoBalanceteInicial ")
            .append(" and to_date(:DATAFINAL,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DATAFINAL,'dd/MM/yyyy')) ")
            .append(" and cc.CONTACONTABIL_ID = :CONTACONTABIL_ID ")
            .append(" and tca.classificacaoContaAuxiliar = :classificacao ")
            .append(filtros.getApresentacao().isApresentacaoUnidade() ? " and vw.subordinada_id = :UNIDADE and vw.superior_id = :ORGAO " : "")
            .append(filtros.getApresentacao().isApresentacaoOrgao() ? " and vw.superior_id = :ORGAO " : "")
            .append(filtros.getApresentacao().isApresentacaoUnidadeGestora() ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(UtilRelatorioContabil.concatenarParametros(filtros.getParametros(), 1, "and"))
            .append("  union all ")
            .append(" select descricao, ")
            .append("        codigo, ")
            .append("        superior_id, ")
            .append("        0 as saldoanterior, ")
            .append("        (creditofinal - creditoanterior) as credito, ")
            .append("        (debitofinal - debitoanterior) as debito ")
            .append("  from ( ")
            .append(" select c.descricao, ")
            .append("        c.codigo, ")
            .append("        c.superior_id, ")
            .append("        s.totalcredito as creditoanterior, ")
            .append("        s.totaldebito as debitoanterior, ")
            .append("        0 as creditofinal, ")
            .append("        0 as debitofinal ")
            .append("  from saldocontacontabil s ")
            .append(" inner join conta c on s.contacontabil_id = c.id ")
            .append(" inner join contaauxiliar cc on c.id = cc.id ")
            .append(" inner join TipoContaAuxiliar tca on cc.tipocontaauxiliar_id = tca.id ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" inner join ( ")
            .append(" select trunc(max(datasaldo)) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
            .append(" where trunc(datasaldo) ")
            .append("01/01".equals(filtros.getDataInicial().substring(0, 5)) && !TipoBalancete.ABERTURA.equals(filtros.getTipoBalanceteFinal()) ? " <= TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') " : " < TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') ")
            .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
            .append(" ) ms on ")
            .append(" trunc(s.datasaldo) = trunc(ms.datasaldo) ")
            .append(" and s.contacontabil_id = ms.contacontabil_id ")
            .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
            .append(" and s.tipobalancete = ms.tipobalancete ")
            .append(" where s.tipobalancete in :tipoBalanceteFinal ")
            .append(" and to_date(:DATAFINAL,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DATAFINAL,'dd/MM/yyyy')) ")
            .append(" and cc.CONTACONTABIL_ID = :CONTACONTABIL_ID ")
            .append(" and tca.classificacaoContaAuxiliar = :classificacao ")
            .append(filtros.getApresentacao().isApresentacaoUnidade() ? " and vw.subordinada_id = :UNIDADE and vw.superior_id = :ORGAO " : "")
            .append(filtros.getApresentacao().isApresentacaoOrgao() ? " and vw.superior_id = :ORGAO " : "")
            .append(filtros.getApresentacao().isApresentacaoUnidadeGestora() ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(UtilRelatorioContabil.concatenarParametros(filtros.getParametros(), 1, "and"))
            .append(" union all ")
            .append(" select c.descricao, ")
            .append("        c.codigo, ")
            .append("        c.superior_id, ")
            .append("        0 as creditoanterior, ")
            .append("        0 as debitoanterior, ")
            .append("        s.totalcredito as creditofinal, ")
            .append("        s.totaldebito as debitofinal ")
            .append("  from saldocontacontabil s ")
            .append(" inner join conta c on s.contacontabil_id = c.id ")
            .append(" inner join contaauxiliar cc on c.id = cc.id ")
            .append(" inner join TipoContaAuxiliar tca on cc.tipocontaauxiliar_id = tca.id ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID ")
            .append(filtros.getPesquisouUg() || filtros.getApresentacao().isApresentacaoUnidadeGestora() ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" inner join ( ")
            .append(" select trunc(max(datasaldo)) as datasaldo, tipobalancete, unidadeorganizacional_id, contacontabil_id from saldocontacontabil ")
            .append(" where trunc(datasaldo) <= to_date(:DATAFINAL,'dd/MM/yyyy') ")
            .append(" group by tipobalancete, unidadeorganizacional_id, contacontabil_id ")
            .append(" ) ms on ")
            .append(" trunc(s.datasaldo) = trunc(ms.datasaldo) ")
            .append(" and s.contacontabil_id = ms.contacontabil_id ")
            .append(" and s.unidadeorganizacional_id = ms.unidadeorganizacional_id ")
            .append(" and s.tipobalancete = ms.tipobalancete ")
            .append(" where s.tipobalancete in :tipoBalanceteSaldoAtual ")
            .append(" and to_date(:DATAFINAL,'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:DATAFINAL,'dd/MM/yyyy')) ")
            .append(" and cc.CONTACONTABIL_ID = :CONTACONTABIL_ID ")
            .append(" and tca.classificacaoContaAuxiliar = :classificacao ")
            .append(filtros.getApresentacao().isApresentacaoUnidade() ? " and vw.subordinada_id = :UNIDADE and vw.superior_id = :ORGAO " : "")
            .append(filtros.getApresentacao().isApresentacaoOrgao() ? " and vw.superior_id = :ORGAO " : "")
            .append(filtros.getApresentacao().isApresentacaoUnidadeGestora() ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(UtilRelatorioContabil.concatenarParametros(filtros.getParametros(), 1, "and"))
            .append(" ) ")
            .append(" ) ")
            .append("  where (coalesce(saldoanterior, 0) <> 0 or coalesce(totalcredito, 0) <> 0 or coalesce(totaldebito, 0) <> 0) ")
            .append(" UNION ALL ")
            .append("  SELECT sup.descricao, ")
            .append("    sup.codigo, ")
            .append("    sup.superior_id, ")
            .append("    it.saldoanterior, ")
            .append("    it.totalcredito, ")
            .append("    it.totaldebito, ")
            .append("    it.saldofinal ")
            .append("  FROM conta sup ")
            .append("  INNER JOIN balancete it ON sup.id = it.superior_id ")
            .append("  where sup.categoria is null ")
            .append("  ) ")
            .append("select codigo,  ")
            .append("       descricao,  ")
            .append("       sum(saldoanterior), ")
            .append("       sum(totalcredito), ")
            .append("       sum(totaldebito), ")
            .append("       sum(saldofinal) ")
            .append("  from balancete ")
            .append(" group by codigo, ")
            .append("          descricao ")
            .append(" order by codigo");

        Query q = em.createNativeQuery(queryRelatorio.toString());
        UtilRelatorioContabil.adicionarParametrosRetirandoLocais(filtros.getParametros(), q, 2);

        q.setParameter("CONTACONTABIL_ID", item.getId());
        if (filtros.getApresentacao().isApresentacaoUnidadeGestora()) {
            q.setParameter("UNIDADEGESTORA", item.getUnidadeGestoraId());
        }
        if (filtros.getApresentacao().isApresentacaoUnidade()) {
            q.setParameter("ORGAO", item.getOrgaoId());
            q.setParameter("UNIDADE", item.getUnidadeId());
        }
        if (filtros.getApresentacao().isApresentacaoOrgao()) {
            q.setParameter("ORGAO", item.getOrgaoId());
        }
        q.setParameter("classificacao", filtros.getClassificacaoContaAuxiliar().name());

        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        List<BalanceteContabilItem> retorno = new ArrayList<>();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                BalanceteContabilItem balanceteContabilItem = new BalanceteContabilItem();
                balanceteContabilItem.setCodigo(obj[0] instanceof BigDecimal ? ((BigDecimal) obj[0]).toString() : (String) obj[0]);
                balanceteContabilItem.setDescricao((String) obj[1]);
                balanceteContabilItem.setSaldoAnterior((BigDecimal) obj[2]);
                balanceteContabilItem.setMovimentoCredito((BigDecimal) obj[3]);
                balanceteContabilItem.setMovimentoDebito((BigDecimal) obj[4]);
                balanceteContabilItem.setSaldoFinal((BigDecimal) obj[5]);

                balanceteContabilItem.setUnidadeGestora(item.getUnidadeGestora());
                balanceteContabilItem.setOrgao(item.getOrgao());
                balanceteContabilItem.setUnidade(item.getUnidade());
                balanceteContabilItem.setUnidadeGestoraId(item.getUnidadeGestoraId());
                balanceteContabilItem.setOrgaoId(item.getOrgaoId());
                balanceteContabilItem.setUnidadeId(item.getUnidadeId());
                if (balanceteContabilItem.getSaldoAnterior().compareTo(BigDecimal.ZERO) != 0 ||
                    balanceteContabilItem.getMovimentoCredito().compareTo(BigDecimal.ZERO) != 0 ||
                    balanceteContabilItem.getMovimentoDebito().compareTo(BigDecimal.ZERO) != 0 ||
                    balanceteContabilItem.getSaldoFinal().compareTo(BigDecimal.ZERO) != 0) {
                    retorno.add(balanceteContabilItem);
                }
            }
        }
        return retorno;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public String getNomeArquivo(Exercicio exercicio, UnidadeGestora unidadeGestora, Date dataInicial, Date dataFinal, ApresentacaoRelatorio apresentacao, TipoBalancete tipoBalanceteInicial, TipoBalancete tipoBalanceteFinal) {
        //BLC-YYYY-MI-DI-TPI-MF-DF-TPF-CSL
        String hifen = "-";
        String nomeArquivo = "BLC-" + exercicio.getAno() + hifen
            + StringUtil.cortarOuCompletarEsquerda(DataUtil.getMes(dataInicial) + "", 2, "0") + hifen
            + StringUtil.cortarOuCompletarEsquerda(DataUtil.getDia(dataInicial) + "", 2, "0") + hifen
            + tipoBalanceteInicial.getDescricao().substring(0, 3).toUpperCase() + hifen
            + StringUtil.cortarOuCompletarEsquerda(DataUtil.getMes(dataFinal) + "", 2, "0") + hifen
            + StringUtil.cortarOuCompletarEsquerda(DataUtil.getDia(dataFinal) + "", 2, "0") + hifen
            + tipoBalanceteFinal.getDescricao().substring(0, 3).toUpperCase() + hifen
            + apresentacao.getSigla().toUpperCase();
        if (unidadeGestora != null) {
            nomeArquivo += hifen + unidadeGestora.getCodigo();
        }
        return nomeArquivo;
    }
}
