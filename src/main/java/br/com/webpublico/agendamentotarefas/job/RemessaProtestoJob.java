package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceRemessaProtesto;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import org.springframework.beans.factory.annotation.Autowired;

public class RemessaProtestoJob extends WPJob {

    @Autowired
    private ServiceRemessaProtesto serviceRemessaProtesto;
    @Autowired
    private SistemaService sistemaService;

    @Override
    public void execute() {
        AsyncExecutor.getInstance().execute(new AssistenteBarraProgresso("Envio de Remessa de Protesto por Agendamento de Tarefas", 0),
            () -> {
                serviceRemessaProtesto.enviarRemessaPorAgendamento();
                return null;
            });
    }
}
