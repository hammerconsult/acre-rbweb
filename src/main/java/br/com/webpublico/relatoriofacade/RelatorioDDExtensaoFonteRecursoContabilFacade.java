package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.DDExtensaoFonteRecurso;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.util.UtilRelatorioContabil;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mateus on 05/07/17.
 */
@Stateless
public class RelatorioDDExtensaoFonteRecursoContabilFacade extends RelatorioQDDSuperFacade implements Serializable {


    public List<DDExtensaoFonteRecurso> montarConsutaSQL(List<ParametrosRelatorios> parametros, String apresentacao, Boolean pesquisouUg) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select sum(dotacaoinicial) as dotacaoinicial, ")
            .append("       sum(dotacaoatual) as dotacaoatual, ")
            .append("       sum(empenhado) as empenhado, ")
            .append("       sum(liquidado) as liquidado, ")
            .append("       sum(pago) as pago, ")
            .append("       codigoExtensao, ")
            .append("       descricaoExtensao, ")
            .append("       codigoOrgao || ' - ' || descricaoOrgao as orgao, ")
            .append("       codigoUnidade || ' - ' || descricaoUnidade as unidade, ")
            .append("       codigoUnidadeGestora || ' - ' || descricaoUnidadeGestora as unidadeGestora ")
            .append(" from ( ")
            .append("  select coalesce(saldo.dotacao,0 ) as dotacaoinicial, ")
            .append("      (coalesce(saldo.dotacao,0) + coalesce(saldo.suplementado,0) - coalesce(saldo.reduzido,0)) as dotacaoatual,  ")
            .append("      coalesce(saldo.empenhado,0) as empenhado, ")
            .append("      coalesce(saldo.liquidado, 0) as liquidado, ")
            .append("      coalesce(saldo.pago, 0) as pago, ")
            .append("      extfr.codigo as codigoExtensao, ")
            .append("      extfr.descricao as descricaoExtensao, ")
            .append("ORGAO" .equals(apresentacao) || "UNIDADE" .equals(apresentacao) ? " vworg.codigo as codigoOrgao, vworg.descricao as descricaoOrgao, " : " null as codigoOrgao, null as descricaoOrgao, ")
            .append("UNIDADE" .equals(apresentacao) ? " vw.codigo as codigoUnidade, vw.descricao as descricaoUnidade, " : " null as codigoUnidade, null as descricaoUnidade, ")
            .append("UNIDADE_GESTORA" .equals(apresentacao) ? " ug.codigo as codigoUnidadeGestora, ug.descricao as descricaoUnidadeGestora " : " null as codigoUnidadeGestora, null as descricaoUnidadeGestora ")
            .append(buscarComplementoQuerySaldo())
            .append(buscarJoinUnidadeGestora(apresentacao, pesquisouUg))
            .append(" inner join extensaoFonteRecurso extfr on extfr.id = provfonte.extensaoFonteRecurso_id ")
            .append(" where saldo.datasaldo <= to_date(:DATAREFERENCIA, 'dd/mm/yyyy')  ")
            .append("   and to_date(:DATAREFERENCIA, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:DATAREFERENCIA, 'dd/mm/yyyy')) ")
            .append("   and to_date(:DATAREFERENCIA, 'dd/mm/yyyy') between vworg.iniciovigencia and coalesce(vworg.fimvigencia, to_date(:DATAREFERENCIA, 'dd/mm/yyyy'))  ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, "AND"))
            .append(") dados ")
            .append(" group by codigoExtensao, descricaoExtensao, codigoOrgao, descricaoOrgao, descricaoUnidade, codigoUnidade, descricaoUnidade, codigoUnidadeGestora, descricaoUnidadeGestora ")
            .append(" order by ")
            .append("ORGAO" .equals(apresentacao) || "UNIDADE" .equals(apresentacao) ? " codigoOrgao, " : "")
            .append("UNIDADE" .equals(apresentacao) ? " codigoUnidade,  " : "")
            .append("UNIDADE_GESTORA" .equals(apresentacao) ? " codigoUnidadeGestora, " : "")
            .append(" codigoExtensao ");

        Query q = getEm().createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        List<DDExtensaoFonteRecurso> retorno = new ArrayList<>();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                DDExtensaoFonteRecurso ddExtensaoFonteRecurso = new DDExtensaoFonteRecurso();
                ddExtensaoFonteRecurso.setDotacaoInicial((BigDecimal) obj[0]);
                ddExtensaoFonteRecurso.setDotacaoAtualizada((BigDecimal) obj[1]);
                ddExtensaoFonteRecurso.setDespesaEmpenhada((BigDecimal) obj[2]);
                ddExtensaoFonteRecurso.setDespesaLiquidada((BigDecimal) obj[3]);
                ddExtensaoFonteRecurso.setDespesaPaga((BigDecimal) obj[4]);
                ddExtensaoFonteRecurso.setCodigoConta(((BigDecimal) obj[5]).toString());
                ddExtensaoFonteRecurso.setDescricaoConta((String) obj[6]);
                ddExtensaoFonteRecurso.setOrgao((String) obj[7]);
                ddExtensaoFonteRecurso.setUnidade((String) obj[8]);
                ddExtensaoFonteRecurso.setUnidadeGestora((String) obj[9]);
                retorno.add(ddExtensaoFonteRecurso);
            }
        }
        return retorno;
    }
}
