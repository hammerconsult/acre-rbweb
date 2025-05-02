package br.com.webpublico.relatoriofacade.administrativo.patrimonio;


import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.patrimonio.ParecerBaixaPatrimonialVO;
import br.com.webpublico.relatoriofacade.administrativo.RelatorioPatrimonioSuperFacade;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by zaca on 30/03/17.
 */
@Stateless
public class RelatorioParecerBaixaPatrimonialFacade extends RelatorioPatrimonioSuperFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public List<ParecerBaixaPatrimonialVO> emitirParecerBaixaPatrimonial(List<ParametrosRelatorios> filters) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append("  parecer.DATEPARECER                          AS dataParecer, ")
                .append("  parecer.CODIGO                               AS codigoParecer, ")
                .append("  parecer.JUSTIFICATIVA                        AS justificativaParecer, ")
                .append("  parecer.SITUACAOPARECER                      AS situacaoParecer, ")
                .append("  solicitacao.TIPOBAIXA                        AS tipoBaixaSolicitacao, ")
                .append("  solicitacao.TIPOBEM                          AS tipoBemSolicitacao, ")
                .append("  pfOrigem.NOME                                AS solicitante, ")
                .append("  pessoa_parecerista.NOME                      AS parecerista, ")
                .append("  trim(b.DESCRICAO)                            AS descricaoBem, ")
                .append("  b.IDENTIFICACAO                              AS codigoPatrimonio, ")
                .append("  b.DATAAQUISICAO                              AS dataAquisicaoBem, ")
                .append("  estado.estadobem                             AS estadoConservacaoBem, ")
                .append("  estado.VALORORIGINAL                         AS valorOriginal, ")
                .append("  estado.VALORACUMULADODEAJUSTE                AS valorAcumuladoAjuste, ")
                .append("  estado.VALORACUMULADODADEPRECIACAO           AS valorAcumuladoDepreciacao, ")
                .append("  estado.VALORACUMULADODAAMORTIZACAO           AS valorAcumuladoAmorizacao, ")
                .append("  estado.VALORACUMULADODAEXAUSTAO              AS valorAcumuladoExaustao, ")
                .append("  coalesce(estado.VALORORIGINAL, 0) - ")
                .append("  coalesce(estado.VALORACUMULADODEAJUSTE, 0) - ")
                .append("  coalesce(estado.VALORACUMULADODADEPRECIACAO, 0) - ")
                .append("  coalesce(estado.VALORACUMULADODAAMORTIZACAO, 0) - ")
                .append("  coalesce(estado.VALORACUMULADODAEXAUSTAO, 0) AS liquido, ")
                .append("  gb.CODIGO                                    AS codigoGrupoBem, ")
                .append("  gb.descricao                                 AS descricaoGrupoBem, ")
                .append("  vwadm.CODIGO || ' - ' || vwadm.DESCRICAO     AS administrativa, ")
                .append("  vworc.codigo || ' - ' || vworc.descricao     AS orcamentaria, ")
                .append("  coalesce(estado.VALORACUMULADODAAMORTIZACAO, 0) + ")
                .append("  coalesce(estado.VALORACUMULADODADEPRECIACAO, 0) + ")
                .append("  coalesce(estado.VALORACUMULADODAEXAUSTAO, 0) + ")
                .append("  coalesce(estado.VALORACUMULADODEAJUSTE, 0)   AS ajustes, ")
                .append("  solicitacao.DATASOLICITACAO                  AS dataSolicitacao, ")
                .append("  solicitacao.CODIGO                           AS codigoSolicitacao ")
                .append("FROM SOLICITABAIXAPATRIMONIAL solicitacao ")
                .append("  INNER JOIN PARECERBAIXAPATRIMONIAL parecer ON solicitacao.ID = parecer.SOLICITACAOBAIXA_ID ")
                .append("  INNER JOIN ITEMSOLICITBAIXAPATRIMONIO item ON ITEM.SOLICITACAOBAIXA_ID = solicitacao.id ")
                .append("  INNER JOIN EVENTOBEM ev ON ev.id = item.id ")
                .append("  INNER JOIN BEM b ON b.id = ev.bem_id ")
                .append("  INNER JOIN ESTADOBEM estado ON estado.id = ev.estadoresultante_id ")
                .append("  INNER JOIN VWHIERARQUIAADMINISTRATIVA vwadm ON vwadm.subordinada_id = estado.detentoraadministrativa_id ")
                .append("  INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.SUBORDINADA_ID = estado.DETENTORAORCAMENTARIA_ID ")
                .append("  INNER JOIN USUARIOSISTEMA us_origem ON us_origem.id = solicitacao.USUARIOSISTEMA_ID ")
                .append("  INNER JOIN PESSOAFISICA pfOrigem ON pfOrigem.ID = US_ORIGEM.PESSOAFISICA_ID ")
                .append("  INNER JOIN USUARIOSISTEMA parecerista ON parecer.PARECERISTA_ID = parecerista.ID ")
                .append("  INNER JOIN PESSOAFISICA pessoa_parecerista ON parecerista.PESSOAFISICA_ID = pessoa_parecerista.ID ")
                .append("  INNER JOIN GRUPOBEM gb ON gb.id = estado.grupobem_id ")
                .append("WHERE ")
                .append("  sysdate BETWEEN vwadm.INICIOVIGENCIA AND (coalesce(vwadm.FIMVIGENCIA, sysdate)) ")
                .append("  AND sysdate BETWEEN VWORC.INICIOVIGENCIA AND (coalesce(VWORC.FIMVIGENCIA, sysdate)) ")
                .append(concatenarParameters(filters, 1, " and "))
                .append("ORDER BY orcamentaria, gb.codigo, codigoPatrimonio ");

        Query q = getEm().createNativeQuery(sql.toString());

        adicionarParameters(filters, q);

        try {
            List<ParecerBaixaPatrimonialVO> parecerBaixaPatrimonialVOs = Lists.newArrayList();

            for (Object[] o : (List<Object[]>) q.getResultList()) {
                ParecerBaixaPatrimonialVO vo = new ParecerBaixaPatrimonialVO();
                vo.setDataParecer((Date) o[0]);
                vo.setCodigoParecer((Number) o[1]);
                vo.setJustificativaParecer((String) o[2]);
                vo.setSituacaoParecer((String) o[3]);
                vo.setTipoBaixaSolicitacao((String) o[4]);
                vo.setTipoBemSolicitacao((String) o[5]);
                vo.setSolicitante((String) o[6]);
                vo.setParecerista((String) o[7]);
                vo.setDescricaoBem((String) o[8]);
                vo.setCodigoPatrimonio((String) o[9]);
                vo.setDataAquisicaoBem((Date) o[10]);
                vo.setEstadoConservacaoBem((String) o[11]);
                vo.setValorOriginal((BigDecimal) o[12]);
                vo.setValorAcumuladoAjuste((BigDecimal) o[13]);
                vo.setValorAcumuladoDepreciacao((BigDecimal) o[14]);
                vo.setValorAcumuladoAmorizacao((BigDecimal) o[15]);
                vo.setValorAcumuladoExaustao((BigDecimal) o[16]);
                vo.setLiquido((BigDecimal) o[17]);
                vo.setCodigoGrupoBem((String) o[18]);
                vo.setDescricaoGrupoBem((String) o[19]);
                vo.setAdministrativa((String) o[20]);
                vo.setOrcamentaria((String) o[21]);
                vo.setAjustes((BigDecimal) o[22]);
                vo.setDataSolicitacao((Date) o[23]);
                vo.setCodigoSolicitacao((Number) o[24]);
                parecerBaixaPatrimonialVOs.add(vo);
            }

            return parecerBaixaPatrimonialVOs;

        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public EntityManager getEm() {
        return em;
    }
}
