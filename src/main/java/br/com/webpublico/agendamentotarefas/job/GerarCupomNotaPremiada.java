package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.NotaPremiadaService;
import org.springframework.beans.factory.annotation.Autowired;

public class GerarCupomNotaPremiada extends WPJob {

    @Autowired
    private NotaPremiadaService notaPremiadaService;

    @Override
    public void execute() {
        notaPremiadaService.gerarCupons();
    }
}
