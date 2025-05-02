package br.com.webpublico.relatoriofacade.administrativo.patrimonio;

import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.patrimonio.RelatorioInventarioBem;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by wellington on 10/08/17.
 */
@Stateless
@Deprecated
public class RelatorioInventarioBemFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<ByteArrayOutputStream> gerarRelatorio(HashMap parameters, Date dataReferencia, String complementoQuery, String caminhoReport, String nomeReport) throws IOException, JRException {
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscarDados(dataReferencia, complementoQuery));
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

    public List<RelatorioInventarioBem> buscarDados(Date dataReferencia, String complementoQuery) {
        String sql = "select b.id as idBem, " +
            "       b.identificacao as registroPatrimonial, " +
            "       b.dataaquisicao as dataAquisicao, " +
            "       coalesce(estado_resultante.valororiginal, 0)               as valorOriginal, " +
            "       coalesce(estado_resultante.valoracumuladodaamortizacao, 0) as valorAmortizacao, " +
            "       coalesce(estado_resultante.valoracumuladodadepreciacao, 0) as valorDepreciacao, " +
            "       coalesce(estado_resultante.valoracumuladodaexaustao, 0)    as valorExaustao, " +
            "       coalesce(estado_resultante.valoracumuladodeajuste, 0)      as valorPerdas, " +
            "       b.descricao as descricaoBem, " +
            "       b.marca as marcaBem, " +
            "       b.modelo as modeloBem, " +
            "       vw.codigo || ' - '|| vw.descricao as unidadeAdministrativa, " +
            "       vworc.codigo || ' - ' || vworc.descricao as unidadeOrcamentaria, " +
            "       grupo.codigo codigoGrupoBem, " +
            "       grupo.codigo || ' - ' || grupo.descricao as grupoBem, " +
            "       grupoobj.codigo || ' - ' || grupoobj.descricao as grupoObjetoCompra, " +
            "       estado_resultante.estadobem as estadoBem, " +
            "       estado_resultante.situacaoconservacaobem as situacaoConservacaoBem, " +
            "       estado_resultante.tipoaquisicaobem as tipoAquisicaoBem, " +
            "       estado_resultante.localizacao as localizacao, " +
            "       (select listagg(bnf.numeronotafiscal, ',') within group (order by bnf.numeronotafiscal) " +
            "           from bemnotafiscal bnf " +
            "        where bnf.bem_id = b.id) as notasFiscais, " +
            "       (select listagg(bnfe.numeroempenho || '/' || extract(YEAR FROM bnfe.dataempenho), ',') within group (order by bnf.numeronotafiscal) " +
            "           from bemnotafiscal bnf " +
            "          inner join bemnotafiscalempenho bnfe on bnfe.bemnotafiscal_id = bnf.id " +
            "        where bnf.bem_id = b.id) empenhos, " +
            "( " +
            "        select pf.nome from ParametroPatrimonio param " +
            "        inner join ResponsavelPatrimonio resp on resp.PARAMETROPATRIMONIO_ID = param.ID " +
            "        inner join pessoafisica pf on pf.id = resp.RESPONSAVEL_ID " +
            "        where trunc(:data_referencia) between trunc(resp.iniciovigencia) and  coalesce(trunc(fimvigencia),trunc(:data_referencia)) " +
            "        and resp.UNIDADEORGANIZACIONAL_ID = estado_resultante.DETENTORAADMINISTRATIVA_ID " +
            "    ) as responsavel," +
            "    b.registroanterior as registroAnterior  " +
            "   from bem b " +
            "   inner join EVENTOBEM ev on ev.bem_id = b.id " +
            "  left join estadobem estado_inicial on estado_inicial.id = ev.estadoinicial_id " +
            "  inner join estadobem estado_resultante  on estado_resultante.id   = ev.estadoresultante_id " +
            "  inner join grupobem grupo on grupo.id = estado_resultante.grupobem_id " +
            "  left join grupoobjetocompra grupoobj on grupoobj.id = estado_resultante.grupoobjetocompra_id " +
            "  inner join vwhierarquiaadministrativa vw on vw.subordinada_id = estado_resultante.detentoraadministrativa_id " +
            "  inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = estado_resultante.detentoraorcamentaria_id " +
            "  left join ITEMEFETIVACAOBAIXA item ON item.id = ev.id " +
            "  left join EFETIVACAOBAIXAPATRIMONIAL baixa ON baixa.ID = item.EFETIVACAOBAIXA_ID " +
            "  where :data_referencia between vw.iniciovigencia and coalesce(vw.fimvigencia, :data_referencia) " +
            "    and :data_referencia between vworc.iniciovigencia and coalesce(vworc.fimvigencia, :data_referencia) " +
            "    and ev.dataoperacao = (select max(s_evb.dataoperacao) " +
            "                              from eventobem s_evb " +
            "                              where s_evb.bem_id = b.id " +
            "                              and trunc(s_evb.datalancamento) <= trunc(:data_referencia) " +
            "                              )";
        sql += complementoQuery;
        Query q = em.createNativeQuery(sql);
        q.setParameter("data_referencia", dataReferencia);
        List<RelatorioInventarioBem> dados = Lists.newArrayList();
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RelatorioInventarioBem item = new RelatorioInventarioBem();
                item.setIdBem(((BigDecimal) obj[0]).longValue());
                item.setRegistroPatrimonial((String) obj[1]);
                item.setDataAquisicao((Date) obj[2]);
                item.setValorOriginal((BigDecimal) obj[3]);
                item.setValorAmortizacao((BigDecimal) obj[4]);
                item.setValorDepreciacao((BigDecimal) obj[5]);
                item.setValorExaustao((BigDecimal) obj[6]);
                item.setValorPerdas((BigDecimal) obj[7]);
                item.setDescricaoBem((String) obj[8]);
                item.setMarcaBem((String) obj[9]);
                item.setModeloBem((String) obj[10]);
                item.setUnidadeAdministrativa((String) obj[11]);
                item.setUnidadeOrcamentaria((String) obj[12]);
                item.setCodigoGrupoBem((String) obj[13]);
                item.setGrupoBem((String) obj[14]);
                item.setGrupoObjetoCompra((String) obj[15]);
                item.setEstadoBem((String) obj[16]);
                item.setSituacaoConservacaoBem((String) obj[17]);
                item.setTipoAquisicaoBem((String) obj[18]);
                item.setLocalizacao((String) obj[19]);
                item.setNotasFiscais((String) obj[20]);
                item.setEmpenhos((String) obj[21]);
                item.setResponsavel((String) obj[22]);
                item.setRegistroAnterior((String) obj[23]);
                if (verificarValorMaiorQueZero(item)) {
                    dados.add(item);
                }
            }
        }
        return dados;
    }

    private boolean verificarValorMaiorQueZero(RelatorioInventarioBem item) {
        return item.getValorOriginal().compareTo(BigDecimal.ZERO) > 0
            || item.getValorDepreciacao().compareTo(BigDecimal.ZERO) > 0
            || item.getValorAmortizacao().compareTo(BigDecimal.ZERO) > 0
            || item.getValorExaustao().compareTo(BigDecimal.ZERO) > 0
            || item.getValorPerdas().compareTo(BigDecimal.ZERO) > 0;
    }
}
