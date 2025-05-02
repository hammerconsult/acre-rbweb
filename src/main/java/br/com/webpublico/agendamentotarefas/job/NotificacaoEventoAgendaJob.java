package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceNotificacaoEventoAgenda;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tributario on 03/09/2015.
 */
public class NotificacaoEventoAgendaJob extends WPJob {
    @Autowired
    private ServiceNotificacaoEventoAgenda serviceNotificacao;

    @Override
    public void execute() {
        serviceNotificacao.enviaEmailNotificacaoEvento();
    }
}
