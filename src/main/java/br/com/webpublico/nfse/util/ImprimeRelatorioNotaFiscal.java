package br.com.webpublico.nfse.util;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.nfse.domain.dtos.NotaFiscalSearchDTO;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ImprimeRelatorioNotaFiscal extends AbstractReport {

    private HashMap parameters;
    private String nomeRelatorio;
    private List<NotaFiscalSearchDTO> resultados;

    public ImprimeRelatorioNotaFiscal(String nomeRelatorio, List<NotaFiscalSearchDTO> resultadosParcela) {
        super();
        this.nomeRelatorio = nomeRelatorio;
        this.resultados = resultadosParcela;
        //super.geraNoDialog = true;
    }

    public void adicionarParametro(String nome, Object valor) {
        if (parameters == null) {
            parameters = new HashMap();
        }
        parameters.put(nome, valor);
    }

    public void imprimeRelatorio() throws IOException, JRException {
        //Collections.sort(resultados);
        gerarRelatorioComDadosEmCollectionView(nomeRelatorio, parameters, new JRBeanCollectionDataSource(resultados));
    }

    public byte[] gerarParaSistemaNota() throws JRException {

        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        String separator = System.getProperty("file.separator");
        String subReport = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        String img = Util.getApplicationPath("/img/") + separator;

        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("BRASAO", img);
        parametros.put("USUARIO", "WebISS");
        parametros.put("SUBREPORT_DIR", subReport);

        definirLocaleAndPerfilApp(parametros);
        JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(100);
        parametros.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        JasperPrint jasperPrint = JasperFillManager.fillReport(subReport + nomeRelatorio, parametros, new JRBeanCollectionDataSource(resultados));
        virtualizer.cleanup();

        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, Lists.newArrayList(jasperPrint));
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
        exporter.exportReport();
        return dadosByte.toByteArray();
    }
}
