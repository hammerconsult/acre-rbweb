package br.com.webpublico.relatoriofacade;

import br.com.webpublico.controlerelatorio.AbstractReport;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by mateus on 15/03/18.
 */
public abstract class AbstractRelatorioAssincronoFacade {

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<ByteArrayOutputStream> gerarRelatorio(HashMap parameters, Object clausulasConsulta, String caminhoReport, String nomeReport) throws IOException, JRException {
        return new AsyncResult<>(AbstractReport.gerarReport(parameters, caminhoReport + nomeReport, criarBean(clausulasConsulta)));
    }

    public abstract JRBeanCollectionDataSource criarBean(Object clausulasConsulta);
}
