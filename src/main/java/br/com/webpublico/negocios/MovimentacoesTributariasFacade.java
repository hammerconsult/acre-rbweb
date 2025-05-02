/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.MovimentacoesTributariasControlador;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class MovimentacoesTributariasFacade {

    private static final Logger logger = LoggerFactory.getLogger(MovimentacoesTributariasFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    protected EntityManager getEntityManager() {
        return em;
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Future<MovimentacoesTributariasControlador.GeraRelatorio> geraRelatorio(MovimentacoesTributariasControlador.GeraRelatorio geraRelatorio) {
        AsyncResult<MovimentacoesTributariasControlador.GeraRelatorio> asyncResult = new AsyncResult<>(geraRelatorio);
        try {
            gerarReport(geraRelatorio);
        } catch (Exception ex) {
            logger.error("Erro:", ex);
        } finally {
            if (geraRelatorio.getCon() != null) {
                try {
                    geraRelatorio.getCon().close();
                } catch (SQLException ex) {
                    logger.error("Erro:", ex);
                }
            }
        }
        return asyncResult;
    }

    private void gerarReport(MovimentacoesTributariasControlador.GeraRelatorio geraRelatorio)
        throws JRException, IOException {
        geraRelatorio.getParameters().put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        //gera relatorio com as classes do jasper
        JasperPrint jasperPrint = JasperFillManager.fillReport(geraRelatorio.getReport(), geraRelatorio.getParameters(), geraRelatorio.getCon());
        geraRelatorio.setDados(new ByteArrayOutputStream());
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, geraRelatorio.getDados());
        exporter.exportReport();
    }
}
