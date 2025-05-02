package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceNotificacaoDebitosAPrescrever;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificacaoDebitosAPrescreverJob extends WPJob {

    @Autowired
    private ServiceNotificacaoDebitosAPrescrever serviceNotificacaoDebitosAPrescrever;

    @Override
    public void execute() {
        try {
            serviceNotificacaoDebitosAPrescrever.lancarNotificacoesDeDebitosAPrescrever();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Notificação de Débitos À Prescrever";
    }
}
