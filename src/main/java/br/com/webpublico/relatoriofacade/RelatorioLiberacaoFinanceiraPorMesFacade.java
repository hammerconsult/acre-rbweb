package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.AcompanhamentoLiberacaoFinanc;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.TipoMovimento;
import br.com.webpublico.util.UtilRelatorioContabil;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mateus on 12/07/17.
 */
@Stateless
public class RelatorioLiberacaoFinanceiraPorMesFacade extends RelatorioAcompanhamentoLiberacaoFinanceiraSuperFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    public List<AcompanhamentoLiberacaoFinanc> recuperarRelatorio(List<ParametrosRelatorios> parametros, Integer mesSelecionado, TipoMovimento tipoMovimento, Boolean exibirContasBancarias) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select unidade, ")
            .append("        coalesce(sum(orcamento), 0) as orcamento, ")
            .append("        coalesce(sum(dotacaoatual), 0) as dotacaoatual, ")
            .append("        coalesce(sum(restoPago), 0) as restoPago, ")
            .append("        coalesce(sum(liberado), 0) as liberado, ")
            .append("        unidade_id ")
            .append(" from ( ")
            .append(" select COALESCE(sum(A.DOTACAO), 0) as orcamento, ")
            .append("        (coalesce(sum(A.dotacao),0) + coalesce(sum(A.suplementado),0) - coalesce(sum(A.reduzido),0)) as dotacaoatual,  ")
            .append("        0 as restoPago, ")
            .append("        0 as liberado, ")
            .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
            .append("        vw.subordinada_id as unidade_id ");
        sql.append(buscarQuerySaldoFonteDespesaOrc());
        sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
            .append("   and C.EXERCICIO_ID = :EXERCICIO_ID ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "));
        sql.append(" group by vw.codigo, vw.descricao, vw.subordinada_id ")
            .append(buscarLiberacoesAndTransferenciasComUnidade(parametros, tipoMovimento))
            .append(" ) ")
            .append(" group by unidade, unidade_id ")
            .append(" order by unidade ");
        Query q = em.createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        List<Object[]> resultado = q.getResultList();
        List<AcompanhamentoLiberacaoFinanc> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                AcompanhamentoLiberacaoFinanc item = new AcompanhamentoLiberacaoFinanc();
                item.setDescricao((String) obj[0]);
                item.setOrcamento((BigDecimal) obj[1]);
                item.setOrcadoAtual((BigDecimal) obj[2]);
                item.setPagoRestoAPagar((BigDecimal) obj[3]);
                item.setSaldoLiberado((BigDecimal) obj[4]);
                item.setSaldoALiberar(item.getOrcamento().subtract(item.getSaldoLiberado()));
                item.setNivel(1);
                retorno.add(item);
                if (exibirContasBancarias) {
                    retorno.addAll(recuperarContasBancarias(parametros, tipoMovimento, ((BigDecimal) obj[5]).longValue()));
                }
            }
        }
        return retorno;
    }

    public List<AcompanhamentoLiberacaoFinanc> recuperarContasBancarias(List<ParametrosRelatorios> parametros, TipoMovimento tipoMovimento, Long unidadeId) {
        StringBuilder sql = buscarLiberacoesAndTransferenciasComContaBancaria(parametros, tipoMovimento);
        Query q = em.createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        q.setParameter("unidade_id", unidadeId);
        List<Object[]> resultado = q.getResultList();
        List<AcompanhamentoLiberacaoFinanc> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                AcompanhamentoLiberacaoFinanc item = new AcompanhamentoLiberacaoFinanc();
                item.setDescricao((String) obj[0]);
                item.setOrcamento(null);
                item.setOrcadoAtual(null);
                item.setSaldoLiberado((BigDecimal) obj[1]);
                item.setPagoRestoAPagar((BigDecimal) obj[2]);
                item.setSaldoALiberar(null);
                item.setNivel(2);
                retorno.add(item);
            }
        }
        return retorno;
    }

    public StringBuilder buscarLiberacoesAndTransferenciasComUnidade(List<ParametrosRelatorios> parametros, TipoMovimento tipoMovimento) {
        StringBuilder sql = new StringBuilder();
        if (tipoMovimento == null || TipoMovimento.LIBERACAO_FINANCEIRA.equals(tipoMovimento)) {
            sql.append(" union all ")
                // LIBERAÇÃO RETIRADA
                .append(" select 0 as orcamento, ")
                .append("        0 as dotacaoatual, ")
                .append("        0 as restoPago, ")
                .append("        coalesce(sum(liberacao.valor), 0) * - 1 as liberado, ")
                .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append("        vw.subordinada_id as unidade_id ");
            sql.append(buscarFromLiberacaoRetirada());
            sql.append("  where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append(buscarAndConfiguracaoRetirada())
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and cast(liberacao.dataliberacao as Date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append(" group by vw.codigo, vw.descricao, vw.subordinada_id ")
                .append(" ")
                .append(" union all ")
                // ESTORNO LIBERAÇÃO RECEBIDA
                .append(" select 0 as orcamento, ")
                .append("        0 as dotacaoatual, ")
                .append("        0 as restoPago, ")
                .append("        coalesce(sum(EST.VALOR), 0) * - 1 as liberado, ")
                .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append("        vw.subordinada_id as unidade_id ");
            sql.append(buscarFromEstornoLiberacaoRecebida());
            sql.append(" where cast(est.DATAESTORNO as Date)  between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append("   and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append(" group by vw.codigo, vw.descricao, vw.subordinada_id ")
                .append(" union all ")
                // ESTORNO LIBERAÇÃO RETIRADA
                .append(" select 0 as orcamento, ")
                .append("        0 as dotacaoatual, ")
                .append("        0 as restoPago, ")
                .append("        coalesce(sum(EST.VALOR), 0) as liberado, ")
                .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append("        vw.subordinada_id as unidade_id ");
            sql.append(buscarFromEstornoLiberacaoRetirada());
            sql.append(" where cast(est.DATAESTORNO as Date)  between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append("   and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append(" group by vw.codigo, vw.descricao, vw.subordinada_id ")
                .append(" union all ")
                // LIBERAÇÃO RECEBIDA
                .append(" select 0 as orcamento, ")
                .append("        0 as dotacaoatual, ")
                .append("        0 as restoPago, ")
                .append("        coalesce(sum(liberacao.valor), 0) as liberado, ")
                .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append("        vw.subordinada_id as unidade_id ");
            sql.append(buscarFromLiberacaoRecebida());
            sql.append(" where cast(liberacao.dataliberacao as Date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append("   and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append(" group by vw.codigo, vw.descricao, vw.subordinada_id ");
        }
        if (tipoMovimento == null || TipoMovimento.TRANSFERENCIA_FINANCEIRA.equals(tipoMovimento)) {
            sql.append(" union all ")
                // TRANSFERÊNCIA CONCEDIDA
                .append(" select 0 as orcamento, ")
                .append("        0 as dotacaoatual, ")
                .append("        0 as restoPago, ")
                .append("        coalesce(sum(transferencia.valor), 0) * -1 as liberado, ")
                .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append("        vw.subordinada_id as unidade_id ");
            sql.append(buscarFromTransferenciaConcedida());
            sql.append("  WHERE cast(transferencia.DATATRANSFERENCIA AS DATE) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append("   AND to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   AND ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append(" group by vw.codigo, vw.descricao, vw.subordinada_id ")
                .append(" union all ")
                // ESTORNO TRANSFERÊNCIA RECEBIDA
                .append(" select 0 as orcamento, ")
                .append("        0 as dotacaoatual, ")
                .append("        0 as restoPago, ")
                .append("        coalesce(sum(estorno.valor), 0)  * -1 as liberado, ")
                .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append("        vw.subordinada_id as unidade_id ");
            sql.append(buscarFromEstornoTransferenciaRecebida());
            sql.append(" WHERE cast(estorno.DATAESTORNO AS DATE) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:MESFINAL, 'dd/mm/yyyy')")
                .append(" AND to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append(" AND ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append(" group by vw.codigo, vw.descricao, vw.subordinada_id ")
                .append(" union all ")
                // ESTORNO TRANSFERÊNCIA CONCEDIDA
                .append(" select 0 as orcamento, ")
                .append("        0 as dotacaoatual, ")
                .append("        0 as restoPago, ")
                .append("        coalesce(sum(estorno.valor), 0) as liberado, ")
                .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append("        vw.subordinada_id as unidade_id ");
            sql.append(buscarFromEstornoTransferenciaConcedida());
            sql.append(" WHERE cast(estorno.DATAESTORNO AS DATE) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:MESFINAL, 'dd/mm/yyyy')")
                .append(" AND to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append(" AND ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append(" group by vw.codigo, vw.descricao, vw.subordinada_id ")
                .append(" union all ")
                // TRANSFERÊNCIA RECEBIDA
                .append(" select 0 as orcamento, ")
                .append("        0 as dotacaoatual, ")
                .append("        0 as restoPago, ")
                .append("        coalesce(sum(transferencia.valor), 0) as liberado, ")
                .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append("        vw.subordinada_id as unidade_id ");
            sql.append(buscarFromTransferenciaRecebida());
            sql.append(" WHERE cast(transferencia.DATATRANSFERENCIA AS DATE) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append(" AND to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append(" AND ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append(" group by vw.codigo, vw.descricao, vw.subordinada_id ");
        }
        sql.append(" union all ")
            .append(" select 0 as orcamento, ")
            .append("        0 as dotacaoatual, ")
            .append("        coalesce(pag.valor,0) as restoPago, ")
            .append("        0 as liberado, ")
            .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
            .append("        vw.subordinada_id as unidade_id ");
        sql.append(buscarFromPagamento());
        sql.append("  where cast(pag.datapagamento as date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
            .append("    and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
            .append("    and EMP.CATEGORIAORCAMENTARIA = 'RESTO' ")
            .append("    and ex.ID = :EXERCICIO_ID ")
            .append("    and pag.status <> 'ABERTO' ")
            .append(buscarAndConfiguracaoRetirada())
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
            .append("  union all ")
            .append(" select 0 as orcamento, ")
            .append("        0 as dotacaoatual, ")
            .append("        coalesce(est.valor,0) * -1 as restoPago, ")
            .append("        0 as liberado, ")
            .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
            .append("        vw.subordinada_id as unidade_id ");
        sql.append(buscarFromPagamentoEstorno());
        sql.append("  where cast(est.dataestorno as Date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
            .append("    and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
            .append("    and EMP.CATEGORIAORCAMENTARIA = 'RESTO' ")
            .append("    and ex.ID = :EXERCICIO_ID  ")
            .append("    and pag.status <> 'ABERTO' ")
            .append(buscarAndConfiguracaoRetirada())
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "));
        return sql;
    }

    private StringBuilder buscarFromPagamentoEstorno() {
        StringBuilder sql = new StringBuilder();
        sql.append("   from pagamentoestorno est ")
            .append("  inner join pagamento pag on est.pagamento_id= pag.id ")
            .append("  inner join liquidacao liq on pag.liquidacao_id = liq.id ")
            .append("  inner join empenho emp on liq.empenho_id = emp.id ")
            .append("  INNER JOIN DESPESAORC desp ON emp.despesaorc_id = desp.ID ")
            .append("  INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append("  INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append("  INNER JOIN exercicio ex ON emp.exercicio_id = ex.id ")
            .append("  inner join fontedespesaorc fontedesp on emp.fontedespesaorc_id = fontedesp.id  ")
            .append("  inner join provisaoppafonte ppf on fontedesp.provisaoppafonte_id = ppf.id  ")
            .append("  inner join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id  ")
            .append("  inner join fontederecursos fr on fr.id = cd.fontederecursos_id ")
            .append("  inner join subconta retirada on pag.subconta_id = retirada.id ")
            .append("  inner JOIN UNIDADEACOMPAFINANCEIRO conf on pag.UNIDADEORGANIZACIONAL_ID = conf.UNIDADEORGANIZACIONAL_ID ")
            .append("  inner JOIN CONTAACOMPAFINANCEIRO CAF ON CAF.UNIDADE_ID = CONF.ID ")
            .append("  inner join contabancariaentidade cbe on retirada.contabancariaentidade_id = cbe.id  ")
            .append("  inner join agencia ag on cbe.agencia_id = ag.id ")
            .append("  inner join banco b on ag.banco_id = b.id ")
            .append(buscarJoinVwUnidade("pag.unidadeorganizacional_id"));
        return sql;
    }

    private StringBuilder buscarFromPagamento() {
        StringBuilder sql = new StringBuilder();
        sql.append("   from pagamento pag ")
            .append("  inner join liquidacao liq on pag.liquidacao_id = liq.id ")
            .append("  inner join empenho emp on liq.empenho_id = emp.id ")
            .append("  INNER JOIN DESPESAORC desp ON emp.despesaorc_id = desp.ID ")
            .append("  INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append("  INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append("  INNER JOIN exercicio ex ON emp.exercicio_id = ex.id ")
            .append("  inner join fontedespesaorc fontedesp on emp.fontedespesaorc_id = fontedesp.id  ")
            .append("  inner join provisaoppafonte ppf on fontedesp.provisaoppafonte_id = ppf.id  ")
            .append("  inner join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id  ")
            .append("  inner join fontederecursos fr on fr.id = cd.fontederecursos_id ")
            .append("  inner join subconta retirada on pag.subconta_id = retirada.id ")
            .append("  inner JOIN UNIDADEACOMPAFINANCEIRO conf on pag.UNIDADEORGANIZACIONAL_ID = conf.UNIDADEORGANIZACIONAL_ID ")
            .append("  inner JOIN CONTAACOMPAFINANCEIRO CAF ON CAF.UNIDADE_ID = CONF.ID ")
            .append("  inner join contabancariaentidade cbe on retirada.contabancariaentidade_id = cbe.id  ")
            .append("  inner join agencia ag on cbe.agencia_id = ag.id ")
            .append("  inner join banco b on ag.banco_id = b.id ")
            .append(buscarJoinVwUnidade("pag.unidadeorganizacional_id"));
        return sql;
    }

    public StringBuilder buscarLiberacoesAndTransferenciasComContaBancaria(List<ParametrosRelatorios> parametros, TipoMovimento tipoMovimento) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select '      ' || numero_banco || ' / ' || numero_agencia || ' / ' || numero_conta as contaBancaria, ")
            .append("       coalesce(sum(liberado), 0) as liberado, ")
            .append("       coalesce(sum(restoPago), 0) as restoPago ")
            .append(" from ( ");
        if (tipoMovimento == null || TipoMovimento.LIBERACAO_FINANCEIRA.equals(tipoMovimento)) {
            // LIBERAÇÃO RETIRADA
            sql.append(" select coalesce(liberacao.valor, 0) as liberado, ")
                .append("  0 as restoPago, ")
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromLiberacaoRetirada());
            sql.append(buscarJoinContaBancariaRetirada());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append(buscarAndConfiguracaoRetirada())
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and vw.subordinada_id = :unidade_id ")
                .append("   and cast(liberacao.dataliberacao as Date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append(" ")
                .append(" union all ")
                // ESTORNO LIBERAÇÃO RECEBIDA
                .append(" select coalesce(EST.VALOR, 0) * - 1 as liberado, ")
                .append("  0 as restoPago, ")
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromEstornoLiberacaoRecebida());
            sql.append(buscarJoinContaBancariaRecebida());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and cast(est.DATAESTORNO as Date)  between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append(" union all ")
                // ESTORNO LIBERAÇÃO RETIRADA
                .append(" select coalesce(EST.VALOR, 0) as liberado, ")
                .append("  0 as restoPago, ")
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromEstornoLiberacaoRetirada());
            sql.append(buscarJoinContaBancariaRetirada());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and cast(est.DATAESTORNO as Date)  between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append(" union all ")
                // LIBERAÇÃO RECEBIDA
                .append(" select coalesce(liberacao.valor, 0) as liberado, ")
                .append("  0 as restoPago, ")
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromLiberacaoRecebida());
            sql.append(buscarJoinContaBancariaRecebida());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and cast(liberacao.dataliberacao as Date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ");
        }
        sql.append(tipoMovimento == null ? " union all " : "");
        if (tipoMovimento == null || TipoMovimento.TRANSFERENCIA_FINANCEIRA.equals(tipoMovimento)) {
            // TRANSFERÊNCIA CONCEDIDA
            sql.append(" select coalesce(transferencia.valor, 0) * -1 as liberado, ")
                .append("  0 as restoPago, ")
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromTransferenciaConcedida());
            sql.append(buscarJoinContaBancariaRetirada());
            sql.append(" WHERE to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   AND ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and cast(transferencia.DATATRANSFERENCIA AS DATE) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append(" union all ")
                // ESTORNO TRANSFERÊNCIA RECEBIDA
                .append(" select coalesce(transferencia.valor, 0) * -1 as liberado, ")
                .append("  0 as restoPago, ")
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromEstornoTransferenciaRecebida());
            sql.append(buscarJoinContaBancariaRecebida());
            sql.append(" WHERE to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   AND ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and cast(estorno.DATAESTORNO AS DATE) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:MESFINAL, 'dd/mm/yyyy')")
                .append(" union all ")
                // ESTORNO TRANSFERÊNCIA CONCEDIDA
                .append(" select coalesce(transferencia.valor, 0) as liberado, ")
                .append("  0 as restoPago, ")
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromEstornoTransferenciaConcedida());
            sql.append(buscarJoinContaBancariaRetirada());
            sql.append(" WHERE to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   AND ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and cast(estorno.DATAESTORNO AS DATE) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:MESFINAL, 'dd/mm/yyyy')")
                .append(" union all ")
                // TRANSFERÊNCIA RECEBIDA
                .append(" select coalesce(transferencia.valor, 0) as liberado, ")
                .append("  0 as restoPago, ")
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromTransferenciaRecebida());
            sql.append(buscarJoinContaBancariaRecebida());
            sql.append(" WHERE to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
                .append("   AND ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and cast(transferencia.DATATRANSFERENCIA AS DATE) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:MESFINAL, 'dd/mm/yyyy') ");
        }
        sql.append(" union all ")
            .append(" select 0 as liberado, ")
            .append("        coalesce(pag.valor,0) as restoPago, ")
            .append("        b.numerobanco as numero_banco, ")
            .append("        ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
            .append("        cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
        sql.append(buscarFromPagamento());
        sql.append("  where cast(pag.datapagamento as date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
            .append("    and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
            .append("    and EMP.CATEGORIAORCAMENTARIA = 'RESTO' ")
            .append("    and ex.ID = :EXERCICIO_ID ")
            .append("   and vw.subordinada_id = :unidade_id ")
            .append("    and pag.status <> 'ABERTO' ")
            .append(buscarAndConfiguracaoRetirada())
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
            .append("  union all ")
            .append(" select 0 as liberado, ")
            .append("        coalesce(est.valor,0) * -1 as restoPago, ")
            .append("        b.numerobanco as numero_banco, ")
            .append("        ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
            .append("        cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
        sql.append(buscarFromPagamentoEstorno());
        sql.append("  where cast(est.dataestorno as Date) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
            .append("    and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL, 'dd/mm/yyyy')) ")
            .append("    and EMP.CATEGORIAORCAMENTARIA = 'RESTO' ")
            .append("    and ex.ID = :EXERCICIO_ID  ")
            .append("    and pag.status <> 'ABERTO' ")
            .append("   and vw.subordinada_id = :unidade_id ")
            .append(buscarAndConfiguracaoRetirada())
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "));

        sql.append(" ) ")
            .append(" group by numero_banco, numero_agencia, numero_conta ")
            .append(" order by numero_banco, numero_agencia, numero_conta ");
        return sql;
    }
}
