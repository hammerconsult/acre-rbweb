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
 * Created by Mateus on 07/07/2015.
 */
@Stateless
public class RelatorioAcompanhamentoLiberacaoFinanceiraFacade extends RelatorioAcompanhamentoLiberacaoFinanceiraSuperFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    public List<AcompanhamentoLiberacaoFinanc> recuperarRelatorio(List<ParametrosRelatorios> parametros, Integer mesSelecionado, TipoMovimento tipoMovimento, Boolean exibirContasBancarias) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select unidade, ")
            .append("        coalesce(sum(orcamento), 0) as orcamento, ")
            .append("        coalesce(sum(liberado_1mes), 0) as liberado_1mes, ")
            .append("        coalesce(sum(liberado_2mes), 0) as liberado_2mes, ")
            .append("        coalesce(sum(liberado_3mes), 0) as liberado_3mes, ")
            .append("        coalesce(sum(liberado_4mes), 0) as liberado_4mes, ")
            .append("        coalesce(sum(liberado_5mes), 0) as liberado_5mes, ")
            .append("        coalesce(sum(liberado_6mes), 0) as liberado_6mes, ")
            .append("        coalesce(sum(liberado_7mes), 0) as liberado_7mes, ")
            .append("        coalesce(sum(liberado_8mes), 0) as liberado_8mes, ")
            .append("        coalesce(sum(liberado_9mes), 0) as liberado_9mes, ")
            .append("        coalesce(sum(liberado_10mes), 0) as liberado_10mes, ")
            .append("        coalesce(sum(liberado_11mes), 0) as liberado_11mes, ")
            .append("        coalesce(sum(liberado_12mes), 0) as liberado_12mes, ")
            .append("        unidade_id ")
            .append(" from ( ")
            .append(" select COALESCE(sum(A.DOTACAO), 0) as orcamento, ")
            .append("        0 as liberado_1mes, ")
            .append("        0 as liberado_2mes, ")
            .append("        0 as liberado_3mes, ")
            .append("        0 as liberado_4mes, ")
            .append("        0 as liberado_5mes, ")
            .append("        0 as liberado_6mes, ")
            .append("        0 as liberado_7mes, ")
            .append("        0 as liberado_8mes, ")
            .append("        0 as liberado_9mes, ")
            .append("        0 as liberado_10mes, ")
            .append("        0 as liberado_11mes, ")
            .append("        0 as liberado_12mes, ")
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
                item.setLiberado1mes((BigDecimal) obj[2]);
                item.setLiberado2mes(atribuirValor(mesSelecionado, 2, (BigDecimal) obj[3]));
                item.setLiberado3mes(atribuirValor(mesSelecionado, 3, (BigDecimal) obj[4]));
                item.setLiberado4mes(atribuirValor(mesSelecionado, 4, (BigDecimal) obj[5]));
                item.setLiberado5mes(atribuirValor(mesSelecionado, 5, (BigDecimal) obj[6]));
                item.setLiberado6mes(atribuirValor(mesSelecionado, 6, (BigDecimal) obj[7]));
                item.setLiberado7mes(atribuirValor(mesSelecionado, 7, (BigDecimal) obj[8]));
                item.setLiberado8mes(atribuirValor(mesSelecionado, 8, (BigDecimal) obj[9]));
                item.setLiberado9mes(atribuirValor(mesSelecionado, 9, (BigDecimal) obj[10]));
                item.setLiberado10mes(atribuirValor(mesSelecionado, 10, (BigDecimal) obj[11]));
                item.setLiberado11mes(atribuirValor(mesSelecionado, 11, (BigDecimal) obj[12]));
                item.setLiberado12mes(atribuirValor(mesSelecionado, 12, (BigDecimal) obj[13]));
                item.setSaldoLiberado(somarTodosOsMeses(item));
                item.setSaldoALiberar(item.getOrcamento().subtract(item.getSaldoLiberado()));
                item.setNivel(1);
                retorno.add(item);
                if (exibirContasBancarias) {
                    retorno.addAll(recuperarContasBancarias(parametros, mesSelecionado, tipoMovimento, ((BigDecimal) obj[14]).longValue()));
                }
            }
        }
        return retorno;
    }

    private List<AcompanhamentoLiberacaoFinanc> recuperarContasBancarias(List<ParametrosRelatorios> parametros, Integer mesSelecionado, TipoMovimento tipoMovimento, Long unidadeId) {
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
                item.setLiberado1mes((BigDecimal) obj[1]);
                item.setLiberado2mes(atribuirValor(mesSelecionado, 2, (BigDecimal) obj[2]));
                item.setLiberado3mes(atribuirValor(mesSelecionado, 3, (BigDecimal) obj[3]));
                item.setLiberado4mes(atribuirValor(mesSelecionado, 4, (BigDecimal) obj[4]));
                item.setLiberado5mes(atribuirValor(mesSelecionado, 5, (BigDecimal) obj[5]));
                item.setLiberado6mes(atribuirValor(mesSelecionado, 6, (BigDecimal) obj[6]));
                item.setLiberado7mes(atribuirValor(mesSelecionado, 7, (BigDecimal) obj[7]));
                item.setLiberado8mes(atribuirValor(mesSelecionado, 8, (BigDecimal) obj[8]));
                item.setLiberado9mes(atribuirValor(mesSelecionado, 9, (BigDecimal) obj[9]));
                item.setLiberado10mes(atribuirValor(mesSelecionado, 10, (BigDecimal) obj[10]));
                item.setLiberado11mes(atribuirValor(mesSelecionado, 11, (BigDecimal) obj[11]));
                item.setLiberado12mes(atribuirValor(mesSelecionado, 12, (BigDecimal) obj[12]));
                item.setSaldoLiberado(somarTodosOsMeses(item));
                item.setSaldoALiberar(null);
                item.setNivel(2);
                retorno.add(item);
            }
        }
        return retorno;
    }

    private BigDecimal somarTodosOsMeses(AcompanhamentoLiberacaoFinanc item) {
        return item.getLiberado1mes()
            .add(item.getLiberado2mes())
            .add(item.getLiberado3mes())
            .add(item.getLiberado4mes())
            .add(item.getLiberado5mes())
            .add(item.getLiberado6mes())
            .add(item.getLiberado7mes())
            .add(item.getLiberado8mes())
            .add(item.getLiberado9mes())
            .add(item.getLiberado10mes())
            .add(item.getLiberado11mes())
            .add(item.getLiberado12mes());
    }

    private StringBuilder buscarLiberacoesAndTransferenciasComUnidade(List<ParametrosRelatorios> parametros, TipoMovimento tipoMovimento) {
        StringBuilder sql = new StringBuilder();
        if (tipoMovimento == null || TipoMovimento.LIBERACAO_FINANCEIRA.equals(tipoMovimento)) {
            sql.append(" union all ")
                // LIBERAÇÃO RETIRADA
                .append(" select 0 as orcamento, ")
                .append(buscarValorLiberacoesComDozeMeses("liberacao.dataliberacao", " coalesce(liberacao.valor, 0) * - 1 "))
                .append(" vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append(" vw.subordinada_id as unidade_id ");
            sql.append(buscarFromLiberacaoRetirada());
            sql.append("  where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append(buscarAndConfiguracaoRetirada())
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and trunc(liberacao.dataliberacao) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append(" ")
                .append(" union all ")
                // ESTORNO LIBERAÇÃO RECEBIDA
                .append(" select 0 as orcamento, ")
                .append(buscarValorLiberacoesComDozeMeses("est.DATAESTORNO", "coalesce(EST.VALOR, 0) * - 1 "))
                .append(" vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append(" vw.subordinada_id as unidade_id ");
            sql.append(buscarFromEstornoLiberacaoRecebida());
            sql.append(" where trunc(est.DATAESTORNO)  BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append("   and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append(" union all ")
                // ESTORNO LIBERAÇÃO RETIRADA
                .append(" select 0 as orcamento, ")
                .append(buscarValorLiberacoesComDozeMeses("est.DATAESTORNO", "coalesce(EST.VALOR, 0) "))
                .append(" vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append(" vw.subordinada_id as unidade_id ");
            sql.append(buscarFromEstornoLiberacaoRetirada());
            sql.append(" where trunc(est.DATAESTORNO)  BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append("   and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))

                .append(" union all ")
                // LIBERAÇÃO RECEBIDA
                .append(" select 0 as orcamento, ")
                .append(buscarValorLiberacoesComDozeMeses("liberacao.dataliberacao", "coalesce(liberacao.valor, 0) "))
                .append(" vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append(" vw.subordinada_id as unidade_id ");
            sql.append(buscarFromLiberacaoRecebida());
            sql.append(" where trunc(liberacao.dataliberacao) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append("   and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "));

        }
        if (tipoMovimento == null || TipoMovimento.TRANSFERENCIA_FINANCEIRA.equals(tipoMovimento)) {
            sql.append(" union all ")
                // TRANSFERÊNCIA CONCEDIDA
                .append(" select 0 as orcamento, ")
                .append(buscarValorLiberacoesComDozeMeses("transferencia.DATATRANSFERENCIA", "coalesce(transferencia.valor, 0) * -1 "))
                .append(" vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append(" vw.subordinada_id as unidade_id ");
            sql.append(buscarFromTransferenciaConcedida());
            sql.append("  WHERE trunc(transferencia.DATATRANSFERENCIA) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append("   and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   AND ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))

                .append(" union all ")
                // ESTORNO TRANSFERÊNCIA RECEBIDA
                .append(" select 0 as orcamento, ")
                .append(buscarValorLiberacoesComDozeMeses("estorno.DATAESTORNO", "coalesce(estorno.valor, 0)  * -1"))
                .append(" vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append(" vw.subordinada_id as unidade_id ");
            sql.append(buscarFromEstornoTransferenciaRecebida());
            sql.append(" WHERE trunc(estorno.DATAESTORNO) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy')")
                .append("   and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append(" AND ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))

                .append(" union all ")
                // ESTORNO TRANSFERÊNCIA CONCEDIDA
                .append(" select 0 as orcamento, ")
                .append(buscarValorLiberacoesComDozeMeses("estorno.DATAESTORNO", "coalesce(estorno.valor, 0) "))
                .append(" vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append(" vw.subordinada_id as unidade_id ");
            sql.append(buscarFromEstornoTransferenciaConcedida());
            sql.append(" WHERE trunc(estorno.DATAESTORNO) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy')")
                .append("   and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append(" AND ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))

                .append(" union all ")
                // TRANSFERÊNCIA RECEBIDA
                .append(" select 0 as orcamento, ")
                .append(buscarValorLiberacoesComDozeMeses("transferencia.DATATRANSFERENCIA", "coalesce(transferencia.valor, 0) "))
                .append(" vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append(" vw.subordinada_id as unidade_id ");
            sql.append(buscarFromTransferenciaRecebida());
            sql.append(" WHERE trunc(transferencia.DATATRANSFERENCIA) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy')  ")
                .append("   and to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append(" AND ex.ID = :EXERCICIO_ID ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "));
        }
        return sql;
    }

    private StringBuilder buscarLiberacoesAndTransferenciasComContaBancaria(List<ParametrosRelatorios> parametros, TipoMovimento tipoMovimento) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select '      ' || numero_banco || ' / ' || numero_agencia || ' / ' || numero_conta as contaBancaria, ")
            .append("       coalesce(sum(liberado_1mes), 0) as liberado_1mes, ")
            .append("       coalesce(sum(liberado_2mes), 0) as liberado_2mes, ")
            .append("       coalesce(sum(liberado_3mes), 0) as liberado_3mes, ")
            .append("       coalesce(sum(liberado_4mes), 0) as liberado_4mes, ")
            .append("       coalesce(sum(liberado_5mes), 0) as liberado_5mes, ")
            .append("       coalesce(sum(liberado_6mes), 0) as liberado_6mes, ")
            .append("       coalesce(sum(liberado_7mes), 0) as liberado_7mes, ")
            .append("       coalesce(sum(liberado_8mes), 0) as liberado_8mes, ")
            .append("       coalesce(sum(liberado_9mes), 0) as liberado_9mes, ")
            .append("       coalesce(sum(liberado_10mes), 0) as liberado_10mes, ")
            .append("       coalesce(sum(liberado_11mes), 0) as liberado_11mes, ")
            .append("       coalesce(sum(liberado_12mes), 0) as liberado_12mes ")
            .append(" from ( ");
        if (tipoMovimento == null || TipoMovimento.LIBERACAO_FINANCEIRA.equals(tipoMovimento)) {
            // LIBERAÇÃO RETIRADA
            sql.append(" select  ")
                .append(buscarValorLiberacoesComDozeMeses("liberacao.dataliberacao", "coalesce(liberacao.valor, 0)"))
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromLiberacaoRetirada());
            sql.append(buscarJoinContaBancariaRetirada());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append(buscarAndConfiguracaoRetirada())
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and vw.subordinada_id = :unidade_id ")
                .append("   and trunc(liberacao.dataliberacao) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")
                .append(" ")
                .append(" union all ")
                // ESTORNO LIBERAÇÃO RECEBIDA
                .append(" select ")
                .append(buscarValorLiberacoesComDozeMeses("est.DATAESTORNO", "coalesce(EST.VALOR, 0) * - 1"))
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromEstornoLiberacaoRecebida());
            sql.append(buscarJoinContaBancariaRecebida());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and trunc(est.DATAESTORNO)  BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")

                .append(" union all ")
                // ESTORNO LIBERAÇÃO RETIRADA
                .append(" select ")
                .append(buscarValorLiberacoesComDozeMeses("est.DATAESTORNO", "coalesce(EST.VALOR, 0) "))
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromEstornoLiberacaoRetirada());
            sql.append(buscarJoinContaBancariaRetirada());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and trunc(est.DATAESTORNO)  BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")

                .append(" union all ")
                // LIBERAÇÃO RECEBIDA
                .append(" select  ")
                .append(buscarValorLiberacoesComDozeMeses("liberacao.dataliberacao", "coalesce(liberacao.valor, 0) "))
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromLiberacaoRecebida());
            sql.append(buscarJoinContaBancariaRecebida());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   and ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and trunc(liberacao.dataliberacao) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ");
        }
        sql.append(tipoMovimento == null ? " union all " : "");
        if (tipoMovimento == null || TipoMovimento.TRANSFERENCIA_FINANCEIRA.equals(tipoMovimento)) {
            // TRANSFERÊNCIA CONCEDIDA
            sql.append(" select ")
                .append(buscarValorLiberacoesComDozeMeses("transferencia.DATATRANSFERENCIA", "coalesce(transferencia.valor, 0) * -1 "))
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromTransferenciaConcedida());
            sql.append(buscarJoinContaBancariaRetirada());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   AND ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and trunc(transferencia.DATATRANSFERENCIA) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy') ")

                .append(" union all ")
                // ESTORNO TRANSFERÊNCIA RECEBIDA
                .append(" select ")
                .append(buscarValorLiberacoesComDozeMeses("estorno.DATAESTORNO", "coalesce(transferencia.valor, 0) * -1 "))
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromEstornoTransferenciaRecebida());
            sql.append(buscarJoinContaBancariaRecebida());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   AND ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and trunc(estorno.DATAESTORNO) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy')")

                .append(" union all ")
                // ESTORNO TRANSFERÊNCIA CONCEDIDA
                .append(" select ")
                .append(buscarValorLiberacoesComDozeMeses("estorno.DATAESTORNO", "coalesce(transferencia.valor, 0) "))
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromEstornoTransferenciaConcedida());
            sql.append(buscarJoinContaBancariaRetirada());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   AND ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRetirada())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "))
                .append("   and trunc(estorno.DATAESTORNO) BETWEEN to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:MESFINAL, 'dd/mm/yyyy')")
                .append(" union all ")
                // TRANSFERÊNCIA RECEBIDA
                .append(" select ")
                .append(buscarValorLiberacoesComDozeMeses("transferencia.DATATRANSFERENCIA", "coalesce(transferencia.valor, 0) "))
                .append("  b.numerobanco as numero_banco, ")
                .append("  ag.numeroagencia || '-' || ag.digitoverificador as numero_agencia, ")
                .append("  cbe.numeroconta || '-' || cbe.digitoverificador as numero_conta ");
            sql.append(buscarFromTransferenciaRecebida());
            sql.append(buscarJoinContaBancariaRecebida());
            sql.append(" where to_date(:MESFINAL, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'dd/mm/yyyy')) ")
                .append("   AND ex.ID = :EXERCICIO_ID ")
                .append("   and vw.subordinada_id = :unidade_id ")
                .append(buscarAndConfiguracaoRecebida())
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
                .append(UtilRelatorioContabil.concatenarParametros(parametros, 4, " and "));
        }
        sql.append(" ) ")
            .append(" group by numero_banco, numero_agencia, numero_conta ")
            .append(" order by numero_banco, numero_agencia, numero_conta ");
        return sql;
    }

    private String buscarValorLiberacoesComDozeMeses(String campoData, String campoValor) {
        StringBuilder sql = new StringBuilder();
        sql.append(" case when extract(month from ").append(campoData).append(") = 1 then ").append(campoValor).append(" else 0 end as liberado_1mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 2 then ").append(campoValor).append(" else 0 end as liberado_2mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 3 then ").append(campoValor).append(" else 0 end as liberado_3mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 4 then ").append(campoValor).append(" else 0 end as liberado_4mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 5 then ").append(campoValor).append(" else 0 end as liberado_5mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 6 then ").append(campoValor).append(" else 0 end as liberado_6mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 7 then ").append(campoValor).append(" else 0 end as liberado_7mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 8 then ").append(campoValor).append(" else 0 end as liberado_8mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 9 then ").append(campoValor).append(" else 0 end as liberado_9mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 10 then ").append(campoValor).append(" else 0 end as liberado_10mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 11 then ").append(campoValor).append(" else 0 end as liberado_11mes, ");
        sql.append(" case when extract(month from ").append(campoData).append(") = 12 then ").append(campoValor).append(" else 0 end as liberado_12mes, ");
        return sql.toString();
    }

    private BigDecimal atribuirValor(Integer mesSelecionado, Integer mesCampo, BigDecimal valor) {
        if (mesSelecionado >= mesCampo) {
            return valor;
        } else {
            return BigDecimal.ZERO;
        }
    }
}
