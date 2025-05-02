package br.com.webpublico.negocios.administrativo.patrimonio;

import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.RelatorioDeBensMoveisPorGrupoComparativo;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.NaturezaTipoGrupoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by William on 06/03/2018.
 */
@Stateless
@Deprecated
public class RelatorioDeBensMoveisPorGrupoBemComparativoFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<ByteArrayOutputStream> gerarRelatorio(HashMap parameters, FiltroPatrimonio filtroPatrimonio, String caminhoReport, String nomeReport) throws IOException, JRException {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscarDados(filtroPatrimonio, parameters));
        return new AsyncResult<>(gerarReport(parameters, caminhoReport + nomeReport, ds));
    }

    private ByteArrayOutputStream gerarReport(HashMap parametros, String nomeReport, JRBeanCollectionDataSource dataSource) throws JRException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        parametros.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(nomeReport, parametros, dataSource);
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bytes);
        exporter.exportReport();
        return bytes;
    }

    @TransactionTimeout(value = 2, unit = TimeUnit.HOURS)
    public List<RelatorioDeBensMoveisPorGrupoComparativo> buscarDados(FiltroPatrimonio filtroPatrimonio, HashMap parametros) {
        List<RelatorioDeBensMoveisPorGrupoComparativo> dados = Lists.newArrayList();
        String sql = "SELECT " +
            "       GRUPOBEM.CODIGO || ' - ' || GRUPOBEM.DESCRICAO AS GRUPOPATRIMONIAL, " +
            "       SUM(QUANTIDADE) AS QUANTIDADE, " +
            "       COALESCE(SUM(SALDOATUALCONTABIL), 0) * -1 AS VALORCONTABIL, " +
            "       COALESCE(SUM(AJUSTECONTABIL), 0) AS AJUSTECONTABIL, " +
            "       (COALESCE(SUM(SALDOATUALCONTABIL), 0) * -1) - COALESCE(SUM(AJUSTECONTABIL), 0) AS DIFERENCACONTABIL, " +
            "       SUM(VALORBEM) AS VALORBEM, " +
            "       SUM(AJUSTEBEM) AS VALORAJUSTEBEM, " +
            "       (COALESCE(SUM((VALORBEM)), 0) - COALESCE(SUM(AJUSTEBEM), 0)) AS DIFERENCABEM, " +
            "       ((COALESCE(SUM(SALDOATUALCONTABIL), 0) * -1) - COALESCE(SUM(VALORBEM), 0)) AS VALORCONCILIACAO, " +
            "       (COALESCE(SUM((AJUSTECONTABIL)), 0) - COALESCE(SUM(AJUSTEBEM), 0)) AS AJUSTECONCILIACAO, " +
            "       ((COALESCE(SUM(SALDOATUALCONTABIL), 0) * -1) - COALESCE(SUM(VALORBEM), 0) - (COALESCE(SUM(AJUSTECONTABIL), 0) - COALESCE(SUM(AJUSTEBEM), 0))) AS VALORATUAL " +
            " FROM " +
            "  ( SELECT " +
            "       COUNT(BEM.ID) AS QUANTIDADE, " +
            "       SUM(ESTADO.VALORORIGINAL) AS VALORBEM, " +
            "       SUM(ESTADO.VALORACUMULADODAAMORTIZACAO) + SUM(ESTADO.VALORACUMULADODADEPRECIACAO) + SUM(ESTADO.VALORACUMULADODAEXAUSTAO) + SUM(ESTADO.VALORACUMULADODEAJUSTE) AS AJUSTEBEM, " +
            "       0 AS SALDOATUALCONTABIL, " +
            "       0 AS AJUSTECONTABIL, " +
            "       VWORC.SUBORDINADA_ID AS UNIDADE, " +
            "       ESTADO.GRUPOBEM_ID AS GRUPOBEM, " +
            "       BEM.DATAAQUISICAO AS DATAAQUISICAO, " +
            "       ESTADO.ESTADOBEM AS ESTADOBEM " +
            "   FROM EVENTOBEM EV " +
            "   INNER JOIN BEM ON BEM.ID = EV.BEM_ID " +
            "   INNER JOIN ESTADOBEM ESTADO ON ESTADO.ID = EV.ESTADORESULTANTE_ID " +
            "   INNER JOIN VWHIERARQUIAADMINISTRATIVA VWADM ON VWADM.SUBORDINADA_ID = ESTADO.DETENTORAADMINISTRATIVA_ID " +
            "   INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.SUBORDINADA_ID = estado.detentoraorcamentaria_id " +
            "   WHERE :dataReferencia BETWEEN VWADM.INICIOVIGENCIA AND COALESCE(VWADM.FIMVIGENCIA, :dataReferencia) " +
            "     AND ESTADO.ESTADOBEM <> :estadobem " +
            "     and estado.valororiginal > 0 " +
            "     AND :dataReferencia BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, :dataReferencia) " +
            "     AND EV.DATAOPERACAO = (SELECT " +
            "                              MAX(EV1.DATAOPERACAO) " +
            "                            FROM " +
            "                              EVENTOBEM EV1 " +
            "                            WHERE EV1.BEM_ID = EV.BEM_ID " +
            "                              AND to_date(to_char(EV1.DATALANCAMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') <= :dataReferencia " +
            "                            ) " +
            "   GROUP BY ESTADO.GRUPOBEM_ID, " +
            "            VWORC.SUBORDINADA_ID, " +
            "            BEM.DATAAQUISICAO, " +
            "            ESTADO.ESTADOBEM " +
            "   UNION ALL " +
            "   SELECT 0 AS QUANTIDADE, " +
            "          0 AS VALORBEM, " +
            "          0 AS AJUSTEBEM, " +
            "          COALESCE(SUM(CREDITO), 0) - COALESCE(SUM(DEBITO), 0) AS SALDOATUALCONTABIL, " +
            "          0 AS AJUSTECONTABIL, " +
            "          UNIDADE AS UNIDADE, " +
            "          GRUPO AS GRUPO, " +
            "          NULL AS DATAAQUISICAO, " +
            "          NULL AS ESTADOBEM " +
            "   FROM " +
            "     (SELECT COALESCE(SALDO.CREDITO, 0) AS CREDITO, " +
            "             COALESCE(SALDO.DEBITO, 0) AS DEBITO, " +
            "             SALDO.UNIDADEORGANIZACIONAL_ID AS UNIDADE, " +
            "             SALDO.GRUPOBEM_ID AS GRUPO " +
            "      FROM SALDOGRUPOBEM SALDO " +
            "      WHERE SALDO.TIPOGRUPO = :bemmovel " +
            "        AND SALDO.NATUREZATIPOGRUPOBEM = :original " +
            "        AND SALDO.DATASALDO = " +
            "          (SELECT MAX(SLD.DATASALDO) " +
            "           FROM SALDOGRUPOBEM SLD " +
            "           WHERE SLD.UNIDADEORGANIZACIONAL_ID = SALDO.UNIDADEORGANIZACIONAL_ID " +
            "             AND SLD.TIPOGRUPO = SALDO.TIPOGRUPO " +
            "             AND SLD.NATUREZATIPOGRUPOBEM = SALDO.NATUREZATIPOGRUPOBEM " +
            "             AND SLD.GRUPOBEM_ID = SALDO.GRUPOBEM_ID " +
            "             AND SLD.DATASALDO <= :dataReferencia)) SALDOCONTABIL " +
            "   GROUP BY UNIDADE, " +
            "            GRUPO " +
            "   UNION ALL " +
            "   SELECT " +
            "          0 AS QUANTIDADE, " +
            "          0 AS VALORBEM, " +
            "          0 AS AJUSTEBEM, " +
            "          0 AS SALDOATUALCONTABIL, " +
            "          COALESCE(SUM(CREDITO), 0) - COALESCE(SUM(DEBITO), 0) AS AJUSTECONTABIL, " +
            "          UNIDADE AS UNIDADE, " +
            "          GRUPO AS GRUPO, " +
            "          NULL AS DATAAQUISICAO, " +
            "          NULL AS ESTADOBEM " +
            "   FROM " +
            "     (SELECT COALESCE(SALDO.CREDITO, 0) AS CREDITO, " +
            "             COALESCE(SALDO.DEBITO, 0) AS DEBITO, " +
            "             SALDO.UNIDADEORGANIZACIONAL_ID AS UNIDADE, " +
            "             SALDO.GRUPOBEM_ID AS GRUPO " +
            "      FROM SALDOGRUPOBEM SALDO " +
            "      WHERE SALDO.TIPOGRUPO = :bemmovel " +
            "        AND SALDO.NATUREZATIPOGRUPOBEM <> :original " +
            "        AND SALDO.NATUREZATIPOGRUPOBEM <> :vpd " +
            "        AND SALDO.NATUREZATIPOGRUPOBEM <> :vpa " +
            "        AND SALDO.DATASALDO = " +
            "          (SELECT " +
            "             MAX(SLD.DATASALDO) " +
            "           FROM SALDOGRUPOBEM SLD " +
            "           WHERE SLD.UNIDADEORGANIZACIONAL_ID = SALDO.UNIDADEORGANIZACIONAL_ID " +
            "             AND SLD.TIPOGRUPO = SALDO.TIPOGRUPO " +
            "             AND SLD.NATUREZATIPOGRUPOBEM = SALDO.NATUREZATIPOGRUPOBEM " +
            "             AND SLD.GRUPOBEM_ID = SALDO.GRUPOBEM_ID " +
            "             AND SLD.DATASALDO <= :dataReferencia)) AJUSTECONTABIL " +
            "   GROUP BY UNIDADE," +
            "            GRUPO ) RELATORIO " +
            " INNER JOIN GRUPOBEM ON GRUPOBEM.ID = RELATORIO.GRUPOBEM ";
        sql += montarClausulaWhere(filtroPatrimonio, parametros);
        sql += " GROUP BY GRUPOBEM.CODIGO," +
            "         GRUPOBEM.DESCRICAO " +
            " ORDER BY GRUPOBEM.CODIGO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataReferencia", filtroPatrimonio.getDataReferencia());
        q.setParameter("vpd", NaturezaTipoGrupoBem.VPD.name());
        q.setParameter("vpa", NaturezaTipoGrupoBem.VPA.name());
        q.setParameter("original", NaturezaTipoGrupoBem.ORIGINAL.name());
        q.setParameter("bemmovel", TipoGrupo.BEM_MOVEL_PRINCIPAL.name());
        q.setParameter("estadobem", EstadoConservacaoBem.BAIXADO.name());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RelatorioDeBensMoveisPorGrupoComparativo item = new RelatorioDeBensMoveisPorGrupoComparativo();
                item.setGrupoPatrimonial((String) obj[0]);
                item.setQuantidade((BigDecimal) obj[1]);
                item.setValorContabil((BigDecimal) obj[2]);
                item.setAjusteContabil((BigDecimal) obj[3]);
                item.setDiferencaContabil((BigDecimal) obj[4]);
                item.setValorBem((BigDecimal) obj[5]);
                item.setValorAjusteBem((BigDecimal) obj[6]);
                item.setDiferencaBem((BigDecimal) obj[7]);
                item.setValorConciliacao((BigDecimal) obj[8]);
                item.setAjusteConciliacao((BigDecimal) obj[9]);
                item.setValorAtual((BigDecimal) obj[10]);
                dados.add(item);
            }
        }
        return dados;
    }

    private String montarClausulaWhere(FiltroPatrimonio filtroPatrimonio, HashMap parametros) {
        StringBuilder where = new StringBuilder();
        StringBuilder filtros = new StringBuilder();

        where.append(" where grupobem.tipobem  = '").append(TipoBem.MOVEIS.name()).append("' ");

        if (filtroPatrimonio.getHierarquiaOrc() != null && filtroPatrimonio.getHierarquiaOrc().getCodigo() != null) {
            where.append(" AND RELATORIO.UNIDADE = ").append(filtroPatrimonio.getHierarquiaOrc().getSubordinada().getId());
            filtros.append("Unidade Orçamentária: ").append(filtroPatrimonio.getHierarquiaOrc().toString()).append(". ").append("\n");
        }

        if (filtroPatrimonio.getGrupoBem() != null) {
            where.append(" AND RELATORIO.GRUPOBEM = ").append(filtroPatrimonio.getGrupoBem().getId());
            filtros.append("Grupo Patrimonial ").append(filtroPatrimonio.getGrupoBem().toString()).append(". ").append("\n");
        }
        filtros.append("Data de Referência: ").append(DataUtil.getDataFormatada(filtroPatrimonio.getDataReferencia())).append("\n");
        parametros.put("DATAREFERENCIA", DataUtil.dataSemHorario(filtroPatrimonio.getDataReferencia()));
        parametros.put("FILTROS", filtros.toString());
        parametros.put("WHERE", where.toString());
        return where.toString();
    }

}
