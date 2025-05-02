package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.AplicacaoFinanceiraItem;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.negocios.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 25/06/14
 * Time: 08:42
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RelatorioAplicacaoFinanceiraFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public RelatorioAplicacaoFinanceiraFacade() {
    }

    public List<AplicacaoFinanceiraItem> recuperaRelatorio(String apresentacao, Boolean pesquisouUg, List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DESCRICAO, ")
                .append("        COALESCE(sum(APLICACAO), 0) AS APLICACAO, ")
                .append("        COALESCE(sum(RESGATE), 0) AS RESGATE, ")
                .append("        COALESCE(sum(RENDIMENTO), 0) AS RENDIMENTO, ")
                .append("        COALESCE(sum(SALDO), 0) AS SALDO, ")
                .append("        CODIGOUNIDADE, ")
                .append("        unidade, ")
                .append("        orgao, ")
                .append("        unidade_gestora, ")
                .append("        1 AS CONT ")
                .append("   FROM ( ")
                .append(" SELECT SUB.ID, ")
                .append("        SUB.DESCRICAO, ")
                .append("        COALESCE(TRANS.SALDO,0) AS APLICACAO, ")
                .append("        0 AS RESGATE, ")
                .append("        0 AS RENDIMENTO, ")
                .append("        0 AS SALDO, ")
                .append("        SUBSTR(VW.CODIGO, 4, 7) AS CODIGOUNIDADE, ")
                .append("        VW.codigo || ' - ' || VW.descricao as unidade, ")
                .append("        vworg.codigo || ' - ' || vworg.descricao as orgao, ");
        sql.append(apresentacao.equals("UNIDADE_GESTORA") ? " ug.codigo || ' - ' || ug.descricao as unidade_gestora " : " '' as unidade_gestora ");
        sql.append("  FROM TRANSFERENCIAMESMAUNIDADE TRANS ")
                .append(" INNER JOIN SUBCONTA SUB ON TRANS.SUBCONTADEPOSITO_ID = SUB.ID ")
                .append(" INNER JOIN CONTABANCARIAENTIDADE CONTABANC ON SUB.CONTABANCARIAENTIDADE_ID = CONTABANC.ID ")
                .append(" INNER JOIN AGENCIA AGENCIA ON CONTABANC.AGENCIA_ID = AGENCIA.ID ")
                .append(" INNER JOIN BANCO BANCO ON AGENCIA.BANCO_ID = BANCO.ID ")
                .append(" INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = TRANS.UNIDADEORGANIZACIONAL_ID ")
                .append(" inner join vwhierarquiaorcamentaria vworg on VW.superior_id = vworg.subordinada_id ");
        sql.append(apresentacao.equals("UNIDADE_GESTORA") || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "");
        sql.append(" WHERE TIPOTRANSFERENCIA = 'APLICACAO' and  trunc(traNs.DATATRANSFERENCIA)BETWEEN to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ")
                .append("   AND trunc(traNs.DATATRANSFERENCIA)BETWEEN VW.iniciovigencia and coalesce(VW.fimvigencia, trunc(traNs.DATATRANSFERENCIA)) ")
                .append("   and trunc(traNs.DATATRANSFERENCIA)between vworg.iniciovigencia and coalesce(vworg.fimvigencia, trunc(traNs.DATATRANSFERENCIA)) ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" union all ")
                .append(" SELECT SUB.ID, ")
                .append("        SUB.DESCRICAO, ")
                .append("        0 AS APLICACAO, ")
                .append("        COALESCE(TRANS.SALDO,0) AS RESGATE, ")
                .append("        0 AS RENDIMENTO, ")
                .append("        0 AS SALDO, ")
                .append("        SUBSTR(VW.CODIGO, 4, 7) AS CODIGOUNIDADE, ")
                .append("        VW.codigo || ' - ' || VW.descricao as unidade, ")
                .append("        vworg.codigo || ' - ' || vworg.descricao as orgao, ");
        sql.append(apresentacao.equals("UNIDADE_GESTORA") ? " ug.codigo || ' - ' || ug.descricao as unidade_gestora " : " '' as unidade_gestora ");
        sql.append("   FROM TRANSFERENCIAMESMAUNIDADE TRANS ")
                .append("  INNER JOIN SUBCONTA SUB ON TRANS.SUBCONTARETIRADA_ID = SUB.ID AND SUB.TIPOCONTAFINANCEIRA = 'APLICACAO' ")
                .append("  INNER JOIN CONTABANCARIAENTIDADE CONTABANC ON SUB.CONTABANCARIAENTIDADE_ID = CONTABANC.ID ")
                .append("  INNER JOIN AGENCIA AGENCIA ON CONTABANC.AGENCIA_ID = AGENCIA.ID ")
                .append("  INNER JOIN BANCO BANCO ON AGENCIA.BANCO_ID = BANCO.ID ")
                .append("  inner JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = TRANS.UNIDADEORGANIZACIONAL_ID ")
                .append("  inner join vwhierarquiaorcamentaria vworg on VW.superior_id = vworg.subordinada_id ");
        sql.append(apresentacao.equals("UNIDADE_GESTORA") || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "");
        sql.append(" WHERE TIPOTRANSFERENCIA = 'RESGATE' and trunc(traNs.DATATRANSFERENCIA)BETWEEN to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ")
                .append("    AND trunc(traNs.DATATRANSFERENCIA)BETWEEN VW.iniciovigencia and coalesce(VW.fimvigencia, trunc(traNs.DATATRANSFERENCIA)) ")
                .append("    and trunc(traNs.DATATRANSFERENCIA)between vworg.iniciovigencia and coalesce(vworg.fimvigencia, trunc(traNs.DATATRANSFERENCIA))  ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" union all ")
                .append(" SELECT SUB.ID, ")
                .append("        SUB.DESCRICAO, ")
                .append("        0 AS APLICACAO, ")
                .append("        0 AS RESGATE, ")
                .append("        COALESCE(REC.SALDO,0) AS RENDIMENTO, ")
                .append("        0 AS SALDO, ")
                .append("        SUBSTR(VW.CODIGO, 4, 7) AS CODIGOUNIDADE, ")
                .append("        VW.codigo || ' - ' || VW.descricao as unidade, ")
                .append("        vworg.codigo || ' - ' || vworg.descricao as orgao, ");
        sql.append(apresentacao.equals("UNIDADE_GESTORA") ? " ug.codigo || ' - ' || ug.descricao as unidade_gestora " : " '' as unidade_gestora ");
        sql.append(" FROM LANCAMENTORECEITAORC REC ")
                .append("  INNER JOIN SUBCONTA SUB ON SUB.ID = REC.SUBCONTA_ID AND SUB.TIPOCONTAFINANCEIRA = 'APLICACAO' ")
                .append("  INNER JOIN CONTABANCARIAENTIDADE CONTABANC ON SUB.CONTABANCARIAENTIDADE_ID = CONTABANC.ID ")
                .append("  INNER JOIN AGENCIA AGENCIA ON CONTABANC.AGENCIA_ID = AGENCIA.ID ")
                .append("  INNER JOIN BANCO BANCO ON AGENCIA.BANCO_ID = BANCO.ID ")
                .append("  inner JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = REC.UNIDADEORGANIZACIONAL_ID ")
                .append("  inner join vwhierarquiaorcamentaria vworg on VW.superior_id = vworg.subordinada_id ");
        sql.append(apresentacao.equals("UNIDADE_GESTORA") || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "");
        sql.append("  WHERE trunc(REC.DATALANCAMENTO) between VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(REC.DATALANCAMENTO)) ")
                .append("  and trunc(REC.DATALANCAMENTO) BETWEEN to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ")
                .append("  and trunc(REC.DATALANCAMENTO) between vworg.iniciovigencia and coalesce(vworg.fimvigencia, trunc(REC.DATALANCAMENTO))  ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append("  union all ")
                .append(" SELECT  SUB.ID, ")
                .append("        SUB.DESCRICAO, ")
                .append("        0 AS APLICACAO, ")
                .append("        0 AS RESGATE, ")
                .append("        0 AS RENDIMENTO, ")
                .append("        (SALDO.TOTALCREDITO - SALDO.TOTALDEBITO) AS SALDO, ")
                .append("        SUBSTR(VW.CODIGO, 4, 7) AS CODIGOUNIDADE, ")
                .append("        VW.codigo || ' - ' || VW.descricao as unidade, ")
                .append("        vworg.codigo || ' - ' || vworg.descricao as orgao, ");
        sql.append(apresentacao.equals("UNIDADE_GESTORA") ? " ug.codigo || ' - ' || ug.descricao as unidade_gestora " : " '' as unidade_gestora ");
        sql.append("   FROM SALDOSUBCONTA SALDO ")
                .append("  INNER JOIN SUBCONTA SUB ON SUB.ID = SALDO.SUBCONTA_ID AND SUB.TIPOCONTAFINANCEIRA = 'APLICACAO' ")
                .append("  INNER JOIN CONTABANCARIAENTIDADE CONTABANC ON SUB.CONTABANCARIAENTIDADE_ID = CONTABANC.ID ")
                .append("  INNER JOIN AGENCIA AGENCIA ON CONTABANC.AGENCIA_ID = AGENCIA.ID ")
                .append("  INNER JOIN BANCO BANCO ON AGENCIA.BANCO_ID = BANCO.ID ")
                .append("  inner JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = SALDO.UNIDADEORGANIZACIONAL_ID ")
                .append("  inner join vwhierarquiaorcamentaria vworg on VW.superior_id = vworg.subordinada_id ");
        sql.append(apresentacao.equals("UNIDADE_GESTORA") || pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "");
        sql.append("  WHERE trunc(SALDO.DATASALDO) = ( SELECT trunc(MAX(sld.DATASALDO)) FROM SALDOSUBCONTA sld ")
                .append("  INNER JOIN SUBCONTA SUB ON SUB.ID = sld.SUBCONTA_ID AND SUB.TIPOCONTAFINANCEIRA = 'APLICACAO' ")
                .append("  WHERE trunc(sld.DATASALDO) <= to_date(:dataFinal, 'dd/mm/yyyy') and sld.SUBCONTA_ID = saldo.SUBCONTA_ID and sld.UNIDADEORGANIZACIONAL_ID = saldo.UNIDADEORGANIZACIONAL_ID and sld.FONTEDERECURSOS_ID = saldo.FONTEDERECURSOS_ID ")
                .append("  ) and trunc(SALDO.DATASALDO) BETWEEN to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ")
                .append("    AND trunc(SALDO.DATASALDO) BETWEEN VW.iniciovigencia and coalesce(VW.fimvigencia, trunc(SALDO.DATASALDO)) ")
                .append("    and trunc(SALDO.DATASALDO) between vworg.iniciovigencia and coalesce(vworg.fimvigencia, trunc(SALDO.DATASALDO))  ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append("  ) group by DESCRICAO, CODIGOUNIDADE, unidade, orgao, unidade_gestora ");
        if (apresentacao.equals("UNIDADE_GESTORA")) {
            sql.append(" order by unidade_gestora, DESCRICAO ");
        } else {
            sql.append(" order by unidade, DESCRICAO ");
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
            List<AplicacaoFinanceiraItem> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                AplicacaoFinanceiraItem item = new AplicacaoFinanceiraItem();
                item.setDescricao((String) obj[0]);
                item.setAplicacao((BigDecimal) obj[1]);
                item.setResgate((BigDecimal) obj[2]);
                item.setRendimento((BigDecimal) obj[3]);
                item.setSaldo((BigDecimal) obj[4]);
                item.setCodigoUnidade((String) obj[5]);
                item.setUnidade((String) obj[6]);
                item.setOrgao((String) obj[7]);
                item.setUnidadeGestora((String) obj[8]);
                item.setCount((BigDecimal) obj[9]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }

    public AgenciaFacade getAgenciaFacade() {
        return agenciaFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
