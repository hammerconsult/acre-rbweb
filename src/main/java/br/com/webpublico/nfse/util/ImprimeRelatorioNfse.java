package br.com.webpublico.nfse.util;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ImprimeRelatorioNfse {

    private AbstractReport abstractReport;
    private HashMap<String, Object> parametros;

    public ImprimeRelatorioNfse() {
        this.abstractReport = AbstractReport.getAbstractReport();
        this.abstractReport.setGeraNoDialog(true);
        this.parametros = new HashMap<>();
    }

    public void imprimirRelatorio(String arquivoJasper,
                                  List resultados) throws IOException, JRException {

        adicionarParametros(parametros);
        this.abstractReport.gerarRelatorioComDadosEmCollectionView(arquivoJasper, parametros, new JRBeanCollectionDataSource(resultados));
    }

    public byte[] gerarRelatorio(String jasper,
                                 List resultados,
                                 boolean excel) throws JRException, IOException {
        if (excel) {
            return gerarRelatorioXls(jasper, resultados);
        } else {
            return gerarRelatorioPdf(jasper, resultados);
        }
    }

    public JasperPrint gerarJasperPrintPdf(String jasper,
                                           List resultados,
                                           String usuario) throws JRException {
        adicionarParametros(parametros);
        parametros.put("USUARIO", usuario);
        return JasperFillManager.fillReport(parametros.get("SUBREPORT_DIR") + jasper,
            parametros, new JRBeanCollectionDataSource(resultados));
    }

    public JasperPrint gerarJasperPrintPdf(String jasper,
                                           List resultados) throws JRException {
        adicionarParametros(parametros);
        return JasperFillManager.fillReport(parametros.get("SUBREPORT_DIR") + jasper,
            parametros, new JRBeanCollectionDataSource(resultados));
    }

    public byte[] gerarRelatorioPdf(String jasper,
                                    List resultados) throws JRException {
        adicionarParametros(parametros);
        JasperPrint jasperPrint = JasperFillManager.fillReport(parametros.get("SUBREPORT_DIR") + jasper, parametros, new JRBeanCollectionDataSource(resultados));
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, Lists.newArrayList(jasperPrint));
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
        exporter.exportReport();
        return dadosByte.toByteArray();

    }

    public byte[] gerarRelatorioXls(String jasper,
                                    List resultados) throws JRException, IOException {
        adicionarParametros(parametros);
        JasperPrint jasperPrint = JasperFillManager.fillReport(parametros.get("SUBREPORT_DIR") + jasper, parametros, new JRBeanCollectionDataSource(resultados));
        JRXlsExporter exporter = new JRXlsExporter();
        ByteArrayOutputStream streamDados = new ByteArrayOutputStream();
        exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, streamDados);
        exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, Integer.decode("65000"));
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
        exporter.exportReport();
        streamDados.flush();
        streamDados.close();

        return streamDados.toByteArray();
    }

    private void adicionarParametros(HashMap<String, Object> parametros) {
        String separator = System.getProperty("file.separator");
        String subReport = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        String img = Util.getApplicationPath("/img/") + separator;

        parametros.put("SUBREPORT_DIR", subReport);
        parametros.put("IMAGEM", img);
        parametros.put("MUNICIPIO", "PREFEITURA DE RIO BRANCO");
        parametros.put("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
        parametros.put("DEPARTAMENTO", "DEPARTAMENTO DE TRIBUTAÇÃO");
        parametros.put("USUARIO", SistemaFacade.obtemLogin());
        this.abstractReport.definirLocaleAndPerfilApp(parametros);
    }

    public ImprimeRelatorioNfse adicionarParametro(String parametro, Object valor) {
        this.parametros.put(parametro, valor);
        return this;
    }
}
