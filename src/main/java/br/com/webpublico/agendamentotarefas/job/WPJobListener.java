package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.entidades.ConfiguracaoAgendamentoTarefa;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class WPJobListener implements JobListener {
    private static final Logger logger = LoggerFactory.getLogger(WPJobListener.class);

    private final ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada tipoAgendamento;
    private final Integer identificador;
    private final String key;
    private UsuarioSistema usuarioSistema;

    public WPJobListener(ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada tipoAgendamento, Integer identificador) {
        this.tipoAgendamento = tipoAgendamento;
        this.identificador = identificador;
        this.key = null;
    }

    public WPJobListener(ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada tipoAgendamento, String key, UsuarioSistema usuarioSistema) {
        this.tipoAgendamento = tipoAgendamento;
        this.key = key;
        this.usuarioSistema = usuarioSistema;
        this.identificador = null;
    }

    @Override
    public String getName() {
        return this.tipoAgendamento.name() + (this.identificador != null ? this.identificador : this.key);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
        if (this.usuarioSistema != null) {
            assistente.setUsuarioSistema(this.usuarioSistema);
        }
        String descricaoProcesso = this.tipoAgendamento != null ? this.tipoAgendamento.getDescricao() : "Tarefa Agendada" +
            DataUtil.getDataFormatadaDiaHora(new Date());
        assistente.setDescricaoProcesso(descricaoProcesso);
        assistente.setTotal(1);
        jobExecutionContext.put("job" + jobExecutionContext.getJobInstance().hashCode(),
            assistente);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        AssistenteBarraProgresso assistente = (AssistenteBarraProgresso) jobExecutionContext
            .get("job" + jobExecutionContext.getJobInstance().hashCode());
        assistente.conta();
        removerJob(jobExecutionContext);
    }

    private void removerJob(JobExecutionContext jobExecutionContext) {
        try {
            if (this.key != null) {
                jobExecutionContext.getScheduler().unscheduleJob(jobExecutionContext.getTrigger().getKey());
                jobExecutionContext.getScheduler().getListenerManager().removeJobListener(getName());
                jobExecutionContext.getScheduler().getListenerManager().removeJobListenerMatcher(getName(),
                    KeyMatcher.keyEquals(JobKey.jobKey(this.key, "group" + this.key)));
            }
        } catch (Exception e) {
            logger.error("Erro ao remover Job. ", e);
        }
    }

}
