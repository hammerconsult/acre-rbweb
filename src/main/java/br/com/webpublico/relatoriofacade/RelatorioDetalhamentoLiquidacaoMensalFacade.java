package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.DetalhamentoLiquidacao;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.negocios.ContaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 24/07/2015.
 */
@Stateless
public class RelatorioDetalhamentoLiquidacaoMensalFacade extends RelatorioContabilSuperFacade {
    @EJB
    private ContaFacade contaFacade;

    public List<DetalhamentoLiquidacao> recuperaRelatorio(List<ParametrosRelatorios> parametrosRelatorios, Boolean pesquisouUg, String apresentacao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select conta_codigo || ' - ' || conta_descricao as conta, ")
                .append("        fonte_codigo || ' - ' || fonte_descricao as fonte, ")
                .append("        case length(data) ")
                .append("           when 6 then '0' || data ")
                .append("           else data ")
                .append("        end as data, ")
                .append("        sum(valor) as valor, ")
                .append("        ORGAO,")
                .append("        unidade, ")
                .append("        unidadegestora ")
                .append("   from ( ")
                .append(" select sum(liq.valor) as valor, ")
                .append("        extract(month from liq.dataliquidacao) as mes, ")
                .append("        extract(month from liq.dataliquidacao) || '/' || extract(year from liq.dataliquidacao) as data, ")
                .append("        c.codigo as conta_codigo, ")
                .append("        c.descricao as conta_descricao, ")
                .append("        fonte.codigo as fonte_codigo, ")
                .append("        fonte.descricao as fonte_descricao, ")
                .append(apresentacao.equals("ORGAO") || apresentacao.equals("UNIDADE") ? " VWORG.CODIGO || ' - ' || VWORG.DESCRICAO AS ORGAO,  " : " '' AS ORGAO, ")
                .append(apresentacao.equals("UNIDADE") ? "       VW.CODIGO || ' - ' || VW.DESCRICAO AS UNIDADE,  " : " '' as unidade, ")
                .append(apresentacao.equals("UNIDADE_GESTORA") ? " ug.codigo || ' - ' || ug.descricao as unidadegestora " : " '' as unidadegestora ")
                .append("   from liquidacao liq ")
                .append("  inner join empenho emp on liq.empenho_id = emp.id ")
                .append("  inner join despesaorc desp on emp.despesaorc_id = desp.id ")
                .append("  inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id ")
                .append("  inner join conta c on provdesp.contadedespesa_id = c.id ")
                .append("  inner join fontedespesaorc fontedesp on emp.fontedespesaorc_id = fontedesp.id ")
                .append("  inner join provisaoppafonte provfonte on fontedesp.provisaoppafonte_id = provfonte.id ")
                .append("  inner join contadedestinacao cd on provfonte.destinacaoderecursos_id = cd.id ")
                .append("  inner join fontederecursos fonte on cd.fontederecursos_id = fonte.id ")
                .append("  inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id ")
                .append("  inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id  ")
                .append(pesquisouUg || apresentacao.equals("UNIDADE_GESTORA") ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
                .append("  where liq.dataliquidacao between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ")
                .append("    and liq.dataliquidacao between vw.iniciovigencia and coalesce(vw.fimvigencia, liq.dataliquidacao) ")
                .append("    and liq.dataliquidacao between vworg.iniciovigencia and coalesce(vworg.fimvigencia, liq.dataliquidacao)  ")
                .append(concatenaParametros(parametrosRelatorios, 1, " and "))
                .append("  group by liq.dataliquidacao, c.codigo, c.descricao, fonte.codigo, fonte.descricao ")
                .append(apresentacao.equals("ORGAO") || apresentacao.equals("UNIDADE") ? " , VWORG.CODIGO, VWORG.DESCRICAO  " : "")
                .append(apresentacao.equals("UNIDADE") ? ", VW.CODIGO, VW.DESCRICAO  " : "")
                .append(apresentacao.equals("UNIDADE_GESTORA") ? " , ug.codigo, ug.descricao " : "")
                .append("  ) ")
                .append(" group by data, mes, conta_codigo, conta_descricao, fonte_codigo, fonte_descricao, orgao, unidade, unidadegestora ");
        if (apresentacao.equals("ORGAO")) {
            sql.append(" order by orgao, conta_codigo, fonte_codigo, mes ");
        } else if (apresentacao.equals("UNIDADE")) {
            sql.append(" order by unidade, conta_codigo, fonte_codigo, mes ");
        } else if (apresentacao.equals("UNIDADE_GESTORA")) {
            sql.append(" order by unidadegestora, conta_codigo, fonte_codigo, mes ");
        } else {
            sql.append(" order by conta_codigo, fonte_codigo, mes ");
        }
        Query q = getEm().createNativeQuery(sql.toString());
        q = adicionaParametros(parametrosRelatorios, q);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<DetalhamentoLiquidacao> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                DetalhamentoLiquidacao item = new DetalhamentoLiquidacao();
                item.setConta((String) obj[0]);
                item.setFonteDeRecursos((String) obj[1]);
                switch (((String) obj[2]).substring(0, 2)) {
                    case "01":
                        item.setValorMes1((BigDecimal) obj[3]);
                        break;
                    case "02":
                        item.setValorMes2((BigDecimal) obj[3]);
                        break;
                    case "03":
                        item.setValorMes3((BigDecimal) obj[3]);
                        break;
                    case "04":
                        item.setValorMes4((BigDecimal) obj[3]);
                        break;
                    case "05":
                        item.setValorMes5((BigDecimal) obj[3]);
                        break;
                    case "06":
                        item.setValorMes6((BigDecimal) obj[3]);
                        break;
                    case "07":
                        item.setValorMes7((BigDecimal) obj[3]);
                        break;
                    case "08":
                        item.setValorMes8((BigDecimal) obj[3]);
                        break;
                    case "09":
                        item.setValorMes9((BigDecimal) obj[3]);
                        break;
                    case "10":
                        item.setValorMes10((BigDecimal) obj[3]);
                        break;
                    case "11":
                        item.setValorMes11((BigDecimal) obj[3]);
                        break;
                    case "12":
                        item.setValorMes12((BigDecimal) obj[3]);
                        break;
                }
                item.setTotal(item.getValorMes1()
                        .add(item.getValorMes2())
                        .add(item.getValorMes3())
                        .add(item.getValorMes4())
                        .add(item.getValorMes5())
                        .add(item.getValorMes6())
                        .add(item.getValorMes7())
                        .add(item.getValorMes8())
                        .add(item.getValorMes9())
                        .add(item.getValorMes10())
                        .add(item.getValorMes11())
                        .add(item.getValorMes12()));
                item.setOrgao((String) obj[4]);
                item.setUnidade((String) obj[5]);
                item.setUnidadeGestora((String) obj[6]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }
}
