package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.reinf.service.RegistroReinfService;
import org.springframework.beans.factory.annotation.Autowired;

public class IntegracaoReinfJob extends WPJob {

    @Autowired
    private RegistroReinfService service;


    @Override
    public void execute() {
        service.atualizarStatusRegistros();
    }
}
