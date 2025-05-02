package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceNotificacaoMalaDireta;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Wellington on 05/02/2016.
 */
public class NotificacaoMalaDireta extends WPJob {

    @Autowired
    protected ServiceNotificacaoMalaDireta serviceNotificacaoMalaDireta;


    @Override
    public void execute() {
        try {
            serviceNotificacaoMalaDireta.lancar();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Notificações do Mala Direta";
    }
}
