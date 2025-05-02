package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceNotaAvulsa;
import org.springframework.beans.factory.annotation.Autowired;

public class CancelamentoNotasAvulsaNaoPagasJob extends WPJob {

    @Autowired
    protected ServiceNotaAvulsa serviceNotaAvulsa;

    @Override
    public void execute() {
        try {
            serviceNotaAvulsa.cancelarNotasVencidasNaoPagas();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Cancelamento de Processos de Débitos Vencidos";
    }
}
