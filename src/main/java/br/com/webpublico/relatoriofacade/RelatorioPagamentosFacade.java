package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.FiltroRelatorioPagamento;
import br.com.webpublico.entidadesauxiliares.PagamentoItem;
import br.com.webpublico.negocios.PagamentoFacade;
import br.com.webpublico.util.UtilRelatorioContabil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 28/09/17.
 */
@Stateless
public class RelatorioPagamentosFacade extends RelatorioContabilSuperFacade {

    @EJB
    private PagamentoFacade pagamentoFacade;

    public List<PagamentoItem> buscarDados(FiltroRelatorioPagamento filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select * ")
            .append(" from ( ")
            .append(" select l.numero as liq_numero, ")
            .append("       emp.numero as emp_numero, ")
            .append("       pgto.numeropagamento as pgto_numero, ")
            .append("       COALESCE(CAST(pgto.datapagamento AS DATE), CAST(pgto.previstopara AS DATE)) AS datapagamento, ")
            .append("       coalesce(pgto.valorfinal,0) as pgto_valor, ")
            .append("       coalesce(pgto.valor,0) AS total, ")
            .append("       coalesce(ret.valor,0) as ret_valor, ")
            .append("       coalesce(pf.nome, pj.razaosocial) as fornecedor, ")
            .append("       fonte.CODIGO as fonte, ")
            .append("       case pgto.status ")
            .append("          when 'ABERTO' then 'A' ")
            .append("          when 'DEFERIDO' then 'D' ")
            .append("          when 'INDEFERIDO' then 'I' ")
            .append("          when 'EFETUADO' then 'E' ")
            .append("          when 'BORDERO' then 'G' ")
            .append("          when 'PAGO' then 'P' ")
            .append("          when 'ESTORNADO' then 'ES' ")
            .append("       end as situacao, ")
            .append("       pgto.dataConciliacao, ")
            .append("       pgto.categoriaOrcamentaria, ")
            .append("       'N' as tipoLancamento, ")
            .append("       vw.codigo as codigoUnidade, vw.descricao as descricaounidade, ")
            .append("       vworg.codigo as codigoOrgao, vworg.descricao as descricaoorgao, ")
            .append(filtro.getPesquisouUg() || filtro.getApresentacao().equals("UNIDADE_GESTORA") ? " ug.codigo as codigoUg, ug.descricao as descricaoUg " : " null as codigoUg, null as descricaoUg ")
            .append("  from pagamento pgto ")
            .append(" inner join liquidacao l on pgto.liquidacao_id=l.id ")
            .append(" inner join empenho emp on l.empenho_id = emp.id ")
            .append(" INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID ")
            .append(" INNER JOIN PROVISAOPPADESPESA PROVDESP ON DESP.PROVISAOPPADESPESA_ID = PROVDESP.ID ")
            .append(" INNER JOIN CONTA CON ON PROVDESP.CONTADEDESPESA_ID = con.id ")
            .append(" inner join pessoa  p on emp.fornecedor_id = p.id ")
            .append("  left join pessoafisica pf on p.id = pf.id ")
            .append("  left join pessoajuridica pj on p.id = pj.id ")
            .append(" INNER JOIN CLASSECREDOR CC ON emp.CLASSECREDOR_ID = CC.ID ")
            .append(" inner join subconta sc on pgto.subconta_id = sc.id ")
            .append(" inner join contabancariaentidade cbe on sc.contabancariaentidade_id = cbe.id ")
            .append(buscarJoinVwUnidade("pgto.unidadeorganizacional_id"))
            .append(buscarJoinVwOrgao())
            .append(buscarJoinUnidadeGestora(filtro.getApresentacao(), filtro.getPesquisouUg()))
            .append(filtro.getFiltrarContaExtra() ? " left join RETENCAOPGTO retencao on retencao.PAGAMENTO_ID = pgto.id  left JOIN CONTA Cextra ON Cextra.ID = retencao.CONTAEXTRAORCAMENTARIA_ID " : "")
            .append("  LEFT JOIN (SELECT RET.PAGAMENTO_ID AS PAGAMENTO_ID,SUM(RET.VALOR) AS VALOR FROM RETENCAOPGTO RET GROUP BY RET.PAGAMENTO_ID)RET ON PGTO.ID = RET.PAGAMENTO_ID ")
            .append(buscarJoinsFuncionalProgramatica("PROVDESP"))
            .append(" inner join exercicio ex on pgto.exercicio_id = ex.id ")
            .append(" INNER JOIN FONTEDESPESAORC ftd on emp.FONTEDESPESAORC_ID =ftd.ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE ppf on ftd.PROVISAOPPAFONTE_ID=ppf.ID ")
            .append(" INNER JOIN conta cd on ppf.DESTINACAODERECURSOS_ID = cd.id ")
            .append(" INNER JOIN contadedestinacao contad on cd.id = contad.id ")
            .append(" INNER JOIN fontederecursos fonte on fonte.id = contad.FONTEDERECURSOS_ID ")
            .append(" WHERE CAST(coalesce(PGTO.DATAPAGAMENTO,PGTO.previstopara)AS DATE) BETWEEN TO_DATE(:DataInicial,'dd/mm/yyyy') AND TO_DATE(:DataFinal,'dd/mm/yyyy') ")
            .append(getAndVigenciaVwUnidade("CAST(coalesce(PGTO.DATAPAGAMENTO,PGTO.previstopara) AS DATE)"))
            .append(getAndVigenciaVwOrgao("CAST(coalesce(PGTO.DATAPAGAMENTO,PGTO.previstopara) AS DATE)"))
            .append(UtilRelatorioContabil.concatenarParametros(filtro.getParametros(), 1, "and"))
            .append(" union all ")
            .append(" select l.numero as liq_numero, ")
            .append("       emp.numero as emp_numero, ")
            .append("       est.numero as pgto_numero, ")
            .append("       cast(est.dataestorno AS DATE) AS datapagamento, ")
            .append("       coalesce(est.valorfinal,0) as pgto_valor, ")
            .append("       coalesce(est.valor,0) AS total, ")
            .append("       coalesce(ret.valor,0) as ret_valor, ")
            .append("       coalesce(pf.nome, pj.razaosocial) as fornecedor, ")
            .append("       fonte.CODIGO as fonte, ")
            .append("       case pgto.status ")
            .append("          when 'ABERTO' then 'A' ")
            .append("          when 'DEFERIDO' then 'D' ")
            .append("          when 'INDEFERIDO' then 'I' ")
            .append("          when 'EFETUADO' then 'E' ")
            .append("          when 'BORDERO' then 'G' ")
            .append("          when 'PAGO' then 'P' ")
            .append("          when 'ESTORNADO' then 'ES' ")
            .append("       end as situacao, ")
            .append("       est.dataConciliacao, ")
            .append("       pgto.categoriaOrcamentaria, ")
            .append("       'E' as tipoLancamento, ")
            .append("       vw.codigo as codigoUnidade, vw.descricao as descricaounidade, ")
            .append("       vworg.codigo as codigoOrgao, vworg.descricao as descricaoorgao, ")
            .append(filtro.getPesquisouUg() || filtro.getApresentacao().equals("UNIDADE_GESTORA") ? " ug.codigo as codigoUg, ug.descricao as descricaoUg " : " null as codigoUg, null as descricaoUg ")
            .append("  from pagamentoestorno est")
            .append(" inner join pagamento pgto on est.pagamento_id = pgto.id")
            .append(" inner join liquidacao l on pgto.liquidacao_id=l.id ")
            .append(" inner join empenho emp on l.empenho_id = emp.id ")
            .append(" INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID ")
            .append(" INNER JOIN PROVISAOPPADESPESA PROVDESP ON DESP.PROVISAOPPADESPESA_ID = PROVDESP.ID ")
            .append(" INNER JOIN CONTA CON ON PROVDESP.CONTADEDESPESA_ID = con.id ")
            .append(" inner join pessoa  p on emp.fornecedor_id = p.id ")
            .append("  left join pessoafisica pf on p.id = pf.id ")
            .append("  left join pessoajuridica pj on p.id = pj.id ")
            .append(" INNER JOIN CLASSECREDOR CC ON emp.CLASSECREDOR_ID = CC.ID ")
            .append(" inner join subconta sc on pgto.subconta_id = sc.id ")
            .append(" inner join contabancariaentidade cbe on sc.contabancariaentidade_id = cbe.id ")
            .append(buscarJoinVwUnidade("pgto.unidadeorganizacional_id"))
            .append(buscarJoinVwOrgao())
            .append(buscarJoinUnidadeGestora(filtro.getApresentacao(), filtro.getPesquisouUg()))
            .append(filtro.getFiltrarContaExtra() ? " left join RETENCAOPGTO retencao on retencao.pagamentoEstorno_id = est.id  left JOIN CONTA Cextra ON Cextra.ID = retencao.CONTAEXTRAORCAMENTARIA_ID " : "")
            .append("  LEFT JOIN (SELECT RET.pagamentoEstorno_id AS estorno_id,SUM(RET.VALOR) AS VALOR FROM RETENCAOPGTO RET GROUP BY ret.pagamentoEstorno_id)RET ON est.ID = RET.estorno_id ")
            .append(buscarJoinsFuncionalProgramatica("PROVDESP"))
            .append(" inner join exercicio ex on pgto.exercicio_id = ex.id ")
            .append(" INNER JOIN FONTEDESPESAORC ftd on emp.FONTEDESPESAORC_ID =ftd.ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE ppf on ftd.PROVISAOPPAFONTE_ID=ppf.ID ")
            .append(" INNER JOIN conta cd on ppf.DESTINACAODERECURSOS_ID = cd.id ")
            .append(" INNER JOIN contadedestinacao contad on cd.id = contad.id ")
            .append(" INNER JOIN fontederecursos fonte on fonte.id = contad.FONTEDERECURSOS_ID ")
            .append(" WHERE CAST(est.dataestorno AS DATE) BETWEEN TO_DATE(:DataInicial,'dd/mm/yyyy') AND TO_DATE(:DataFinal,'dd/mm/yyyy') ")
            .append(getAndVigenciaVwUnidade("CAST(est.dataestorno AS DATE)"))
            .append(getAndVigenciaVwOrgao("CAST(est.dataestorno AS DATE)"))
            .append(UtilRelatorioContabil.concatenarParametros(filtro.getParametros(), 1, "and"))
            .append(") ")
            .append(filtro.getOrdenacao());


        Query q = getEm().createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametros(filtro.getParametros(), q);
        List<Object[]> resultado = q.getResultList();
        List<PagamentoItem> retorno = new ArrayList<>();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                PagamentoItem pagamentoItem = new PagamentoItem();
                pagamentoItem.setNumeroLiquidacao((String) obj[0]);
                pagamentoItem.setNumeroEmpenho((String) obj[1]);
                pagamentoItem.setNumeroPagamento((String) obj[2]);
                pagamentoItem.setDataPagamento((Date) obj[3]);
                pagamentoItem.setValorPagamento((BigDecimal) obj[4]);
                pagamentoItem.setTotal((BigDecimal) obj[5]);
                pagamentoItem.setValorRetencao((BigDecimal) obj[6]);
                pagamentoItem.setFornecedor((String) obj[7]);
                pagamentoItem.setFonteCodigo((String) obj[8]);
                pagamentoItem.setSituacao((String) obj[9]);
                pagamentoItem.setDataBaixa((Date) obj[10]);
                pagamentoItem.setCategoriaOrcamentaria((String) obj[11]);
                pagamentoItem.setTipoLancamento(((Character) obj[12]).toString());
                pagamentoItem.setUnidade((String) obj[13] + " - " + (String) obj[14]);
                pagamentoItem.setOrgao((String) obj[15] + " - " + (String) obj[16]);
                pagamentoItem.setUnidadeGestora((String) obj[17] + " - " + (String) obj[18]);
                retorno.add(pagamentoItem);
            }
        }
        return retorno;
    }

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
    }
}
