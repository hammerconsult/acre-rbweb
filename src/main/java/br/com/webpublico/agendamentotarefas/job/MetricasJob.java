package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceTramitesDosProcessos;
import br.com.webpublico.seguranca.SingletonMetricas;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

public class MetricasJob implements Job {
    @Autowired
    protected SingletonMetricas singletonMetricas;

    @Override
    public String toString() {
        return "Gravando Métricas";
    }

    public MetricasJob() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if(singletonMetricas != null){
            singletonMetricas.gravarMetricas();
        }
    }
}
