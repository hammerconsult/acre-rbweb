package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceMonitoramentoFiscal;
import org.springframework.beans.factory.annotation.Autowired;

public class MonitoramentoFiscalJob extends WPJob {

    @Autowired
    private ServiceMonitoramentoFiscal service;

    @Override
    public void execute() {
        service.processarVerificacaoROL();
    }
}
