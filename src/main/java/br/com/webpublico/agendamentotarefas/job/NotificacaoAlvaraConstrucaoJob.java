package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceNotificacaoAlvaraConstrucao;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificacaoAlvaraConstrucaoJob extends WPJob {


    @Autowired
    protected ServiceNotificacaoAlvaraConstrucao serviceNotificacaoAlvaraConstrucao;

    @Override
    public void execute() {
        serviceNotificacaoAlvaraConstrucao.lancar();
    }


}
