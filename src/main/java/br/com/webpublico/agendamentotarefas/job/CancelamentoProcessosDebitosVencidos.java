package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceProcessoDebitos;
import org.springframework.beans.factory.annotation.Autowired;

public class CancelamentoProcessosDebitosVencidos extends WPJob {

    @Autowired
    protected ServiceProcessoDebitos serviceProcessoDebitos;

    @Override
    public void execute() {
        try {
            serviceProcessoDebitos.cancelarProcessosVencidos();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Cancelamento de Processos de Débitos Vencidos";
    }
}
