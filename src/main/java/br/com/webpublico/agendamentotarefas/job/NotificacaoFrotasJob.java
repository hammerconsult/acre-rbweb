package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceNotificacaoFrotas;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Wellington on 05/02/2016.
 */
public class NotificacaoFrotasJob extends WPJob {

    @Autowired
    protected ServiceNotificacaoFrotas serviceNotificacaoFrotas;


    @Override
    public void execute() {
        try {
            serviceNotificacaoFrotas.lancarNotificacoesDoFrotas();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Notificações do Frotas";
    }
}
