package br.com.webpublico.negocios;

import br.com.webpublico.agendamentotarefas.job.WPJobListener;
import br.com.webpublico.entidades.ConfiguracaoAgendamentoTarefa;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ConfiguracaoAgendamentoTarefaFacade extends AbstractFacade<ConfiguracaoAgendamentoTarefa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConfiguracaoAgendamentoTarefaFacade() {
        super(ConfiguracaoAgendamentoTarefa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ConfiguracaoAgendamentoTarefa> findAll() {
        return em.createQuery("from ConfiguracaoAgendamentoTarefa where tipoTarefaAgendada in (:tiposExistentes)")
            .setParameter("tiposExistentes", Lists.newArrayList(ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada.values()))
            .getResultList();
    }

    public List<ConfiguracaoAgendamentoTarefa> buscarPorTipo(ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada tipo) {
        Query query = em.createQuery("select c from ConfiguracaoAgendamentoTarefa c where c.tipoTarefaAgendada = :tipoTarefaAgendada");
        query.setParameter("tipoTarefaAgendada", tipo);
        return query.getResultList();
    }

    public void salvar(List<ConfiguracaoAgendamentoTarefa> agendamentos) {
        for (ConfiguracaoAgendamentoTarefa agendamento : agendamentos) {
            if (agendamento.getId() != null) {
                salvar(agendamento);
            } else {
                salvarNovo(agendamento);
            }
        }
    }

    public void deletar(List<ConfiguracaoAgendamentoTarefa> agendamentos) {
        for (ConfiguracaoAgendamentoTarefa agendamento : agendamentos) {
            if(agendamento.getId() != null) {
                em.remove(em.find(ConfiguracaoAgendamentoTarefa.class, agendamento.getId()));
            }
        }
    }

    public void rodarAgora(ConfiguracaoAgendamentoTarefa agendamento, Scheduler scheduler) throws SchedulerException {
        try {
            String key = gerarKey();
            JobDetail jobDetail = criarJobDetail(agendamento, key);
            SimpleTrigger simpleTrigger = criarTrigger(key);
            adicionarListener(agendamento, scheduler, key, jobDetail);
            scheduler.scheduleJob(jobDetail, simpleTrigger);
        } catch (Exception e) {
            logger.error("Erro ao criar scheduler. ", e);
        } finally {
            scheduler.start();
        }
    }

    private void adicionarListener(ConfiguracaoAgendamentoTarefa agendamento, Scheduler scheduler, String key, JobDetail jobDetail) throws SchedulerException {
        scheduler.getListenerManager().addJobListener(
            new WPJobListener(agendamento.getTipoTarefaAgendada(), key, Util.recuperarUsuarioCorrente()),
            KeyMatcher.keyEquals(jobDetail.getKey())
        );
    }

    private String gerarKey() {
        return RandomStringUtils.randomAlphanumeric(BigDecimal.TEN.intValue());
    }

    private JobDetail criarJobDetail(ConfiguracaoAgendamentoTarefa agendamento, String key) {
        return JobBuilder.newJob(agendamento.getTipoTarefaAgendada().getJob()).withIdentity(key, "group" + key).build();
    }

    private SimpleTrigger criarTrigger(String key) {
        return (SimpleTrigger) TriggerBuilder.newTrigger().withIdentity(key, "group" + key).startNow().build();
    }

    public boolean hasAgendamento(ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada tipo) {
        Query query = em.createQuery("select c from ConfiguracaoAgendamentoTarefa c " +
            " where c.tipoTarefaAgendada = :tipoTarefaAgendada ");
        query.setParameter("tipoTarefaAgendada", tipo);
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }

}
