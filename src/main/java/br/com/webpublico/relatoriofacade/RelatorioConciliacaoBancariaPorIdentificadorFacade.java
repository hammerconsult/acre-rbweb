package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.ConciliacaoBancariaConstante;
import br.com.webpublico.entidadesauxiliares.ConciliacaoBancariaItem;
import br.com.webpublico.entidadesauxiliares.ConciliacaoBancariaMovimentos;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRelatorioContabil;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mateus on 04/08/17.
 */
@Stateless
public class RelatorioConciliacaoBancariaPorIdentificadorFacade extends AbstractRelatorioConciliacaoBancariaFacade implements Serializable {

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    @Override
    public ConciliacaoBancariaItem gerarConsultaSaldo(Boolean pesquisouUg, List<ParametrosRelatorios> parametros, String dataSaldoAnterior, HashMap parametrosItens, ConciliacaoBancariaItem item) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(ssb.totaldebito - ssb.totalcredito), 0) as saldocontabil ")
            .append("  FROM SALDOSUBCONTA SSB ")
            .append(" inner join subconta sub on ssb.subconta_id = sub.id ")
            .append(" inner join contabancariaentidade cbe on sub.contabancariaentidade_id = cbe.id ")
            .append(" inner join fontederecursos font on ssb.fontederecursos_id = font.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on ssb.unidadeorganizacional_id  = vw.subordinada_id  ")
            .append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" inner join (select trunc(max(datasaldo)) as datasaldo, subconta_id, fontederecursos_id, unidadeorganizacional_id ")
            .append("               from saldosubconta ")
            .append(dataSaldoAnterior)
            .append("           group by subconta_id, fontederecursos_id, unidadeorganizacional_id) dataSaldo on dataSaldo.subconta_id = ssb.subconta_id ")
            .append("       and trunc(ssb.datasaldo) = trunc(dataSaldo.datasaldo) ")
            .append("       and ssb.fontederecursos_id = dataSaldo.fontedeRecursos_id ")
            .append("       and ssb.unidadeorganizacional_id = datasaldo.unidadeorganizacional_id ")
            .append(" where cbe.id = :CBE_ID ")
            .append("   and trunc(ssb.datasaldo) <= to_date(:DATAREFERENCIA, 'dd/mm/yyyy') ")
            .append("   and font.exercicio_id =  :EXERCICIO_ID ")
            .append("   and to_date(:DATAREFERENCIA, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:DATAREFERENCIA, 'dd/mm/yyyy')) ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
            .append(" group by cbe.id ");
        Query q = getEm().createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametrosRetirandoLocais(parametros, q, 2, 4);
        q.setParameter("CBE_ID", parametrosItens.get("CBE_ID"));
        if (!q.getResultList().isEmpty()) {
            item.setSaldoContabil((BigDecimal) q.getSingleResult());
        } else {
            item.setSaldoContabil(BigDecimal.ZERO);
        }
        return item;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    @Override
    public List<ConciliacaoBancariaItem> buscarRelatorio(List<ParametrosRelatorios> parametros, Boolean pesquisouUg, String dataSaldoAnterior, Boolean movimentosDiferenteZero) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from ( ")
            .append(" select distinct b.NUMEROBANCO || ' - '  || b.DESCRICAO AS BANCO, ")
            .append("       ag.NUMEROAGENCIA  || ' - '  || ag.DIGITOVERIFICADOR ||' - '  || ag.NOMEAGENCIA AS AGENCIA, ")
            .append("       CBE.NUMEROCONTA || ' - ' || CBE.DIGITOVERIFICADOR AS NUMEROCONTA, ")
            .append("       CBE.NOMECONTA AS TITULOCONTA, ")
            .append("       CBE.TIPOCONTABANCARIA AS TIPOCONTA, ")
            .append("       CBE.ID AS CONTABANCARIA_ID, ")
            .append("       to_number(cbe.numeroconta) as ordenacaoNumeroConta, ")
            .append("       b.numeroBanco as ordenacaoBanco, ")
            .append("       ag.NUMEROAGENCIA as ordenacaoAgencia ")
            .append("  from subconta sub ")
            .append(" inner join contabancariaentidade cbe on sub.contabancariaentidade_id = cbe.id ")
            .append(" inner join agencia ag on cbe.agencia_id = ag.id ")
            .append(" inner join banco b on ag.banco_id = b.id ")
            .append(" inner join subcontauniorg subund on sub.id = subund.subconta_id ")
            .append(" inner join vwhierarquiaorcamentaria vw on subund.unidadeorganizacional_id = vw.subordinada_id ")
            .append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" where to_date(:DATAREFERENCIA, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:DATAREFERENCIA, 'dd/mm/yyyy')) ")
            .append("   and subund.exercicio_id = :EXERCICIO_ID ")
            .append("   and cbe.id = :CBE_ID ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(") order by ordenacaoBanco, ordenacaoAgencia, ordenacaoNumeroConta ");
        Query q = getEm().createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametrosRetirandoLocais(parametros, q, 2, 3, 4);
        List<ConciliacaoBancariaItem> retorno = new ArrayList<>();
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                ConciliacaoBancariaItem item = new ConciliacaoBancariaItem();
                HashMap parametrosItens = new HashMap();
                item.setBanco((String) obj[0]);
                item.setAgencia((String) obj[1]);
                item.setNumero((String) obj[2]);
                item.setTituloContaBancaria((String) obj[3]);
                item.setTipoContaBancaria((String) obj[4]);
                parametrosItens.put("CBE_ID", ((BigDecimal) obj[5]).longValue());
                item = gerarConsultaSaldo(pesquisouUg, parametros, dataSaldoAnterior, parametrosItens, item);
                item.setMovimentos(gerarConsultaMovimentos(pesquisouUg, parametros, parametrosItens, movimentosDiferenteZero));
                if (item.getSaldoContabil().compareTo(BigDecimal.ZERO) != 0) {
                    retorno.add(item);
                } else if (movimentosDiferenteZero) {
                    retorno.add(item);
                }
            }
        }
        return retorno;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    @Override
    public List<ConciliacaoBancariaMovimentos> gerarConsultaMovimentos(Boolean pesquisouUg, List<ParametrosRelatorios> parametros, HashMap parametrosItens, Boolean mostrarMovimentoIgualZero) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select historico, ")
            .append("        datamovimento,  ")
            .append("        dataconciliacao,  ")
            .append("        credito,  ")
            .append("        debito,  ")
            .append("        numero, ")
            .append("        tipoMovimento,  ")
            .append("        case when numeroIdentificador is not null then 'IDENTIFICADOR: ' || numeroIdentificador || ' - ' || descricaoIdentificador ")
            .append("        else 'IDENTIFICADOR: ' end as identificador ")
            .append("   from (  ")
            .append(" select vwmov.historico,  ")
            .append("        vwmov.data_movimento as datamovimento,  ")
            .append("        vwmov.data_conciliacao as dataconciliacao,  ")
            .append("        vwmov.credito as credito,  ")
            .append("        vwmov.debito as debito,  ")
            .append("        vwmov.numero as numero, ")
            .append("        case vwmov.tipo_movimento  ")
            .append("           when 'AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO' then 'Ajt. Atv. Disp. Aum.' ")
            .append("           when 'AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO' then 'Ajt. Atv. Disp. Dim.' ")
            .append("           when 'DESPESA_EXTRA' then 'Despesa Extra' ")
            .append("           when 'ESTORNO_AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO' then 'Est. de Ajt. Atv. Disp. Aum.' ")
            .append("           when 'ESTORNO_AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO' then 'Est. de Ajt. Atv. Disp. Dim.' ")
            .append("           when 'ESTORNO_DESPESA_EXTRA' then 'Est. de Desp. Extra' ")
            .append("           when 'ESTORNO_LIBERACAO_FINANCEIRA' then 'Est. de Liberação Financeira' ")
            .append("           when 'ESTORNO_LIBERACAO_FINANCEIRA_REPASSE' then 'Est. da Lib. Fin. Repasse' ")
            .append("           when 'ESTORNO_PAGAMENTO' then 'Est. de Pagamento' ")
            .append("           when 'ESTORNO_PAGAMENTO_RESTO' then 'Est. de Pag. de Resto' ")
            .append("           when 'ESTORNO_RECEITA_REALIZADA' then 'Est. de Rec. Realizada' ")
            .append("           when 'ESTORNO_TRANSFERENCIA_FINANCEIRA_DEPOSITO' then 'Est. de Transferência Fin.' ")
            .append("           when 'ESTORNO_TRANSFERENCIA_FINANCEIRA_RETIRADA' then 'Est. de Transferência Fin.' ")
            .append("           when 'LIBERACAO_FINANCEIRA' then 'Liberação Financeira' ")
            .append("           when 'LIBERACAO_FINANCEIRA_REPASSE' then 'Liberação Fin. Repasse' ")
            .append("           when 'PAGAMENTO' then 'Pagamento' ")
            .append("           when 'PAGAMENTO_RESTO' then 'Pagamento de Resto' ")
            .append("           when 'RECEITA_REALIZADA' then 'Receita Realizada' ")
            .append("           when 'TRANSFERENCIA_FINANCEIRA_DEPOSITO' then 'Transferência Financeira' ")
            .append("           when 'TRANSFERENCIA_FINANCEIRA_RETIRADA' then 'Transferência Financeira' ")
            .append("        end as tipoMovimento,  ")
            .append("        ident.numero as numeroIdentificador, ")
            .append("        ident.descricao as descricaoIdentificador,  ")
            .append("        case when ident.numero is null then 1 else 2 end as ordemIdentificador ")
            .append("   from vwmovimentocontabil vwmov  ")
            .append("  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = vwmov.unidadeorganizacional_id ")
            .append(pesquisouUg ? " inner join ugestorauorganizacional ugunidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append("  inner join subconta sub on vwmov.subconta_id = sub.id  ")
            .append("  inner join contabancariaentidade cbe on sub.contabancariaentidade_id = cbe.id ")
            .append("  inner join fontederecursos font on vwmov.fontederecursos_id = font.id ")
            .append("  inner join eventocontabil eve on vwmov.eventocontabil_id = eve.id ")
            .append("   left join identificador ident on vwmov.identificador_id = ident.id ")
            .append("  where trunc(vwmov.data_movimento) <= to_date(:DATAREFERENCIA, 'dd/mm/yyyy') ")
            .append("    and to_date(:DATAREFERENCIA, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:DATAREFERENCIA, 'dd/mm/yyyy')) ")
            .append("    and (vwmov.data_conciliacao is null ")
            .append("     or trunc(vwmov.data_conciliacao)  > to_date(:DATAREFERENCIA, 'dd/mm/yyyy')) ")
            .append("    and cbe.id = :CBE_ID ")
            .append("    and vwmov.tipo_movimento not in ('RECEITA_EXTRA', 'ESTORNO_RECEITA_EXTRA') ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append("  union all  ")
            .append(" select vwmov.historico,  ")
            .append("        vwmov.data_movimento as datamovimento,  ")
            .append("        vwmov.data_conciliacao as dataconciliacao,  ")
            .append("        vwmov.credito as credito,  ")
            .append("        vwmov.debito as debito,  ")
            .append("        vwmov.numero as numero, ")
            .append("        case vwmov.tipo_movimento  ")
            .append("           when 'RECEITA_EXTRA' then 'Receita Extra' ")
            .append("        end as tipoMovimento,  ")
            .append("        ident.numero as numeroIdentificador, ")
            .append("        ident.descricao as descricaoIdentificador, ")
            .append("        case when ident.numero is null then 1 else 2 end as ordemIdentificador ")
            .append("   from vwmovimentocontabil vwmov  ")
            .append("  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = vwmov.unidadeorganizacional_id ")
            .append(pesquisouUg ? " inner join ugestorauorganizacional ugunidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append("  inner join receitaextra recextra on vwmov.movimento_id = recextra.id  ")
            .append("  inner join contaextraorcamentaria cextra on recextra.contaextraorcamentaria_id = cextra.id  ")
            .append("  inner join subconta sub on vwmov.subconta_id = sub.id  ")
            .append("  inner join contabancariaentidade cbe on sub.contabancariaentidade_id = cbe.id ")
            .append("  inner join fontederecursos font on vwmov.fontederecursos_id = font.id ")
            .append("  inner join eventocontabil eve on vwmov.eventocontabil_id = eve.id ")
            .append("   left join identificador ident on vwmov.identificador_id = ident.id  ")
            .append("  where trunc(vwmov.data_movimento) <= to_date(:DATAREFERENCIA, 'dd/mm/yyyy') ")
            .append("    and to_date(:DATAREFERENCIA, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:DATAREFERENCIA, 'dd/mm/yyyy')) ")
            .append("    and (vwmov.data_conciliacao IS NULL ")
            .append("     or trunc(vwmov.data_conciliacao) > to_date(:DATAREFERENCIA, 'DD/MM/YYYY')) ")
            .append("    and cbe.id = :CBE_ID ")
            .append("    and vwmov.tipo_movimento in ('RECEITA_EXTRA')  ")
            .append("    and cextra.tipocontaextraorcamentaria <> 'DEPOSITOS_CONSIGNACOES'  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append("  union all  ")
            .append(" select vwmov.historico,  ")
            .append("        vwmov.data_movimento as datamovimento,  ")
            .append("        vwmov.data_conciliacao as dataconciliacao,  ")
            .append("        vwmov.credito as credito,  ")
            .append("        vwmov.debito as debito,  ")
            .append("        vwmov.numero as numero, ")
            .append("        case vwmov.tipo_movimento  ")
            .append("           when 'ESTORNO_RECEITA_EXTRA' then 'Est. de Rec. Extra' ")
            .append("        end as tipoMovimento,  ")
            .append("        ident.numero as numeroIdentificador, ")
            .append("        ident.descricao as descricaoIdentificador, ")
            .append("        case when ident.numero is null then 1 else 2 end as ordemIdentificador ")
            .append("   from vwmovimentocontabil vwmov  ")
            .append("  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = vwmov.unidadeorganizacional_id ")
            .append(pesquisouUg ? " inner join ugestorauorganizacional ugunidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append("  inner join receitaextraestorno est on vwmov.movimento_id = est.id  ")
            .append("  inner join receitaextra recextra on est.receitaextra_id = recextra.id  ")
            .append("  inner join contaextraorcamentaria cextra on recextra.contaextraorcamentaria_id = cextra.id  ")
            .append("  inner join subconta sub on vwmov.subconta_id = sub.id  ")
            .append("  inner join contabancariaentidade cbe on sub.contabancariaentidade_id = cbe.id ")
            .append("  inner join fontederecursos font on vwmov.fontederecursos_id = font.id ")
            .append("  inner join eventocontabil eve on vwmov.eventocontabil_id = eve.id ")
            .append("   left join identificador ident on vwmov.identificador_id = ident.id  ")
            .append("  where trunc(vwmov.data_movimento) <= to_date(:DATAREFERENCIA, 'dd/mm/yyyy') ")
            .append("    and to_date(:DATAREFERENCIA, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:DATAREFERENCIA, 'dd/mm/yyyy')) ")
            .append("    and (vwmov.data_conciliacao IS NULL ")
            .append("     or trunc(vwmov.data_conciliacao) > to_date(:DATAREFERENCIA, 'DD/MM/YYYY')) ")
            .append("    and cbe.id = :CBE_ID ")
            .append("    and vwmov.tipo_movimento in ('ESTORNO_RECEITA_EXTRA')  ")
            .append("    and cextra.tipocontaextraorcamentaria <> 'DEPOSITOS_CONSIGNACOES'  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append("  union all  ")
            .append(" select lanc.historico, ")
            .append("        lanc.data as datamovimento,  ")
            .append("        lanc.dataconciliacao as dataconciliacao,  ")
            .append("        case lanc.tipooperacaoconciliacao  ")
            .append("           when 'CREDITO_NO_BANCO_NAO_CONSIDERADO_NO_RAZAO' then lanc.valor  ")
            .append("           when 'CREDITO_NO_RAZAO_NAO_CONSIDERADO_NO_BANCO' then lanc.valor  ")
            .append("           else 0  ")
            .append("        end as credito,   ")
            .append("        case lanc.tipooperacaoconciliacao  ")
            .append("           when 'DEBITO_NO_BANCO_NAO_CONSIDERADO_NO_RAZAO' then lanc.valor  ")
            .append("           when 'DEBITO_NO_RAZAO_NAO_CONSIDERADO_NO_BANCO' then lanc.valor  ")
            .append("           else 0  ")
            .append("        end as debito,   ")
            .append("        cast(lanc.numero as varchar(20)) as numero, ")
            .append("        coalesce(cast(tp.numero as   varchar(20)), '--') || ' - ' || tp.descricao as tipomovimento, ")
            .append("        ident.numero as numeroIdentificador, ")
            .append("        ident.descricao as descricaoIdentificador, ")
            .append("        case when ident.numero is null then 1 else 2 end as ordemIdentificador ")
            .append("   from lancconciliacaobancaria lanc  ")
            .append("  inner join subconta sub on lanc.subconta_id = sub.id  ")
            .append("  inner join contabancariaentidade cbe on sub.contabancariaentidade_id = cbe.id ")
            .append("  inner join tipoconciliacao tp on lanc.tipoconciliacao_id = tp.id ")
            .append("  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = lanc.unidadeorganizacional_id ")
            .append(pesquisouUg ? " inner join ugestorauorganizacional ugunidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append("   left join identificador ident on lanc.identificador_id = ident.id  ")
            .append("  where trunc(lanc.data) <= to_date(:DATAREFERENCIA, 'dd/mm/yyyy') ")
            .append("    and to_date(:DATAREFERENCIA, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, TO_DATE(:DATAREFERENCIA, 'dd/mm/yyyy')) ")
            .append("    and (lanc.dataconciliacao is null ")
            .append("     or trunc(lanc.dataconciliacao) > to_date(:DATAREFERENCIA, 'dd/mm/yyyy')) ")
            .append("    and cbe.id = :CBE_ID ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(" ) ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " where "))
            .append(" order by ordemIdentificador, numeroIdentificador, datamovimento, to_number(numero) ");
        Query q = getEm().createNativeQuery(sql.toString());
        if (pesquisouUg) {
            UtilRelatorioContabil.adicionarParametrosComparandoLocais(parametros, q, 0, 1, 4, 5);
        } else {
            UtilRelatorioContabil.adicionarParametrosComparandoLocais(parametros, q, 0, 1, 4);
        }
        q.setParameter("CBE_ID", parametrosItens.get("CBE_ID"));
        List<ConciliacaoBancariaMovimentos> retorno = new ArrayList<>();
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                ConciliacaoBancariaMovimentos item = new ConciliacaoBancariaMovimentos();
                item.setHistorico((String) obj[0]);
                item.setDataOperacao(DataUtil.getDataFormatada((Date) obj[1]));
                item.setDataConciliacao(DataUtil.getDataFormatada((Date) obj[2]));
                item.setCredito((BigDecimal) obj[3]);
                item.setDebito((BigDecimal) obj[4]);
                item.setNumeroMovimento((String) obj[5]);
                item.setTipoMovimento((String) obj[6]);
                item.setTipoOperacao((String) obj[7]);

                if ((item.getCredito().compareTo(BigDecimal.ZERO) != 0)
                    || (item.getDebito().compareTo(BigDecimal.ZERO) != 0)) {
                    retorno.add(item);
                } else if (mostrarMovimentoIgualZero) {
                    retorno.add(item);
                }
            }
        }
        return retorno;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    @Override
    public List<ConciliacaoBancariaConstante> gerarConsultaSubReportSaldoConstante(Boolean pesquisouUg, List<ParametrosRelatorios> parametros, String mesReferencia) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DISTINCT SD.NUMERO, ")
            .append("       TO_CHAR(SD.DATASALDO, 'DD/MM/YYYY') AS DATASALDO, ")
            .append("       COALESCE(SD.VALOR, 0) AS VALOR, ")
            .append("       SD.HISTORICO AS HISTORICO, ")
            .append("       pf.nome as responsavel ")
            .append("  FROM SALDOCONSTCONTABANCARIA SD ")
            .append(" INNER JOIN CONTABANCARIAENTIDADE CBE ON SD.CONTABANCARIAENTIDADE_ID = CBE.ID ")
            .append(" INNER JOIN SUBCONTA SUB ON SUB.CONTABANCARIAENTIDADE_ID = CBE.ID ")
            .append(" INNER JOIN SUBCONTAUNIORG S ON S.SUBCONTA_ID = SUB.ID ")
            .append("  left join usuariosistema usu on sd.usuariosistema_id = usu.id ")
            .append("  left join pessoafisica pf on usu.pessoafisica_id = pf.id ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = S.UNIDADEORGANIZACIONAL_ID  ")
            .append(pesquisouUg ? " INNER JOIN UGESTORAUORGANIZACIONAL UGUNIDADE ON VW.SUBORDINADA_ID = UGUNIDADE.UNIDADEORGANIZACIONAL_ID  INNER JOIN UNIDADEGESTORA UG ON UGUNIDADE.UNIDADEGESTORA_ID = UG.ID AND UG.EXERCICIO_ID = :EXERCICIO_ID " : "")
            .append(" INNER JOIN (  ")
            .append("              SELECT trunc(MAX(SALDO.DATASALDO)) AS DATA,  ")
            .append("              SALDO.CONTABANCARIAENTIDADE_ID  ")
            .append("              FROM SALDOCONSTCONTABANCARIA SALDO ")
            .append("              INNER JOIN CONTABANCARIAENTIDADE CBE ON SALDO.CONTABANCARIAENTIDADE_ID = CBE.ID ")
            .append("              INNER JOIN SUBCONTA SUB ON SUB.CONTABANCARIAENTIDADE_ID = CBE.ID ")
            .append("              WHERE trunc(SALDO.DATASALDO) <= TO_DATE(:DATAREFERENCIA, 'dd/MM/YYYY') ")
            .append("              and to_char(SALDO.DATASALDO, 'MM/YYYY') = to_char(TO_DATE(:MESREFERENCIA, 'MM/YYYY'), 'MM/YYYY')  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " AND "))
            .append("              GROUP BY SALDO.CONTABANCARIAENTIDADE_ID) MAXSALDO ON trunc(MAXSALDO.DATA) = trunc(SD.DATASALDO) ")
            .append("              AND SD.CONTABANCARIAENTIDADE_ID = MAXSALDO.CONTABANCARIAENTIDADE_ID ")
            .append(" where TO_DATE(:DATAREFERENCIA, 'dd/MM/YYYY') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:DATAREFERENCIA, 'dd/MM/YYYY')) ")
            .append("   AND s.exercicio_id = :EXERCICIO_ID ")
            .append("   AND cbe.id = :CBE_ID ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " AND "))
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " AND "));
        Query q = getEm().createNativeQuery(sql.toString());
        q.setParameter("MESREFERENCIA", mesReferencia);
        UtilRelatorioContabil.adicionarParametrosRetirandoLocais(parametros, q, 3, 4);
        List<ConciliacaoBancariaConstante> retorno = new ArrayList<>();
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                ConciliacaoBancariaConstante item = new ConciliacaoBancariaConstante();
                item.setNumero((String) obj[0]);
                item.setDataOperacao((String) obj[1]);
                item.setValor((BigDecimal) obj[2]);
                item.setHistorico((String) obj[3]);
                item.setResponsavel((String) obj[4]);
                retorno.add(item);
            }
        }
        return retorno;
    }
}
