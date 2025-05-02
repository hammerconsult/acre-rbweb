package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.rh.ServicePeriodoAquisitivoFL;
import org.springframework.beans.factory.annotation.Autowired;

public class PeriodoAquisitivoFLJob extends WPJob {
    @Autowired
    private ServicePeriodoAquisitivoFL service;

    @Override
    public void execute() {
        try {
            service.lancarPeriodosAquisitivos();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Geração dos períodos aquisitivos automaticamente.";
    }
}
