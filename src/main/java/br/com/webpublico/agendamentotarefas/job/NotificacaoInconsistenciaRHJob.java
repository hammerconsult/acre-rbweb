package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.rh.ServiceNotificacaoInconsistenciaRH;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificacaoInconsistenciaRHJob extends WPJob {
    @Autowired
    private ServiceNotificacaoInconsistenciaRH serviceNotificacao;

    @Override
    public void execute() {
        try {
            serviceNotificacao.recuperarInconsistencias();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Notificações de Inconsistências de Cadastros ou Cálculos do RH";
    }
}
