package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.agendamentotarefas.SingletonAgendamentoTarefas;
import br.com.webpublico.entidades.MetricaSistema;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.seguranca.SingletonMetricas;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Objects;

public abstract class WPJob implements Job {
    protected static final Logger logger = LoggerFactory.getLogger(WPJob.class);
    @Autowired
    protected SingletonAgendamentoTarefas singletonAgendamentoTarefas;
    @Autowired
    private SingletonMetricas singletonMetricas;
    private Long criadoEm;

    public WPJob() {
        this.criadoEm = System.nanoTime();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        singletonMetricas.iniciarMetrica(SistemaFacade.SEM_LOGIN, this.toString(), System.currentTimeMillis(), MetricaSistema.Tipo.ROTINA_AGENDADA);
        singletonAgendamentoTarefas.addJob(this);
        execute();
        singletonAgendamentoTarefas.removeJob(this);
        singletonMetricas.finalizarMetrica(SistemaFacade.SEM_LOGIN, this.toString(), System.currentTimeMillis(), MetricaSistema.Tipo.ROTINA_AGENDADA);
    }

    public abstract void execute();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WPJob wpJob = (WPJob) o;
        return criadoEm.equals(wpJob.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(criadoEm);
    }

    @Override
    public String toString() {
        return "Rotina interna (" + this.getClass().getSimpleName() + ")";
    }
}
