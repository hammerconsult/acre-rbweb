package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.seguranca.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;

public class RecarregarNotificacaoJob extends WPJob {

    @Autowired
    protected NotificacaoService notificacaoService;

    @Override
    public void execute() {
        try {
            notificacaoService.recarregarTodasNotificacoes();
        } catch (Exception e) {
            logger.warn("Erro executando Job", e);
        }
    }

    @Override
    public String toString() {
        return "Integração com o WebReport.";
    }

}
