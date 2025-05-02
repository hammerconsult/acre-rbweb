package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.comum.services.ServiceBloqueioDesbloqueioUsuario;
import org.springframework.beans.factory.annotation.Autowired;

public class BloqueioDesbloqueioUsuarioJob extends WPJob {

    @Autowired
    private ServiceBloqueioDesbloqueioUsuario service;

    @Override
    public void execute() {
//        service.processarDesbloqueio(); //TODO por solicitação da DTI no momento não serão desbloqueados por rotina automatica
        service.processarBloqueios();
    }
}
