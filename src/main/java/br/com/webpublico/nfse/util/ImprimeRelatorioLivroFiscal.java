package br.com.webpublico.nfse.util;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.RelatorioLivroFiscalDTO;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ImprimeRelatorioLivroFiscal {

    private AbstractReport abstractReport;

    public ImprimeRelatorioLivroFiscal() {
        this.abstractReport = AbstractReport.getAbstractReport();
        this.abstractReport.setGeraNoDialog(true);
    }

    private void adicionarParametros(HashMap<String, Object> parametros) {
        String separator = System.getProperty("file.separator");
        String img = Util.getApplicationPath("/img/") + separator;
        parametros.put("IMAGEM", img);
        parametros.put("MUNICIPIO", "Municipio de Rio Branco");
        parametros.put("SECRETARIA", "Secretaria Municipal de Finanças");
        parametros.put("DEPARTAMENTO", "Departamento de Tributação");
        parametros.put("USUARIO", SistemaFacade.obtemLogin());
    }

    public void imprimirRelatorio(List<RelatorioLivroFiscalDTO> resultados) throws IOException, JRException {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("SUBREPORT_DIR", this.abstractReport.getCaminho());
        parametros.put("FILTROS", "TODOS");
        adicionarParametros(parametros);
        this.abstractReport.gerarRelatorioComDadosEmCollectionView("LivroFiscal.jasper", parametros, new JRBeanCollectionDataSource(resultados));
    }

    public byte[] gerarRelatorioSistemaNfse(List<RelatorioLivroFiscalDTO> resultados) throws JRException {
        String separator = System.getProperty("file.separator");
        String subReport = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("SUBREPORT_DIR", subReport);
        adicionarParametros(parametros);
        this.abstractReport.definirLocaleAndPerfilApp(parametros);
        JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(100);
        parametros.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        JasperPrint jasperPrint = JasperFillManager.fillReport(subReport + "LivroFiscal.jasper", parametros, new JRBeanCollectionDataSource(resultados));
        virtualizer.cleanup();

        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, Lists.newArrayList(jasperPrint));
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
        exporter.exportReport();
        return dadosByte.toByteArray();
    }
}
