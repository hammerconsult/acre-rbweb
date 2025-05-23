package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.MensagemUsuario;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.tributario.services.ServiceAgendamentoTarefa;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.util.List;
import java.util.Set;


@Service
public class MensagemTodosUsuariosService {
    private Set<MensagemUsuario> mensagens = Sets.newHashSet();
    private static final String idPushMessage = "mensagemUsuario";
    private static final String idPushMessageBloqueio = "mensagemUsuarioBloqueado";
    private static final String GROUP_SCHEDULE_NAME = "group_mensagem_usuario";
    private final String ID_FIELD = "id";

    @PersistenceContext
    protected transient EntityManager em;

    @Transactional
    public void enviarMensagensTodosUsuarios(Long id) {
        MensagemUsuario mensagem = em.find(MensagemUsuario.class, id);
        enviarMensagensTodosUsuarios(mensagem);
    }


    public void enviarMensagensTodosUsuarios(MensagemUsuario mensagem) {
        mensagem.setPublicada(true);
        this.mensagens.add(em.merge(mensagem));
        getVerificadorSessoesAtivasUsuario().notificarTodosUsuariosLogados(mensagem.getBloqueiaAcesso() ? idPushMessageBloqueio : idPushMessage);
    }

    private static VerificadorSessoesAtivasUsuario getVerificadorSessoesAtivasUsuario() {
        VerificadorSessoesAtivasUsuario verificadorSessoesAtivasUsuario = (VerificadorSessoesAtivasUsuario) Util.getSpringBeanPeloNome("verificadorSessoesAtivasUsuario");
        return verificadorSessoesAtivasUsuario;
    }

    public void limparMensagensTodosUsuarios() {
        mensagens = Sets.newHashSet();
    }

    @Transactional
    public void removerMensagensTodosUsuarios(Long id) {
        MensagemUsuario mensagem = em.find(MensagemUsuario.class, id);
        removerMensagensTodosUsuarios(mensagem);
    }

    private void removerMensagensTodosUsuarios(MensagemUsuario mensagem) {
        mensagem.setPublicada(false);
        em.merge(mensagem);
        this.mensagens.remove(mensagem);
        getVerificadorSessoesAtivasUsuario().notificarTodosUsuariosLogados(mensagem.getBloqueiaAcesso() ? idPushMessageBloqueio : idPushMessage);
    }

    public List<MensagemUsuario> buscarTodas() {
        return em.createQuery("select m from MensagemUsuario m").getResultList();
    }

    public List<MensagemUsuario> buscarEmitidas() {
        return em.createQuery("select m from MensagemUsuario m where m.publicada = true and m.ativo = true").getResultList();
    }

    public List<MensagemUsuario> getMensagens() {
        return Lists.newArrayList(mensagens);
    }


    @Transactional
    public void agendaTarefas() {
        try {
            limparMensagensTodosUsuarios();
            buscarEmitidas().forEach(this::enviarMensagensTodosUsuarios);
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            limpaJobs(scheduler);
            addJobs(scheduler);
            scheduler.start();
        } catch (Exception e) {
            Util.loggingError(this.getClass(), "Erro ao Agendar Tarefas", e);
        }
    }

    private void limpaJobs(Scheduler scheduler) throws SchedulerException {
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(GROUP_SCHEDULE_NAME))) {
            scheduler.deleteJob(jobKey);
            scheduler.getListenerManager().removeJobListener(jobKey.getName());
            scheduler.getListenerManager().removeJobListenerMatcher(jobKey.getName(), KeyMatcher.keyEquals(JobKey.jobKey(jobKey.getName(), "group" + jobKey.getName())));
        }
    }

    private void addJobs(Scheduler scheduler) throws SchedulerException {
        String perfilExecutarRotinaAgendada = System.getenv(ServiceAgendamentoTarefa.APP_PERFIL_DESATIVAR_ROTINA_AGENDADA);
        if (perfilExecutarRotinaAgendada == null) {
            List<MensagemUsuario> agendamentos = buscarTodas();
            for (MensagemUsuario mensagem : agendamentos) {
                try {

                    final JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put(ID_FIELD, mensagem.getId());
                    jobDataMap.put(MensagemTodosUsuariosService.class.getSimpleName(), this);

                    JobDetail jobAdd = JobBuilder.newJob(AddMensagemUsuarioJob.class)
                        .withIdentity(
                            "mensagem_add_" + mensagem.getId().toString(),
                            GROUP_SCHEDULE_NAME
                        )
                        .usingJobData(jobDataMap)

                        .build();

                    Trigger triggerAdd = criaTrigger("add" + mensagem.getId().toString(), mensagem.getCronPublicar());
                    scheduler.scheduleJob(jobAdd, triggerAdd);

                    JobDetail jobRemove = JobBuilder.newJob(RemoveMensagemUsuarioJob.class)
                        .withIdentity(
                            "mensagem_remove_" + mensagem.getId().toString(),
                            GROUP_SCHEDULE_NAME
                        )
                        .usingJobData(jobDataMap)
                        .build();

                    Trigger triggerRemove = criaTrigger("remove" + mensagem.getId().toString(), mensagem.getCronRemover());
                    scheduler.scheduleJob(jobRemove, triggerRemove);
                } catch (Exception e) {
                    Util.loggingError(this.getClass(), "Não foi possível agendar o job: ", e);
                }
            }
        }
    }

    private Trigger criaTrigger(String id, String cron) {
        TriggerBuilder<CronTrigger> triggerBuilder = null;
        try {
            triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(
                    "trigger_mensagem_" + id,
                    GROUP_SCHEDULE_NAME)
                .withSchedule(CronScheduleBuilder.cronScheduleNonvalidatedExpression(cron));
            return triggerBuilder.build();
        } catch (ParseException e) {
            throw new ExcecaoNegocioGenerica("Formato do CRON inválido.");
        }
    }


    public static class AddMensagemUsuarioJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            MensagemTodosUsuariosService singleton = Util.recuperarSpringBean(MensagemTodosUsuariosService.class);
            singleton.enviarMensagensTodosUsuarios(dataMap.getLong("id"));
        }
    }


    public static class RemoveMensagemUsuarioJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            MensagemTodosUsuariosService singleton = Util.recuperarSpringBean(MensagemTodosUsuariosService.class);
            singleton.removerMensagensTodosUsuarios(dataMap.getLong("id"));
        }
    }

}
