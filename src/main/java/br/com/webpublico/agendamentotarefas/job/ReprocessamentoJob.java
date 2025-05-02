package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.contabil.services.ServiceReprocessamento;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by mateus on 06/12/17.
 */
public class ReprocessamentoJob extends WPJob {

    @Autowired
    private ServiceReprocessamento serviceReprocessamento;

    @Override
    public String toString() {
        return "Ajustes";
    }

    @Override
    public void execute() {
        serviceReprocessamento.reprocessar();
    }
}
