package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.*;

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

/**
 * Created by Mateus on 15/12/2014.
 */
@Stateless
public class RelatorioTransparenciaFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public List<ReceitaTransparencia> recuperaRelatorioReceitas(HashMap parametros, Boolean filtrouUG) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT CODIGO, ")
            .append("        DESCRICAO, ")
            .append("        CASE SUBSTR(CODIGO, 1, 1) ")
            .append("            WHEN '9' THEN COALESCE(ORCADA_INI * - 1, 0) ")
            .append("            ELSE COALESCE(ORCADA_INI, 0) ")
            .append("        END AS ORCADA_INICIAL, ")
            .append("        CASE SUBSTR(CODIGO, 1, 1) ")
            .append("           WHEN '9' THEN COALESCE(ORCADA_ATU * - 1, 0 ) ")
            .append("           else COALESCE(ORCADA_ATU, 0 ) ")
            .append("        end AS ORCADA_ATUAL, ")
            .append("        CASE SUBSTR(CODIGO, 1, 1) ")
            .append("           WHEN '9' THEN COALESCE(RECEITA * - 1, 0) ")
            .append("           else COALESCE(RECEITA, 0) ")
            .append("        END AS ARRECADADO, ")
            .append("        NIVELESTRUTURA(CODIGO, '.') AS NIVEL ")
            .append("   FROM ( ")
            .append("   WITH ITENS(ID, ")
            .append("        CODIGO, ")
            .append("        DESCRICAO, ")
            .append("        SUPERIOR_ID, ")
            .append("        ORCADA_INI, ")
            .append("        ORCADA_ATU, ")
            .append("        RECEITA) AS ( ")
            .append(" SELECT CONTA_ID, ")
            .append("        CONTA_COD, ")
            .append("        CONTA_DESC, ")
            .append("        CONTA_SUPER, ")
            .append("        ORCADA_INI, ")
            .append("        ORCADA_ATU, ")
            .append("        RECEITA ")
            .append("  FROM ")
            .append(" ( ")
            .append(" SELECT CONTA_ID, ")
            .append("        CONTA_COD, ")
            .append("        CONTA_DESC, ")
            .append("        CONTA_SUPER, ")
            .append("        coalesce(VALOR, 0) as ORCADA_ATU, ")
            .append("        0 as ORCADA_INI, ")
            .append("        0 AS RECEITA ")
            .append("  FROM( ")
            .append(" ")
            .append("  select CONTA_ID, ")
            .append("        CONTA_COD, ")
            .append("        CONTA_DESC, ")
            .append("        CONTA_SUPER, ")
            .append("        SUM(VALOR) as VALOR ")
            .append(" ")
            .append(" from ( ")
            .append(" SELECT ")
            .append("           C.ID AS CONTA_ID, ")
            .append("           C.CODIGO AS CONTA_COD, ")
            .append("           C.DESCRICAO AS CONTA_DESC, ")
            .append("           C.SUPERIOR_ID as CONTA_SUPER, ")
            .append("           COALESCE(RECLOA.VALOR, 0) AS VALOR ")
            .append("      FROM RECEITALOA RECLOA ")
            .append("      INNER JOIN LOA ON LOA.ID = RECLOA.LOA_ID ")
            .append("      INNER JOIN LDO ON LDO.ID = LOA.LDO_ID AND LDO.EXERCICIO_ID = :EXERCICIO_ID")
            .append("      INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = RECLOA.ENTIDADE_ID ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append("      INNER JOIN CONTA C ON RECLOA.CONTADERECEITA_ID = C.ID ")
            .append("      INNER JOIN PLANODECONTASEXERCICIO PCE ON PCE.PLANODERECEITAS_ID = C.PLANODECONTAS_ID AND PCE.EXERCICIO_ID = :EXERCICIO_ID")
            .append(" where trunc(RECLOA.DATAREGISTRO) between VW.INICIOVIGENCIA and coalesce(VW.FIMVIGENCIA, trunc(RECLOA.DATAREGISTRO)) ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append("  union all ")
            .append("  SELECT ")
            .append("           C.ID AS CONTA_ID, ")
            .append("           C.CODIGO AS CONTA_COD, ")
            .append("           C.DESCRICAO AS CONTA_DESC, ")
            .append("           C.SUPERIOR_ID as CONTA_SUPER, ")
            .append("           coalesce(EST.VALOR, 0) * -1 as VALOR ")
            .append("      FROM ESTORNORECEITALOA EST ")
            .append("      INNER JOIN RECEITALOA RECLOA ON EST.RECEITALOA_ID = RECLOA.Id ")
            .append("      INNER JOIN LOA ON LOA.ID = RECLOA.LOA_ID ")
            .append("      INNER JOIN LDO ON LDO.ID = LOA.LDO_ID AND LDO.EXERCICIO_ID = :EXERCICIO_ID")
            .append("      INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = RECLOA.ENTIDADE_ID ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append("      INNER JOIN CONTA C ON RECLOA.CONTADERECEITA_ID = C.ID ")
            .append("      INNER JOIN PLANODECONTASEXERCICIO PCE ON PCE.PLANODERECEITAS_ID = C.PLANODECONTAS_ID AND PCE.EXERCICIO_ID = :EXERCICIO_ID")
            .append(" where trunc(EST.dataestorno) between VW.INICIOVIGENCIA and coalesce(VW.FIMVIGENCIA, trunc(EST.dataestorno)) ")
            .append(" AND trunc(est.dataEstorno) between TO_DATE(:DATAINICIAL, 'dd/mm/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append("   ) ")
            .append(" GROUP BY CONTA_ID, ")
            .append("        CONTA_COD, ")
            .append("        CONTA_DESC, ")
            .append("        CONTA_SUPER ")
            .append(" UNION ALL ")
            .append(" SELECT ")
            .append("     C.ID AS CONTA_ID, ")
            .append("     C.CODIGO AS CONTA_COD, ")
            .append("     C.DESCRICAO AS CONTA_DESC, ")
            .append("     C.SUPERIOR_ID AS CONTA_SUPER, ")
            .append("     (COALESCE(DECODE(ALT.TIPOALTERACAOORC, 'PREVISAO', ALT.VALOR, ALT.VALOR * (-1)), 0)) AS VALOR ")
            .append(" FROM RECEITALOA REC ")
            .append(" LEFT JOIN RECEITAALTERACAOORC ALT ON REC.ID = ALT.RECEITALOA_ID ")
            .append(" LEFT JOIN ALTERACAOORC ALTORC ON ALTORC.ID = ALT.ALTERACAOORC_ID ")
            .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = REC.ENTIDADE_ID ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" INNER JOIN CONTA C ON REC.CONTADERECEITA_ID = C.ID ")
            .append(" INNER JOIN PLANODECONTASEXERCICIO PCE ON PCE.PLANODERECEITAS_ID = C.PLANODECONTAS_ID AND PCE.EXERCICIO_ID = :EXERCICIO_ID")
            .append(" WHERE trunc(ALTORC.DATAALTERACAO) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(ALTORC.DATAALTERACAO)) ")
            .append("  and trunc(ALTORC.DATAALTERACAO) between TO_DATE(:DATAINICIAL, 'dd/mm/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" ) ORCADA_ATUAL ")
            .append(" union all ")
            .append(" select CONTA_ID, ")
            .append("        CONTA_COD, ")
            .append("        CONTA_DESC, ")
            .append("        CONTA_SUPER, ")
            .append("        0 as ORCADA_ATU, ")
            .append("        coalesce(ORCADA_INI, 0) as ORCADA_INI, ")
            .append("        0 AS RECEITA ")
            .append(" from ( ")
            .append(" select CONTA_ID, ")
            .append("        CONTA_COD, ")
            .append("        CONTA_DESC, ")
            .append("        CONTA_SUPER, ")
            .append("        SUM(VALOR) AS ORCADA_INI ")
            .append(" from ( ")
            .append(" SELECT ")
            .append("           C.ID AS CONTA_ID, ")
            .append("           C.CODIGO AS CONTA_COD, ")
            .append("           C.DESCRICAO AS CONTA_DESC, ")
            .append("           C.SUPERIOR_ID as CONTA_SUPER, ")
            .append("           COALESCE(RECLOA.VALOR, 0) AS VALOR ")
            .append("      FROM RECEITALOA RECLOA ")
            .append("      INNER JOIN LOA ON LOA.ID = RECLOA.LOA_ID ")
            .append("      INNER JOIN LDO ON LDO.ID = LOA.LDO_ID AND LDO.EXERCICIO_ID = :EXERCICIO_ID")
            .append("      INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = RECLOA.ENTIDADE_ID ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append("      INNER JOIN CONTA C ON RECLOA.CONTADERECEITA_ID = C.ID ")
            .append("      INNER JOIN PLANODECONTASEXERCICIO PCE ON PCE.PLANODERECEITAS_ID = C.PLANODECONTAS_ID AND PCE.EXERCICIO_ID = :EXERCICIO_ID")
            .append(" where trunc(RECLOA.DATAREGISTRO)  between VW.INICIOVIGENCIA and coalesce(VW.FIMVIGENCIA, trunc(RECLOA.DATAREGISTRO)) ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append("  union all ")
            .append("  SELECT ")
            .append("           C.ID AS CONTA_ID, ")
            .append("           C.CODIGO AS CONTA_COD, ")
            .append("           C.DESCRICAO AS CONTA_DESC, ")
            .append("           C.SUPERIOR_ID as CONTA_SUPER, ")
            .append("           coalesce(EST.VALOR, 0) * -1 as VALOR ")
            .append("      FROM ESTORNORECEITALOA EST ")
            .append("      INNER JOIN RECEITALOA RECLOA ON EST.RECEITALOA_ID = RECLOA.Id ")
            .append("      INNER JOIN LOA ON LOA.ID = RECLOA.LOA_ID ")
            .append("      INNER JOIN LDO ON LDO.ID = LOA.LDO_ID AND LDO.EXERCICIO_ID = :EXERCICIO_ID")
            .append("      INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = RECLOA.ENTIDADE_ID ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append("      INNER JOIN CONTA C ON RECLOA.CONTADERECEITA_ID = C.ID ")
            .append("      INNER JOIN PLANODECONTASEXERCICIO PCE ON PCE.PLANODERECEITAS_ID = C.PLANODECONTAS_ID AND PCE.EXERCICIO_ID = :EXERCICIO_ID")
            .append(" where trunc(EST.DATAESTORNO)  between VW.INICIOVIGENCIA and coalesce(VW.FIMVIGENCIA, trunc(EST.DATAESTORNO)) ")
            .append(" AND trunc(est.dataEstorno) between TO_DATE(:DATAINICIAL, 'dd/mm/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" ) ")
            .append(" GROUP BY CONTA_ID, ")
            .append("        CONTA_COD, ")
            .append("        CONTA_DESC, ")
            .append("        CONTA_SUPER ")
            .append(" ) ORC_INICIAL ")
            .append(" union all ")
            .append(" select CONTA_ID, ")
            .append("        CONTA_COD, ")
            .append("        CONTA_DESC, ")
            .append("        CONTA_SUPER, ")
            .append("        0 as ORCADA_ATU, ")
            .append("        0 as ORCADA_INI, ")
            .append("        coalesce(receita, 0) AS RECEITA ")
            .append(" from ( ")
            .append(" select distinct CONTA_ID, ")
            .append(" SUPERIOR_ID as CONTA_SUPER, ")
            .append(" CODIGO as CONTA_COD, ")
            .append(" DESCRICAO as CONTA_DESC, ")
            .append(" sum(RECEITA) as receita ")
            .append("  from( ")
            .append(" select C.id as CONTA_ID, ")
            .append(" C.SUPERIOR_ID as SUPERIOR_ID, ")
            .append(" c.CODIGO as CODIGO, ")
            .append(" C.DESCRICAO as DESCRICAO, ")
            .append(" sum(LANC.VALOR) as receita ")
            .append("  FROM CONTA C ")
            .append("  INNER JOIN RECEITALOA RECEITA ON RECEITA.CONTADERECEITA_ID = C.ID ")
            .append("  INNER JOIN LANCAMENTORECEITAORC LANC ON LANC.RECEITALOA_ID = RECEITA.ID ")
            .append("  INNER JOIN LOA LO ON LO.ID = RECEITA.LOA_ID ")
            .append("  INNER JOIN LDO LD ON LD.ID = LO.LDO_ID ")
            .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = RECEITA.ENTIDADE_ID AND trunc(LANC.DATALANCAMENTO) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(LANC.DATALANCAMENTO)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append("  where trunc(LANC.DATALANCAMENTO) between TO_DATE(:DATAINICIAL, 'dd/mm/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append("  GROUP BY C.ID, ")
            .append("  C.SUPERIOR_ID, ")
            .append("  C.CODIGO, ")
            .append("  C.DESCRICAO ")
            .append(" UNION ALL ")
            .append("  select ")
            .append("  C.id as CONTA_ID, ")
            .append("  C.SUPERIOR_ID as SUPERIOR_ID, ")
            .append("  C.CODIGO, ")
            .append("  C.DESCRICAO, ")
            .append("  SUM(RLEST.VALOR) * -1 AS receita ")
            .append("  FROM CONTA C ")
            .append("  INNER JOIN RECEITALOA RECEITA ON RECEITA.CONTADERECEITA_ID = C.ID ")
            .append("  INNER JOIN RECEITAORCESTORNO RLEST ON  RECEITA.ID = RLEST.RECEITALOA_ID ")
            .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = RECEITA.ENTIDADE_ID AND trunc(RLEST.DATAESTORNO) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(RLEST.DATAESTORNO)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append("  where trunc(RLEST.DATAESTORNO) between TO_DATE(:DATAINICIAL, 'dd/mm/yyyy') AND TO_DATE(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append("  GROUP BY C.ID, ")
            .append("  C.SUPERIOR_ID, ")
            .append("  C.CODIGO, ")
            .append("  C.DESCRICAO ")
            .append(" ) itens ")
            .append(" group by CONTA_ID, ")
            .append(" SUPERIOR_ID, ")
            .append(" CODIGO, ")
            .append(" DESCRICAO ")
            .append(" ) ARREC ")
            .append(" ) ")
            .append("  UNION ALL ")
            .append(" SELECT SUP.ID, ")
            .append("        SUP.CODIGO, ")
            .append("        SUP.DESCRICAO, ")
            .append("        SUP.SUPERIOR_ID, ")
            .append("        IT.ORCADA_INI, ")
            .append("        IT.ORCADA_ATU, ")
            .append("        IT.RECEITA ")
            .append("   FROM ITENS IT ")
            .append("  INNER JOIN CONTA SUP ON IT.SUPERIOR_ID = SUP.ID ")
            .append("  ) ")
            .append("  SELECT DISTINCT ")
            .append("         CODIGO, ")
            .append("         DESCRICAO, ")
            .append("         SUM(ORCADA_INI) AS ORCADA_INI, ")
            .append("         SUM(ORCADA_ATU) AS ORCADA_ATU, ")
            .append("         SUM(RECEITA) AS RECEITA ")
            .append("    FROM ITENS ")
            .append("   GROUP BY CODIGO, DESCRICAO ")
            .append("   ORDER BY CODIGO, DESCRICAO) ")
            .append(" REG ")
            .append(" where (COALESCE(ORCADA_INI, 0) > 0 or COALESCE(ORCADA_ATU, 0 ) > 0 or COALESCE(RECEITA, 0) > 0) ");
        Query q = em.createNativeQuery(sql.toString());
        if (!parametros.get("SQL").equals("")) {
            q.setParameter("SQL", parametros.get("SQL"));
        }
        q.setParameter("EXERCICIO_ID", parametros.get("EXERCICIO_ID"));
        q.setParameter("DATAINICIAL", parametros.get("DATAINICIAL"));
        q.setParameter("DATAFINAL", parametros.get("DATAFINAL"));
        if (filtrouUG) {
            q.setParameter("UNIDADEGESTORA", parametros.get("UNIDADEGESTORA"));
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ReceitaTransparencia> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ReceitaTransparencia item = new ReceitaTransparencia();
                item.setCodigo((String) obj[0]);
                item.setDescricao((String) obj[1]);
                item.setOrcadaInicial((BigDecimal) obj[2]);
                item.setOrcadaAtual((BigDecimal) obj[3]);
                item.setArrecadado((BigDecimal) obj[4]);
                item.setNivel((BigDecimal) obj[5]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public List<AcompanhamentoLiberacaoFinanceira> recuperaRelatorioAcompanhamento(HashMap parametros, Boolean filtrouUG, Integer mesFinal) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select unidade, coalesce(sum(orcamento), 0) as orcamento, coalesce(sum(liberado_1mes), 0) as liberado_1mes, coalesce(sum(liberado_2mes), 0) as liberado_2mes, ")
            .append(" coalesce(sum(liberado_3mes), 0) as liberado_3mes, coalesce(sum(liberado_4mes), 0) as liberado_4mes, coalesce(sum(orcamento), 0) - coalesce(sum(totalMeses), 0) as saldoALiberar ")
            .append(" from ( ")
            .append(" select vw.codigo || ' - ' || vw.descricao as unidade, 0 as orcamento, sum(liberacao.valor) as liberado_1mes, 0 as liberado_2mes, 0 as liberado_3mes, 0 as liberado_4mes, 0 as totalMeses ")
            .append(" from liberacaocotafinanceira liberacao ")
            .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
            .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
            .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = solicitacao.unidadeorganizacional_id AND liberacao.dataliberacao BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, liberacao.dataliberacao) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" where to_char(liberacao.dataliberacao, 'mm/yyyy')  = :DATAGERACAO_1MES ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" group by vw.codigo, vw.descricao ")
            .append(" union all ")
            .append(" select vw.codigo || ' - ' || vw.descricao as unidade, 0 as orcamento, coalesce(sum(EST.VALOR), 0) * - 1 as liberado_1mes, 0 as liberado_2mes, 0 as liberado_3mes, 0 as liberado_4mes, 0 as totalMeses ")
            .append(" from ESTORNOLIBCOTAFINANCEIRA est ")
            .append(" INNER JOIN LIBERACAOCOTAFINANCEIRA liberacao ON EST.LIBERACAO_ID = liberacao.ID ")
            .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
            .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
            .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = solicitacao.unidadeorganizacional_id AND trunc(est.dataEstorno) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(est.dataestorno)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" where to_char(est.DATAESTORNO, 'mm/yyyy')  = :DATAGERACAO_1MES ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" group by vw.codigo, vw.descricao ")
            .append(" union all ");
        if (mesFinal.compareTo(2) >= 1) {
            sql.append(" select vw.codigo || ' - ' || vw.descricao as unidade, 0 as orcamento, 0 as liberado_1mes, sum(liberacao.valor) as liberado_2mes, 0 as liberado_3mes, 0 as liberado_4mes, 0 as totalMeses ")
                .append(" from liberacaocotafinanceira liberacao ")
                .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
                .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
                .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = solicitacao.unidadeorganizacional_id AND liberacao.dataliberacao BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, liberacao.dataliberacao) ")
                .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
                .append(" where to_char(liberacao.dataliberacao, 'mm/yyyy')  = :DATAGERACAO_2MES ")
                .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
                .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
                .append(" group by vw.codigo, vw.descricao ")
                .append(" ")
                .append(" union all ")
                .append(" select vw.codigo || ' - ' || vw.descricao as unidade, 0 as orcamento, 0 as liberado_1mes, coalesce(sum(EST.VALOR), 0) * - 1 as liberado_2mes, 0 as liberado_3mes, 0 as liberado_4mes, 0 as totalMeses ")
                .append(" from ESTORNOLIBCOTAFINANCEIRA est ")
                .append(" INNER JOIN LIBERACAOCOTAFINANCEIRA liberacao ON EST.LIBERACAO_ID = liberacao.ID ")
                .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
                .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
                .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = solicitacao.unidadeorganizacional_id AND trunc(est.dataEstorno) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(est.dataestorno)) ")
                .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
                .append(" where to_char(est.DATAESTORNO, 'mm/yyyy')  = :DATAGERACAO_2MES ")
                .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
                .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
                .append(" group by vw.codigo, vw.descricao ")
                .append(" union all ");
        }
        if (mesFinal.compareTo(3) >= 1) {
            sql.append(" select vw.codigo || ' - ' || vw.descricao as unidade, 0 as orcamento, 0 as liberado_1mes, 0 as liberado_2mes, sum(liberacao.valor) as liberado_3mes, 0 as liberado_4mes, 0 as totalMeses ")
                .append(" from liberacaocotafinanceira liberacao ")
                .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
                .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
                .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = solicitacao.unidadeorganizacional_id AND liberacao.dataliberacao BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, liberacao.dataliberacao) ")
                .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
                .append(" where to_char(liberacao.dataliberacao, 'mm/yyyy')  = :DATAGERACAO_3MES ")
                .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
                .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
                .append(" group by vw.codigo, vw.descricao ")
                .append(" union all ")
                .append(" select vw.codigo || ' - ' || vw.descricao as unidade, 0 as orcamento, 0 as liberado_1mes, 0 as liberado_2mes, coalesce(sum(EST.VALOR), 0) * - 1 as liberado_3mes, 0 as liberado_4mes, 0 as totalMeses ")
                .append(" from ESTORNOLIBCOTAFINANCEIRA est ")
                .append(" INNER JOIN LIBERACAOCOTAFINANCEIRA liberacao ON EST.LIBERACAO_ID = liberacao.ID ")
                .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
                .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
                .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = solicitacao.unidadeorganizacional_id AND trunc(est.dataEstorno) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(est.dataestorno)) ")
                .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
                .append(" where to_char(est.DATAESTORNO, 'mm/yyyy')  = :DATAGERACAO_3MES ")
                .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
                .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
                .append(" group by vw.codigo, vw.descricao ")
                .append(" union all ");
        }
        if (mesFinal.compareTo(4) >= 1) {
            sql.append(" select vw.codigo || ' - ' || vw.descricao as unidade, 0 as orcamento, 0 as liberado_1mes, 0 as liberado_2mes, 0 as liberado_3mes, sum(liberacao.valor) as liberado_4mes, 0 as totalMeses ")
                .append(" from liberacaocotafinanceira liberacao ")
                .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
                .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
                .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = solicitacao.unidadeorganizacional_id AND liberacao.dataliberacao BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, liberacao.dataliberacao) ")
                .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
                .append(" where to_char(liberacao.dataliberacao, 'mm/yyyy')  = :DATAGERACAO_4MES ")
                .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
                .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
                .append(" group by vw.codigo, vw.descricao ")
                .append(" union all ")
                .append(" select vw.codigo || ' - ' || vw.descricao as unidade, 0 as orcamento, 0 as liberado_1mes, 0 as liberado_2mes, 0 as liberado_3mes, coalesce(sum(EST.VALOR), 0) * - 1 as liberado_4mes, 0 as totalMeses ")
                .append(" from ESTORNOLIBCOTAFINANCEIRA est ")
                .append(" INNER JOIN LIBERACAOCOTAFINANCEIRA liberacao ON EST.LIBERACAO_ID = liberacao.ID ")
                .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
                .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
                .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = solicitacao.unidadeorganizacional_id AND trunc(est.dataEstorno) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(est.dataestorno)) ")
                .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
                .append(" where to_char(est.DATAESTORNO, 'mm/yyyy')  = :DATAGERACAO_4MES ")
                .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
                .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
                .append(" group by vw.codigo, vw.descricao ")
                .append(" union all ")
                .append(" select vw.codigo || ' - ' || vw.descricao as unidade, 0 as orcamento, 0 as liberado_1mes, 0 as liberado_2mes, 0 as liberado_3mes, 0 as liberado_4mes, sum(liberacao.valor) as totalMeses ")
                .append(" from liberacaocotafinanceira liberacao ")
                .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
                .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
                .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = solicitacao.unidadeorganizacional_id AND liberacao.dataliberacao BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, liberacao.dataliberacao) ")
                .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
                .append(" where to_char(liberacao.dataliberacao, 'mm/yyyy') <= :MESFINAL and liberacao.EXERCICIO_ID = :EXERCICIO_ID ")
                .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
                .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
                .append(" group by vw.codigo, vw.descricao ")
                .append(" union all ");
        }
        sql.append(" select vw.codigo || ' - ' || vw.descricao as unidade, 0 as orcamento, 0 as liberado_1mes, 0 as liberado_2mes, 0 as liberado_3mes, 0 as liberado_4mes, coalesce(sum(EST.VALOR), 0) * - 1 as totalMeses ")
            .append(" from ESTORNOLIBCOTAFINANCEIRA est ")
            .append(" INNER JOIN LIBERACAOCOTAFINANCEIRA liberacao ON EST.LIBERACAO_ID = liberacao.ID ")
            .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
            .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
            .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = solicitacao.unidadeorganizacional_id AND trunc(est.dataEstorno) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(est.dataestorno)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" where to_char(est.DATAESTORNO, 'mm/yyyy') <= :MESFINAL and liberacao.EXERCICIO_ID = :EXERCICIO_ID ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" group by vw.codigo, vw.descricao ")
            .append(" union all ")
            .append(" SELECT vw.codigo || ' - ' || vw.descricao as unidade, COALESCE(sum(A.DOTACAO), 0) as orcamento, 0 as liberado_1mes, 0 as liberado_2mes, 0 as liberado_3mes, 0 as liberado_4mes, 0 as totalMeses ")
            .append("  FROM SALDOFONTEDESPESAORC A ")
            .append(" INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID ")
            .append(" INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID ")
            .append(" inner join PROVISAOPPAFONTE ppf on b.PROVISAOPPAFONTE_ID = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id ")
            .append(" inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id ")
            .append(" INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID ")
            .append(" INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID ")
            .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = a.unidadeorganizacional_id  ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" INNER JOIN ")
            .append("  (SELECT A.FONTEDESPESAORC_ID AS FONTE, MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A ")
            .append("  GROUP BY A.FONTEDESPESAORC_ID) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND A.DATASALDO = FONTES.MAXDATE ")
            .append(" where to_date(:DATAGERACAO_1MES,'mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:MESFINAL,'mm/yyyy')) ")
            .append("   and C.EXERCICIO_ID = :EXERCICIO_ID  ")
            .append(!parametros.get("SQL").equals("") ? " :SQL " : "")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" group by vw.codigo, vw.descricao ")
            .append(" ) ")
            .append(" group by unidade ")
            .append(" order by unidade ");
        Query q = em.createNativeQuery(sql.toString());
        if (!parametros.get("SQL").equals("")) {
            q.setParameter("SQL", parametros.get("SQL"));
        }
        q.setParameter("EXERCICIO_ID", parametros.get("EXERCICIO_ID"));
        q.setParameter("DATAGERACAO_1MES", parametros.get("DATAGERACAO_1MES"));
        if (mesFinal.compareTo(2) >= 1) {
            q.setParameter("DATAGERACAO_2MES", parametros.get("DATAGERACAO_2MES"));
        }
        if (mesFinal.compareTo(3) >= 1) {
            q.setParameter("DATAGERACAO_3MES", parametros.get("DATAGERACAO_3MES"));
        }
        if (mesFinal.compareTo(4) >= 1) {
            q.setParameter("DATAGERACAO_4MES", parametros.get("DATAGERACAO_4MES"));
        }
        q.setParameter("MESFINAL", parametros.get("MESFINAL"));
        if (filtrouUG) {
            q.setParameter("UNIDADEGESTORA", parametros.get("UNIDADEGESTORA"));
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<AcompanhamentoLiberacaoFinanceira> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                AcompanhamentoLiberacaoFinanceira item = new AcompanhamentoLiberacaoFinanceira();
                item.setUnidade((String) obj[0]);
                item.setOrcamento((BigDecimal) obj[1]);
                item.setLiberado_1mes((BigDecimal) obj[2]);
                item.setLiberado_2mes((BigDecimal) obj[3]);
                item.setLiberado_3mes((BigDecimal) obj[4]);
                item.setLiberado_4mes((BigDecimal) obj[5]);
                item.setSaldoALiberar((BigDecimal) obj[6]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public List<ComprasLiquidacaoTransparencia> recuperaRelatorioComprasLiquidacao(HashMap parametros, Boolean
        filtrouUG) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select 1 as conta, ")
            .append("        cast(liq.dataliquidacao as Date) as dataliquidacao, ")
            .append("        liq.numero as liq_numero, ")
            .append("        emp.numero as emp_numero, ")
            .append("        desp.codigo as funcional, ")
            .append("        c.codigo as conta_codigo, ")
            .append("        fr.codigo as fonte, ")
            .append("        coalesce(pf.cpf, pj.cnpj) as pessoa, ")
            .append("        liq.valor as valor_liquidado, ")
            .append("        liq.historiconota, ")
            .append("        vw.codigo || ' - ' || vw.descricao as unidade, ")
            .append("        vworg.codigo || ' - ' || vworg.descricao as orgao, ")
            .append("        liq.id as liq_id ")
            .append(" from liquidacao liq ")
            .append(" inner join empenho emp on liq.empenho_id = emp.id ")
            .append(" INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = EMP.FONTEDESPESAORC_ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE PPF ON PPF.ID = FONT.PROVISAOPPAFONTE_ID ")
            .append(" INNER join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" INNER join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vw.iniciovigencia  and coalesce(vw.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vworg.iniciovigencia  and coalesce(vworg.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" inner join despesaorc desp on emp.despesaorc_id = desp.id ")
            .append(" inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id ")
            .append(" inner join conta c on provdesp.CONTADEDESPESA_ID = c.id ")
            .append(" inner join pessoa p on emp.fornecedor_id = p.id ")
            .append(" left join pessoafisica pf on p.id = pf.id ")
            .append(" left join pessoajuridica pj on p.id = pj.id ")
            .append(" where trunc(liq.dataliquidacao) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" and emp.exercicio_id = :EXERCICIO_ID ")
            .append(" order by vw.codigo, trunc(liq.dataliquidacao) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("EXERCICIO_ID", parametros.get("EXERCICIO_ID"));
        q.setParameter("DATAINICIAL", parametros.get("DATAINICIAL"));
        q.setParameter("DATAFINAL", parametros.get("DATAFINAL"));
        if (filtrouUG) {
            q.setParameter("UNIDADEGESTORA", parametros.get("UNIDADEGESTORA"));
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ComprasLiquidacaoTransparencia> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ComprasLiquidacaoTransparencia item = new ComprasLiquidacaoTransparencia();
                item.setQuantidade((BigDecimal) obj[0]);
                item.setDataLiquidacao((Date) obj[1]);
                item.setNumeroLiquidacao((String) obj[2]);
                item.setNumeroEmpenho((String) obj[3]);
                item.setFuncional((String) obj[4]);
                item.setContaCodigo((String) obj[5]);
                item.setFonte((String) obj[6]);
                item.setPessoa((String) obj[7]);
                item.setValorLiquidacao((BigDecimal) obj[8]);
                item.setHistoricoNota((String) obj[9]);
                item.setUnidade((String) obj[10]);
                item.setOrgao((String) obj[11]);
                item.setDoctos(recuperaRelatorioComprasDoctos(((BigDecimal) obj[12]).longValue()));
                item.setDetalhamentos(recuperaRelatorioComprasDesdobramento(((BigDecimal) obj[12]).longValue()));
                retorno.add(item);
            }
            return retorno;
        }
    }

    public List<ComprasDoctoTransparencia> recuperaRelatorioComprasDoctos(Long liquidacaoId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select dcto.numero ")
            .append(" from  liquidacao liq ")
            .append(" inner join liquidacaodoctofiscal d on d.liquidacao_id = liq.id ")
            .append(" inner join doctofiscalliquidacao dcto on dcto.id = d.doctofiscalliquidacao_id ")
            .append(" where liq.id = :LIQ_ID ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("LIQ_ID", liquidacaoId);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ComprasDoctoTransparencia> retorno = new ArrayList<>();
            for (String obj : (List<String>) q.getResultList()) {
                ComprasDoctoTransparencia item = new ComprasDoctoTransparencia();
                item.setDescricao(obj);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public List<ComprasDetalhamentoTransparencia> recuperaRelatorioComprasDesdobramento(Long liquidacaoId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select desd.VALOR, c.codigo, c.descricao ")
            .append(" from desdobramento desd ")
            .append(" left join conta c on desd.conta_id = c.id ")
            .append(" where desd.LIQUIDACAO_ID = :LIQ_ID ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("LIQ_ID", liquidacaoId);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ComprasDetalhamentoTransparencia> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ComprasDetalhamentoTransparencia item = new ComprasDetalhamentoTransparencia();
                item.setValor((BigDecimal) obj[0]);
                item.setCodigo((String) obj[1]);
                item.setDescricao((String) obj[2]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public List<ComprasElementoDespesaTransparencia> recuperaRelatorioComprasElementoDespesa(HashMap
                                                                                                 parametros, Boolean filtrouUG) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select sum(desd.VALOR) as valor, c.codigo, c.descricao, sup.codigo as cod_superior ")
            .append(" from desdobramento desd ")
            .append(" inner join conta c on desd.conta_id = c.id ")
            .append(" inner join conta sup on c.superior_id = sup.id ")
            .append(" inner join liquidacao liq on desd.liquidacao_id = liq.id ")
            .append(" inner join empenho emp on emp.id = liq.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = EMP.FONTEDESPESAORC_ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE PPF ON PPF.ID = FONT.PROVISAOPPAFONTE_ID ")
            .append(" INNER join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" INNER join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vw.iniciovigencia  and coalesce(vw.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vworg.iniciovigencia  and coalesce(vworg.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" inner join despesaorc desp on emp.despesaorc_id = desp.id ")
            .append(" inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id ")
            .append(" inner join pessoa p on emp.fornecedor_id = p.id ")
            .append(" left join pessoafisica pf on p.id = pf.id ")
            .append(" left join pessoajuridica pj on p.id = pj.id ")
            .append(" where trunc(liq.dataliquidacao) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(" and emp.exercicio_id = :EXERCICIO_ID ")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" group by c.codigo, c.descricao, sup.codigo ")
            .append(" order by sup.codigo, c.codigo ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("EXERCICIO_ID", parametros.get("EXERCICIO_ID"));
        q.setParameter("DATAINICIAL", parametros.get("DATAINICIAL"));
        q.setParameter("DATAFINAL", parametros.get("DATAFINAL"));
        if (filtrouUG) {
            q.setParameter("UNIDADEGESTORA", parametros.get("UNIDADEGESTORA"));
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ComprasElementoDespesaTransparencia> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ComprasElementoDespesaTransparencia item = new ComprasElementoDespesaTransparencia();
                item.setValor((BigDecimal) obj[0]);
                item.setCodigo((String) obj[1]);
                item.setDescricao((String) obj[2]);
                item.setCodigoSuperior((String) obj[3]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public List<ComprasAnulacoesTransparencia> recuperaRelatorioComprasAnulacoes(HashMap parametros, Boolean
        filtrouUG) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select cast(est.dataestorno as Date) as dataestorno, ")
            .append("        est.numero as est_numero, ")
            .append("        emp.numero as emp_numero, ")
            .append("        desp.codigo as funcional, ")
            .append("        c.codigo as conta_codigo, ")
            .append("        fr.codigo as fonte, ")
            .append("        coalesce(pf.nome, pj.nomefantasia) as pessoa, ")
            .append("        est.valor as valor_estorno ")
            .append(" from liquidacaoestorno est ")
            .append(" inner join liquidacao liq on est.liquidacao_id = liq.id ")
            .append(" inner join empenho emp on emp.id = liq.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = EMP.FONTEDESPESAORC_ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE PPF ON PPF.ID = FONT.PROVISAOPPAFONTE_ID ")
            .append(" INNER join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" INNER join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id ")
            .append("        and trunc(est.dataEstorno) between vw.iniciovigencia  and coalesce(vw.fimvigencia, trunc(est.dataestorno)) ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("        and trunc(est.dataEstorno) between vworg.iniciovigencia  and coalesce(vworg.fimvigencia, trunc(est.dataestorno)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" inner join despesaorc desp on emp.despesaorc_id = desp.id ")
            .append(" inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id ")
            .append(" inner join conta c on provdesp.CONTADEDESPESA_ID = c.id ")
            .append(" inner join pessoa p on emp.fornecedor_id = p.id ")
            .append(" left join pessoafisica pf on p.id = pf.id ")
            .append(" left join pessoajuridica pj on p.id = pj.id ")
            .append(" where trunc(est.dataEstorno) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" and emp.exercicio_id = :EXERCICIO_ID ")
            .append(" order by trunc(est.dataEstorno) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("EXERCICIO_ID", parametros.get("EXERCICIO_ID"));
        q.setParameter("DATAINICIAL", parametros.get("DATAINICIAL"));
        q.setParameter("DATAFINAL", parametros.get("DATAFINAL"));
        if (filtrouUG) {
            q.setParameter("UNIDADEGESTORA", parametros.get("UNIDADEGESTORA"));
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ComprasAnulacoesTransparencia> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ComprasAnulacoesTransparencia item = new ComprasAnulacoesTransparencia();
                item.setDataEstorno((Date) obj[0]);
                item.setNumeroEstorno((String) obj[1]);
                item.setNumeroEmpenho((String) obj[2]);
                item.setFuncional((String) obj[3]);
                item.setContaCodigo((String) obj[4]);
                item.setFonte((String) obj[5]);
                item.setPessoa((String) obj[6]);
                item.setValorEstorno((BigDecimal) obj[7]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public List<ComprasResumoGeralTransparencia> recuperaRelatorioComprasResumoGeral(HashMap parametros, Boolean
        filtrouUG) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select sum(detalhado) as detalhado, ")
            .append("        sum(nao_detalhado) as nao_detalhado, ")
            .append("        sum(liquidacao_resto) as liquidacao_resto, ")
            .append("        sum(anulacao_anteriores) as anulacao_anteriores ")
            .append(" from ( ")
            .append(" select sum(desd.VALOR) as detalhado, ")
            .append("        0 as nao_detalhado, ")
            .append("        0 as liquidacao_resto, ")
            .append("        0 as anulacao_anteriores ")
            .append(" from desdobramento desd ")
            .append(" inner join liquidacao liq on desd.liquidacao_id = liq.id ")
            .append(" inner join empenho emp on emp.id = liq.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = EMP.FONTEDESPESAORC_ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE PPF ON PPF.ID = FONT.PROVISAOPPAFONTE_ID ")
            .append(" INNER join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" INNER join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vw.iniciovigencia  and coalesce(vw.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vworg.iniciovigencia  and coalesce(vworg.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" inner join despesaorc desp on emp.despesaorc_id = desp.id ")
            .append(" inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id ")
            .append(" inner join conta c on provdesp.CONTADEDESPESA_ID = c.id ")
            .append(" inner join pessoa p on emp.fornecedor_id = p.id ")
            .append(" left join pessoafisica pf on p.id = pf.id ")
            .append(" left join pessoajuridica pj on p.id = pj.id ")
            .append(" where trunc(liq.dataliquidacao) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(" and emp.exercicio_id = :EXERCICIO_ID ")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" union all ")
            .append(" select 0 as detalhado, ")
            .append("        0 as nao_detalhado, ")
            .append("        sum(liq.valor) as liquidacao_resto, ")
            .append("        0 as anulacao_anteriores ")
            .append(" from liquidacao liq ")
            .append(" inner join empenho emp on emp.id = liq.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = EMP.FONTEDESPESAORC_ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE PPF ON PPF.ID = FONT.PROVISAOPPAFONTE_ID ")
            .append(" INNER join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" INNER join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vw.iniciovigencia  and coalesce(vw.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vworg.iniciovigencia  and coalesce(vworg.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" inner join despesaorc desp on emp.despesaorc_id = desp.id ")
            .append(" inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id ")
            .append(" inner join conta c on provdesp.CONTADEDESPESA_ID = c.id ")
            .append(" inner join pessoa p on emp.fornecedor_id = p.id ")
            .append(" left join pessoafisica pf on p.id = pf.id ")
            .append(" left join pessoajuridica pj on p.id = pj.id ")
            .append(" where trunc(liq.dataliquidacao) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(" and liq.categoriaorcamentaria = 'RESTO' and emp.exercicio_id = :EXERCICIO_ID ")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" union all ")
            .append(" select 0 as detalhado, ")
            .append("        0 as nao_detalhado, ")
            .append("        0 as liquidacao_resto, ")
            .append("        sum(est.valor) as anulacao_anteriores ")
            .append(" from liquidacaoestorno est ")
            .append(" inner join liquidacao liq on est.liquidacao_id = liq.id ")
            .append(" inner join empenho emp on emp.id = liq.empenho_id ")
            .append(" INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = EMP.FONTEDESPESAORC_ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE PPF ON PPF.ID = FONT.PROVISAOPPAFONTE_ID ")
            .append(" INNER join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" INNER join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id ")
            .append("        and trunc(est.dataEstorno) between vw.iniciovigencia  and coalesce(vw.fimvigencia, trunc(est.dataestorno)) ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("        and trunc(est.dataEstorno) between vworg.iniciovigencia  and coalesce(vworg.fimvigencia, trunc(est.dataestorno)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" inner join despesaorc desp on emp.despesaorc_id = desp.id ")
            .append(" inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id ")
            .append(" inner join conta c on provdesp.CONTADEDESPESA_ID = c.id ")
            .append(" inner join pessoa p on emp.fornecedor_id = p.id ")
            .append(" left join pessoafisica pf on p.id = pf.id ")
            .append(" left join pessoajuridica pj on p.id = pj.id ")
            .append(" where trunc(est.dataEstorno) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') and emp.exercicio_id = :EXERCICIO_ID ")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" ) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("EXERCICIO_ID", parametros.get("EXERCICIO_ID"));
        q.setParameter("DATAINICIAL", parametros.get("DATAINICIAL"));
        q.setParameter("DATAFINAL", parametros.get("DATAFINAL"));
        if (filtrouUG) {
            q.setParameter("UNIDADEGESTORA", parametros.get("UNIDADEGESTORA"));
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ComprasResumoGeralTransparencia> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ComprasResumoGeralTransparencia item = new ComprasResumoGeralTransparencia();
                item.setDetalhado((BigDecimal) obj[0]);
                item.setNaoDetalhado((BigDecimal) obj[1]);
                item.setLiquidacaoResto((BigDecimal) obj[2]);
                item.setAnulacoesAnteriores((BigDecimal) obj[3]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public List<ComprasResumoOrgaoTransparencia> recuperaRelatorioComprasResumoOrgao(HashMap parametros, Boolean
        filtrouUG) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select sum(valor_liquidado) as valor_liquidado, ")
            .append("        sum(total_lancamentos) as total_lancamentos, ")
            .append("        sum(total_orgao) as total_orgao, ")
            .append("        sum(total_unidade) as total_unidade ")
            .append(" from ( ")
            .append(" select sum(liq.valor) as valor_liquidado, ")
            .append("        count(liq.id) as total_lancamentos, ")
            .append("        0 as total_orgao, ")
            .append("        0 as total_unidade ")
            .append(" from liquidacao liq ")
            .append(" inner join empenho emp on liq.empenho_id = emp.id ")
            .append(" INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = EMP.FONTEDESPESAORC_ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE PPF ON PPF.ID = FONT.PROVISAOPPAFONTE_ID ")
            .append(" INNER join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" INNER join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vw.iniciovigencia  and coalesce(vw.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vworg.iniciovigencia  and coalesce(vworg.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" inner join despesaorc desp on emp.despesaorc_id = desp.id ")
            .append(" inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id ")
            .append(" inner join conta c on provdesp.CONTADEDESPESA_ID = c.id ")
            .append(" inner join pessoa p on emp.fornecedor_id = p.id ")
            .append(" left join pessoafisica pf on p.id = pf.id ")
            .append(" left join pessoajuridica pj on p.id = pj.id ")
            .append(" where trunc(liq.dataliquidacao) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(" and emp.exercicio_id = :EXERCICIO_ID ")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" union all ")
            .append(" select 0 as valor_liquidado, ")
            .append("        0 as total_lancamentos, ")
            .append("        1 as total_orgao, ")
            .append("        0 as total_unidade ")
            .append(" from liquidacao liq ")
            .append(" inner join empenho emp on liq.empenho_id = emp.id ")
            .append(" INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = EMP.FONTEDESPESAORC_ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE PPF ON PPF.ID = FONT.PROVISAOPPAFONTE_ID ")
            .append(" INNER join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" INNER join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vw.iniciovigencia  and coalesce(vw.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vworg.iniciovigencia  and coalesce(vworg.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" inner join despesaorc desp on emp.despesaorc_id = desp.id ")
            .append(" inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id ")
            .append(" inner join conta c on provdesp.CONTADEDESPESA_ID = c.id ")
            .append(" inner join pessoa p on emp.fornecedor_id = p.id ")
            .append(" left join pessoafisica pf on p.id = pf.id ")
            .append(" left join pessoajuridica pj on p.id = pj.id ")
            .append(" where trunc(liq.dataliquidacao) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(" and emp.exercicio_id = :EXERCICIO_ID ")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" group by vworg.id ")
            .append(" union all ")
            .append(" select 0 as valor_liquidado, ")
            .append("        0 as total_lancamentos, ")
            .append("        0 as total_orgao, ")
            .append("        1 as total_unidade ")
            .append(" from liquidacao liq ")
            .append(" inner join empenho emp on liq.empenho_id = emp.id ")
            .append(" INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = EMP.FONTEDESPESAORC_ID ")
            .append(" INNER JOIN PROVISAOPPAFONTE PPF ON PPF.ID = FONT.PROVISAOPPAFONTE_ID ")
            .append(" INNER join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id ")
            .append(" INNER join fontederecursos fr on cd.fontederecursos_id = fr.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vw.iniciovigencia  and coalesce(vw.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("        and trunc(liq.dataliquidacao) between vworg.iniciovigencia  and coalesce(vworg.fimvigencia, trunc(liq.dataliquidacao)) ")
            .append(filtrouUG ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO_ID " : "")
            .append(" inner join despesaorc desp on emp.despesaorc_id = desp.id ")
            .append(" inner join provisaoppadespesa provdesp on desp.provisaoppadespesa_id = provdesp.id ")
            .append(" inner join conta c on provdesp.CONTADEDESPESA_ID = c.id ")
            .append(" inner join pessoa p on emp.fornecedor_id = p.id ")
            .append(" left join pessoafisica pf on p.id = pf.id ")
            .append(" left join pessoajuridica pj on p.id = pj.id ")
            .append(" where trunc(liq.dataliquidacao) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(" and emp.exercicio_id = :EXERCICIO_ID ")
            .append(filtrouUG ? " and ug.id = :UNIDADEGESTORA " : "")
            .append(" group by vw.id) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("EXERCICIO_ID", parametros.get("EXERCICIO_ID"));
        q.setParameter("DATAINICIAL", parametros.get("DATAINICIAL"));
        q.setParameter("DATAFINAL", parametros.get("DATAFINAL"));
        if (filtrouUG) {
            q.setParameter("UNIDADEGESTORA", parametros.get("UNIDADEGESTORA"));
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<ComprasResumoOrgaoTransparencia> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ComprasResumoOrgaoTransparencia item = new ComprasResumoOrgaoTransparencia();
                item.setTotalLiquidado((BigDecimal) obj[0]);
                item.setTotalLancamentos((BigDecimal) obj[1]);
                item.setTotalOrgao((BigDecimal) obj[2]);
                item.setTotalUnidade((BigDecimal) obj[3]);
                retorno.add(item);
            }
            return retorno;
        }
    }
}
