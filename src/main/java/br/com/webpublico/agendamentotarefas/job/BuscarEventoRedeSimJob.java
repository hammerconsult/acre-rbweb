package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.IntegracaoRedeSimService;
import org.springframework.beans.factory.annotation.Autowired;

public class BuscarEventoRedeSimJob extends WPJob {


    @Autowired
    protected IntegracaoRedeSimService integracaoRedeSimService;

    @Override
    public void execute() {
        integracaoRedeSimService.buscarEventosRedeSim();
    }


}
