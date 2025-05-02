package br.com.webpublico.agendamentotarefas;

import br.com.webpublico.agendamentotarefas.job.WPJob;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SingletonAgendamentoTarefas {
    @PersistenceContext
    protected transient EntityManager entityManager;
    private List<WPJob> jobs;
    private List<AssistenteBarraProgresso> processos;


    @PostConstruct
    public void initialize() {
        jobs = Lists.newArrayList();
        processos = Lists.newArrayList();
    }

    public void addJob(WPJob wpJob) {
        jobs.add(wpJob);
    }

    public void removeJob(WPJob wpJob) {
        jobs.remove(wpJob);
    }

    public List<WPJob> getJobs() {
        return Lists.newArrayList(jobs);
    }


    public void addProcesso(AssistenteBarraProgresso assistente) {
        if (!processos.contains(assistente)) {
            processos.add(assistente);
        }
    }

    public void removeProcesso(AssistenteBarraProgresso assistente) {
        if (processos.contains(assistente))
            processos.remove(assistente);
    }

    public List<AssistenteBarraProgresso> getProcessos() {
        return Lists.newArrayList(processos);
    }
}
