package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.esocial.service.RegistroESocialService;
import org.springframework.beans.factory.annotation.Autowired;

public class AtualizarDescricoesEsocialJob extends WPJob {

    @Autowired
    private RegistroESocialService service;

    @Override
    public void execute() {
        service.atualizarDescricoesEventosEsocial();
        service.atualizarDescricoesEventosEsocialS1010();
    }
}
