package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.agendamentotarefas.job.GarbageCollectorJob;
import br.com.webpublico.agendamentotarefas.job.MetricasJob;
import br.com.webpublico.agendamentotarefas.job.WPJobListener;
import br.com.webpublico.agendamentotarefas.job.ReprocessamentoContabilJob;
import br.com.webpublico.entidades.ConfiguracaoAgendamentoTarefa;
import br.com.webpublico.entidades.ConfiguracaoMetrica;
import br.com.webpublico.entidades.comum.FechamentoMensalMes;
import br.com.webpublico.enums.SituacaoFechamentoMensal;
import br.com.webpublico.enums.TipoSituacaoAgendamento;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import com.google.common.collect.Lists;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.util.List;

@Service
public class ServiceAgendamentoTarefa {

    protected static final Logger logger = LoggerFactory.getLogger(ServiceAgendamentoTarefa.class);

    public static String APP_PERFIL_DESATIVAR_ROTINA_AGENDADA = "DESATIVAR_ROTINA_AGENDADA";

    @PersistenceContext
    protected transient EntityManager em;


    public List<ConfiguracaoAgendamentoTarefa> findAll() {
        return em.createQuery("from ConfiguracaoAgendamentoTarefa where tipoTarefaAgendada in (:tiposExistentes)")
            .setParameter("tiposExistentes", Lists.newArrayList(ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada.values()))
            .getResultList();
    }

    private List<FechamentoMensalMes> buscarConfiguracoesReprocessamento() {
        String hql = "from FechamentoMensalMes where situacaoContabil = :aberto and cron is not null and tipoSituacaoAgendamento = :aguardando ";
        Query q = em.createQuery(hql);
        q.setParameter("aberto", SituacaoFechamentoMensal.ABERTO);
        q.setParameter("aguardando", TipoSituacaoAgendamento.AGUARDANDO);
        return q.getResultList();
    }

    public List<ConfiguracaoMetrica> findMetrica() {
        return em.createQuery("from ConfiguracaoMetrica").getResultList();
    }

    public void agendaTarefas() throws SchedulerException {
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        limpaJobs(scheduler);
        try {
            addJobs(scheduler);
            agendarMetricas(scheduler);
            agendarReprocessamento(scheduler);
            agendarGarbageCollector(scheduler);
        } finally {
            scheduler.start();
        }
    }

    private void agendarReprocessamento(Scheduler scheduler) {
        List<FechamentoMensalMes> fechamentos = buscarConfiguracoesReprocessamento();
        for (FechamentoMensalMes fechamentoMensalMes : fechamentos) {
            try {
                JobDetail job = criarJobReprocessamento(fechamentos, fechamentoMensalMes);
                CronTrigger trigger = criarTriggerReprocessamento(fechamentos, fechamentoMensalMes);
                scheduler.scheduleJob(job, trigger);
            } catch (Exception e) {
                logger.error("Não foi possível agendar a reprocessamento: ", e);
            }
        }
    }

    private CronTrigger criarTriggerReprocessamento(List<FechamentoMensalMes> fechamentos, FechamentoMensalMes fechamento) {
        TriggerBuilder<CronTrigger> triggerBuilder;
        try {
            triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(
                    "trigger" + fechamento.getClass().getSimpleName() + fechamentos.indexOf(fechamento),
                    "group" + fechamento.getClass().getSimpleName() + fechamentos.indexOf(fechamento))
                .withSchedule(CronScheduleBuilder.cronScheduleNonvalidatedExpression(fechamento.getCron()));
            return triggerBuilder.build();
        } catch (ParseException e) {
            throw new ExcecaoNegocioGenerica("Formato do CRON inválido.");
        }
    }

    private JobDetail criarJobReprocessamento(List<FechamentoMensalMes> fechamentos, FechamentoMensalMes fechamento) {
        return JobBuilder.newJob(ReprocessamentoContabilJob.class)
            .withIdentity(
                fechamento.getClass().getSimpleName() + fechamentos.indexOf(fechamento),
                "group" + fechamento.getClass().getSimpleName() + fechamentos.indexOf(fechamento)
            ).build();
    }

    private void agendarMetricas(Scheduler scheduler) {
        List<ConfiguracaoMetrica> metricas = findMetrica();
        for (ConfiguracaoMetrica configuracaoMetrica : metricas) {
            try {
                JobDetail job = criarJobMetrica(metricas, configuracaoMetrica);
                CronTrigger trigger = criarTriggerMetrica(metricas, configuracaoMetrica);
                scheduler.scheduleJob(job, trigger);
            } catch (Exception e) {
                logger.error("Não foi possível agendar a métrica: ", e);
            }
        }
    }

    private CronTrigger criarTriggerMetrica(List<ConfiguracaoMetrica> metricas, ConfiguracaoMetrica configuracaoMetrica) {
        return TriggerBuilder
            .newTrigger()
            .withIdentity(
                "trigger" + configuracaoMetrica.getClass().getSimpleName() + metricas.indexOf(configuracaoMetrica),
                "group" + configuracaoMetrica.getClass().getSimpleName() + metricas.indexOf(configuracaoMetrica))
            .withSchedule(CronScheduleBuilder.cronSchedule(configuracaoMetrica.getCron()))
            .build();
    }

    private JobDetail criarJobMetrica(List<ConfiguracaoMetrica> metricas, ConfiguracaoMetrica configuracaoMetrica) {
        return JobBuilder.newJob(MetricasJob.class)
            .withIdentity(
                configuracaoMetrica.getClass().getSimpleName() + metricas.indexOf(configuracaoMetrica),
                "group" + configuracaoMetrica.getClass().getSimpleName() + metricas.indexOf(configuracaoMetrica)
            ).build();
    }

    private void addJobs(Scheduler scheduler) throws SchedulerException {
        String perfilExecutarRotinaAgendada = System.getenv(APP_PERFIL_DESATIVAR_ROTINA_AGENDADA);
        if (perfilExecutarRotinaAgendada == null) {
            List<ConfiguracaoAgendamentoTarefa> agendamentos = findAll();
            for (ConfiguracaoAgendamentoTarefa agendamento : agendamentos) {
                try {
                    JobDetail job = criaJob(agendamentos, agendamento);
                    Trigger trigger = criaTrigger(agendamentos, agendamento);
                    adicionarListener(scheduler, agendamentos, agendamento, job);
                    scheduler.start();
                    scheduler.scheduleJob(job, trigger);
                } catch (Exception e) {
                    logger.error("Não foi possível agendar o job: ", e);
                }
            }
        }
    }

    private void adicionarListener(Scheduler scheduler, List<ConfiguracaoAgendamentoTarefa> agendamentos, ConfiguracaoAgendamentoTarefa agendamento,
                                   JobDetail job) throws SchedulerException {
        scheduler.getListenerManager().addJobListener(
            new WPJobListener(agendamento.getTipoTarefaAgendada(), agendamentos.indexOf(agendamento)),
            KeyMatcher.keyEquals(job.getKey())
        );
    }

    private Trigger criaTrigger(List<ConfiguracaoAgendamentoTarefa> agendamentos, ConfiguracaoAgendamentoTarefa agendamento) {
        if (agendamento.getCron() != null) {
            TriggerBuilder<CronTrigger> triggerBuilder = null;
            try {
                triggerBuilder = TriggerBuilder.newTrigger()
                    .withIdentity(
                        "trigger" + agendamento.getTipoTarefaAgendada().name() + agendamentos.indexOf(agendamento),
                        "group" + agendamento.getTipoTarefaAgendada().name() + agendamentos.indexOf(agendamento))
                    .withSchedule(CronScheduleBuilder.cronScheduleNonvalidatedExpression(agendamento.getCron()));
                return triggerBuilder.build();
            } catch (ParseException e) {
                throw new ExcecaoNegocioGenerica("Formato do CRON inválido.");
            }
        } else {
            java.util.Calendar startTime = getStartTime(agendamento);
            return TriggerBuilder
                .newTrigger()
                .withIdentity(
                    "trigger" + agendamento.getTipoTarefaAgendada().name() + agendamentos.indexOf(agendamento)
                    , "group" + agendamento.getTipoTarefaAgendada().name() + agendamentos.indexOf(agendamento))
                .startAt(startTime.getTime())
                .withSchedule(SimpleScheduleBuilder.repeatHourlyForever(24))
                .build();
        }
    }

    private java.util.Calendar getStartTime(ConfiguracaoAgendamentoTarefa agendamento) {
        java.util.Calendar startTime = java.util.Calendar.getInstance();
        startTime.set(java.util.Calendar.HOUR_OF_DAY, agendamento.getHora());
        startTime.set(java.util.Calendar.MINUTE, agendamento.getMinuto());
        startTime.set(java.util.Calendar.SECOND, 0);
        startTime.set(java.util.Calendar.MILLISECOND, 0);
        return startTime;
    }

    private JobDetail criaJob(List<ConfiguracaoAgendamentoTarefa> agendamentos, ConfiguracaoAgendamentoTarefa agendamento) {
        return JobBuilder.newJob(agendamento.getTipoTarefaAgendada().getJob())
            .withIdentity(
                agendamento.getTipoTarefaAgendada().name() + agendamentos.indexOf(agendamento),
                "group" + agendamento.getTipoTarefaAgendada().name() + agendamentos.indexOf(agendamento)
            ).build();
    }

    private void limpaJobs(Scheduler scheduler) throws SchedulerException {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                scheduler.deleteJob(jobKey);
                scheduler.getListenerManager().removeJobListener(jobKey.getName());
                scheduler.getListenerManager().removeJobListenerMatcher(jobKey.getName(), KeyMatcher.keyEquals(JobKey.jobKey(jobKey.getName(), "group" + jobKey.getName())));
            }
        }
    }

    private void agendarGarbageCollector(Scheduler scheduler) throws SchedulerException {
        logger.debug("Agendando Garbage Collector.");
        JobDetail job = criarJobPersonalizado(GarbageCollectorJob.class, "GarbageCollectorJob", "GarbageCollectorJobGroup");
        Trigger trigger = criarTriggerPersonalizada("GarbageCollectorJob", "GarbageCollectorJobGroup", 15);
        scheduler.startDelayed(150);
        scheduler.scheduleJob(job, trigger);
    }

    public Trigger criarTriggerPersonalizada(String nome, String grupo, Integer intervaloMinutos) {
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(nome, grupo)
            .startNow()
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(intervaloMinutos)
                .repeatForever())
            .build();
        return trigger;
    }

    private JobDetail criarJobPersonalizado(Class<? extends Job> job, String nome, String grupo) {
        return JobBuilder.newJob(job)
            .withIdentity(nome, grupo).build();
    }
}
