package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceNotificacaoAvisoCobrancaAdministrativa;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class NotificacaoAvisoCobrancaAdministrativaJob extends WPJob {

    @Autowired
    private ServiceNotificacaoAvisoCobrancaAdministrativa serviceNotificacaoAvisoCobrancaAdministrativa;

    @Override
    public void execute() {
        try {
            serviceNotificacaoAvisoCobrancaAdministrativa.lancarNotificacoesCobrancaAdministrativa(new Date());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Aviso/Notificação de Cobrança Administrativa";
    }
}
