package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author renat
 * @since 15/10/2019
 */
public class WebreportJob extends WPJob {

    @Autowired
    protected ReportService reportService;

    @Override
    public void execute() {
        try {
            reportService.rotinaApagarRelatorio();
        } catch (Exception e) {
            logger.warn("Erro executando Job", e);
        }
    }

    @Override
    public String toString() {
        return "Integração com o WebReport.";
    }

}
