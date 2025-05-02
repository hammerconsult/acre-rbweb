package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.MovimentacaoDividaPublicaEstornoItem;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.negocios.DividaPublicaFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 07/08/14
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RelatorioMovimentacaoDividaPublicaEstornoFacade implements Serializable {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;

    public RelatorioMovimentacaoDividaPublicaEstornoFacade() {
    }

    public List<MovimentacaoDividaPublicaEstornoItem> recuperaResumido(Boolean pesquisouUg, List<ParametrosRelatorios> parametros, String apresentacao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT trunc(EST.DATA) AS DATA, ")
                .append("        EST.NUMERO AS DOCUMENTO, ")
                .append("        CASE EST.OPERACAOMOVIMENTODIVIDAPUBLICA ")
                .append("           WHEN 'INSCRICAO_PRINCIPAL' THEN 'Inscrição Principal' ")
                .append("           WHEN 'ATUALIZACAO_JUROS' THEN 'Atualização de Juros' ")
                .append("           WHEN 'ATUALiZACAO_ENCARGOS' THEN 'Atualização de Encargos' ")
                .append("           WHEN 'APROPRIACAO_JUROS' THEN 'Apropriação de Juros' ")
                .append("           WHEN 'APROPRIACAO_ENCARGOS' THEN 'Apropriação de Encargos' ")
                .append("           WHEN 'TRANSFERENCIA_LONGO_PARA_CURTO_PRAZO' THEN 'Transferência de Longo para Curto prazo' ")
                .append("           WHEN 'TRANSFERENCIA_CURTO_PARA_LONGO_PRAZO' THEN 'Transferência de Curto para Longo prazo' ")
                .append("           WHEN 'CANCELAMENTO_APROPRIACAO_JUROS' THEN 'Cancelamento de Apropriação de Juros' ")
                .append("           WHEN 'CANCELAMENTO_APROPRIACAO_ENCARGOS' THEN 'Cancelamento de Apropriação de Encargos' ")
                .append("           WHEN 'CANCELAMENTO_PRINCIPAL' THEN 'Cancelamento do Principal' ")
                .append("           WHEN 'CANCELAMENTO_JUROS' THEN 'Cancelamento de Juros' ")
                .append("           WHEN 'CANCELAMENTO_ENCARGOS' THEN 'Cancelamento de Encargos' ")
                .append("           WHEN 'PAGAMENTO_AMORTIZACAO' THEN 'Pagamento/Amortização' ")
                .append("           WHEN 'RECEITA_OPERACAO_CREDITO' THEN 'Receita de operação de crédito' ")
                .append("        END AS OPERACAO, ")
                .append("        EST.HISTORICO, ")
                .append("        DIVIDA.NUMERODOCCONTPROC, ")
                .append("        SUBSTR(vw.CODIGO, 4, 7) AS ORGUNI, ")
                .append("        EST.VALOR, ")
                .append("        1 AS CONT, ")
                .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
                .append("        vworg.codigo || ' - ' || vworg.descricao as orgao, ");
        sql.append(pesquisouUg || apresentacao.equals("UNIDADE_GESTORA") ? " ug.codigo || ' - ' || ug.descricao as unidade_gestora " : " '' as unidade_gestora ");
        sql.append("   FROM MOVIMENTODIVIDAPUBLICA est ")
                .append("  INNER JOIN DIVIDAPUBLICA DIVIDA ON est.DIVIDAPUBLICA_ID = DIVIDA.ID ")
                .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA vw ON vw.SUBORDINADA_ID = EST.UNIDADEORGANIZACIONAL_ID ")
                .append("  inner join VWHIERARQUIAORCAMENTARIA vworg on vworg.subordinada_id = vw.superior_id ");
        sql.append(pesquisouUg || apresentacao.equals("UNIDADE_GESTORA") ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "");
        sql.append(" where trunc(EST.DATA) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(EST.DATA)) and est.tipolancamento = 'ESTORNO' ")
                .append(" AND trunc(EST.DATA) between vwORG.iniciovigencia and coalesce(vwORG.fimvigencia, trunc(EST.DATA)) ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        if (apresentacao.equals("UNIDADE_GESTORA")) {
            sql.append(" ORDER BY ug.codigo, trunc(EST.DATA) ");
        } else {
            sql.append(" order by vw.codigo, trunc(EST.DATA) ");
        }

        Query q = em.createNativeQuery(sql.toString());
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<MovimentacaoDividaPublicaEstornoItem> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                MovimentacaoDividaPublicaEstornoItem item = new MovimentacaoDividaPublicaEstornoItem();
                item.setData((Date) obj[0]);
                item.setDocumento((BigDecimal) obj[1]);
                item.setOperacao((String) obj[2]);
                item.setHistorico((String) obj[3]);
                item.setNumeroDoctCont((String) obj[4]);
                item.setOrgUni((String) obj[5]);
                item.setSaldo((BigDecimal) obj[6]);
                item.setCont((BigDecimal) obj[7]);
                item.setUnidade((String) obj[8]);
                item.setOrgao((String) obj[9]);
                item.setUnidadeGestora((String) obj[10]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public DividaPublicaFacade getDividaPublicaFacade() {
        return dividaPublicaFacade;
    }
}
