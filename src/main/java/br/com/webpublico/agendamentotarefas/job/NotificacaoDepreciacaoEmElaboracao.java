package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.NotificacaoDepreciacaoService;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificacaoDepreciacaoEmElaboracao extends WPJob {

    @Autowired
    private NotificacaoDepreciacaoService service;

    @Override
    public void execute() {
        service.notificar();
    }

    @Override
    public String toString() {
        return "Notificação de Depreciação em Elaboração";
    }
}
