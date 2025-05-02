package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.rh.ServiceNotificacaoInconsistenciasParametrizacaoLotacoes;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificacaoInconsistenciasParametrizacaoLotacoesJob extends WPJob {
    @Autowired
    private ServiceNotificacaoInconsistenciasParametrizacaoLotacoes serviceNotificacao;

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
        return "Notificações de Inconsistências na Parametrização de Lotações";
    }
}
