package br.com.webpublico.negocios;

import br.com.webpublico.entidadesauxiliares.PesquisaLCP;
import br.com.webpublico.enums.TipoMovimentoTCE;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 04/10/17.
 */
@Stateless
public class ConsultaLCPFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public List<PesquisaLCP> buscarLcps(PesquisaLCP filtros, int inicio, int max) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select * from ( ")
            .append(" select e.codigo as codigo_evento, ")
            .append("        e.descricao as descricao_evento, ")
            .append("        clp.codigo as codigo_clp, ")
            .append("        clp.nome as descricao_clp, ")
            .append("        T.CODIGO as codigo_tag, ")
            .append("        t.descricao as descricao_tag, ")
            .append("        c.codigo as codigo_conta_contabil, ")
            .append("        c.descricao as descricao_conta_contabil, ")
            .append("        CA.CODIGO as codigo_conta_auxiliar, ")
            .append("        ca.descricao as descricao_conta_auxiliar, ")
            .append("        LCP.TIPOMOVIMENTOTCECREDITO as tipo_movimento_tce, ")
            .append("        LCP.ITEM as item_lcp, ")
            .append("        LCP.CODIGO as codigo_lcp, ")
            .append("        CLP.INICIOVIGENCIA as clp_inicio_vigencia, ")
            .append("        'CREDITO' as debito_credito, ")
            .append("        caSiconfi.CODIGO as codigo_conta_auxiliar_siconfi, ")
            .append("        caSiconfi.descricao as descricao_conta_siconfi ")
            .append("   from LCP lcp ")
            .append("  inner join clp clp on lcp.CLP_ID = clp.id ")
            .append("  inner join ITEMEVENTOCLP it on it.CLP_ID = clp.id and it.DATAVIGENCIA is null ")
            .append("  inner join EVENTOCONTABIL e on it.EVENTOCONTABIL_ID = e.id ")
            .append("   left join TAGOCC t on t.id = lcp.TAGOCCCREDITO_ID ")
            .append("   left join conta c on c.id = LCP.CONTACREDITO_ID ")
            .append("   left join TIPOCONTAAUXILIAR ca on ca.id = lcp.TIPOCONTAAUXILIARCREDITO_ID ")
            .append("   left join TIPOCONTAAUXILIAR caSiconfi on caSiconfi.id = lcp.tipoContaAuxCredSiconfi_ID ")
            .append("  union all ")
            .append(" select e.codigo as codigo_evento, ")
            .append("        e.descricao as descricao_evento, ")
            .append("        clp.codigo as codigo_clp, ")
            .append("        clp.nome as descricao_clp, ")
            .append("        T.CODIGO as codigo_tag, ")
            .append("        t.descricao as descricao_tag, ")
            .append("        c.codigo as codigo_conta_contabil, ")
            .append("        c.descricao as descricao_conta_contabil, ")
            .append("        CA.CODIGO as codigo_conta_auxiliar, ")
            .append("        ca.descricao as descricao_conta_auxiliar, ")
            .append("        LCP.TIPOMOVIMENTOTCEDEBITO as tipo_movimento_tce, ")
            .append("        LCP.ITEM as item_lcp, ")
            .append("        LCP.CODIGO as codigo_lcp, ")
            .append("        CLP.INICIOVIGENCIA as clp_inicio_vigencia, ")
            .append("        'DEBITO' as debito_credito, ")
            .append("        caSiconfi.CODIGO as codigo_conta_auxiliar_siconfi, ")
            .append("        caSiconfi.descricao as descricao_conta_siconfi ")
            .append("   from LCP lcp ")
            .append("  inner join clp clp on lcp.CLP_ID = clp.id ")
            .append("  inner join ITEMEVENTOCLP it on it.CLP_ID = clp.id and it.DATAVIGENCIA is null ")
            .append("  inner join EVENTOCONTABIL e on it.EVENTOCONTABIL_ID = e.id ")
            .append("   left join TAGOCC t on t.id = lcp.TAGOCCDEBITO_ID ")
            .append("   left join conta c on c.id = LCP.CONTADEBITO_ID ")
            .append("   left join TIPOCONTAAUXILIAR ca on ca.id = lcp.TIPOCONTAAUXILIARDEBITO_ID ")
            .append("   left join TIPOCONTAAUXILIAR caSiconfi on caSiconfi.id = lcp.tipoContaAuxDebSiconfi_ID ")
            .append("  ) ")
            .append(montarClausulaWhere(filtros))
            .append("  order by codigo_clp, codigo_evento, item_lcp, debito_credito desc ");
        Query q = em.createNativeQuery(sql.toString());
        adicionarParametros(filtros, q);
        q.setMaxResults(max);
        q.setFirstResult(inicio);
        List<PesquisaLCP> retorno = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                PesquisaLCP pesquisaLCP = new PesquisaLCP();
                pesquisaLCP.setCodigoEvento((String) obj[0]);
                pesquisaLCP.setDescricaoEvento((String) obj[1]);
                pesquisaLCP.setCodigoClp((String) obj[2]);
                pesquisaLCP.setDescricaoClp((String) obj[3]);
                if (obj[4] != null) {
                    pesquisaLCP.setCodigoTag((String) obj[4]);
                }
                if (obj[5] != null) {
                    pesquisaLCP.setDescricaoTag((String) obj[5]);
                }
                if (obj[6] != null) {
                    pesquisaLCP.setCodigoContaContabil((String) obj[6]);
                }
                if (obj[7] != null) {
                    pesquisaLCP.setDescricaoContaContabil((String) obj[7]);
                }
                pesquisaLCP.setCodigoContaAuxiliar((String) obj[8]);
                pesquisaLCP.setDescricaoContaAuxiliar((String) obj[9]);
                if (obj[10] != null) {
                    pesquisaLCP.setTipoMovimentoTCE(TipoMovimentoTCE.valueOf((String) obj[10]));
                }
                pesquisaLCP.setItemLcp(((BigDecimal) obj[11]).intValue());
                pesquisaLCP.setCodigoLcp((String) obj[12]);
                pesquisaLCP.setClpInicioVigencia((Date) obj[13]);
                pesquisaLCP.setDebitoCredito(PesquisaLCP.DebitoCredito.valueOf((String) obj[14]));
                pesquisaLCP.setCodigoContaAuxiliarSiconfi((String) obj[15]);
                pesquisaLCP.setDescricaoContaAuxiliarSiconfi((String) obj[16]);
                retorno.add(pesquisaLCP);
            }
        }
        return retorno;
    }

    public Integer buscarQuantidadeLcps(PesquisaLCP filtros) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select count(1) from ( ")
            .append(" select e.codigo as codigo_evento, ")
            .append("        e.descricao as descricao_evento, ")
            .append("        clp.codigo as codigo_clp, ")
            .append("        clp.descricao as descricao_clp, ")
            .append("        T.CODIGO as codigo_tag, ")
            .append("        t.descricao as descricao_tag, ")
            .append("        c.codigo as codigo_conta_contabil, ")
            .append("        c.descricao as descricao_conta_contabil, ")
            .append("        CA.CODIGO as codigo_conta_auxiliar, ")
            .append("        ca.descricao as descricao_conta_auxiliar, ")
            .append("        LCP.TIPOMOVIMENTOTCECREDITO as tipo_movimento_tce, ")
            .append("        LCP.ITEM as item_lcp, ")
            .append("        LCP.CODIGO as codigo_lcp, ")
            .append("        CLP.INICIOVIGENCIA as clp_inicio_vigencia, ")
            .append("        'CREDITO' as debito_credito, ")
            .append("        caSiconfi.CODIGO as codigo_conta_auxiliar_siconfi, ")
            .append("        caSiconfi.descricao as descricao_conta_siconfi ")
            .append("   from LCP lcp ")
            .append("  inner join clp clp on lcp.CLP_ID = clp.id ")
            .append("  inner join ITEMEVENTOCLP it on it.CLP_ID = clp.id and it.DATAVIGENCIA is null ")
            .append("  inner join EVENTOCONTABIL e on it.EVENTOCONTABIL_ID = e.id ")
            .append("   left join TAGOCC t on t.id = lcp.TAGOCCCREDITO_ID ")
            .append("   left join conta c on c.id = LCP.CONTACREDITO_ID ")
            .append("   left join TIPOCONTAAUXILIAR ca on ca.id = lcp.TIPOCONTAAUXILIARCREDITO_ID ")
            .append("   left join TIPOCONTAAUXILIAR caSiconfi on caSiconfi.id = lcp.tipoContaAuxCredSiconfi_ID ")
            .append("  union all ")
            .append(" select e.codigo as codigo_evento, ")
            .append("        e.descricao as descricao_evento, ")
            .append("        clp.codigo as codigo_clp, ")
            .append("        clp.descricao as descricao_clp, ")
            .append("        T.CODIGO as codigo_tag, ")
            .append("        t.descricao as descricao_tag, ")
            .append("        c.codigo as codigo_conta_contabil, ")
            .append("        c.descricao as descricao_conta_contabil, ")
            .append("        CA.CODIGO as codigo_conta_auxiliar, ")
            .append("        ca.descricao as descricao_conta_auxiliar, ")
            .append("        LCP.TIPOMOVIMENTOTCEDEBITO as tipo_movimento_tce, ")
            .append("        LCP.ITEM as item_lcp, ")
            .append("        LCP.CODIGO as codigo_lcp, ")
            .append("        CLP.INICIOVIGENCIA as clp_inicio_vigencia, ")
            .append("        'DEBITO' as debito_credito, ")
            .append("        caSiconfi.CODIGO as codigo_conta_auxiliar_siconfi, ")
            .append("        caSiconfi.descricao as descricao_conta_siconfi ")
            .append("   from LCP lcp ")
            .append("  inner join clp clp on lcp.CLP_ID = clp.id ")
            .append("  inner join ITEMEVENTOCLP it on it.CLP_ID = clp.id and it.DATAVIGENCIA is null ")
            .append("  inner join EVENTOCONTABIL e on it.EVENTOCONTABIL_ID = e.id ")
            .append("   left join TAGOCC t on t.id = lcp.TAGOCCDEBITO_ID ")
            .append("   left join conta c on c.id = LCP.CONTADEBITO_ID ")
            .append("   left join TIPOCONTAAUXILIAR ca on ca.id = lcp.TIPOCONTAAUXILIARDEBITO_ID ")
            .append("   left join TIPOCONTAAUXILIAR caSiconfi on caSiconfi.id = lcp.tipoContaAuxDebSiconfi_ID ")
            .append("  ) ")
            .append(montarClausulaWhere(filtros));
        Query q = em.createNativeQuery(sql.toString());
        adicionarParametros(filtros, q);
        return ((BigDecimal) q.getResultList().get(0)).intValue();
    }

    private String montarClausulaWhere(PesquisaLCP filtros) {
        StringBuilder sql = new StringBuilder();
        String clausula = " where ";
        if (!filtros.getCodigoEvento().isEmpty()) {
            sql.append(clausula).append(" lower(codigo_evento) = :codigoEvento ");
            clausula = " and ";
        }
        if (!filtros.getDescricaoEvento().isEmpty()) {
            sql.append(clausula).append(" lower(descricao_evento) like :descricaoEvento ");
            clausula = " and ";
        }
        if (!filtros.getCodigoClp().isEmpty()) {
            sql.append(clausula).append(" lower(codigo_clp) = :codigoClp ");
            clausula = " and ";
        }
        if (!filtros.getDescricaoClp().isEmpty()) {
            sql.append(clausula).append(" lower(descricao_clp) like :descricaoClp ");
            clausula = " and ";
        }
        if (!filtros.getCodigoTag().isEmpty()) {
            sql.append(clausula).append(" lower(codigo_tag) = :codigoTag ");
            clausula = " and ";
        }
        if (!filtros.getDescricaoTag().isEmpty()) {
            sql.append(clausula).append(" lower(descricao_tag) like :descricaoTag ");
            clausula = " and ";
        }
        if (!filtros.getCodigoContaContabil().isEmpty()) {
            sql.append(clausula).append(" codigo_conta_contabil like :codigoContaContabil ");
            clausula = " and ";
        }
        if (!filtros.getDescricaoContaContabil().isEmpty()) {
            sql.append(clausula).append(" lower(descricao_conta_contabil) like :descricaoContaContabil ");
            clausula = " and ";
        }
        if (!filtros.getCodigoContaAuxiliar().isEmpty()) {
            sql.append(clausula).append(" codigo_conta_auxiliar = :codigoContaAuxiliar ");
            clausula = " and ";
        }
        if (!filtros.getDescricaoContaAuxiliar().isEmpty()) {
            sql.append(clausula).append(" lower(descricao_conta_auxiliar) like :descricaoContaAuxiliar ");
            clausula = " and ";
        }
        if (filtros.getTipoMovimentoTCE() != null) {
            sql.append(clausula).append(" tipo_movimento_tce = :tipoMovimentoTCE ");
            clausula = " and ";
        }
        if (!filtros.getCodigoContaAuxiliarSiconfi().isEmpty()) {
            sql.append(clausula).append(" codigo_conta_auxiliar_siconfi = :codContaAuxiliarSiconfi ");
            clausula = " and ";
        }
        if (!filtros.getDescricaoContaAuxiliarSiconfi().isEmpty()) {
            sql.append(clausula).append(" lower(descricao_conta_auxiliar_siconfi) like :desContaAuxiliarSiconfi ");
            clausula = " and ";
        }
        if (filtros.getItemLcp() != null) {
            sql.append(clausula).append(" item_lcp = :itemLcp ");
            clausula = " and ";
        }
        if (!filtros.getCodigoLcp().isEmpty()) {
            sql.append(clausula).append(" lower(codigo_lcp) = :codigoLcp ");
            clausula = " and ";
        }
        if (filtros.getClpInicioVigencia() != null) {
            sql.append(clausula).append(" trunc(clp_inicio_vigencia) >= to_date(:inicioVigencia, 'dd/mm/yyyy') ");
            clausula = " and ";
        }
        if (filtros.getDebitoCredito() != null) {
            sql.append(clausula).append(" debito_credito = :debitoCredito ");
        }
        return sql.toString();
    }

    private void adicionarParametros(PesquisaLCP filtros, Query q) {
        if (!filtros.getCodigoEvento().isEmpty()) {
            q.setParameter("codigoEvento", getCampoString(filtros.getCodigoEvento()));
        }
        if (!filtros.getDescricaoEvento().isEmpty()) {
            q.setParameter("descricaoEvento", getCampoStringLike(filtros.getDescricaoEvento()));
        }
        if (!filtros.getCodigoClp().isEmpty()) {
            q.setParameter("codigoClp", getCampoString(filtros.getCodigoClp()));
        }
        if (!filtros.getDescricaoClp().isEmpty()) {
            q.setParameter("descricaoClp", getCampoStringLike(filtros.getDescricaoClp()));
        }
        if (!filtros.getCodigoTag().isEmpty()) {
            q.setParameter("codigoTag", getCampoString(filtros.getCodigoTag()));
        }
        if (!filtros.getDescricaoTag().isEmpty()) {
            q.setParameter("descricaoTag", getCampoStringLike(filtros.getDescricaoTag()));
        }
        if (!filtros.getCodigoContaContabil().isEmpty()) {
            q.setParameter("codigoContaContabil", getCampoString(filtros.getCodigoContaContabil()) + "%");
        }
        if (!filtros.getDescricaoContaContabil().isEmpty()) {
            q.setParameter("descricaoContaContabil", getCampoStringLike(filtros.getDescricaoContaContabil()));
        }
        if (!filtros.getCodigoContaAuxiliar().isEmpty()) {
            q.setParameter("codigoContaAuxiliar", getCampoString(filtros.getCodigoContaAuxiliar()));
        }
        if (!filtros.getDescricaoContaAuxiliar().isEmpty()) {
            q.setParameter("descricaoContaAuxiliar", getCampoStringLike(filtros.getDescricaoContaAuxiliar()));
        }
        if (filtros.getTipoMovimentoTCE() != null) {
            q.setParameter("tipoMovimentoTCE", filtros.getTipoMovimentoTCE().name());
        }
        if (!filtros.getCodigoContaAuxiliarSiconfi().isEmpty()) {
            q.setParameter("codContaAuxiliarSiconfi", getCampoString(filtros.getCodigoContaAuxiliarSiconfi()));
        }
        if (!filtros.getDescricaoContaAuxiliarSiconfi().isEmpty()) {
            q.setParameter("desContaAuxiliarSiconfi", getCampoStringLike(filtros.getDescricaoContaAuxiliarSiconfi()));
        }
        if (filtros.getItemLcp() != null) {
            q.setParameter("itemLcp", filtros.getItemLcp());
        }
        if (!filtros.getCodigoLcp().isEmpty()) {
            q.setParameter("codigoLcp", getCampoString(filtros.getCodigoLcp()));
        }
        if (filtros.getClpInicioVigencia() != null) {
            q.setParameter("inicioVigencia", DataUtil.getDataFormatada(filtros.getClpInicioVigencia()));
        }
        if (filtros.getDebitoCredito() != null) {
            q.setParameter("debitoCredito", filtros.getDebitoCredito().name());
        }
    }

    private String getCampoStringLike(String campo) {
        return "%" + getCampoString(campo) + "%";
    }

    private String getCampoString(String campo) {
        return campo.trim().toLowerCase() ;
    }
}
