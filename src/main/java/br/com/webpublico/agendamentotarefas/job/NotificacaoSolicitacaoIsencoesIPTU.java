package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceNotificacaoSolicitacaoIsencoesIPTU;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificacaoSolicitacaoIsencoesIPTU extends WPJob {

    @Autowired
    private ServiceNotificacaoSolicitacaoIsencoesIPTU serviceIsencao;

    @Override
    public void execute() {
        try {
            serviceIsencao.notificarSolicitacoesDeIsencaoEmAberto();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
