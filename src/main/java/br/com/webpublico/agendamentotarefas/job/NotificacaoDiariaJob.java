package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.contabil.services.ServiceNotificacaoDiaria;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificacaoDiariaJob extends WPJob {

    @Autowired
    protected ServiceNotificacaoDiaria serviceNotificacaoDiaria;

    @Override
    public void execute() {
        try {
            serviceNotificacaoDiaria.notificar();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Notificação de Diárias";
    }
}
