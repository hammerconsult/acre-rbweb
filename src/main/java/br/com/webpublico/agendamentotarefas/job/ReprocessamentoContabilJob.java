package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.contabil.services.ServiceReprocessamentoContabil;
import org.springframework.beans.factory.annotation.Autowired;

public class ReprocessamentoContabilJob extends WPJob {

    @Autowired
    private ServiceReprocessamentoContabil serviceReprocessamentoContabil;

    @Override
    public void execute() {
        serviceReprocessamentoContabil.reprocessar();
    }

    @Override
    public String toString() {
        return "Reprocessamento Contábil Automático";
    }

}
